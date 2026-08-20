package com.luanarabelo.treinodaluana.v12.wear;

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
import java.util.Map;

public final class WatchProgressSync {
    public static final String PATH_PREFIX = "/treino-lua/v13/block/";
    private static final String PREFS = "treino_v12_watch8";
    private static final String SOURCE = "watch";
    private static final String WEEK = "sync_week";

    private WatchProgressSync() {}

    public interface PullCallback {
        void onComplete(boolean changed);
    }

    public static void prepareWeek(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String current = weekKey();
        if (current.equals(prefs.getString(WEEK, ""))) return;

        SharedPreferences.Editor editor = prefs.edit();
        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            String key = entry.getKey();
            if (key.matches("w\\d+_b\\d+") || key.startsWith("sync_ts_")) {
                editor.remove(key);
            }
        }
        editor.putString(WEEK, current).apply();
    }

    public static void publishBlock(Context context, int workout, int block, boolean done) {
        Context app = context.getApplicationContext();
        prepareWeek(app);
        SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long timestamp = System.currentTimeMillis();
        prefs.edit()
                .putBoolean(key(workout, block), done)
                .putLong(timestampKey(workout, block), timestamp)
                .apply();

        PutDataMapRequest mapRequest = PutDataMapRequest.create(PATH_PREFIX + workout + "/" + block);
        DataMap map = mapRequest.getDataMap();
        map.putString("source", SOURCE);
        map.putString("week", weekKey());
        map.putInt("workout", workout);
        map.putInt("block", block);
        map.putBoolean("done", done);
        map.putLong("updated_at", timestamp);
        map.putLong("nonce", System.nanoTime());
        PutDataRequest request = mapRequest.asPutDataRequest();
        request.setUrgent();
        Wearable.getDataClient(app).putDataItem(request);
    }

    public static void pullRemote(Context context, PullCallback callback) {
        Context app = context.getApplicationContext();
        prepareWeek(app);
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
        if (workout < 0 || workout >= 4 || block < 0 || block >= 6 || remoteTimestamp == 0L) {
            return false;
        }

        prepareWeek(context);
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (remoteTimestamp <= prefs.getLong(timestampKey(workout, block), 0L)) return false;
        prefs.edit()
                .putBoolean(key(workout, block), map.getBoolean("done", false))
                .putLong(timestampKey(workout, block), remoteTimestamp)
                .apply();
        return true;
    }

    private static String key(int workout, int block) {
        return "w" + workout + "_b" + block;
    }

    private static String timestampKey(int workout, int block) {
        return "sync_ts_w" + workout + "_b" + block;
    }

    private static String weekKey() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.WEEK_OF_YEAR);
    }
}

