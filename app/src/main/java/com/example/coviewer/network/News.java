package com.example.coviewer.network;

public class News implements java.io.Serializable{
    public String _id;
    public String date, type, title, content;
    public String entities, geoInfo, lang, category, related_events, urls, source;
    boolean visited = false;
}