package com.example.coviewer.network;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class EpidemicMap {
    private static final String TAG = "EpidemicMap";
    public LinkedHashMap<String, Integer> map;
    public LinkedHashMap<String, ArrayList<Integer> > timeline_map;
    public ArrayList<String> district;
    public ArrayList<Integer> number;
    public EpidemicMap() {
        district = new ArrayList<>();
        map = new LinkedHashMap<>();
        number = new ArrayList<>();
        timeline_map = new LinkedHashMap<>();
    }
    public void add(String key, int val) {
        //map.merge(key, val, Integer::sum);
        if(map.containsKey(key)) map.put(key, map.get(key) + val);
        else map.put(key, val);
        timeline_map.put(key, new ArrayList<Integer>());
    }

    public void addTimeline(String key, int val, int tim) {
        ArrayList<Integer> list = timeline_map.get(key);
        while(tim >= list.size()) list.add(0);
        list.set(tim, list.get(tim) + val);
    }

    public void dosort() {
        Log.d(TAG, "dosort: size" + map.size());
        district = new ArrayList<>();
        number = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> stringIntegerEntry, Map.Entry<String, Integer> t1) {
                return stringIntegerEntry.getValue().compareTo(t1.getValue());
            }
        });
        for(Map.Entry<String, Integer> entry : list) {
            district.add(entry.getKey());
            number.add(entry.getValue());
        }
    }
}
