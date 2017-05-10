package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_alekseev on 08.05.17.
 */

public class FadeAnimation implements CanvasAnimator {

    private static final int SLIDING_RADIUS_COEFFICIENT = 2;
    private float slidingOvalStartRadius;

    private Animator.AnimatorListener animatorListener;
    private AnimatedRadioGroup parent;
    private PointF src;
    private PointF dst;
    private int circleCenterFillRadius;
    private PointF ovalActive;
    private boolean isAnimating = false;
    private Paint pathPaint;
    private List<PointF> circles;
    private int activeIndex = 0;
    private PointF slidingOvalStart;
    private float ovalActiveRadius;

    FadeAnimation(CircleItem circleItem) {

        circleCenterFillRadius = circleItem.getCenterFillCircleRadius();
        ovalActiveRadius = circleItem.getOutlineCircleRadius();
        pathPaint = circleItem.getCenterFillCirclePaint();
        slidingOvalStartRadius = circleCenterFillRadius / SLIDING_RADIUS_COEFFICIENT;

        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("animationLife", "animation start");
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                ovalActiveRadius = circleCenterFillRadius;
                ovalActive = new PointF(circles.get(activeIndex).x, circles.get(activeIndex).y);
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                isAnimating = false;
                ovalActiveRadius = circleCenterFillRadius;
                ovalActive = new PointF(circles.get(activeIndex).x, circles.get(activeIndex).y);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    @Override
    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
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
    public void setCircles(List<PointF> circles) {
        this.circles = circles;
    }


    @Override
    public void onDraw(Canvas canvas) {

        if (!circles.isEmpty()) {
            Log.d("onDrawAnimating", "!circles.isEmpty() ovalActive.x: " + ovalActive.x + "ovalActive.y" + ovalActive.y + " circleCenterFillRadius: " + circleCenterFillRadius);
            canvas.drawCircle(ovalActive.x, ovalActive.y, slidingOvalStartRadius, pathPaint);
        }

        if (isAnimating) {
            Log.d("onDrawAnimating", "slidingOvalStart.x: " + slidingOvalStart.x + " slidingOvalStart.y: " + slidingOvalStart.y + " slidingOvalStartRadius: " + slidingOvalStartRadius);
            canvas.drawCircle(ovalActive.x, ovalActive.y, slidingOvalStartRadius, pathPaint);
//            canvas.drawPath(path, pathPaint);
        }
    }

    @Override
    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        //start oval slide animation
        slidingOvalStart = new PointF(src.x, src.y);


        //start oval growth animation
        ValueAnimator slideOvalStartGrow = ValueAnimator.ofFloat(circleCenterFillRadius / SLIDING_RADIUS_COEFFICIENT, circleCenterFillRadius);
        slideOvalStartGrow.setInterpolator(new OvershootInterpolator(6));
        slideOvalStartGrow.setDuration(300);
        slideOvalStartGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("ValueAnimator", "slideOvalStartGrow");
                slidingOvalStartRadius = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });


        // start fade-out animation
        ValueAnimator goneCircleAnimation = ValueAnimator.ofInt((int)slidingOvalStartRadius, 0);
        goneCircleAnimation.setInterpolator(new LinearInterpolator());
        goneCircleAnimation.setDuration(250);
        goneCircleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("ValueAnimator", "goneCircleAnimation");
                slidingOvalStartRadius = (int) animation.getAnimatedValue();
//                circleCenterFillRadius = ((int) animation.getAnimatedValue());
                parent.invalidate();
            }
        });

        // move circle to destination
        ValueAnimator moveToDestinationAnimation = ValueAnimator.ofInt(0, 0);
//        goneCircleAnimation.setInterpolator(new LinearInterpolator());
        moveToDestinationAnimation.setDuration(0);
        moveToDestinationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("ValueAnimator", "moveToDestinationAnimation");
                slidingOvalStartRadius = (float) (int) animation.getAnimatedValue();
                setPath();
            }
        });

        //start fade-in animation
        ValueAnimator showCircleAnimation = ValueAnimator.ofInt(0, circleCenterFillRadius);
        showCircleAnimation.setInterpolator(new LinearInterpolator());
        showCircleAnimation.setDuration(250);
        showCircleAnimation.setStartDelay(100);
        showCircleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("ValueAnimator", "showCircleAnimation");
                slidingOvalStartRadius = (int) animation.getAnimatedValue();
//                circleCenterFillRadius = ((int) animation.getAnimatedValue());
                parent.invalidate();
            }
        });

        //start oval growth animation
        ValueAnimator slideOvalEndGrow = ValueAnimator.ofFloat(circleCenterFillRadius / SLIDING_RADIUS_COEFFICIENT, circleCenterFillRadius);
        slideOvalEndGrow.setInterpolator(new OvershootInterpolator(6));
        slideOvalEndGrow.setDuration(300);
        slideOvalEndGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("ValueAnimator", "slideOvalStartGrow");
                slidingOvalStartRadius = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });


        List<Animator> animationList = new ArrayList<>();

        animationList.add(slideOvalStartGrow);
        animationList.add(goneCircleAnimation);
        animationList.add(moveToDestinationAnimation);
        animationList.add(showCircleAnimation);
        animationList.add(slideOvalEndGrow);

        animatorSet.playSequentially(animationList);

        return animatorSet;
    }

    private void setPath() {
//        if (listener != null) {

//        path = new Path();
//        path.moveTo(dst.x, dst.y);
//            listener.updatePath(path);
        ovalActive.set(dst.x, dst.y);
//        }
    }


}
