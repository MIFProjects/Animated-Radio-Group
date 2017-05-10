package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by v_alekseev on 08.05.17.
 */

public class BubbleAnimation implements CanvasAnimator {

    public static final int SLIDING_RADIUS_COEFFICIENT = 2;
    public float slidingOvalStartRadius;

    private Animator.AnimatorListener animatorListener;
    private AnimatedRadioGroup parent;
    private PointF src;
    private PointF dst;
    private Path path;
    private int circleCenterRadius;
    private PointF ovalActive;
    private boolean isAnimating = false;
    private Paint pathPaint;
    private List<PointF> circles;
    private int activeIndex = 0;
    private PointF slidingOvalStart;
    private float ovalActiveRadius;

    public BubbleAnimation(CircleItem circleItem) {

        circleCenterRadius = circleItem.getCenterFillCircleRadius();
        ovalActiveRadius = circleItem.getOutlineCircleRadius();
        pathPaint = circleItem.getCenterFillCirclePaint();
        slidingOvalStartRadius = circleCenterRadius / SLIDING_RADIUS_COEFFICIENT;

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
                ovalActive = new PointF(circles.get(activeIndex).x, circles.get(activeIndex).y);
                Log.d("animationLife", "animation end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animationLife", "animation cancel");
                isAnimating = false;
                ovalActiveRadius = circleCenterRadius;
                ovalActive = new PointF(circles.get(activeIndex).x, circles.get(activeIndex).y);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (!circles.isEmpty()) {
            Log.d("onDrawAnimating", "!circles.isEmpty()");
            canvas.drawCircle(ovalActive.x, ovalActive.y, circleCenterRadius, pathPaint);
        }

        if (isAnimating) {
            Log.d("onDrawAnimating", "slidingOvalStart.x: ");
            canvas.drawCircle(slidingOvalStart.x, slidingOvalStart.y, slidingOvalStartRadius, pathPaint);
            canvas.drawPath(path, pathPaint);
        }
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

    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        //start oval slide animation
        slidingOvalStart = new PointF(src.x, src.y);

        ValueAnimator slideOvalStart;
        ValueAnimator slideOvalEnd;

        if (parent.getOrientation() == VERTICAL) {
            slideOvalStart = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalStart.setInterpolator(new AccelerateInterpolator());
            slideOvalStart.setDuration(200);
            slideOvalStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slidingOvalStart.y = (float) animation.getAnimatedValue();
                    recalculatePath();
                    parent.invalidate();
                }
            });


            //end oval slide animation
            slideOvalEnd = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalEnd.setInterpolator(new AccelerateInterpolator());
            slideOvalEnd.setDuration(200);
            slideOvalEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.y = (float) animation.getAnimatedValue();
                    recalculatePath();
                    parent.invalidate();
                }
            });

        } else {
            slideOvalStart = ValueAnimator.ofFloat(src.x, dst.x);
            slideOvalStart.setInterpolator(new AccelerateInterpolator());
            slideOvalStart.setDuration(200);
            slideOvalStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slidingOvalStart.x = (float) animation.getAnimatedValue();
                    recalculatePath();
                    parent.invalidate();
                }
            });


            //end oval slide animation
            slideOvalEnd = ValueAnimator.ofFloat(src.x, dst.x);
            slideOvalEnd.setInterpolator(new AccelerateInterpolator());
            slideOvalEnd.setDuration(200);
            slideOvalEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.x = (float) animation.getAnimatedValue();
                    recalculatePath();
                    parent.invalidate();
                }
            });
        }

        //start oval growth animation
        ValueAnimator slideOvalStartGrow = ValueAnimator.ofFloat(circleCenterRadius / SLIDING_RADIUS_COEFFICIENT, circleCenterRadius);
        slideOvalStartGrow.setInterpolator(new OvershootInterpolator(6));
        slideOvalStartGrow.setDuration(400);
        slideOvalStartGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                slidingOvalStartRadius = (float) animation.getAnimatedValue();
                recalculatePath();
                parent.invalidate();
            }
        });

        //end oval reduction animation
        ValueAnimator slideOvalEndReduction = ValueAnimator.ofFloat(circleCenterRadius, circleCenterRadius / SLIDING_RADIUS_COEFFICIENT);
        slideOvalEndReduction.setInterpolator(new AccelerateInterpolator());
        slideOvalEndReduction.setDuration(200);
        slideOvalEndReduction.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ovalActiveRadius = (float) animation.getAnimatedValue();
                recalculatePath();
            }
        });

        animatorSet.play(slideOvalStart).before(slideOvalEnd);
        animatorSet.play(slideOvalEnd).with(slideOvalStartGrow).with(slideOvalEndReduction);


        return animatorSet;
    }

    public void recalculatePath() {
        path = new Path();
        if (parent.getOrientation() == VERTICAL) {
            path.moveTo(ovalActive.x - ovalActiveRadius, ovalActive.y);
            path.lineTo(ovalActive.x + ovalActiveRadius, ovalActive.y);
            path.quadTo(slidingOvalStart.x, (slidingOvalStart.y - ovalActive.y) / 2 + ovalActive.y, slidingOvalStart.x + slidingOvalStartRadius, slidingOvalStart.y);
            path.lineTo(slidingOvalStart.x - slidingOvalStartRadius, slidingOvalStart.y);
            path.quadTo(slidingOvalStart.x, (slidingOvalStart.y - ovalActive.y) / 2 + ovalActive.y, ovalActive.x - ovalActiveRadius, ovalActive.y);
        } else {
            path.moveTo(ovalActive.x, ovalActive.y - ovalActiveRadius);
            path.lineTo(ovalActive.x, ovalActive.y + ovalActiveRadius);
            path.quadTo((slidingOvalStart.x - ovalActive.x) / 2 + ovalActive.x, slidingOvalStart.y, slidingOvalStart.x, slidingOvalStart.y + slidingOvalStartRadius);
            path.lineTo(slidingOvalStart.x, slidingOvalStart.y - slidingOvalStartRadius);
            path.quadTo((slidingOvalStart.x - ovalActive.x) / 2 + ovalActive.x, slidingOvalStart.y, ovalActive.x, ovalActive.y - ovalActiveRadius);
        }
    }


}
