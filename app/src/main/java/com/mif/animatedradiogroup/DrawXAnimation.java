package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by v_alekseev on 11.05.17.
 */

class DrawXAnimation extends CanvasAnimator {

    PointF firstPoint;
    PointF secondPoint;
    private float translation;
    private float translation2;

    DrawXAnimation() {

    }

    @Override
    public void init() {

        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("animationLife", "animation start");
                isAnimating = true;
                ovalActive = new PointF(dst.x, dst.y);
                translation2 = translation = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                translation2 = translation = circleCenterRadius;
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                isAnimating = false;
                translation2 = translation = circleCenterRadius;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };

    }

    @Override
    public void onDraw(Canvas canvas) {
        float radius = circleCenterRadius / 2;
//        canvas.drawLine(ovalActive.x - radius, ovalActive.y - radius, ovalActive.x - radius + translation, ovalActive.y - radius + translation, pathPaint);
//        canvas.drawLine(ovalActive.x + (circleCenterRadius), ovalActive.y - (circleCenterRadius), ovalActive.x - translation2, ovalActive.y + translation2, pathPaint);

//        canvas.drawLine(firstPoint.x - (ovalActiveRadius/2), firstPoint.y- (ovalActiveRadius/2), firstPoint.x + translation, firstPoint.y + translation, pathPaint);
//        canvas.drawLine(secondPoint.x + (ovalActiveRadius/2), secondPoint.y - (ovalActiveRadius/2), secondPoint.x - translation2, secondPoint.y + translation2, pathPaint);
        canvas.drawLine(ovalActive.x - radius, ovalActive.y - radius, ovalActive.x - radius + translation, ovalActive.y - radius + translation, pathPaint);
        canvas.drawLine(ovalActive.x + radius, ovalActive.y - radius, ovalActive.x + radius - translation2, ovalActive.y - radius + translation2, pathPaint);

        if (isAnimating) {
//            canvas.drawLine(firstPoint.x, firstPoint.y, firstPoint.x + translation, firstPoint.y + translation, pathPaint);
//            canvas.drawLine(secondPoint.x, secondPoint.y, secondPoint.x - translation2, secondPoint.y + translation2, pathPaint);

            canvas.drawLine(ovalActive.x - radius, ovalActive.y - radius, ovalActive.x - radius + translation, ovalActive.y - radius + translation, pathPaint);
            canvas.drawLine(ovalActive.x + radius, ovalActive.y - radius, ovalActive.x + radius - translation2, ovalActive.y - radius + translation2, pathPaint);
        }

    }

    @Override
    public AnimatorSet getAnimation() {


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        // hide circle
        ValueAnimator drawFirstLineAnimation = ValueAnimator.ofFloat(0, circleCenterRadius);
        drawFirstLineAnimation.setDuration(500);
        drawFirstLineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translation = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        // show circle
        ValueAnimator drawSecondLineAnimation = ValueAnimator.ofFloat(0, circleCenterRadius);
        drawSecondLineAnimation.setDuration(500);
        drawSecondLineAnimation.setStartDelay(100);
        drawSecondLineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translation2 = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        animatorSet.play(drawFirstLineAnimation).before(drawSecondLineAnimation);
//        animatorSet.play(drawFirstLineAnimation);

        return animatorSet;
    }

}
