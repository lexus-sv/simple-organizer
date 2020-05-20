package com.example.densetsu.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.densetsu.model.ModelTask;

public class DBUpdateManager {

    SQLiteDatabase database;

    DBUpdateManager(SQLiteDatabase database) {
        this.database = database;
    }

    public void title(long timeStamp, String title) {
        update(DBHelper.TASK_TITLE_COLUMN, timeStamp, title);
    }

    public void date(long timeStamp, long date) {
        update(DBHelper.TASK_DATE_COLUMN, timeStamp, date);
    }

    public void priority(long timeStamp, int priority) {
        update(DBHelper.TASK_PRIORITY_COLUMN, timeStamp, priority);
    }

    public void status(long timeStamp, int status) {
        update(DBHelper.TASK_STATUS_COLUMN, timeStamp, status);
    }

    public void description(long timeStamp, String description){
        update(DBHelper.TASK_DESCRIPTION_COLUMN, timeStamp, description);
    }

    public void alarm(long timeStamp, int alarm){
        update(DBHelper.TASK_ALARM_COLUMN, timeStamp, alarm);
    }
    public void repeat(long timeStamp, long repeat){
        update(DBHelper.TASK_REPEATING_ALARM_COLUMN, timeStamp, repeat);
    }
    public void day(long timeStamp, long day){
        update(DBHelper.TASK_DAY_COLUMN, timeStamp, day);
    }

    public void task(ModelTask task) {
        title(task.getTimeStamp(), task.getTitle());
        date(task.getTimeStamp(), task.getDate());
        priority(task.getTimeStamp(), task.getPriority());
        status(task.getTimeStamp(), task.getStatus());
        description(task.getTimeStamp(),task.getDescription());
        alarm(task.getTimeStamp(),task.getAlarm());
        repeat(task.getTimeStamp(),task.getRepeat());
        day(task.getTimeStamp(),task.getDay());
    }



    private void update(String column, long key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        database.update(DBHelper.TASKS_TABLE, cv, DBHelper.TASK_TIME_STAMP_COLUMN + " = " + key, null);
    }

    private void update (String column, long key, long value) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        database.update(DBHelper.TASKS_TABLE, cv, DBHelper.TASK_TIME_STAMP_COLUMN + " = " + key, null);
    }

}
