package com.example.densetsu.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.densetsu.R;

public class SetRepeatingDialogFragment extends AppCompatDialogFragment implements CustomRepeatDialogFragment.CustomRepeatListener {
    int radioId;
    RadioGroup radioGroup;
    RadioButton radioButton;
    private RepeatingDialogListener listener;
    private static final String TAG = "SetRepeatingDialogFragment";
    String repeat;
    String text1;
    long seconds1;

    @Override
    public void applyCustomRepeat(long seconds, String text) {
        radioButton.setText("Custom"+" ("+text+")");
        text1=text;
        seconds1=seconds;
    }

    public interface RepeatingDialogListener{
        void applyRepeat(String repeat, int id, long seconds, String text);
    }

    public static SetRepeatingDialogFragment newInstance(int id){
        SetRepeatingDialogFragment fragment = new SetRepeatingDialogFragment();
        Bundle args = new Bundle();
        args.putInt("id_key", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (RepeatingDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement RepeatingDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.repeat_dialog,null);
        radioGroup = view.findViewById(R.id.radioGroup);
        if(getArguments() != null){
            radioId = getArguments().getInt("id_key");
            radioButton = view.findViewById(radioId);
            radioButton.setChecked(true);
        }
        builder.setView(view)
                .setTitle("Chose")
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        radioId = radioGroup.getCheckedRadioButtonId();
                        radioButton = view.findViewById(radioId);
                        repeat = radioButton.getText().toString();
                        listener.applyRepeat(repeat, radioId, seconds1, text1);
                        Log.d("setRepeating", "Seconds1= "+text1);
                    }
                });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                if(radioId==R.id.radio_custom){
                    radioButton = view.findViewById(radioId);
                    CustomRepeatDialogFragment fragment = new CustomRepeatDialogFragment();
                    fragment.setTargetFragment(SetRepeatingDialogFragment.this, 1);
                    fragment.show(getFragmentManager(),"CustomRepeatDialogFragment");
                    repeat = radioButton.getText().toString();
                } else {
                radioButton = view.findViewById(radioId);
                repeat = radioButton.getText().toString();
                }
            }
        });

        return builder.create();
    }


}
