package com.example.coviewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coviewer.network.News;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>{
    public ArrayList<News> newslist;
    static public class NewsListViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout layout;
        public NewsListViewHolder(ConstraintLayout v) {
            super(v);
            layout = v;
        }
    }
    public NewsListAdapter() {
        newslist = new  ArrayList<News>();
    }
    @Override
    public NewsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_piece_layout, parent, false);
        NewsListViewHolder vh = new NewsListViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(NewsListViewHolder holder, final int position) {
        ((TextView)holder.layout.findViewById(R.id.news_title)).setText(newslist.get(position).title);
        ((TextView)holder.layout.findViewById(R.id.news_source)).setText(newslist.get(position).date);
        ((TextView)holder.layout.findViewById(R.id.news_time)).setText(newslist.get(position).source);
        final News news = newslist.get(position);
        if(news_main_fragment.praser.viewed(news)) {
            ((TextView)holder.layout.findViewById(R.id.news_title)).setTextColor(0xffaaaaaa);
        }else {
            ((TextView)holder.layout.findViewById(R.id.news_title)).setTextColor(0xff000000);
        }
        ((TextView)holder.layout.findViewById(R.id.news_title)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("news", news);
                news_main_fragment.praser.markAsHistory(news);
                Navigation.findNavController(v).navigate(R.id.action_view_news_body, bundle);

            }
        });
        //TextView textView = (TextView)holder.layout.findViewById(R.id.textView5);
        //textView.setText(data[position]);
    }
    @Override
    public int getItemCount() {
        return newslist.size();
    }
}
