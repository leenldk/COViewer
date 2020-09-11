package com.example.coviewer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coviewer.network.Expert;
import com.example.coviewer.network.News;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link news_body_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class expert_body_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public expert_body_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment news_body_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static expert_body_fragment newInstance(String param1, String param2) {
        expert_body_fragment fragment = new expert_body_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TAG = "expert_body_fragment";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.expert_body_fragment, container, false);
        Expert expert = (Expert)getArguments().getSerializable("expert");
        if(expert == null) Log.d(TAG, "onCreateView: null expert");
        ((ImageView)ret_view.findViewById(R.id.expert_body_image)).setImageBitmap(expert.bitmap);
        ((TextView)ret_view.findViewById(R.id.expert_body_name)).setText(expert.name);
        ((TextView)ret_view.findViewById(R.id.expert_body_bio)).setText(expert.bio);
        ((TextView)ret_view.findViewById(R.id.expert_pos)).setText(expert.position);
        ((TextView)ret_view.findViewById(R.id.expert_aff)).setText(expert.affiliation);
        Log.d(TAG, "onCreateView: " + expert.bio);
        return ret_view;
    }
}