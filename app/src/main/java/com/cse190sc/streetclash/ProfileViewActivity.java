package com.cse190sc.streetclash;

import android.content.SharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
    private BeaconTransmitterApplication m_Application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        setTitle("Profile");

        //Beacon Code
        m_Application = (BeaconTransmitterApplication) this.getApplicationContext();

        Intent i = getIntent();
        fromEditScreen = i.getBooleanExtra("editScreen", false);
        ownProfile = i.getBooleanExtra("ownProfile", true);

        final TextView nameView = (TextView) findViewById(R.id.text_name);
        final TextView ageView = (TextView) findViewById(R.id.text_age);
        m_ProfileImage = (ImageView) findViewById(R.id.iv_profile_pic);
        final TextView nameGender = (TextView) findViewById(R.id.text_gender);
        final TextView nameAbout = (TextView) findViewById(R.id.aboutString);
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
            nameView.setText(m_Name);
            ageView.setText(m_Age);
            nameGender.setText(m_Gender);
            nameAbout.setText(m_AboutMe);
            m_ProfileImage.setImageBitmap(image);

            ListAdapter adapter = new CustomAdapter(this, m_Skills);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
            ProfileEditActivity.setListViewHeightBasedOnChildren(listView);
        }
        else /*ownProfile OR other user's profile*/ {
            String userID = "";
            if (ownProfile) {
                SharedPreferences prefs = getSharedPreferences("com.cse190sc.streetclash", Context.MODE_PRIVATE);
                userID = prefs.getString("userID", "invalid");
            }
            else {
                userID = i.getStringExtra("userID");
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    Constants.SERVER_URL + "/users?userID=" + userID,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                m_Name = response.getString("name");
                                m_Age = response.getString("age");
                                m_Gender = response.getString("gender");
                                m_AboutMe = response.getString("about");

                                JSONArray skillsArray = response.getJSONArray("skills");
                                m_Skills = new String[skillsArray.length()];
                                for (int i = 0; i < skillsArray.length(); i++) {
                                    m_Skills[i] = (String) skillsArray.get(i);
                                }

                                nameView.setText(m_Name);
                                ageView.setText(m_Age);
                                nameGender.setText(m_Gender);
                                nameAbout.setText(m_AboutMe);
                                //m_ProfileImage.setImageBitmap(image);

                                ListAdapter adapter = new CustomAdapter(getApplicationContext(), m_Skills);
                                ListView listView = (ListView) findViewById(R.id.listView);
                                listView.setAdapter(adapter);
                                ProfileEditActivity.setListViewHeightBasedOnChildren(listView);

                                String imageAsString = response.getString("image");
                                if (imageAsString.equals("temporary")) {
                                    Log.i(TAG, "Image was temporary!");
                                    m_ProfileImage.setImageResource(R.mipmap.default_profile_pic);
                                }
                                else {
                                    byte[] decoded = null;
                                    try {
                                        decoded = imageAsString.getBytes("ISO-8859-1");
                                    }
                                    catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    m_ProfileImage.setImageBitmap(
                                            BitmapFactory.decodeByteArray(
                                                    decoded,
                                                    0,
                                                    imageAsString.length()));
                                }

                                Log.i(TAG, "Setting up the user fields");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "PVA: Volley Error: " + error.getMessage());
                        }
                    }
            );

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


        }
    }

    public void passFeedButtonClicked(View v) {
        Log.i(TAG, "PVA: passFeedButton, going to ProfileListActivity");
        Intent i = new Intent(this, ProfileListActivity.class);
        startActivity(i);
    }

    public void optionsButtonClicked(View v) {
        Log.i(TAG, "PVA: optionsButton, going to OptionsActivity");
        Intent i = new Intent(this, OptionsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BeaconTransmitterApplication.enteringApp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BeaconTransmitterApplication.leavingApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_edit, menu);

        if (ownProfile) {
            menu.findItem(R.id.item_edit).setEnabled(true);
            Log.i(TAG, "PVA: Edit button true");
        }
        else {
            menu.findItem(R.id.item_edit).setEnabled(false);
            Log.i(TAG, "PVA: Edit button false");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_edit) {
            Log.i(TAG, "PVA: Edit clicked, going to PEA");
            Intent i = new Intent(this, ProfileEditActivity.class);
            i.putExtra("name", m_Name);
            i.putExtra("age", m_Age);
            i.putExtra("gender", m_Gender);
            i.putExtra("about", m_AboutMe);
            i.putExtra("skills", m_Skills);
            m_ProfileImage.buildDrawingCache();
            Bitmap profile = m_ProfileImage.getDrawingCache();
            i.putExtra("profile_image", profile);
            i.putExtra("cameFromProfileView", true);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
