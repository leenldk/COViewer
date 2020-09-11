package com.example.coviewer;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coviewer.network.EntityRelation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GraphPropertiesAdapter extends ArrayAdapter<Pair<String, String>> {
    ArrayList<Pair<String, String>> properties;
    private static final String TAG = "GraphEntryListAdapter";
    GraphListAdapter parentAdapter;

    public GraphPropertiesAdapter(Context context, ArrayList<Pair<String, String>> properties, GraphListAdapter parentAdapter) {
        super(context, R.layout.graph_relation, (List<Pair<String, String>>)properties);
        this.properties = properties;
        this.parentAdapter = parentAdapter;
        Log.d(TAG, "GraphEntryListAdapter: properties size " + properties.size());
    }

    @Override
    public int getCount() {
        return properties.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Pair<String, String> property = getItem(position);
        //GraphEntryListViewHolder viewHolder = new GraphEntryListViewHolder();
        Log.d(TAG, "getView: position " + position);
        view = LayoutInflater.from(getContext()).inflate(R.layout.graph_properties, null);
        TextView keyView = (TextView) view.findViewById(R.id.property_key);
        TextView valView = (TextView) view.findViewById(R.id.property_val);
        keyView.setText(property.first);
        valView.setText(property.second);
        return view;
    }
}
