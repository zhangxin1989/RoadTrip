package com.example.pathandsvgdemo;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SvgAnimView extends View {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mDuration = 4000;

    private final SvgHelper mSvg = new SvgHelper(mPaint);
    private int mSvgResource = R.raw.china_administrative_de_facto;
    private final Object mSvgLock = new Object();
    private List<SvgHelper.SvgPath> mPaths = new ArrayList<SvgHelper.SvgPath>(0);
    private float mPhase;
    private ObjectAnimator mSvgAnimator;

    public SvgAnimView(Context context) {
        this(context, null);
    }

    public SvgAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SvgAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setStyle(Paint.Style.STROKE);
        // Note: using a software layer here is an optimization. This view works with
        // hardware accelerated rendering but every time a path is modified (when the
        // dash path effect is modified), the graphics pipeline will rasterize the path
        // again in a new texture. Since we are dealing with dozens of paths, it is much
        // more efficient to rasterize the entire view into a single re-usable texture
        // instead. Ideally this should be toggled using a heuristic based on the number
        // and or dimensions of paths to render.
        // Note that PathDashPathEffects can lead to clipping issues with hardware rendering.
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Thread loader = new Thread(new Runnable() {
            @Override
            public void run() {
                //读取SVG文件到Svg对象中
                mSvg.load(getContext(), mSvgResource);
                synchronized (mSvgLock) {
                    //获取当前SVG文件对应的path并保存到mPaths对象中
                    mPaths = mSvg.getPathsForViewport(
                            w - getPaddingLeft() - getPaddingRight(),
                            h - getPaddingTop() - getPaddingBottom());
                    updatePathsPhaseLocked();
                }
            }
        }, "SVG Loader");
        loader.start();

    }

    private void updatePathsPhaseLocked() {
        final int count = mPaths.size();
        for (int i = 0; i < count; i++) {
            SvgHelper.SvgPath svgPath = mPaths.get(i);
            svgPath.renderPath.reset();
            svgPath.measure.getSegment(0.0f, svgPath.length * mPhase, svgPath.renderPath, true);
            // Required only for Android 4.4 and earlier
            svgPath.renderPath.rLineTo(0.0f, 0.0f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mSvgLock) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop() - getPaddingBottom());
            final int count = mPaths.size();
            for (int i = 0; i < count; i++) {
                SvgHelper.SvgPath svgPath = mPaths.get(i);

                canvas.drawPath(svgPath.renderPath, svgPath.paint);
            }
            canvas.restore();
        }
    }

    public void startAnim() {
        if (mSvgAnimator == null) {
            mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f).setDuration(mDuration);
        }

        if (mSvgAnimator.isRunning()) {
            mSvgAnimator.cancel();
        }
        mSvgAnimator.start();
    }

    public float getPhase() {
        return mPhase;
    }
    public void setPhase(float phase) {
        mPhase = phase;
        synchronized (mSvgLock) {
            updatePathsPhaseLocked();
        }
        invalidate();
    }
}
