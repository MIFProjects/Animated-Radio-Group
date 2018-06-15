package com.mif.animatedradiogrpouplib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by v_alekseev on 08.05.17.
 */

class BubbleAnimation extends CanvasAnimator {

    private static final int SLIDING_RADIUS_COEFFICIENT = 2;
    private float slidingOvalStartRadius;
    private Path path;
    private PointF slidingOvalStart;


    BubbleAnimation() {

    }

    @Override
    public void init() {
        slidingOvalStartRadius = circleCenterRadius / SLIDING_RADIUS_COEFFICIENT;
    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawCircle(ovalActive.x, ovalActive.y, circleCenterRadius, pathPaint);

        if (isAnimating) {
            canvas.drawCircle(slidingOvalStart.x, slidingOvalStart.y, slidingOvalStartRadius, pathPaint);
            canvas.drawPath(path, pathPaint);
        }
    }

    @Override
    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        //start oval slide animation
        slidingOvalStart = new PointF(src.x, src.y);

        ValueAnimator slideOvalStart;
        ValueAnimator slideOvalStart2 = null;
        ValueAnimator slideOvalEnd2 = null;
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

            slideOvalStart2 = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalStart2.setInterpolator(new AccelerateInterpolator());
            slideOvalStart2.setDuration(200);
            slideOvalStart2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slidingOvalStart.y = (float) animation.getAnimatedValue();
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
//            //end oval slide animation
            slideOvalEnd2 = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalEnd2.setInterpolator(new AccelerateInterpolator());
            slideOvalEnd2.setDuration(200);
            slideOvalEnd2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.y = (float) animation.getAnimatedValue();
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
        if (parent.getOrientation() == LinearLayout.HORIZONTAL) {
            animatorSet.play(slideOvalStart2).before(slideOvalEnd);
            animatorSet.play(slideOvalEnd2).with(slideOvalStartGrow).with(slideOvalEndReduction);
        }


        return animatorSet;
    }

    private void recalculatePath() {
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
