package com.example.jmaloney.myapplication.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmaloney on 3/17/2015.
 */
public class Template531 extends Template {
    static public int numWaves = 3;
    static public int numDaysPerWave = 4;
    static public String[] dayNames = {"Bench","Squat","OverHeadPress","Deadlift"};
    int[] increases = {5,10,5,10};
    Double[][] mainPercents = {{0.65,0.75,0.85},{0.7,0.8,0.9},{0.75,0.85,0.95}};
    int[][] mainReps = {{5,5,5},{3,3,3},{5,3,1}};
    Double[] warmupPercents = {0.4,0.5,0.6};
    int[] warmupReps = {5,5,3};
    boolean BBB = true;

    HashMap<String,Integer> maxes;
    HashMap<String,String> BBBAccesory = new HashMap<String,String>() {{
        put("Bench","Row");
        put("Squat","Deadlift");
        put("OverHeadPress","Pullups");
        put("Deadlift","Squat");
    }};


    public Template531(){
        super();
    }

    public Template531(int wave, int day){
        super(wave,day);
    }

    public void setMaxes(Map<String,Integer> maxesMap){
        maxes = new HashMap<String,Integer>();
        maxes.putAll(maxesMap);
    }

    public ArrayList<String> getWarmup(int wave, String day){
        ArrayList<String> sets = new ArrayList<String>();

        for(int i = 0; i < warmupPercents.length; i++){
            sets.add(String.format("%s: %d x %d lbs",day,warmupReps[i],roundToPlate(warmupPercents[i]*maxes.get(day))));
        }

        return sets;
    }

    public ArrayList<String> getAccesoryWork(int wave, String day){
        ArrayList<String> sets = new ArrayList<String>();

        if (BBB == true) {
            for (int i = 0; i < 5; i++) {
                sets.add(String.format("%s: %d x %d lbs", day, 10, roundToPlate(0.5* maxes.get(day))));
            }

            for (int i = 0; i < 5; i++) {
                sets.add(String.format("%s: %d x %d lbs", BBBAccesory.get(day), 10, roundToPlate(0.5 * maxes.get(day))));
            }
        }

        return sets;
    }

    public List<String> getDay(int wave, String day){
        ArrayList<String> sets = getWarmup(wave, day);

        sets.addAll(getMainLift(wave, day));

        sets.addAll(getAccesoryWork(wave, day));

        return sets;
    }

    public ArrayList<String> getMainLift(int wave, String day) {
        ArrayList<String> sets = new ArrayList<String>();
        for(int i = 0; i < mainPercents[wave].length; i++){
            sets.add(String.format("%s: %d x %d lbs",day,mainReps[wave][i],roundToPlate( mainPercents[wave][i]*maxes.get(day))));
        }
        return sets;
    }

    public void incrementMaxes(){
        for(int i = 0; i < dayNames.length; i++){
            maxes.put(dayNames[i],maxes.get(dayNames[i])+ increases[i]);
        }
    }
}
