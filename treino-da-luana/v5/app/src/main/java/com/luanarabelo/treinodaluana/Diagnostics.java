package com.luanarabelo.treinodaluana;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public final class Diagnostics {
    private static final String FILE_NAME = "crash_report.txt";
    public static final String REPORT_EMAIL = "luanarabelo2@gmail.com";

    private Diagnostics() {}

    public static void saveCrash(Context context, Throwable throwable) {
        try {
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            String text = "DATA: " + new java.util.Date() + "\n\n" + sw;
            try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
                fos.write(text.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Throwable ignored) {
        }
    }

    public static String readSavedCrash(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) return "Nenhum crash anterior registrado.";
            byte[] data = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                int read = fis.read(data);
                if (read <= 0) return "Arquivo de crash vazio.";
            }
            return new String(data, StandardCharsets.UTF_8);
        } catch (Throwable t) {
            return "Falha ao ler crash anterior: " + t;
        }
    }

    public static String buildReport(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("TREINO DA LUANA - RELATÓRIO COMPLETO\n");
        sb.append("===================================\n\n");
        try {
            PackageInfo p = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            sb.append("App: Treino da Luana\n");
            sb.append("Pacote: ").append(context.getPackageName()).append("\n");
            sb.append("Versão: ").append(p.versionName).append(" (").append(p.getLongVersionCode()).append(")\n\n");
        } catch (Throwable t) {
            sb.append("Falha ao ler versão do app: ").append(t).append("\n\n");
        }

        sb.append("FABRICANTE: ").append(Build.MANUFACTURER).append("\n");
        sb.append("MARCA: ").append(Build.BRAND).append("\n");
        sb.append("MODELO: ").append(Build.MODEL).append("\n");
        sb.append("DISPOSITIVO: ").append(Build.DEVICE).append("\n");
        sb.append("ANDROID: ").append(Build.VERSION.RELEASE).append("\n");
        sb.append("SDK: ").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("BUILD: ").append(Build.DISPLAY).append("\n");
        sb.append("FINGERPRINT: ").append(Build.FINGERPRINT).append("\n\n");

        sb.append("ÚLTIMO CRASH REGISTRADO\n");
        sb.append("------------------------\n");
        sb.append(readSavedCrash(context)).append("\n");
        return sb.toString();
    }

    public static void sendReport(Context context) {
        String subject = "Treino da Luana v5 - relatório de diagnóstico";
        String body = buildReport(context);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + REPORT_EMAIL));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{REPORT_EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (Throwable first) {
            Intent fallback = new Intent(Intent.ACTION_SEND);
            fallback.setType("text/plain");
            fallback.putExtra(Intent.EXTRA_EMAIL, new String[]{REPORT_EMAIL});
            fallback.putExtra(Intent.EXTRA_SUBJECT, subject);
            fallback.putExtra(Intent.EXTRA_TEXT, body);
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(fallback, "Enviar diagnóstico").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
