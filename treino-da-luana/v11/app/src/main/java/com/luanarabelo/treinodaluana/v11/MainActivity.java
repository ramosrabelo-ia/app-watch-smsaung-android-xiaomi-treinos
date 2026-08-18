package com.luanarabelo.treinodaluana.v11;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1110;
    private static final String WITHINGS_PACKAGE = "com.withings.wiscale2";
    private static final String WITHINGS_ENABLED = "withings_notifications_enabled";
    private static final int BLACK = Color.rgb(7, 7, 7);
    private static final int OBSIDIAN = Color.rgb(16, 16, 16);
    private static final int CARD = Color.rgb(24, 24, 24);
    private static final int CARD_LIGHT = Color.rgb(31, 31, 31);
    private static final int WHITE = Color.rgb(244, 239, 233);
    private static final int MUTED = Color.rgb(166, 157, 149);
    private static final int ORANGE = Color.rgb(255, 138, 61);
    private static final int ORANGE_DARK = Color.rgb(111, 58, 27);
    private static final int CYAN = Color.rgb(92, 200, 215);
    private static final int GREEN = Color.rgb(117, 205, 139);
    private static final int LINE = Color.rgb(58, 48, 42);

    private static final Locale PT_BR = new Locale("pt", "BR");

    private SharedPreferences preferences;
    private CountDownTimer restTimer;
    private TextView restTimerText;
    private int currentScreen = 0;
    private int currentWorkout = -1;
    private int currentExercise = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BLACK);
        getWindow().setNavigationBarColor(BLACK);
        preferences = getSharedPreferences("treino_v11", MODE_PRIVATE);
        NotificationHelper.createChannel(this);
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
        if (workout >= 0 && workout < 4 && exercise >= 0 && exercise < 7) {
            showExercise(workout, exercise);
        } else {
            showHome();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentScreen != 0) {
            showHome();
        } else {
            super.onBackPressed();
        }
    }

    private void showHome() {
        cancelRestTimer();
        currentScreen = 0;

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        scroll.setFillViewport(true);

        LinearLayout root = vertical();
        root.setPadding(dp(16), dp(16), dp(16), dp(34));
        scroll.addView(root, full());

        root.addView(createOfficialHero(), full(dp(230)));
        root.addView(space(14));
        root.addView(createWeeklySummary(), full());
        root.addView(space(12));
        root.addView(createWithingsHomeCard(), full());
        root.addView(space(24));

        TextView eyebrow = micro("PLANO DE TREINO", ORANGE);
        root.addView(eyebrow, full());

        TextView heading = heading("Sua semana em 4 treinos", 25);
        heading.setPadding(0, dp(5), 0, dp(14));
        root.addView(heading, full());

        for (int workout = 0; workout < WorkoutData.LETTERS.length; workout++) {
            root.addView(createWorkoutCard(workout), fullWithBottom(12));
        }

        Button progress = actionButton("VER PROGRESSO DA SEMANA", false);
        progress.setOnClickListener(view -> showProgress());
        root.addView(progress, full(dp(54)));

        TextView note = body("Treine no seu ritmo e ajuste cargas ou movimentos com um profissional quando precisar.", 12, MUTED);
        note.setGravity(Gravity.CENTER);
        note.setPadding(dp(14), dp(18), dp(14), 0);
        root.addView(note, full());

        setContentView(scroll);
    }

    private View createOfficialHero() {
        FrameLayout hero = new FrameLayout(this);
        hero.setBackground(round(OBSIDIAN, 24, 0, 0));
        hero.setClipToOutline(true);

        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription("Foto oficial da Luana no treino");
        image.setImageBitmap(loadAsset("heroes/hero_official.jpg"));
        hero.addView(image, frameMatch());

        View shade = new View(this);
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.argb(238, 7, 7, 7), Color.argb(145, 7, 7, 7), Color.argb(18, 7, 7, 7)}
        );
        shade.setBackground(gradient);
        hero.addView(shade, frameMatch());

        TextView dayChip = micro("CAPA OFICIAL", WHITE);
        dayChip.setGravity(Gravity.CENTER);
        dayChip.setBackground(round(Color.argb(180, 16, 16, 16), 14, ORANGE, 1));
        FrameLayout.LayoutParams chipParams = new FrameLayout.LayoutParams(dp(126), dp(30));
        chipParams.gravity = Gravity.TOP | Gravity.START;
        chipParams.setMargins(dp(18), dp(18), 0, 0);
        hero.addView(dayChip, chipParams);

        LinearLayout copy = vertical();
        TextView title = heading("TREINO DA\nLUANA", 31);
        title.setLineSpacing(0, 0.92f);
        copy.addView(title, full());

        TextView subtitle = micro("V11  •  PREMIUM OBSIDIAN", CYAN);
        subtitle.setPadding(0, dp(7), 0, 0);
        copy.addView(subtitle, full());

        FrameLayout.LayoutParams copyParams = new FrameLayout.LayoutParams(dp(260), LinearLayout.LayoutParams.WRAP_CONTENT);
        copyParams.gravity = Gravity.BOTTOM | Gravity.START;
        copyParams.setMargins(dp(18), 0, 0, dp(18));
        hero.addView(copy, copyParams);
        return hero;
    }

    private View createWeeklySummary() {
        LinearLayout card = card(LINE);
        card.setPadding(dp(18), dp(17), dp(18), dp(17));

        LinearLayout top = horizontal();
        LinearLayout copy = vertical();
        TextView label = micro("SEMANA ATUAL", ORANGE);
        copy.addView(label, full());
        TextView period = body(weekLabel(), 13, MUTED);
        period.setPadding(0, dp(4), 0, 0);
        copy.addView(period, full());
        top.addView(copy, weighted());

        int completeWorkouts = completedWorkoutCount();
        TextView score = heading(completeWorkouts + " de 4", 25);
        score.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        top.addView(score, wrap());
        card.addView(top, full());

        ProgressBar bar = progressBar((completeWorkouts * 100) / 4);
        LinearLayout.LayoutParams barParams = full(dp(7));
        barParams.setMargins(0, dp(15), 0, dp(12));
        card.addView(bar, barParams);

        int exercises = completedExerciseCount();
        TextView detail = body(exercises + " de 28 exercícios concluídos", 14, WHITE);
        card.addView(detail, full());

        TextView helper = body(completeWorkouts == 4
                ? "Semana completa. Todos os check-ins estão feitos."
                : "Toque em um treino para começar ou continuar de onde parou.", 12, MUTED);
        helper.setPadding(0, dp(5), 0, 0);
        card.addView(helper, full());
        return card;
    }

    private View createWithingsHomeCard() {
        boolean enabled = preferences.getBoolean(WITHINGS_ENABLED, false);
        boolean notifications = notificationsAllowed();
        boolean installed = isWithingsInstalled();
        boolean access = isWithingsNotificationAccessEnabled();
        boolean phoneReady = enabled && notifications && installed && access;

        LinearLayout card = card(phoneReady ? GREEN : ORANGE_DARK);
        card.setPadding(dp(16), dp(15), dp(16), dp(15));

        LinearLayout top = horizontal();
        LinearLayout copy = vertical();
        copy.addView(micro("WITHINGS", CYAN), full());
        TextView title = heading("Notificações no relógio", 19);
        title.setPadding(0, dp(3), 0, 0);
        copy.addView(title, full());
        top.addView(copy, weighted());

        TextView status = micro(phoneReady ? "CELULAR PRONTO" : "CONFIGURAR", phoneReady ? GREEN : ORANGE);
        status.setGravity(Gravity.CENTER);
        status.setBackground(round(CARD_LIGHT, 13, phoneReady ? GREEN : ORANGE, 1));
        top.addView(status, new LinearLayout.LayoutParams(dp(122), dp(30)));
        card.addView(top, full());

        TextView detail = body(phoneReady
                ? "Ao abrir um exercício, o nome e as séries serão enviados como notificação para o Withings."
                : "Faça o teste guiado para liberar o app no Android e selecionar a V11 dentro do Withings.", 13, MUTED);
        detail.setPadding(0, dp(10), 0, dp(12));
        card.addView(detail, full());

        Button setup = actionButton(phoneReady ? "REVISAR CONFIGURAÇÃO" : "CONFIGURAR WITHINGS", false);
        setup.setOnClickListener(view -> showWithingsSetup());
        card.addView(setup, full(dp(48)));
        return card;
    }

    private void openExercise(int workout, int exercise) {
        showExercise(workout, exercise);
        if (preferences.getBoolean(WITHINGS_ENABLED, false) && notificationsAllowed()) {
            NotificationHelper.showExercise(this, workout, exercise);
        }
    }

    private View createWorkoutCard(int workout) {
        int completed = completedExerciseCount(workout);
        boolean done = completed == WorkoutData.NAMES[workout].length;
        boolean started = completed > 0;

        LinearLayout card = card(done ? GREEN : LINE);
        card.setPadding(dp(16), dp(16), dp(16), dp(15));
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(view -> openExercise(workout, firstIncompleteExercise(workout)));

        LinearLayout top = horizontal();
        TextView badge = heading(WorkoutData.LETTERS[workout], 22);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(round(done ? GREEN : ORANGE, 18, 0, 0));
        badge.setTextColor(BLACK);
        top.addView(badge, fixed(46, 46));

        LinearLayout titleCopy = vertical();
        titleCopy.setPadding(dp(13), 0, dp(8), 0);
        TextView title = heading("TREINO " + WorkoutData.LETTERS[workout], 19);
        titleCopy.addView(title, full());
        TextView type = micro(WorkoutData.TYPES[workout], CYAN);
        type.setPadding(0, dp(3), 0, 0);
        titleCopy.addView(type, full());
        top.addView(titleCopy, weighted());

        String status = done ? "CONCLUÍDO" : started ? "CONTINUAR" : "COMEÇAR";
        TextView statusChip = micro(status, done ? GREEN : ORANGE);
        statusChip.setGravity(Gravity.CENTER);
        statusChip.setBackground(round(CARD_LIGHT, 13, done ? GREEN : ORANGE, 1));
        top.addView(statusChip, new LinearLayout.LayoutParams(dp(92), dp(30)));
        card.addView(top, full());

        TextView focus = body(WorkoutData.FOCUSES[workout], 16, WHITE);
        focus.setPadding(0, dp(15), 0, dp(11));
        card.addView(focus, full());

        LinearLayout progressCopy = horizontal();
        TextView amount = micro(completed + "/7 EXERCÍCIOS", MUTED);
        progressCopy.addView(amount, weighted());
        TextView percent = micro(((completed * 100) / 7) + "%", done ? GREEN : ORANGE);
        percent.setGravity(Gravity.END);
        progressCopy.addView(percent, wrap());
        card.addView(progressCopy, full());

        ProgressBar bar = progressBar((completed * 100) / 7);
        if (done) bar.setProgressTintList(ColorStateList.valueOf(GREEN));
        LinearLayout.LayoutParams barParams = full(dp(6));
        barParams.setMargins(0, dp(8), 0, 0);
        card.addView(bar, barParams);
        return card;
    }

    private void showExercise(int workout, int exercise) {
        cancelRestTimer();
        currentScreen = 1;
        currentWorkout = workout;
        currentExercise = exercise;

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        scroll.setFillViewport(true);
        LinearLayout root = vertical();
        root.setPadding(dp(16), dp(15), dp(16), dp(34));
        scroll.addView(root, full());

        LinearLayout toolbar = horizontal();
        TextView back = micro("‹  VOLTAR", ORANGE);
        back.setGravity(Gravity.CENTER_VERTICAL);
        back.setOnClickListener(view -> showHome());
        toolbar.addView(back, weighted());
        TextView route = micro("TREINO " + WorkoutData.LETTERS[workout] + "  •  " + (exercise + 1) + "/7", MUTED);
        route.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        toolbar.addView(route, wrap());
        root.addView(toolbar, full(dp(42)));

        boolean relayEnabled = preferences.getBoolean(WITHINGS_ENABLED, false) && notificationsAllowed();
        TextView watchStatus = micro(relayEnabled
                ? "● WITHINGS ATIVO  •  EXERCÍCIO ENVIADO AO RELÓGIO"
                : "○ WITHINGS DESLIGADO  •  TOQUE PARA CONFIGURAR", relayEnabled ? GREEN : ORANGE);
        watchStatus.setGravity(Gravity.CENTER);
        watchStatus.setBackground(round(CARD, 15, relayEnabled ? GREEN : ORANGE_DARK, 1));
        watchStatus.setOnClickListener(view -> showWithingsSetup());
        LinearLayout.LayoutParams watchParams = full(dp(42));
        watchParams.setMargins(0, 0, 0, dp(10));
        root.addView(watchStatus, watchParams);

        FrameLayout photo = new FrameLayout(this);
        photo.setBackground(round(OBSIDIAN, 24, 0, 0));
        photo.setClipToOutline(true);
        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription("Demonstração de " + WorkoutData.NAMES[workout][exercise]);
        image.setImageBitmap(loadAsset(WorkoutData.imagePath(workout, exercise)));
        photo.addView(image, frameMatch());

        View bottomShade = new View(this);
        GradientDrawable shade = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{Color.argb(225, 7, 7, 7), Color.argb(0, 7, 7, 7)}
        );
        bottomShade.setBackground(shade);
        FrameLayout.LayoutParams shadeParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, dp(130), Gravity.BOTTOM
        );
        photo.addView(bottomShade, shadeParams);

        TextView photoLabel = micro("EXERCÍCIO " + (exercise + 1) + " DE 7", WHITE);
        photoLabel.setGravity(Gravity.CENTER);
        photoLabel.setBackground(round(Color.argb(200, 16, 16, 16), 14, ORANGE, 1));
        FrameLayout.LayoutParams labelParams = new FrameLayout.LayoutParams(dp(132), dp(30));
        labelParams.gravity = Gravity.TOP | Gravity.START;
        labelParams.setMargins(dp(15), dp(15), 0, 0);
        photo.addView(photoLabel, labelParams);

        root.addView(photo, full(dp(355)));

        TextView type = micro(WorkoutData.TYPES[workout], CYAN);
        type.setPadding(dp(2), dp(20), 0, dp(7));
        root.addView(type, full());

        TextView name = heading(WorkoutData.NAMES[workout][exercise], 28);
        root.addView(name, full());

        String dose = WorkoutData.SETS[workout][exercise] + " SÉRIES  •  "
                + WorkoutData.REPS[workout][exercise].toUpperCase(PT_BR) + " REPETIÇÕES";
        TextView prescription = micro(dose, ORANGE);
        prescription.setPadding(0, dp(8), 0, dp(17));
        root.addView(prescription, full());

        LinearLayout tip = card(LINE);
        tip.setPadding(dp(15), dp(13), dp(15), dp(13));
        TextView tipLabel = micro("FOCO DE EXECUÇÃO", CYAN);
        tip.addView(tipLabel, full());
        TextView tipText = body(WorkoutData.TIPS[workout][exercise], 14, WHITE);
        tipText.setPadding(0, dp(6), 0, 0);
        tip.addView(tipText, full());
        root.addView(tip, fullWithBottom(21));

        TextView logTitle = heading("Registrar séries", 21);
        root.addView(logTitle, full());
        TextView logHelp = body("Toque em cada série quando terminar.", 13, MUTED);
        logHelp.setPadding(0, dp(4), 0, dp(12));
        root.addView(logHelp, full());

        final int[] liveMask = {getMask(workout, exercise)};
        int totalSets = WorkoutData.SETS[workout][exercise];
        TextView setCounter = micro(setCount(liveMask[0], totalSets) + " DE " + totalSets + " CONCLUÍDAS", ORANGE);
        setCounter.setPadding(0, 0, 0, dp(8));
        root.addView(setCounter, full());

        for (int set = 0; set < totalSets; set++) {
            final int setIndex = set;
            Button setButton = new Button(this);
            setButton.setAllCaps(false);
            setButton.setStateListAnimator(null);
            styleSetButton(setButton, (liveMask[0] & (1 << setIndex)) != 0, setIndex, workout, exercise);
            setButton.setOnClickListener(view -> {
                liveMask[0] ^= (1 << setIndex);
                saveMask(workout, exercise, liveMask[0]);
                boolean checked = (liveMask[0] & (1 << setIndex)) != 0;
                styleSetButton(setButton, checked, setIndex, workout, exercise);
                setCounter.setText(setCount(liveMask[0], totalSets) + " DE " + totalSets + " CONCLUÍDAS");
                updateWorkoutCheckin(workout);
            });
            root.addView(setButton, fullWithBottom(8));
        }

        restTimerText = micro("DESCANSO 60s", CYAN);
        restTimerText.setGravity(Gravity.CENTER);
        restTimerText.setBackground(round(CARD_LIGHT, 16, CYAN, 1));
        restTimerText.setOnClickListener(view -> startRestTimer());
        LinearLayout.LayoutParams restParams = full(dp(46));
        restParams.setMargins(0, dp(4), 0, dp(18));
        root.addView(restTimerText, restParams);

        Button finish = actionButton(exercise == 6 ? "FINALIZAR TREINO" : "CONCLUÍDO E PRÓXIMO", true);
        finish.setOnClickListener(view -> {
            markExerciseComplete(workout, exercise);
            updateWorkoutCheckin(workout);
            if (exercise < 6) {
                openExercise(workout, exercise + 1);
            } else {
                NotificationHelper.cancel(this);
                Toast.makeText(this, "Check-in do Treino " + WorkoutData.LETTERS[workout] + " concluído", Toast.LENGTH_LONG).show();
                showHome();
            }
        });
        root.addView(finish, full(dp(58)));

        LinearLayout navigation = horizontal();
        Button previous = smallButton("ANTERIOR", exercise > 0);
        previous.setOnClickListener(view -> openExercise(workout, exercise - 1));
        navigation.addView(previous, weightedWithRight(6));
        Button next = smallButton("PRÓXIMO", exercise < 6);
        next.setOnClickListener(view -> openExercise(workout, exercise + 1));
        navigation.addView(next, weightedWithLeft(6));
        LinearLayout.LayoutParams navParams = full(dp(50));
        navParams.setMargins(0, dp(10), 0, 0);
        root.addView(navigation, navParams);

        setContentView(scroll);
    }

    private void showWithingsSetup() {
        cancelRestTimer();
        currentScreen = 3;

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        scroll.setFillViewport(true);
        LinearLayout root = vertical();
        root.setPadding(dp(16), dp(16), dp(16), dp(34));
        scroll.addView(root, full());

        TextView back = micro("‹  VOLTAR PARA O INÍCIO", ORANGE);
        back.setGravity(Gravity.CENTER_VERTICAL);
        back.setOnClickListener(view -> showHome());
        root.addView(back, full(dp(42)));

        TextView eyebrow = micro("CONEXÃO POR NOTIFICAÇÃO", CYAN);
        eyebrow.setPadding(0, dp(8), 0, dp(5));
        root.addView(eyebrow, full());
        root.addView(heading("Configurar Withings", 31), full());

        LinearLayout explanation = card(ORANGE_DARK);
        explanation.setPadding(dp(16), dp(15), dp(16), dp(15));
        explanation.addView(micro("COMO FUNCIONA", ORANGE), full());
        TextView explanationText = body(
                "O relógio espelha as notificações do Android. A V11 envia o nome do exercício, as séries e as repetições; o app Withings transfere esse texto para o relógio.",
                14,
                WHITE
        );
        explanationText.setPadding(0, dp(7), 0, 0);
        explanation.addView(explanationText, full());
        LinearLayout.LayoutParams explanationParams = full();
        explanationParams.setMargins(0, dp(18), 0, dp(18));
        root.addView(explanation, explanationParams);

        boolean notificationReady = notificationsAllowed();
        boolean accessReady = isWithingsNotificationAccessEnabled();
        boolean installed = isWithingsInstalled();
        boolean relayEnabled = preferences.getBoolean(WITHINGS_ENABLED, false);

        root.addView(setupStep(
                "1",
                "Liberar a V11 no Android",
                "Permita notificações e envie o primeiro teste para a V11 aparecer entre os aplicativos disponíveis no Withings.",
                notificationReady ? "LIBERADO" : "PENDENTE",
                notificationReady ? GREEN : ORANGE
        ), fullWithBottom(8));
        Button notificationButton = actionButton(notificationReady ? "ENVIAR NOTIFICAÇÃO DE TESTE" : "PERMITIR E ENVIAR TESTE", true);
        notificationButton.setOnClickListener(view -> enableAndTestNotifications());
        root.addView(notificationButton, fullWithBottom(16, 52));

        root.addView(setupStep(
                "2",
                "Dar acesso ao Withings",
                "No acesso especial do Android, ative o Withings para que ele possa ler e encaminhar notificações ao relógio.",
                accessReady ? "LIBERADO" : "VERIFICAR",
                accessReady ? GREEN : ORANGE
        ), fullWithBottom(8));
        Button accessButton = actionButton("ABRIR ACESSO ÀS NOTIFICAÇÕES", false);
        accessButton.setOnClickListener(view -> openNotificationAccess());
        root.addView(accessButton, fullWithBottom(16, 52));

        root.addView(setupStep(
                "3",
                "Selecionar a V11 no Withings",
                "No Withings: Dispositivos, seu relógio, Notificações, Aplicativos. Ative “Treino da Luana v11” e volte.",
                installed ? "FAZER NO WITHINGS" : "APP AUSENTE",
                installed ? CYAN : ORANGE
        ), fullWithBottom(8));
        Button withingsButton = actionButton(installed ? "ABRIR O WITHINGS" : "INSTALAR O WITHINGS", false);
        withingsButton.setOnClickListener(view -> openWithings());
        root.addView(withingsButton, fullWithBottom(16, 52));

        root.addView(setupStep(
                "4",
                "Ativar durante o treino",
                "Quando estiver ativo, cada exercício aberto no celular gera uma notificação curta para aparecer no relógio.",
                relayEnabled ? "ATIVO" : "DESLIGADO",
                relayEnabled ? GREEN : MUTED
        ), fullWithBottom(8));

        Button relayButton = actionButton(relayEnabled ? "DESATIVAR ENVIO AUTOMÁTICO" : "ATIVAR ENVIO AUTOMÁTICO", !relayEnabled);
        relayButton.setOnClickListener(view -> {
            boolean next = !preferences.getBoolean(WITHINGS_ENABLED, false);
            preferences.edit().putBoolean(WITHINGS_ENABLED, next).apply();
            if (next) {
                enableAndTestNotifications();
            } else {
                NotificationHelper.cancel(this);
                showWithingsSetup();
            }
        });
        root.addView(relayButton, fullWithBottom(12, 54));

        Button settingsButton = actionButton("REVISAR NOTIFICAÇÕES DA V11", false);
        settingsButton.setOnClickListener(view -> openAppNotificationSettings());
        root.addView(settingsButton, full(dp(50)));

        TextView footer = body(
                "Para o relógio receber o aviso, deixe o Withings rodando em segundo plano e mantenha o modo Não Perturbe desligado no celular e no relógio.",
                12,
                MUTED
        );
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(dp(12), dp(18), dp(12), 0);
        root.addView(footer, full());

        setContentView(scroll);
    }

    private View setupStep(String number, String title, String detail, String status, int statusColor) {
        LinearLayout card = card(statusColor == MUTED ? LINE : statusColor);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));

        LinearLayout top = horizontal();
        TextView badge = heading(number, 18);
        badge.setTextColor(BLACK);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(round(statusColor == MUTED ? MUTED : statusColor, 15, 0, 0));
        top.addView(badge, fixed(40, 40));

        TextView titleView = heading(title, 17);
        titleView.setPadding(dp(12), 0, dp(8), 0);
        top.addView(titleView, weighted());

        TextView statusView = micro(status, statusColor);
        statusView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        top.addView(statusView, wrap());
        card.addView(top, full());

        TextView detailView = body(detail, 13, MUTED);
        detailView.setPadding(dp(52), dp(8), 0, 0);
        card.addView(detailView, full());
        return card;
    }

    private void enableAndTestNotifications() {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
            return;
        }
        preferences.edit().putBoolean(WITHINGS_ENABLED, true).apply();
        NotificationHelper.showTest(this);
        Toast.makeText(this, "Teste enviado. Confira o celular e o relógio.", Toast.LENGTH_LONG).show();
        showWithingsSetup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != NOTIFICATION_PERMISSION_REQUEST) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            preferences.edit().putBoolean(WITHINGS_ENABLED, true).apply();
            NotificationHelper.showTest(this);
            Toast.makeText(this, "Teste enviado. Agora selecione a V11 no Withings.", Toast.LENGTH_LONG).show();
        } else {
            preferences.edit().putBoolean(WITHINGS_ENABLED, false).apply();
            Toast.makeText(this, "As notificações continuam bloqueadas no Android.", Toast.LENGTH_LONG).show();
        }
        showWithingsSetup();
    }

    private boolean notificationsAllowed() {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return manager != null && manager.areNotificationsEnabled();
    }

    private boolean isWithingsInstalled() {
        return getPackageManager().getLaunchIntentForPackage(WITHINGS_PACKAGE) != null;
    }

    private boolean isWithingsNotificationAccessEnabled() {
        String listeners = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return listeners != null && listeners.contains(WITHINGS_PACKAGE);
    }

    private void openNotificationAccess() {
        try {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        } catch (Throwable error) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void openAppNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }

    private void openWithings() {
        Intent launch = getPackageManager().getLaunchIntentForPackage(WITHINGS_PACKAGE);
        if (launch != null) {
            startActivity(launch);
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + WITHINGS_PACKAGE)));
        } catch (Throwable error) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=" + WITHINGS_PACKAGE
            )));
        }
    }

    private void showProgress() {
        cancelRestTimer();
        currentScreen = 2;

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        LinearLayout root = vertical();
        root.setPadding(dp(16), dp(16), dp(16), dp(34));
        scroll.addView(root, full());

        TextView back = micro("‹  VOLTAR PARA O INÍCIO", ORANGE);
        back.setGravity(Gravity.CENTER_VERTICAL);
        back.setOnClickListener(view -> showHome());
        root.addView(back, full(dp(42)));

        TextView eyebrow = micro("CHECK-IN SEMANAL", CYAN);
        eyebrow.setPadding(0, dp(8), 0, dp(5));
        root.addView(eyebrow, full());
        root.addView(heading("Seu progresso", 31), full());

        int workouts = completedWorkoutCount();
        int exercises = completedExerciseCount();
        int percent = (exercises * 100) / 28;

        LinearLayout hero = card(ORANGE_DARK);
        hero.setPadding(dp(18), dp(19), dp(18), dp(19));
        LinearLayout stats = horizontal();
        LinearLayout percentBox = vertical();
        percentBox.addView(micro("SEMANA ATUAL", ORANGE), full());
        TextView percentText = heading(percent + "%", 42);
        percentText.setPadding(0, dp(5), 0, 0);
        percentBox.addView(percentText, full());
        stats.addView(percentBox, weighted());

        LinearLayout countBox = vertical();
        TextView workoutCount = heading(workouts + "/4", 24);
        workoutCount.setGravity(Gravity.END);
        countBox.addView(workoutCount, full());
        TextView workoutLabel = body("treinos", 12, MUTED);
        workoutLabel.setGravity(Gravity.END);
        countBox.addView(workoutLabel, full());
        TextView exerciseCount = body(exercises + "/28 exercícios", 13, WHITE);
        exerciseCount.setGravity(Gravity.END);
        exerciseCount.setPadding(0, dp(7), 0, 0);
        countBox.addView(exerciseCount, full());
        stats.addView(countBox, weighted());
        hero.addView(stats, full());

        ProgressBar totalProgress = progressBar(percent);
        LinearLayout.LayoutParams progressParams = full(dp(8));
        progressParams.setMargins(0, dp(17), 0, 0);
        hero.addView(totalProgress, progressParams);
        LinearLayout.LayoutParams heroParams = full();
        heroParams.setMargins(0, dp(18), 0, dp(24));
        root.addView(hero, heroParams);

        TextView week = heading("Treinos da semana", 22);
        week.setPadding(0, 0, 0, dp(12));
        root.addView(week, full());

        for (int workout = 0; workout < 4; workout++) {
            root.addView(createProgressRow(workout), fullWithBottom(10));
        }

        TextView message = body(workouts == 4
                ? "Semana fechada. Quatro treinos, quatro check-ins."
                : "Cada série marcada fica salva no celular. Você pode sair e continuar depois.", 14, MUTED);
        message.setGravity(Gravity.CENTER);
        message.setPadding(dp(18), dp(12), dp(18), dp(18));
        root.addView(message, full());

        Button home = actionButton("VOLTAR AOS TREINOS", true);
        home.setOnClickListener(view -> showHome());
        root.addView(home, full(dp(56)));

        setContentView(scroll);
    }

    private View createProgressRow(int workout) {
        int completed = completedExerciseCount(workout);
        boolean done = completed == 7;
        LinearLayout row = horizontal();
        row.setBackground(round(CARD, 22, done ? GREEN : LINE, 1));
        row.setPadding(dp(14), dp(14), dp(14), dp(14));
        row.setOnClickListener(view -> openExercise(workout, firstIncompleteExercise(workout)));

        TextView badge = heading(WorkoutData.LETTERS[workout], 19);
        badge.setTextColor(BLACK);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(round(done ? GREEN : ORANGE, 16, 0, 0));
        row.addView(badge, fixed(42, 42));

        LinearLayout copy = vertical();
        copy.setPadding(dp(12), 0, dp(8), 0);
        copy.addView(heading(WorkoutData.TYPES[workout], 16), full());
        TextView focus = body(WorkoutData.FOCUSES[workout], 12, MUTED);
        focus.setPadding(0, dp(3), 0, 0);
        copy.addView(focus, full());
        row.addView(copy, weighted());

        TextView status = micro(done ? "CHECK-IN" : completed + "/7", done ? GREEN : ORANGE);
        status.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        row.addView(status, wrap());
        return row;
    }

    private void styleSetButton(Button button, boolean checked, int set, int workout, int exercise) {
        String prefix = checked ? "✓  " : "○  ";
        button.setText(prefix + "SÉRIE " + (set + 1) + "     " + WorkoutData.REPS[workout][exercise] + " reps");
        button.setTextSize(14);
        button.setTextColor(checked ? BLACK : WHITE);
        button.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        button.setPadding(dp(16), 0, dp(16), 0);
        button.setTypeface(Typeface.create("monospace", Typeface.BOLD));
        button.setBackground(round(checked ? ORANGE : CARD, 17, checked ? ORANGE : LINE, 1));
    }

    private void startRestTimer() {
        cancelRestTimer();
        if (restTimerText == null) return;
        restTimerText.setText("DESCANSO 01:00");
        restTimer = new CountDownTimer(60_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = (millisUntilFinished + 999) / 1_000;
                restTimerText.setText(String.format(PT_BR, "DESCANSO 00:%02d", seconds));
            }

            @Override
            public void onFinish() {
                restTimerText.setText("DESCANSO CONCLUÍDO");
                restTimerText.setTextColor(ORANGE);
            }
        }.start();
    }

    private void cancelRestTimer() {
        if (restTimer != null) {
            restTimer.cancel();
            restTimer = null;
        }
        restTimerText = null;
    }

    private Bitmap loadAsset(String path) {
        try (InputStream stream = getAssets().open(path)) {
            return BitmapFactory.decodeStream(stream);
        } catch (IOException error) {
            Toast.makeText(this, "Não foi possível abrir a foto deste exercício.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String weekKey() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private String weekLabel() {
        Calendar calendar = Calendar.getInstance();
        String day = new SimpleDateFormat("d 'de' MMMM", PT_BR).format(calendar.getTime());
        return "Semana " + calendar.get(Calendar.WEEK_OF_YEAR) + "  •  " + day;
    }

    private String maskKey(int workout, int exercise) {
        return weekKey() + "_w" + workout + "_e" + exercise;
    }

    private int getMask(int workout, int exercise) {
        return preferences.getInt(maskKey(workout, exercise), 0);
    }

    private void saveMask(int workout, int exercise, int mask) {
        preferences.edit().putInt(maskKey(workout, exercise), mask).apply();
    }

    private boolean isExerciseComplete(int workout, int exercise) {
        int sets = WorkoutData.SETS[workout][exercise];
        int all = (1 << sets) - 1;
        return (getMask(workout, exercise) & all) == all;
    }

    private boolean isWorkoutComplete(int workout) {
        return completedExerciseCount(workout) == WorkoutData.NAMES[workout].length;
    }

    private void markExerciseComplete(int workout, int exercise) {
        int sets = WorkoutData.SETS[workout][exercise];
        saveMask(workout, exercise, (1 << sets) - 1);
    }

    private void updateWorkoutCheckin(int workout) {
        String key = weekKey() + "_checkin_" + workout;
        SharedPreferences.Editor editor = preferences.edit();
        if (isWorkoutComplete(workout)) {
            if (!preferences.contains(key)) editor.putLong(key, System.currentTimeMillis());
        } else {
            editor.remove(key);
        }
        editor.apply();
    }

    private int completedExerciseCount(int workout) {
        int count = 0;
        for (int exercise = 0; exercise < WorkoutData.NAMES[workout].length; exercise++) {
            if (isExerciseComplete(workout, exercise)) count++;
        }
        return count;
    }

    private int completedExerciseCount() {
        int count = 0;
        for (int workout = 0; workout < 4; workout++) count += completedExerciseCount(workout);
        return count;
    }

    private int completedWorkoutCount() {
        int count = 0;
        for (int workout = 0; workout < 4; workout++) if (isWorkoutComplete(workout)) count++;
        return count;
    }

    private int firstIncompleteExercise(int workout) {
        for (int exercise = 0; exercise < WorkoutData.NAMES[workout].length; exercise++) {
            if (!isExerciseComplete(workout, exercise)) return exercise;
        }
        return 0;
    }

    private int setCount(int mask, int totalSets) {
        int count = 0;
        for (int set = 0; set < totalSets; set++) if ((mask & (1 << set)) != 0) count++;
        return count;
    }

    private LinearLayout vertical() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private LinearLayout horizontal() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        return layout;
    }

    private LinearLayout card(int strokeColor) {
        LinearLayout layout = vertical();
        layout.setBackground(round(CARD, 22, strokeColor, 1));
        return layout;
    }

    private TextView heading(String value, int size) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(WHITE);
        view.setTextSize(size);
        view.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        view.setLetterSpacing(0.02f);
        return view;
    }

    private TextView body(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(size);
        view.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        view.setLineSpacing(dp(2), 1.0f);
        return view;
    }

    private TextView micro(String value, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(11);
        view.setTypeface(Typeface.create("monospace", Typeface.BOLD));
        view.setLetterSpacing(0.08f);
        return view;
    }

    private Button actionButton(String value, boolean primary) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setStateListAnimator(null);
        button.setText(value);
        button.setTextSize(14);
        button.setTextColor(primary ? BLACK : ORANGE);
        button.setTypeface(Typeface.create("monospace", Typeface.BOLD));
        button.setBackground(round(primary ? ORANGE : CARD, 18, ORANGE, 1));
        return button;
    }

    private Button smallButton(String value, boolean enabled) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setStateListAnimator(null);
        button.setText(value);
        button.setTextSize(12);
        button.setTypeface(Typeface.create("monospace", Typeface.BOLD));
        button.setTextColor(enabled ? WHITE : Color.rgb(78, 75, 72));
        button.setBackground(round(CARD, 16, enabled ? LINE : Color.rgb(35, 35, 35), 1));
        button.setEnabled(enabled);
        return button;
    }

    private ProgressBar progressBar(int progress) {
        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setProgress(progress);
        bar.setProgressTintList(ColorStateList.valueOf(ORANGE));
        bar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(52, 46, 42)));
        return bar;
    }

    private GradientDrawable round(int color, int radiusDp, int strokeColor, int strokeDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        if (strokeColor != 0 && strokeDp > 0) drawable.setStroke(dp(strokeDp), strokeColor);
        return drawable;
    }

    private View space(int height) {
        View view = new View(this);
        view.setLayoutParams(full(dp(height)));
        return view;
    }

    private LinearLayout.LayoutParams full() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams full(int height) {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
    }

    private LinearLayout.LayoutParams fullWithBottom(int bottom) {
        LinearLayout.LayoutParams params = full();
        params.setMargins(0, 0, 0, dp(bottom));
        return params;
    }

    private LinearLayout.LayoutParams fullWithBottom(int bottom, int height) {
        LinearLayout.LayoutParams params = full(dp(height));
        params.setMargins(0, 0, 0, dp(bottom));
        return params;
    }

    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams weighted() {
        return new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
    }

    private LinearLayout.LayoutParams weightedWithRight(int margin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        params.setMargins(0, 0, dp(margin), 0);
        return params;
    }

    private LinearLayout.LayoutParams weightedWithLeft(int margin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        params.setMargins(dp(margin), 0, 0, 0);
        return params;
    }

    private LinearLayout.LayoutParams fixed(int width, int height) {
        return new LinearLayout.LayoutParams(dp(width), dp(height));
    }

    private FrameLayout.LayoutParams frameMatch() {
        return new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
