package com.cse190sc.streetclash;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import org.altbeacon.beacon.BeaconTransmitter;

import java.util.ArrayList;

public class OptionsActivity extends AppCompatActivity {

    private static final String TAG = "Options";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BeaconTransmitterApplication m_Application;
    private BeaconTransmitter m_Transmitter;
    private SharedPreferences m_Prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Options");
        setContentView(R.layout.activity_options2);

        //set application and transmitter
        m_Application = (BeaconTransmitterApplication) this.getApplicationContext();
        m_Transmitter = m_Application.getTransmitter();

        doPermissionChecks();

        m_Prefs = this.getSharedPreferences("com.cse190sc.streetclash", Context.MODE_PRIVATE);

        //switch for transmitting/scanning
        Switch transmitSwitch = (Switch) findViewById(R.id.ao_transmitSwitch);
        transmitSwitch.setChecked(m_Prefs.getBoolean("transmitSwitch", false));
        transmitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTransmitting();
                    startScanning();
                } else {
                    stopTransmitting();
                    stopScanning();
                }
                m_Prefs.edit().putBoolean("transmitSwitch", isChecked).apply();
            }
        });

        final Spinner filteringMode = (Spinner) findViewById(R.id.ao_filteringSpinner);
        int selection = m_Prefs.getInt("filteringMode", 0);
        filteringMode.setSelection(selection);
        BeaconTransmitterApplication.setFilteringMode(selection);
        filteringMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) filteringMode.getSelectedItem();
                int mode = -1;
                if (item.equals("All of")) {
                    mode = BeaconTransmitterApplication.FILTERING_ALL;
                } else if (item.equals("Any of")) {
                    mode = BeaconTransmitterApplication.FILTERING_ANY;
                }
                m_Prefs.edit().putInt("filteringMode", mode).apply();
                BeaconTransmitterApplication.setFilteringMode(mode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing
            }
        });

        if (transmitSwitch.isChecked()) {
            startTransmitting();
            startScanning();
        }

        // populate the skills filter list
        int numFilters = m_Prefs.getInt("skillsFilterAllCount", 0);
        if (numFilters > 0) {
            String[] skillsArray = new String[numFilters];
            for (int i = 0; i < numFilters; i++) {
                String key = "skillsFilterAll_" + i;
                skillsArray[i] = m_Prefs.getString(key, "Error");
            }

            BeaconTransmitterApplication.setSkillsFilterAll(skillsArray);

            ListAdapter adapter = new CustomAdapter(OptionsActivity.this, skillsArray);
            ListView listView = (ListView) findViewById(R.id.ao_skillsFilteringAll);
            listView.setAdapter(adapter);
            ProfileEditActivity.setListViewHeightBasedOnChildren(listView);
        }

        numFilters = m_Prefs.getInt("skillsFilterAnyCount", 0);
        if (numFilters > 0) {
            String[] skillsArray = new String[numFilters];
            for (int i = 0; i < numFilters; i++) {
                String key = "skillsFilterAny_" + i;
                skillsArray[i] = m_Prefs.getString(key, "Error");
            }

            BeaconTransmitterApplication.setSkillsFilterAny(skillsArray);

            ListAdapter adapter = new CustomAdapter(OptionsActivity.this, skillsArray);
            ListView listView = (ListView) findViewById(R.id.ao_skillsFilteringAny);
            listView.setAdapter(adapter);
            ProfileEditActivity.setListViewHeightBasedOnChildren(listView);
        }
    }

    public void startTransmitting() {
        if (!m_Transmitter.isStarted())
            m_Transmitter.startAdvertising();
    }

    public void stopTransmitting() {
        if (m_Transmitter.isStarted())
            m_Transmitter.stopAdvertising();
    }

    public void startScanning() {
        m_Application.startScanning();
    }

    public void stopScanning() {
        m_Application.stopScanning();
    }

    public void passFeedButtonClicked(View v) {
        Intent i = new Intent(this, ProfileListActivity.class);
        startActivity(i);
    }

    public void profileButtonClicked(View v) {
        Intent i = new Intent(this, ProfileViewActivity.class);
        i.putExtra("editScreen", false);
        i.putExtra("ownProfile", true);
        startActivity(i);
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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
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
    */

    public void changeFiltersAllClicked(View v) {
        final ArrayList<String> chosenSkills = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add skills to your filter");
        builder.setMultiChoiceItems(ProfileEditActivity.SKILL_CARDS, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    chosenSkills.add(ProfileEditActivity.SKILL_CARDS[which]);
                } else {
                    chosenSkills.remove(ProfileEditActivity.SKILL_CARDS[which]);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // store the selected filters in the preferences so the app remembers them
                m_Prefs.edit().putInt("skillsFilterAllCount", chosenSkills.size()).apply();
                for (int i = 0; i < chosenSkills.size(); i++) {
                    String key = "skillsFilterAll_" + i;
                    String value = chosenSkills.get(i);
                    m_Prefs.edit().putString(key, value).apply();
                }

                String[] skills = chosenSkills.toArray(new String[1]);
                BeaconTransmitterApplication.setSkillsFilterAll(skills);

                ListAdapter adapter = new CustomAdapter(OptionsActivity.this, skills);
                ListView listView = (ListView) findViewById(R.id.ao_skillsFilteringAll);
                listView.setAdapter(adapter);
                ProfileEditActivity.setListViewHeightBasedOnChildren(listView);
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

    public void changeFiltersAnyClicked(View v) {
        final ArrayList<String> chosenSkills = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add skills to your filter");
        builder.setMultiChoiceItems(ProfileEditActivity.SKILL_CARDS, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    chosenSkills.add(ProfileEditActivity.SKILL_CARDS[which]);
                } else {
                    chosenSkills.remove(ProfileEditActivity.SKILL_CARDS[which]);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // store the selected filters in the preferences so the app remembers them
                m_Prefs.edit().putInt("skillsFilterAnyCount", chosenSkills.size()).apply();
                for (int i = 0; i < chosenSkills.size(); i++) {
                    String key = "skillsFilterAny_" + i;
                    String value = chosenSkills.get(i);
                    m_Prefs.edit().putString(key, value).apply();
                }

                String[] skills = chosenSkills.toArray(new String[1]);
                BeaconTransmitterApplication.setSkillsFilterAny(skills);

                ListAdapter adapter = new CustomAdapter(OptionsActivity.this, skills);
                ListView listView = (ListView) findViewById(R.id.ao_skillsFilteringAny);
                listView.setAdapter(adapter);
                ProfileEditActivity.setListViewHeightBasedOnChildren(listView);
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
}
