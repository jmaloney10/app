package com.example.jmaloney.myapplication.common;

import android.os.Parcel;
import android.os.Parcelable;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

import java.util.Date;

/**
 * Created by jmaloney on 3/26/2015.
 */
@CsvDataType()
public class Record implements Parcelable {
    @CsvField(pos = 1, format = "dd MMM yyyy")
    public Date date = null;

    @CsvField(pos = 2)
    public String groupName = null;

    @CsvField(pos = 3)
    public String day = null;

    @CsvField(pos = 4)
    public String exercise = null;

    @CsvField(pos = 5)
    public int weight = 0;

    @CsvField(pos = 6)
    public int repitions = 0;

    @CsvField(pos = 7)
    public boolean completed = false;

    @CsvField(pos = 8)
    public int wave = 0;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(wave);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeInt(repitions);
        dest.writeInt(weight);
        dest.writeString(day);
        dest.writeString(groupName);
        dest.writeSerializable(date);
    }

    public Record(Parcel in){
        wave = in.readInt();
        completed = in.readByte() != 0;
        repitions = in.readInt();
        weight = in.readInt();
        day = in.readString();
        groupName = in.readString();
        date = (Date) in.readSerializable();
    }

    public Record(){
    }
}
