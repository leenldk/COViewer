package com.example.coviewer;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.coviewer.network.GraphEntity;
import com.example.coviewer.network.GraphGetter;

import java.util.ArrayList;
import java.util.List;

public class GraphListAdapter extends BaseExpandableListAdapter {
    ArrayList<GraphEntity> group_list;
    public static Handler network_handler;
    GraphGetter graphGetter;
    GraphEntryListAdapter relationAdapter;
    GraphPropertiesAdapter propertiesAdapter;

    public GraphListAdapter(Handler handler) {
        group_list = new ArrayList<>();
        //group_list.add("test1");
        //group_list.add("test2");
        //group_list.add("test3");
        network_handler = handler;
        graphGetter = new GraphGetter(handler);
    }

    void netRequest(String keyword) {
        graphGetter.getGraph(keyword);
    }

    void updateFromResponse() {
        graphGetter.praseResponse();
        group_list = graphGetter.entity_list;
        graphGetter.getImages();
    }
    @Override
    public int getGroupCount() {
        return group_list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i)
    {
        return group_list.get(i).name;
    }

    @Override
    public Object getChild(int i, int i1) {
        return group_list.get(i);
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
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.graph_content, viewGroup, false);
        ((TextView)view).setText((String)getGroup(i));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.graph_entry, viewGroup, false);
        GraphEntity entity = (GraphEntity) getChild(i, i1);
        TextView text = (TextView) view.findViewById(R.id.entry_text);
        ImageView img = (ImageView) view.findViewById(R.id.entry_image);
        boolean empty_desc = (entity.description.equals("") || entity.description == null);
        boolean empty_bitmap = (entity.bitmap == null);
        if(empty_bitmap && empty_desc) {
            text.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
        } else if(empty_bitmap) {
            img.setVisibility(View.GONE);
            text.setText(entity.description);
        } else if(empty_desc) {
            text.setVisibility(View.GONE);
            img.setImageBitmap(entity.bitmap);
        } else {
            text.setText(entity.description);
            img.setImageBitmap(entity.bitmap);
        }

        relationAdapter = new GraphEntryListAdapter(viewGroup.getContext(), entity.relations, this);
        ListView graphEntry = view.findViewById(R.id.graph_entry);
        graphEntry.setAdapter(relationAdapter);
        Utility.setListViewHeightBasedOnChildren(graphEntry);

        propertiesAdapter = new GraphPropertiesAdapter(viewGroup.getContext(), entity.properities, this);
        ListView graphProperties = view.findViewById(R.id.graph_properties);
        graphProperties.setAdapter(propertiesAdapter);
        Utility.setListViewHeightBasedOnChildren(graphProperties);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
