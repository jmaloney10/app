package com.example.jmaloney.myapplication.template;

import java.util.List;

/**
 * Created by jmaloney on 3/17/2015.
 */
public abstract class Template {

    int currentWave;
    int currentDay;

    public Template(){
        currentWave = 0;
        currentDay = 0;
    }

    public Template(int wave,int day){
        currentWave = wave;
        currentDay = day;
    }

    public int roundToPlate(Double initial){
        return 5*(int)(Math.round(initial/5.0));
    }

    abstract public List<String> getDay(int wave, String day);

    abstract public List<String> getWarmup(int wave, String day);

    abstract public List<String> getAccesoryWork(int wave, String day);
}
