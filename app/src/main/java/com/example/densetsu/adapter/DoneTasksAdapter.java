package com.example.densetsu.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.example.densetsu.fragment.DoneTaskFragment;
import com.example.densetsu.fragment.TaskFragment;
import com.example.densetsu.model.Item;
import com.example.densetsu.model.ModelTask;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class DoneTasksAdapter extends TaskAdapter {

    public AlarmHelper alarmHelper;


    public DoneTasksAdapter(DoneTaskFragment taskFragment) {
        super(taskFragment);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        alarmHelper = AlarmHelper.getInstance();

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_task, viewGroup, false);
        TextView title = (TextView) v.findViewById(R.id.tvTaskTitle);
        TextView date = (TextView) v.findViewById(R.id.tvTaskDate);
        TextView time = (TextView) v.findViewById(R.id.tvTaskTime);
        TextView description = (TextView) v.findViewById(R.id.tvDescription);
        ImageView imgDesc = (ImageView) v.findViewById(R.id.description_image);
        CircleImageView priority = (CircleImageView) v.findViewById(R.id.cvTaskPriority);
        ImageButton buttonViewOption=(ImageButton) v.findViewById(R.id.taskPopup);
        ImageButton showDesc = (ImageButton) v.findViewById(R.id.drop_down);
        ImageView imCircle = (ImageView) v.findViewById(R.id.imCircle);
        ImageView alarm = (ImageView) v.findViewById(R.id.alarm_image);
        ImageView repeat = (ImageView) v.findViewById(R.id.repeat_image);


        return new TaskViewHolder(v, title, date, priority, buttonViewOption, time, description,showDesc,imgDesc, imCircle, alarm,repeat);


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        Item item = items.get(position);

        if (item.isTask()) {
            viewHolder.itemView.setEnabled(true);
            final ModelTask task = (ModelTask) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;

            final View itemView = taskViewHolder.itemView;
            final Resources resources = itemView.getResources();

            taskViewHolder.title.setText(task.getTitle());
            if (task.getDate() != 0) {
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            } else {
                taskViewHolder.date.setText(null);
            }

            itemView.setVisibility(View.VISIBLE);
            taskViewHolder.priority.setEnabled(true);
//            Date time = new Date(task.getDate());
//            DateFormat sdf = new SimpleDateFormat("H:mm");
//            String timeStr = sdf.format(time);
//            taskViewHolder.time.setText(timeStr);
            taskViewHolder.time.setText(Utils.getTime(task.getDate()));
            taskViewHolder.date.setText(Utils.getDate(task.getDate()));
            if(task.getAlarm()==1)
                taskViewHolder.AlarmImg.setVisibility(View.VISIBLE);
            if(task.getRepeat()!=0){
                taskViewHolder.RepeatImg.setVisibility(View.VISIBLE);
            }


            taskViewHolder.description.setText(task.getDescription());

            if(taskViewHolder.description.length()==0){
                taskViewHolder.showDesc.setEnabled(false);
                taskViewHolder.showDesc.setVisibility(View.INVISIBLE);
            } else {taskViewHolder.showDesc.setEnabled(true);
                taskViewHolder.showDesc.setVisibility(View.VISIBLE);
            }
            taskViewHolder.imCircle.setImageResource(task.getImage());
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
                                    task.setStatus(ModelTask.STATUS_CURRENT);
                                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(), ModelTask.STATUS_CURRENT);


//                    taskViewHolder.title.setTextColor(resources.getColor(R.color.disabled_text_color));
//                    taskViewHolder.date.setTextColor(resources.getColor(R.color.disabled_secondary_text_color));
                                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY", 180f, 0f);
//                                    taskViewHolder.priority.setImageResource(R.drawable.ic_checkbox_blank_circle_white_48dp);

                                    flipIn.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            if (task.getStatus() != ModelTask.STATUS_DONE) {

                                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,
                                                        "translationX", 0f, -itemView.getWidth());

                                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,
                                                        "translationX", -itemView.getWidth(), 0f);


                                                translationX.addListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        itemView.setVisibility(View.GONE);
                                                        getTaskFragment().moveTask(task);
                                                        removeItem(taskViewHolder.getLayoutPosition());
                                                        alarmHelper.removeAlarm(task.getTimeStamp());
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

                                    flipIn.start();}
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
                                    Toast.makeText(viewHolder.itemView.getContext(), "Task text copied", Toast.LENGTH_LONG).show();
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });



            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
//            taskViewHolder.priority.setImageResource(R.drawable.baseline_check_circle_white_48);

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
                    task.setStatus(ModelTask.STATUS_CURRENT);
                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(), ModelTask.STATUS_CURRENT);


//                    taskViewHolder.title.setTextColor(resources.getColor(R.color.disabled_text_color));
//                    taskViewHolder.date.setTextColor(resources.getColor(R.color.disabled_secondary_text_color));
                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY", 180f, 0f);
//                    taskViewHolder.priority.setImageResource(R.drawable.ic_checkbox_blank_circle_white_48dp);

                    flipIn.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (task.getStatus() != ModelTask.STATUS_DONE) {

                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,
                                        "translationX", 0f, -itemView.getWidth());

                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,
                                        "translationX", -itemView.getWidth(), 0f);


                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        itemView.setVisibility(View.GONE);
                                        getTaskFragment().moveTask(task);
                                        removeItem(taskViewHolder.getLayoutPosition());
                                        alarmHelper.removeAlarm(task.getTimeStamp());
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
        }

    }
}
