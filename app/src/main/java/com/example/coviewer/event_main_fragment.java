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
import androidx.loader.app.LoaderManager;
import androidx.navigation.Navigation;

import com.example.coviewer.network.Event;
import com.example.coviewer.network.EventGetter;
import com.example.coviewer.network.Expert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.coviewer.MainActivity.content_string;

public class event_main_fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "event_main_fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View root_view;
    private boolean load_finished = false;
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
    public int total_events;
    Event input_event;
    Button btn_next, btn_prev, btn_cluster;

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
        root_view = ret_view;
        input_event = (Event) getArguments().getSerializable("event");

        btn_next = (Button)ret_view.findViewById(R.id.next_event_button);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!load_finished) {
                    return ;
                }
                if(curr_event == total_events - 1)
                    curr_event = 0;
                else curr_event++;
                Event t_event = eventGetter.events_list.get(curr_event);
                changeEvent(t_event);
            }
        });

        btn_prev = (Button)ret_view.findViewById(R.id.previous_event_button);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!load_finished) {
                    return ;
                }
                if(curr_event == 0)
                    curr_event = total_events - 1;
                else curr_event--;
                Event t_event = eventGetter.events_list.get(curr_event);
                changeEvent(t_event);
            }
        });

        btn_cluster = (Button)ret_view.findViewById(R.id.cluster_page_button);
        btn_cluster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Navigation.findNavController(view).navigate(R.id.event_cluster_fragment, bundle);
            }
        });

        eventGetter.getEvents();
        return ret_view;
    }

    public void changeEvent(Event event) {
        Log.d(TAG, "onClick: click");
        Event t_event1 = eventGetter.getEventById("5ec7ce549fced0a24bf419a1");
        Log.d(TAG, "getRelatedEvents before change: size 5ec7ce549fced0a24bf419a1 " + t_event1.related_events.size());
        Log.d(TAG, "changeEvent: changing");
        int size = adapter.getGroupCount();
        for(int i = 0; i < size; i++)
            listView.collapseGroup(i);
        main_title.setText(content_string(event.title));

        Log.d(TAG, "onClick: click");
        t_event1 = eventGetter.getEventById("5ec7ce549fced0a24bf419a1");
        Log.d(TAG, "getRelatedEvents after change: size 5ec7ce549fced0a24bf419a1 " + t_event1.related_events.size());

        adapter.changeEvent(event);
        adapter.notifyDataSetChanged();
    }

    public void onResposeFinished() {
        eventGetter.praseResponse();
        curr_event = eventGetter.id_to_event_number.get(input_event._id);
        load_finished = true;
        root_view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        getRelatedEvents();
        total_events = eventGetter.events_list.size();
        changeEvent(input_event);
    }

    public void getRelatedEvents() {
        ArrayList<Event> events_list = eventGetter.events_list;
        int size = events_list.size();
        Log.d(TAG, "getRelatedEvents: size " + size);
        BufferedReader reader = null;
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().openRawResource(R.raw.related_events));
            BufferedReader bufReader = new BufferedReader(inputReader);
            Scanner scanner = new Scanner(bufReader);

            for(Event event : events_list) {
                for(int i = 0; i < 10; i++) {
                    int t = scanner.nextInt();
                    Event t_event = events_list.get(t);
                    event.related_events.add(t_event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: button");
    }
}
