package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by v_alekseev on 08.05.17.
 */

public class MagnetAnimation implements CanvasAnimator {

    public static final int SLIDING_RADIUS_COEFFICIENT = 2;
    public float slidingOvalStartRadius;

    private Animator.AnimatorListener animatorListener;
    private AnimatedRadioGroup parent;
    private PointF src;
    private PointF dst;
    private int circleCenterRadius;
    private PointF ovalActive;
    private Paint pathPaint;

    public MagnetAnimation(CircleItem circleItem) {

        circleCenterRadius = circleItem.getCenterFillCircleRadius();
        pathPaint = circleItem.getCenterFillCirclePaint();
        slidingOvalStartRadius = circleCenterRadius / SLIDING_RADIUS_COEFFICIENT;

        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("animationLife", "animation start");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ovalActive = new PointF(dst.x, dst.y);
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                ovalActive = new PointF(dst.x, dst.y);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(ovalActive.x, ovalActive.y, circleCenterRadius, pathPaint);
    }

    @Override
    public void setParent(AnimatedRadioGroup view) {
        parent = view;
    }

    @Override
    public void setDestinationPoint(PointF dst) {
        this.dst = dst;
    }

    @Override
    public void setSourcePoint(PointF src) {
        this.src = src;
    }

    @Override
    public void setOvalActive(PointF ovalActive) {
        this.ovalActive = ovalActive;
    }

    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        ValueAnimator slideOval;

        if (parent.getOrientation() == VERTICAL) {

            //oval slide animation
            slideOval = ValueAnimator.ofFloat(src.y, dst.y);
            slideOval.setInterpolator(new AccelerateInterpolator());
            slideOval.setDuration(200);
            slideOval.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.y = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });

        } else {

            //oval slide animation
            slideOval = ValueAnimator.ofFloat(src.x, dst.x);
            slideOval.setInterpolator(new AccelerateInterpolator());
            slideOval.setDuration(200);
            slideOval.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.x = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });
        }

        animatorSet.play(slideOval);

        return animatorSet;
    }

}
