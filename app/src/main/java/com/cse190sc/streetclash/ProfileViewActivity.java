package com.cse190sc.streetclash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        setTitle("Profile");

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
        else if(ownProfile) {
            SharedPreferences prefs = getSharedPreferences("com.cse190sc.streetclash", Context.MODE_PRIVATE);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    Constants.SERVER_URL + "/users?userID=" + prefs.getString("userID", "invalid"),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String name = response.getString("name");
                                int age = response.getInt("age");
                                String gender = response.getString("gender");
                                String about = response.getString("about");
//                                String email = response.getString("email");

                                JSONArray skillsArray = response.getJSONArray("skills");
                                String[] skills = new String[skillsArray.length()];
                                for (int i = 0; i < skillsArray.length(); i++) {
                                   skills[i] = (String) skillsArray.get(i);
                                }

                                nameView.setText(name);
                                ageView.setText(age);
                                nameGender.setText(gender);
                                nameAbout.setText(about);
                                //m_ProfileImage.setImageBitmap(image);

                                ListAdapter adapter = new CustomAdapter(getApplicationContext(), skills);
                                ListView listView = (ListView) findViewById(R.id.listView);
                                listView.setAdapter(adapter);
                                ProfileEditActivity.setListViewHeightBasedOnChildren(listView);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
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
