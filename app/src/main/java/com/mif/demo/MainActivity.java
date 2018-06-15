package com.mif.demo;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;



public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.vp_orientation);
        tabLayout = (TabLayout) findViewById(R.id.tb_tabs);

        OrientationAdapter adapterViewPager = new OrientationAdapter(getSupportFragmentManager(), this);
        adapterViewPager.addItem(VerticalFragment.class.getName());
        adapterViewPager.addItem(HorizontalFragment.class.getName());

        viewPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Vertical");
        tabLayout.getTabAt(1).setText("Horizontal");

        final ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.AnimatedType, R.layout.spinner_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nameOfAnimation = (String) adapter.getItem(position);
                applyAnimation(nameOfAnimation);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });
    }

    private void applyAnimation(String nameOfAnimation) {
        OrientationAdapter adapter = (OrientationAdapter) viewPager.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {

            Fragment fragment = (Fragment) adapter.getFragment(i);
            if (fragment instanceof BaseFragment) {
                BaseFragment baseFragment = (BaseFragment) fragment;
                baseFragment.applyAnimation(nameOfAnimation);
            }

        }
    }

}
