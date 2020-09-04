package com.example.coviewer.network;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}

