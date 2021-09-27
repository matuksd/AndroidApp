package com.example.searchapp.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.searchapp.R;
import com.example.searchapp.models.User;
import com.example.searchapp.models.UserAccountSettings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//holder.email.setText(mUser.get(position).getEmail());
public class UserListAdapter_copy extends ArrayAdapter {

    private static final String TAG = "UserListAdapter";

    private LayoutInflater mInflater;
    private List<User> mUser = null;
    private Context mContent;
    private int layoutResource;

    public UserListAdapter_copy(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        mContent = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUser = objects;
    }

    private static class ViewHolder{
        TextView username, email;
        CircleImageView profileImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(layoutResource,parent,false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.email = convertView.findViewById(R.id.email);
            holder.profileImage= convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.username.setText(mUser.get(position).getUsername());
        holder.email.setText(mUser.get(position).getEmail());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContent.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContent.getString(R.string.field_user_id))
                .equalTo(mUser.get(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " + singleSnapshot.getValue(UserAccountSettings.class).toString());
                    ImageLoader imageLoader = ImageLoader.getInstance();

                    Log.d(TAG, "onDataChange: found image: " + singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo());
                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                            holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return convertView;
    }
}
