package com.example.densetsu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.densetsu.MainActivity;
import com.example.densetsu.PreferenceHelper;
import com.example.densetsu.R;
import com.example.densetsu.adapter.TaskAdapter;
import com.example.densetsu.alarm.AlarmHelper;
import com.example.densetsu.dialog.EditTaskDialogFragment;
import com.example.densetsu.model.Item;
import com.example.densetsu.model.ModelTask;

import java.util.Date;

public abstract class TaskFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected TaskAdapter adapter;
    public AlarmHelper alarmHelper;
    PreferenceHelper preferenceHelper;

    public MainActivity activity;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }
        alarmHelper = AlarmHelper.getInstance();

        addTaskFromDB();
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDB);

    public void updateTask(ModelTask task){
        adapter.updateTask(task);
    }


    public void removeTaskDialog(final int location) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//        preferenceHelper = new PreferenceHelper(getContext());
//        if(preferenceHelper.loadNightModeState()==true) {
//            dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.darkTheme);
//        }
//        else  dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppTheme);

        dialogBuilder.setMessage(R.string.dialog_removing_message);

        Item item = adapter.getItem(location);

        if (item.isTask()) {

            ModelTask removingTask = (ModelTask) item;

            final long timeStamp = removingTask.getTimeStamp();
            final boolean[] isRemoved = {false};

            dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    adapter.removeItem(location);
                    isRemoved[0] = true;
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.container),
                            R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addTask(activity.dbHelper.query().getTask(timeStamp), false);
                            isRemoved[0] = false;
                        }
                    });

                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            if (isRemoved[0]) {
                                alarmHelper.removeAlarm(timeStamp);
                                activity.dbHelper.removeTask(timeStamp);
//                                if(checkCount()){
//                                    Intent i = new Intent(getContext(), MainActivity.class);
//                                    startActivity(i);
//                                }
                            }
                        }
                    });

                    snackbar.show();


                    dialog.dismiss();

                }
            });

            dialogBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        }

        dialogBuilder.show();
    }

    public void removeAllTasks() {
        adapter.removeAllItems();
    }

    public abstract void findTasks(String title);
    public abstract void findTasksByCategory(int priority);

    public void showTaskEditDialog(ModelTask task){
        DialogFragment editingTaskDialog = EditTaskDialogFragment.newInstance(task);
        editingTaskDialog.show(getActivity().getSupportFragmentManager(), "EditTaskDialogFragment");
    }

    public abstract void addTaskFromDB();
    public abstract void showAllTasks();

    public abstract void findTasksByDate(long date);

    public abstract void checkAdapter();

    public abstract void moveTask(ModelTask task);

    public boolean checkCount(){
        if(adapter.getItemCount()==0){
            return true;
        }
       else return false;
    }
}
