package com.example.coviewer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coviewer.network.Event;
import com.example.coviewer.network.EventGetter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class event_cluster_fragment extends Fragment {

    private static final String TAG = "event_main_fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public event_cluster_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment data_main_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static event_cluster_fragment newInstance(String param1, String param2) {
        event_cluster_fragment fragment = new event_cluster_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static final int NETCALL_COMPLETE = 1;
    public static final int IMAGEGET_COMPLETE = 2;

    private EventClusterAdapter adapter;
    ExpandableListView listView;
    public static Handler network_handler;
    EventGetter eventGetter;
    ArrayList<String> labels;
    ArrayList<ArrayList<Event> > label_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        network_handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NETCALL_COMPLETE) {
                    Log.d(TAG, "handleMessage: event call complete");
                    onResposeFinished();
                }
            }
        };
        eventGetter = new EventGetter(network_handler);
        adapter = new EventClusterAdapter(this, eventGetter);
        label_list = new ArrayList<>();
        labels = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret_view = inflater.inflate(R.layout.event_cluster_fragment, container, false);
        listView = ret_view.findViewById(R.id.expandable_event_cluster_list);
        listView.setAdapter(adapter);

        eventGetter.getEvents();
        return ret_view;
    }

    public void getCluster() {
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().openRawResource(R.raw.event_labels));
            BufferedReader bufReader = new BufferedReader(inputReader);
            Scanner scanner = new Scanner(bufReader);
            while(scanner.hasNext()) {
                String s = scanner.next();
                labels.add(s);
                int number = scanner.nextInt();
                ArrayList<Event> t_events = new ArrayList<>();
                for (int i = 0; i < number; i++) {
                    int id = scanner.nextInt();
                    t_events.add(eventGetter.events_list.get(id));
                }
                label_list.add(t_events);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResposeFinished() {
        eventGetter.praseResponse();
        getCluster();
        Log.d(TAG, "onResposeFinished: ");
        adapter.onResposeFinished();
    }
}
