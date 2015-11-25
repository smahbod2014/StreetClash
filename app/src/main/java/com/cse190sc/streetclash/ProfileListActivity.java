package com.cse190sc.streetclash;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.Collections;
import java.util.List;

/*public class ProfileListActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Pass Feed");
    }

    @Override
    protected Fragment createFragment(){
        return new ProfileListFragment();
    }
}*/

/*public class ProfileListActivity extends AppCompatActivity {
    private RecyclerView mProfileRecyclerView;
    private ProfileAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    //Beacon Stuff
    private static final String TAG = "ProfileListActivity";
    private BeaconTransmitterApplication m_Application;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTitle("Pass Feed");
        setContentView(R.layout.fragment_profile_list);

        mProfileRecyclerView = (RecyclerView) findViewById(R.id.profile_recycler_view);
        mProfileRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();

        m_Application = (BeaconTransmitterApplication) this.getApplicationContext();

        boolean b = this.getIntent().getBooleanExtra("arrivedFromNotification", false);
        if (b) {
            Toast.makeText(this, "Arrived from notification", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Came from notification");
        }
        else {
            Toast.makeText(this, "Opened app manually", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Opened app manually");
        }

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

    private void updateUI(){
        ProfileLib profileLib = ProfileLib.get(this);
        List<Profile> profiles = profileLib.getProfile();

        mAdapter = new ProfileAdapter(profiles);
        mProfileRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mProfileRecyclerView);
    }

    private class ProfileHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder{
        private TextView mNameView;
        private ImageView mPhotoView;

        private Profile mProfile;

        public ProfileHolder(View itemView){
            super(itemView);
            //itemView.setOnClickListener(this);

            mNameView = (TextView) itemView.findViewById(R.id.list_item_profile_name_text_view);
            mPhotoView = (ImageView) itemView.findViewById(R.id.list_item_profile_photo_image_view);
        }

        public void bindProfile (Profile profile){
            mProfile = profile;
            mNameView.setText(mProfile.getMname());
            //mPhotoView.setImageDrawable(null);
        }
*/
        /*@Override*/
        /*public void onClick(View v){
            Toast.makeText(getActivity(), mProfile.getMname() + "clicked!", Toast.LENGTH_SHORT).show();
        }*/
 /*       @Override
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

}*/
