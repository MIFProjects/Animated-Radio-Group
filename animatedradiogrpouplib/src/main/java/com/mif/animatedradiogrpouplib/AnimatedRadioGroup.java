package com.mif.animatedradiogrpouplib;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by Yuriy Diachenko on 23.03.2017.
 */

public class AnimatedRadioGroup extends LinearLayout {

    private static final int BUBBLE_ANIMATION = 1;
    private static final int JUMP_ANIMATION = 2;
    private static final int FADE_ANIMATION = 3;
    private static final int GRAVITY_ANIMATION = 4;
    private static final int YOYO_ANIMATION = 5;
    private static final int NONE_ANIMATION = 6;
    private static final int RAIL_LINE_ANIMATION = 7;
    private static final int DRAW_X_ANIMAITON = 8;
    private static final int DRAW_Y_ANIMAITON = 9;

    private static final int RADIUS = 20;
    private static final int CIRCLE_PADDING_RIGHT = 50;
    private static final int CIRCLE_PADDING_LEFT = 5;
    private static final int CIRCLE_PADDING_TOP = 20;
    private static final int CIRCLE_PADDING_BOTTOM = 5;

    private static final int STROKE_WIDTH = 3;
    private int totalCircleWidth;
    private int totalCircleHeight;

    private Paint pathPaint;
    private Paint inactivePaint;
    private Paint separatorPaint;
    private Paint bgPaint;
    private CircleItem circleItem;
    private List<PointF> circles = new ArrayList<>();
    private List<Rect> rects = new ArrayList<>();
    private List<Integer> bgColors = new ArrayList<Integer>();

    private PointF ovalActive;

    private int circleFillColor = Color.DKGRAY;
    private int circleStrokeColor = Color.DKGRAY;
    private int circleRadius = RADIUS;
    private int circleCenterFillRadius = circleRadius;
    private int circlePaddingRight = CIRCLE_PADDING_RIGHT;
    private int circlePaddingLeft = CIRCLE_PADDING_LEFT;
    private int circlePaddingTop = CIRCLE_PADDING_TOP;
    private int circlePaddingBottom = CIRCLE_PADDING_TOP;
    private int circleStrokeWidth = STROKE_WIDTH;
    private int circleGravity = Gravity.TOP;
    private int animationType = BUBBLE_ANIMATION;
    private int separatorMarginStart;
    private int separatorMarginEnd;
    private boolean fullItemForClick = false;

    private boolean isSeparate = false;
    private int separatorColor = Color.WHITE;
    private AnimatorSet animatorSet;
    private int activeIndex = 0;

    private Rect separatorRect;
    private int separatorWidth;

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

    CanvasAnimator canvasAnimator;

    @IntDef({BUBBLE_ANIMATION, JUMP_ANIMATION, FADE_ANIMATION, GRAVITY_ANIMATION, YOYO_ANIMATION,
            NONE_ANIMATION, RAIL_LINE_ANIMATION, DRAW_X_ANIMAITON, DRAW_Y_ANIMAITON})
    public @interface AnimationType {
    }


    @IntDef({Gravity.TOP, Gravity.CENTER, Gravity.BOTTOM})
    public @interface GravityMode {
    }

    private AnimatedRadioGroup.OnCheckedChangeListener mOnCheckedChangeListener;


    public AnimatedRadioGroup(Context context) {
        super(context);
        init();
    }

    public AnimatedRadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initXmlStyle(attrs);
        init();
    }

    public AnimatedRadioGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initXmlStyle(attrs);
        init();
    }

    private void initXmlStyle(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AnimatedRadioGroup);
            circleFillColor = a.getColor(R.styleable.AnimatedRadioGroup_circleFillColor, Color.DKGRAY);
            circleStrokeColor = a.getColor(R.styleable.AnimatedRadioGroup_circleStrokeColor, Color.DKGRAY);
            circleRadius = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_circleRadius, RADIUS);
            circleCenterFillRadius = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_circleCenterFillRadius, RADIUS);

            circlePaddingTop = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_circlePaddingTop, CIRCLE_PADDING_TOP);
            circlePaddingBottom = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_circlePaddingBottom, CIRCLE_PADDING_BOTTOM);
            circlePaddingLeft = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_circlePaddingLeft, CIRCLE_PADDING_LEFT);
            circlePaddingRight = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_circlePaddingRight, CIRCLE_PADDING_RIGHT);
            circleStrokeWidth = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_circleStrokeWidth, STROKE_WIDTH);
            circleGravity = a.getInt(R.styleable.AnimatedRadioGroup_circleGravity, Gravity.TOP);
            isSeparate = a.getBoolean(R.styleable.AnimatedRadioGroup_setSeparator, false);
            separatorColor = a.getColor(R.styleable.AnimatedRadioGroup_separatorColor, Color.WHITE);
            separatorWidth = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_separatorStrokeWidth, STROKE_WIDTH);
            animationType = a.getInt(R.styleable.AnimatedRadioGroup_animationType, BUBBLE_ANIMATION);
            fullItemForClick = a.getBoolean(R.styleable.AnimatedRadioGroup_setFullItemForClick, false);
            separatorMarginStart = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_separatorMarginStart, 0);
            separatorMarginEnd = a.getDimensionPixelSize(R.styleable.AnimatedRadioGroup_separatorMarginEnd, 0);

            a.recycle();
        }
    }

    private void init() {
        canvasAnimator = null;
        final int version = getContext().getApplicationInfo().targetSdkVersion;
        mAllowInconsistentMeasurement = version <= Build.VERSION_CODES.M;

        setWillNotDraw(false);

        totalCircleWidth = circlePaddingRight + circlePaddingLeft + circleRadius * 2 + (circleStrokeWidth * 2);
        totalCircleHeight = circlePaddingTop + circlePaddingBottom + circleRadius * 2 + (circleStrokeWidth * 2);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(circleFillColor);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setStrokeWidth(circleStrokeWidth);


        inactivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        inactivePaint.setColor(circleStrokeColor);
        inactivePaint.setStyle(Paint.Style.STROKE);
        inactivePaint.setStrokeWidth(circleStrokeWidth);

        separatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        separatorPaint.setColor(separatorColor);
        separatorPaint.setStyle(Paint.Style.FILL);
        separatorPaint.setStrokeWidth(separatorWidth);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);

        separatorRect = new Rect();

        circleItem = new CircleItem();
        circleItem.setOutlineCircleRadius(circleRadius);
        circleItem.setCenterFillCircleRadius(circleCenterFillRadius);
        circleItem.setCenterFillCirclePaint(pathPaint);
        selectAnimation(animationType);
    }


    public void selectAnimation(@AnimationType int animationType) {
        CanvasAnimator animator;
        switch (animationType) {
            case JUMP_ANIMATION:
                animator = new JumpAnimation();
                break;
            case FADE_ANIMATION:
                animator = new FadeAnimation();
                break;
            case GRAVITY_ANIMATION:
                animator = new GravityAnimation();
                break;
            case YOYO_ANIMATION:
                animator = new YoyoAnimation();
                break;
            case NONE_ANIMATION:
                animator = new NoneAnimation();
                break;
            case RAIL_LINE_ANIMATION:
                animator = new RailLineAnimation();
                break;
            case DRAW_X_ANIMAITON:
                animator = new DrawXAnimation();
                break;
            case DRAW_Y_ANIMAITON:
                animator = new DrawVAnimation();
                break;
            default:
                animator = new BubbleAnimation();
                break;
        }

        setupAnimation(animator);
    }

    /**
     * Setup animation for radio group
     */
    public void setupAnimation(CanvasAnimator animator) {
        canvasAnimator = animator;
        canvasAnimator.setupCircle(circleItem);
        canvasAnimator.setParent(this);
        canvasAnimator.setOvalActive(ovalActive);
        requestLayout();
        invalidate();
        canvasAnimator.init();
    }

    /**
     * Setup animation for radio group
     */
    public void setCircleFillColor(int circleColor) {
        circleFillColor = circleColor;
        init();
        invalidate();
    }

    /**
     * @return color of circle
     */
    public int getCircleFillColor() {
        return circleFillColor;
    }

    /**
     * Set radius of circle
     */
    public void setCircleCenterFillRadius(int size) {
        circleCenterFillRadius = size;
        init();
        invalidate();
    }

    public int getCircleCenterFillRadius() {
        return circleCenterFillRadius;
    }

    public void setCircleStrokeColor(int circleStrokeColor) {
        this.circleStrokeColor = circleStrokeColor;
        init();
        invalidate();
    }

    public int getCircleStrokeColor() {
        return circleStrokeColor;
    }

    public void setCirclePaddingTop(int circlePaddingTop) {
        this.circlePaddingTop = circlePaddingTop;
        init();
        invalidate();
    }

    public int getCirclePaddingTop() {
        return circlePaddingTop;
    }

    public void setCirclePaddingBottom(int circlePaddingBottom) {
        this.circlePaddingBottom = circlePaddingBottom;
        init();
        invalidate();
    }

    public int getCirclePaddingBottom() {
        return this.circlePaddingBottom;
    }

    public void setCirclePaddingLeft(int circlePaddingLeft) {
        this.circlePaddingLeft = circlePaddingLeft;
        init();
        invalidate();
    }

    public int getCirclePaddingLeft() {
        return circlePaddingLeft;
    }

    public void setCirclePaddingRight(int circlePaddingRight) {
        this.circlePaddingRight = circlePaddingRight;
        init();
        invalidate();
    }

    public int getCirclePaddingRight() {
        return this.circlePaddingRight;
    }

    public void setCircleStrokeWidth(int circleStrokeWidth) {
        this.circleStrokeWidth = circleStrokeWidth;
        init();
        invalidate();
    }

    public int getCircleStrokeWidth() {
        return circleStrokeWidth;
    }

    public void setCircleGravity(@GravityMode int circleGravity) {
        this.circleGravity = circleGravity;
        init();
        invalidate();
    }

    public int getCircleGravity() {
        return circleGravity;
    }

    public void setFullItemForClick(boolean fullItemForClick) {
        this.fullItemForClick = fullItemForClick;
        init();
        invalidate();
    }

    public boolean isFullItemForClick() {
        return fullItemForClick;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
        init();
        invalidate();
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setSeparator(boolean isSeparate) {
        this.isSeparate = isSeparate;
    }

    public boolean isSeparate() {
        return isSeparate;
    }

    public void setSeparatorColor(int separatorColor) {
        this.separatorColor = separatorColor;
    }

    public int getSeparatorColor() {
        return separatorColor;
    }

    public void setSeparatorMarginEnd(int separatorMarginEnd) {
        this.separatorMarginEnd = separatorMarginEnd;
    }

    public int getSeparatorMarginEnd() {
        return separatorMarginEnd;
    }

    public void setSeparatorMarginStart(int separatorMarginStart) {
        this.separatorMarginStart = separatorMarginStart;
    }

    public int getSeparatorMarginStart() {
        return separatorMarginStart;
    }

    public void setSeparatorStrokeWidth(int separatorWidth) {
        this.separatorWidth = separatorWidth;
    }

    public int getSeparatorStrokeWidth() {
        return separatorWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < circles.size(); i++) {

            bgPaint.setColor(bgColors.get(i));
            canvas.drawRect(rects.get(i), bgPaint);


            if (isSeparate) {
                if (getOrientation() == VERTICAL) {
                    if (hasDividerBeforeChildAt(i)) {
                        separatorRect.set(rects.get(i).left, rects.get(i).bottom - separatorWidth, rects.get(i).right, rects.get(i).bottom);
                    }
                } else {
                    if (hasDividerBeforeChildAt(i)) {
                        separatorRect.set(rects.get(i).right - separatorWidth, rects.get(i).top, rects.get(i).right, rects.get(i).bottom);
                    }
                }

                canvas.drawRect(separatorRect, separatorPaint);
            }

            PointF circle = circles.get(i);
            canvas.drawCircle(circle.x, circle.y, circleRadius, inactivePaint);
        }

        if (!circles.isEmpty()) {
            canvasAnimator.onDraw(canvas);
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
        super.onTouchEvent(event);

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
        if (activeIndex == clickedCircleIndex) {
            return;
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(clickedCircleIndex);
        }

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
        int lastMotionX = (int) ev.getX(activePointerIndex);
        int lastMotionY = (int) ev.getY(activePointerIndex);

        if (fullItemForClick) {
            for (Rect rect : rects) {
                if (rect.contains(lastMotionX, lastMotionY)) {
                    return true;
                }
            }
        } else {
            for (PointF circle : circles) {
                if (new RectF(circle.x - circleRadius, circle.y - circleRadius, circle.x + circleRadius, circle.y + circleRadius).contains(lastMotionX, lastMotionY)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int getClickedCircle(MotionEvent ev) {
        int activePointerId = ev.getPointerId(0);
        final int activePointerIndex = ev.findPointerIndex(activePointerId);

        int lastMotionX = (int) ev.getX(activePointerIndex);
        int lastMotionY = (int) ev.getY(activePointerIndex);

        if (fullItemForClick) {
            for (int i = 0; i < rects.size(); i++) {
                Rect rect = rects.get(i);
                if (rect.contains(lastMotionX, lastMotionY)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < circles.size(); i++) {
                PointF rect = circles.get(i);
                if (new RectF(rect.x - circleRadius, rect.y - circleRadius, rect.x + circleRadius, rect.y + circleRadius).contains(lastMotionX, lastMotionY)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void animateSliding(PointF src, PointF dst) {

        canvasAnimator.setSourcePoint(src);
        canvasAnimator.setDestinationPoint(dst);
        animatorSet = canvasAnimator.getAnimation();

        animatorSet.start();
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
        final boolean useLargestChild = isMeasureWithLargestChildEnabled();

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

            if (hasDividerBeforeChildAt(i)) {
                mTotalLength += separatorWidth;
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
                measureChildBeforeLayout(child, i, widthMeasureSpec, totalCircleWidth,
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

                if (useLargestChild) {
                    largestChildHeight = Math.max(childHeight, largestChildHeight);
                }
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
            final int measuredWidth = child.getMeasuredWidth() + margin + totalCircleWidth;
            maxWidth = Math.max(maxWidth, measuredWidth);
            childState = combineMeasuredStates(childState, child.getMeasuredState());

            allFillParent = allFillParent && lp.width == LayoutParams.MATCH_PARENT;
            if (lp.weight > 0) {
                /*
                 * Widths of weighted Views are bogus if we end up
                 * remeasuring, so keep them isSeparate.
                 */
                weightedMaxWidth = Math.max(weightedMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
            } else {
                alternativeMaxWidth = Math.max(alternativeMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
            }

        }

        if (useLargestChild &&
                (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED)) {
            mTotalLength = 0;

            for (int i = 0; i < count; ++i) {
                final View child = getChildAt(i);


                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)
                        child.getLayoutParams();
                // Account for negative margins
                final int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + largestChildHeight +
                        lp.topMargin + lp.bottomMargin);
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
                    if (isMeasureWithLargestChildEnabled() && heightMode != MeasureSpec.EXACTLY) {
                        childHeight = largestChildHeight;
                    } else if (lp.height == 0 && (!mAllowInconsistentMeasurement
                            || heightMode == MeasureSpec.EXACTLY)) {
                        // This child needs to be laid out from scratch using
                        // only its share of excess space.
                        childHeight = share;
                    } else {
                        // This child had some intrinsic height to which we
                        // need to add its share of excess space.
                        childHeight = child.getMeasuredHeight() + share;
                    }

                    final int childHeightMeasureSpec = makeMeasureSpec(
                            Math.max(0, childHeight), MeasureSpec.EXACTLY);


                    final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
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
        } else {
            alternativeMaxWidth = Math.max(alternativeMaxWidth,
                    weightedMaxWidth);

            // We have no limit, so make all weighted views as tall as the largest child.
            // Children will have already been measured once.
            if (useLargestChild && heightMode != MeasureSpec.EXACTLY) {
                for (int i = 0; i < count; i++) {
                    final View child = getChildAt(i);
                    if (child == null || child.getVisibility() == View.GONE) {
                        continue;
                    }

                    final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();

                    float childExtra = lp.weight;
                    if (childExtra > 0) {
                        child.measure(
                                MeasureSpec.makeMeasureSpec(child.getMeasuredWidth() - totalCircleWidth,
                                        MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(largestChildHeight,
                                        MeasureSpec.EXACTLY));
                    }
                }
            }
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
        // added circle width for increase wight of view and for correct calculation of weight
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - totalCircleWidth,
                MeasureSpec.EXACTLY);
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                LinearLayout.LayoutParams lp = ((LinearLayout.LayoutParams) child.getLayoutParams());

                // Temporarily force children to reuse their old measured height
                int oldHeight = lp.height;
                lp.height = child.getMeasuredHeight();

                measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                lp.height = oldHeight;

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

        final boolean baselineAligned = isBaselineAligned();
        final boolean useLargestChild = isMeasureWithLargestChildEnabled();

        final boolean isExactly = widthMode == MeasureSpec.EXACTLY;

        int largestChildWidth = Integer.MIN_VALUE;
        int usedExcessSpace = 0;

        // See how wide everyone is. Also remember max height.
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();


            totalWeight += lp.weight;
            if (hasDividerBeforeChildAt(i)) {
                mTotalLength += separatorWidth;
            }
            final boolean useExcessSpace = lp.width == 0 && lp.weight > 0;
            if (widthMode == MeasureSpec.EXACTLY && useExcessSpace) {
                // Optimization: don't bother measuring children who are only
                // laid out using excess space. These views will get measured
                // later if we have space to distribute.
                int separatorWidth = 0;
//                if (hasDividerBeforeChildAt(i)) {
//                    separatorWidth = this.separatorWidth;
//                }
                if (isExactly) {
                    mTotalLength += lp.leftMargin + lp.rightMargin + totalCircleWidth + separatorWidth;
                } else {
                    final int totalLength = mTotalLength;
                    mTotalLength = Math.max(totalLength, totalLength +
                            lp.leftMargin + lp.rightMargin + totalCircleWidth + separatorWidth);
                }

                // Baseline alignment requires to measure widgets to obtain the
                // baseline offset (in particular for TextViews). The following
                // defeats the optimization mentioned above. Allow the child to
                // use as much space as it wants because we can shrink things
                // later (and re-measure).
                if (baselineAligned) {
                    final int freeWidthSpec = MeasureSpec.makeMeasureSpec(
                            MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.UNSPECIFIED);
                    final int freeHeightSpec = MeasureSpec.makeMeasureSpec(
                            MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED);
                    child.measure(freeWidthSpec, freeHeightSpec);
                } else {
                    skippedMeasure = true;
                }
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

                int childWidth = child.getMeasuredWidth() + totalCircleWidth;
                if (useExcessSpace) {
                    // Restore the original width and record how much space
                    // we've allocated to excess-only children so that we can
                    // match the behavior of EXACTLY measurement.
                    lp.width = 0;
                    usedExcessSpace += childWidth;
                }

                if (isExactly) {
                    mTotalLength += childWidth + lp.leftMargin + lp.rightMargin;
//                            + getNextLocationOffset(child);
                } else {
                    final int totalLength = mTotalLength;
                    mTotalLength = Math.max(totalLength, totalLength + childWidth + lp.leftMargin
                            + lp.rightMargin);
//                            + getNextLocationOffset(child));
                }

                if (useLargestChild) {
                    largestChildWidth = Math.max(childWidth, largestChildWidth);
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

            if (baselineAligned) {
                final int childBaseline = child.getBaseline();
                if (childBaseline != -1) {
                    // Translates the child's vertical gravity into an index
                    // in the range 0..VERTICAL_GRAVITY_COUNT
                    final int gravity = (lp.gravity < 0 ? mGravity : lp.gravity)
                            & Gravity.VERTICAL_GRAVITY_MASK;
                    final int index = ((gravity >> Gravity.AXIS_Y_SHIFT)
                            & ~Gravity.AXIS_SPECIFIED) >> 1;

                    maxAscent[index] = Math.max(maxAscent[index], childBaseline);
                    maxDescent[index] = Math.max(maxDescent[index], childHeight - childBaseline);
                }
            }

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

        if (useLargestChild &&
                (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED)) {
            mTotalLength = 0;

            for (int i = 0; i < count; ++i) {
                final View child = getChildAt(i);
                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)
                        child.getLayoutParams();
                if (isExactly) {
                    mTotalLength += largestChildWidth + lp.leftMargin + lp.rightMargin;
                } else {
                    final int totalLength = mTotalLength;
                    mTotalLength = Math.max(totalLength, totalLength + largestChildWidth +
                                    lp.leftMargin + lp.rightMargin);
                }
            }
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
                    if (isMeasureWithLargestChildEnabled() && widthMode != MeasureSpec.EXACTLY) {
                        childWidth = largestChildWidth;
                    } else if (lp.width == 0 && (!mAllowInconsistentMeasurement
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

                if (baselineAligned) {
                    final int childBaseline = child.getBaseline();
                    if (childBaseline != -1) {
                        // Translates the child's vertical gravity into an index in the range 0..2
                        final int gravity = (lp.gravity < 0 ? mGravity : lp.gravity)
                                & Gravity.VERTICAL_GRAVITY_MASK;
                        final int index = ((gravity >> Gravity.AXIS_Y_SHIFT)
                                & ~Gravity.AXIS_SPECIFIED) >> 1;

                        maxAscent[index] = Math.max(maxAscent[index], childBaseline);
                        maxDescent[index] = Math.max(maxDescent[index],
                                childHeight - childBaseline);
                    }
                }
            }

            // Add in our padding
            mTotalLength += getPaddingLeft() + getPaddingRight();

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

            // We have no limit, so make all weighted views as wide as the largest child.
            // Children will have already been measured once.
            if (useLargestChild && widthMode != MeasureSpec.EXACTLY) {
                for (int i = 0; i < count; i++) {
                    final View child = getChildAt(i);
                    if (child == null || child.getVisibility() == View.GONE) {
                        continue;
                    }

                    final LinearLayout.LayoutParams lp =
                            (LinearLayout.LayoutParams) child.getLayoutParams();

                    float childExtra = lp.weight;
                    if (childExtra > 0) {
                        child.measure(
                                MeasureSpec.makeMeasureSpec(largestChildWidth, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(),
                                        MeasureSpec.EXACTLY));
                    }
                }
            }
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

    /**
     * Determines where to position dividers between children.
     *
     * @return true if there should be a divider before the child at childIndex
     * @hide Pending API consideration. Currently only used internally by the system.
     */
    protected boolean hasDividerBeforeChildAt(int index) {
        return isSeparate && getChildCount() != index + 1;
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

        rects.clear();

        if (getOrientation() == VERTICAL) {
            layoutVertical(l, t, r, b);
        } else {
            layoutHorizontal(l, t, r, b);
        }

        if (!circles.isEmpty()) {
            ovalActive = new PointF(circles.get(activeIndex).x, circles.get(activeIndex).y);
            canvasAnimator.setOvalActive(ovalActive);
        }
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
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

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
                        childLeft = paddingLeft + ((childSpace - childWidth) / 2)
                                + lp.leftMargin - lp.rightMargin;
                        break;

                    case Gravity.RIGHT:
                        childLeft = childRight - childWidth - lp.rightMargin;
                        break;

                    case Gravity.LEFT:
                    default:
                        childLeft = paddingLeft + lp.leftMargin + totalCircleWidth;
                        break;
                }

                if (hasDividerBeforeChildAt(i)) {
                    childTop += lp.topMargin;
                    setChildFrame(child, childLeft, childLeft + childWidth, childTop,
                            childTop + childHeight + separatorWidth, childWidth, childHeight, VERTICAL);
                    childTop += childHeight + lp.bottomMargin + separatorWidth;
                } else {
                    childTop += lp.topMargin;
                    setChildFrame(child, childLeft, childLeft + childWidth,
                            childTop, childTop + childHeight, childWidth, childHeight, VERTICAL);
                    childTop += childHeight + lp.bottomMargin;
                }
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
                childLeft = getPaddingLeft() + right - left - mTotalLength;
                break;

            case Gravity.CENTER_HORIZONTAL:
                // mTotalLength contains the padding already
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
                if (hasDividerBeforeChildAt(i)) {
                    childLeft += lp.leftMargin + totalCircleWidth;
                    setChildFrame(child, childLeft, childLeft + childWidth + separatorWidth,
                            childTop, childTop + height, childWidth, childHeight, HORIZONTAL);
                } else {
                    childLeft += lp.leftMargin + totalCircleWidth;
                    setChildFrame(child, childLeft, childLeft + childWidth + separatorWidth,
                            childTop, childTop + height, childWidth + separatorWidth,
                            childHeight, HORIZONTAL);
                }

                childLeft += childWidth + lp.rightMargin + separatorWidth;
            }
        }
    }

    private void setChildFrame(View child, int left, int right, int top, int bottom, int width,
                               int height, int orientation) {
        child.layout(left, top, left + width, top + height);

        int yAxis = 0;
        switch (circleGravity) {
            case Gravity.TOP:
                if (HORIZONTAL == orientation) {
                    yAxis = circleRadius + circlePaddingTop + circleStrokeWidth;
                } else {
                    yAxis = top + circleRadius + circlePaddingTop + circleStrokeWidth;
                }
                break;
            case Gravity.CENTER:
                if (HORIZONTAL == orientation) {
                    yAxis = ((getHeight() / 2));
                } else {
                    yAxis = ((child.getHeight() / 2)) + top;
                }

                break;
            case Gravity.BOTTOM:
                if (HORIZONTAL == orientation) {
                    yAxis = ((getHeight()) - circleRadius - circlePaddingBottom);
                } else {
                    yAxis = ((child.getHeight()) - circleRadius - circlePaddingBottom) + top;
                }
                break;
        }

        // set bg colors to items
        int color = Color.TRANSPARENT;
        Drawable background = child.getBackground();
        if (background instanceof ColorDrawable) {
            color = ((ColorDrawable) background).getColor();
            child.setBackgroundColor(Color.TRANSPARENT);
        }
        bgColors.add(color);
        // Create circles by x and y
        circles.add(new PointF(left - circleRadius - circleStrokeWidth - circlePaddingRight, yAxis));

        // Create rect for all view
        if (orientation == HORIZONTAL) {
            // Create rect for all view
            rects.add(new Rect(left - totalCircleWidth, separatorMarginStart, right, getHeight() - separatorMarginEnd));
        } else {
            rects.add(new Rect(left - totalCircleWidth + separatorMarginStart, top, getWidth() - separatorMarginEnd, bottom));
        }
    }


    /**
     * @return index of item which select
     */
    public int getCheckedItem() {
        return activeIndex;
    }

    public void setCheckedItem(int index) {
        activeIndex = index;
        if (circles.size() > 0)
            setSelection(index);
    }


    /**
     * @param listener call when selected item and g item position
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        public void onCheckedChanged(@IdRes int checkedId);
    }
}

class CircleItem {

    private int outlineCircleRadius;
    private int centerFillCircleRadius;
    private Paint centerFillCirclePaint;

    public int getOutlineCircleRadius() {
        return outlineCircleRadius;
    }

    public void setOutlineCircleRadius(int outlineCircleRadius) {
        this.outlineCircleRadius = outlineCircleRadius;
    }

    public int getCenterFillCircleRadius() {
        return centerFillCircleRadius;
    }

    public void setCenterFillCircleRadius(int centerFillCircleRadius) {
        this.centerFillCircleRadius = centerFillCircleRadius;
    }

    public Paint getCenterFillCirclePaint() {
        return centerFillCirclePaint;
    }

    public void setCenterFillCirclePaint(Paint centerFillCirclePaint) {
        this.centerFillCirclePaint = centerFillCirclePaint;
    }

}