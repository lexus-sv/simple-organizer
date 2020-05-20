package com.example.densetsu.model;

import android.graphics.Color;

import com.example.densetsu.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ModelTask implements Item{

//    public final static int PRIORITY_LOW=0;
//    public final static int PRIORITY_NORMAL=1;
//    public final static int PRIORITY_HIGH=2;
//
//    public final static String[] PRIORITY_LEVELS = {"Low priority","Normal priority","High priority"};
    public final static int STATUS_OVERDUE=0;
    public final static int STATUS_CURRENT=1;
    public final static int STATUS_DONE=2;

    private String title;
    private long date;
    private int priority;
    private int status;
    private long timeStamp;
    private int alarm;
    private int dateStatus;
    private String description;
    private long repeat;
    private long day;


    public ModelTask() {
        this.status=-1;
        this.timeStamp=new Date().getTime();
    }



    public ModelTask(String title, long date, int priority, int status,long timeStamp, String description, int alarm, long repeat, long day) {
        this.title = title;
        this.date = date;
        this.priority=priority;
        this.status=status;
        this.timeStamp=timeStamp;
        this.description=description;
        this.alarm = alarm;
        this.repeat=repeat;
        this.day= day;
    }

    @Override
    public boolean isTask() {
        return true;
    }

    public int getPriorityColor(){
        switch (getPriority()) {

            case 0:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_1;
                } else {
                    return R.color.priority_1_selected;
                }
            case 1:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_2;
                } else {
                    return R.color.priority_2_selected;
                }
            case 2:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_3;
                } else {
                    return R.color.priority_3_selected;
                }
            case 3:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_4;
                } else {
                    return R.color.priority_4_selected;
                }
            case 4:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_5;
                } else {
                    return R.color.priority_5_selected;
                }
            case 5:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_6;
                } else {
                    return R.color.priority_6_selected;
                }
            case 6:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_7;
                } else {
                    return R.color.priority_7_selected;
                }
            case 7:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_8;
                } else {
                    return R.color.priority_8_selected;
                }
            case 8:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_9;
                } else {
                    return R.color.priority_9_selected;
                }
            case 9:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_10;
                } else {
                    return R.color.priority_10_selected;
                }
            case 10:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_11;
                } else {
                    return R.color.priority_11_selected;
                }
            case 11:
                if (getStatus() == STATUS_CURRENT || getStatus() == STATUS_OVERDUE) {
                    return R.color.priority_12;
                } else {
                    return R.color.priority_12_selected;
                }

            default:
                return 0;
        }
    }

    public int getImage(){
        switch (getPriority()) {

            case 0:
                    return R.drawable.home;

            case 1:
                return R.drawable.work;
            case 2:
                return R.drawable.sport;
            case 3:
                return R.drawable.pokupki;
            default:
                return 0;
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public int getStatus() {
        return status;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(int dateStatus) {
        this.dateStatus = dateStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public long getRepeat() {
        return repeat;
    }

    public void setRepeat(long repeat) {
        this.repeat = repeat;
    }

    public long dateTrim(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar.getTimeInMillis();
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }
}

