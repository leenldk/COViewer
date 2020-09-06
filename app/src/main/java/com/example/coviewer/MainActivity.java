package com.example.coviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.coviewer.network.JsonPraser;

public class MainActivity extends AppCompatActivity {
    public static final int NETCALL_COMPLETE = 1;
    private static final String TAG = "MainActivity";
    public JsonPraser praser;
    public static Handler network_handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.browse);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(TAG, "onCreate: in create");
        network_handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == NETCALL_COMPLETE) {
                    Log.d(TAG, "handleMessage: network call complete");
                    praser.markAsHistory(praser.newslist.get(0));
                    Log.d(TAG, "handleMessage: " + praser.historylist.size());
                }
            }
        };
        praser = new JsonPraser(this, network_handler);
        //praser.getEpidemic();
        praser.getNewsList("news", 1, 10, false, null);
        praser.getNewsList("news", 2, 7, false, null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean main_drawer_button_click(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).openDrawer(Gravity.LEFT);
                return true;
            case R.id.main_drawer_item_one:
                Navigation.findNavController(findViewById((R.id.main_fragment_host))).navigate(R.id.news_main_fragment);
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).closeDrawers();
                return true;
            case R.id.main_drawer_item_two:
                Navigation.findNavController(findViewById((R.id.main_fragment_host))).navigate(R.id.data_main_fragment);
                ((DrawerLayout) findViewById(R.id.main_drawerlayout)).closeDrawers();
                return true;
            case R.id.main_drawer_item_three:
                return true;
            case R.id.main_drawer_item_four:
                return true;
            case R.id.main_drawer_item_five:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "call on stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy: saving to history");
        //praser.saveCache();
    }
}