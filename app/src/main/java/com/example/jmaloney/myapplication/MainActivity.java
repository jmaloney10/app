package com.example.jmaloney.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jmaloney.myapplication.calendar.CaldroidSampleCustomFragment;
import com.example.jmaloney.myapplication.common.Record;
import com.example.jmaloney.myapplication.editor.SelectDay;
import com.example.jmaloney.myapplication.settings.SettingsActivity;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.csv.CsvIOFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends FragmentActivity {
    private boolean undo = false;
    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;
    public final static String DATE_MESSAGE = "com.mycompany.myfirstapp.DATEMESSAGE";
    String activityDate = "";
    static public String DATE_FORMAT = "dd MMM yyyy";
    ArrayList<Record> logs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadLogs();
        initializeCalendar(savedInstanceState);
        displayDaySummary();
    }

    public void addNewWorkout(View v){
        Intent intent = new Intent(this, SelectDay.class);
        intent.putExtra(DATE_MESSAGE,activityDate);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void displayDaySummary(){
        SimpleDateFormat formatter = new SimpleDateFormat(MainActivity.DATE_FORMAT);

        try {
            for (Record record: logs) {
                if (record.date.equals(formatter.parse(activityDate))) {
                    editorPane();
                    return;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        addNewPane();
    }

    public void editorPane(){

    }

    public void addNewPane(){
        ScrollView summaryPane = (ScrollView) findViewById(R.id.summary_pane);

        TextView addNewView = new TextView(this);
        addNewView.setText("Add Workout");
        addNewView.setTextSize(55);
        addNewView.setGravity(Gravity.CENTER);
        addNewView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addNewView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addNewWorkout(v);
            }
        });

        summaryPane.removeAllViewsInLayout();
        summaryPane.addView(addNewView);
    }

    public void loadLogs(){
        FileInputStream fis = null;
        BufferedReader bfr = null;
        if (logs == null) {
            logs = new ArrayList<Record>();
        } else {
            logs.clear();
        }

        Toast.makeText(getApplicationContext(), "load logs called",
                Toast.LENGTH_SHORT).show();

        try {
            fis = openFileInput("workout_log");
            bfr = new BufferedReader(new InputStreamReader(fis));


            Deserializer deserializer = CsvIOFactory.createFactory(Record.class).createDeserializer();
            deserializer.open(bfr);
            while (deserializer.hasNext()) {
                Record p = deserializer.next();
                logs.add(p);
            }
            deserializer.close(true);


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void initializeCalendar(Bundle savedInstanceState) {
        final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        //caldroidFragment = new CaldroidFragment();

        caldroidFragment = new CaldroidSampleCustomFragment();

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            args.putParcelableArrayList("logs",logs);

            // Uncomment this to customize startDayOfWeek
            // args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
            // CaldroidFragment.TUESDAY); // Tuesday

            // Uncomment this line to use Caldroid in compact mode
            // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            caldroidFragment.setArguments(args);

            activityDate = formatter.format(cal.getTime());
        }
        setCustomResourceForDates();

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendarView, caldroidFragment);
        t.commit();

        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                Toast.makeText(getApplicationContext(), formatter.format(date),
                        Toast.LENGTH_SHORT).show();
                caldroidFragment.setTextColorForDate(R.color.caldroid_sky_blue, date);
                caldroidFragment.refreshView();
                activityDate = formatter.format(date);
                displayDaySummary();
            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                Toast.makeText(getApplicationContext(),
                        "Long click " + formatter.format(date),
                        Toast.LENGTH_SHORT).show();
            }


        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

        //final TextView textView = (TextView) findViewById(R.id.textview);

        //final Button customizeButton = (Button) findViewById(R.id.customize_button);

        // Customize the calendar

    }

    private void setCustomResourceForDates() {
        Calendar cal = Calendar.getInstance();

        // Min date is last 7 days
        cal.add(Calendar.DATE, -18);
        Date blueDate = cal.getTime();

        // Max date is next 7 days
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 16);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null) {
            caldroidFragment.setBackgroundResourceForDate(R.color.blue,
                    blueDate);
            caldroidFragment.setBackgroundResourceForDate(R.color.green,
                    greenDate);
            caldroidFragment.setTextColorForDate(R.color.white, blueDate);
            caldroidFragment.setTextColorForDate(R.color.white, greenDate);
        }
    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }
}
