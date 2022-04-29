package ru.mdev.goculture.ui.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class ProximityIntentReceiver extends BroadcastReceiver {

    private final String TAG = "SIGHT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        boolean entering = intent.getBooleanExtra(key, false);
        if(entering){
            Log.i(TAG, "entering");
        }
    }
}
