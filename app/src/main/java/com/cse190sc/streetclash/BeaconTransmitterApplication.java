package com.cse190sc.streetclash;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Jono on 11/21/2015.
 */
public class BeaconTransmitterApplication extends Application implements BootstrapNotifier {

    private static final String TAG = "BeaconTransmitterApp";
    private static final String ALTBEACON_ID = "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6";
    private static final long TIMEOUT = 10l * 1000l;
    public static final int FILTERING_ALL = 0;
    public static final int FILTERING_ANY = 1;
    private RegionBootstrap m_RegionBootstrap;
    private BackgroundPowerSaver m_PowerSaver;
    private BeaconManager m_BeaconManager;
    private BeaconTransmitter m_Transmitter;
    private boolean m_InsideActivity;
    private boolean m_IsScanning = true;
    private static boolean s_ApplicationInBackground;
    private static String[] s_SkillsFilterAll = new String[1];
    private static String[] s_SkillsFilterAny = new String[1];
    private static int s_FilteringMode = FILTERING_ALL;
    private Region m_Region;
    private final HashMap<Identifier, Long> m_BeaconMap = new HashMap<>();
    private static final ArrayList<Profile> s_ProfileListEntries = new ArrayList<>();
    private ProfileListActivity m_ProfileListActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BeaconTransmitterApplication started up!");
        //this should add an AltBeacon parser automatically
        m_BeaconManager = BeaconManager.getInstanceForApplication(this);
        m_BeaconManager.setBackgroundBetweenScanPeriod(0l);
        m_BeaconManager.setBackgroundScanPeriod(1100l);
        m_BeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if (collection.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Beacon beacon : collection) {
                        synchronized (m_BeaconMap) {
                            //first time seeing this beacon
                            if(m_BeaconMap.get(beacon.getId2()) == null) {
                                handleBeaconSighting(beacon.getId2());
                            }
                            m_BeaconMap.put(beacon.getId2(), System.currentTimeMillis());
                        }
                        sb.append("Beacon with id2 = " + beacon.getId2() + " is " + beacon.getDistance() + " meters away");
                        sb.append("\n");
                    }
                    //logToDisplay(sb.toString());
                    //logNumBeacons();
                }
            }
        });


        //wake up the app when any beacon is seen
        //startBackgroundMonitoring();
        //m_PowerSaver = new BackgroundPowerSaver(this);
        Log.d(TAG, "Begin monitoring");
        m_Region = new Region("com.cse190sc.streetclash", Identifier.parse(ALTBEACON_ID), null, null);
        m_RegionBootstrap = new RegionBootstrap(this, m_Region);
        m_PowerSaver = new BackgroundPowerSaver(this);

        stopScanning();
        m_Transmitter = new BeaconTransmitter(this, new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        m_Transmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        m_Transmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        m_Transmitter.setBeacon(new Beacon.Builder()
                .setId1("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
                .setId2(String.valueOf(new Random().nextInt(30000)))
                .setId3("987")
                .setManufacturer(0x0000) // Choose a number of 0x00ff or less as some devices cannot detect beacons with a manufacturer code > 0x00ff
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[]{0l}))
                .build());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (m_BeaconMap) {
                        ArrayList<Identifier> idsToRemove = new ArrayList<>();
                        for (Identifier id : m_BeaconMap.keySet()) {
                            Long time = m_BeaconMap.get(id);
                            if (System.currentTimeMillis() - time >= TIMEOUT) {
                                idsToRemove.add(id);
                            }
                        }

                        for (int i = 0; i < idsToRemove.size(); i++) {
                            m_BeaconMap.remove(idsToRemove.get(i));
                        }

                        //logNumBeacons();
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void setBeaconIdentifier(String id) {
        boolean wasTransmitting = m_Transmitter.isStarted();
        if (wasTransmitting) {
            m_Transmitter.stopAdvertising();
        }

        m_Transmitter.setBeacon(new Beacon.Builder()
                .setId1("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
                .setId2(id)
                .setId3("987")
                .setManufacturer(0x0000) // Choose a number of 0x00ff or less as some devices cannot detect beacons with a manufacturer code > 0x00ff
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[]{0l}))
                .build());

        if (wasTransmitting) {
            m_Transmitter.startAdvertising();
        }
    }
    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "I see a beacon!");
        //logToDisplay("I see a beacon!");
        try {
            m_BeaconManager.startRangingBeaconsInRegion(region);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "Went out of range of beacon with Id2 = " + region.getId2());
        //logToDisplay("Went outside range");
        try {
            m_BeaconManager.stopRangingBeaconsInRegion(region);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        //don't care?
        if (i == BootstrapNotifier.INSIDE) {
            Log.d(TAG, "Went INSIDE a beacon range");
        }
        else if (i == BootstrapNotifier.OUTSIDE) {
            Log.d(TAG, "Went OUTSIDE a beacon range");
        }
    }

    public static boolean isAppInBackground() {
        return s_ApplicationInBackground;
    }

    public static void leavingApp() {
        s_ApplicationInBackground = true;
    }

    public static void enteringApp() {
        s_ApplicationInBackground = false;
    }

    /**
     * This will create an Android notification that will appear at the top of the phone.
     * It's also configured so that when the user opens and clicks on the notification,
     * the ProfileListActivity activity will be launched.
     */
    public void createNotification() {
        int yellow = 0xffffff00;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("StreetClash")
                        .setContentText("Congratulations! Found a beacon while not in-app!")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setVibrate(new long[] {0, 125, 125, 125})
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        Intent i = new Intent(this, ProfileListActivity.class);
        i.putExtra("arrivedFromNotification", true);
        stackBuilder.addNextIntent(i);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public int getNumBeacons() {
        int size = 0;

        synchronized (m_BeaconMap) {
            size = m_BeaconMap.size();
        }

        return size;
    }

    public void startScanning() {
        try {
            if (!m_IsScanning) {
                m_BeaconManager.startMonitoringBeaconsInRegion(m_Region);
                m_IsScanning = true;
            }
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopScanning() {
        try {
            if (m_IsScanning) {
                m_BeaconManager.stopMonitoringBeaconsInRegion(m_Region);
                m_BeaconManager.stopRangingBeaconsInRegion(m_Region);
                m_IsScanning = false;
            }
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public BeaconTransmitter getTransmitter() {
        return m_Transmitter;
    }

    public static void setSkillsFilterAll(String[] skillsFilter) {
        s_SkillsFilterAll = skillsFilter;
    }

    public static void setSkillsFilterAny(String[] skillsFilter) {
        s_SkillsFilterAny = skillsFilter;
    }

    public static void setFilteringMode(int mode) {
        s_FilteringMode = mode;
    }

    private void handleBeaconSighting(Identifier id) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Constants.SERVER_URL + "/users?userID=" + id.toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray skillsArray = response.getJSONArray("skills");
                            ArrayList<String> skills = new ArrayList<>();
                            for (int i = 0; i < skillsArray.length(); i++) {
                                skills.add((String) skillsArray.get(i));
                            }

                            boolean success = true;
                            // using FILTERING_ALL mode
                            if (s_FilteringMode == FILTERING_ALL) {
                                for (int i = 0; i < s_SkillsFilterAll.length; i++) {
                                    if (!skills.contains(s_SkillsFilterAll[i])) {
                                        success = false;
                                        break;
                                    }
                                }
                            }
                            // using FILTERING_ANY mode
                            else if (s_FilteringMode == FILTERING_ANY) {
                                success = false;
                                for (int i = 0; i < s_SkillsFilterAll.length; i++) {
                                    if (skills.contains(s_SkillsFilterAll[i])) {
                                        success = true;
                                        break;
                                    }
                                }
                            }

                            // yes! notify us about this user
                            if (success) {
                                notifyAboutUser(
                                        response.getString("name"),
                                        response.getString("image"),
                                        response.getString("userID"));
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "[BTA] Volley error: " + error.getMessage());
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void notifyAboutUser(String name, String imageBytes, String userID) {
        if (isAppInBackground()) {
            Log.d(TAG, "Sending notification!");
            createNotification();
        }
        else {
            Log.d(TAG, "No need to send a notification, app is in foreground");
        }

        // we create a profile list entry anyway to put in the ProfileListActivity
        // This will happen regardless of whether we're in the app or not
        Profile entry = new Profile(name, userID, imageBytes);
        s_ProfileListEntries.add(entry);
        if (m_ProfileListActivity != null) {
            m_ProfileListActivity.notifyChanged();
        }
    }

    // this is a temporary test method. delete it!
    public void createEntry(String name, String imageBytes, String userID) {
        final Profile entry = new Profile(name, userID, imageBytes);
        s_ProfileListEntries.add(entry);
        if (m_ProfileListActivity != null) {
            m_ProfileListActivity.notifyChanged();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                s_ProfileListEntries.add(entry);
                if (m_ProfileListActivity != null) {
                    m_ProfileListActivity.notifyChanged();
                }
            }
        }).start();
    }

    public static ArrayList<Profile> getProfileListEntries() {
        return s_ProfileListEntries;
    }

    public void setProfileListActivity(ProfileListActivity activity) {
        m_ProfileListActivity = activity;
    }
}