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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class graph_main_fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "graph_main_fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public graph_main_fragment() {
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
    public static data_main_fragment newInstance(String param1, String param2) {
        data_main_fragment fragment = new data_main_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static final int NETCALL_COMPLETE = 1;
    public static final int IMAGEGET_COMPLETE = 2;

    private GraphListAdapter adapter;
    ExpandableListView listView;
    public static Handler network_handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        network_handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NETCALL_COMPLETE) {
                    Log.d(TAG, "handleMessage: graph call complete");
                    adapter.updateFromResponse();
                } else if(msg.what == IMAGEGET_COMPLETE) {
                    Log.d(TAG, "handleMessage: image get complete");
                    adapter.notifyDataSetChanged();
                }
            }
        };
        adapter = new GraphListAdapter(network_handler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret_view = inflater.inflate(R.layout.graph_main_fragment, container, false);
        listView = ret_view.findViewById(R.id.expandable_graph_list);
        listView.setAdapter(adapter);
        Button btn = (Button)ret_view.findViewById(R.id.graph_button);
        btn.setOnClickListener(this);
        return ret_view;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: button");
        adapter.netRequest("病毒");
    }
}
