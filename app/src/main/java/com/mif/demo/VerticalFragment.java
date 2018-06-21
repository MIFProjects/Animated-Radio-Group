package com.mif.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mif.animatedradiogrpouplib.AnimatedRadioGroup;

public class VerticalFragment extends BaseFragment {


    private AnimatedRadioGroup animatedWrapContent;
    private AnimatedRadioGroup animatedMatchParent;
    private AnimatedRadioGroup animatedWeight;
    private AnimatedRadioGroup animatedFixSize;
    private AnimatedRadioGroup animatedWrapContentWithFixContent;

    public VerticalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_vertical, container, false);

        animatedWrapContent = (AnimatedRadioGroup) view.findViewById(R.id.animatedWrapContent);
        animatedMatchParent = (AnimatedRadioGroup) view.findViewById(R.id.animatedMatchParent);
        animatedWeight = (AnimatedRadioGroup) view.findViewById(R.id.animatedWeight);
        animatedFixSize = (AnimatedRadioGroup) view.findViewById(R.id.animatedFixSize);
        animatedWrapContentWithFixContent = (AnimatedRadioGroup) view.findViewById(R.id.animatedWrapContentWithFixContent);
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
