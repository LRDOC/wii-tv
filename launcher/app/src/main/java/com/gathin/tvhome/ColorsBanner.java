package com.gathin.tvhome;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

/**
 * The Colors channel's banner: a rainbow arch on one line with the word COLORS, the word starting at the arch's
 * middle so it covers the arch's right half. Drawn in the menu's colours (Themed), on the plain-channel fill.
 */
final class ColorsBanner extends Drawable implements WiiMenuView.Themed {
    private static final int[] BANDS = {0xFFE8403A, 0xFFF39A2B, 0xFFF5D235, 0xFF4DBB5F, 0xFF3D8FE0, 0xFF8A5BD6};
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();
    private final String word;
    private WiiTheme theme = WiiTheme.CLASSIC;

    ColorsBanner(String word) {
        this.word = word;
    }

    @Override
    public void setTheme(WiiTheme theme) {
        this.theme = theme;
    }

    @Override
    public void draw(Canvas c) {
        Rect b = getBounds();
        float w = b.width(), h = b.height();
        int save = c.save();
        c.translate(b.left, b.top);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, h, theme.plainTop, theme.plainBottom, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        paint.setTextSize(h * 0.30f);
        paint.setLetterSpacing(0.04f);
        float textW = paint.measureText(word);
        // Arch and word side by side, centred as one line: the arch's left half, then the word over its right half.
        float outer = h * 0.36f, band = outer / 9f;
        float baseY = h * 0.66f;
        float left = (w - (outer + textW)) / 2f;
        float cx = left + outer;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(band + 0.5f);
        for (int i = 0; i < BANDS.length; i++) {
            float r = outer - band * (i + 0.5f);
            oval.set(cx - r, baseY - r, cx + r, baseY + r);
            paint.setColor(BANDS[i]);
            c.drawArc(oval, 180f, 180f, false, paint);
        }

        // The word gets a rim of the tile's own fill, so it cleanly cuts the arch where it crosses it.
        float x = cx - h * 0.02f;
        paint.setColor(theme.plainTop);
        paint.setStrokeWidth(h * 0.05f);
        paint.setStrokeJoin(Paint.Join.ROUND);
        c.drawText(word, x, baseY, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(theme.date);
        c.drawText(word, x, baseY, paint);
        c.restoreToCount(save);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter filter) {
        paint.setColorFilter(filter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
