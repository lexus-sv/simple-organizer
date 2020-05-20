package com.example.densetsu.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.densetsu.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

public class DBQueryManager {

    private SQLiteDatabase database;

    DBQueryManager(SQLiteDatabase database) {
        this.database = database;
    }


    public ModelTask getTask(long timeStamp) {
        ModelTask modelTask = null;
        Cursor cursor = database.query(DBHelper.TASKS_TABLE, null, DBHelper.SELECTION_TIME_STAMP,
                new String[]{Long.toString(timeStamp)}, null, null, null);

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
            long date = cursor.getLong(cursor.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
            int priority = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
            int status = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
            String description = cursor.getString(cursor.getColumnIndex(DBHelper.TASK_DESCRIPTION_COLUMN));
            int alarm = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_ALARM_COLUMN));
            long repeat = cursor.getLong(cursor.getColumnIndex(DBHelper.TASK_REPEATING_ALARM_COLUMN));
            long day = cursor.getLong(cursor.getColumnIndex(DBHelper.TASK_DAY_COLUMN));

            modelTask = new ModelTask(title, date, priority, status, timeStamp, description, alarm, repeat, day);
        }
        cursor.close();

        return modelTask;

    }


    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String orderBy) {
        List<ModelTask> tasks = new ArrayList<>();

        Cursor c = database.query(DBHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);

        if (c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
                long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
                int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
                int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
                long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASK_TIME_STAMP_COLUMN));
                String description = c.getString(c.getColumnIndex(DBHelper.TASK_DESCRIPTION_COLUMN));
                int alarm = c.getInt(c.getColumnIndex(DBHelper.TASK_ALARM_COLUMN));
                long repeat = c.getLong(c.getColumnIndex(DBHelper.TASK_REPEATING_ALARM_COLUMN));
                long day = c.getLong(c.getColumnIndex(DBHelper.TASK_DAY_COLUMN));

                Log.d("wtf?", "title = " + title);
                Log.d("wtf?", "alarm = " + alarm);

                ModelTask modelTask = new ModelTask(title, date, priority, status, timeStamp, description, alarm, repeat, day);
                tasks.add(modelTask);
            } while (c.moveToNext());
        }
        c.close();

        return tasks;
    }

}
