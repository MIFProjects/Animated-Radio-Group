package com.mif.animatedradiogrpouplib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by v_alekseev on 11.05.17.
 */

class DrawXAnimation extends CanvasAnimator {

    private float translationFirstLine;
    private float translationSecondLine;

    DrawXAnimation() {

    }

    @Override
    public void init() {
        translationFirstLine = (float) circleCenterRadius;
        translationSecondLine = (float) circleCenterRadius;

        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("animationLife", "animation start");
                isAnimating = true;
                ovalActive = new PointF(dst.x, dst.y);
                translationSecondLine = translationFirstLine = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                translationSecondLine = translationFirstLine = (float)circleCenterRadius;
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                isAnimating = false;
                translationSecondLine = translationFirstLine = (float)circleCenterRadius;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };

    }

    @Override
    public void onDraw(Canvas canvas) {
        float radius = circleCenterRadius / 2;
        canvas.drawLine(ovalActive.x - radius, ovalActive.y - radius, ovalActive.x - radius + translationFirstLine, ovalActive.y - radius + translationFirstLine, pathPaint);
        canvas.drawLine(ovalActive.x + radius, ovalActive.y - radius, ovalActive.x + radius - translationSecondLine, ovalActive.y - radius + translationSecondLine, pathPaint);

        if (isAnimating) {
            canvas.drawLine(ovalActive.x - radius, ovalActive.y - radius, ovalActive.x - radius + translationFirstLine, ovalActive.y - radius + translationFirstLine, pathPaint);
            canvas.drawLine(ovalActive.x + radius, ovalActive.y - radius, ovalActive.x + radius - translationSecondLine, ovalActive.y - radius + translationSecondLine, pathPaint);
        }

    }

    @Override
    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        // draw first line
        ValueAnimator drawFirstLineAnimation = ValueAnimator.ofFloat(0, circleCenterRadius);
        drawFirstLineAnimation.setDuration(300);
        drawFirstLineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translationFirstLine = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        // draw second line
        ValueAnimator drawSecondLineAnimation = ValueAnimator.ofFloat(0, circleCenterRadius);
        drawSecondLineAnimation.setDuration(300);
        drawSecondLineAnimation.setStartDelay(100);
        drawSecondLineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translationSecondLine = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        animatorSet.play(drawFirstLineAnimation).before(drawSecondLineAnimation);

        return animatorSet;
    }

}
