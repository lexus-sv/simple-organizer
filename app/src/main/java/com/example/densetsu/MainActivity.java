package com.example.densetsu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.densetsu.accessory.AboutActivity;
import com.example.densetsu.accessory.GuideActivity;
import com.example.densetsu.accessory.SettingsActivity;
import com.example.densetsu.adapter.TabAdapter;
import com.example.densetsu.alarm.AlarmHelper;
import com.example.densetsu.database.DBHelper;
import com.example.densetsu.dialog.AddingTaskDialogFragment;
import com.example.densetsu.dialog.EditTaskDialogFragment;
import com.example.densetsu.dialog.SetRepeatingDialogFragment;
import com.example.densetsu.fragment.CurrentTaskFragment;
import com.example.densetsu.fragment.DoneTaskFragment;
import com.example.densetsu.fragment.TaskFragment;
import com.example.densetsu.model.ModelTask;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements AddingTaskDialogFragment.AddingTaskListener,
        CurrentTaskFragment.OnTaskDoneListener,
        DoneTaskFragment.OnTaskRestoreListener,
        EditTaskDialogFragment.EditingTaskListener, NavigationView.OnNavigationItemSelectedListener,
        CurrentTaskFragment.PassListListener
         {


    private DrawerLayout drawerLayout;
    FragmentManager fragmentManager;
    public List<Event> calendarEvents = new ArrayList<>();
    private SimpleDateFormat dateFormatFull = new SimpleDateFormat("dd MMM- yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM- yyyy", Locale.getDefault());

    int lastCommand = 0;
    TabAdapter tabAdapter;
    PreferenceHelper preferenceHelper;
    public CompactCalendarView compactCalendarView;

    TaskFragment currentTaskFragment;
    TaskFragment doneTaskFragment;
    SearchView searchView;

    public DBHelper dbHelper;
    NavigationView navigationView;

    boolean ccvState;

    ImageButton nav_calendar;

    Constant constant;
    SharedPreferences.Editor editor;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AlarmHelper.getInstance().init(getApplicationContext());


        dbHelper=new DBHelper(getApplicationContext());

        fragmentManager = getSupportFragmentManager();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView nav_date = (TextView) hView.findViewById(R.id.navigation_date);
        nav_calendar = (ImageButton) hView.findViewById(R.id.header_calendar);
        Date date = new Date();
        nav_date.setText(dateFormatFull.format(date));

        if(savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_tasks);
        }
        runSplash();

        setUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.help:
                startActivity(new Intent(this, GuideActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
                default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    public void runSplash() {
//        if(!preferenceHelper.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE)) {
//            SplashActivity splashActivity = new SplashActivity();
//
//            fragmentManager.beginTransaction()
//                    .replace(R.id.container, splashActivity)
//                    .addToBackStack(null)
//                    .commit();
//        }

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(lastCommand == 1){
            currentTaskFragment.showAllTasks();
            doneTaskFragment.showAllTasks();
            navigationView.getMenu().getItem(0).setChecked(true);
        lastCommand=0;} else {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();}}
    }

    public void setUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.current_task));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.done_task));
        final Switch searchSwitch = (Switch) findViewById(R.id.search_switch);
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        ccvState = false;
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        final TextView month = (TextView) findViewById(R.id.monthText);
        month.setText(dateFormatMonth.format(new Date()));

        nav_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(compactCalendarView.getVisibility()==View.INVISIBLE){
                    compactCalendarView.setVisibility(View.VISIBLE); }
                if(!ccvState) {
                    ccvState=!ccvState;
                    month.setVisibility(View.VISIBLE);
                    compactCalendarView.showCalendar();
                    searchSwitch.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {ccvState=!ccvState;
                    compactCalendarView.hideCalendar();
                    month.setVisibility(View.INVISIBLE);
                    searchSwitch.setVisibility(View.INVISIBLE);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        final ImageButton ccvOpen = (ImageButton) findViewById(R.id.calendarButton);
        ccvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(compactCalendarView.getVisibility()==View.INVISIBLE)
                    compactCalendarView.setVisibility(View.VISIBLE);
//                Intent i = new Intent(getApplicationContext(), CalendarActivity.class);
//                startActivity(i);
                if(!ccvState) {ccvState=!ccvState;
                month.setVisibility(View.VISIBLE);
                searchSwitch.setVisibility(View.VISIBLE);
                compactCalendarView.showCalendar();
                }
                else {ccvState=!ccvState;
                    compactCalendarView.hideCalendar();
                    searchSwitch.setVisibility(View.INVISIBLE);
                month.setVisibility(View.INVISIBLE);
                }
            }
        });
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                if(compactCalendarView.getEvents(dateClicked).isEmpty()){
                    AddingTaskDialogFragment fragment = AddingTaskDialogFragment.newInstance(dateClicked.getTime());
                    fragment.show(fragmentManager, "AddingTaskDialogFragment");
                    compactCalendarView.hideCalendar();
                }
                    else {if(searchSwitch.isChecked()){
                        currentTaskFragment.findTasksByDate(dateClicked.getTime());
                        doneTaskFragment.findTasksByDate(dateClicked.getTime());
                    compactCalendarView.hideCalendarWithAnimation();
                    month.setVisibility(View.INVISIBLE);
                    searchSwitch.setVisibility(View.INVISIBLE);
                    ccvState=!ccvState;
                    lastCommand=1;}
                    else {
                    AddingTaskDialogFragment fragment = AddingTaskDialogFragment.newInstance(dateClicked.getTime());
                    fragment.show(fragmentManager, "AddingTaskDialogFragment");
                    compactCalendarView.hideCalendar();
                }
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                month.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        tabAdapter = new TabAdapter(fragmentManager, 2);

        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }



        });
        currentTaskFragment = (CurrentTaskFragment) tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION);
        doneTaskFragment = (DoneTaskFragment) tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION);
        searchView = (SearchView) findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                currentTaskFragment.findTasks(newText);
                doneTaskFragment.findTasks(newText);
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addingTaskDialogFragment = new AddingTaskDialogFragment();
                addingTaskDialogFragment.show(fragmentManager, "AddingTaskDialogFragment");

            }
        });

    }

    @Override
    public void onTaskAdded(ModelTask newTask) {
        Log.d("mainActivity task add","mainActivity task adding");
        currentTaskFragment.addTask(newTask, true);
        Log.d("*******************","timestamp = "+ newTask.getDate());
        calendarEvents.add(new Event(Color.GREEN, newTask.getDate()));
        compactCalendarView.removeAllEvents();
        compactCalendarView.addEvents(calendarEvents);
        Log.d("data", "date of task = "+new Date(newTask.getDate()));

    }

    @Override
    public void onTaskAddingCancel() {
        Toast.makeText(this, R.string.toast_adding_cancel, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onTaskDone(ModelTask task) {
        doneTaskFragment.addTask(task,false);
        for(int i = 0; i<calendarEvents.size();i++){
            if(calendarEvents.get(i).getTimeInMillis()==task.getDate()){
                calendarEvents.remove(i);
            }
        }
        compactCalendarView.removeAllEvents();
            compactCalendarView.addEvents(calendarEvents);


    }

    @Override
    public void onTaskRestore(ModelTask task) {
        currentTaskFragment.addTask(task, false);
        calendarEvents.add(new Event(Color.WHITE,task.getDate()));
        compactCalendarView.removeAllEvents();
        compactCalendarView.addEvents(calendarEvents);
    }

    @Override
    public void onTaskEdited(ModelTask updatedTask, long date) {
        Toast.makeText(this, R.string.toast_task_edited, Toast.LENGTH_LONG).show();
        currentTaskFragment.updateTask(updatedTask);
        dbHelper.update().task(updatedTask);
        for(int i=0;i<calendarEvents.size();i++){
            if(calendarEvents.get(i).getTimeInMillis()== date)
                calendarEvents.remove(i);
        }
        compactCalendarView.removeAllEvents();
        compactCalendarView.addEvents(calendarEvents);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_tasks:
//                startActivity(new Intent(this, MainActivity.class));
                currentTaskFragment.showAllTasks();
                doneTaskFragment.showAllTasks();
                return true;
            case R.id.nav_guide:
                startActivity(new Intent(this, GuideActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_info:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_share:
                Toast.makeText(getApplicationContext(), "Shared", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.home_tasks:
                currentTaskFragment.findTasksByCategory(0);
                doneTaskFragment.findTasksByCategory(0);
                lastCommand = 1;
                return true;
            case R.id.work_tasks:
                currentTaskFragment.findTasksByCategory(1);
                doneTaskFragment.findTasksByCategory(1);
                lastCommand = 1;
                return true;
            case R.id.sport_tasks:
                currentTaskFragment.findTasksByCategory(2);
                doneTaskFragment.findTasksByCategory(2);
                lastCommand = 1;
                return true;
            case R.id.shopping_tasks:
                currentTaskFragment.findTasksByCategory(3);
                doneTaskFragment.findTasksByCategory(3);
                lastCommand = 1;
                return true;


        }
//        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
             @Override
             public void passList(List<Event> events) {
        compactCalendarView.removeAllEvents();
        calendarEvents.clear();

                 for(int i=0;i<events.size();i++){
                     calendarEvents.add(i, events.get(i));
                 }
                 compactCalendarView.addEvents(calendarEvents);

    }
}
