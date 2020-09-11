package com.example.coviewer.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class EpidemicGetter {
    public static final int NETCALL_COMPLETE = 1;
    private static final String TAG = "EpidemicGetter";

    Handler callback_handler;
    public String responseData;
    public EpidemicMap china_confirmed, china_cured, china_dead;
    public EpidemicMap world_confirmed, world_cured, world_dead;

    public EpidemicGetter(Handler handler) {
        callback_handler = handler;
        china_confirmed = new EpidemicMap();
        china_cured = new EpidemicMap();
        china_dead = new EpidemicMap();
        world_confirmed = new EpidemicMap();
        world_cured = new EpidemicMap();
        world_dead = new EpidemicMap();
    }

    public void getEpidemic() {
        HttpUtil.sendHttpRequest("https://covid-dashboard.aminer.cn/api/dist/epidemic.json",
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

    public void praseResponse() {

        JSONObject obj = JSON.parseObject(responseData);
        for(Map.Entry<String, Object> entry : obj.entrySet()) {
            //Log.d(TAG, "praseResponse: " + entry.getKey());
            JSONObject data_obj = (JSONObject)entry.getValue();
            //Log.d(TAG, "praseResponse: data_obj " + data_obj);
            JSONArray data_array = (JSONArray)data_obj.get("data");
            //Log.d(TAG, "praseResponse: data_array " + data_array);
            JSONArray last_data = (JSONArray) data_array.get(data_array.size() - 1);
            //Log.d(TAG, "praseResponse: last data" + last_data);
            int confirmed = (Integer)last_data.get(0);
            int cured = (Integer)last_data.get(2);
            int dead = (Integer)last_data.get(3);
            //Log.d(TAG, "praseResponse: confirmed" + confirmed);
            //Log.d(TAG, "praseResponse: " + entry.getKey());
            String[] district = entry.getKey().split("\\|");
            if(district[0].equals("China") && district.length == 2) {
                china_confirmed.add(district[1], confirmed);
                china_cured.add(district[1], cured);
                china_dead.add(district[1], dead);
            }
            if(district.length == 1) {
                world_confirmed.add(district[0], confirmed);
                world_cured.add(district[0], cured);
                world_dead.add(district[0], dead);
            }
            //Log.d(TAG, "praseResponse: end ");

            for(int i = data_array.size() - 1; i >= 0; i--) {
                int tim = data_array.size() - i;
                JSONArray t_array = (JSONArray)data_array.get(i);
                int t_confirmed = (Integer)t_array.get(0);
                int t_cured = (Integer)t_array.get(2);
                int t_dead = (Integer)t_array.get(3);
                //Log.d(TAG, "praseResponse: confirmed" + confirmed);
                //Log.d(TAG, "praseResponse: " + entry.getKey());
                if(district[0].equals("China") && district.length == 2) {
                    china_confirmed.addTimeline(district[1], t_confirmed, tim);
                    china_cured.addTimeline(district[1], t_cured, tim);
                    china_dead.addTimeline(district[1], t_dead, tim);
                }
                if(district.length == 1) {
                    world_confirmed.addTimeline(district[0], t_confirmed, tim);
                    world_cured.addTimeline(district[0], t_cured, tim);
                    world_dead.addTimeline(district[0], t_dead, tim);
                }
            }
        }
        Log.d(TAG, "praseResponse: final");
    }

}
