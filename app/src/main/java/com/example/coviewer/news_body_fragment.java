package com.example.coviewer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.coviewer.network.News;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link news_body_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class news_body_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public news_body_fragment() {
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
    public static news_body_fragment newInstance(String param1, String param2) {
        news_body_fragment fragment = new news_body_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        View ret_view = inflater.inflate(R.layout.news_body_fragment, container, false);
        News news = (News)getArguments().getSerializable("news");
        ((TextView)ret_view.findViewById(R.id.news_body_title)).setText(news.title);
        ((TextView)ret_view.findViewById(R.id.news_body_content)).setText(news.content);
        ((TextView)ret_view.findViewById(R.id.news_body_date)).setText(news.date);
        ((TextView)ret_view.findViewById(R.id.news_body_source)).setText(news.source);
        ((Button)ret_view.findViewById(R.id.button_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareDialog dialog = new ShareDialog();
                dialog.show(getActivity().getSupportFragmentManager(), "share_dialog");
            }
        });
        return ret_view;
    }
    static public class ShareDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            String a[] = {"微信", "微博"};
            builder.setTitle("分享")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setItems(a,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            return builder.create();
        }
    }
}