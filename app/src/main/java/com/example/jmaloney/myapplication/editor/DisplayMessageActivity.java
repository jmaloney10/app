package com.example.jmaloney.myapplication.editor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.jmaloney.myapplication.Day;
import com.example.jmaloney.myapplication.MainActivity;
import com.example.jmaloney.myapplication.common.Group;
import com.example.jmaloney.myapplication.common.MyExpandableListAdapter;
import com.example.jmaloney.myapplication.R;
import com.example.jmaloney.myapplication.common.Record;
import com.example.jmaloney.myapplication.common.SelectableGroup;
import com.example.jmaloney.myapplication.template.Template531;
import com.example.jmaloney.myapplication.settings.SettingsActivity;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.CsvSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DisplayMessageActivity extends Activity{
    HashMap<Integer,Day> genIDs = new HashMap<Integer, Day>();
    SparseArray<Group> groups;
    String date = "";
    ExpandableListView listView = null;
    MyExpandableListAdapter adapter = null;
    SimpleDateFormat formatter = null;
    String day;
    Integer wave;
    ArrayList<Record> logs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String[] messages = intent.getStringArrayExtra(SelectDay.EXTRA_MESSAGE);
        date = intent.getStringExtra(SelectDay.DATE_MESSAGE);
//        date = "03 Mar 2015";
        day = messages[0];
        wave = Integer.parseInt(messages[1]);
        displayLifts();


    }

    public void displayLifts(){
        Template531 template = new Template531();
        Map<String,Integer> maxes = new HashMap<String,Integer>();

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.saved_bench_max_default);
        int max = sharedPref.getInt(getString(R.string.saved_bench_max), defaultValue);
        maxes.put("Bench",max);

        defaultValue = getResources().getInteger(R.integer.saved_squat_max_default);
        max = sharedPref.getInt(getString(R.string.saved_squat_max), defaultValue);
        maxes.put("Squat",max);

        defaultValue = getResources().getInteger(R.integer.saved_deadlift_max_default);
        max = sharedPref.getInt(getString(R.string.saved_deadlift_max), defaultValue);
        maxes.put("Deadlift",max);

        defaultValue = getResources().getInteger(R.integer.saved_overheadpress_max_default);
        max = sharedPref.getInt(getString(R.string.saved_overheadpress_max), defaultValue);
        maxes.put("OverHeadPress",max);
        template.setMaxes(maxes);


        groups = new SparseArray<Group>();

        SelectableGroup warmup = new SelectableGroup("Warmup");
        warmup.addAll(template.getWarmup(wave,day));
        groups.append(0,warmup);

        SelectableGroup mainLift = new SelectableGroup("Main Lifts");
        mainLift.addAll(template.getMainLift(wave,day));
        groups.append(1,mainLift);

        SelectableGroup accesory = new SelectableGroup("Accesory Work");
        accesory.addAll(template.getAccesoryWork(wave,day));
        groups.append(2,accesory);

        listView = (ExpandableListView ) findViewById(R.id.listview);

        adapter = new MyExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);
    }

    public void saveWorkout(){
        String FILENAME = "workout_log";
        String string = "";
        SelectableGroup tempGroup = null;

        loadLogs();

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, this.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CsvSerializer serializer = (CsvSerializer) CsvIOFactory.createFactory(Record.class).createSerializer();
        StringWriter writer = new StringWriter();
        serializer.open(writer);

        if (formatter == null){
            formatter = new SimpleDateFormat(MainActivity.DATE_FORMAT);
        }

        try {
            Iterator<Record> it = logs.iterator();
            while(it.hasNext()){
                Record record = it.next();
                if (record.date.equals(formatter.parse(date))){
                    it.remove();
                }
            }

            for(int i = 0; i < groups.size() ; i++){
                tempGroup = (SelectableGroup) groups.get(i);
                for (int j = 0; j <tempGroup.selected.size(); j++) {
                    logs.add(createRecord(tempGroup.groupName, tempGroup.children.get(j), tempGroup.selected.get(j)));
                }
            }

            for (Record record: logs){
                serializer.write(record);
            }

            serializer.close(true);
            string = writer.toString();
            fos.write(string.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Saved!",
                Toast.LENGTH_SHORT).show();
    }

    public void loadLogs(){
        FileInputStream fis = null;
        BufferedReader bfr = null;
        logs = new ArrayList<Record>();
        String line = null;
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

    public Record createRecord(String groupName,String setInfo, boolean completed) throws ParseException {
        if (formatter == null){
            formatter = new SimpleDateFormat(MainActivity.DATE_FORMAT);
        }
        String subString = null;
        Record record = new Record();

        record.date = formatter.parse(date);
        record.groupName = groupName;
        record.exercise = setInfo.split(":")[0];

        Pattern pattern = Pattern.compile(": [0-9]+ x ");
        Matcher matcher = pattern.matcher(setInfo);
        if (matcher.find()) {
            subString = matcher.group(0);
            record.repitions = Integer.parseInt(subString.substring(2,subString.length() - 3).trim());
        }
        pattern = Pattern.compile(": [0-9]+ x [0-9]+ lbs");
        matcher = pattern.matcher(setInfo);
        if (matcher.find()) {
            subString = matcher.group(0);
            record.weight = Integer.parseInt(subString.substring(subString.indexOf('x')+ 1,subString.length() - 4).trim());
        }

        record.completed = completed;
        record.day = day;
        record.wave = wave;

        return record;
    }

    public void addExercise(){
        SelectableGroup other = null;
        if (groups.get(groups.size() - 1).groupName.equals("Other")){
            other = (SelectableGroup) groups.get(groups.size() - 1);
            other.add("Test seen");
        } else {
            other = new SelectableGroup("Other");
            other.add("Test new");
            groups.append(groups.size(),other);
        }

        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_save:
                saveWorkout();
                return true;
            case R.id.action_add:
                addExercise();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
