package com.example.searchapp.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.searchapp.R;
import com.example.searchapp.Utils.FirebaseMethods;
import com.example.searchapp.Utils.ImageManager;
import com.example.searchapp.Utils.SectionsStatePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private Context mContext;
    public SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mviewPager;
    private RelativeLayout mrelativeLayout;



    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        mContext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: started");

        mviewPager = findViewById(R.id.container);
        mrelativeLayout = findViewById(R.id.relLayout1);

        setupFragments();
        setupSettingsList();
        getIncomingIntent();

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navaigation back to 'ProfileActivity'");
                finish();
            }
        });
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(mContext.getString(R.string.selected_image))){
            Log.d(TAG, "getIncomingIntent: new image url");
            if(intent.getStringExtra(mContext.getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile))){

                //set new profile picture
                ImageManager imageManager= new ImageManager();
                String imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo),null,0,imgUrl,ImageManager.getBitmap(imgUrl));
            }
        }


        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile)));
        }
    }

    public void setupFragments(){

        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager(),1);
        pagerAdapter.addFragment(new EditProfileFragment(),getString(R.string.edit_profile));
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out));

    }

    public void setViewPager(int fragmentNumber){

        mrelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigation to fragment #:"+fragmentNumber);
        mviewPager.setAdapter(pagerAdapter);
        mviewPager.setCurrentItem(fragmentNumber);

    }

    private void setupSettingsList(){

        Log.d(TAG, "setupSettingsList: initalizing 'Account Settings' list ");
        ListView listView = (ListView) findViewById(R.id.AccountSettings);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile));
        options.add(getString(R.string.sign_out));

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1,options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigation to fragment " + position);
                setViewPager(position);
            }
        });
        
    }

}
