package com.example.coviewer.network;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.ArrayList;

public class GraphEntity {
    public String name;
    public String description, img;
    public Bitmap bitmap;
    public ArrayList<EntityRelation> relations;
    public ArrayList<Pair<String, String> > properities;
    GraphEntity() {
        relations = new ArrayList<>();
        properities = new ArrayList<>();
    }
}
