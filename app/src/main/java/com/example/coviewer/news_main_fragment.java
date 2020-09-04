package com.example.coviewer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link news_main_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class news_main_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static private String[] news_class_names = {"新闻", "论文"};
    static private boolean[] news_class_visible = {true, true};
    static private TabLayout.Tab[] news_class_tabs = {null, null};
    static private TabLayout tablayout;
    static private ClassHandler[] news_class_handler = {null, null};
    private int news_type = 0;
    private boolean view_history = false;
    public news_main_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment news_main_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static news_main_fragment newInstance(String param1, String param2) {
        news_main_fragment fragment = new news_main_fragment();
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
    private boolean scrolling_to_end = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view =  inflater.inflate(R.layout.news_main_fragment, container, false);


        RecyclerView recyclerView = (RecyclerView) ret_view.findViewById(R.id.news_list_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        String []a = {
                "1!!!!", "sfidojf", "fart", "fuck",
                "1!!!!", "sfidojf", "fart", "fuck",
        };
        //*
        NewsListAdapter mAdapter= new NewsListAdapter(a);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(!scrolling_to_end) {

                    }
                    scrolling_to_end = true;

                }else if (!recyclerView.canScrollVertically(-1)) {
                    if(!scrolling_to_end) {

                    }
                    scrolling_to_end = true;
                }else {
                    scrolling_to_end = false;
                }
            }
        });

        tablayout = (TabLayout)ret_view.findViewById(R.id.news_class_tablayout);
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TabLayout.Tab temp_tab = tablayout.newTab().setText("全部");
        tablayout.addTab(temp_tab);
        LinearLayout linearLayout = (LinearLayout)temp_tab.view;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = 200;
        linearLayout.setLayoutParams(layoutParams);
        for(int i = 0; i < news_class_names.length; i++) {
            news_class_handler[i] = new ClassHandler(tablayout.newTab().setText(news_class_names[i]));
            tablayout.addTab(news_class_handler[i].tab);
            linearLayout = (LinearLayout)news_class_handler[i].tab.view;
            layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = 200;
            linearLayout.setLayoutParams(layoutParams);
        }
        temp_tab = tablayout.newTab().setText("+");
        tablayout.addTab(temp_tab);
        linearLayout = temp_tab.view;
        layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = 100;
        linearLayout.setLayoutParams(layoutParams);
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("全部")) {
                    news_type = 0;
                }
                if(tab.getText().equals("+")) {
                    news_type = 1;
                }
                if(tab.getText().equals("+")) {
                    news_type = 2;
                }
                if(tab.getText().equals("+")) {
                    NewsClassDialog dialog = new NewsClassDialog();
                    dialog.show(getActivity().getSupportFragmentManager(), "choose_class_dialog");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getText().equals("+")) {
                    NewsClassDialog dialog = new NewsClassDialog();
                    dialog.show(getActivity().getSupportFragmentManager(), "choose_class_dialog");
                }
            }
        });
        ((TabLayout)ret_view.findViewById(R.id.history_tab_layout)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    view_history = false;
                }
                if(tab.getPosition() == 1) {
                    view_history = true;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return ret_view;
    }
    static public class ClassHandler {
        TabLayout.Tab tab;
        static private long animation_time = 250;
        public ClassHandler(TabLayout.Tab _tab) {
            tab = _tab;
        }
        public void create_animation() {
            ValueAnimator animation = ValueAnimator.ofInt(0, 200);
            animation.setDuration(animation_time);
            animation.start();
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    int animatedValue = (int)updatedAnimation.getAnimatedValue();
                    LinearLayout linearLayout = (LinearLayout)tab.view;
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                    layoutParams.width = animatedValue;
                    linearLayout.setLayoutParams(layoutParams);
                }
            });
        }
        public void delete_animation() {
            ValueAnimator animation = ValueAnimator.ofInt(200, 0);
            animation.setDuration(animation_time);
            animation.start();
            animation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    tablayout.removeTab(tab);
                }
                @Override
                public void onAnimationCancel(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    tablayout.removeTab(tab);
                }
                @Override
                public void onAnimationRepeat(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    tablayout.removeTab(tab);
                }
                @Override
                public void onAnimationStart(Animator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    tablayout.removeTab(tab);
                }
            });
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                    // You can use the animated value in a property that uses the
                    // same type as the animation. In this case, you can use the
                    // float value in the translationX property.
                    int animatedValue = (int)updatedAnimation.getAnimatedValue();
                    LinearLayout linearLayout = (LinearLayout)tab.view;
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                    layoutParams.width = animatedValue;
                    linearLayout.setLayoutParams(layoutParams);
                }
            });
        }
    }
    static public class NewsClassDialog extends DialogFragment {
        ArrayList selectedItems;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final boolean visible_update[] = new boolean[news_class_visible.length];
            for(int i = 0; i < visible_update.length; i++) {
                visible_update[i] = news_class_visible[i];
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle("编辑分类")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(news_class_names, visible_update,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    System.out.println(which);
                                    visible_update[which] = isChecked;
                                }
                            })
                    // Set the action buttons
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the selectedItems results somewhere
                            // or return them to the component that opened the dialog
                            long animation_duration = 250;
                            for(int i = 0; i < visible_update.length; i++) {
                                if(visible_update[i] != news_class_visible[i]) {
                                    if(visible_update[i]) {
                                        news_main_fragment.news_class_handler[i] = new ClassHandler(tablayout.newTab().setText(news_class_names[i]));
                                        tablayout.addTab(news_main_fragment.news_class_handler[i].tab, 1);
                                        news_class_handler[i].create_animation();
                                    }else {
                                        news_class_handler[i].delete_animation();
                                    }
                                }
                                news_class_visible[i] = visible_update[i];
                            }
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            return builder.create();
        }
    }

}