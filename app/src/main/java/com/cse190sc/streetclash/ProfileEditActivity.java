package com.cse190sc.streetclash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileEditActivity extends AppCompatActivity {

    private static final String TAG = "ProfileEditActivity";
    private static final int REQUEST_LOAD_IMAGE = 1;
    private static final String[] SKILL_CARDS = {
            "Comp Sci", "Economics", "Electrical Eng", "Accounting",
            "Math", "Crypto", "Human Bio", "Bioinfo", "Robotics",
            "Dentistry", "Music", "Drawing/Art", "Dance", "Theater",
            "Leadership", "Cooking", "Marketing", "Chemistry",
            "Plumbing", "Cleaning", "Filming", "Communications", "Teaching"
    };

    private ImageView m_ProfileImage;
    private String m_Name;
    private String m_Age;
    private String m_Gender;
    private String m_AboutMe;
    private String[] m_Skills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        setTitle("Edit Profile");

        Log.i(TAG, "Starting up");
        m_ProfileImage = (ImageView) findViewById(R.id.iv_profile_pic);
        m_ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_LOAD_IMAGE);
            }
        });

        final EditText aboutMe = (EditText) findViewById(R.id.about_me);
        aboutMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (aboutMe.getLayout().getLineCount() > 7) {
                    aboutMe.getText().delete(aboutMe.getText().length() - 1, aboutMe.getText().length());
                }
            }
        });

        m_Skills = new String[0];
    }

    public void passFeedButtonClicked(View v) {
        Intent i = new Intent(this, ProfileListActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOAD_IMAGE) {
            Log.i(TAG, "Result code was: " + resultCode);
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                m_ProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_done) {
            Toast.makeText(this, "Done clicked", Toast.LENGTH_SHORT).show();

            //finalize fields
            //skills list should already be set
            m_ProfileImage.buildDrawingCache();
            Bitmap profile = m_ProfileImage.getDrawingCache();


            EditText editName = (EditText) findViewById(R.id.et_name);
            m_Name = editName.getText().toString();

            EditText editAge = (EditText) findViewById(R.id.et_age);
            m_Age = editAge.getText().toString();

            Spinner gender = (Spinner) findViewById(R.id.spinner_gender);
            if(gender.getSelectedItem() == null) {
                m_Gender = "";
            }
            else {
                m_Gender = gender.getSelectedItem().toString();
            }

            EditText editAbout = (EditText) findViewById(R.id.about_me);
            m_AboutMe = editAbout.getText().toString();

            //send the set fields to the real profile screen
            Intent i = new Intent(this, ProfileViewActivity.class);
            i.putExtra("profile_image", profile);
            i.putExtra("name", m_Name);
            i.putExtra("age", m_Age);
            i.putExtra("gender", m_Gender);
            i.putExtra("about_me", m_AboutMe);
            i.putExtra("skills", m_Skills);
            i.putExtra("editScreen", true);
            i.putExtra("ownProfile", false);

            //temporary image field


            //send stuff to server
            //m_ProfileImage.get

            JSONObject obj = null;
            try {
                obj = new JSONObject();
                obj.put("name", m_Name);
                obj.put("age", m_Age);
                obj.put("gender", m_Gender);
                obj.put("about", m_AboutMe);
                obj.put("image", "temporary");
                JSONArray skillsArray = new JSONArray();
                for (int index = 0; index < m_Skills.length; index++) {
                    skillsArray.put(m_Skills[index]);
                }
                obj.put("skills", skillsArray);
                SharedPreferences prefs = getSharedPreferences("com.cse190sc.streetclash", Context.MODE_PRIVATE);
//                obj.put("email", prefs.getString("email", "none"));
                obj.put("userID", prefs.getString("userID", "invalid"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    Constants.SERVER_URL + "/users",
                    obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean update = response.getBoolean("docUpdate");
                                if (update)
                                    Log.i(TAG, "ProfileEditActivity: Updated successfully");
                                else
                                    Log.e(TAG, "ProfileEditActivity: Error updating");
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "ProfileEditActivity: Volley error: " + error.getMessage());
                        }
                    }
            );

//            Volley.newRequestQueue(this).add(request);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

            startActivity(i);

            return true;
        }
        return false;
    }

    public void editSkillsButtonClicked(View v) {
        final ArrayList<String> chosenSkills = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your skills");
        builder.setMultiChoiceItems(SKILL_CARDS, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    chosenSkills.add(SKILL_CARDS[which]);
                } else {
                    chosenSkills.remove(SKILL_CARDS[which]);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               //store chosen skills for passing in an intent
                m_Skills = chosenSkills.toArray(new String[chosenSkills.size()]);

                ListAdapter adapter = new CustomAdapter(ProfileEditActivity.this, chosenSkills.toArray(new String[1]));
                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter);
                setListViewHeightBasedOnChildren(listView);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });

        builder.create();
        builder.show();
    }

    /* Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     * http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
