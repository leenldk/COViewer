package com.example.coviewer.network;

import android.util.Log;

import androidx.annotation.LongDef;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonPraser {
    private static final String TAG = "JsonPraser";
    public void getEpidemic() {
        HttpUtil.sendHttpRequest("https://covid-dashboard.aminer.cn/api/dist/epidemic.json",
                new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d(TAG, "onResponse: " + responseData.substring(0, 100));
                        JSONObject obj = JSON.parseObject(responseData);
                        for(Map.Entry<String, Object> entry : obj.entrySet()) {
                            Log.d(TAG, "onResponse: " + entry.getKey());
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }
}
