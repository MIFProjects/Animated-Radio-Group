package com.mif.animatedradiogroup;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final AnimatedRadioGroup rg = (AnimatedRadioGroup) findViewById(R.id.animatedRG);
//
//        rg.setCheckedItem(1);
//        rg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Selected item # " + rg.getCheckedItem(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        rg.setOnCheckedChangeListener(new AnimatedRadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(@IdRes int checkedId) {
//                Log.d("AnimatedRadioGroup", "checkedID " + checkedId);
//
//            }
//        });


//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rg.setCheckedItem(2);
//            }
//        });


    }
}
