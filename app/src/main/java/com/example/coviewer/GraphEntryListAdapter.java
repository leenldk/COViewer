package com.example.coviewer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coviewer.network.EntityRelation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GraphEntryListAdapter extends ArrayAdapter<EntityRelation> implements View.OnClickListener {
    ArrayList<EntityRelation> entities;
    private static final String TAG = "GraphEntryListAdapter";
    GraphListAdapter parentAdapter;

    public GraphEntryListAdapter(Context context, ArrayList<EntityRelation> entities, GraphListAdapter parentAdapter) {
        super(context, R.layout.graph_relation, (List<EntityRelation>)entities);
        this.entities = entities;
        this.parentAdapter = parentAdapter;
        Log.d(TAG, "GraphEntryListAdapter: entity size " + entities.size());
    }

    class GraphEntryListViewHolder {
        TextView label;
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        EntityRelation entity = getItem(position);
        //GraphEntryListViewHolder viewHolder = new GraphEntryListViewHolder();
        Log.d(TAG, "getView: position " + position);
        view = LayoutInflater.from(getContext()).inflate(R.layout.graph_relation, null);
        TextView textView = (TextView) view.findViewById(R.id.relation_label);
        textView.setText(entity.label);
        textView.setOnClickListener(this);
        ImageView imageView = (ImageView) view.findViewById(R.id.relation_image);

        TextView relation_string = (TextView) view.findViewById(R.id.relation_string);
        relation_string.setText(entity.relation);

        if(entity.forward)
            imageView.setImageResource(R.drawable.right_arrow);
        else imageView.setImageResource(R.drawable.left_arrow);
        return view;
    }

    @Override
    public void onClick(View view) {
        parentAdapter.netRequest((String)((TextView)view).getText());
    }
}
