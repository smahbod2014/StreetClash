package com.cse190sc.streetclash;

import android.widget.ImageView;

/**
 * Created by Aki on 11/12/15.
 */
public class Profile {
    private String musername;
    private String mname;
    private ImageView photo;

    public Profile(){}

    public String getMusername() {
        return musername;
    }

    public String getMname() {
        return mname;
    }

    public ImageView getPhoto() {
        return photo;
    }

    public void setMusername(String musername) {
        this.musername = musername;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    public Profile(String u, String n, ImageView p){
        this.musername = u;
        this.mname = n;
        this.photo = p;
    }
}
