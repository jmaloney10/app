package com.example.jmaloney.myapplication.calendar;

import android.os.Bundle;

import com.example.jmaloney.myapplication.common.Record;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class CaldroidSampleCustomFragment extends CaldroidFragment {
    ArrayList<Record> logs;

    @Override
    public void setArguments(Bundle args) {
        logs = args.getParcelableArrayList("logs");
        super.setArguments(args);
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        // TODO Auto-generated method stub
        return new CaldroidSampleCustomAdapter(getActivity(), month, year,
                getCaldroidData(), extraData,logs);
    }

}