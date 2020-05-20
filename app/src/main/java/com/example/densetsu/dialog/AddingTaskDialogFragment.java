package com.example.densetsu.dialog;


import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.widget.Toast;

import com.example.densetsu.Constant;
import com.example.densetsu.MainActivity;
import com.example.densetsu.PreferenceHelper;
import com.example.densetsu.R;
import com.example.densetsu.Utils;
import com.example.densetsu.alarm.AlarmHelper;
import com.example.densetsu.customSpinner.MarkerAdapter;
import com.example.densetsu.customSpinner.MarkerItem;
import com.example.densetsu.model.ModelTask;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

//Класс,в котором всё реализуется

public class AddingTaskDialogFragment extends DialogFragment implements SetRepeatingDialogFragment.RepeatingDialogListener
//        ,
////        CustomRepeatDialogFragment.CustomRepeatListener
{
    private static final String TAG = "AddingTaskDialogFragment";

    String DATE_KEY = "dateKey";
    Intent shareIntent;
    Toolbar toolbar;
    PreferenceHelper preferenceHelper;
    private ArrayList<MarkerItem> markerItems;
    private MarkerAdapter markerAdapter;
    long inputDate;
    TextView tvRepeat;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    EditText etTitle;
    boolean keyb;
    long repPeriod;
    String repText;
    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;


    private AddingTaskListener addingTaskListener;

    public static AddingTaskDialogFragment newInstance(long inputDate){
        AddingTaskDialogFragment fragment = new AddingTaskDialogFragment();
        Bundle args = new Bundle();
        args.putLong("dateKey", inputDate);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void applyCustomRepeat(long seconds, String text) {
//        repText = text;
//        tvRepeat.setText(seconds+"");
//        repPeriod = seconds;
//    }


    public interface AddingTaskListener {
        void onTaskAdded(ModelTask newTask);
        void onTaskAddingCancel();
    }


    public void openDialog(){
        SetRepeatingDialogFragment setRepeatingDialogFragment = new SetRepeatingDialogFragment();
        setRepeatingDialogFragment.show(getFragmentManager(),"RepeatingDialog");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            addingTaskListener = (AddingTaskListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddingTaskListener");
        }
    }
    @Override
    public void applyRepeat(String repeat, int id, long seconds, String text) {
        tvRepeat.setText(repeat);
        repText = text;
        repPeriod = seconds;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        final AlertDialog.Builder builder;//= new AlertDialog.Builder(getActivity());
        if (themeColor == 0){
            builder = new AlertDialog.Builder(getActivity(), Constant.theme);
        } else if (appTheme==0){
            builder = new AlertDialog.Builder(getActivity(), Constant.theme);
        } else builder = new AlertDialog.Builder(getActivity(), appTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View container = inflater.inflate(R.layout.activity_adding_task, null);
        if(getArguments() != null){
            inputDate = getArguments().getLong(DATE_KEY);
        }

        toolbar = (Toolbar) container.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.dialog_title));
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                Intent i = new Intent(getContext(), MainActivity.class);
                startActivity(i);
            }
        });
        initList();
        final FloatingActionButton fab = (FloatingActionButton) container.findViewById(R.id.addButton);
        /////////////////////////////////////////////////////////////
        tvRepeat = container.findViewById(R.id.tvRepeat);
        final ImageView imRepeat = (ImageView) container.findViewById(R.id.repeat);
        /////////////////////////////////////////////////////////////////

        final TextInputLayout tilTitle = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTitle);
        etTitle = tilTitle.getEditText();

        TextInputLayout tilDate = (TextInputLayout) container.findViewById(R.id.tilDialogTaskDate);
        final EditText etDate = tilDate.getEditText();
        final ImageButton keyboardButton = (ImageButton) container.findViewById(R.id.keyboard);
        keyb = false;
        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyb){
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
                    keyb=!keyb;
                }
                else {
                    InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    keyb=!keyb;
                }
            }
        });

        final TextInputLayout tilTime = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTime = tilTime.getEditText();

        final TextInputLayout tilDescription = (TextInputLayout) container.findViewById(R.id.tilDialogTaskDescription);
        final EditText etDescription = tilDescription.getEditText();

        final Switch switchNotify = (Switch) container.findViewById(R.id.switchNotify);
        TextView notifyMe = (TextView) container.findViewById(R.id.tvNotify);
        final ImageView alarmOn = (ImageView) container.findViewById(R.id.alarm_on);
        final ImageView alarmOff = (ImageView) container.findViewById(R.id.alarm_off);

        ImageButton voice = (ImageButton) container.findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        TextView marker = (TextView) container.findViewById(R.id.marker);
        Spinner spPriority=(Spinner) container.findViewById(R.id.spDialogTaskPriority);
        ImageView imMarker = (ImageView) container.findViewById(R.id.imMarker);

        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));
        tilDescription.setHint(getResources().getString(R.string.enter_description));
        builder.setView(container);
        final Date date = new Date();


        final Calendar calendar=Calendar.getInstance();

        final ModelTask task=new ModelTask();
//
//        ArrayAdapter<String> priorityAdapter=new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_spinner_dropdown_item,ModelTask.PRIORITY_LEVELS);
//        spPriority.setAdapter(priorityAdapter);
        markerAdapter = new MarkerAdapter(getActivity(), markerItems);
        spPriority.setAdapter(markerAdapter);
        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)+1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        final int time = calendar.get(Calendar.HOUR_OF_DAY);
        if(getArguments()!=null){
            etDate.setText(Utils.getDate(inputDate));
            etTime.setText(Utils.getTime(inputDate));
        } else {
        etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
        etTime.setText(Utils.getTime(calendar.getTimeInMillis()));}


        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if(calendar.getTime().before(new Date())){
                    Toast.makeText(getActivity(), getResources().getText(R.string.dialog_data_in_past),
                            Toast.LENGTH_LONG).show();
                    etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
                } else {
                etDate.setText(Utils.getDate(calendar.getTimeInMillis()));}
            }
        };
        final TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                if(calendar.getTime().before(new Date())){
                    Toast.makeText(getActivity(), getResources().getText(R.string.dialog_data_in_past),
                            Toast.LENGTH_LONG).show();
                    etTime.setText(Utils.getTime(calendar.getTimeInMillis()));
                }
                else {etTime.setText(Utils.getTime(calendar.getTimeInMillis()));}
            }
        };

        //Реализация

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDate.length() == 0) {
                    etDate.setText(" ");
                }


                DatePickerDialog dpd = new DatePickerDialog(getActivity(), dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();




            }
        });
        final String NO_REPEATING = getString(R.string.no_repeating);
        final String EVERY_HOUR = getString(R.string.every_hour);
        final String EVERY_DAY = getString(R.string.every_day);
        final String EVERY_WEEK = getString(R.string.every_week);
        final String EVERY_MONTH = getString(R.string.every_month);
        final String EVERY_YEAR = getString(R.string.every_year);

        tvRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {if(tvRepeat.getText()==getString(R.string.no_repeating)){
                SetRepeatingDialogFragment fragment = new SetRepeatingDialogFragment();
                fragment.setTargetFragment(AddingTaskDialogFragment.this,1);
                fragment.show(getFragmentManager(),"SetRepeatingDialogFragment"); }
                 if(tvRepeat.getText()==EVERY_HOUR){
                    SetRepeatingDialogFragment fragment = SetRepeatingDialogFragment.newInstance(R.id.radio_2);
                    fragment.setTargetFragment(AddingTaskDialogFragment.this, 1);
                    fragment.show(getFragmentManager(), "SetRepeatingDialogFragment");
            }
                if(tvRepeat.getText()==EVERY_DAY){
                    SetRepeatingDialogFragment fragment = SetRepeatingDialogFragment.newInstance(R.id.radio_3);
                    fragment.setTargetFragment(AddingTaskDialogFragment.this, 1);
                    fragment.show(getFragmentManager(), "SetRepeatingDialogFragment");
                }
                if(tvRepeat.getText()==EVERY_WEEK){
                    SetRepeatingDialogFragment fragment = SetRepeatingDialogFragment.newInstance(R.id.radio_4);
                    fragment.setTargetFragment(AddingTaskDialogFragment.this, 1);
                    fragment.show(getFragmentManager(), "SetRepeatingDialogFragment");
                }
                if(tvRepeat.getText()==EVERY_MONTH){
                    SetRepeatingDialogFragment fragment = SetRepeatingDialogFragment.newInstance(R.id.radio_5);
                    fragment.setTargetFragment(AddingTaskDialogFragment.this, 1);
                    fragment.show(getFragmentManager(), "SetRepeatingDialogFragment");
                }
                if(tvRepeat.getText()==EVERY_YEAR){
                    SetRepeatingDialogFragment fragment = SetRepeatingDialogFragment.newInstance(R.id.radio_6);
                    fragment.setTargetFragment(AddingTaskDialogFragment.this, 1);
                    fragment.show(getFragmentManager(), "SetRepeatingDialogFragment");
                }
                if(tvRepeat.getText()!=NO_REPEATING&&tvRepeat.getText()!=EVERY_HOUR
                        &&tvRepeat.getText()!=EVERY_DAY&&tvRepeat.getText()!=EVERY_WEEK
                        &&tvRepeat.getText()!=EVERY_MONTH&&tvRepeat.getText()!=EVERY_YEAR){
                    SetRepeatingDialogFragment fragment = SetRepeatingDialogFragment.newInstance(R.id.radio_custom);
                    fragment.setTargetFragment(AddingTaskDialogFragment.this, 1);
                    fragment.show(getFragmentManager(),"SetRepeatingDialogFragment");
                }
            }
        });
        switchNotify.setChecked(true);
        tvRepeat.setLongClickable(false);
        switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    alarmOff.setVisibility(View.INVISIBLE);
                    alarmOn.setVisibility(View.VISIBLE);
                    tvRepeat.setClickable(true);
//                    tvRepeat.setVisibility(View.VISIBLE);
//                    imRepeat.setVisibility(View.VISIBLE);

                } else {alarmOn.setVisibility(View.INVISIBLE);
                    alarmOff.setVisibility(View.VISIBLE);
                    tvRepeat.setClickable(false);
//                    tvRepeat.setVisibility(View.GONE);
//                    imRepeat.setVisibility(View.GONE);
                    }
            }
        });



        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTime.length() == 0) {
                    etTime.setText("");
                }
                TimePickerDialog tpd= new TimePickerDialog(getActivity(),timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),true);
                tpd.show();
            }
        });


//        btnShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Densetsu");
//                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//                startActivity(Intent.createChooser(shareIntent, "Share with"));
//            }
//        });

//        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                task.setTitle(etTitle.getText().toString());
//                task.setStatus(ModelTask.STATUS_CURRENT);
//
//                task.setDescription(etDescription.getText().toString());
//
//                if (etDate.length() != 0 || etTime.length() != 0) {
//                    task.setDate(calendar.getTimeInMillis());
//
//                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
//                    alarmHelper.setAlarm(task);
//                }
//                Log.d("task add", etDescription.getText().toString());
//                task.setStatus(ModelTask.STATUS_CURRENT);
//                Log.d("task add","task adding");
//                addingTaskListener.onTaskAdded(task);
//                dialog.dismiss();
//            }
//        });

//        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                addingTaskListener.onTaskAddingCancel();
//                dialog.cancel();
//            }
//        });
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if (etTitle.length() == 0) {
                    fab.setEnabled(false);
                    etTitle.setHint(getResources().getString(R.string.hint));
                }

                etTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            fab.setEnabled(false);
                            etTitle.setHint(getResources().getString(R.string.hint));
                        } else {
                            fab.setEnabled(true);
                            tilTitle.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = etTitle.getText().toString();
                        if(s.trim().length()!=0){
                            task.setTitle(s.trim());
                            task.setStatus(ModelTask.STATUS_CURRENT);

                            task.setDescription(etDescription.getText().toString());


                            if(tvRepeat.getText()==NO_REPEATING){
                                task.setRepeat(0);
                            }
                            if(tvRepeat.getText()==EVERY_HOUR){
                                task.setRepeat(AlarmManager.INTERVAL_HOUR);
                            }
                            if(tvRepeat.getText()==EVERY_DAY){
                                task.setRepeat(AlarmManager.INTERVAL_DAY);
                            }
                            if(tvRepeat.getText()==EVERY_WEEK){
                                task.setRepeat(AlarmManager.INTERVAL_DAY*7);
                            }
                            if(tvRepeat.getText()==EVERY_MONTH){
                                task.setRepeat(AlarmManager.INTERVAL_DAY*31);
                            }
                            if(tvRepeat.getText()==EVERY_YEAR){
                                task.setRepeat(AlarmManager.INTERVAL_DAY*365);
                            }
                            if(tvRepeat.getText()!=NO_REPEATING&&tvRepeat.getText()!=EVERY_HOUR
                                    &&tvRepeat.getText()!=EVERY_DAY&&tvRepeat.getText()!=EVERY_WEEK
                                    &&tvRepeat.getText()!=EVERY_MONTH&&tvRepeat.getText()!=EVERY_YEAR){
                                task.setRepeat(repPeriod);
                            }
                            Log.d("task add", "task.getRepeat()= "+task.getRepeat());
                            Log.d("task add", "repPeriod= "+repPeriod);
                            Log.d("task add", "repText= "+repText);
                            if (etDate.length() != 0 || etTime.length() != 0) {
                                calendar.set(Calendar.SECOND,0);
                                calendar.set(Calendar.MILLISECOND,0);
                                Date newDate = new Date(calendar.getTimeInMillis());
                                newDate = dateTrim(newDate);
                                Log.d("Loggg","newDate= "+newDate);
                                task.setDay(newDate.getTime());
                                task.setDate(calendar.getTimeInMillis());

                                if(switchNotify.isChecked()&&task.getDate()>new Date().getTime()){
                                    if(task.getDate()>new Date().getTime())
                                        task.setAlarm(1);
                                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                                    alarmHelper.setAlarm(task);
                                } else task.setAlarm(0);
                            }
                            Log.d("task add", "task.day= "+task.getDay() + "date ="+new Date(task.getDay()));

                            if(task.getDate()<new Date().getTime()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Warning")
                                    .setMessage(R.string.dateConfirm)
                                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            task.setStatus(ModelTask.STATUS_CURRENT);
                                            Log.d("task add","task adding");
                                            addingTaskListener.onTaskAdded(task);
                                            alertDialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(R.string.dialog_cancel,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();}
                            else {
                            task.setStatus(ModelTask.STATUS_CURRENT);
                            Log.d("task add","task adding");
                            addingTaskListener.onTaskAdded(task);

                            alertDialog.dismiss();}
                        }
                        else Toast.makeText(getContext(), R.string.toast_no_text, Toast.LENGTH_LONG).show();
//                        task.setTitle(etTitle.getText().toString());

                    }
                });

            }
        });

        return alertDialog;
    }


    private void initList(){
        markerItems = new ArrayList<>();

        markerItems.add(new MarkerItem(R.drawable.home, getString(R.string.home)));
        markerItems.add(new MarkerItem(R.drawable.work, getString(R.string.work)));
        markerItems.add(new MarkerItem( R.drawable.sport, getString(R.string.sport)));
        markerItems.add(new MarkerItem( R.drawable.pokupki, getString(R.string.shopping)));
//        markerItems.add(new MarkerItem(ResourcesCompat.getColor(getResources(), R.color.priority_5, null)));
//        markerItems.add(new MarkerItem(ResourcesCompat.getColor(getResources(), R.color.priority_6, null)));
//        markerItems.add(new MarkerItem(ResourcesCompat.getColor(getResources(), R.color.priority_7, null)));
//        markerItems.add(new MarkerItem(ResourcesCompat.getColor(getResources(), R.color.priority_8, null)));
//        markerItems.add(new MarkerItem(ResourcesCompat.getColor(getResources(), R.color.priority_9, null)));
//        markerItems.add(new MarkerItem( ResourcesCompat.getColor(getResources(), R.color.priority_10, null)));
//        markerItems.add(new MarkerItem( ResourcesCompat.getColor(getResources(), R.color.priority_11, null)));
//        markerItems.add(new MarkerItem( ResourcesCompat.getColor(getResources(), R.color.priority_12, null)));

    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etTitle.setText(result.get(0));
                }
                break;
            }

        }
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
