package com.mif.animatedradiogroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

/**
 * Created by v_alekseev on 08.05.17.
 */

public interface CanvasAnimator {

    void onDraw(Canvas canvas);

    void setActiveIndex(int activeIndex);

    void setParent(AnimatedRadioGroup view);

    void setDestinationPoint(PointF dst);

    void setSourcePoint(PointF src);

    AnimatorSet getAnimation();

    void setOvalActive(PointF ovalActive);

    void setCircles(List<PointF> circles);

}


