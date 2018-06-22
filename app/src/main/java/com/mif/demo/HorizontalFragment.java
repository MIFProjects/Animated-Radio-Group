package com.mif.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mif.animatedradiogrpouplib.AnimatedRadioGroup;

public class HorizontalFragment extends BaseFragment {


    private AnimatedRadioGroup animatedWrapContent;
    private AnimatedRadioGroup animatedMatchParent;
    private AnimatedRadioGroup animatedWeight;
    private AnimatedRadioGroup animatedFixSize;
    private AnimatedRadioGroup animatedWrapContentWithFixContent;

    public HorizontalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_horizontal, container, false);
        animatedWrapContent = (AnimatedRadioGroup) view.findViewById(R.id.animatedWrapContent);
        animatedMatchParent = (AnimatedRadioGroup) view.findViewById(R.id.animatedMatchParent);
        animatedWeight = (AnimatedRadioGroup) view.findViewById(R.id.animatedWeight);
        animatedFixSize = (AnimatedRadioGroup) view.findViewById(R.id.animatedFixSize);
        animatedWrapContentWithFixContent = (AnimatedRadioGroup) view.findViewById(R.id.animatedWrapContentWithFixContent);

//
//        animatedWrapContent.setCircleCenterFillRadius((int) getResources().getDimension(R.dimen.circleFillRadius));
//        animatedWrapContent.setCircleFillColor(getResources().getColor(R.color.colorPrimary));
//        animatedWrapContent.setCircleGravity(Gravity.CENTER);
//        animatedWrapContent.setCirclePaddingLeft((int) getResources().getDimension(R.dimen.circlePaddingLeft));
//        animatedWrapContent.setCirclePaddingRight((int) getResources().getDimension(R.dimen.circlePaddingLeft));
//        animatedWrapContent.setCircleRadius((int) getResources().getDimension(R.dimen.circleRadius));
//        animatedWrapContent.setCircleStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
//        animatedWrapContent.setCircleStrokeWidth((int)getResources().getDimension(R.dimen.circleStrokeWidth));
//        animatedWrapContent.setSeparatorColor(getResources().getColor(R.color.colorDivider));
//        animatedWrapContent.setSeparatorMarginEnd((int)getResources().getDimension(R.dimen.separatorMargin));
//        animatedWrapContent.setSeparatorMarginStart((int)getResources().getDimension(R.dimen.separatorMargin));
//        animatedWrapContent.setSeparatorStrokeWidth((int)getResources().getDimension(R.dimen.separatorStrokeWidth));
//        animatedWrapContent.setFullItemForClick(false);
//        animatedWrapContent.setSeparator(false);



        return view;
    }

    @Override
    public void applyAnimationToView(int indexOf) {
        animatedWrapContent.selectAnimation(indexOf);
        animatedMatchParent.selectAnimation(indexOf);
        animatedWeight.selectAnimation(indexOf);
        animatedFixSize.selectAnimation(indexOf);
        animatedWrapContentWithFixContent.selectAnimation(indexOf);
    }
}
