package ru.mdev.goculture.ui.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.mdev.goculture.model.User;

public class ProximityIntentReceiver extends BroadcastReceiver {

    private DatabaseReference mDatabaseReference;
    private final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        boolean entering = intent.getBooleanExtra(key, false);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("Users");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser == null){
            return;
        }

        DatabaseReference userIdReference = mDatabaseReference.child(firebaseUser.getUid());

        if(!entering){
            Log.i(TAG, "entering");
            userIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if( user == null){
                        return;
                    }
                    mDatabaseReference.child(firebaseUser.getUid()).child("score").setValue(user.getScore() + 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
