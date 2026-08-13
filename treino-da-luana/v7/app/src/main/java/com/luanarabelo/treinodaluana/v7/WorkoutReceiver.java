package com.luanarabelo.treinodaluana.v7;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WorkoutReceiver extends BroadcastReceiver {
    public static final String ACTION_PREVIOUS = "com.luanarabelo.treinodaluana.v7.PREVIOUS";
    public static final String ACTION_NEXT = "com.luanarabelo.treinodaluana.v7.NEXT";
    public static final String ACTION_FINISH = "com.luanarabelo.treinodaluana.v7.FINISH";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        if (ACTION_FINISH.equals(intent.getAction())) {
            NotificationHelper.cancel(context);
            Toast.makeText(context, "Treino finalizado", Toast.LENGTH_SHORT).show();
            return;
        }

        int workout = intent.getIntExtra("workout", -1);
        int exercise = intent.getIntExtra("exercise", 0);
        if (workout < 0 || workout >= WorkoutData.NAMES.length) return;

        if (ACTION_NEXT.equals(intent.getAction())) {
            exercise = Math.min(exercise + 1, WorkoutData.NAMES[workout].length - 1);
        } else if (ACTION_PREVIOUS.equals(intent.getAction())) {
            exercise = Math.max(exercise - 1, 0);
        }
        NotificationHelper.show(context, workout, exercise);
    }
}
