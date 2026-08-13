package com.luanarabelo.treinodaluana.v7;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 707;
    private static final int BLACK = Color.rgb(7, 10, 13);
    private static final int GRAPHITE = Color.rgb(18, 24, 32);
    private static final int WHITE = Color.rgb(232, 238, 242);
    private static final int GREEN = Color.rgb(124, 255, 107);
    private static final int CYAN = Color.rgb(52, 214, 255);
    private TextView status;
    private int pendingWorkout = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showScreen();
        try {
            NotificationHelper.createChannel(this);
        } catch (Throwable error) {
            status.setText("O aplicativo abriu, mas houve um erro ao preparar as notificações.");
        }
    }

    private void showScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BLACK);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        root.setPadding(dp(22), dp(24), dp(22), dp(24));
        scroll.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.MATCH_PARENT
        ));

        scroll.setOnApplyWindowInsetsListener((view, insets) -> {
            int safeTop = insets.getSystemWindowInsetTop();
            int safeBottom = insets.getSystemWindowInsetBottom();
            root.setPadding(dp(22), safeTop + dp(24), dp(22), safeBottom + dp(24));
            return insets;
        });

        TextView title = text("TREINO DA LUANA", 27, WHITE);
        title.setGravity(Gravity.CENTER);
        root.addView(title, fullWidth());

        TextView subtitle = text("V7 // MAROMBA TECH", 15, CYAN);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(6), 0, dp(8));
        root.addView(subtitle, fullWidth());

        TextView online = text("[ SYSTEM // ONLINE ]", 13, GREEN);
        online.setGravity(Gravity.CENTER);
        online.setPadding(0, 0, 0, dp(22));
        root.addView(online, fullWidth());

        for (int index = 0; index < WorkoutData.LETTERS.length; index++) {
            final int workout = index;
            Button button = new Button(this);
            button.setAllCaps(false);
            button.setText("TREINO " + WorkoutData.LETTERS[index] + "     READY\n" + WorkoutData.TITLES[index]);
            button.setTextSize(15);
            button.setTextColor(WHITE);
            button.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            button.setGravity(Gravity.CENTER);
            button.setTextAlignment(Button.TEXT_ALIGNMENT_CENTER);
            button.setPadding(dp(18), dp(12), dp(18), dp(12));
            button.setBackground(panelBackground(CYAN));
            button.setOnClickListener(view -> startWorkout(workout));
            LinearLayout.LayoutParams params = fullWidth();
            params.setMargins(0, 0, 0, dp(10));
            root.addView(button, params);
        }

        status = text("● SYSTEM READY // V7 CENTRALIZADA", 14, GREEN);
        status.setGravity(Gravity.CENTER);
        status.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        status.setPadding(dp(12), dp(14), dp(12), dp(16));
        root.addView(status, fullWidth());

        Button copy = new Button(this);
        copy.setAllCaps(false);
        copy.setText("COPIAR LOG");
        styleActionButton(copy);
        copy.setOnClickListener(view -> {
            Diagnostics.copy(this, null);
            Toast.makeText(this, "Diagnóstico copiado", Toast.LENGTH_SHORT).show();
        });
        LinearLayout.LayoutParams copyParams = fullWidth();
        copyParams.setMargins(0, 0, 0, dp(8));
        root.addView(copy, copyParams);

        Button email = new Button(this);
        email.setAllCaps(false);
        email.setText("ENVIAR LOG");
        styleActionButton(email);
        email.setOnClickListener(view -> {
            try {
                Diagnostics.send(this, null);
            } catch (Throwable error) {
                Diagnostics.copy(this, error);
                Toast.makeText(this, "Não abriu o e-mail. O diagnóstico foi copiado.", Toast.LENGTH_LONG).show();
            }
        });
        root.addView(email, fullWidth());

        setContentView(scroll);
    }

    private void startWorkout(int workout) {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            pendingWorkout = workout;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
            return;
        }
        publishWorkout(workout);
    }

    private void publishWorkout(int workout) {
        try {
            if (NotificationHelper.show(this, workout, 0)) {
                status.setText("● TREINO " + WorkoutData.LETTERS[workout]
                        + " ATIVO // EXERCÍCIO 01 ENVIADO");
            } else {
                status.setText("Não foi possível mostrar a notificação.");
            }
        } catch (Throwable error) {
            status.setText("Erro na notificação. Toque em COPIAR LOG.");
            Diagnostics.copy(this, error);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != NOTIFICATION_PERMISSION_REQUEST) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && pendingWorkout >= 0) {
            publishWorkout(pendingWorkout);
        } else {
            status.setText("A abertura funcionou, mas as notificações foram bloqueadas.");
        }
        pendingWorkout = -1;
    }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(Typeface.MONOSPACE);
        return view;
    }

    private void styleActionButton(Button button) {
        button.setTextColor(CYAN);
        button.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setTextAlignment(Button.TEXT_ALIGNMENT_CENTER);
        button.setBackground(panelBackground(GREEN));
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
