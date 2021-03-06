package com.cse190sc.streetclash;

import android.widget.ImageView;

/**
 * Created by Aki on 11/12/15.
 */
public class Profile {
    public String userID;
    public String name;
    public String imageBytes;
    public double distance;
    public boolean inRange = true;
    public boolean imageChanged = true;

    public Profile(String name, String userID, String imageBytes, double distance) {
        this.name = name;
        this.userID = userID;
        this.imageBytes = imageBytes;
        this.distance = distance;
    }

    public Profile(Profile p) {
        userID = p.userID;
        name = p.name;
        imageBytes = p.imageBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Profile))
            return false;
        Profile p = (Profile) o;
        return this.userID.equals(p.userID);
    }
}