package com.mif.demo;

import android.support.v4.app.Fragment;

import java.util.Arrays;
import java.util.List;

public abstract class BaseFragment extends Fragment {

    public void applyAnimation(String nameOfAnimation) {
        String[]array = getResources().getStringArray(R.array.AnimatedType);
        List<String> animations = Arrays.asList(array);
        int indexOf = animations.indexOf(nameOfAnimation);
        applyAnimationToView(indexOf + 1);
    }
    public abstract void applyAnimationToView(int indexOf);
}
