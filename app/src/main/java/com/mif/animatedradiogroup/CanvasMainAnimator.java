package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by v_alekseev on 12.05.17.
 */

public class CanvasMainAnimator implements CanvasAnimator {

    protected Animator.AnimatorListener animatorListener;
    protected AnimatedRadioGroup parent;
    protected PointF ovalActive;
    protected PointF src;
    protected PointF dst;
    protected int circleCenterRadius;
    protected Paint pathPaint;
    protected float ovalActiveRadius;
    protected boolean isAnimating = false;

    CanvasMainAnimator(CircleItem circleItem){
        circleCenterRadius = circleItem.getCenterFillCircleRadius();
        pathPaint = circleItem.getCenterFillCirclePaint();
        ovalActiveRadius = circleItem.getOutlineCircleRadius();

        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("animationLife", "animation start");
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                ovalActiveRadius = circleCenterRadius;
                ovalActive = new PointF(dst.x, dst.y);
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                isAnimating = false;
                ovalActiveRadius = circleCenterRadius;
                ovalActive = new PointF(dst.x, dst.y);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public void setParent(AnimatedRadioGroup view) {
        this.parent = view;
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
    public AnimatorSet getAnimation() {
        return null;
    }

    @Override
    public void setOvalActive(PointF ovalActive) {
        this.ovalActive = ovalActive;
    }
}
