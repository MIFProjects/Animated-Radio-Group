package com.mif.animatedradiogroup;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AnimatedRadioGroup rg2 = (AnimatedRadioGroup) findViewById(R.id.animatedRG2);
        rg2.setCheckedItem(1);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.AnimatedType, R.layout.spinner_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rg2.selectAnimation(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,
//                R.array.OrientationType, R.layout.spinner_item);
//        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
//        spinner2.setAdapter(adapter2);
//        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    rg2.setOrientation(LinearLayout.VERTICAL);
//                } else if (position == 1) {
//                    rg2.setOrientation(LinearLayout.HORIZONTAL);
//                }
//                rg2.invalidate();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this,
//                R.array.LayoutSizeType, R.layout.spinner_item);
//        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
//        spinner3.setAdapter(adapter3);
//        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    AnimatedRadioGroup.LayoutParams param = new AnimatedRadioGroup.LayoutParams(
//                            AnimatedRadioGroup.LayoutParams.MATCH_PARENT,
//                            AnimatedRadioGroup.LayoutParams.MATCH_PARENT,
//                            1.0f
//                    );
//                    rg2.setLayoutParams(param);
//                } else if (position == 1) {
//                    AnimatedRadioGroup.LayoutParams param = new AnimatedRadioGroup.LayoutParams(
//                            AnimatedRadioGroup.LayoutParams.WRAP_CONTENT,
//                            AnimatedRadioGroup.LayoutParams.WRAP_CONTENT,
//                            1.0f
//                    );
//                    rg2.setLayoutParams(param);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

}
