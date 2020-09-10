package com.example.coviewer.network;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

public class EventGetter {
    public static final int NETCALL_COMPLETE = 1;
    private static final String TAG = "EventGetter";

    Handler callback_handler;
    public String responseData;
    public ArrayList<Event> events_list;
    public LinkedHashMap<String, ArrayList<Event> > label_events;
    public LinkedHashMap<String, Event> id_to_event;

    public EventGetter(Handler handler) {
        callback_handler = handler;
        label_events = new LinkedHashMap<>();
        id_to_event = new LinkedHashMap<>();
        events_list = new ArrayList<>();
    }

    public void getEvents() {
        Log.d(TAG, "in getEvents");
        HttpUtil.sendHttpRequest("https://covid-dashboard.aminer.cn/api/events/list?type=event&page=1&size=2000" ,
                new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseData = response.body().string();
                        Log.d(TAG, "onResponse: " + responseData.substring(0, 100));
                        Message message = new Message();
                        message.what = NETCALL_COMPLETE;
                        callback_handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    public Event getEventById(String id) {
        return id_to_event.get(id);
    }

    public ArrayList<Event> getLabelEvents(String label, Event this_event) {
        ArrayList<Event> events = label_events.get(label);
        events.sort(new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                double t = t1.influence - event.influence;
                return t < 0 ? -1 : (t == 0 ? 0 : 1);
            }
        });
        ArrayList<Event> ret_events = new ArrayList<>();
        for(Event event : events) {
            if(event._id == this_event._id) continue;
            ret_events.add(event);
        }
        return ret_events;
    }

    public void praseResponse() {
        Log.d(TAG, "praseResponse: parsing");
        Log.d(TAG, "praseResponse: " + responseData.substring(0, 100));
        JSONArray data_array = (JSONArray)JSON.parseObject(responseData).get("data");
        for(Object entry : data_array) {
            JSONObject obj = (JSONObject)entry;
            Event event = new Event();
            event._id = (String)obj.get("_id");
            event.title = (String)obj.get("title");
            event.influence = ((BigDecimal)obj.get("influence")).doubleValue();

            Log.d(TAG, "praseResponse: title " + event.title);
            Log.d(TAG, "praseResponse: influence " + event.influence);
            JSONArray related_events = (JSONArray) obj.get("related_events");
            for(Object rel_obj : related_events) {
                JSONObject t_rel_obj = (JSONObject)rel_obj;
                event.related_ids.add((String)t_rel_obj.get("id"));
            }
            JSONArray labels = (JSONArray) obj.get("entities");
            for(Object label_obj : labels) {
                JSONObject t_label_obj = (JSONObject)label_obj;
                String label = (String)t_label_obj.get("label");
                event.labels.add(label);
                Log.d(TAG, "praseResponse: label : " + label);
                if(!label_events.containsKey(label))
                    label_events.put(label, new ArrayList<Event>());
                label_events.get(label).add(event);
            }
            id_to_event.put(event._id, event);
            events_list.add(event);
        }
    }
}