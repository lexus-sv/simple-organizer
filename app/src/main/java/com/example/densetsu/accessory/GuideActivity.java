package com.example.densetsu.accessory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.densetsu.Constant;
import com.example.densetsu.MainActivity;
import com.example.densetsu.Methods;
import com.example.densetsu.PreferenceHelper;
import com.example.densetsu.R;
import com.example.densetsu.SliderAdapter;

public class GuideActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences, app_preferences;
    SharedPreferences.Editor editor;
    TextView button;
    Methods methods;

    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;

    private ViewPager viewPager;
    SliderAdapter sliderAdapter;
    LinearLayout dotsLayout;
    Toolbar toolbar;
    private ImageView[] dots;
    PreferenceHelper preferenceHelper;
    @Override
    public boolean onSupportNavigateUp() {
//        Intent i = new Intent(this, MainActivity.class);
//        startActivity(i);
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        createDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                createDots(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void createDots(int currentPosition) {
        if (dotsLayout != null) {
            dotsLayout.removeAllViews();
        }
        dots = new ImageView[4];

        for (int i = 0; i < 4; i++) {
            dots[i] = new ImageView(this);
            if (i == currentPosition) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            } else
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 0, 4, 0);
            dotsLayout.addView(dots[i], layoutParams);
        }
    }
}
