package com.example.coviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.coviewer.network.JsonPraser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JsonPraser praser = new JsonPraser();
        praser.getEpidemic();
    }
}