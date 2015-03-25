package com.example.jmaloney.myapplication.editor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.example.jmaloney.myapplication.Day;
import com.example.jmaloney.myapplication.common.Group;
import com.example.jmaloney.myapplication.common.MyExpandableListAdapter;
import com.example.jmaloney.myapplication.R;
import com.example.jmaloney.myapplication.template.Template531;
import com.example.jmaloney.myapplication.settings.SettingsActivity;

import java.util.HashMap;


public class SelectDay extends Activity{
    HashMap<Integer,Day> genIDs = new HashMap<Integer, Day>();
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    public final static String DATE_MESSAGE = "com.mycompany.myfirstapp.DATEMESSAGE";
    String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_day);

        Intent intent = getIntent();
        date = intent.getStringExtra(SelectDay.DATE_MESSAGE);
        displayDays();
    }

    public void daySelected(String[] params){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, params);
        intent.putExtra(DATE_MESSAGE,date);
        this.startActivity(intent);
    }

    public void displayDays() {
        // Create the text view
        int j = 0;
        SparseArray<Group> groups = new SparseArray<Group>();
        for (String day : Template531.dayNames) {
            Group dayGroup = new Group(day);
            for(int i = 0; i < Template531.numWaves;i++) {
                dayGroup.add("Wave " + (i + 1));
            }
            groups.append(j++, dayGroup);
        }

        final ExpandableListView listView = (ExpandableListView ) findViewById(R.id.dayView);

        MyExpandableListAdapter adapter = new MyExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_day, menu);
        return true;
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

}
