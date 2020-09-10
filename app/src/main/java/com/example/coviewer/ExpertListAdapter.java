package com.example.coviewer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coviewer.network.EntityRelation;
import com.example.coviewer.network.Expert;
import com.example.coviewer.network.ExpertGetter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ExpertListAdapter extends ArrayAdapter<Expert> {
    ExpertGetter expertGetter;
    private static final String TAG = "GraphEntryListAdapter";
    boolean highlight_mode;
    expert_main_fragment parent;
    ArrayList<Expert> experts;

    public ExpertListAdapter(Context context, ExpertGetter expertGetter, expert_main_fragment parent, boolean mode) {
        //super(context, R.layout.graph_relation, (List<EntityRelation>)entities);
        super(context, R.layout.expert_entry, (List<Expert>)expertGetter.experts);
        this.highlight_mode = mode;
        if(highlight_mode) experts = expertGetter.highlight_experts;
        else experts = expertGetter.passedaway_experts;
        this.parent = parent;
        this.expertGetter = expertGetter;
        Log.d(TAG, "GraphEntryListAdapter: entity size " + expertGetter.experts.size());
    }

    public void changeMode(boolean mode) {
        highlight_mode = mode;
        if(highlight_mode) experts = expertGetter.highlight_experts;
        else experts = expertGetter.passedaway_experts;
    }

    class GraphEntryListViewHolder {
        TextView label;
    }

    @Override
    public int getCount() {
        return experts.size();
    }

    @Nullable
    @Override
    public Expert getItem(int position) {
        return experts.get(position);
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        final Expert expert = getItem(position);

       //GraphEntryListViewHolder viewHolder = new GraphEntryListViewHolder();
        Log.d(TAG, "getView: position " + position);
        view = LayoutInflater.from(getContext()).inflate(R.layout.expert_entry, null);
        TextView textView = (TextView) view.findViewById(R.id.expert_entry_text);
        textView.setText(expert.name);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("expert", expert);
                Navigation.findNavController(view).navigate(R.id.action_view_expert_body, bundle);
            }
        });
        if(expert.bitmap != null) {
            ImageView img = (ImageView) view.findViewById(R.id.expert_entry_image);
            img.setImageBitmap(expert.bitmap);
        }
        return view;
    }
}
