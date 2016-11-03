package cn.xz.mytodo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Xml中配置：
 * <com.xxx.view.CircleProgressBar
 android:id="@+id/circleProgressbar"
 android:layout_width="74dp"
 android:layout_height="74dp"
 android:layout_centerInParent="true" />
 代码中：
 mCircleBar = (CircleProgressBar) findViewById(R.id.circleProgressbar);
 mCircleBar.setProgress(80);
 * Created by gsx on 2016/10/16.
 */

public class CircleProgressView extends View {
    private static final String TAG = "CircleProgressBar";

    private int maxProgress = 100;

    private int mProgress = 30;

    private final int mCircleLineStrokeWidth = 8;

    private final int txtStrokeWidth = 2;

    // 画圆所在的距形区域
    private final RectF rectF;

    private final Paint paint;

    private final Context context;

    private String txtHint1;

    private String txtHint2;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        rectF = new RectF();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // 设置画笔相关属性
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
        canvas.drawColor(Color.TRANSPARENT);
        paint.setStrokeWidth(mCircleLineStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        // 位置
        rectF.left = mCircleLineStrokeWidth / 2; // 左上角x
        rectF.top = mCircleLineStrokeWidth / 2; // 左上角y
        rectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
        rectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(rectF, -90, 360, false, paint);
        paint.setColor(Color.rgb(0xf8, 0x60, 0x30));
        canvas.drawArc(rectF, -90, ((float) mProgress / maxProgress) * 360, false, paint);

        // 绘制进度文案显示
        paint.setStrokeWidth(txtStrokeWidth);
        String text = mProgress + "%";
        int textHeight = height / 4;
        paint.setTextSize(textHeight);
        int textWidth = (int) paint.measureText(text, 0, text.length());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, paint);

        if (!TextUtils.isEmpty(txtHint1)) {
            paint.setStrokeWidth(txtStrokeWidth);
            text = txtHint1;
            textHeight = height / 8;
            paint.setTextSize(textHeight);
            paint.setColor(Color.rgb(0x99, 0x99, 0x99));
            textWidth = (int) paint.measureText(text, 0, text.length());
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, width / 2 - textWidth / 2, height / 4 + textHeight / 2, paint);
        }

        if (!TextUtils.isEmpty(txtHint2)) {
            paint.setStrokeWidth(txtStrokeWidth);
            text = txtHint2;
            textHeight = height / 8;
            paint.setTextSize(textHeight);
            textWidth = (int) paint.measureText(text, 0, text.length());
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, width / 2 - textWidth / 2, 3 * height / 4 + textHeight / 2, paint);
        }
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        this.invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.mProgress = progress;
        this.postInvalidate();
    }

    public String getTxtHint1() {
        return txtHint1;
    }

    public void setTxtHint1(String txtHint1) {
        this.txtHint1 = txtHint1;
    }

    public String getTxtHint2() {
        return txtHint2;
    }

    public void setTxtHint2(String txtHint2) {
        this.txtHint2 = txtHint2;
    }
}
