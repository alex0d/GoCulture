package ru.mdev.goculture.ui.map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

public class OsmGeoUpdateHandler implements LocationListener
{

    private MapFragment mMapActivity;

    public OsmGeoUpdateHandler(MapFragment aMapActivity)
    {
        this.mMapActivity = aMapActivity;

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Toast.makeText(mMapActivity.getContext(),
                "latitude = " + location.getLatitude() * 1e6 + " longitude = " + location.getLongitude() * 1e6,
                Toast.LENGTH_SHORT).show();

        int latitude = (int) (location.getLatitude() * 1E6);
        int longitude = (int) (location.getLongitude() * 1E6);
        GeoPoint point = new GeoPoint(latitude, longitude);
        //FIXME: Uncomment
        //mMapActivity.updateCarPosition(point);
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // TODO Auto-generated method stub

    }

}
