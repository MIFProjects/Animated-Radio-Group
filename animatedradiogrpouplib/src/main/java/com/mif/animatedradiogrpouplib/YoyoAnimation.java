package com.mif.animatedradiogrpouplib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by v_alekseev on 08.05.17.
 */

class YoyoAnimation extends CanvasAnimator {

    private static final int SLIDING_RADIUS_COEFFICIENT = 2;
    private float slidingOvalStartRadius;
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private PointF slidingOvalStart;


    YoyoAnimation() {

    }

    @Override
    public void init() {
        linePaint.setColor(pathPaint.getColor());
        linePaint.setStyle(pathPaint.getStyle());
        linePaint.setStrokeWidth(3f);
        slidingOvalStartRadius = circleCenterRadius / SLIDING_RADIUS_COEFFICIENT;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(ovalActive.x, ovalActive.y, circleCenterRadius, pathPaint);
        if (isAnimating) {
            canvas.drawCircle(slidingOvalStart.x, slidingOvalStart.y, slidingOvalStartRadius, pathPaint);
            canvas.drawLine(ovalActive.x, ovalActive.y, slidingOvalStart.x, slidingOvalStart.y, linePaint);
        }
    }

    @Override
    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        //start oval slide animation
        slidingOvalStart = new PointF(src.x, src.y);

        ValueAnimator slideOvalStart;
        ValueAnimator slideOvalEnd;

        if (parent.getOrientation() == VERTICAL) {
            slideOvalStart = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalStart.setInterpolator(new AccelerateInterpolator());
            slideOvalStart.setDuration(200);
            slideOvalStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slidingOvalStart.y = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });


            //end oval slide animation
            slideOvalEnd = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalEnd.setInterpolator(new OvershootInterpolator(3));
            slideOvalEnd.setDuration(500);
            slideOvalEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.y = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });

        } else {
            slideOvalStart = ValueAnimator.ofFloat(src.x, dst.x);
            slideOvalStart.setInterpolator(new AccelerateInterpolator());
            slideOvalStart.setDuration(200);
            slideOvalStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slidingOvalStart.x = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });


            //end oval slide animation
            slideOvalEnd = ValueAnimator.ofFloat(src.x, dst.x);
            slideOvalEnd.setInterpolator(new OvershootInterpolator(3));
            slideOvalEnd.setDuration(500);
            slideOvalEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.x = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });
        }

        List<Animator> animationList = new ArrayList<>();

        animationList.add(slideOvalStart);
        animationList.add(slideOvalEnd);

        animatorSet.playSequentially(animationList);

        return animatorSet;
    }

}
