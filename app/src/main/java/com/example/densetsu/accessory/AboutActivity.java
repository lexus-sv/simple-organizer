package com.example.densetsu.accessory;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.densetsu.Constant;
import com.example.densetsu.Methods;
import com.example.densetsu.PreferenceHelper;
import com.example.densetsu.R;

import java.util.Random;

public class AboutActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences, app_preferences;
    SharedPreferences.Editor editor;
    TextView button;
    Methods methods;

    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;

    private android.support.v7.widget.Toolbar toolbar;
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
        setContentView(R.layout.activity_about);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final ImageView imageView = (ImageView) findViewById(R.id.icon_img);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator flipIn= ObjectAnimator.ofFloat(imageView,"rotationY",-360f,0f);
                flipIn.start();
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                imageView.setColorFilter(color);
            }
        });
    }
}
