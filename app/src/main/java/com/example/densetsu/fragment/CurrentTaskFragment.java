package com.example.densetsu.fragment;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.densetsu.MainActivity;
import com.example.densetsu.R;
import com.example.densetsu.adapter.CurrentTasksAdapter;
import com.example.densetsu.database.DBHelper;
import com.example.densetsu.model.ModelSeparator;
import com.example.densetsu.model.ModelTask;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.sql.SQLData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentTaskFragment extends TaskFragment {

    ImageView backgroundImage;
    public int itemCount;
    List<Event> events = new ArrayList<>();

    public int getItemCount() {
        return itemCount;
    }

    public CurrentTaskFragment() {
    }

    OnTaskDoneListener onTaskDoneListener;

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask task);
    }

    public interface PassListListener{
        void passList(List<Event> events);
    }
    PassListListener passListListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTaskDoneListener = (OnTaskDoneListener) activity;
            passListListener = (PassListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTaskDoneListener, PassListListener");
        }
    }
    public void passData(List<Event> events){
        passListListener.passList(events);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_current_tasks);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        adapter = new CurrentTasksAdapter(this);
        recyclerView.setAdapter(adapter);
        itemCount = adapter.getItemCount();
        backgroundImage = (ImageView) rootView.findViewById(R.id.backgroundImage);
        return rootView;
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;
        ModelSeparator separator = null;




        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) adapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }

        if (newTask.getDate() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());
            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_OVERDUE);
                if (!adapter.containsSeparatorOverdue) {
                    adapter.containsSeparatorOverdue = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_OVERDUE);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_TODAY);
                if (!adapter.containsSeparatorToday) {
                    adapter.containsSeparatorToday = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TODAY);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_TOMORROW);
                if (!adapter.containsSeparatorTomorrow) {
                    adapter.containsSeparatorTomorrow = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TOMORROW);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_FUTURE);
                if (!adapter.containsSeparatorFuture) {
                    adapter.containsSeparatorFuture = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_FUTURE);
                }
            }


            if (position != -1) {

                if(!adapter.getItem(position-1).isTask()){
                    if(position - 2 >=0 && adapter.getItem(position-2).isTask()){
                        ModelTask task = (ModelTask) adapter.getItem(position-2);
                        if(task.getDateStatus()==newTask.getDateStatus()){
                            position -= 1;
                        }
                    } else if (position-2 < 0 && newTask.getDate() == 0){
                        position -=1;
                    }
                }
                if (separator!=null){
                    adapter.addItem(position-1,separator);
                }
                adapter.addItem(position, newTask);

            } else {
                if (separator!= null){
                    adapter.addItem(separator);
                }
                events.add(new Event(Color.WHITE, newTask.getDate()));
                adapter.addItem(newTask);
            }

            if (saveToDB) {
                activity.dbHelper.saveTask(newTask);
            }
        }

    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_TITLE + " AND "
                        + DBHelper.SELECTION_STATUS + " OR " + DBHelper.SELECTION_STATUS,
                new String[]{"%"+ title + "%", Integer.toString(ModelTask.STATUS_CURRENT),
                        Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            Log.d("data", "Title = " + tasks.get(i).getTitle());
            addTask(tasks.get(i), false);

        }
    }

    @Override
    public void findTasksByCategory(int priority) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_PRIORITY + " AND "
                        + DBHelper.SELECTION_STATUS + " OR " + DBHelper.SELECTION_STATUS,
                new String[]{"%"+ priority + "%", Integer.toString(ModelTask.STATUS_CURRENT),
                        Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            Log.d("data", "Title = " + tasks.get(i).getTitle());
            addTask(tasks.get(i), false);

        }
    }

    @Override
    public void findTasksByDate(long date) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        Date newDate = new Date(date);
        dateTrim(newDate);
        date = newDate.getTime();
//        tasks.addAll(activity.dbHelper.query().getTasksWithDate(new String[]{"%"+date+"%",
//                Integer.toString(ModelTask.STATUS_CURRENT),
//                Integer.toString(ModelTask.STATUS_OVERDUE)}));
        tasks.addAll((activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_DATE + " AND "
                        + DBHelper.SELECTION_STATUS + " OR " + DBHelper.SELECTION_STATUS,
                new String[]{"%"+ date + "%", Integer.toString(ModelTask.STATUS_CURRENT),
                        Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN)));
        Log.d("data", "Date = "+date+"Fulldate = " + new Date(date));
        for (int i = 0; i < tasks.size(); i++) {
            Log.d("data", "Title = " + tasks.get(i).getTitle());
            addTask(tasks.get(i), false);

        }
    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
            setBackgroundImage();

        }
        passData(events);
    }

    @Override
    public void showAllTasks() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
            setBackgroundImage();

        }
    }

    @Override
    public void checkAdapter() {
        if (adapter == null){
            adapter = new CurrentTasksAdapter(this);
            adapter.removeAllItems();
            addTaskFromDB();
            setBackgroundImage();
        }
    }

    @Override
    public void moveTask(ModelTask task) {
        alarmHelper.removeAlarm(task.getTimeStamp());
        onTaskDoneListener.onTaskDone(task);
    }
    public void setBackgroundImage(){
        if(adapter.getItemCount()==0){
            backgroundImage.setVisibility(View.VISIBLE);
        } else backgroundImage.setVisibility(View.INVISIBLE);
    }
    public Date dateTrim(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar.getTime();
    }
}
