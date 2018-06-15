package com.mif.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mif.animatedradiogrpouplib.AnimatedRadioGroup;

public class HorizontalFragment extends BaseFragment {

    private AnimatedRadioGroup animationFirst;
    private AnimatedRadioGroup animationSecond;

    public HorizontalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_horizontal, container, false);
        animationFirst = (AnimatedRadioGroup) view.findViewById(R.id.animatedRG1);
        animationSecond = (AnimatedRadioGroup) view.findViewById(R.id.animatedRG2);
        return view;
    }

    @Override
    public void applyAnimationToView(int indexOf) {
        animationFirst.selectAnimation(indexOf);
        animationSecond.selectAnimation(indexOf);
    }
}
