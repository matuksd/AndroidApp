package com.example.searchapp.Share;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.searchapp.R;
import com.example.searchapp.Utils.FirebaseMethods;
import com.example.searchapp.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.searchapp.Utils.ImageManager.getBitmap;

public class NextActivityCopy extends AppCompatActivity {
    private static final String TAG = "NextActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String mAppend = "file:/";
    private String imgUrl;

    private int imageCount = 0;
    private EditText mCaption;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods = new FirebaseMethods(NextActivityCopy.this);
        mCaption = findViewById(R.id.caption);

        setupFirebaseAuth();
        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                finish();
            }
        });


        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: upload image to firebase.");
                Toast.makeText(NextActivityCopy.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();
                mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption, imageCount, imgUrl,getBitmap(imgUrl));


            }
        });
        setImage();
    }

    private void setImage(){
        Intent intent = getIntent();
        ImageView image = findViewById(R.id.imageShare);
        imgUrl = intent.getStringExtra(getString(R.string.selected_image));
        UniversalImageLoader.setImage(imgUrl,image,null,mAppend);
    }
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        Log.d(TAG, "setupFirebaseAuth: image count " + imageCount);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count " + imageCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
