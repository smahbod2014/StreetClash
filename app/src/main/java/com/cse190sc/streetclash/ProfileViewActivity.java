package com.cse190sc.streetclash;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;

public class ProfileViewActivity extends AppCompatActivity {

    private static final String TAG = "ProfileViewActivity";

    private boolean fromEditScreen = false;
    private boolean ownProfile = true;
    private boolean fromListScreen=false;

    private ImageView m_ProfileImage;
    private String m_Name;
    private String m_Age;
    private String m_Gender;
    private String m_AboutMe;
    private String[] m_Skills;

    //Beacon Code
    private BeaconTransmitterApplication m_Application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        setTitle("Profile");

        //Beacon Code
        //m_Application = (BeaconTransmitterApplication) this.getApplicationContext();

        Intent i = getIntent();
        fromEditScreen = i.getBooleanExtra("editScreen", false);

        ownProfile = i.getBooleanExtra("ownProfile", true);
        fromListScreen=i.getBooleanExtra("listScreen",false);
        //different fields depending on how profile was accessed
        //three possibilities: from editing own profile, from personal profile button,
        //                     or from other person's profile button on pass feed screen
        if(fromEditScreen||fromListScreen) {
            //get info from intent
            Bitmap image = i.getExtras().getParcelable("profile_image");
            m_Name = i.getStringExtra("name");
            m_Age = i.getStringExtra("age");
            m_Gender = i.getStringExtra("gender");
            m_AboutMe = i.getStringExtra("about_me");
            m_Skills = i.getStringArrayExtra("skills");


            //set views
            TextView nameView = (TextView) findViewById(R.id.text_name);
            nameView.setText(m_Name);
            TextView ageView = (TextView) findViewById(R.id.text_age);
            ageView.setText(m_Age);
            TextView nameGender = (TextView) findViewById(R.id.text_gender);
            nameGender.setText(m_Gender);
            TextView nameAbout = (TextView) findViewById(R.id.aboutString);
            nameAbout.setText(m_AboutMe);
            m_ProfileImage = (ImageView) findViewById(R.id.iv_profile_pic);
            m_ProfileImage.setImageBitmap(image);

            ListAdapter adapter = new CustomAdapter(this, m_Skills);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
            ProfileEditActivity.setListViewHeightBasedOnChildren(listView);
        }
        else if(ownProfile) {

        }
        else {

        }
    }

    public static Intent newIntent(Context packageContext,String name,String age, String gender,String aboutMe,String[] skills,ImageView image){
        Intent i=new Intent(packageContext, ProfileViewActivity.class);
        i.putExtra("name", name);
        i.putExtra("age",age);
        i.putExtra("gender", gender);
        i.putExtra("about_me",aboutMe);
        i.putExtra("skills",skills);
        i.putExtra("listScreen",true);
        return i;
    }

    public void passFeedButtonClicked(View v) {
        Intent i = new Intent(this, SingleFragmentActivity.class);
        startActivity(i);
    }
    public void optionsButtonClicked(View v) {
        Intent i = new Intent(this, OptionsActivity.class);
        startActivity(i);
    }

   /* @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        m_Application.setInsideActivity(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        m_Application.setInsideActivity(true);
    }*/

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
