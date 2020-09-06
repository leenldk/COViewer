package com.example.coviewer.network;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import androidx.annotation.LongDef;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import android.os.Handler;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonPraser {
    public static final int NETCALL_COMPLETE = 1;
    private static final String TAG = "JsonPraser";
    final int my_pagesize = 10;
    public ArrayList<News> newslist = new ArrayList<>();
    public ArrayList<News> historylist = new ArrayList<>();
    HashSet<String> historyset = new HashSet<>(); //
    HashSet<String> scannedset = new HashSet<>();
    Activity activity;
    int current_page = 0;
    Handler main_handler;

    public JsonPraser(Activity activity, Handler handler) {
        this.activity = activity;
        Log.d(TAG, "JsonPraser: init");
        Log.d(TAG, "JsonPraser: " + activity.getFilesDir().getPath());
        BufferedReader reader = null;
        main_handler = handler;
        try {
            FileInputStream in = activity.openFileInput("history");
            reader = new BufferedReader(new InputStreamReader(in));
            String s = "";
            while((s = reader.readLine()) != null) {
                News news = loadHistoryNews(s);
                historylist.add(news);
                historyset.add(s);
        }
        } catch(IOException e) {
            Log.e(TAG, "JsonPraser: ", e);
        }
    }

    News loadHistoryNews(String id) {
        BufferedReader reader = null;
        News news = null;
        try {
            FileInputStream in = activity.openFileInput(id);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String s;
            while((s = reader.readLine()) != null) {
                builder.append(s);
            }
            news = JSON.parseObject(builder.toString(), News.class);
        } catch (IOException e) {
            Log.e(TAG, "loadFromHistoryFile: load error ", e);
        }
        return news;
    }

    void saveNewsToHistory(News news) {
        BufferedWriter writer;
        try {
            FileOutputStream out = activity.openFileOutput(news._id, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(JSON.toJSONString(news));
            writer.close();
        } catch(IOException e) {
            Log.e(TAG, "JsonPraser: ", e);
        }
    }

    /**
     * 清空缓存时调用
     */
    public void refreshScanned() {
        current_page = 0;
        newslist.clear();
        scannedset.clear();
    }

    /**
     * 将新闻标为已读
     */
    public void markAsHistory(News news) {
        String s = news._id;
        if(historyset.contains(s)) return;
        historyset.add(s);
        news.visited = true;
        historylist.add(news);
        saveNewsToHistory(news);
        saveCache();
    }

    /**
     * @param req_type : 请求新闻类型，"news", "paper", "event", "all"
     * @param start_page : 起始页
     * @param page_size : 页大小
     * @param from_hisory : 是否从历史
     */
    public void getNewsList(String req_type, int start_page, int page_size, boolean from_hisory) {
        getNewsList(req_type, start_page, page_size, from_hisory, "");
    }
    public void getNewsList(String req_type, int start_page, int page_size, boolean from_hisory, String keyword)
    {
        int remain = start_page * page_size - newslist.size();
        Log.d(TAG, "getNewsList: remain : " + remain);
        if(remain > 0)
            getNewsList(req_type, start_page, page_size, from_hisory, remain);
        System.out.println("!!!!!!!!!!!!");
        System.out.println(keyword);
        if(keyword != null && !keyword.equals("")) {
            ArrayList<News> templist = new ArrayList<>();
            System.out.println("@@@@@@@@@@@@@@@@@");
            for(News news : newslist) {
                if(news.title.contains(keyword)) {
                    templist.add(news);
                    System.out.println(news.title);
                }
            }
            newslist = templist;
            for(News news : newslist) {
                System.out.println(news.title);
            }
        }
    }

    private void getNewsList(final String req_type, final int start_page, final int page_size, final boolean from_hisory, final int remain)
    {
        if(from_hisory) {
            Message message = new Message();
            message.what = NETCALL_COMPLETE;
            main_handler.sendMessage(message);
            // todo : notify;
            return;
        }
        String url = "";
        if(req_type == "news") {
            url = "https://covid-dashboard.aminer.cn/api/events/list?type=news&page="
                    + start_page + "&size=" + page_size;
        } else if(req_type == "paper") {
            url = "https://covid-dashboard.aminer.cn/api/events/list?type=paper&page="
                    + start_page + "&size=" + page_size;
        } else if(req_type == "event") {
            url = "https://covid-dashboard.aminer.cn/api/events/list?type=event&page="
                    + start_page + "&size=" + page_size;
        } else {
            url = "https://covid-dashboard.aminer.cn/api/events/list?page="
                    + start_page + "&size=" + page_size;
        }
        HttpUtil.sendHttpRequest(url,
            new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    //Log.d(TAG, "getEvents: " + responseData.substring(0, 100));
                    JSONObject obj = JSON.parseObject(responseData);
                    JSONArray newsarray = (JSONArray)obj.get("data");
                    int newslen = newsarray.size();
                    int t_remain = remain;
                    //Log.d(TAG, "news len" + newslen);
                    for(int i = 0; i < newslen; i++) {
                        Object object = newsarray.get(i);
                        News news = buildNews(object);
                        if(scannedset.contains(news._id)) continue;
                        t_remain--;
                        //Log.d(TAG, "build news ");
                        scannedset.add(news._id);
                        newslist.add(news);
                        //Log.d(TAG, "add news: " + news._id);
                    }
                    //.d(TAG, "news number: " + newslist.size());
                    if(t_remain <= 0) {
                        Message message = new Message();
                        message.what = NETCALL_COMPLETE;
                        main_handler.sendMessage(message);
                    } else {
                        getNewsList(req_type, start_page + 1, page_size, from_hisory, t_remain);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            });
    }

    public void getEpidemic() {
        HttpUtil.sendHttpRequest("https://covid-dashboard.aminer.cn/api/dist/epidemic.json",
            new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    //Log.d(TAG, "onResponse: " + responseData.substring(0, 100));
                    JSONObject obj = JSON.parseObject(responseData);
                    for(Map.Entry<String, Object> entry : obj.entrySet()) {
                        //Log.d(TAG, "onResponse: " + entry.getKey());
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    //Log.e(TAG, "onFailure: " + e.getMessage());
                }
            });
    }

    News buildNews(Object newsobject) {
        //Log.d(TAG, "in buildNews");
        JSONObject newsjson = (JSONObject)newsobject;
        News news = JSON.parseObject(newsjson.toString(), News.class);
        //news._id = newsjson.get("_id").toString();
        //Log.d(TAG, "buildNews: " + news.geoInfo);
        return news;
    }

    public void getEvents() {
        HttpUtil.sendHttpRequest("https://covid-dashboard.aminer.cn/api/dist/events.json",
            new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    //Log.d(TAG, "getEvents: " + responseData.substring(0, 100));
                    JSONObject obj = JSON.parseObject(responseData);
                    JSONArray newsarray = (JSONArray)obj.get("datas");
                    //Log.d(TAG, "onResponse: " + newsarray.size());
                    int newslen = newsarray.size();
                    //Log.d(TAG, "onResponse: " + obj.get("tflag"));
                    //JSONArray news_data = JSON.parseArray(obj.get("datas").toString());
                    //Log.d(TAG, "onResponse: " + news_data.size());
                    newslist.clear();
                    for(int i = 0; i < newslen; i++) {
                        Object object = newsarray.get(i);
                        News news = buildNews(object);
                        newslist.add(news);
                    }
                    //Log.d(TAG, "news number: " + newsarray.size());
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            });
    }

    public void saveCache() {
        BufferedWriter writer;
        try {
            FileOutputStream out = activity.openFileOutput("history", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            for(News news : historylist) {
                writer.write(news._id + "\n");
            }
            writer.close();
        } catch(IOException e) {
            Log.e(TAG, "JsonPraser: ", e);
        }
    }

}
