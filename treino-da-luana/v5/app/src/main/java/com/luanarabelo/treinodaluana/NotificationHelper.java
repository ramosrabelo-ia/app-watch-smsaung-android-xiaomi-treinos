package com.luanarabelo.treinodaluana;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

public final class NotificationHelper {
    public static final String CHANNEL_ID = "treino_da_luana";
    public static final int NOTIFICATION_ID = 2405;
    private static final String PREFS = "workout_state";
    private static final String KEY_WORKOUT = "workout";
    private static final String KEY_EXERCISE = "exercise";

    private NotificationHelper() {}

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Treino da Luana",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Mostra o exercício atual do treino");
            channel.setSound(null, null);
            channel.enableVibration(false);
            nm.createNotificationChannel(channel);
        }
    }

    public static void saveState(Context context, int workout, int exercise) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_WORKOUT, workout)
                .putInt(KEY_EXERCISE, exercise)
                .apply();
    }

    public static int[] getState(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return new int[]{p.getInt(KEY_WORKOUT, -1), p.getInt(KEY_EXERCISE, 0)};
    }

    public static void clearState(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static boolean show(Context context, int workout, int exercise) {
        createChannel(context);

        if (workout < 0 || workout >= WorkoutData.NAMES.length) return false;
        if (exercise < 0 || exercise >= WorkoutData.NAMES[workout].length) return false;

        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        saveState(context, workout, exercise);

        String letter = WorkoutData.LETTERS[workout];
        String name = WorkoutData.NAMES[workout][exercise];
        String reps = WorkoutData.REPS[workout][exercise];
        int total = WorkoutData.NAMES[workout].length;

        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent openPending = PendingIntent.getActivity(
                context,
                2000 + workout,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent previous = actionIntent(context, WorkoutReceiver.ACTION_PREVIOUS, workout, exercise, 3000);
        PendingIntent next = actionIntent(context, WorkoutReceiver.ACTION_NEXT, workout, exercise, 4000);
        PendingIntent finish = actionIntent(context, WorkoutReceiver.ACTION_FINISH, workout, exercise, 5000);

        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(context, CHANNEL_ID)
                : new Notification.Builder(context);

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(letter + " " + (exercise + 1) + "/" + total + " • " + name)
                .setContentText(reps)
                .setStyle(new Notification.BigTextStyle().bigText(reps))
                .setContentIntent(openPending)
                .setOngoing(false)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setCategory(Notification.CATEGORY_WORKOUT)
                .addAction(new Notification.Action.Builder(null, "ANTERIOR", previous).build())
                .addAction(new Notification.Action.Builder(null, "PRÓXIMO", next).build())
                .addAction(new Notification.Action.Builder(null, "FINALIZAR", finish).build());

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.build());
        return true;
    }

    private static PendingIntent actionIntent(Context context, String action, int workout, int exercise, int base) {
        Intent intent = new Intent(context, WorkoutReceiver.class);
        intent.setAction(action);
        intent.putExtra("workout", workout);
        intent.putExtra("exercise", exercise);
        int request = base + workout * 100 + exercise;
        return PendingIntent.getBroadcast(
                context,
                request,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public static void cancel(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
        clearState(context);
    }
}
