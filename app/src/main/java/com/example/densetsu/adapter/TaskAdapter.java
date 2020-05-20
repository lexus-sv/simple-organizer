package com.example.densetsu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.densetsu.fragment.TaskFragment;
import com.example.densetsu.model.Item;
import com.example.densetsu.model.ModelSeparator;
import com.example.densetsu.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Item> items;

    TaskFragment taskFragment;

    public boolean containsSeparatorOverdue;
    public boolean containsSeparatorToday;
    public boolean containsSeparatorTomorrow;
    public boolean containsSeparatorFuture;

    public TaskAdapter(TaskFragment taskFragment) {
        this.taskFragment = taskFragment;
        items = new ArrayList<>();
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }
    public void updateTask(ModelTask newTask) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).isTask()) {
                ModelTask task = (ModelTask) getItem(i);
                if (newTask.getTimeStamp() == task.getTimeStamp()) {
                    removeItem(i);
                    getTaskFragment().addTask(newTask, false);
                }
            }
        }
    }

    public void addItem(int location, Item item) {
        items.add(location, item);
        notifyItemInserted(location);
    }

    public void removeItem(int location) {
        if (location >= 0 && location <= getItemCount() -1) {
            items.remove(location);
            notifyItemRemoved(location);
            if (location - 1 >= 0 && location <= getItemCount() - 1) {
                if (!getItem(location).isTask() && !getItem(location - 1).isTask()) {
                    ModelSeparator separator = (ModelSeparator) getItem(location - 1);
                    checkSeparators(separator.getType());
                    items.remove(location - 1);
                    notifyItemRemoved(location - 1);
                }
            } else if (getItemCount() - 1 >= 0 && !getItem(getItemCount() - 1).isTask()) {
                ModelSeparator separator = (ModelSeparator) getItem(getItemCount() - 1);
                checkSeparators(separator.getType());

                int locationTemp = getItemCount() - 1;
                items.remove(locationTemp);
                notifyItemRemoved(locationTemp);
            }
        }
    }
    public void checkSeparators(int type) {
        switch (type) {
            case ModelSeparator.TYPE_OVERDUE:
                containsSeparatorOverdue = false;
                break;
            case ModelSeparator.TYPE_TODAY:
                containsSeparatorToday = false;
                break;
            case ModelSeparator.TYPE_TOMORROW:
                containsSeparatorTomorrow = false;
                break;
            case ModelSeparator.TYPE_FUTURE:
                containsSeparatorFuture = false;
                break;
        }
    }

    public void removeAllItems() {
        if (getItemCount() != 0) {
            items = new ArrayList<>();
            notifyDataSetChanged();
            containsSeparatorFuture = false;
            containsSeparatorTomorrow = false;
            containsSeparatorToday = false;
            containsSeparatorOverdue = false;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class TaskViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView date;
        protected TextView time;
        protected CircleImageView priority;
        protected ImageButton menu;
        protected TextView description;
        protected ImageButton showDesc;
        protected ImageView imgDesc;
        protected ImageView imCircle;
        protected ImageView AlarmImg;
        protected ImageView RepeatImg;

        public TaskViewHolder(View itemView, TextView title, TextView date, CircleImageView priority,
                ImageButton menu, TextView time, TextView description, ImageButton showDesc, ImageView imgDesc,
                ImageView imCircle, ImageView imgAlarm, ImageView RepeatImg) {
            super(itemView);
            this.title = title;
            this.date = date;
            this.priority = priority;
            this.menu = menu;
            this.time = time;
            this.description = description;
            this.showDesc = showDesc;
            this.imgDesc = imgDesc;
            this.imCircle = imCircle;
            this.AlarmImg = imgAlarm;
            this.RepeatImg = RepeatImg;
        }

    }
    protected class SeparatorViewHolder extends RecyclerView.ViewHolder{

        protected TextView type;

        public SeparatorViewHolder(@NonNull View itemView, TextView type) {
            super(itemView);
            this.type=type;
        }
    }

    public TaskFragment getTaskFragment() {
        return taskFragment;
    }
}