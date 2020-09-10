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

public class event_main_fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "event_main_fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public event_main_fragment() {
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
    public static event_main_fragment newInstance(String param1, String param2) {
        event_main_fragment fragment = new event_main_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static final int NETCALL_COMPLETE = 1;
    public static final int IMAGEGET_COMPLETE = 2;

    private EventListAdapter adapter;
    ExpandableListView listView;
    TextView main_title;
    public static Handler network_handler;
    EventGetter eventGetter;
    public int curr_event;

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
        adapter = new EventListAdapter(this, eventGetter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret_view = inflater.inflate(R.layout.event_main_fragment, container, false);
        listView = ret_view.findViewById(R.id.expandable_event_list);
        listView.setAdapter(adapter);

        main_title = ret_view.findViewById(R.id.main_title_text);
        main_title.setText("loading...");

        Button btn = (Button)ret_view.findViewById(R.id.next_event_button);
        btn.setOnClickListener(this);
        eventGetter.getEvents();
        return ret_view;
    }

    public void changeEvent(Event event) {
        int size = adapter.getGroupCount();
        for(int i = 0; i < size; i++)
            listView.collapseGroup(i);

        main_title.setText(event.title);

        adapter.changeEvent(event);
        adapter.notifyDataSetChanged();
    }

    public void onResposeFinished() {
        eventGetter.praseResponse();
        curr_event = 0;
        changeEvent(eventGetter.events_list.get(0));
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: button");
    }
}
