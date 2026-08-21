package com.luanarabelo.treinodaluana.v12.wear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.luanarabelo.treinodaluana.v12.WorkoutData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public final class WatchMainActivity extends Activity implements DataClient.OnDataChangedListener {
    private static final int BG = Color.rgb(7, 7, 7);
    private static final int OBSIDIAN = Color.rgb(16, 16, 16);
    private static final int CARD = Color.rgb(24, 24, 24);
    private static final int CARD_DONE = Color.rgb(47, 29, 17);
    private static final int LINE = Color.rgb(63, 63, 63);
    private static final int WHITE = Color.rgb(247, 247, 247);
    private static final int MUTED = Color.rgb(166, 166, 166);
    private static final int ORANGE = Color.rgb(255, 138, 61);
    private static final int GREEN = Color.rgb(105, 207, 132);
    private static final int SCREEN_HOME = 0;
    private static final int SCREEN_WORKOUT = 1;
    private static final int SCREEN_EXERCISE = 2;
    private static final int SCREEN_SUMMARY = 3;
    private static final String PREFS = "treino_v12_watch8";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private SharedPreferences preferences;
    private int screen = SCREEN_HOME;
    private int workout = -1;
    private int block = -1;
    private int exerciseOffset = 0;
    private long summaryDeadline;
    private TextView countdownView;
    private Runnable summaryTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(BG);
        window.setNavigationBarColor(BG);
        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        WatchProgressSync.prepareWeek(this);
        showHome();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
        WatchProgressSync.pullRemote(this, changed -> runOnUiThread(() -> {
            if (changed) refreshCurrentScreen();
        }));
    }

    @Override
    protected void onPause() {
        Wearable.getDataClient(this).removeListener(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cancelSummaryTimer();
        super.onDestroy();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        boolean relevant = false;
        for (DataEvent event : WearBufferCompat.<DataEvent>iterable(dataEvents)) {
            WatchProgressSync.applyEvent(this, event);
            relevant = true;
        }
        if (relevant) runOnUiThread(this::refreshCurrentScreen);
    }

    private void showHome() {
        cancelSummaryTimer();
        screen = SCREEN_HOME;
        workout = -1;
        block = -1;
        LinearLayout root = rootLayout();
        root.addView(header("TREINO DA LUANA", "×", this::finishAndRemoveTask), fullHeight(28));

        int done = totalDone();
        ProgressRingView ring = new ProgressRingView(this, done, WorkoutData.TOTAL_BLOCKS,
                String.valueOf(done), "DE 24 BLOCOS");
        root.addView(ring, centeredFixed(60, 60));
        root.addView(spacer(2));

        for (int row = 0; row < 2; row++) {
            LinearLayout pair = horizontal();
            for (int column = 0; column < 2; column++) {
                int selected = row * 2 + column;
                int workoutDone = workoutDone(selected);
                TextView card = workoutCard(selected, workoutDone);
                card.setOnClickListener(v -> openWorkoutSynced(selected));
                pair.addView(card, weightedHeight(44, column == 0 ? 3 : 0));
            }
            root.addView(pair, fullHeightWithMargins(44, 0, row == 0 ? 3 : 0));
        }
        setContentView(root);
    }

    private void openWorkoutSynced(int selectedWorkout) {
        WatchProgressSync.pullRemote(this, changed -> runOnUiThread(() -> showWorkout(selectedWorkout)));
    }

    private void showWorkout(int selectedWorkout) {
        cancelSummaryTimer();
        screen = SCREEN_WORKOUT;
        workout = selectedWorkout;
        block = -1;
        LinearLayout root = rootLayout();
        root.addView(header("TREINO " + WorkoutData.LETTERS[workout], "‹",
                () -> confirmBack("Voltar para a tela inicial?", this::showHome)), fullHeight(28));
        root.addView(label(WorkoutData.TYPES[workout], 8, ORANGE, true), fullHeight(11));
        root.addView(label(WorkoutData.FOCUSES[workout], 8, MUTED, true), fullHeight(14));
        root.addView(progressDots(workout), fullHeightWithMargins(15, 2, 4));

        for (int row = 0; row < 3; row++) {
            LinearLayout pair = horizontal();
            for (int column = 0; column < 2; column++) {
                int selectedBlock = row * 2 + column;
                TextView card = blockCard(selectedBlock);
                card.setOnClickListener(v -> showExercise(selectedBlock, 0));
                pair.addView(card, weightedHeight(38, column == 0 ? 3 : 0));
            }
            root.addView(pair, fullHeightWithMargins(38, 0, row < 2 ? 3 : 0));
        }
        setContentView(root);
    }

    private void showExercise(int selectedBlock, int offset) {
        cancelSummaryTimer();
        ensureWorkoutStarted();
        screen = SCREEN_EXERCISE;
        block = selectedBlock;
        exerciseOffset = Math.min(offset, WorkoutData.blockSize(block) - 1);
        int exercise = WorkoutData.blockStart(block) + exerciseOffset;

        LinearLayout root = rootLayout();
        String position = (block == 5 ? "FINAL" : (block + 1) + (exerciseOffset == 0 ? "A" : "B"));
        root.addView(header("TREINO " + WorkoutData.LETTERS[workout] + "  •  " + position, "‹",
                () -> confirmBack("Sair deste exercício e voltar aos blocos?",
                        () -> showWorkout(workout))), fullHeight(28));

        TextView title = label(WorkoutData.NAMES[workout][exercise], 13, WHITE, true);
        title.setMaxLines(2);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        root.addView(title, fullHeightWithMargins(30, 1, 1));
        root.addView(exerciseImage(workout, exercise), fullHeight(60));

        LinearLayout metrics = horizontal();
        metrics.addView(metric("REPETIÇÕES", compactReps(WorkoutData.REPS[workout][exercise]), ORANGE), weightedHeight(34, 2));
        String savedLoad = preferences.getString(loadKey(workout, exercise), "");
        metrics.addView(metric("CARGA", savedLoad.isEmpty() ? "— kg" : formatLoad(savedLoad), WHITE), weightedHeight(34, 0));
        root.addView(metrics, fullHeightWithMargins(34, 2, 2));

        root.addView(setIndicator(WorkoutData.SETS[workout][exercise]), fullHeightWithMargins(18, 0, 2));
        boolean done = isDone(workout, block);
        TextView action = primary(done ? "REABRIR BLOCO" : "CONCLUIR EXERCÍCIO");
        action.setOnClickListener(v -> {
            if (done) {
                setBlockDone(false);
                showExercise(block, exerciseOffset);
            } else {
                completeCurrentExercise();
            }
        });
        root.addView(action, fullHeight(30));
        setContentView(root);
    }

    private void completeCurrentExercise() {
        int size = WorkoutData.blockSize(block);
        if (exerciseOffset + 1 < size) {
            showExercise(block, exerciseOffset + 1);
            return;
        }
        setBlockDone(true);
        if (block == WorkoutData.BLOCKS_PER_WORKOUT - 1 && workoutDone(workout) == WorkoutData.BLOCKS_PER_WORKOUT) {
            showSummary();
        } else if (block + 1 < WorkoutData.BLOCKS_PER_WORKOUT) {
            showExercise(block + 1, 0);
        } else {
            showWorkout(workout);
        }
    }

    private void showSummary() {
        screen = SCREEN_SUMMARY;
        block = -1;
        long startedAt = preferences.getLong(startKey(workout), System.currentTimeMillis());
        long elapsedMillis = Math.max(0L, System.currentTimeMillis() - startedAt);
        preferences.edit().remove(startKey(workout)).apply();

        LinearLayout root = rootLayout();
        root.addView(header("TREINO FINALIZADO", "‹", this::showHome), fullHeight(28));
        root.addView(spacer(4));
        ProgressRingView check = new ProgressRingView(this, 1, 1, "✓", "CONCLUÍDO");
        root.addView(check, centeredFixed(56, 56));
        root.addView(label("PARABÉNS, LUANA", 14, WHITE, true), fullHeightWithMargins(22, 3, 1));

        LinearLayout metrics = horizontal();
        metrics.addView(metric("TEMPO", elapsed(elapsedMillis), WHITE), weightedHeight(38, 2));
        metrics.addView(metric("EXERCÍCIOS", "11 / 11", ORANGE), weightedHeight(38, 0));
        root.addView(metrics, fullHeightWithMargins(38, 4, 3));

        countdownView = label("Voltando ao início em 10 s", 11, MUTED, true);
        root.addView(countdownView, fullHeight(14));
        TextView home = primary("IR PARA O INÍCIO");
        home.setOnClickListener(v -> showHome());
        root.addView(home, fullHeightWithMargins(30, 2, 0));
        setContentView(root);
        startSummaryTimer();
    }

    private View exerciseImage(int selectedWorkout, int exercise) {
        FrameLayout frame = new FrameLayout(this);
        frame.setBackground(panel(OBSIDIAN, LINE, 20));
        Bitmap bitmap = loadBitmap(WorkoutData.imagePath(selectedWorkout, exercise));
        if (bitmap != null) {
            ImageView image = new ImageView(this);
            image.setImageBitmap(bitmap);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setContentDescription("Demonstração de " + WorkoutData.NAMES[selectedWorkout][exercise]);
            frame.addView(image, new FrameLayout.LayoutParams(-1, -1));
        } else {
            ExerciseOutlineView outline = new ExerciseOutlineView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp(150), -1, Gravity.CENTER);
            frame.addView(outline, params);
        }
        return frame;
    }

    private Bitmap loadBitmap(String path) {
        try (InputStream input = getAssets().open(path)) {
            return BitmapFactory.decodeStream(input);
        } catch (IOException ignored) {
            return null;
        }
    }

    private TextView workoutCard(int selectedWorkout, int done) {
        String value = "TREINO " + WorkoutData.LETTERS[selectedWorkout] + "\n"
                + done + "/6  " + shortType(selectedWorkout);
        TextView view = label(value, 10, done == 6 ? ORANGE : WHITE, true);
        view.setLineSpacing(dp(1), 1f);
        view.setPadding(dp(5), dp(4), dp(5), dp(4));
        view.setBackground(panel(done == 6 ? CARD_DONE : CARD, done == 6 ? ORANGE : LINE, 20));
        makeClickable(view);
        return view;
    }

    private TextView blockCard(int selectedBlock) {
        boolean done = isDone(workout, selectedBlock);
        int start = WorkoutData.blockStart(selectedBlock);
        String name = WorkoutData.NAMES[workout][start];
        if (name.length() > 19) name = name.substring(0, 18) + "…";
        String value = (done ? "✓  " : "") + (selectedBlock == 5 ? "FINAL" : "DUPLA " + (selectedBlock + 1))
                + "\n" + name;
        TextView view = label(value, 9, done ? ORANGE : WHITE, true);
        view.setPadding(dp(4), dp(3), dp(4), dp(3));
        view.setBackground(panel(done ? CARD_DONE : CARD, done ? ORANGE : LINE, 18));
        makeClickable(view);
        return view;
    }

    private View progressDots(int selectedWorkout) {
        LinearLayout row = horizontal();
        row.setGravity(Gravity.CENTER);
        for (int index = 0; index < WorkoutData.BLOCKS_PER_WORKOUT; index++) {
            TextView dot = label(isDone(selectedWorkout, index) ? "✓" : String.valueOf(index + 1), 10,
                    isDone(selectedWorkout, index) ? BG : MUTED, true);
            dot.setBackground(panel(isDone(selectedWorkout, index) ? ORANGE : CARD,
                    isDone(selectedWorkout, index) ? ORANGE : LINE, 16));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(15), dp(15));
            params.setMargins(dp(1), 0, dp(1), 0);
            row.addView(dot, params);
        }
        return row;
    }

    private View setIndicator(int sets) {
        LinearLayout row = horizontal();
        row.setGravity(Gravity.CENTER);
        TextView prefix = label("SÉRIES", 9, MUTED, true);
        row.addView(prefix, new LinearLayout.LayoutParams(dp(38), -1));
        for (int index = 0; index < sets; index++) {
            TextView dot = label(String.valueOf(index + 1), 10, BG, true);
            dot.setBackground(panel(ORANGE, ORANGE, 14));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(17), dp(17));
            params.setMargins(dp(1), 0, dp(1), 0);
            row.addView(dot, params);
        }
        return row;
    }

    private View metric(String caption, String value, int valueColor) {
        LinearLayout card = vertical();
        card.setGravity(Gravity.CENTER);
        card.setBackground(panel(CARD, LINE, 17));
        card.addView(label(caption, 7, MUTED, true), fullHeight(11));
        TextView number = label(value, 14, valueColor, true);
        number.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        card.addView(number, fullHeight(20));
        return card;
    }

    private View header(String title, String backSymbol, Runnable action) {
        LinearLayout row = horizontal();
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = label(backSymbol, backSymbol.equals("×") ? 17 : 21, ORANGE, true);
        back.setBackground(panel(CARD, LINE, 22));
        makeClickable(back);
        back.setOnClickListener(v -> action.run());
        row.addView(back, new LinearLayout.LayoutParams(dp(28), dp(28)));
        TextView heading = label(title, 10, WHITE, true);
        heading.setMaxLines(1);
        heading.setEllipsize(android.text.TextUtils.TruncateAt.END);
        row.addView(heading, new LinearLayout.LayoutParams(0, -1, 1f));
        row.addView(new View(this), new LinearLayout.LayoutParams(dp(28), dp(28)));
        return row;
    }

    private TextView primary(String value) {
        TextView view = label(value, 10, BG, true);
        view.setBackground(panel(ORANGE, ORANGE, 22));
        makeClickable(view);
        return view;
    }

    private TextView label(String value, int size, int color, boolean centered) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(size);
        view.setGravity(centered ? Gravity.CENTER : Gravity.START | Gravity.CENTER_VERTICAL);
        view.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        view.setIncludeFontPadding(false);
        return view;
    }

    private void makeClickable(View view) {
        view.setClickable(true);
        view.setFocusable(true);
    }

    private LinearLayout rootLayout() {
        LinearLayout root = vertical();
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(11), dp(8), dp(11), dp(8));
        root.setBackgroundColor(BG);
        return root;
    }

    private LinearLayout horizontal() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    private LinearLayout vertical() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private View progressBar(int done, int total) {
        LinearLayout track = horizontal();
        track.setBackground(panel(CARD, Color.TRANSPARENT, 6));
        View fill = new View(this);
        fill.setBackground(panel(ORANGE, Color.TRANSPARENT, 6));
        track.addView(fill, new LinearLayout.LayoutParams(0, -1, Math.max(done, .001f)));
        track.addView(new View(this), new LinearLayout.LayoutParams(0, -1, Math.max(total - done, .001f)));
        return track;
    }

    private GradientDrawable panel(int fill, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));
        if (Color.alpha(stroke) > 0) drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private void setBlockDone(boolean done) {
        preferences.edit().putBoolean(blockKey(workout, block), done).apply();
        WatchProgressSync.publishBlock(this, workout, block, done);
    }

    private void ensureWorkoutStarted() {
        String key = startKey(workout);
        if (!preferences.contains(key)) preferences.edit().putLong(key, System.currentTimeMillis()).apply();
    }

    private void confirmBack(String message, Runnable confirmed) {
        new AlertDialog.Builder(this)
                .setTitle("Voltar?")
                .setMessage(message)
                .setNegativeButton("CONTINUAR TREINO", null)
                .setPositiveButton("VOLTAR", (dialog, which) -> confirmed.run())
                .show();
    }

    private void startSummaryTimer() {
        cancelSummaryTimer();
        summaryDeadline = System.currentTimeMillis() + 10_000L;
        summaryTick = new Runnable() {
            @Override
            public void run() {
                long remaining = summaryDeadline - System.currentTimeMillis();
                if (remaining <= 0L) {
                    showHome();
                    return;
                }
                long seconds = (remaining + 999L) / 1_000L;
                if (countdownView != null) countdownView.setText("Voltando ao início em " + seconds + " s");
                handler.postDelayed(this, 250L);
            }
        };
        handler.post(summaryTick);
    }

    private void cancelSummaryTimer() {
        if (summaryTick != null) handler.removeCallbacks(summaryTick);
        summaryTick = null;
        countdownView = null;
    }

    private void refreshCurrentScreen() {
        if (screen == SCREEN_HOME) showHome();
        else if (screen == SCREEN_WORKOUT && workout >= 0) showWorkout(workout);
        else if (screen == SCREEN_EXERCISE && workout >= 0 && block >= 0) showExercise(block, exerciseOffset);
    }

    private String compactReps(String reps) {
        return reps.replace(" a ", "–").replace(" segundos", "s").replace(" por perna", "/perna")
                .replace(" por lado", "/lado");
    }

    private String formatLoad(String load) {
        return load.replace('.', ',') + " kg";
    }

    private String shortType(int selectedWorkout) {
        switch (selectedWorkout) {
            case 0: return "PUSH";
            case 1: return "QUADS";
            case 2: return "PULL";
            default: return "POSTERIOR";
        }
    }

    private String elapsed(long millis) {
        long totalMinutes = Math.max(1L, millis / 60_000L);
        long hours = totalMinutes / 60L;
        long minutes = totalMinutes % 60L;
        return hours > 0 ? String.format(Locale.US, "%d:%02d", hours, minutes) : totalMinutes + " min";
    }

    private String blockKey(int selectedWorkout, int selectedBlock) {
        return "w" + selectedWorkout + "_b" + selectedBlock;
    }

    private String loadKey(int selectedWorkout, int exercise) {
        return "load_w" + selectedWorkout + "_e" + exercise;
    }

    private String startKey(int selectedWorkout) {
        return "workout_start_w" + selectedWorkout;
    }

    private boolean isDone(int selectedWorkout, int selectedBlock) {
        return preferences.getBoolean(blockKey(selectedWorkout, selectedBlock), false);
    }

    private int workoutDone(int selectedWorkout) {
        int done = 0;
        for (int index = 0; index < WorkoutData.BLOCKS_PER_WORKOUT; index++) {
            if (isDone(selectedWorkout, index)) done++;
        }
        return done;
    }

    private int totalDone() {
        int done = 0;
        for (int index = 0; index < WorkoutData.LETTERS.length; index++) done += workoutDone(index);
        return done;
    }

    private LinearLayout.LayoutParams fullHeight(int height) {
        return new LinearLayout.LayoutParams(-1, dp(height));
    }

    private LinearLayout.LayoutParams fullHeightWithMargins(int height, int top, int bottom) {
        LinearLayout.LayoutParams params = fullHeight(height);
        params.setMargins(0, dp(top), 0, dp(bottom));
        return params;
    }

    private LinearLayout.LayoutParams weightedHeight(int height, int endMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(height), 1f);
        params.setMargins(0, 0, dp(endMargin), 0);
        return params;
    }

    private LinearLayout.LayoutParams centeredFixed(int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(width), dp(height));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        return params;
    }

    private View spacer(int height) {
        View view = new View(this);
        view.setLayoutParams(fullHeight(height));
        return view;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        if (screen == SCREEN_HOME) {
            finishAndRemoveTask();
        } else if (screen == SCREEN_SUMMARY) {
            showHome();
        } else if (screen == SCREEN_EXERCISE) {
            confirmBack("Sair deste exercício e voltar aos blocos?", () -> showWorkout(workout));
        } else {
            confirmBack("Voltar para a tela inicial?", this::showHome);
        }
    }
}
