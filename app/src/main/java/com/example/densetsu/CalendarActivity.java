package com.example.densetsu;

import android.content.Context;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.densetsu.dialog.AddingTaskDialogFragment;
import com.example.densetsu.model.Item;
import com.example.densetsu.model.ModelTask;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    PreferenceHelper preferenceHelper;
    FragmentManager fragmentManager;

    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM- yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceHelper = new PreferenceHelper(this);
        if(preferenceHelper.loadNightModeState()==true) {
            setTheme(R.style.darkTheme);
        }
        else  setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Date date = new Date();
        fragmentManager = getSupportFragmentManager();
        final TextView textView = (TextView) findViewById(R.id.monthView);
        textView.setText(dateFormatMonth.format(date));






//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setTitle(null);

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        //Set an event for Teachers' Professional Day 2016 which is 21st of October

//        Event ev1 = new Event(Color.RED, 1554911710000L, "Teachers' Professional Day");
//        compactCalendar.addEvent(ev1);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                textView.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }
}
