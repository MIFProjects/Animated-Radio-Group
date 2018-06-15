package com.mif.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mif.animatedradiogrpouplib.AnimatedRadioGroup;

public class VerticalFragment extends BaseFragment {

    private AnimatedRadioGroup animatedRadioGroup;

    public VerticalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_vertical, container, false);

        animatedRadioGroup = (AnimatedRadioGroup) view.findViewById(R.id.animatedRG1);
        return view;
    }


    @Override
    public void applyAnimationToView(int indexOf) {
        animatedRadioGroup.selectAnimation(indexOf);
    }
}
