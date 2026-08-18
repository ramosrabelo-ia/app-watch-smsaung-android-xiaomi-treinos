package com.luanarabelo.treinodaluana.v11;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

public final class NotificationHelper {
    private static final String CHANNEL_ID = "treino_withings_v11";
    private static final int NOTIFICATION_ID = 11110;

    private NotificationHelper() {}

    public static void createChannel(Context context) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Treino no Withings",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Nome do exercício, séries e repetições para o celular e o relógio Withings");
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 180, 100, 180});
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        manager.createNotificationChannel(channel);
    }

    public static boolean showExercise(Context context, int workout, int exercise) {
        if (workout < 0 || workout >= WorkoutData.NAMES.length) return false;
        if (exercise < 0 || exercise >= WorkoutData.NAMES[workout].length) return false;
        if (!canNotify(context)) return false;
        createChannel(context);

        Intent open = new Intent(context, MainActivity.class);
        open.putExtra("workout", workout);
        open.putExtra("exercise", exercise);
        open.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent openPending = PendingIntent.getActivity(
                context,
                11000 + workout * 10 + exercise,
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = "Treino " + WorkoutData.LETTERS[workout] + " • " + (exercise + 1) + " de 7";
        String instruction = WorkoutData.NAMES[workout][exercise]
                + " • " + WorkoutData.SETS[workout][exercise] + " séries • "
                + WorkoutData.REPS[workout][exercise] + " reps";

        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.rgb(255, 138, 61))
                .setContentTitle(title)
                .setContentText(instruction)
                .setStyle(new Notification.BigTextStyle().bigText(instruction))
                .setCategory(Notification.CATEGORY_REMINDER)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(openPending)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(false)
                .addAction(R.drawable.ic_notification, "Anterior", action(context, WorkoutReceiver.ACTION_PREVIOUS, workout, exercise, 11100))
                .addAction(R.drawable.ic_notification, "Próximo", action(context, WorkoutReceiver.ACTION_NEXT, workout, exercise, 11200))
                .addAction(R.drawable.ic_notification, "Finalizar", action(context, WorkoutReceiver.ACTION_FINISH, workout, exercise, 11300));

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return false;
        manager.notify(NOTIFICATION_ID, builder.build());
        return true;
    }

    public static boolean showTest(Context context) {
        if (!canNotify(context)) return false;
        createChannel(context);

        Intent open = new Intent(context, MainActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent openPending = PendingIntent.getActivity(
                context,
                11999,
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.rgb(255, 138, 61))
                .setContentTitle("Treino da Luana v11")
                .setContentText("Teste Withings • notificações prontas")
                .setStyle(new Notification.BigTextStyle().bigText(
                        "Teste Withings concluído. Ative Treino da Luana v11 em Aplicativos dentro do Withings."
                ))
                .setCategory(Notification.CATEGORY_REMINDER)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(openPending)
                .setAutoCancel(true)
                .setTimeoutAfter(60_000)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return false;
        manager.notify(NOTIFICATION_ID, notification);
        return true;
    }

    private static boolean canNotify(Context context) {
        return Build.VERSION.SDK_INT < 33
                || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    private static PendingIntent action(
            Context context,
            String action,
            int workout,
            int exercise,
            int base
    ) {
        Intent intent = new Intent(context, WorkoutReceiver.class);
        intent.setAction(action);
        intent.putExtra("workout", workout);
        intent.putExtra("exercise", exercise);
        return PendingIntent.getBroadcast(
                context,
                base + workout * 10 + exercise,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public static void cancel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.cancel(NOTIFICATION_ID);
    }
}
