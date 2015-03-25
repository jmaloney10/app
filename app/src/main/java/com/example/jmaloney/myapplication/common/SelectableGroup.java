package com.example.jmaloney.myapplication.common;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.jmaloney.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SelectableGroup extends Group {

    public List<Boolean> selected;

    @Override
    public void addAll(List<String> childs){
        for(String child: childs){
            children.add(child);
            selected.add(false);
        }
    }

    @Override
    public void add(String child){
        children.add(child);
        selected.add(false);
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, android.view.ViewGroup parent,LayoutInflater inflater,final Activity act) {
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(children.get(childPosition));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.check);
        checkbox.setTag(this);
        checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        SelectableGroup selectableGroup = (SelectableGroup) checkbox.getTag();
                        final int groupPos = groupPosition;
                        final int childPos = childPosition;
                        selectableGroup.selected.set(childPosition, buttonView.isChecked());
                        //setSelect(groupPos, childPos, buttonView.isChecked());

                    }
                });

        return convertView;
    }

    public SelectableGroup(String groupName) {
        super(groupName);
        selected = new ArrayList<Boolean>();
    }

}