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

    //Beacon Code
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BeaconTransmitterApplication m_Application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        setTitle("Profile");

        //Beacon Code
        m_Application = (BeaconTransmitterApplication) this.getApplicationContext();
        doPermissionChecks();

        Intent i = getIntent();
        fromEditScreen = i.getBooleanExtra("editScreen", false);
        ownProfile = i.getBooleanExtra("ownProfile", true);

        //different fields depending on how profile was accessed
        //three possibilities: from editing own profile, from personal profile button,
        //                     or from other person's profile button on pass feed screen
        if(fromEditScreen) {
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

    public void passFeedButtonClicked(View v) {
        Intent i = new Intent(this, ProfileListActivity.class);
        startActivity(i);
    }
    public void optionsButtonClicked(View v) {
        Intent i = new Intent(this, OptionsActivity.class);
        startActivity(i);
    }

    @Override
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
    }

    private void doPermissionChecks() {
        //this huge block of code is to let this work on phones running
        //Android 6.0, like my Nexus 5
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Location permission granted!");
            }
            else {
                //inform the user here that they will not be able to use the app properly
            }
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
