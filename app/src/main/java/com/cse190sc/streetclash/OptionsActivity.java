package com.cse190sc.streetclash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
}
