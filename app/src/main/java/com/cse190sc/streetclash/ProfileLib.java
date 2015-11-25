package com.cse190sc.streetclash;

import java.util.ArrayList;
import java.util.*;
import android.content.Context;
import android.widget.ImageView;
/**
 * Created by Aki on 11/12/15.
 */
public class ProfileLib {
    private static ProfileLib proLib;
    private ArrayList<Profile> lib;

    public static ProfileLib get(Context context){
        if(proLib == null){
            proLib = new ProfileLib(context);
        }
        return proLib;
    }

    private ProfileLib(Context context){
        lib = new ArrayList<Profile>();
        for(int i=0; i<10; ++i){
            Profile pro = new Profile();
            pro.setM_Name("student"+i);
            pro.setM_Age("" + i);
            pro.setM_Gender("male");
            pro.setM_AboutMe("I am a student");
            pro.setM_Skills(new String[]{"java","android"});
            //pro.setPhoto();
            lib.add(pro);
        }
    }

    public List<Profile> getProfile(){
        return lib;
    }

    public Profile getProfile(String user){
        for (Profile prof: lib){
            if(prof.getM_Name() == user){
                return prof;
            }
        }
        return null;
    }
}