package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuriy Diachenko on 23.03.2017.
 */

public class AnimatedRadioGroup extends LinearLayout {

    private static final int SLIDING_RADIUS_COEFFICIENT = 2;
    private static final int RADIUS = 20;
    private static final int CIRCLE_PADDING_RIGHT = 50;
    private static final int CIRCLE_PADDING_LEFT = 5;
    private static final int CIRCLE_PADDING_TOP = 20;

    public static final int STROKE_WIDTH = 2;
    public static final int CIRCLE_COLOR = Color.BLUE;

    private Paint pathPaint;
    private Paint inactivePaint;
    List<PointF> circles = new ArrayList<>();
    private PointF slidingOvalStart;
    private PointF ovalActive;
    private Path path;
    private float radius = RADIUS;
    private float slidingOvalStartRadius = RADIUS / SLIDING_RADIUS_COEFFICIENT;
    private float ovalActiveRadius = radius;
    private boolean isAnimating = false;
    private AnimatorSet animatorSet;
    private int activeIndex = 0;

    private float lastMotionX;
    private float lastMotionY;
    //Linear Layout related
    private int mTotalLength;
    private int mBaselineChildTop;
    private boolean mAllowInconsistentMeasurement;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_TOP = 1;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_FILL = 3;
    private int mGravity = Gravity.START | Gravity.TOP;


    public AnimatedRadioGroup(Context context) {
        super(context);
        init();
    }

    public AnimatedRadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedRadioGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        final int version = getContext().getApplicationInfo().targetSdkVersion;
        mAllowInconsistentMeasurement = version <= Build.VERSION_CODES.M;

        setWillNotDraw(false);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(CIRCLE_COLOR);
        pathPaint.setStyle(Paint.Style.FILL);

        inactivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        inactivePaint.setColor(CIRCLE_COLOR);
        inactivePaint.setStyle(Paint.Style.STROKE);
        inactivePaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (PointF circle : circles) {
            canvas.drawCircle(circle.x, circle.y, RADIUS, inactivePaint);
        }

        if (!circles.isEmpty()) {
            canvas.drawCircle(ovalActive.x, ovalActive.y, ovalActiveRadius, pathPaint);
        }

        if (isAnimating) {
            canvas.drawCircle(slidingOvalStart.x, slidingOvalStart.y, slidingOvalStartRadius, pathPaint);
            canvas.drawPath(path, pathPaint);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                return isCirclePressed(ev);
            case MotionEvent.ACTION_CANCEL:
                lastMotionX = -1;
                lastMotionY = -1;
                break;

        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isCirclePressed(event)) {
                    int activePointerId = event.getPointerId(0);
                    final int activePointerIndex = event.findPointerIndex(activePointerId);
                    lastMotionX = event.getX(activePointerIndex);
                    lastMotionY = event.getY(activePointerIndex);

                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if ((lastMotionY > -1 && lastMotionX > -1) && isCirclePressed(event)) {
                    int clickedCircleIndex = getClickedCircle(event);

                    Log.d(getClass().getSimpleName(), "Circle clicked: " + clickedCircleIndex);
                    if (clickedCircleIndex > -1) {
                        PointF clickedCircle = circles.get(clickedCircleIndex);
                        Log.d(getClass().getSimpleName(), clickedCircle.toString());


                        setSelection(clickedCircleIndex);
                    }
                    lastMotionX = -1;
                    lastMotionY = -1;

                    return true;
                }
                break;
        }
        return false;
    }

    private void setSelection(int clickedCircleIndex) {

        resetAnimation();
        PointF dst = circles.get(clickedCircleIndex);
        PointF src = circles.get(activeIndex);
        activeIndex = clickedCircleIndex;
        animateSliding(src, dst);
    }

    private void resetAnimation() {
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.cancel();
        }
    }

    private boolean isCirclePressed(MotionEvent ev) {
        int activePointerId = ev.getPointerId(0);
        final int activePointerIndex = ev.findPointerIndex(activePointerId);
        float lastMotionX = ev.getX(activePointerIndex);
        float lastMotionY = ev.getY(activePointerIndex);

        for (PointF circle : circles) {
            if (new RectF(circle.x - RADIUS, circle.y - RADIUS, circle.x + RADIUS, circle.y + RADIUS).contains(lastMotionX, lastMotionY)) {
                return true;
            }
        }
        return false;
    }

    private int getClickedCircle(MotionEvent ev) {
        int activePointerId = ev.getPointerId(0);
        final int activePointerIndex = ev.findPointerIndex(activePointerId);
        float lastMotionX = ev.getX(activePointerIndex);
        float lastMotionY = ev.getY(activePointerIndex);

        for (int i = 0; i < circles.size(); i++) {
            PointF circle = circles.get(i);
            if (new RectF(circle.x - RADIUS, circle.y - RADIUS, circle.x + RADIUS, circle.y + RADIUS).contains(lastMotionX, lastMotionY)) {
                return i;
            }
        }

        return -1;
    }

    private void animateSliding(PointF src, PointF dst) {
        animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                ovalActiveRadius = radius;
                ovalActive = new PointF(circles.get(activeIndex).x, circles.get(activeIndex).y);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimating = false;
                ovalActiveRadius = radius;
                ovalActive = new PointF(circles.get(activeIndex).x, circles.get(activeIndex).y);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        //start oval slide animation
        slidingOvalStart = new PointF(src.x, src.y);

        ValueAnimator slideOvalStartY = ValueAnimator.ofFloat(src.y, dst.y);
        slideOvalStartY.setInterpolator(new AccelerateInterpolator());
        slideOvalStartY.setDuration(200);
        slideOvalStartY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                slidingOvalStart.y = (float) animation.getAnimatedValue();
                recalculatePath();
                invalidate();
            }
        });


        //end oval slide animation
        ValueAnimator slideOvalEndY = ValueAnimator.ofFloat(src.y, dst.y);
        slideOvalEndY.setInterpolator(new AccelerateInterpolator());
        slideOvalEndY.setDuration(200);
        slideOvalEndY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ovalActive.y = (float) animation.getAnimatedValue();
                recalculatePath();
                invalidate();
            }
        });

        //start oval growth animation
        ValueAnimator slideOvalStartGrow = ValueAnimator.ofFloat(RADIUS / SLIDING_RADIUS_COEFFICIENT, radius);
        slideOvalStartGrow.setInterpolator(new OvershootInterpolator(6));
        slideOvalStartGrow.setDuration(400);
        slideOvalStartGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                slidingOvalStartRadius = (float) animation.getAnimatedValue();
                recalculatePath();
                invalidate();
            }
        });

        //end oval reduction animation
        ValueAnimator slideOvalEndReduction = ValueAnimator.ofFloat(radius, RADIUS / SLIDING_RADIUS_COEFFICIENT);
        slideOvalEndReduction.setInterpolator(new AccelerateInterpolator());
        slideOvalEndReduction.setDuration(200);
        slideOvalEndReduction.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ovalActiveRadius = (float) animation.getAnimatedValue();
                recalculatePath();
            }
        });


        animatorSet.play(slideOvalStartY).before(slideOvalEndY);
        animatorSet.play(slideOvalEndY).with(slideOvalStartGrow).with(slideOvalEndReduction);

        slidingOvalStartRadius = RADIUS / SLIDING_RADIUS_COEFFICIENT;
        recalculatePath();

        animatorSet.start();
    }

    private void recalculatePath() {
        path = new Path();

        path.moveTo(ovalActive.x - ovalActiveRadius, ovalActive.y);
        path.lineTo(ovalActive.x + ovalActiveRadius, ovalActive.y);
        path.quadTo(slidingOvalStart.x, (slidingOvalStart.y - ovalActive.y) / 2 + ovalActive.y, slidingOvalStart.x + slidingOvalStartRadius, slidingOvalStart.y);
        path.lineTo(slidingOvalStart.x - slidingOvalStartRadius, slidingOvalStart.y);
        path.quadTo(slidingOvalStart.x, (slidingOvalStart.y - ovalActive.y) / 2 + ovalActive.y, ovalActive.x - ovalActiveRadius, ovalActive.y);
    }


    //Linear Layout measurements
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getOrientation() == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Measures the children when the orientation of this LinearLayout is set
     * to {@link #VERTICAL}.
     *
     * @param widthMeasureSpec  Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent.
     * @see #getOrientation()
     * @see #setOrientation(int)
     * @see #onMeasure(int, int)
     */
    void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        mTotalLength = 0;
        int maxWidth = 0;
        int childState = 0;
        int alternativeMaxWidth = 0;
        int weightedMaxWidth = 0;
        boolean allFillParent = true;
        float totalWeight = 0;

        final int count = getChildCount();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        boolean matchWidth = false;
        boolean skippedMeasure = false;

        final int baselineChildIndex = getBaselineAlignedChildIndex();

        int largestChildHeight = Integer.MIN_VALUE;
        int consumedExcessSpace = 0;

        // See how tall everyone is. Also remember max width.
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child == null) {
                continue;
            }

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            totalWeight += lp.weight;

            final boolean useExcessSpace = lp.height == 0 && lp.weight > 0;
            if (heightMode == MeasureSpec.EXACTLY && useExcessSpace) {
                // Optimization: don't bother measuring children who are only
                // laid out using excess space. These views will get measured
                // later if we have space to distribute.
                final int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + lp.topMargin + lp.bottomMargin);
                skippedMeasure = true;
            } else {
                if (useExcessSpace) {
                    // The heightMode is either UNSPECIFIED or AT_MOST, and
                    // this child is only laid out using excess space. Measure
                    // using WRAP_CONTENT so that we can find out the view's
                    // optimal height. We'll restore the original height of 0
                    // after measurement.
                    lp.height = LayoutParams.WRAP_CONTENT;
                }

                // Determine how big this child would like to be. If this or
                // previous children have given a weight, then we allow it to
                // use all available space (and we will shrink things later
                // if needed).
                final int usedHeight = totalWeight == 0 ? mTotalLength : 0;
                measureChildBeforeLayout(child, i, widthMeasureSpec, 0,
                        heightMeasureSpec, usedHeight);

                final int childHeight = child.getMeasuredHeight();
                if (useExcessSpace) {
                    // Restore the original height and record how much space
                    // we've allocated to excess-only children so that we can
                    // match the behavior of EXACTLY measurement.
                    lp.height = 0;
                    consumedExcessSpace += childHeight;
                }

                final int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + childHeight + lp.topMargin +
                        lp.bottomMargin);

            }

            /**
             * If applicable, compute the additional offset to the child's baseline
             * we'll need later when asked {@link #getBaseline}.
             */
            if ((baselineChildIndex >= 0) && (baselineChildIndex == i + 1)) {
                mBaselineChildTop = mTotalLength;
            }

            // if we are trying to use a child index for our baseline, the above
            // book keeping only works if there are no children above it with
            // weight.  fail fast to aid the developer.
            if (i < baselineChildIndex && lp.weight > 0) {
                throw new RuntimeException("A child of LinearLayout with index "
                        + "less than mBaselineAlignedChildIndex has weight > 0, which "
                        + "won't work.  Either remove the weight, or don't set "
                        + "mBaselineAlignedChildIndex.");
            }

            boolean matchWidthLocally = false;
            if (widthMode != MeasureSpec.EXACTLY && lp.width == LayoutParams.MATCH_PARENT) {
                // The width of the linear layout will scale, and at least one
                // child said it wanted to match our width. Set a flag
                // indicating that we need to remeasure at least that view when
                // we know our width.
                matchWidth = true;
                matchWidthLocally = true;
            }


            final int margin = lp.leftMargin + lp.rightMargin;
            final int measuredWidth = child.getMeasuredWidth() + margin;
            maxWidth = Math.max(maxWidth, measuredWidth);
            childState = combineMeasuredStates(childState, child.getMeasuredState());

            allFillParent = allFillParent && lp.width == LayoutParams.MATCH_PARENT;
            if (lp.weight > 0) {
                /*
                 * Widths of weighted Views are bogus if we end up
                 * remeasuring, so keep them separate.
                 */
                weightedMaxWidth = Math.max(weightedMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
            } else {
                alternativeMaxWidth = Math.max(alternativeMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
            }
        }

        // Add in our padding
        mTotalLength += getPaddingTop() + getPaddingBottom();

        int heightSize = mTotalLength;

        // Check against our minimum height
        heightSize = Math.max(heightSize, getSuggestedMinimumHeight());

        // Reconcile our calculated size with the heightMeasureSpec
        int heightSizeAndState = resolveSizeAndState(heightSize, heightMeasureSpec, 0);
        heightSize = heightSizeAndState & MEASURED_SIZE_MASK;

        // Either expand children with weight to take up available space or
        // shrink them if they extend beyond our current bounds. If we skipped
        // measurement on any children, we need to measure them now.
        int remainingExcess = heightSize - mTotalLength
                + (mAllowInconsistentMeasurement ? 0 : consumedExcessSpace);
        if (skippedMeasure || remainingExcess != 0 && totalWeight > 0.0f) {
            float remainingWeightSum = getWeightSum() > 0.0f ? getWeightSum() : totalWeight;

            mTotalLength = 0;

            for (int i = 0; i < count; ++i) {
                final View child = getChildAt(i);
                if (child == null || child.getVisibility() == View.GONE) {
                    continue;
                }

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final float childWeight = lp.weight;
                if (childWeight > 0) {
                    final int share = (int) (childWeight * remainingExcess / remainingWeightSum);
                    remainingExcess -= share;
                    remainingWeightSum -= childWeight;

                    final int childHeight;
                    if (lp.height == 0 && (!mAllowInconsistentMeasurement
                            || heightMode == MeasureSpec.EXACTLY)) {
                        // This child needs to be laid out from scratch using
                        // only its share of excess space.
                        childHeight = share;
                    } else {
                        // This child had some intrinsic height to which we
                        // need to add its share of excess space.
                        childHeight = child.getMeasuredHeight() + share;
                    }

                    final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            Math.max(0, childHeight), MeasureSpec.EXACTLY);

                    int circleArea = RADIUS * 2 + CIRCLE_PADDING_RIGHT + CIRCLE_PADDING_LEFT + STROKE_WIDTH;

                    final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + circleArea,
                            lp.width);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    // Child may now not fit in vertical dimension.
                    childState = combineMeasuredStates(childState, child.getMeasuredState()
                            & (MEASURED_STATE_MASK >> MEASURED_HEIGHT_STATE_SHIFT));
                }

                final int margin = lp.leftMargin + lp.rightMargin;
                final int measuredWidth = child.getMeasuredWidth() + margin;
                maxWidth = Math.max(maxWidth, measuredWidth);

                boolean matchWidthLocally = widthMode != MeasureSpec.EXACTLY &&
                        lp.width == LayoutParams.MATCH_PARENT;

                alternativeMaxWidth = Math.max(alternativeMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);

                allFillParent = allFillParent && lp.width == LayoutParams.MATCH_PARENT;

                final int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + child.getMeasuredHeight() +
                        lp.topMargin + lp.bottomMargin);
            }

            // Add in our padding
            mTotalLength += getPaddingTop() + getPaddingBottom();
            // TODO: Should we recompute the heightSpec based on the new total length?
        } else {
            alternativeMaxWidth = Math.max(alternativeMaxWidth,
                    weightedMaxWidth);
        }

        if (!allFillParent && widthMode != MeasureSpec.EXACTLY) {
            maxWidth = alternativeMaxWidth;
        }

        maxWidth += getPaddingLeft() + getPaddingRight();

        // Check against our minimum width
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                heightSizeAndState);

        if (matchWidth) {
            forceUniformWidth(count, heightMeasureSpec);
        }
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        // Pretend that the linear layout has an exact size.
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                MeasureSpec.EXACTLY);
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                LinearLayout.LayoutParams lp = ((LinearLayout.LayoutParams) child.getLayoutParams());

                if (lp.width == LayoutParams.MATCH_PARENT) {
                    // Temporarily force children to reuse their old measured height
                    // FIXME: this may not be right for something like wrapping text?
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();

                    // Remeasue with new dimensions
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    /**
     * Measures the children when the orientation of this LinearLayout is set
     * to {@link #HORIZONTAL}.
     *
     * @param widthMeasureSpec  Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent.
     * @see #getOrientation()
     * @see #setOrientation(int)
     * @see #onMeasure(int, int)
     */
    void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        mTotalLength = 0;
        int maxHeight = 0;
        int childState = 0;
        int alternativeMaxHeight = 0;
        int weightedMaxHeight = 0;
        boolean allFillParent = true;
        float totalWeight = 0;

        final int count = getChildCount();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        boolean matchHeight = false;
        boolean skippedMeasure = false;

        if (mMaxAscent == null || mMaxDescent == null) {
            mMaxAscent = new int[VERTICAL_GRAVITY_COUNT];
            mMaxDescent = new int[VERTICAL_GRAVITY_COUNT];
        }

        final int[] maxAscent = mMaxAscent;
        final int[] maxDescent = mMaxDescent;

        maxAscent[0] = maxAscent[1] = maxAscent[2] = maxAscent[3] = -1;
        maxDescent[0] = maxDescent[1] = maxDescent[2] = maxDescent[3] = -1;

        final boolean isExactly = widthMode == MeasureSpec.EXACTLY;

        int largestChildWidth = Integer.MIN_VALUE;
        int usedExcessSpace = 0;

        // See how wide everyone is. Also remember max height.
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child == null) {
                continue;
            }

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            totalWeight += lp.weight;

            final boolean useExcessSpace = lp.width == 0 && lp.weight > 0;
            if (widthMode == MeasureSpec.EXACTLY && useExcessSpace) {
                // Optimization: don't bother measuring children who are only
                // laid out using excess space. These views will get measured
                // later if we have space to distribute.
                if (isExactly) {
                    mTotalLength += lp.leftMargin + lp.rightMargin;
                } else {
                    final int totalLength = mTotalLength;
                    mTotalLength = Math.max(totalLength, totalLength +
                            lp.leftMargin + lp.rightMargin);
                }

                // Baseline alignment requires to measure widgets to obtain the
                // baseline offset (in particular for TextViews). The following
                // defeats the optimization mentioned above. Allow the child to
                // use as much space as it wants because we can shrink things
                // later (and re-measure).
                skippedMeasure = true;
            } else {
                if (useExcessSpace) {
                    // The widthMode is either UNSPECIFIED or AT_MOST, and
                    // this child is only laid out using excess space. Measure
                    // using WRAP_CONTENT so that we can find out the view's
                    // optimal width. We'll restore the original width of 0
                    // after measurement.
                    lp.width = LayoutParams.WRAP_CONTENT;
                }

                // Determine how big this child would like to be. If this or
                // previous children have given a weight, then we allow it to
                // use all available space (and we will shrink things later
                // if needed).
                final int usedWidth = totalWeight == 0 ? mTotalLength : 0;
                measureChildBeforeLayout(child, i, widthMeasureSpec, usedWidth,
                        heightMeasureSpec, 0);

                final int childWidth = child.getMeasuredWidth();
                if (useExcessSpace) {
                    // Restore the original width and record how much space
                    // we've allocated to excess-only children so that we can
                    // match the behavior of EXACTLY measurement.
                    lp.width = 0;
                    usedExcessSpace += childWidth;
                }

                if (isExactly) {
                    mTotalLength += childWidth + lp.leftMargin + lp.rightMargin;
                } else {
                    final int totalLength = mTotalLength;
                    mTotalLength = Math.max(totalLength, totalLength + childWidth + lp.leftMargin
                            + lp.rightMargin);
                }
            }

            boolean matchHeightLocally = false;
            if (heightMode != MeasureSpec.EXACTLY && lp.height == LayoutParams.MATCH_PARENT) {
                // The height of the linear layout will scale, and at least one
                // child said it wanted to match our height. Set a flag indicating that
                // we need to remeasure at least that view when we know our height.
                matchHeight = true;
                matchHeightLocally = true;
            }

            final int margin = lp.topMargin + lp.bottomMargin;
            final int childHeight = child.getMeasuredHeight() + margin;
            childState = combineMeasuredStates(childState, child.getMeasuredState());

            maxHeight = Math.max(maxHeight, childHeight);

            allFillParent = allFillParent && lp.height == LayoutParams.MATCH_PARENT;
            if (lp.weight > 0) {
                /*
                 * Heights of weighted Views are bogus if we end up
                 * remeasuring, so keep them separate.
                 */
                weightedMaxHeight = Math.max(weightedMaxHeight,
                        matchHeightLocally ? margin : childHeight);
            } else {
                alternativeMaxHeight = Math.max(alternativeMaxHeight,
                        matchHeightLocally ? margin : childHeight);
            }

        }

        // Check mMaxAscent[INDEX_TOP] first because it maps to Gravity.TOP,
        // the most common case
        if (maxAscent[INDEX_TOP] != -1 ||
                maxAscent[INDEX_CENTER_VERTICAL] != -1 ||
                maxAscent[INDEX_BOTTOM] != -1 ||
                maxAscent[INDEX_FILL] != -1) {
            final int ascent = Math.max(maxAscent[INDEX_FILL],
                    Math.max(maxAscent[INDEX_CENTER_VERTICAL],
                            Math.max(maxAscent[INDEX_TOP], maxAscent[INDEX_BOTTOM])));
            final int descent = Math.max(maxDescent[INDEX_FILL],
                    Math.max(maxDescent[INDEX_CENTER_VERTICAL],
                            Math.max(maxDescent[INDEX_TOP], maxDescent[INDEX_BOTTOM])));
            maxHeight = Math.max(maxHeight, ascent + descent);
        }

        // Add in our padding
        mTotalLength += getPaddingLeft() + getPaddingRight();

        int widthSize = mTotalLength;

        // Check against our minimum width
        widthSize = Math.max(widthSize, getSuggestedMinimumWidth());

        // Reconcile our calculated size with the widthMeasureSpec
        int widthSizeAndState = resolveSizeAndState(widthSize, widthMeasureSpec, 0);
        widthSize = widthSizeAndState & MEASURED_SIZE_MASK;

        // Either expand children with weight to take up available space or
        // shrink them if they extend beyond our current bounds. If we skipped
        // measurement on any children, we need to measure them now.
        int remainingExcess = widthSize - mTotalLength
                + (mAllowInconsistentMeasurement ? 0 : usedExcessSpace);
        if (skippedMeasure || remainingExcess != 0 && totalWeight > 0.0f) {
            float remainingWeightSum = getWeightSum() > 0.0f ? getWeightSum() : totalWeight;

            maxAscent[0] = maxAscent[1] = maxAscent[2] = maxAscent[3] = -1;
            maxDescent[0] = maxDescent[1] = maxDescent[2] = maxDescent[3] = -1;
            maxHeight = -1;

            mTotalLength = 0;

            for (int i = 0; i < count; ++i) {
                final View child = getChildAt(i);
                if (child == null || child.getVisibility() == View.GONE) {
                    continue;
                }

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final float childWeight = lp.weight;
                if (childWeight > 0) {
                    final int share = (int) (childWeight * remainingExcess / remainingWeightSum);
                    remainingExcess -= share;
                    remainingWeightSum -= childWeight;

                    final int childWidth;
                    if (lp.width == 0 && (!mAllowInconsistentMeasurement
                            || widthMode == MeasureSpec.EXACTLY)) {
                        // This child needs to be laid out from scratch using
                        // only its share of excess space.
                        childWidth = share;
                    } else {
                        // This child had some intrinsic width to which we
                        // need to add its share of excess space.
                        childWidth = child.getMeasuredWidth() + share;
                    }

                    final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            Math.max(0, childWidth), MeasureSpec.EXACTLY);
                    final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                            lp.height);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    // Child may now not fit in horizontal dimension.
                    childState = combineMeasuredStates(childState,
                            child.getMeasuredState() & MEASURED_STATE_MASK);
                }

                if (isExactly) {
                    mTotalLength += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                } else {
                    final int totalLength = mTotalLength;
                    mTotalLength = Math.max(totalLength, totalLength + child.getMeasuredWidth() +
                            lp.leftMargin + lp.rightMargin);
                }

                boolean matchHeightLocally = heightMode != MeasureSpec.EXACTLY &&
                        lp.height == LayoutParams.MATCH_PARENT;

                final int margin = lp.topMargin + lp.bottomMargin;
                int childHeight = child.getMeasuredHeight() + margin;
                maxHeight = Math.max(maxHeight, childHeight);
                alternativeMaxHeight = Math.max(alternativeMaxHeight,
                        matchHeightLocally ? margin : childHeight);

                allFillParent = allFillParent && lp.height == LayoutParams.MATCH_PARENT;

            }

            // Add in our padding
            mTotalLength += getPaddingLeft() + getPaddingRight();
            // TODO: Should we update widthSize with the new total length?

            // Check mMaxAscent[INDEX_TOP] first because it maps to Gravity.TOP,
            // the most common case
            if (maxAscent[INDEX_TOP] != -1 ||
                    maxAscent[INDEX_CENTER_VERTICAL] != -1 ||
                    maxAscent[INDEX_BOTTOM] != -1 ||
                    maxAscent[INDEX_FILL] != -1) {
                final int ascent = Math.max(maxAscent[INDEX_FILL],
                        Math.max(maxAscent[INDEX_CENTER_VERTICAL],
                                Math.max(maxAscent[INDEX_TOP], maxAscent[INDEX_BOTTOM])));
                final int descent = Math.max(maxDescent[INDEX_FILL],
                        Math.max(maxDescent[INDEX_CENTER_VERTICAL],
                                Math.max(maxDescent[INDEX_TOP], maxDescent[INDEX_BOTTOM])));
                maxHeight = Math.max(maxHeight, ascent + descent);
            }
        } else {
            alternativeMaxHeight = Math.max(alternativeMaxHeight, weightedMaxHeight);
        }

        if (!allFillParent && heightMode != MeasureSpec.EXACTLY) {
            maxHeight = alternativeMaxHeight;
        }

        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum height
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

        setMeasuredDimension(widthSizeAndState | (childState & MEASURED_STATE_MASK),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        (childState << MEASURED_HEIGHT_STATE_SHIFT)));

        if (matchHeight) {
            forceUniformHeight(count, widthMeasureSpec);
        }
    }

    private void forceUniformHeight(int count, int widthMeasureSpec) {
        // Pretend that the linear layout has an exact size. This is the measured height of
        // ourselves. The measured height should be the max height of the children, changed
        // to accommodate the heightMeasureSpec from the parent
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
                MeasureSpec.EXACTLY);
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();

                if (lp.height == LayoutParams.MATCH_PARENT) {
                    // Temporarily force children to reuse their old measured width
                    // FIXME: this may not be right for something like wrapping text?
                    int oldWidth = lp.width;
                    lp.width = child.getMeasuredWidth();

                    // Remeasure with new dimensions
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    /**
     * <p>Measure the child according to the parent's measure specs. This
     * method should be overriden by subclasses to force the sizing of
     * children. This method is called by {@link #measureVertical(int, int)} and
     * {@link #measureHorizontal(int, int)}.</p>
     *
     * @param child             the child to measure
     * @param childIndex        the index of the child in this view
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent
     * @param totalWidth        extra space that has been used up by the parent horizontally
     * @param heightMeasureSpec vertical space requirements as imposed by the parent
     * @param totalHeight       extra space that has been used up by the parent vertically
     */
    void measureChildBeforeLayout(View child, int childIndex,
                                  int widthMeasureSpec, int totalWidth, int heightMeasureSpec,
                                  int totalHeight) {
        measureChildWithMargins(child, widthMeasureSpec, totalWidth,
                heightMeasureSpec, totalHeight);
    }

    //Linear Layout onLayout part
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //clear circles before refill
        circles.clear();

        if (getOrientation() == VERTICAL) {
            layoutVertical(l, t, r, b);
        } else {
            layoutHorizontal(l, t, r, b);
        }

        ovalActive = new PointF(circles.get(activeIndex).x, circles.get(activeIndex).y);
    }

    /**
     * Position the children during a layout pass if the orientation of this
     * LinearLayout is set to {@link #VERTICAL}.
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @see #getOrientation()
     * @see #setOrientation(int)
     * @see #onLayout(boolean, int, int, int, int)
     */
    void layoutVertical(int left, int top, int right, int bottom) {
        final int paddingLeft = getPaddingLeft();

        int childTop;
        int childLeft;

        // Where right end of child should go
        final int width = right - left;
        int childRight = width - getPaddingRight();

        // Space available for child
        int childSpace = width - paddingLeft - getPaddingRight();

        final int count = getChildCount();

        final int majorGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
        final int minorGravity = mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

        switch (majorGravity) {
            case Gravity.BOTTOM:
                // mTotalLength contains the padding already
                childTop = getPaddingTop() + bottom - top - mTotalLength;
                break;

            // mTotalLength contains the padding already
            case Gravity.CENTER_VERTICAL:
                childTop = getPaddingTop() + (bottom - top - mTotalLength) / 2;
                break;

            case Gravity.TOP:
            default:
                childTop = getPaddingTop();
                break;
        }

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                final LinearLayout.LayoutParams lp =
                        (LinearLayout.LayoutParams) child.getLayoutParams();

                int gravity = lp.gravity;
                if (gravity < 0) {
                    gravity = minorGravity;
                }
                final int layoutDirection = getLayoutDirection();
                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        //todo: add circle paddings
                        childLeft = paddingLeft + ((childSpace - childWidth) / 2)
                                + lp.leftMargin - lp.rightMargin;
                        break;

                    case Gravity.RIGHT:
                        //todo: add circle paddings
                        childLeft = childRight - childWidth - lp.rightMargin;
                        break;

                    case Gravity.LEFT:
                    default:
                        childLeft = paddingLeft + lp.leftMargin + CIRCLE_PADDING_RIGHT  + CIRCLE_PADDING_LEFT + RADIUS * 2 + STROKE_WIDTH;
                        break;
                }

                childTop += lp.topMargin;
                setChildFrame(child, childLeft, childTop, childWidth, childHeight);
                childTop += childHeight + lp.bottomMargin;
            }
        }
    }

    /**
     * Position the children during a layout pass if the orientation of this
     * LinearLayout is set to {@link #HORIZONTAL}.
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @see #getOrientation()
     * @see #setOrientation(int)
     * @see #onLayout(boolean, int, int, int, int)
     */
    void layoutHorizontal(int left, int top, int right, int bottom) {
        final int paddingTop = getPaddingTop();

        int childTop;
        int childLeft;

        // Where bottom of child should go
        final int height = bottom - top;
        int childBottom = height - getPaddingBottom();

        // Space available for child
        int childSpace = height - paddingTop - getPaddingBottom();

        final int count = getChildCount();

        final int majorGravity = mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        final int minorGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;

        final int[] maxAscent = mMaxAscent;
        final int[] maxDescent = mMaxDescent;

        final int layoutDirection = getLayoutDirection();
        switch (Gravity.getAbsoluteGravity(majorGravity, layoutDirection)) {
            case Gravity.RIGHT:
                // mTotalLength contains the padding already
                //todo: add circle paddings
                childLeft = getPaddingLeft() + right - left - mTotalLength;
                break;

            case Gravity.CENTER_HORIZONTAL:
                // mTotalLength contains the padding already
                //todo: add circle paddings
                childLeft = getPaddingLeft() + (right - left - mTotalLength) / 2;
                break;

            case Gravity.LEFT:
            default:
                //todo: add circle paddings
                childLeft = getPaddingLeft();
                break;
        }

        int start = 0;
        int dir = 1;

        for (int i = 0; i < count; i++) {
            final int childIndex = start + dir * i;
            final View child = getChildAt(childIndex);
            if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                int childBaseline = -1;

                final LinearLayout.LayoutParams lp =
                        (LinearLayout.LayoutParams) child.getLayoutParams();

                int gravity = lp.gravity;
                if (gravity < 0) {
                    gravity = minorGravity;
                }

                switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
                    case Gravity.TOP:
                        childTop = paddingTop + lp.topMargin;
                        if (childBaseline != -1) {
                            childTop += maxAscent[INDEX_TOP] - childBaseline;
                        }
                        break;

                    case Gravity.CENTER_VERTICAL:
                        // Removed support for baseline alignment when layout_gravity or
                        // gravity == center_vertical. See bug #1038483.
                        // Keep the code around if we need to re-enable this feature
                        // if (childBaseline != -1) {
                        //     // Align baselines vertically only if the child is smaller than us
                        //     if (childSpace - childHeight > 0) {
                        //         childTop = paddingTop + (childSpace / 2) - childBaseline;
                        //     } else {
                        //         childTop = paddingTop + (childSpace - childHeight) / 2;
                        //     }
                        // } else {
                        childTop = paddingTop + ((childSpace - childHeight) / 2)
                                + lp.topMargin - lp.bottomMargin;
                        break;

                    case Gravity.BOTTOM:
                        childTop = childBottom - childHeight - lp.bottomMargin;
                        if (childBaseline != -1) {
                            int descent = child.getMeasuredHeight() - childBaseline;
                            childTop -= (maxDescent[INDEX_BOTTOM] - descent);
                        }
                        break;
                    default:
                        childTop = paddingTop;
                        break;
                }

                childLeft += lp.leftMargin;
                setChildFrame(child, childLeft, childTop, childWidth, childHeight);
                childLeft += childWidth + lp.rightMargin;
            }
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);

        circles.add(new PointF(RADIUS + STROKE_WIDTH + CIRCLE_PADDING_LEFT, top + RADIUS + CIRCLE_PADDING_TOP));
    }

}