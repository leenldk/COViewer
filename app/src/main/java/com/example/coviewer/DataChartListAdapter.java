package com.example.coviewer;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coviewer.network.EpidemicGetter;
import com.example.coviewer.network.EpidemicMap;
import com.example.coviewer.network.Event;
import com.example.coviewer.network.EventGetter;
import com.example.coviewer.network.GraphEntity;
import com.example.coviewer.network.GraphGetter;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataChartListAdapter extends BaseExpandableListAdapter implements OnChartValueSelectedListener {
    private static final String TAG = "DataListAdapter";
    EpidemicGetter epidemicGetter;
    data_main_fragment parent;
    EpidemicMap map;
    Activity activity;
    final int max_entries = 20;
    final int max_timeline = 20;

    HorizontalBarChart main_chart;
    RecyclerView main_recyclerView;

    public DataChartListAdapter(data_main_fragment parent, EpidemicGetter epidemicGetter, Activity activity) {
        this.parent = parent;
        this.epidemicGetter = epidemicGetter;
        this.activity = activity;
        map = new EpidemicMap();
    }

    public void updateChart(EpidemicMap map) {
        this.map = map;
        map.dosort();
        Log.d(TAG, "### updateChart: update main_chart");
        Log.d(TAG, "updateChart: group count "+ getGroupCount());
        //updateMainChart(main_chart);
        Log.d(TAG, "updateChart: ");
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }

    @Override
    public int getGroupCount() {
        return map.district.size() + 1;
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i)
    {
        if(i == 0) return main_chart;
        else return map.district.get(i - 1);
    }

    @Override
    public Object getChild(int i, int i1) {
        if(i == 0) return main_chart;
        else return map.district.get(i - 1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1 * getGroupCount() + i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if(i == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_overall_label, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.data_overall_label_text);
            textView.setText("overall");
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_entry_label, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.data_entry_label_text);
            int pos = map.district.size() - i;
            textView.setText(map.district.get(pos));
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        Event t_event;
        if(i == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_overall_layout, viewGroup, false);
            main_chart = view.findViewById(R.id.chart1);
            initMainChart(main_chart);
            updateMainChart(main_chart);

            /// view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_entry_label, viewGroup, false);
            /// TextView textView = (TextView) view.findViewById(R.id.data_entry_label_text);
            /// textView.setText("test");

        } else {
            int pos = map.district.size() - i;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_entry_layout, viewGroup, false);
            LineChart chart = view.findViewById(R.id.line_chart);
            initEntryChart(chart);
            String key = map.district.get(pos);
            ArrayList<Integer> t_array = map.timeline_map.get(key);
            updateEntryChart(chart, t_array);
        }
        return view;
    }

    public void initEntryChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);

        YAxis yl = chart.getAxisLeft();
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yr = chart.getAxisRight();
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        chart.animateY(500);


        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
    }

    private void updateEntryChart(LineChart chart, final ArrayList<Integer> array) {

        int color;
        LineDataSet set1;
        if(parent.case_type[0]) color = R.color.barConfirmed;
        else if(parent.case_type[1]) color = R.color.barCured;
        else color = R.color.barDeath;

        /*Matrix matrix = new Matrix();
        if(parent.case_global)
            matrix.postScale(0.9f, 1f);
        else
            matrix.postScale(0.9f, 1f);
        chart.getViewPortHandler().refresh(matrix, chart, false); */

        Log.d(TAG, "### updateChart: color " + color);
        Log.d(TAG, "### updateChart: type " + parent.case_type[0] + parent.case_type[1] + parent.case_type[2]);

        float barWidth = 0.9f;
        float spaceForBar = 1f;
        XAxis xl = chart.getXAxis();
        //xl.setLabelCount(map.number.size());


        Log.d(TAG, "updateChart: " + map.number.size());
        int v1 = 0;
        int v2 = array.size();
        //if(v2 > max_timeline) v2 = max_timeline;
        final int start_entry = v1;
        final int end_entry = v2;

        //Calendar calendar = Calendar.getInstance();

        //xl.setLabelCount(end_entry - start_entry);
        //xl.setGranularity(1);

        /**ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                label = new ArrayList<>();
                for(int i = start_entry; i < end_entry; i++)
                {
                    label.add(map.district.get(i));
                    //Log.d(TAG, "getFormattedValue: add district " + map.district.get(i) + value);
                }
                int t = (int)value;
                if(t < label.size() && t >= 0)
                    return label.get(t);
                else return "";
            }
        }; */
        //xl.setValueFormatter(valueFormatter);

        ArrayList<Entry> values = new ArrayList<>();
        for(int i = end_entry - 1; i >= start_entry; i -= 20) {
            //values.add(new BarEntry(i, i + 10));
            int t = array.get(i);
            values.add(new Entry(end_entry - i, t));
            Log.d(TAG, "updateEntryChart: " + (end_entry - i) + " " + array.get(i));
        }

        //values.add(new Entry(end_entry - 0, array.get(0)));
        //values.add(new Entry(end_entry - 1, array.get(1)));
        //values.add(new Entry(end_entry - 2, array.get(2)));
        //values.add(new Entry(33, 3482));

        set1 = new LineDataSet(values, "人数");

        set1.setDrawIcons(false);

        set1.setColor(parent.getResources().getColor(color));
        set1.setCircleColor(parent.getResources().getColor(color));
        set1.setLineWidth(2f);
        set1.setCircleRadius(5f);
        List<ILineDataSet> list = new ArrayList<>();
        list.add(set1);

        LineData data = new LineData(list);
        data.setValueTextSize(10f);
        chart.setData(data);
        chart.invalidate();
        chart.notifyDataSetChanged();
        //}
    }

    public void initMainChart(HorizontalBarChart chart) {
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);

        YAxis yl = chart.getAxisLeft();
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yr = chart.getAxisRight();
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        chart.setFitBars(true);
        chart.animateY(1500);


        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
    }

    private void updateMainChart(HorizontalBarChart chart) {
        Log.d(TAG, "updateChart: begin");
        Log.d(TAG, "updateChart: end sort");

        int color;
        BarDataSet set1;
        if(parent.case_type[0]) color = R.color.barConfirmed;
        else if(parent.case_type[1]) color = R.color.barCured;
        else color = R.color.barDeath;

        /*
        if(case_global)
            chart.getViewPortHandler().getMatrixTouch().postScale(1f, 15f);
        else chart.getViewPortHandler().getMatrixTouch().postScale(1f, 2f);
        Log.d(TAG, "updateChart: " + chart.getViewPortHandler().getMatrixTouch());
           */
        Matrix matrix = new Matrix();
        if(parent.case_global)
            matrix.postScale(0.9f, 1f);
        else
            matrix.postScale(0.9f, 1f);
        chart.getViewPortHandler().refresh(matrix, chart, false);


        //if(case_global) chart.setScaleY(5);
        //else chart.setScaleY(1);

        //Log.d(TAG, "### updateChart: color " + R.color.barConfirmed);
        Log.d(TAG, "### updateChart: color " + color);
        Log.d(TAG, "### updateChart: type " + parent.case_type[0] + parent.case_type[1] + parent.case_type[2]);

        float barWidth = 0.9f;
        float spaceForBar = 1f;
        XAxis xl = chart.getXAxis();
        //xl.setLabelCount(map.number.size());


        Log.d(TAG, "updateChart: " + map.number.size());
        int v1 = map.district.size() - max_entries;
        int v2 = map.district.size();
        if(parent.case_global) {
            v1--;
            v2--;
        }

        final int start_entry = v1;
        final int end_entry = v2;

        xl.setLabelCount(end_entry - start_entry);
        xl.setGranularity(1);

        ValueFormatter valueFormatter = new ValueFormatter() {
            private ArrayList<String> label;
            //new String[]{"北京","changchun", "hongkong", "上海", "tibet"};
            @Override
            public String getFormattedValue(float value) {
                label = new ArrayList<>();
                for(int i = start_entry; i < end_entry; i++)
                {
                    label.add(map.district.get(i));
                    //Log.d(TAG, "getFormattedValue: add district " + map.district.get(i) + value);
                }
                int t = (int)value;
                if(t < label.size() && t >= 0)
                    return label.get(t);
                else return "";
            }
        };
        xl.setValueFormatter(valueFormatter);

        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = start_entry; i < end_entry; i++) {
            //values.add(new BarEntry(i, i + 10));
            values.add(new BarEntry(i - start_entry, map.number.get(i)));
            Log.d(TAG, "getFormattedValue: add value " + map.number.get(i));
        }

        /* if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setColor(color);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else { */

        set1 = new BarDataSet(values, "人数");

        set1.setDrawIcons(false);

        set1.setColor(parent.getResources().getColor(color));
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(barWidth);
        Log.d(TAG, "### updateChart: size " + data.getEntryCount());
        chart.setData(data);
        Log.d(TAG, "### updateChart: set data");
        Log.d(TAG, "### updateChart: count " + chart.getData().getEntryCount());
        chart.invalidate();
        chart.notifyDataSetChanged();
        //}
    }

    private final RectF mOnValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        main_chart.getBarBounds((BarEntry) e, bounds);

        MPPointF position = main_chart.getPosition(e, main_chart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency());

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {}


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
