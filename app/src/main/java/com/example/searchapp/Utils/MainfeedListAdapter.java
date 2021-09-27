package com.example.searchapp.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.searchapp.Home.MainActivity;
import com.example.searchapp.Profile.ProfileActivity;
import com.example.searchapp.R;
import com.example.searchapp.models.Photo;
import com.example.searchapp.models.User;
import com.example.searchapp.models.UserAccountSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.VERSION_CODES.R;

public class MainfeedListAdapter extends ArrayAdapter<Photo> {
    private static final String TAG = "MainfeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername;

    public MainfeedListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
        mReference = FirebaseDatabase.getInstance().getReference();
    }
    static class ViewHolder{
        CircleImageView mProfileImage;
        TextView username,timeDelta,caption;
        SquareImageView image;

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();
        StringBuilder users;

        GestureDetector detector;
        Photo photo;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG, "getView: created");
        final ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource,parent,false);
            holder = new ViewHolder();
            Log.d(TAG, "getView: username :" +convertView.findViewById(com.example.searchapp.R.id.username)+ "\n" +
                   "caption" + convertView.findViewById(com.example.searchapp.R.id.image_caption) );
            holder.username = (TextView) convertView.findViewById(com.example.searchapp.R.id.username);
            holder.image = (SquareImageView) convertView.findViewById(com.example.searchapp.R.id.post_image);
            holder.caption = (TextView) convertView.findViewById(com.example.searchapp.R.id.image_caption);
            holder.timeDelta = (TextView) convertView.findViewById(com.example.searchapp.R.id.image_time_posted);
            holder.mProfileImage = (CircleImageView) convertView.findViewById(com.example.searchapp.R.id.profile_photo);
            holder.photo = getItem(position);
            holder.users = new StringBuilder();
            //holder.detector = new GestureDetector(mContext, new GestureListener(holder))

            convertView.setTag(holder);
        }
        else{
            Log.d(TAG, "getView: failed to get ViewHolder");
            holder = (ViewHolder) convertView.getTag();
        }
        getCurrentUsername();

        //set time since it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timeDelta.setText(timestampDifference + " DAYS AGO");
        }
        else {
            holder.timeDelta.setText("TODAY");
        }

        final ImageLoader imageLoader = ImageLoader.getInstance();
        Log.d(TAG, "getView: image " + getItem(position).getImage_path());
        imageLoader.displayImage(getItem(position).getImage_path(),holder.image);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(com.example.searchapp.R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(com.example.searchapp.R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    //currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                    Log.d(TAG, "onDataChange: found user " +singleSnapshot.getValue(UserAccountSettings.class).getUsername() );
                    holder.username.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of " + holder.user.getUsername() );

                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(com.example.searchapp.R.string.calling_activity),
                                    mContext.getString(com.example.searchapp.R.string.Main_activity));
                            intent.putExtra(mContext.getString(com.example.searchapp.R.string.intent_user),holder.user);
                            mContext.startActivity(intent);
                        }
                    });
                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),holder.mProfileImage);
                    holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of " + holder.user.getUsername() );

                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(com.example.searchapp.R.string.calling_activity),
                                    mContext.getString(com.example.searchapp.R.string.Main_activity));
                            intent.putExtra(mContext.getString(com.example.searchapp.R.string.intent_user),holder.user);
                            mContext.startActivity(intent);
                        }
                    });

                    holder.settings = singleSnapshot.getValue(UserAccountSettings.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query userQuery = mReference
                .child(mContext.getString(com.example.searchapp.R.string.dbname_users))
                .orderByChild(mContext.getString(com.example.searchapp.R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user" + singleSnapshot.getValue(User.class).getUsername());

                    holder.user = singleSnapshot.getValue(User.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return convertView;
    }
    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: retrieving user account settings");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(com.example.searchapp.R.string.dbname_users))
                .orderByChild(mContext.getString(com.example.searchapp.R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String getTimestampDifference(Photo photo){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

}
