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

/** Sincroniza somente o progresso dos 24 blocos. Cargas ficam privadas no celular. */
public final class PhoneProgressSync {
    public static final String PATH_PREFIX = "/treino-lua/v13/block/";
    private static final String PREFS = "treino_v12";
    private static final String SOURCE = "phone";
    private static final String SEEDED = "wear_sync_seeded_v13";

    private PhoneProgressSync() {}

    public interface PullCallback {
        void onComplete(boolean changed);
    }

    public static void migrateAndPublish(Context context) {
        Context app = context.getApplicationContext();
        SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (prefs.getBoolean(SEEDED, false)) return;

        long now = System.currentTimeMillis();
        SharedPreferences.Editor editor = prefs.edit().putBoolean(SEEDED, true);
        for (int workout = 0; workout < WorkoutData.LETTERS.length; workout++) {
            for (int block = 0; block < WorkoutData.BLOCKS_PER_WORKOUT; block++) {
                String timestampKey = timestampKey(workout, block);
                if (!prefs.contains(timestampKey)) {
                    editor.putLong(timestampKey, now + workout * 10L + block);
                }
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
                long timestamp = prefs.getLong(timestampKey(workout, block), 0L);
                if (timestamp == 0L) continue;
                put(app, week, workout, block, isBlockComplete(prefs, week, workout, block), timestamp);
            }
        }
    }

    public static void publishBlock(Context context, int workout, int block, boolean done) {
        Context app = context.getApplicationContext();
        SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long timestamp = System.currentTimeMillis();
        prefs.edit().putLong(timestampKey(workout, block), timestamp).apply();
        put(app, weekKey(), workout, block, done, timestamp);
    }

    private static void put(
            Context context,
            String week,
            int workout,
            int block,
            boolean done,
            long timestamp
    ) {
        PutDataMapRequest mapRequest = PutDataMapRequest.create(PATH_PREFIX + workout + "/" + block);
        DataMap map = mapRequest.getDataMap();
        map.putString("source", SOURCE);
        map.putString("week", week);
        map.putInt("workout", workout);
        map.putInt("block", block);
        map.putBoolean("done", done);
        map.putLong("updated_at", timestamp);
        map.putLong("nonce", System.nanoTime());
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
                        for (DataItem item : buffer) {
                            changed |= applyDataItem(app, item);
                        }
                    } finally {
                        buffer.release();
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
        if (path == null || !path.startsWith(PATH_PREFIX)) return false;

        DataMap map;
        try {
            map = DataMapItem.fromDataItem(item).getDataMap();
        } catch (Throwable ignored) {
            return false;
        }
        if (SOURCE.equals(map.getString("source"))) return false;
        if (!weekKey().equals(map.getString("week"))) return false;

        int workout = map.getInt("workout", -1);
        int block = map.getInt("block", -1);
        long remoteTimestamp = map.getLong("updated_at", 0L);
        if (workout < 0 || workout >= WorkoutData.LETTERS.length
                || block < 0 || block >= WorkoutData.BLOCKS_PER_WORKOUT
                || remoteTimestamp == 0L) return false;

        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long localTimestamp = prefs.getLong(timestampKey(workout, block), 0L);
        if (remoteTimestamp <= localTimestamp) return false;

        setBlockComplete(prefs, weekKey(), workout, block, map.getBoolean("done", false));
        prefs.edit().putLong(timestampKey(workout, block), remoteTimestamp).apply();
        return true;
    }

    private static void setBlockComplete(
            SharedPreferences prefs,
            String week,
            int workout,
            int block,
            boolean done
    ) {
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
            if (!prefs.contains(checkinKey)) {
                checkinEditor.putLong(checkinKey, System.currentTimeMillis());
            }
        } else {
            checkinEditor.remove(checkinKey);
            checkinEditor.remove(week + "_health_synced_" + workout);
        }
        checkinEditor.apply();
    }

    private static boolean isBlockComplete(
            SharedPreferences prefs,
            String week,
            int workout,
            int block
    ) {
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

    private static String timestampKey(int workout, int block) {
        return "wear_sync_ts_w" + workout + "_b" + block;
    }

    public static String weekKey() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.WEEK_OF_YEAR);
    }
}
