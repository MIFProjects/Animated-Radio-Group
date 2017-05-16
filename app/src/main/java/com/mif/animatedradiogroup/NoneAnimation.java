package com.mif.animatedradiogroup;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;

/**
 * Created by v_alekseev on 12.05.17.
 */

class NoneAnimation extends CanvasAnimator {


    NoneAnimation() {

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(ovalActive.x, ovalActive.y, circleCenterRadius, pathPaint);
    }

    @Override
    public AnimatorSet getAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        ValueAnimator moveAnimation = ValueAnimator.ofFloat(src.x, dst.x);
        moveAnimation.setDuration(10);
        moveAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ovalActive.x = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });

        animatorSet.play(moveAnimation);

        return animatorSet;
    }

}
