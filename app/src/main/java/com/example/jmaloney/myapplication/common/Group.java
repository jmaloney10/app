package com.example.jmaloney.myapplication.common;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.jmaloney.myapplication.R;
import com.example.jmaloney.myapplication.editor.DisplayMessageActivity;
import com.example.jmaloney.myapplication.editor.SelectDay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmaloney on 3/19/2015.
 */
public class Group {

    public String groupName;
    public List<String> children;

    public void addAll(List<String> childs){
        for(String child: childs){
            children.add(child);
        }
    }

    public void add(String child){
        children.add(child);
    }

    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, android.view.ViewGroup parent
                            ,LayoutInflater inflater,final Activity act) {
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.select_day_listrow_details, null);
        }
        text = (TextView) convertView.findViewById(R.id.textViewSelectDay);
        text.setText(children.get(childPosition));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDay selectDay = (SelectDay) act;
                String[] params = {groupName,String.valueOf(childPosition)};
                selectDay.daySelected(params);
            }
        });
        return convertView;
    }

    public Group(String groupName) {
        children = new ArrayList<String>();
        this.groupName = groupName;
    }


}
