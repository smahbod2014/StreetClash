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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconTransmitter;

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
        setContentView(R.layout.activity_options);

        //set application and transmitter
        m_Application = (BeaconTransmitterApplication) this.getApplicationContext();
        m_Transmitter = m_Application.getTransmitter();

        doPermissionChecks();

        m_Prefs = this.getSharedPreferences("com.cse190sc.streetclash", Context.MODE_PRIVATE);

        //switch for transmitting/scanning
        Switch transmitSwitch = (Switch) findViewById(R.id.transmit_switch);
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

        if (transmitSwitch.isChecked()) {
            startTransmitting();
            startScanning();
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
