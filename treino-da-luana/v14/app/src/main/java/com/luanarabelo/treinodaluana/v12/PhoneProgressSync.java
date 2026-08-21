package com.luanarabelo.treinodaluana.v12;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;

public final class PhoneProgressSync {
    public static final String BLOCK_PATH_PREFIX = "/treino-lua/v13/block/";
    public static final String LOAD_PATH_PREFIX = "/treino-lua/v14/load/";
    private static final String PREFS = "treino_v12";
    private static final String SEEDED = "wear_sync_seeded_v14";
    private static final String SOURCE = "phone";

    public interface PullCallback {
        void onComplete(boolean changed);
    }

    private PhoneProgressSync() {}

    public static void migrateAndPublish(Context context) {
        Context app = context.getApplicationContext();
        SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (prefs.getBoolean(SEEDED, false)) return;

        long now = System.currentTimeMillis();
        SharedPreferences.Editor editor = prefs.edit().putBoolean(SEEDED, true);
        for (int workout = 0; workout < WorkoutData.LETTERS.length; workout++) {
            for (int block = 0; block < WorkoutData.BLOCKS_PER_WORKOUT; block++) {
                String key = blockTimestampKey(workout, block);
                if (!prefs.contains(key)) editor.putLong(key, now + workout * 10L + block);
            }
        }
        editor.apply();
        publishAll(app);
    }

    public static void publishAll(Context context) {
        Context app = context.getApplicationContext();
        SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String week = weekKey();
        for (int workout = 0; workout < WorkoutData.LETTERS.length; workout++) {
            for (int block = 0; block < WorkoutData.BLOCKS_PER_WORKOUT; block++) {
                long timestamp = prefs.getLong(blockTimestampKey(workout, block), 0L);
                if (timestamp != 0L) {
                    putBlock(app, week, workout, block,
                            isBlockComplete(prefs, week, workout, block), timestamp);
                }
            }
        }
        publishAllLoads(app);
    }

    public static void publishAllLoads(Context context) {
        Context app = context.getApplicationContext();
        SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long baseTimestamp = System.currentTimeMillis();
        SharedPreferences.Editor editor = prefs.edit();
        for (int workout = 0; workout < WorkoutData.LETTERS.length; workout++) {
            for (int exercise = 0; exercise < WorkoutData.NAMES[workout].length; exercise++) {
                long timestamp = baseTimestamp + workout * 100L + exercise;
                editor.putLong(loadTimestampKey(workout, exercise), timestamp);
                putLoad(app, workout, exercise,
                        prefs.getString(loadKey(workout, exercise), ""), timestamp);
            }
        }
        editor.apply();
    }

    public static void publishBlock(Context context, int workout, int block, boolean done) {
        Context app = context.getApplicationContext();
        SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long timestamp = System.currentTimeMillis();
        prefs.edit().putLong(blockTimestampKey(workout, block), timestamp).apply();
        putBlock(app, weekKey(), workout, block, done, timestamp);
    }

    public static void publishLoad(Context context, int workout, int exercise, String load) {
        Context app = context.getApplicationContext();
        long timestamp = System.currentTimeMillis();
        app.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putLong(loadTimestampKey(workout, exercise), timestamp).apply();
        putLoad(app, workout, exercise, load == null ? "" : load, timestamp);
    }

    private static void putBlock(Context context, String week, int workout, int block,
                                 boolean done, long timestamp) {
        PutDataMapRequest request = PutDataMapRequest.create(BLOCK_PATH_PREFIX + workout + "/" + block);
        DataMap map = request.getDataMap();
        map.putString("source", SOURCE);
        map.putString("week", week);
        map.putInt("workout", workout);
        map.putInt("block", block);
        map.putBoolean("done", done);
        map.putLong("updated_at", timestamp);
        map.putLong("nonce", System.nanoTime());
        send(context, request);
    }

    private static void putLoad(Context context, int workout, int exercise,
                                String load, long timestamp) {
        PutDataMapRequest request = PutDataMapRequest.create(LOAD_PATH_PREFIX + workout + "/" + exercise);
        DataMap map = request.getDataMap();
        map.putString("source", SOURCE);
        map.putInt("workout", workout);
        map.putInt("exercise", exercise);
        map.putString("load", load);
        map.putLong("updated_at", timestamp);
        map.putLong("nonce", System.nanoTime());
        send(context, request);
    }

    private static void send(Context context, PutDataMapRequest mapRequest) {
        PutDataRequest request = mapRequest.asPutDataRequest();
        request.setUrgent();
        Wearable.getDataClient(context).putDataItem(request);
    }

    public static void pullRemote(Context context, PullCallback callback) {
        Context app = context.getApplicationContext();
        Wearable.getDataClient(app).getDataItems()
                .addOnSuccessListener(buffer -> {
                    boolean changed = false;
                    try {
                        for (DataItem item : WearBufferCompat.<DataItem>iterable(buffer)) {
                            changed |= applyDataItem(app, item);
                        }
                    } finally {
                        WearBufferCompat.release(buffer);
                    }
                    callback.onComplete(changed);
                })
                .addOnFailureListener(error -> callback.onComplete(false));
    }

    public static boolean applyEvent(Context context, DataEvent event) {
        return event.getType() == DataEvent.TYPE_CHANGED
                && applyDataItem(context.getApplicationContext(), event.getDataItem());
    }

    public static boolean applyDataItem(Context context, DataItem item) {
        String path = item.getUri().getPath();
        if (path == null || !path.startsWith(BLOCK_PATH_PREFIX)) return false;
        try {
            DataMap map = DataMapItem.fromDataItem(item).getDataMap();
            if (SOURCE.equals(map.getString("source")) || !weekKey().equals(map.getString("week"))) {
                return false;
            }
            int workout = map.getInt("workout", -1);
            int block = map.getInt("block", -1);
            long remoteTimestamp = map.getLong("updated_at", 0L);
            if (workout < 0 || workout >= WorkoutData.LETTERS.length
                    || block < 0 || block >= WorkoutData.BLOCKS_PER_WORKOUT
                    || remoteTimestamp == 0L) return false;

            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            if (remoteTimestamp <= prefs.getLong(blockTimestampKey(workout, block), 0L)) return false;
            setBlockComplete(prefs, weekKey(), workout, block, map.getBoolean("done", false));
            prefs.edit().putLong(blockTimestampKey(workout, block), remoteTimestamp).apply();
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static void setBlockComplete(SharedPreferences prefs, String week, int workout,
                                         int block, boolean done) {
        int start = WorkoutData.blockStart(block);
        int size = WorkoutData.blockSize(block);
        SharedPreferences.Editor editor = prefs.edit();
        for (int offset = 0; offset < size; offset++) {
            int exercise = start + offset;
            int allSets = (1 << WorkoutData.SETS[workout][exercise]) - 1;
            editor.putInt(maskKey(week, workout, exercise), done ? allSets : 0);
        }
        editor.commit();

        boolean workoutDone = true;
        for (int index = 0; index < WorkoutData.BLOCKS_PER_WORKOUT; index++) {
            if (!isBlockComplete(prefs, week, workout, index)) {
                workoutDone = false;
                break;
            }
        }
        String checkinKey = week + "_checkin_" + workout;
        SharedPreferences.Editor checkinEditor = prefs.edit();
        if (workoutDone) {
            if (!prefs.contains(checkinKey)) checkinEditor.putLong(checkinKey, System.currentTimeMillis());
        } else {
            checkinEditor.remove(checkinKey);
            checkinEditor.remove(week + "_health_synced_" + workout);
        }
        checkinEditor.apply();
    }

    private static boolean isBlockComplete(SharedPreferences prefs, String week,
                                           int workout, int block) {
        int start = WorkoutData.blockStart(block);
        int size = WorkoutData.blockSize(block);
        for (int offset = 0; offset < size; offset++) {
            int exercise = start + offset;
            int mask = prefs.getInt(maskKey(week, workout, exercise), 0);
            int allSets = (1 << WorkoutData.SETS[workout][exercise]) - 1;
            if ((mask & allSets) != allSets) return false;
        }
        return true;
    }

    private static String maskKey(String week, int workout, int exercise) {
        return week + "_w" + workout + "_e" + exercise;
    }

    private static String loadKey(int workout, int exercise) {
        return "load_w" + workout + "_e" + exercise;
    }

    private static String blockTimestampKey(int workout, int block) {
        return "wear_sync_ts_w" + workout + "_b" + block;
    }

    private static String loadTimestampKey(int workout, int exercise) {
        return "load_sync_ts_w" + workout + "_e" + exercise;
    }

    public static String weekKey() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.WEEK_OF_YEAR);
    }
}
