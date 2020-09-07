package com.example.coviewer.network;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class GraphEntity {
    public String name;
    public String description, img;
    public Bitmap bitmap;
    public ArrayList<EntityRelation> relations;
    GraphEntity() {
        relations = new ArrayList<>();
    }
}
