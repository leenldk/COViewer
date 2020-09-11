package com.example.coviewer;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.coviewer.network.EpidemicGetter;
import com.example.coviewer.network.EpidemicMap;
import com.example.coviewer.network.JsonPraser;
import com.example.coviewer.network.News;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.example.coviewer.network.JsonPraser.NETCALL_COMPLETE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link data_main_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class data_main_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public data_main_fragment() {
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
    private static final String TAG = "data_main_fragment";
    
    private Activity activity;
    /// private HorizontalBarChart chart;
    /// private SeekBar seekBarX, seekBarY;
    /// private TextView tvX, tvY;
    public static Handler network_handler;
    public EpidemicGetter epidemicGetter;
    public boolean case_global;
    public boolean[] case_type;
    ExpandableListView listView;
    private View root_view;
    DataChartListAdapter adapter;
    public Resources resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        activity = getActivity();
        /*activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); */

        network_handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NETCALL_COMPLETE) {
                    Log.d(TAG, "handleMessage: epidemicGetter call complete");
                    epidemicGetter.praseResponse();
                    root_view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    updateChart();
                }
            }
        };
        resources = getResources();
        epidemicGetter = new EpidemicGetter(network_handler);
        adapter = new DataChartListAdapter(this, epidemicGetter, activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.data_main_fragment, container, false);
        listView = ret_view.findViewById(R.id.expandable_data_list);
        listView.setAdapter(adapter);
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition == 0) return;
                int count=listView.getExpandableListAdapter().getGroupCount();
                for(int i=1;i<count; i++){
                    if(groupPosition != i){
                        listView.collapseGroup(i);
                    }
            }}});

        Log.d(TAG, "onCreateView: in getEpidemic");
        epidemicGetter.getEpidemic();

        RecyclerView recyclerView = (RecyclerView) ret_view.findViewById(R.id.data_list_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        root_view = ret_view;
        //*
        /*
        The array contains elements on the list
         */
        String []a = {"北京", "tianjin", "butthole"};
        DataListAdapter adapter= new DataListAdapter(a);
        recyclerView.setAdapter(adapter);
        TabLayout tab_global = ret_view.findViewById(R.id.tab_global);
        TabLayout tab_three = ret_view.findViewById(R.id.tab_three);
        case_global = false;
        case_type = new boolean[3];
        case_type[0] = true;
        case_type[1] = false;
        case_type[2] = false;
        search();
        tab_global.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    case_global = false;
                    search();
                }
                if(tab.getPosition() == 1) {
                    case_global = true;
                    search();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tab_three.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                for(int i = 0; i < case_type.length; i++) {
                    case_type[i] = false;
                }
                case_type[tab.getPosition()] = true;
                search();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return ret_view;
    }
    private void search() {
        Log.d(TAG, "### search: changing value");
        System.out.println(case_global);
        System.out.println(case_type);  //size-3 array, 0:confirmed, 1:cured, 2: death
        EpidemicMap map;
        if(case_global) {
            if(case_type[0]) map = epidemicGetter.world_confirmed;
            else if(case_type[1]) map = epidemicGetter.world_cured;
            else map = epidemicGetter.world_dead;
        } else {
            if(case_type[0]) map = epidemicGetter.china_confirmed;
            else if(case_type[1]) map = epidemicGetter.china_cured;
            else map = epidemicGetter.china_dead;
        }
        adapter.updateChart(map);
    }


    private void updateChart() {
        adapter.updateChart(epidemicGetter.china_confirmed);
        //setTest();
    }

    /****
    private void setTest() {
        float barWidth = 0.9f;
        float spaceForBar = 1f;
        XAxis xl = chart.getXAxis();
        xl.setLabelCount(5);
        xl.setGranularity(1);
        ValueFormatter valueFormatter = new ValueFormatter() {
            private final String[] label = new String[]{"北京","changchun", "hongkong", "上海", "tibet"};

            @Override
            public String getFormattedValue(float value) {
                if((int)value < label.length && (int)value >= 0)
                    return label[(int)value];
                else return "";
            }
        };
        xl.setValueFormatter(valueFormatter);

        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            values.add(new BarEntry(i, i + 10));
        }
        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(barWidth);
            chart.setData(data);
        }
    }

    private void setData(int count, float range) {
        float barWidth = 9f;
        float spaceForBar = 10f;
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            values.add(new BarEntry(i * spaceForBar, val));
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(barWidth);
            chart.setData(data);
        }
    }
    ***/

    static public class DataListAdapter extends RecyclerView.Adapter<com.example.coviewer.data_main_fragment.DataListAdapter.DataListViewHolder>{
        String []a;
        static public class DataListViewHolder extends RecyclerView.ViewHolder {
            public ConstraintLayout layout;
            public DataListViewHolder(ConstraintLayout v) {
                super(v);
                layout = v;
            }
        }
        public DataListAdapter(String []_a) {
            a = _a;
        }
        @Override
        public com.example.coviewer.data_main_fragment.DataListAdapter.DataListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.data_piece_layout, parent, false);
            com.example.coviewer.data_main_fragment.DataListAdapter.DataListViewHolder vh = new com.example.coviewer.data_main_fragment.DataListAdapter.DataListViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(com.example.coviewer.data_main_fragment.DataListAdapter.DataListViewHolder holder, final int position) {
            ((Button)holder.layout.findViewById(R.id.button)).setText(a[position]);
            ((Button)holder.layout.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    Define what to do next
                     */
                    System.out.println(position);
                }
            });
            //TextView textView = (TextView)holder.layout.findViewById(R.id.textView5);
            //textView.setText(data[position]);
        }
        @Override
        public int getItemCount() {
            return a.length;
        }
    }


}