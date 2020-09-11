package com.example.coviewer.network;

import android.graphics.Bitmap;

public class Expert implements java.io.Serializable {
    public String id;
    public String name, position, avatar, affiliation;
    public String bio;
    public Bitmap bitmap;

    boolean is_passedaway;
}
