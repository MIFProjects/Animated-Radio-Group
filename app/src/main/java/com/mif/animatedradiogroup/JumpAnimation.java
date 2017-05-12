package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_alekseev on 08.05.17.
 */

class JumpAnimation extends CanvasMainAnimator {

    private float animatedOvalRadius;

    JumpAnimation(CircleItem circleItem) {
        super(circleItem);
        animatedOvalRadius = circleCenterRadius;
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
        ValueAnimator animatedOvalGrowAnimation = ValueAnimator.ofFloat(circleCenterRadius - (circleCenterRadius / 4), circleCenterRadius);
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
        ValueAnimator startDecreaseAnimation = ValueAnimator.ofFloat(circleCenterRadius, 1);
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
        ValueAnimator animatedOvalGrowEndAnimation = ValueAnimator.ofFloat(0, circleCenterRadius);
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
