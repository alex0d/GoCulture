package ru.mdev.goculture.ui.map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class GeoUpdateHandler implements LocationListener
{
    private final String TAG = "GeoUpdateHandler";

    private MapFragment mMapActivity;

    public GeoUpdateHandler()
    {

    }

    public GeoUpdateHandler(MapFragment mapFragment)
    {
        this.mMapActivity = mapFragment;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(TAG, "onLocationChanged called");
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.i(TAG, "onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.i(TAG, "onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.i(TAG, "onStatusChanged");
    }

}
