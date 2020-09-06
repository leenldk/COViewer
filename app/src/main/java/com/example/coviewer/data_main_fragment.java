package com.example.coviewer;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.coviewer.network.EpidemicGetter;
import com.example.coviewer.network.EpidemicMap;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link data_main_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class data_main_fragment extends Fragment implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

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
    private HorizontalBarChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    public static Handler network_handler;
    public EpidemicGetter epidemicGetter;

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
                    updateChart();
                }
            }
        };
        epidemicGetter = new EpidemicGetter(network_handler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.data_main_fragment, container, false);


        tvX = ret_view.findViewById(R.id.tvXMax);
        tvY = ret_view.findViewById(R.id.tvYMax);

        seekBarX = ret_view.findViewById(R.id.seekBar1);
        seekBarY = ret_view.findViewById(R.id.seekBar2);

        seekBarY.setOnSeekBarChangeListener(this);
        seekBarX.setOnSeekBarChangeListener(this);

        chart = ret_view.findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);

        YAxis yl = chart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yr = chart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        chart.setFitBars(true);
        chart.animateY(1500);

        seekBarY.setProgress(50);
        seekBarX.setProgress(12);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        Log.d(TAG, "onCreateView: in getEpidemic");
        epidemicGetter.getEpidemic();

        return ret_view;
    }

    private void updateChart(final EpidemicMap map) {
        Log.d(TAG, "updateChart: begin");
        map.dosort();
        Log.d(TAG, "updateChart: end sort");
        Log.d(TAG, "updateChart: size " + map.number.size());
        Log.d(TAG, "updateChart: size " + map.district.size());
        float barWidth = 0.9f;
        float spaceForBar = 1f;
        XAxis xl = chart.getXAxis();
        xl.setLabelCount(map.number.size());
        xl.setGranularity(1);
        ValueFormatter valueFormatter = new ValueFormatter() {
            private final ArrayList<String> label = map.district;
                    //new String[]{"北京","changchun", "hongkong", "上海", "tibet"};
            @Override
            public String getFormattedValue(float value) {
                int t = (int)value;
                if(t < label.size() && t >= 0)
                    return label.get(t);
                else return "";
            }
        };
        xl.setValueFormatter(valueFormatter);

        ArrayList<BarEntry> values = new ArrayList<>();
        int size = map.number.size();
        for (int i = 0; i < size; i++) {
            //values.add(new BarEntry(i, i + 10));
            values.add(new BarEntry(i, map.number.get(i)));
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

    private void updateChart() {
        updateChart(epidemicGetter.china_confirmed);
        //setTest();
    }

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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        /*tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        setData(seekBarX.getProgress(), seekBarY.getProgress());
        chart.setFitBars(true);
        chart.invalidate(); */
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private final RectF mOnValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        chart.getBarBounds((BarEntry) e, bounds);

        MPPointF position = chart.getPosition(e, chart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency());

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {}
}