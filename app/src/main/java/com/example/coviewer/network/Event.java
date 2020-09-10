package com.example.coviewer.network;

import java.util.ArrayList;

public class Event {
    public String _id;
    public String title;
    public double influence;
    public ArrayList<String> labels;
    public ArrayList<String> related_ids;

    public Event() {
        labels = new ArrayList<>();
        related_ids = new ArrayList<>();
    }
}
