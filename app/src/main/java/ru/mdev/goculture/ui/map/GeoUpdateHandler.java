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
