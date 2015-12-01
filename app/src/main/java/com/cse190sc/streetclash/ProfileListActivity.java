package com.cse190sc.streetclash;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

public class ProfileListActivity extends AppCompatActivity {
    private RecyclerView mProfileRecyclerView;
    private ProfileAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    //Beacon Stuff
    private static final String TAG = "ProfileListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTitle("Pass Feed");
        setContentView(R.layout.fragment_profile_list);

        mProfileRecyclerView = (RecyclerView) findViewById(R.id.profile_recycler_view);
        mProfileRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ((BeaconTransmitterApplication) this.getApplicationContext()).setProfileListActivity(this);

        updateUI();
    }

    public void profileButtonClicked(View v) {
        Intent i = new Intent(this, ProfileViewActivity.class);
        i.putExtra("editScreen", false);
        i.putExtra("ownProfile", true);
        startActivity(i);
    }

    public void optionsButtonClicked(View v) {
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

    public void notifyChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateUI(){
        mAdapter = new ProfileAdapter(BeaconTransmitterApplication.getProfileListEntries());
        mProfileRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mProfileRecyclerView);
    }

    private class ProfileHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder, View.OnClickListener {
        private TextView mNameView;
        private TextView mDistanceView;
        private ImageView mPhotoView;

        private Profile mProfile;

        public ProfileHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            mNameView = (TextView) itemView.findViewById(R.id.list_item_profile_name_text_view);
            mPhotoView = (ImageView) itemView.findViewById(R.id.list_item_profile_photo_image_view);
            mDistanceView = (TextView) itemView.findViewById(R.id.list_item_distance);
        }

        public void bindProfile (Profile profile){
            mProfile = profile;
            mNameView.setText(mProfile.name);
            double roundOff = Math.round(mProfile.distance * 100.0) / 100.0;
            mDistanceView.setText("About " + roundOff + " meters away");

            if (mProfile.imageBytes.equals("temporary")) {
                mPhotoView.setImageResource(R.mipmap.cse190_otherprofile);
            }
            else {
                byte[] decoded = null;
                try {
                    decoded = mProfile.imageBytes.getBytes("ISO-8859-1");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mPhotoView.setImageBitmap(
                        BitmapFactory.decodeByteArray(
                                decoded,
                                0,
                                mProfile.imageBytes.length()));
            }
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(ProfileListActivity.this, ProfileViewActivity.class);
            i.putExtra("editScreen", false);
            i.putExtra("ownProfile", false);
            i.putExtra("userID", mProfile.userID);
            startActivity(i);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);

        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    private class ProfileAdapter extends RecyclerView.Adapter<ProfileHolder>implements ItemTouchHelperAdapter{
        private List<Profile> mProfiles;

        public ProfileAdapter(List<Profile> profiles){mProfiles = profiles;}

        @Override
        public ProfileHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(ProfileListActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_profile, parent, false);

            return new ProfileHolder(view);
        }

        @Override
        public void onBindViewHolder(ProfileHolder holder, int position){
            Profile profile = mProfiles.get(position);
            holder.bindProfile(profile);


            // Drag From Left
        }

        @Override
        public int getItemCount(){
            return mProfiles.size();
        }

        @Override
        public void onItemDismiss(int position) {
            mProfiles.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mProfiles, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mProfiles, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    }
}