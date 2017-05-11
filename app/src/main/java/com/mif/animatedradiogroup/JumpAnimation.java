package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_alekseev on 08.05.17.
 */

public class JumpAnimation implements CanvasAnimator {

    private static final int SLIDING_RADIUS_COEFFICIENT = 2;
    private float animatedOvalRadius;

    private Animator.AnimatorListener animatorListener;
    private AnimatedRadioGroup parent;
    private PointF src;
    private PointF dst;
    private int circleCenterFillRadius;
    private PointF ovalActive;
    private boolean isAnimating = false;
    private Paint pathPaint;

    JumpAnimation(CircleItem circleItem) {

        circleCenterFillRadius = circleItem.getCenterFillCircleRadius();
        pathPaint = circleItem.getCenterFillCirclePaint();
        animatedOvalRadius = circleCenterFillRadius;

        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("animationLife", "animation start");
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                ovalActive = new PointF(dst.x, dst.y);
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                isAnimating = false;
                ovalActive = new PointF(dst.x, dst.y);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
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

    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawCircle(ovalActive.x, ovalActive.y, animatedOvalRadius, pathPaint);

        if (isAnimating) {
            canvas.drawCircle(ovalActive.x, ovalActive.y, animatedOvalRadius, pathPaint);
        }
    }

    @Override
    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        // start oval growth animation
        ValueAnimator animatedOvalGrowAnimation = ValueAnimator.ofFloat(circleCenterFillRadius - (circleCenterFillRadius / 4), circleCenterFillRadius);
        animatedOvalGrowAnimation.setInterpolator(new OvershootInterpolator(6));
        animatedOvalGrowAnimation.setDuration(500);
        animatedOvalGrowAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedOvalRadius = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        //start oval decrease animation
        ValueAnimator startDecreaseAnimation = ValueAnimator.ofFloat(circleCenterFillRadius, 1);
        startDecreaseAnimation.setInterpolator(new OvershootInterpolator(1));
        startDecreaseAnimation.setDuration(300);
        startDecreaseAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedOvalRadius = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        // move circle to destination
        ValueAnimator moveToDestinationAnimation = ValueAnimator.ofInt(0, 0);
        moveToDestinationAnimation.setDuration(0);
        moveToDestinationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedOvalRadius = (float) (int) animation.getAnimatedValue();
                setPath();
            }
        });

        //start oval growth animation
        ValueAnimator animatedOvalGrowEndAnimation = ValueAnimator.ofFloat(0, circleCenterFillRadius);
        animatedOvalGrowEndAnimation.setInterpolator(new OvershootInterpolator(4));
        animatedOvalGrowEndAnimation.setDuration(500);
        animatedOvalGrowEndAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedOvalRadius = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });


        List<Animator> animationList = new ArrayList<>();

        animationList.add(animatedOvalGrowAnimation);
        animationList.add(startDecreaseAnimation);
        animationList.add(moveToDestinationAnimation);
        animationList.add(animatedOvalGrowEndAnimation);

        animatorSet.playSequentially(animationList);

        return animatorSet;
    }

    private void setPath() {
        ovalActive.set(dst.x, dst.y);
    }


}
