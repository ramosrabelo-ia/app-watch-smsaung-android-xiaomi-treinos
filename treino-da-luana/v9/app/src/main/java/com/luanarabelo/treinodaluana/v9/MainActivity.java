package com.luanarabelo.treinodaluana.v9;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 909;
    private static final int BLACK = Color.rgb(7, 10, 13);
    private static final int GRAPHITE = Color.rgb(18, 24, 32);
    private static final int WHITE = Color.rgb(232, 238, 242);
    private static final int GREEN = Color.rgb(124, 255, 107);
    private static final int CYAN = Color.rgb(52, 214, 255);

    private static final int[] IMAGES = {
            R.drawable.exercise_0,
            R.drawable.exercise_1,
            R.drawable.exercise_2,
            R.drawable.exercise_3,
            R.drawable.exercise_4,
            R.drawable.exercise_5,
            R.drawable.exercise_6,
            R.drawable.exercise_7
    };

    private int currentWorkout = -1;
    private int currentExercise = 0;
    private int pendingWorkout = -1;
    private int pendingExercise = 0;
    private boolean notificationActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            NotificationHelper.createChannel(this);
        } catch (Throwable ignored) {
        }
        openFromIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openFromIntent(intent);
    }

    private void openFromIntent(Intent intent) {
        int workout = intent == null ? -1 : intent.getIntExtra("workout", -1);
        int exercise = intent == null ? 0 : intent.getIntExtra("exercise", 0);
        if (workout >= 0 && workout < WorkoutData.NAMES.length) {
            showExercise(workout, clampExercise(workout, exercise));
        } else {
            showHome();
        }
    }

    private LinearLayout newScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(20), dp(30), dp(20), dp(30));
        scroll.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));

        scroll.setOnApplyWindowInsetsListener((view, insets) -> {
            root.setPadding(
                    dp(20),
                    insets.getSystemWindowInsetTop() + dp(22),
                    dp(20),
                    insets.getSystemWindowInsetBottom() + dp(22)
            );
            return insets;
        });
        setContentView(scroll);
        return root;
    }

    private void addHeader(LinearLayout root, String subtitleText) {
        TextView title = text("TREINO DA LUANA", 27, WHITE);
        title.setGravity(Gravity.CENTER);
        root.addView(title, fullWidth());

        TextView subtitle = text(subtitleText, 14, CYAN);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(6), 0, dp(18));
        root.addView(subtitle, fullWidth());
    }

    private void showHome() {
        currentWorkout = -1;
        notificationActive = false;
        LinearLayout root = newScreen();
        addHeader(root, "V9 // TREINO COMPLETO COM FOTOS");

        TextView instruction = text("ESCOLHA O TREINO", 14, GREEN);
        instruction.setGravity(Gravity.CENTER);
        instruction.setPadding(dp(8), 0, dp(8), dp(20));
        root.addView(instruction, fullWidth());

        for (int index = 0; index < WorkoutData.LETTERS.length; index++) {
            final int workout = index;
            Button button = new Button(this);
            button.setAllCaps(false);
            button.setText("TREINO " + WorkoutData.LETTERS[index] + "\n"
                    + WorkoutData.TITLES[index] + "\n"
                    + WorkoutData.NAMES[index].length + " EXERCÍCIOS");
            button.setTextSize(15);
            button.setTextColor(WHITE);
            button.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            button.setGravity(Gravity.CENTER);
            button.setTextAlignment(Button.TEXT_ALIGNMENT_CENTER);
            button.setPadding(dp(16), dp(14), dp(16), dp(14));
            button.setBackground(panelBackground(CYAN));
            button.setOnClickListener(view -> showExercise(workout, 0));
            LinearLayout.LayoutParams params = fullWidth();
            params.setMargins(0, 0, 0, dp(12));
            root.addView(button, params);
        }
    }

    private void showExercise(int workout, int exercise) {
        currentWorkout = workout;
        currentExercise = exercise;
        LinearLayout root = newScreen();

        Button back = new Button(this);
        back.setAllCaps(false);
        back.setText("TODOS OS TREINOS");
        styleSecondaryButton(back);
        back.setOnClickListener(view -> showHome());
        root.addView(back, fullWidth());

        addHeader(root, "V9 // TREINO " + WorkoutData.LETTERS[workout]);

        TextView progress = text(
                "EXERCÍCIO " + (exercise + 1) + " DE " + WorkoutData.NAMES[workout].length,
                14,
                GREEN
        );
        progress.setGravity(Gravity.CENTER);
        progress.setPadding(0, 0, 0, dp(12));
        root.addView(progress, fullWidth());

        ImageView image = new ImageView(this);
        image.setImageResource(IMAGES[WorkoutData.IMAGE_TYPES[workout][exercise]]);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setAdjustViewBounds(false);
        image.setBackgroundColor(GRAPHITE);
        image.setContentDescription("Referência visual de " + WorkoutData.NAMES[workout][exercise]);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(280)
        );
        imageParams.setMargins(0, 0, 0, dp(16));
        root.addView(image, imageParams);

        TextView exerciseName = text(WorkoutData.NAMES[workout][exercise], 22, WHITE);
        exerciseName.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        exerciseName.setGravity(Gravity.CENTER);
        exerciseName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        exerciseName.setPadding(dp(10), dp(4), dp(10), dp(10));
        root.addView(exerciseName, fullWidth());

        TextView repetitions = text(WorkoutData.REPS[workout][exercise], 17, CYAN);
        repetitions.setGravity(Gravity.CENTER);
        repetitions.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        repetitions.setPadding(dp(10), 0, dp(10), dp(18));
        root.addView(repetitions, fullWidth());

        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setGravity(Gravity.CENTER);

        Button previous = new Button(this);
        previous.setAllCaps(false);
        previous.setText("ANTERIOR");
        styleSecondaryButton(previous);
        previous.setEnabled(exercise > 0);
        previous.setAlpha(exercise > 0 ? 1f : 0.35f);
        previous.setOnClickListener(view -> changeExercise(-1));

        Button next = new Button(this);
        next.setAllCaps(false);
        next.setText(exercise == WorkoutData.NAMES[workout].length - 1 ? "FINALIZAR" : "PRÓXIMO");
        styleSecondaryButton(next);
        next.setOnClickListener(view -> {
            if (currentExercise == WorkoutData.NAMES[currentWorkout].length - 1) {
                NotificationHelper.cancel(this);
                Toast.makeText(this, "Treino finalizado", Toast.LENGTH_SHORT).show();
                showHome();
            } else {
                changeExercise(1);
            }
        });

        LinearLayout.LayoutParams leftButton = new LinearLayout.LayoutParams(0, dp(54), 1f);
        leftButton.setMargins(0, 0, dp(5), 0);
        LinearLayout.LayoutParams rightButton = new LinearLayout.LayoutParams(0, dp(54), 1f);
        rightButton.setMargins(dp(5), 0, 0, 0);
        navigation.addView(previous, leftButton);
        navigation.addView(next, rightButton);
        root.addView(navigation, fullWidth());

        Button notify = new Button(this);
        notify.setAllCaps(false);
        notify.setText(notificationActive ? "ATUALIZAR NOTIFICAÇÃO" : "ATIVAR NOTIFICAÇÃO");
        styleSecondaryButton(notify);
        notify.setOnClickListener(view -> requestNotification(workout, exercise));
        LinearLayout.LayoutParams notifyParams = fullWidth();
        notifyParams.height = dp(54);
        notifyParams.setMargins(0, dp(10), 0, 0);
        root.addView(notify, notifyParams);
    }

    private int clampExercise(int workout, int exercise) {
        return Math.max(0, Math.min(exercise, WorkoutData.NAMES[workout].length - 1));
    }

    private void changeExercise(int delta) {
        int nextExercise = clampExercise(currentWorkout, currentExercise + delta);
        if (notificationActive) {
            NotificationHelper.show(this, currentWorkout, nextExercise);
        }
        showExercise(currentWorkout, nextExercise);
    }

    private void requestNotification(int workout, int exercise) {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            pendingWorkout = workout;
            pendingExercise = exercise;
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST
            );
            return;
        }
        publishExercise(workout, exercise);
    }

    private void publishExercise(int workout, int exercise) {
        try {
            if (NotificationHelper.show(this, workout, exercise)) {
                notificationActive = true;
                showExercise(workout, exercise);
                Toast.makeText(this, "Notificação ativada", Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable error) {
            Diagnostics.copy(this, error);
            Toast.makeText(this, "Não foi possível ativar a notificação", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != NOTIFICATION_PERMISSION_REQUEST) return;
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && pendingWorkout >= 0) {
            publishExercise(pendingWorkout, pendingExercise);
        }
        pendingWorkout = -1;
        pendingExercise = 0;
    }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(Typeface.MONOSPACE);
        return view;
    }

    private void styleSecondaryButton(Button button) {
        button.setTextColor(CYAN);
        button.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setTextAlignment(Button.TEXT_ALIGNMENT_CENTER);
        button.setBackground(panelBackground(CYAN));
    }

    private GradientDrawable panelBackground(int strokeColor) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(GRAPHITE);
        background.setStroke(dp(1), strokeColor);
        background.setCornerRadius(dp(2));
        return background;
    }

    private LinearLayout.LayoutParams fullWidth() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
