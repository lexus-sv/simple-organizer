package com.example.densetsu;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {
    SharedPreferences mySharedPref ;
    public PreferenceHelper(Context context) {
        mySharedPref = context.getSharedPreferences("filename",Context.MODE_PRIVATE);
    }
    // this method will save the nightMode State : True or False
    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode",state);
        editor.commit();
    }
    // this method will load the Night Mode State
    public Boolean loadNightModeState (){
        Boolean state = mySharedPref.getBoolean("NightMode",false);
        return  state;
    }
//    public static final String SPLASH_IS_INVISIBLE = "splash_is_invisible";
//
//    private static PreferenceHelper instance;
//
//    private Context context;
//
//    private SharedPreferences preferences;
//
//    private PreferenceHelper() {
//
//    }
//
//    public static PreferenceHelper getInstance() {
//        if (instance ==null) {
//            instance = new PreferenceHelper();
//        }
//        return instance;
//    }
//
//    public void init(Context context) {
//        this.context = context;
//        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
//    }
//
//    public void putBoolean(String key, boolean value) {
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean(key, value);
//        editor.apply();
//    }
//
//    public boolean getBoolean(String key) {
//        return preferences.getBoolean(key, false);
//    }
}
