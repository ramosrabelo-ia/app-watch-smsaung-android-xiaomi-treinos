package com.luanarabelo.treinodaluana.v12.wear;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;

public final class WatchMainActivity extends Activity implements DataClient.OnDataChangedListener {
    private static final int BG = Color.rgb(7, 9, 14);
    private static final int SURFACE = Color.rgb(19, 23, 31);
    private static final int SURFACE_DONE = Color.rgb(38, 29, 20);
    private static final int WHITE = Color.rgb(244, 245, 247);
    private static final int MUTED = Color.rgb(156, 164, 178);
    private static final int ORANGE = Color.rgb(255, 122, 26);

    private static final String[] LETTERS = {"A", "B", "C", "D"};
    private static final String[] TYPES = {
            "SUPERIOR PUSH", "INFERIOR QUADS", "SUPERIOR PULL", "INFERIOR POSTERIOR"
    };
    private static final String[] FOCUSES = {
            "Peito, ombro e tríceps",
            "Quadríceps e glúteos",
            "Costas, bíceps e ombro",
            "Posterior e glúteos"
    };
    private static final String[][][] BLOCKS = {
            {
                    {"Supino máquina", "Tríceps francês halter"},
                    {"Peck deck", "Elevação lateral"},
                    {"Desenvolvimento máquina", "Tríceps coice"},
                    {"Supino inclinado máquina", "Elevação frontal"},
                    {"Mergulho assistido", "Tríceps testa"},
                    {"Abdominal na máquina"}
            },
            {
                    {"Leg press 45°", "Agachamento goblet"},
                    {"Cadeira extensora", "Afundo reverso"},
                    {"Cadeira adutora", "Agachamento sumô"},
                    {"Búlgaro na Smith", "Stiff com halteres"},
                    {"Panturrilha no leg", "Isométrico com anilha"},
                    {"Prancha"}
            },
            {
                    {"Puxada alta", "Remada baixa"},
                    {"Remada articulada", "Rosca máquina"},
                    {"Remada unilateral", "Rosca martelo"},
                    {"Face pull", "Pulldown braços estendidos"},
                    {"Voador inverso", "Rosca alternada"},
                    {"Elevação de joelhos"}
            },
            {
                    {"Flexora sentada", "Stiff com halteres"},
                    {"Hip thrust máquina", "Stiff unilateral"},
                    {"Glúteo kickback", "Afundo reverso"},
                    {"Cadeira abdutora", "Afundo lateral"},
                    {"Panturrilha máquina", "Terra sumô"},
                    {"Abdominal infra reverso"}
            }
    };
    private static final String[][][] DETAILS = {
            {
                    {"3 x 10–12", "3 x 10–12"}, {"3 x 10–12", "3 x 12–15"},
                    {"3 x 8–10", "3 x 10–12"}, {"3 x 8–10", "3 x 12–15"},
                    {"2 x 8–10", "2 x 10–12"}, {"3 x 12–15"}
            },
            {
                    {"3 x 10–12", "3 x 10–12"}, {"3 x 10–12", "3 x 10 cada"},
                    {"3 x 12–15", "3 x 10–12"}, {"3 x 8–10 cada", "3 x 8–10"},
                    {"2 x 12–15", "2 x 30–40s"}, {"3 x 30–45s"}
            },
            {
                    {"3 x 8–10", "3 x 8–10"}, {"3 x 8–10", "3 x 10–12"},
                    {"3 x 8–12 cada", "3 x 10–12"}, {"3 x 12–15", "3 x 10–12"},
                    {"2 x 12–15", "2 x 10–12"}, {"3 x 10–15"}
            },
            {
                    {"3 x 10–12", "3 x 8–10"}, {"3 x 8–12", "3 x 8–10 cada"},
                    {"3 x 12 cada", "3 x 10 cada"}, {"3 x 15–20", "3 x 10 cada"},
                    {"2 x 12–15", "2 x 10–12"}, {"3 x 12–15"}
            }
    };

    private SharedPreferences preferences;
    private int workout = -1;
    private int block = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(BG);
        window.setNavigationBarColor(BG);
        preferences = getSharedPreferences("treino_v12_watch8", MODE_PRIVATE);
        WatchProgressSync.prepareWeek(this);
        showHome();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
        WatchProgressSync.pullRemote(this, changed -> {
            if (changed) runOnUiThread(this::refreshCurrentScreen);
        });
    }

    @Override
    protected void onPause() {
        Wearable.getDataClient(this).removeListener(this);
        super.onPause();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        boolean changed = false;
        for (DataEvent event : dataEvents) {
            changed |= WatchProgressSync.applyEvent(this, event);
        }
        if (changed) runOnUiThread(this::refreshCurrentScreen);
    }

    private void refreshCurrentScreen() {
        if (block >= 0) showBlock(block);
        else if (workout >= 0) showWorkout(workout);
        else showHome();
    }

    private void showHome() {
        workout = -1;
        block = -1;
        LinearLayout root = rootLayout();
        root.addView(text("V13 // GALAXY WATCH8", 11, ORANGE, true));
        root.addView(text("TREINO\nDA LUANA", 24, WHITE, true));
        root.addView(spacer(7));

        int total = totalDone();
        root.addView(text(total + " / 24 BLOCOS", 12, total == 24 ? ORANGE : MUTED, true));
        root.addView(progressLine(total, 24));
        root.addView(spacer(8));

        for (int i = 0; i < LETTERS.length; i++) {
            final int selected = i;
            int done = workoutDone(i);
            String label = "TREINO " + LETTERS[i] + "  ·  " + done + "/6\n"
                    + TYPES[i] + "\n" + FOCUSES[i];
            TextView card = card(label, done == 6);
            card.setOnClickListener(v -> showWorkout(selected));
            root.addView(card, cardParams());
        }
        root.addView(spacer(18));
        root.addView(text("SYNC COM O CELULAR", 10, MUTED, true));
        setContentView(scroll(root));
    }

    private void showWorkout(int selectedWorkout) {
        workout = selectedWorkout;
        block = -1;
        LinearLayout root = rootLayout();
        root.addView(back("‹  TODOS OS TREINOS", this::showHome), cardParams());
        root.addView(text("TREINO " + LETTERS[workout], 25, WHITE, true));
        root.addView(text(TYPES[workout], 12, ORANGE, true));
        root.addView(text(workoutDone(workout) + " / 6 CONCLUÍDOS", 11, MUTED, true));
        root.addView(progressLine(workoutDone(workout), 6));
        root.addView(spacer(8));

        for (int i = 0; i < BLOCKS[workout].length; i++) {
            final int selectedBlock = i;
            boolean done = isDone(workout, i);
            StringBuilder label = new StringBuilder();
            label.append(done ? "✓  " : "");
            label.append(i == 5 ? "ABS FINAL" : "DUPLA " + (i + 1));
            for (int e = 0; e < BLOCKS[workout][i].length; e++) {
                label.append("\n");
                label.append(i == 5 ? "" : (i + 1) + (e == 0 ? "A  " : "B  "));
                label.append(BLOCKS[workout][i][e]);
            }
            TextView card = card(label.toString(), done);
            card.setOnClickListener(v -> showBlock(selectedBlock));
            root.addView(card, cardParams());
        }
        root.addView(spacer(26));
        setContentView(scroll(root));
    }

    private void showBlock(int selectedBlock) {
        block = selectedBlock;
        LinearLayout root = rootLayout();
        root.addView(back("‹  TREINO " + LETTERS[workout], () -> showWorkout(workout)), cardParams());
        root.addView(text(block == 5 ? "ABS FINAL" : "DUPLA " + (block + 1), 24, WHITE, true));
        root.addView(text(block == 5 ? "FINALIZADOR" : "A → B → DESCANSO", 11, ORANGE, true));
        root.addView(spacer(8));

        for (int e = 0; e < BLOCKS[workout][block].length; e++) {
            String prefix = block == 5 ? "ABS" : (block + 1) + (e == 0 ? "A" : "B");
            String label = prefix + "\n" + BLOCKS[workout][block][e]
                    + "\n" + DETAILS[workout][block][e];
            TextView exercise = card(label, false);
            exercise.setTextSize(16);
            root.addView(exercise, cardParams());
        }

        boolean done = isDone(workout, block);
        TextView check = primary(done ? "✓  CONCLUÍDO" : "MARCAR CONCLUÍDO");
        check.setOnClickListener(v -> {
            boolean newValue = !isDone(workout, block);
            WatchProgressSync.publishBlock(this, workout, block, newValue);
            showBlock(block);
        });
        root.addView(check, cardParams());

        if (block < 5) {
            TextView next = back("PRÓXIMA  ›", () -> showBlock(block + 1));
            root.addView(next, cardParams());
        }
        root.addView(spacer(28));
        setContentView(scroll(root));
    }

    private LinearLayout rootLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(22), dp(22), dp(22), dp(34));
        root.setBackgroundColor(BG);
        return root;
    }

    private ScrollView scroll(View child) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.addView(child);
        return scroll;
    }

    private TextView text(String value, int size, int color, boolean centered) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(size);
        view.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        view.setGravity(centered ? Gravity.CENTER : Gravity.START);
        view.setLineSpacing(dp(2), 1f);
        return view;
    }

    private TextView card(String value, boolean done) {
        TextView view = text(value, 13, done ? ORANGE : WHITE, true);
        view.setPadding(dp(14), dp(13), dp(14), dp(13));
        view.setBackground(panel(done ? SURFACE_DONE : SURFACE, done ? ORANGE : Color.rgb(53, 61, 75), 18));
        view.setClickable(true);
        view.setFocusable(true);
        return view;
    }

    private TextView primary(String value) {
        TextView view = text(value, 13, BG, true);
        view.setPadding(dp(14), dp(14), dp(14), dp(14));
        view.setBackground(panel(ORANGE, ORANGE, 18));
        view.setClickable(true);
        view.setFocusable(true);
        return view;
    }

    private TextView back(String value, Runnable action) {
        TextView view = text(value, 12, ORANGE, true);
        view.setPadding(dp(12), dp(10), dp(12), dp(10));
        view.setBackground(panel(SURFACE, Color.rgb(53, 61, 75), 16));
        view.setClickable(true);
        view.setFocusable(true);
        view.setOnClickListener(v -> action.run());
        return view;
    }

    private View progressLine(int done, int total) {
        LinearLayout track = new LinearLayout(this);
        track.setOrientation(LinearLayout.HORIZONTAL);
        track.setBackground(panel(Color.rgb(31, 36, 46), Color.TRANSPARENT, 6));
        LinearLayout fill = new LinearLayout(this);
        fill.setBackground(panel(ORANGE, Color.TRANSPARENT, 6));
        track.addView(fill, new LinearLayout.LayoutParams(0, dp(6), Math.max(done, 0.02f)));
        track.addView(new View(this), new LinearLayout.LayoutParams(0, dp(6), Math.max(total - done, 0.02f)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(6));
        params.setMargins(0, dp(7), 0, dp(3));
        track.setLayoutParams(params);
        return track;
    }

    private GradientDrawable panel(int fill, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));
        if (Color.alpha(stroke) > 0) drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private LinearLayout.LayoutParams cardParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp(5), 0, dp(5));
        return params;
    }

    private View spacer(int height) {
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(height)));
        return view;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private String key(int selectedWorkout, int selectedBlock) {
        return "w" + selectedWorkout + "_b" + selectedBlock;
    }

    private boolean isDone(int selectedWorkout, int selectedBlock) {
        return preferences.getBoolean(key(selectedWorkout, selectedBlock), false);
    }

    private int workoutDone(int selectedWorkout) {
        int done = 0;
        for (int i = 0; i < 6; i++) if (isDone(selectedWorkout, i)) done++;
        return done;
    }

    private int totalDone() {
        int total = 0;
        for (int i = 0; i < 4; i++) total += workoutDone(i);
        return total;
    }

    @Override
    public void onBackPressed() {
        if (block >= 0) showWorkout(workout);
        else if (workout >= 0) showHome();
        else super.onBackPressed();
    }
}
