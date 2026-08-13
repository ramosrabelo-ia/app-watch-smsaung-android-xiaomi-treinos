package com.luanarabelo.treinodaluana.v8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class ExerciseImageView extends View {
    private final Bitmap atlas;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final int cell;

    public ExerciseImageView(Context context, int cell) {
        super(context);
        this.cell = Math.max(0, Math.min(cell, 7));
        this.atlas = BitmapFactory.decodeResource(getResources(), R.drawable.exercise_atlas);
        setBackgroundColor(0xFF070A0D);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (atlas == null) return;

        int cellWidth = atlas.getWidth() / 4;
        int cellHeight = atlas.getHeight() / 2;
        int column = cell % 4;
        int row = cell / 4;

        Rect source = new Rect(
                column * cellWidth,
                row * cellHeight,
                column == 3 ? atlas.getWidth() : (column + 1) * cellWidth,
                row == 1 ? atlas.getHeight() : (row + 1) * cellHeight
        );

        float side = Math.min(getWidth(), getHeight());
        float left = (getWidth() - side) / 2f;
        float top = (getHeight() - side) / 2f;
        RectF destination = new RectF(left, top, left + side, top + side);
        canvas.drawBitmap(atlas, source, destination, paint);
    }
}
