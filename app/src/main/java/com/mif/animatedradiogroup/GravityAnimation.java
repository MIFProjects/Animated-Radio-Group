package com.mif.animatedradiogroup;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.view.animation.AccelerateInterpolator;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by v_alekseev on 08.05.17.
 */

class GravityAnimation extends CanvasAnimator {

    GravityAnimation() {

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(ovalActive.x, ovalActive.y, circleCenterRadius, pathPaint);
    }

    @Override
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
