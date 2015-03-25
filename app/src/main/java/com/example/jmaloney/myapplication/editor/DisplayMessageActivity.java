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
import com.example.jmaloney.myapplication.common.Group;
import com.example.jmaloney.myapplication.common.MyExpandableListAdapter;
import com.example.jmaloney.myapplication.R;
import com.example.jmaloney.myapplication.common.SelectableGroup;
import com.example.jmaloney.myapplication.template.Template531;
import com.example.jmaloney.myapplication.settings.SettingsActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class DisplayMessageActivity extends Activity{
    HashMap<Integer,Day> genIDs = new HashMap<Integer, Day>();
    SparseArray<Group> groups;
    String date = "";
    ExpandableListView listView = null;
    MyExpandableListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String[] messages = intent.getStringArrayExtra(SelectDay.EXTRA_MESSAGE);
        date = intent.getStringExtra(SelectDay.DATE_MESSAGE);
        displayLifts(messages[0],Integer.parseInt(messages[1]));


    }

    public void displayLifts(String day,Integer wave){
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

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, this.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            for(int i = 0; i < groups.size() ; i++){
                tempGroup = (SelectableGroup) groups.get(i);
                for (int j = 0; j <tempGroup.selected.size(); j++) {
                    if (tempGroup.selected.get(j)) {
                        string = String.format("%s\t%s\t%s\t1", date, tempGroup.groupName, tempGroup.children.get(j));
                    } else {
                        string = String.format("%s\t%s\t%s\t0", date, tempGroup.groupName, tempGroup.children.get(j));
                    }
                    Toast.makeText(getApplicationContext(), string,
                            Toast.LENGTH_SHORT).show();
                    fos.write(string.getBytes());
                }
            }

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Saved!",
                Toast.LENGTH_SHORT).show();
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
