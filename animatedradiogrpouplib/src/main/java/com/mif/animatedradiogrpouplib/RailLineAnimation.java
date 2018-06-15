package com.mif.animatedradiogrpouplib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by v_alekseev on 16.05.17.
 */

public class RailLineAnimation extends CanvasAnimator {

    private static final int SLIDING_RADIUS_COEFFICIENT = 2;
    private float slidingOvalStartRadius;
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private PointF slidingOvalStart;
    private PointF slidingOvalEnd;

    @Override
    public void init() {
        linePaint.setColor(pathPaint.getColor());
        linePaint.setStyle(pathPaint.getStyle());
        linePaint.setStrokeWidth(3f);
        slidingOvalStartRadius = circleCenterRadius / SLIDING_RADIUS_COEFFICIENT;
        slidingOvalStartRadius = circleCenterRadius / SLIDING_RADIUS_COEFFICIENT;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(ovalActive.x, ovalActive.y, circleCenterRadius, pathPaint);
        if (isAnimating) {
            canvas.drawCircle(slidingOvalStart.x, slidingOvalStart.y, slidingOvalStartRadius, pathPaint);
            canvas.drawLine(ovalActive.x, ovalActive.y, slidingOvalStart.x, slidingOvalStart.y, linePaint);
            canvas.drawCircle(slidingOvalEnd.x, slidingOvalEnd.y, slidingOvalStartRadius, pathPaint);
            canvas.drawLine(ovalActive.x, ovalActive.y, slidingOvalEnd.x, slidingOvalEnd.y, linePaint);
        }
    }

    @Override
    public AnimatorSet getAnimation() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);

        //start oval slide animation
        slidingOvalStart = new PointF(src.x, src.y);
        slidingOvalEnd = new PointF(src.x, src.y);

        ValueAnimator slideOvalStart;
        ValueAnimator slideOvalEnd;
        ValueAnimator slideOvalEndEnd;

        if (parent.getOrientation() == VERTICAL) {
            slideOvalStart = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalStart.setInterpolator(new AccelerateInterpolator());
            slideOvalStart.setDuration(200);
            slideOvalStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slidingOvalStart.y = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });


            //end oval slide animation
            slideOvalEnd = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalEnd.setInterpolator(new AnticipateOvershootInterpolator());
            slideOvalEnd.setDuration(600);
//            slideOvalEnd.setInterpolator(new AccelerateInterpolator());
//            slideOvalEnd.setDuration(400);
            slideOvalEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.y = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });

            slideOvalEndEnd = ValueAnimator.ofFloat(src.y, dst.y);
            slideOvalEndEnd.setInterpolator(new AccelerateInterpolator());
            slideOvalEndEnd.setDuration(200);
            slideOvalEndEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slidingOvalEnd.y = (float) animation.getAnimatedValue();
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
                    parent.invalidate();
                }
            });


            //end oval slide animation
            slideOvalEnd = ValueAnimator.ofFloat(src.x, dst.x);
            slideOvalEnd.setInterpolator(new AnticipateOvershootInterpolator());
            slideOvalEnd.setDuration(600);
            slideOvalEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ovalActive.x = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });

            slideOvalEndEnd = ValueAnimator.ofFloat(src.x, dst.x);
            slideOvalEndEnd.setInterpolator(new AccelerateInterpolator());
            slideOvalEndEnd.setDuration(200);
            slideOvalEndEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slidingOvalEnd.x = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });
        }

        List<Animator> animationList = new ArrayList<>();

        animationList.add(slideOvalStart);
        animationList.add(slideOvalEnd);
        animationList.add(slideOvalEndEnd);

        animatorSet.playSequentially(animationList);

        return animatorSet;
    }
}
