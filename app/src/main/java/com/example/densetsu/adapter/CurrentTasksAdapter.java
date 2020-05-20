package com.example.densetsu.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.densetsu.R;
import com.example.densetsu.Utils;
import com.example.densetsu.alarm.AlarmHelper;
import com.example.densetsu.fragment.CurrentTaskFragment;
import com.example.densetsu.fragment.TaskFragment;
import com.example.densetsu.model.Item;
import com.example.densetsu.model.ModelSeparator;
import com.example.densetsu.model.ModelTask;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class CurrentTasksAdapter extends TaskAdapter {

    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;
    public AlarmHelper alarmHelper;

    public CurrentTasksAdapter(CurrentTaskFragment taskFragment) {
        super(taskFragment);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case TYPE_TASK:
                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.model_task, viewGroup, false);
                TextView title = (TextView) v.findViewById(R.id.tvTaskTitle);
                TextView date = (TextView) v.findViewById(R.id.tvTaskDate);
                TextView time = (TextView) v.findViewById(R.id.tvTaskTime);
                ImageView imgDesc = (ImageView) v.findViewById(R.id.description_image);
                CircleImageView priority=(CircleImageView) v.findViewById(R.id.cvTaskPriority);
                ImageButton buttonViewOption=(ImageButton) v.findViewById(R.id.taskPopup);
                ImageButton showDesc = (ImageButton) v.findViewById(R.id.drop_down);
                TextView description = (TextView) v.findViewById(R.id.tvDescription);
                ImageView imCircle = (ImageView) v.findViewById(R.id.imCircle);
                ImageView alarm = (ImageView) v.findViewById(R.id.alarm_image);
                ImageView repeat = (ImageView) v.findViewById(R.id.repeat_image);


                return new TaskViewHolder(v, title, date, priority, buttonViewOption, time, description, showDesc, imgDesc, imCircle, alarm,repeat);

            case TYPE_SEPARATOR:
                View separator = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_separator,
                        viewGroup, false);
                TextView type = separator.findViewById(R.id.tvSeparatorName);
                return new SeparatorViewHolder(separator, type);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        Item item = items.get(position);
        final Resources resources=viewHolder.itemView.getResources();


        if (item.isTask()) {
            viewHolder.itemView.setEnabled(true);
            final ModelTask task = (ModelTask) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;
            final View itemView=taskViewHolder.itemView;
            taskViewHolder.title.setText(task.getTitle());

            itemView.setVisibility(View.VISIBLE);
            taskViewHolder.priority.setEnabled(true);
            taskViewHolder.time.setText(Utils.getTime(task.getDate()));
            taskViewHolder.date.setText(Utils.getDate(task.getDate()));
            taskViewHolder.description.setText(task.getDescription());
            if(task.getAlarm()==1)
                taskViewHolder.AlarmImg.setVisibility(View.VISIBLE);
            if(task.getRepeat()!=0){
                taskViewHolder.RepeatImg.setVisibility(View.VISIBLE);
            }

            if(taskViewHolder.description.length()==0){
                taskViewHolder.showDesc.setEnabled(false);
                taskViewHolder.showDesc.setVisibility(View.INVISIBLE);
            } else {taskViewHolder.showDesc.setEnabled(true);
                taskViewHolder.showDesc.setVisibility(View.VISIBLE);
            }

            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
            taskViewHolder.imCircle.setImageResource(task.getImage());

//            switch (task.getPriority()){
//                case 0: taskViewHolder.imCircle.setImageDrawable(resources.getDrawable(R.drawable.home));
//                case 1: taskViewHolder.imCircle.setImageResource(R.drawable.work);
//                case 2: taskViewHolder.imCircle.setImageResource(R.drawable.sport);
//                case 3: taskViewHolder.imCircle.setImageResource(R.drawable.pokupki);
//            }

            taskViewHolder.showDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(taskViewHolder.description.getVisibility()==View.GONE){
                        taskViewHolder.showDesc.setImageDrawable(resources.getDrawable(R.drawable.drop_up_arrow));
                        taskViewHolder.imgDesc.setVisibility(View.VISIBLE);
                        taskViewHolder.description.setText(task.getDescription());
                        taskViewHolder.description.setVisibility(View.VISIBLE);
                    }
                    else {
                        taskViewHolder.showDesc.setImageDrawable(resources.getDrawable(R.drawable.drop_down_arrow));
                        taskViewHolder.description.setVisibility(View.GONE);
                        taskViewHolder.imgDesc.setVisibility(View.GONE);
                    }
                }
            });



            if(task.getDate()!=0 && task.getDate()< Calendar.getInstance().getTimeInMillis()){
                itemView.setBackgroundColor(resources.getColor(R.color.taskoverduebackground));
            } else itemView.setBackgroundColor(resources.getColor(R.color.gray_50));




            taskViewHolder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(viewHolder.itemView.getContext(), taskViewHolder.menu);
                    popupMenu.inflate(R.menu.menu_task);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.done_task:{
                                    taskViewHolder.priority.setEnabled(false);
                                    task.setStatus(ModelTask.STATUS_DONE);
                                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(), ModelTask.STATUS_DONE);

                                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(),ModelTask.STATUS_DONE);


//
//                                    taskViewHolder.title.setTextColor(resources.getColor(R.color.disabled_text_color));
//                                    taskViewHolder.date.setTextColor(resources.getColor(R.color.disabled_secondary_text_color));
                                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
                                    itemView.setBackgroundColor(resources.getColor(R.color.colorAccent));

                                    ObjectAnimator flipIn= ObjectAnimator.ofFloat(taskViewHolder.priority,"rotationY",-180f,0f);
                                    flipIn.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            if (task.getStatus() == ModelTask.STATUS_DONE) {
//                                                taskViewHolder.priority.setImageResource(R.drawable.baseline_check_circle_white_48);

                                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,
                                                        "translationX", 0f, itemView.getWidth());

                                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,
                                                        "translationX", itemView.getWidth(), 0f);


                                                translationX.addListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        itemView.setVisibility(View.GONE);
                                                        getTaskFragment().moveTask(task);
                                                        removeItem(taskViewHolder.getLayoutPosition());
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animation) {

                                                    }
                                                });

                                                AnimatorSet translationSet = new AnimatorSet();
                                                translationSet.play(translationX).before(translationXBack);
                                                translationSet.start();
                                            }

                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    flipIn.start();
                                    if(getItemCount()==0){

                                    }
                                }
                                    return true;
                                case R.id.delete_task:
                                {Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getTaskFragment().removeTaskDialog(taskViewHolder.getLayoutPosition());
                                        }
                                    }, 0);


                                    return true;}
                                case R.id.share_task:
                                    String shareBody;
                                    if(taskViewHolder.description.length()==0){
                                    shareBody = taskViewHolder.title.getText().toString()
                                            + "\n Date: " + taskViewHolder.date.getText().toString()+
                                            "\n Time:" + taskViewHolder.time.getText().toString();}
                                            else {shareBody = taskViewHolder.title.getText().toString()
                                            + "\n Date: " + taskViewHolder.date.getText().toString()+
                                            "\n Time:" + taskViewHolder.time.getText().toString()+
                                            "\n Description" + taskViewHolder.description.getText().toString();}
                                   Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                                    shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Densetsu");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                viewHolder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share with"));

                                    return true;
                                case R.id.edit:
                                    getTaskFragment().showTaskEditDialog(task);
                                    return true;
                                case R.id.copy_task:
                                    ClipboardManager myClipboard = (ClipboardManager) viewHolder.itemView.getContext().getSystemService(CLIPBOARD_SERVICE);
                                    String text = taskViewHolder.title.getText().toString()
                                            + "\n Date: " + taskViewHolder.date.getText().toString();

                                    ClipData myClip = ClipData.newPlainText("text", text);
                                    myClipboard.setPrimaryClip(myClip);

                                    Toast.makeText(viewHolder.itemView.getContext(), "Copied", Toast.LENGTH_LONG).show();
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });
//
//            taskViewHolder.title.setTextColor(resources.getColor(R.color.text_color));
//            taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_color));

//            taskViewHolder.description.setText(task.getDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTaskFragment().showTaskEditDialog(task);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getTaskFragment().removeTaskDialog(taskViewHolder.getLayoutPosition());
                        }
                    }, 1000);


                    return true;
                }
            });

            taskViewHolder.priority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskViewHolder.priority.setEnabled(false);
                    task.setStatus(ModelTask.STATUS_DONE);
                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(), ModelTask.STATUS_DONE);

                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(),ModelTask.STATUS_DONE);


//
//                    taskViewHolder.title.setTextColor(resources.getColor(R.color.disabled_text_color));
//                    taskViewHolder.date.setTextColor(resources.getColor(R.color.disabled_secondary_text_color));
                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
                    itemView.setBackgroundColor(resources.getColor(R.color.colorAccent));

                    ObjectAnimator flipIn= ObjectAnimator.ofFloat(taskViewHolder.priority,"rotationY",-180f,0f);
                    flipIn.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (task.getStatus() == ModelTask.STATUS_DONE) {
//                                taskViewHolder.priority.setImageResource(R.drawable.baseline_check_circle_white_48);

                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,
                                        "translationX", 0f, itemView.getWidth());

                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,
                                        "translationX", itemView.getWidth(), 0f);


                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        itemView.setVisibility(View.GONE);
                                        getTaskFragment().moveTask(task);
                                        removeItem(taskViewHolder.getLayoutPosition());
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });

                                AnimatorSet translationSet = new AnimatorSet();
                                translationSet.play(translationX).before(translationXBack);
                                translationSet.start();
                            }

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    flipIn.start();
                }
            });

        } else {

            ModelSeparator separator = (ModelSeparator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) viewHolder;

            separatorViewHolder.type.setText(resources.getString(separator.getType()));
        }

    }



    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) {
            return TYPE_TASK;
        } else {
            return TYPE_SEPARATOR;
        }
    }

}











