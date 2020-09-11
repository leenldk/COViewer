package com.example.coviewer.network;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.coviewer.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.coviewer.expert_main_fragment.T;

public class ExpertGetter {
    public static final int NETCALL_COMPLETE = 1;
    public static final int IMAGEGET_COMPLETE = 2;
    private static final String TAG = "GraphGetter";

    Handler callback_handler;
    public String responseData;
    public ArrayList<Expert> experts;
    public ArrayList<Expert> highlight_experts;
    public ArrayList<Expert> passedaway_experts;

    public ExpertGetter(Handler handler) {
        callback_handler = handler;
        experts = new ArrayList<>();
        highlight_experts = new ArrayList<>();
        passedaway_experts = new ArrayList<>();
    }

    public void getExperts() {

        HttpUtil.sendHttpRequest("https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2",
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

    public void getImages() {
        final AtomicInteger counter = new AtomicInteger(0);
        final int size = experts.size();
        for (final Expert e : experts) {
            if (e.avatar == null || e.avatar.equals("")) {
                counter.getAndIncrement();
                if (counter.get() == size) {
                    Log.d(TAG, "onResponse: get image end");
                    Message message = new Message();
                    message.what = IMAGEGET_COMPLETE;
                    for (Expert expert : passedaway_experts) {
                        if (expert.bitmap != null)
                            expert.bitmap = Utility.toGrayscale(expert.bitmap);
                    }
                    callback_handler.sendMessage(message);
                }
                continue;
            }
            HttpUtil.sendHttpRequest(e.avatar,
                    new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            byte[] pic_byte = response.body().bytes();
                            e.bitmap = BitmapFactory.decodeByteArray(pic_byte, 0, pic_byte.length);
                            counter.getAndIncrement();
                            if (counter.get() == size) {
                                Log.d(TAG, "onResponse: get image end");
                                Message message = new Message();
                                message.what = IMAGEGET_COMPLETE;
                                for (Expert expert : passedaway_experts) {
                                    if (expert.bitmap != null)
                                        expert.bitmap = Utility.toGrayscale(expert.bitmap);
                                }
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
    public void getImageOf(final int position, final int myT) {
        if(experts.get(position).avatar == null || experts.get(position).avatar.equals("")) {
            Log.d(TAG, "onResponse: get image end");
            Message message = new Message();
            message.what = IMAGEGET_COMPLETE;
            if(experts.get(position).is_passedaway){
                if(experts.get(position).bitmap != null)
                    experts.get(position).bitmap = Utility.toGrayscale(experts.get(position).bitmap);
            }
            callback_handler.sendMessage(message);
        }
        HttpUtil.sendHttpRequest(experts.get(position).avatar,
            new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    byte[] pic_byte = response.body().bytes();
                    if(T == myT) {
                        experts.get(position).bitmap = BitmapFactory.decodeByteArray(pic_byte, 0, pic_byte.length);
                        Log.d(TAG, "onResponse: get image end");
                        Message message = new Message();
                        message.what = IMAGEGET_COMPLETE;
                        if(experts.get(position).is_passedaway){
                            if(experts.get(position).bitmap != null) {
                                experts.get(position).bitmap = Utility.toGrayscale(experts.get(position).bitmap);
                            }
                        }
                        callback_handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            });
    }

    public void praseResponse() {
        Log.d(TAG, "praseResponse: parsing");
        Log.d(TAG, "praseResponse: " + responseData.substring(0, 100));
        JSONArray data_array = (JSONArray)JSON.parseObject(responseData).get("data");
        for(Object entry : data_array) {
            JSONObject obj = (JSONObject)entry;
            Expert expert = new Expert();

            expert.id = (String)obj.get("id");
            expert.is_passedaway = (boolean)obj.get("is_passedaway");
            String name = (String)obj.get("name");
            String name_zh = (String)obj.get("name_zh");
            if(name_zh.equals("") || name_zh == null) {
                expert.name = name;
            } else {
                expert.name = name_zh;
            }
            expert.avatar = (String)obj.get("avatar");
            JSONObject profile = (JSONObject)obj.get("profile");
            expert.bio = (String)profile.get("bio");
            expert.position = (String)profile.get("position");
            expert.affiliation = (String)profile.get("affiliation");
            Log.d(TAG, "praseResponse: name " + expert.name);
            Log.d(TAG, "praseResponse: is_passedaway " + expert.is_passedaway);
            Log.d(TAG, "praseResponse: bio " + expert.bio);

            experts.add(expert);
            if(expert.is_passedaway) passedaway_experts.add(expert);
            else highlight_experts.add(expert);
        }
    }
}