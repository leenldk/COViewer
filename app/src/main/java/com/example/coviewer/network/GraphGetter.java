package com.example.coviewer.network;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

public class GraphGetter {
    public static final int NETCALL_COMPLETE = 1;
    public static final int IMAGEGET_COMPLETE = 2;
    private static final String TAG = "GraphGetter";

    Handler callback_handler;
    public String responseData;
    public ArrayList<GraphEntity> entity_list;

    public GraphGetter(Handler handler) {
        callback_handler = handler;
    }

    public void getGraph(String keyword) {

        Log.d(TAG, "getGraph: " + keyword);
        HttpUtil.sendHttpRequest("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=" + keyword,
                new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseData = response.body().string();
//                        Log.d(TAG, "onResponse: " + responseData.substring(0, 100));
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

    public void getImages() {
        final AtomicInteger counter = new AtomicInteger(0);
        final int size = entity_list.size();
        for(final GraphEntity e : entity_list) {
            if(e.img == null || e.img.equals("")) {
                counter.getAndIncrement();
                if(counter.get() == size) {
                    Log.d(TAG, "onResponse: get image end");
                    Message message = new Message();
                    message.what = IMAGEGET_COMPLETE;
                    callback_handler.sendMessage(message);
                }
                continue;
            }
            HttpUtil.sendHttpRequest(e.img,
                    new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            byte[] pic_byte = response.body().bytes();
                            e.bitmap = BitmapFactory.decodeByteArray(pic_byte, 0, pic_byte.length);
                            counter.getAndIncrement();
                            if(counter.get() == size) {
                                Log.d(TAG, "onResponse: get image end");
                                Message message = new Message();
                                message.what = IMAGEGET_COMPLETE;
                                callback_handler.sendMessage(message);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        }
    }

    public void praseResponse() {
        Log.d(TAG, "praseResponse: parsing");
        //Log.d(TAG, "praseResponse: " + responseData.substring(0, 100));
        JSONArray data_array = (JSONArray)JSON.parseObject(responseData).get("data");
        entity_list = new ArrayList<>();
        for(Object entry : data_array) {
            JSONObject obj = (JSONObject)entry;
            GraphEntity entity = new GraphEntity();
            entity.name = (String)obj.get("label");
            entity.img = (String)obj.get("img");
            Log.d(TAG, "praseResponse: name " + entity.name);
            Log.d(TAG, "praseResponse: img " + entity.img);
            JSONObject abstractInfo = (JSONObject) obj.get("abstractInfo");
            entity.description = (String)abstractInfo.get("enwiki") + (String)abstractInfo.get("baidu") + (String)abstractInfo.get("zhwiki");
            Log.d(TAG, "praseResponse: description " + entity.description);
            JSONArray relation_array = (JSONArray) ((JSONObject)abstractInfo.get("COVID")).get("relations");
            for(Object relation : relation_array) {
                EntityRelation t_relation = JSON.parseObject(((JSONObject)relation).toString(), EntityRelation.class);
                entity.relations.add(t_relation);
                Log.d(TAG, "praseResponse: relation label " + t_relation.label);
            }
            entity_list.add(entity);
        }
    }
}