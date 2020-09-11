package com.example.coviewer;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.coviewer.network.Event;
import com.example.coviewer.network.EventGetter;
import com.example.coviewer.network.GraphEntity;
import com.example.coviewer.network.GraphGetter;

import java.util.ArrayList;
import java.util.List;

public class EventClusterAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    private static final String TAG = "EventListAdapter";
    Event event;
    ArrayList<ArrayList<Event> > label_events;
    event_cluster_fragment parent;
    EventGetter eventGetter;
    ArrayList<String> labels;
    ArrayList<ArrayList<Event> > label_list;

    public EventClusterAdapter(event_cluster_fragment parent, EventGetter eventGetter) {
        this.parent = parent;
        this.eventGetter = eventGetter;
        labels = new ArrayList<>();
        label_list = new ArrayList<>();
    }

    public void onResposeFinished() {
        labels = parent.labels;
        label_list = parent.label_list;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        Log.d(TAG, "getGroupCount: " + labels.size());
        return labels.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return label_list.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return labels.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return label_list.get(i).get(i1);
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
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_cluster_label, viewGroup, false);
        TextView textView = (TextView) view.findViewById(R.id.event_cluster_label_text);
        textView.setText(labels.get(i));
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final Event t_event;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_cluster_entry, viewGroup, false);
        t_event = label_list.get(i).get(i1);
        TextView textView = (TextView) view.findViewById(R.id.event_cluster_entry_text);
        textView.setText(t_event.title);
        view.setTag(t_event);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", t_event);
                Navigation.findNavController(view).navigate(R.id.event_main_fragment, bundle);
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: click");
        if(view.getTag() instanceof Event) {
            Event tag = (Event) view.getTag();
            Log.d(TAG, "onClick: " + tag.title);
        }
    }
}
