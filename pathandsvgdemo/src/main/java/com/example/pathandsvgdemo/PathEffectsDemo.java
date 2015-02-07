/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pathandsvgdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.SumPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PathEffectsDemo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SampleView(this));
    }

    private static class SampleView extends View {
        private Paint mPaint;
        private Paint mDragPaint;
        private Paint mTextPaint;
        private Path mPath;
        private Path mArrowPath;
        private PathEffect[] mEffects;
        private int[] mColors;
        private float mPhase;
        private float mDragPhase = 1.0f;

        private Path mDragPath;
        private float mPathLength;

        private int mArrowLength = 50;
        private int mArrowHeight = 25;

        public SampleView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(12);

            mDragPaint = new Paint(mPaint);
            mDragPaint.setStrokeWidth(8);

            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextSize(35);

            mPath = makeFollowPath();
            mArrowPath = new Path();
            mArrowPath.moveTo(0.0f, 0.0f);
            mArrowPath.lineTo(900, 0.0f);

            mEffects = new PathEffect[7];

            mColors = new int[] { Color.BLACK, Color.RED, Color.BLUE,
                    Color.GREEN, Color.MAGENTA, Color.BLACK, Color.CYAN
            };


            mDragPath = makeDragPath(50);
            PathMeasure pathMeasure = new PathMeasure(mDragPath, false);
            mPathLength = pathMeasure.getLength();
        }

        private static PathEffect makeDash(float phase) {
            return new DashPathEffect(new float[] { 15, 5, 8, 5 }, phase);
        }

        private static void makeEffects(PathEffect[] e, float phase) {
            e[0] = null;     // no effect
            //六种效果的PathEffect
            e[1] = new CornerPathEffect(16);
            e[2] = new DashPathEffect(new float[] {20, 38, 14, 5}, phase);
            e[3] = new PathDashPathEffect(makePathDash(), 35, phase,
                    PathDashPathEffect.Style.ROTATE);
            e[4] = new DiscretePathEffect(23, 18);
            e[5] = new ComposePathEffect(e[2], e[1]);
            e[6] = new SumPathEffect(e[2], e[4]);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            RectF bounds = new RectF();
            mPath.computeBounds(bounds, false);
            canvas.translate(10 - bounds.left, 10 - bounds.top);

            makeEffects(mEffects, mPhase);
            mPhase -= 1;
            invalidate();

            for (int i = 0; i < mEffects.length; i++) {
                mPaint.setPathEffect(mEffects[i]);
                mPaint.setColor(mColors[i]);
                canvas.save();
                if(mEffects[i] == null) {
                    canvas.drawText("No Path Effect", 0, 42, mTextPaint);
                } else {
                    canvas.drawText(mEffects[i].getClass().getName(), 0, 42, mTextPaint);
                }
                canvas.translate(550, 0);
                canvas.drawPath(mPath, mPaint);
                canvas.restore();
                canvas.translate(0, 228);
            }
            //绘制横向箭头动画
            PathEffect arrowPath = new PathDashPathEffect(makeConcaveArrow(80, 100), 80,
                    mPhase, PathDashPathEffect.Style.ROTATE);
            mPaint.setPathEffect(arrowPath);
            canvas.save();
            canvas.drawPath(mArrowPath, mPaint);
            canvas.restore();

            canvas.translate(100, 228);
            //绘制竖向箭头加路径动画
            canvas.save();
            mDragPaint.setPathEffect(createArrowPathEffect(mPathLength, mDragPhase, mArrowLength));
            canvas.drawPath(mDragPath, mDragPaint);

            canvas.translate(200, 0);
            mDragPaint.setPathEffect(createPathEffect(mPathLength, mDragPhase, mArrowLength));
            canvas.drawPath(mDragPath, mDragPaint);

            canvas.translate(200, 0);
            mDragPaint.setPathEffect(createArrowPathEffect(mPathLength, mDragPhase, mArrowLength));
            canvas.drawPath(mDragPath, mDragPaint);
            mDragPaint.setPathEffect(createPathEffect(mPathLength, mDragPhase, mArrowLength));
            canvas.drawPath(mDragPath, mDragPaint);
            canvas.restore();
            mDragPhase -=0.01f;
            Log.i("zx", "mDragPhase = " + mDragPhase);
            if (mDragPhase <= 0) {
                mDragPhase = 1.0f;
                Log.i("zx", "mDragPhase is 0");
            }

        }

        private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
            return new DashPathEffect(new float[] { pathLength, pathLength },
                    Math.max(phase * pathLength, offset));
        }

        private PathEffect createArrowPathEffect(float pathLength, float phase, float offset) {
            return new PathDashPathEffect(makeArrow(mArrowLength, mArrowHeight), pathLength,
                    Math.max(phase * pathLength, offset), PathDashPathEffect.Style.ROTATE);
        }

        private static Path makeDragPath(int radius) {
            Path p = new Path();
            RectF oval = new RectF(0.0f, 0.0f, radius * 2.0f, radius * 2.0f);

            float cx = oval.centerX();
            float cy = oval.centerY();
            float rx = oval.width() / 2.0f;
            float ry = oval.height() / 2.0f;

            final float TAN_PI_OVER_8 = 0.414213562f;
            final float ROOT_2_OVER_2 = 0.707106781f;

            float sx = rx * TAN_PI_OVER_8;
            float sy = ry * TAN_PI_OVER_8;
            float mx = rx * ROOT_2_OVER_2;
            float my = ry * ROOT_2_OVER_2;

            float L = oval.left;
            float T = oval.top;
            float R = oval.right;
            float B = oval.bottom;

            p.moveTo(R, cy);
            p.quadTo(      R, cy + sy, cx + mx, cy + my);
            p.quadTo(cx + sx, B, cx, B);
            p.quadTo(cx - sx,       B, cx - mx, cy + my);
            p.quadTo(L, cy + sy, L, cy);
            p.quadTo(      L, cy - sy, cx - mx, cy - my);
            p.quadTo(cx - sx, T, cx, T);
            p.lineTo(cx, T - oval.height() * 1.3f);

            return p;
        }

        private static Path makeArrow(float length, float height) {
            Path p = new Path();
            p.moveTo(-2.0f, -height / 2.0f);
            p.lineTo(length, 0.0f);
            p.lineTo(-2.0f, height / 2.0f);
            p.close();
            return p;
        }

        private static Path makeFollowPath() {
            Path p = new Path();
            p.moveTo(0, 0);
            for (int i = 1; i <= 15; i++) {
                p.lineTo(i*65, (float)Math.random() * 95);
            }
            return p;
        }

        private static Path makePathDash() {
            Path p = new Path();
            p.moveTo(8, 0);
            p.lineTo(0, -8);
            p.lineTo(16, -8);
            p.lineTo(24, 0);
            p.lineTo(16, 8);
            p.lineTo(0, 8);
            return p;
        }

        private static Path makeConcaveArrow(float length, float height) {
            Path p = new Path();
            p.moveTo(-2.0f, -height / 2.0f);
            p.lineTo(length - height / 4.0f, -height / 2.0f);
            p.lineTo(length, 0.0f);
            p.lineTo(length - height / 4.0f, height / 2.0f);
            p.lineTo(-2.0f, height / 2.0f);
            p.lineTo(-2.0f + height / 4.0f, 0.0f);
            p.close();
            return p;
        }
    }

}

