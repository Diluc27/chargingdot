package io.github.diluc27.chargingdot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class DotView extends View {
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int dotColor = DotSpec.COLOR_CYAN;

    public DotView(Context context) {
        super(context);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    public void setDotColor(int color) {
        if (this.dotColor != color) {
            this.dotColor = color;
            postInvalidate();
        }
    }

    public int getDotColor() {
        return dotColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        fillPaint.setColor(dotColor);
        float radius = Math.min(getWidth(), getHeight()) / 2f;
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, fillPaint);
    }
}
