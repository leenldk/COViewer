package com.example.coviewer;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.coviewer.network.Event;
import com.example.coviewer.network.EventGetter;
import com.example.coviewer.network.GraphEntity;
import com.example.coviewer.network.GraphGetter;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    private static final String TAG = "EventListAdapter";
    Event event;
    ArrayList<ArrayList<Event> > label_events;
    event_main_fragment parent;
    EventGetter eventGetter;
    ArrayList<Event> relate_events;
    ArrayList<String> labels;
    public static final int max_label_elements = 10; // 单个label拥有最多元素数量

    public EventListAdapter(event_main_fragment parent, EventGetter eventGetter) {
        this.parent = parent;
        this.eventGetter = eventGetter;
        label_events = new ArrayList<>();
        relate_events = new ArrayList<>();
        labels = new ArrayList<>();
    }

    public void changeEvent(Event event) {

        this.event = event;

        label_events.clear();

        labels.clear();

        for(String label : event.labels) {
            ArrayList<Event> t_events = eventGetter.getLabelEvents(label, event);
            if(t_events.size() == 0) continue;
            label_events.add(t_events);
            labels.add(label);
            Log.d(TAG, "changeEvent: t_events size : " + t_events.size());
        }
        /*for(String id : event.related_ids) {
            Event t_event = eventGetter.getEventById(id);
            if(t_event != null)
                relate_events.add(t_event);
        }*/
        Event t_event = eventGetter.getEventById(event._id);
        Log.d(TAG, "changeEvent: " + event.title);
        Log.d(TAG, "changeEvent: related size " + event.related_events.size() );
        Log.d(TAG, "changeEvent: id " + event._id );
        relate_events = t_event.related_events;
    }

    @Override
    public int getGroupCount() {
        return labels.size() + 1;
    }

    @Override
    public int getChildrenCount(int i) {
        if(i == 0) return relate_events.size();
        else {
            int t = label_events.get(i - 1).size();
            if(t > max_label_elements) t = max_label_elements;
            return t;
        }
    }

    @Override
    public Object getGroup(int i)
    {
        if(i == 0) return relate_events;
        return label_events.get(i - 1);
    }

    @Override
    public Object getChild(int i, int i1) {
        if(i == 0) return relate_events.get(i1);
        return label_events.get(i - 1).get(i1);
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_relation_title, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.event_relation_title_text);
            textView.setText("relations : ");
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_label_title, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.event_label_title_text);
            textView.setText(labels.get(i - 1));
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        Event t_event;
        if(i == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_title, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.event_title_text);
            t_event = relate_events.get(i1);
            textView.setText(t_event.title);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_title, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.event_title_text);
            t_event = label_events.get(i - 1).get(i1);
            textView.setText(t_event.title);
        }
        view.setTag(t_event);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: click");
        //Event t_event1 = eventGetter.getEventById("5ec7ce549fced0a24bf419a1");
        //Log.d(TAG, "getRelatedEvents: size 5ec7ce549fced0a24bf419a1 " + t_event1.related_events.size());
        if(view.getTag() instanceof Event) {
            Event tag = (Event) view.getTag();
            parent.changeEvent(tag);
            //changeEvent(tag);
            //notifyDataSetChanged();
        }
        //t_event1 = eventGetter.getEventById("5ec7ce549fced0a24bf419a1");
        //Log.d(TAG, "getRelatedEvents: size 5ec7ce549fced0a24bf419a1 " + t_event1.related_events.size());
    }
}
