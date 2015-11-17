package com.cse190sc.streetclash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileViewActivity extends AppCompatActivity {

    private static final String TAG = "ProfileViewActivity";

    private boolean fromEditScreen = false;
    private boolean ownProfile = true;

    private ImageView m_ProfileImage;
    private String m_Name;
    private String m_Age;
    private String m_Gender;
    private String m_AboutMe;
    private String[] m_Skills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        setTitle("Profile");

        Intent i = getIntent();
        fromEditScreen = i.getBooleanExtra("editScreen", false);
        ownProfile = i.getBooleanExtra("ownProfile", true);

        //different fields depending on how profile was accessed
        //three possibilities: from editing own profile, from personal profile button,
        //                     or from other person's profile button on pass feed screen
        if(fromEditScreen) {
            //get info from intent
            Bitmap image = (Bitmap) i.getExtras().getParcelable("profile_image");
            m_ProfileImage.setImageBitmap(image);
            m_Name = i.getStringExtra("name");
            m_Age = i.getStringExtra("age");
            m_Gender = i.getStringExtra("gender");
            m_AboutMe = i.getStringExtra("about_me");
            m_Skills = i.getStringArrayExtra("skills");

            //set views
            TextView nameView = (TextView) findViewById(R.id.text_name);
            nameView.setText(m_Name);
            TextView ageView = (TextView) findViewById(R.id.text_age);
            nameView.setText(m_Age);
            TextView nameGender = (TextView) findViewById(R.id.text_gender);
            nameView.setText(m_Gender);
            TextView nameAbout = (TextView) findViewById(R.id.aboutString);
            nameView.setText(m_AboutMe);


        }
        else if(ownProfile) {

        }
        else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
