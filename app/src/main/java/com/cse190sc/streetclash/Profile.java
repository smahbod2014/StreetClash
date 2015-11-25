package com.cse190sc.streetclash;

import android.widget.ImageView;

/**
 * Created by Aki on 11/12/15.
 */
public class Profile {
    private ImageView m_ProfileImage;
    private String m_Name;
    private String m_Age;
    private String m_Gender;
    private String m_AboutMe;
    private String[] m_Skills;

    public Profile(){}
    public Profile(String name, String age,String gender,String aboutMe, String[] skills, ImageView image){
        this.m_Name=name;
        this.m_Age=age;
        this.m_Gender=gender;
        this.m_AboutMe=aboutMe;
        this.m_Skills=skills;
        this.m_ProfileImage= image;
    }

    public String getM_Age() {
        return m_Age;
    }

    public void setM_Age(String m_Age) {
        this.m_Age = m_Age;
    }

    public ImageView getM_ProfileImage() {
        return m_ProfileImage;
    }

    public void setM_ProfileImage(ImageView m_ProfileImage) {
        this.m_ProfileImage = m_ProfileImage;
    }

    public String getM_Name() {
        return m_Name;
    }

    public void setM_Name(String m_Name) {
        this.m_Name = m_Name;
    }

    public String getM_Gender() {
        return m_Gender;
    }

    public void setM_Gender(String m_Gender) {
        this.m_Gender = m_Gender;
    }

    public String getM_AboutMe() {
        return m_AboutMe;
    }

    public void setM_AboutMe(String m_AboutMe) {
        this.m_AboutMe = m_AboutMe;
    }

    public String[] getM_Skills() {
        return m_Skills;
    }

    public void setM_Skills(String[] m_Skills) {
        this.m_Skills = m_Skills;
    }


}