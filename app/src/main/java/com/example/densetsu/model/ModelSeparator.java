package com.example.densetsu.model;

import com.example.densetsu.R;

public class ModelSeparator implements Item {
    public final static int TYPE_OVERDUE = R.string.separator_overdue;
    public final static int TYPE_TODAY = R.string.separator_today;
    public final static int TYPE_TOMORROW = R.string.separator_tomorrow;
    public final static int TYPE_FUTURE =  R.string.separator_future;

    private int type;

    public ModelSeparator(int type) {
        this.type = type;
    }


    @Override
    public boolean isTask() {
        return false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
