package com.example.densetsu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.densetsu.R;

public class CustomRepeatDialogFragment extends DialogFragment {
    private static final String TAG = "CustomRepeatDialogFragment";
    CustomRepeatListener listener;
    EditText et_number;
    Spinner spinner;
    long customRepeat=0;
    String customRepeatText;
    int spinPos;

    public interface CustomRepeatListener{
        void applyCustomRepeat(long seconds, String text);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CustomRepeatListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement RepeatingDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.user_repeat_dialog, null);
        spinner = (Spinner) view.findViewById(R.id.units_spinner);
        et_number = (EditText) view.findViewById(R.id.et_units);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        customRepeat = 0;
//        et_number.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                if(et_number.length()!=0)
//                listener.applyCustomRepeat(customRepeat,customRepeatText);
//                Log.d("rep", "customrep= "+customRepeat);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position){
////                    case 0:spinPos=position;
////                        customRepeat= Long.parseLong(et_number.getText().toString())*60*1000L;
////                        Log.d("rep", "customrep= "+customRepeat);
////                        customRepeatText = "Every " + et_number.getText().toString()
////                                +" " + spinner.getItemAtPosition(position)+"(s)";
////                        listener.applyCustomRepeat(customRepeat, customRepeatText);
////                    case 1:spinPos=position;
////                        customRepeat= Long.parseLong(et_number.getText().toString())*60*60*1000L;
////                        customRepeatText = "Every " + et_number.getText().toString()
////                                +" " + spinner.getItemAtPosition(position)+"(s)";
////                        listener.applyCustomRepeat(customRepeat, customRepeatText);
////                    case 2:spinPos=position;
////                        customRepeat= Long.parseLong(et_number.getText().toString())*60*60*24*1000L;
////                        customRepeatText = "Every " + et_number.getText().toString()
////                                +" " + spinner.getItemAtPosition(position)+"(s)";
////                        listener.applyCustomRepeat(customRepeat, customRepeatText);
////                    case 3:spinPos=position;
////                        customRepeat= Long.parseLong(et_number.getText().toString())*60*60*24*7*1000L;
////                        customRepeatText = "Every " + et_number.getText().toString()
////                                +" " + spinner.getItemAtPosition(position)+"(s)";
////                        listener.applyCustomRepeat(customRepeat, customRepeatText);
////                    case 4:spinPos=position;
////                        customRepeat= Long.parseLong(et_number.getText().toString())*60*60*24*31*1000L;
////                        customRepeatText = "Every " + et_number.getText().toString()
////                                +" " + spinner.getItemAtPosition(position)+"(s)";
////                        listener.applyCustomRepeat(customRepeat, customRepeatText);
////                    case 5:spinPos=position;
////                        customRepeat= Long.parseLong(et_number.getText().toString())*60*60*24*30*12*1000L;
////                        customRepeatText = "Every " + et_number.getText().toString()
////                                +" " + spinner.getItemAtPosition(position)+"(s)";
////                        listener.applyCustomRepeat(customRepeat, customRepeatText);
////                }
                spinPos=position;
        }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setView(view)
                .setTitle(R.string.custom_repeat_title)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            if(spinPos==0){
                                customRepeat = Long.parseLong(et_number.getText().toString())*60*1000L;
                                Log.d(TAG, "onClick1: customRepeat"+customRepeat);}
                                if(spinPos==1){
                            customRepeat = Long.parseLong(et_number.getText().toString())*60*60*1000L;
                                Log.d(TAG, "onClick2: customRepeat"+customRepeat+"spinpos= "+ spinPos);}
                                if(spinPos==2){
                            customRepeat = Long.parseLong(et_number.getText().toString())*60*60*24*1000L;
                                Log.d(TAG, "onClick3: customRepeat"+customRepeat+"spinpos= "+ spinPos);}
                                if(spinPos==3){
                            customRepeat = Long.parseLong(et_number.getText().toString())*60*60*24*7*1000L;
                                Log.d(TAG, "onClick4: customRepeat"+customRepeat+"spinpos= "+ spinPos);}
                        if(spinPos==4){
                            customRepeat = Long.parseLong(et_number.getText().toString())*60*60*24*31*1000L;
                                Log.d(TAG, "onClick5: customRepeat"+customRepeat+"spinpos= "+ spinPos);}
                            if(spinPos==5){
                            customRepeat = Long.parseLong(et_number.getText().toString())*60*60*24*30*12*1000L;
                                Log.d(TAG, "onClick6: customRepeat"+customRepeat+"spinpos= "+ spinPos);}
                        customRepeatText = "Every " + et_number.getText().toString()
                                +" " + spinner.getItemAtPosition(spinPos)+"(s)";
                        listener.applyCustomRepeat(customRepeat,customRepeatText);
                    }
                });
        return builder.create();
    }
}
