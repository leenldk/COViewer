package com.example.coviewer;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coviewer.network.GraphEntity;
import com.example.coviewer.network.GraphGetter;

import java.util.ArrayList;
import java.util.List;

public class GraphListAdapter extends BaseExpandableListAdapter {
    ArrayList<GraphEntity> group_list;
    public static Handler network_handler;
    GraphGetter graphGetter;

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
        text.setText(entity.description);
        if(entity.bitmap != null) {
            ImageView img = (ImageView) view.findViewById(R.id.entry_image);
            img.setImageBitmap(entity.bitmap);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
