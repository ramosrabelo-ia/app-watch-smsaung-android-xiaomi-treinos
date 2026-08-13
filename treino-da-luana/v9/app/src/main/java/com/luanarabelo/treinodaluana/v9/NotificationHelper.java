package com.luanarabelo.treinodaluana.v9;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public final class NotificationHelper {
    private static final String CHANNEL_ID = "treino_da_luana_v9";
    private static final int NOTIFICATION_ID = 9009;

    private NotificationHelper() {}

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Treino da Luana",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Exercício atual e controles do treino");
            channel.setSound(null, null);
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
        }
    }

    public static boolean show(Context context, int workout, int exercise) {
        if (workout < 0 || workout >= WorkoutData.NAMES.length) return false;
        if (exercise < 0 || exercise >= WorkoutData.NAMES[workout].length) return false;
        if (Build.VERSION.SDK_INT >= 33
                && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) return false;

        createChannel(context);

        Intent open = new Intent(context, MainActivity.class);
        open.putExtra("workout", workout);
        open.putExtra("exercise", exercise);
        open.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent openPending = PendingIntent.getActivity(
                context, 9000 + workout, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent previous = action(context, WorkoutReceiver.ACTION_PREVIOUS, workout, exercise, 9100);
        PendingIntent next = action(context, WorkoutReceiver.ACTION_NEXT, workout, exercise, 9200);
        PendingIntent finish = action(context, WorkoutReceiver.ACTION_FINISH, workout, exercise, 9300);

        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(context, CHANNEL_ID)
                : new Notification.Builder(context);

        builder.setSmallIcon(com.luanarabelo.treinodaluana.v9.R.drawable.ic_notification)
                .setContentTitle("Treino " + WorkoutData.LETTERS[workout] + ", exercício " + (exercise + 1))
                .setContentText(WorkoutData.NAMES[workout][exercise] + ", " + WorkoutData.REPS[workout][exercise])
                .setStyle(new Notification.BigTextStyle().bigText(
                        WorkoutData.NAMES[workout][exercise] + "\n" + WorkoutData.REPS[workout][exercise]))
                .setContentIntent(openPending)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .addAction(0, "Anterior", previous)
                .addAction(0, "Próximo", next)
                .addAction(0, "Finalizar", finish);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
        return true;
    }

    private static PendingIntent action(Context context, String action, int workout, int exercise, int base) {
        Intent intent = new Intent(context, WorkoutReceiver.class);
        intent.setAction(action);
        intent.putExtra("workout", workout);
        intent.putExtra("exercise", exercise);
        return PendingIntent.getBroadcast(
                context,
                base + workout * 100 + exercise,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public static void cancel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }
}
