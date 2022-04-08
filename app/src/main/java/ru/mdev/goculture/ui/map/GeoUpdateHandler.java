package ru.mdev.goculture.ui.map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.util.Locale;

public class GeoUpdateHandler implements LocationListener
{
    private final String TAG = "GeoUpdateHandler";

    private MapFragment mMapActivity;

    public GeoUpdateHandler(MapFragment mapFragment)
    {
        this.mMapActivity = mapFragment;

    }

    @Override
    public void onLocationChanged(Location location)
    {
        String msg = String.format(Locale.getDefault(),
                "Your location is %2.3f %2.3f \nI'm watching you!",
                    location.getLatitude(),
                    location.getLongitude());
        Toast.makeText(mMapActivity.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.i(TAG, "onProviderDisabled");
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.i(TAG, "onProviderEnabled");
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.i(TAG, "onStatusChanged");
        // TODO Auto-generated method stub

    }

}
