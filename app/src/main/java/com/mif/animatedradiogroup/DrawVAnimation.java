package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.animation.LinearInterpolator;

/**
 * Created by v_alekseev on 11.05.17.
 */

class DrawVAnimation extends CanvasAnimator {

    private float translationFirstLine;
    private float translationSecondLine;
    private Path path;

    DrawVAnimation() {

    }

    @Override
    public void init() {
        translationFirstLine = (float) circleCenterRadius;
        translationSecondLine = (float) circleCenterRadius * 2;

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
                translationFirstLine = (float) circleCenterRadius;
                translationSecondLine = (float)circleCenterRadius * 2;
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                isAnimating = false;
                translationFirstLine = (float) circleCenterRadius;
                translationSecondLine = (float)circleCenterRadius * 2;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };

    }

    @Override
    public void onDraw(Canvas canvas) {
        float radius = circleCenterRadius / 2;
        canvas.drawLine(ovalActive.x - (circleCenterRadius), ovalActive.y, ovalActive.x - circleCenterRadius + translationFirstLine, ovalActive.y + translationFirstLine, pathPaint);
        canvas.drawLine(ovalActive.x - (pathPaint.getStrokeWidth() / 2), ovalActive.y + circleCenterRadius, ovalActive.x  - radius + translationSecondLine, ovalActive.y + circleCenterRadius - translationSecondLine, pathPaint);

        if (isAnimating) {
//            canvas.drawPath(path, pathPaint);
            canvas.drawLine(ovalActive.x - (circleCenterRadius), ovalActive.y, ovalActive.x - circleCenterRadius + translationFirstLine, ovalActive.y + translationFirstLine, pathPaint);
            canvas.drawLine(ovalActive.x - (pathPaint.getStrokeWidth() / 2), ovalActive.y + circleCenterRadius, ovalActive.x  - radius + translationSecondLine, ovalActive.y + circleCenterRadius - translationSecondLine, pathPaint);
        }

    }

    @Override
    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        // draw first line
        ValueAnimator drawFirstLineAnimation = ValueAnimator.ofFloat(0, circleCenterRadius);
        drawFirstLineAnimation.setDuration(200);
        drawFirstLineAnimation.setInterpolator(new LinearInterpolator());
        drawFirstLineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translationFirstLine = (float) animation.getAnimatedValue();
//                recalculatePath();
                parent.invalidate();
            }
        });

        // draw second line
        ValueAnimator drawSecondLineAnimation = ValueAnimator.ofFloat(0, circleCenterRadius * 2);
        drawSecondLineAnimation.setDuration(300);
        drawSecondLineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translationSecondLine = (float) animation.getAnimatedValue();
//                recalculatePath();
                parent.invalidate();
            }
        });

        animatorSet.play(drawFirstLineAnimation).before(drawSecondLineAnimation);
//        animatorSet.play(drawFirstLineAnimation);

        return animatorSet;
    }

//    private void recalculatePath() {
//        path = new Path();
//
//        path.moveTo(ovalActive.x - ovalActiveRadius, ovalActive.y);
//        path.lineTo(ovalActive.x + ovalActiveRadius, ovalActive.y);
//        path.quadTo(ovalActive.x - (circleCenterRadius), ovalActive.y, ovalActive.x - circleCenterRadius + translationFirstLine, ovalActive.y + translationFirstLine);
//
//    }

}
