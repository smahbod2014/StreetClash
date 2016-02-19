package com.cse190sc.streetclash;

import android.widget.ImageView;

public class ProfileListEntry {
    public String name;
    public String imageBytes;
    public String userID;

    public ProfileListEntry(String name, String imageBytes, String userID) {
        this.name = name;
        this.imageBytes = imageBytes;
        this.userID = userID;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ProfileListEntry))
            return false;
        ProfileListEntry p = (ProfileListEntry) other;
        return this.userID.equals(p.userID);
    }
}
