package com.example.densetsu.customSpinner;

import android.graphics.Color;

public class MarkerItem {
    private String mCategory;
    private int mImage;

    public int getmImage() {
        return mImage;
    }

    public void setmImage(int mImage) {
        this.mImage = mImage;
    }

    public MarkerItem(int mImage, String mCategory) {
        this.mCategory = mCategory;
        this.mImage = mImage;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }
}
