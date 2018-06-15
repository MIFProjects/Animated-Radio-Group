package com.mif.animatedradiogrpouplib;

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

class FadeAnimation extends CanvasAnimator {

    private final int ALPHA_LEVEL = 255;
    private Paint pathPaintFadeOut;
    private Paint pathPaintFadeIn;
    private int fadeInAlpha = 0;
    private int fadeOutAlpha = ALPHA_LEVEL;

    FadeAnimation() {

    }

    @Override
    public void init() {
        pathPaintFadeOut = pathPaint;
        pathPaintFadeOut.setAlpha(fadeOutAlpha);

        pathPaintFadeIn = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaintFadeIn.setColor(pathPaintFadeOut.getColor());
        pathPaintFadeIn.setStyle(pathPaintFadeOut.getStyle());
        pathPaintFadeIn.setAlpha(fadeInAlpha);

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
                fadeOutAlpha = ALPHA_LEVEL;
                fadeInAlpha = 0;
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                isAnimating = false;
                ovalActive = new PointF(dst.x, dst.y);
                fadeOutAlpha = ALPHA_LEVEL;
                fadeInAlpha = 0;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    @Override
    public void onDraw(Canvas canvas) {

        pathPaintFadeOut.setAlpha(fadeOutAlpha);
        pathPaintFadeIn.setAlpha(fadeInAlpha);
        canvas.drawCircle(ovalActive.x, ovalActive.y, circleCenterRadius, pathPaintFadeOut);

        if (isAnimating) {
            canvas.drawCircle(dst.x, dst.y, circleCenterRadius, pathPaintFadeIn);
        }

    }

    @Override
    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        // hide circle
        ValueAnimator fadeOutAnimation = ValueAnimator.ofInt(ALPHA_LEVEL, 0);
        fadeOutAnimation.setDuration(500);
        fadeOutAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fadeOutAlpha = (int) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        // show circle
        ValueAnimator fadeInAnimation = ValueAnimator.ofInt(0, ALPHA_LEVEL);
        fadeInAnimation.setDuration(500);
        fadeInAnimation.setStartDelay(100);
        fadeInAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fadeInAlpha = (int) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        animatorSet.play(fadeOutAnimation).with(fadeInAnimation);

        return animatorSet;
    }

}
