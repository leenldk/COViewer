package com.example.coviewer.network;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    public String _id;
    public String title;
    public double influence;
    public ArrayList<String> labels;
    public ArrayList<String> related_ids;
    public ArrayList<Event> related_events;

    public Event() {
        labels = new ArrayList<>();
        related_ids = new ArrayList<>();
        related_events = new ArrayList<>();
    }
}
