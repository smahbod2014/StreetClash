package com.cse190sc.streetclash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int REQUEST_LOAD_IMAGE = 1;
    private static final String[] SKILL_CARDS = {
            "Comp Sci", "Economics", "Electrical Eng", "Accounting",
            "Math", "Crypto", "Human Bio", "Bioinfo", "Robotics",
            "Dentistry", "Music", "Drawing/Art", "Dance", "Theater",
            "Leadership", "Cooking", "Marketing", "Chemistry",
            "Plumbing", "Cleaning", "Filming", "Communications", "Teaching"
    };

    private ImageView m_ProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        setTitle("Profile");

        Log.i(TAG, "Starting up");
        m_ProfileImage = (ImageView) findViewById(R.id.iv_profile_pic);
        m_ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_LOAD_IMAGE);
            }
        });
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_done) {
            Toast.makeText(this, "Done clicked", Toast.LENGTH_SHORT).show();
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
                int rows = (int) Math.ceil((double) chosenSkills.size() / 3);
                String[][] skillSet = new String[rows][3];
                for (int i = 0; i < rows * 3; i++) {
                    if (i < chosenSkills.size())
                        skillSet[i / 3][i % 3] = chosenSkills.get(i);
                    else
                        skillSet[i / 3][i % 3] = "";
                }

                ListAdapter adapter = new CustomAdapter(ProfileActivity.this, skillSet);
                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter);
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
}
