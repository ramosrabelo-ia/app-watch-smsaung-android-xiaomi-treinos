package com.luanarabelo.treinodaluana;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 36;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            NotificationHelper.createChannel(this);
            buildMainScreen();
            askNotificationPermissionIfNeeded();
        } catch (Throwable t) {
            Diagnostics.saveCrash(this, t);
            buildFallbackScreen(t);
        }
    }

    private void buildMainScreen() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(28));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("TREINO DA LUANA");
        title.setTextSize(30);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title, fullWidth());

        TextView subtitle = new TextView(this);
        subtitle.setText("meu primeiro APK • v5");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.DKGRAY);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, dp(24));
        root.addView(subtitle, fullWidth());

        for (int i = 0; i < 4; i++) {
            final int workout = i;
            Button button = new Button(this);
            button.setText("TREINO " + WorkoutData.LETTERS[i] + "\n" + WorkoutData.TITLES[i]);
            button.setTextSize(16);
            button.setAllCaps(false);
            button.setPadding(dp(12), dp(14), dp(12), dp(14));
            button.setOnClickListener(v -> startWorkout(workout));
            LinearLayout.LayoutParams bp = fullWidth();
            bp.setMargins(0, 0, 0, dp(10));
            root.addView(button, bp);
        }

        status = new TextView(this);
        status.setText("Escolha um treino. O exercício vai aparecer nas notificações do celular e pode ser espelhado no ScanWatch.");
        status.setTextSize(16);
        status.setTextColor(Color.DKGRAY);
        status.setPadding(dp(4), dp(14), dp(4), dp(18));
        root.addView(status, fullWidth());

        Button report = new Button(this);
        report.setText("Enviar diagnóstico completo");
        report.setAllCaps(false);
        report.setOnClickListener(v -> {
            try {
                Diagnostics.sendReport(this);
            } catch (Throwable t) {
                Diagnostics.saveCrash(this, t);
                Toast.makeText(this, "Não consegui abrir o e-mail. O relatório foi salvo no app.", Toast.LENGTH_LONG).show();
            }
        });
        root.addView(report, fullWidth());

        TextView email = new TextView(this);
        email.setText("Relatórios: " + Diagnostics.REPORT_EMAIL);
        email.setTextSize(13);
        email.setTextColor(Color.GRAY);
        email.setGravity(Gravity.CENTER);
        email.setPadding(0, dp(8), 0, 0);
        root.addView(email, fullWidth());

        setContentView(scroll);
        refreshStatusFromSavedState();
    }

    private void startWorkout(int workout) {
        try {
            if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
                status.setText("Autorize as notificações e toque no treino novamente.");
                return;
            }

            boolean shown = NotificationHelper.show(this, workout, 0);
            if (shown) {
                setStatus(workout, 0, "Notificação enviada");
            } else {
                status.setText("Não consegui publicar a notificação. Toque em 'Enviar diagnóstico completo'.");
            }
        } catch (Throwable t) {
            Diagnostics.saveCrash(this, t);
            status.setText("Ocorreu um erro. Toque em 'Enviar diagnóstico completo'.");
        }
    }

    private void refreshStatusFromSavedState() {
        int[] state = NotificationHelper.getState(this);
        int workout = state[0];
        int exercise = state[1];
        if (workout >= 0 && workout < WorkoutData.NAMES.length && exercise >= 0 && exercise < WorkoutData.NAMES[workout].length) {
            setStatus(workout, exercise, "Treino ativo");
        }
    }

    private void setStatus(int workout, int exercise, String prefix) {
        String text = prefix + ":\n" + WorkoutData.LETTERS[workout] + " " + (exercise + 1) + "/" + WorkoutData.NAMES[workout].length
                + " • " + WorkoutData.NAMES[workout][exercise] + "\n" + WorkoutData.REPS[workout][exercise];
        status.setText(text);
    }

    private void askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST && status != null) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                status.setText("Notificações autorizadas. Agora escolha o Treino A, B, C ou D.");
            } else {
                status.setText("Notificações bloqueadas. O app abre normalmente, mas o exercício não poderá aparecer no relógio.");
            }
        }
    }

    private void buildFallbackScreen(Throwable t) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(32));

        TextView title = new TextView(this);
        title.setText("Treino da Luana abriu em modo de diagnóstico");
        title.setTextSize(24);
        title.setTextColor(Color.BLACK);
        root.addView(title, fullWidth());

        TextView error = new TextView(this);
        error.setText("Erro: " + t.getClass().getName() + "\n" + String.valueOf(t.getMessage()));
        error.setTextSize(15);
        error.setTextColor(Color.DKGRAY);
        error.setPadding(0, dp(18), 0, dp(18));
        root.addView(error, fullWidth());

        Button report = new Button(this);
        report.setText("Enviar relatório completo para " + Diagnostics.REPORT_EMAIL);
        report.setAllCaps(false);
        report.setOnClickListener(v -> Diagnostics.sendReport(this));
        root.addView(report, fullWidth());

        setContentView(root);
    }

    private LinearLayout.LayoutParams fullWidth() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
