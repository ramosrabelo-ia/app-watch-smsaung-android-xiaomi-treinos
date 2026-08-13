package com.luanarabelo.treinodaluana.v9;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public final class Diagnostics {
    public static final String REPORT_EMAIL = "luanarabelo2@gmail.com";

    private Diagnostics() {}

    public static String report(Context context, Throwable error) {
        StringBuilder text = new StringBuilder();
        text.append("Treino da Luana v9\n");
        text.append("Pacote: ").append(context.getPackageName()).append("\n");
        text.append("Fabricante: ").append(Build.MANUFACTURER).append("\n");
        text.append("Modelo: ").append(Build.MODEL).append("\n");
        text.append("Android: ").append(Build.VERSION.RELEASE).append("\n");
        text.append("SDK: ").append(Build.VERSION.SDK_INT).append("\n");
        if (error != null) {
            text.append("Erro: ").append(error.getClass().getName()).append("\n");
            text.append("Mensagem: ").append(String.valueOf(error.getMessage())).append("\n");
        }
        return text.toString();
    }

    public static void copy(Context context, Throwable error) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Diagnóstico Treino da Luana", report(context, error)));
    }

    public static void send(Context context, Throwable error) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + REPORT_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Treino da Luana v9, diagnóstico");
        intent.putExtra(Intent.EXTRA_TEXT, report(context, error));
        context.startActivity(intent);
    }
}
