package com.example.coviewer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link news_main_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class news_main_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public news_main_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment news_main_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static news_main_fragment newInstance(String param1, String param2) {
        news_main_fragment fragment = new news_main_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view =  inflater.inflate(R.layout.news_main_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) ret_view.findViewById(R.id.news_list_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity());
        recyclerView.setLayoutManager(layoutManager);

        String []a = {
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
        };
        //*
        NewsListAdapter mAdapter= new NewsListAdapter(a);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    System.out.println("bottom");

                }
                if (!recyclerView.canScrollVertically(-1)) {
                    System.out.println("top");
                }
            }
        });
        return ret_view;
    }
}