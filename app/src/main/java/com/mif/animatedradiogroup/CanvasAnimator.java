package com.mif.animatedradiogroup;

import android.animation.AnimatorSet;
import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.List;

/**
 * Created by v_alekseev on 08.05.17.
 */

public interface CanvasAnimator {

    void onDraw(Canvas canvas);

    void setParent(AnimatedRadioGroup view);

    void setDestinationPoint(PointF dst);

    void setSourcePoint(PointF src);

    AnimatorSet getAnimation();

    void setOvalActive(PointF ovalActive);

}


