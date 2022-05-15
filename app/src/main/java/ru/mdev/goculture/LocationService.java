package ru.mdev.goculture;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.mdev.goculture.model.Sight;
import ru.mdev.goculture.ui.map.ProximityIntentReceiver;
import ru.mdev.goculture.ui.map.SightResponseCallback;
import ru.mdev.goculture.ui.map.SightsCollector;

public class LocationService extends Service {

    private final String TAG = "LOCATION_SERVICE";

    private LocationListener listener;
    private LocationManager locationManager;
    private List<Sight> sights;
    private SightsCollector sightsCollector;

    final float POINT_RADIUS = 50;
    final int PROXY_ALERT_EXPIRATION = -1;
    final String PROXY_ALERT_INTENT = "ru.mdev.goculture.ui.map";
    final double distance = 0.003;
    private Context context;

    private Timer timer;
    private final TimerTask timerTask = new TimerTask() {
        public void run() {
            checkLocation();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        final SightResponseCallback sightResponseCallback = () -> sights = sightsCollector.getAll();
        sightsCollector = new SightsCollector(sightResponseCallback);

        timer = new Timer();
        context = getApplicationContext();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3, listener);


        IntentFilter filter = new IntentFilter(PROXY_ALERT_INTENT);
        context.registerReceiver(new ProximityIntentReceiver(), filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long INTERVAL = 600;
        timer.schedule(timerTask, 0, INTERVAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
        context.unregisterReceiver(new ProximityIntentReceiver());
    }

    private void checkLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = getBestLocation();
        PendingIntent proximityIntent = PendingIntent.getBroadcast(context, 0, new Intent(PROXY_ALERT_INTENT), PendingIntent.FLAG_IMMUTABLE);

        if (location == null || sights == null) {
            return;
        }

        for (Sight sight : sights) {
            if ((Math.abs(sight.getPoint().getLat() - location.getLatitude()) < distance)
                    && (Math.abs(sight.getPoint().getLon() - location.getLongitude()) < distance)) {
                locationManager.addProximityAlert(sight.getPoint().getLat(),
                        sight.getPoint().getLon(),
                        POINT_RADIUS,
                        PROXY_ALERT_EXPIRATION,
                        proximityIntent
                );
            }
        }
    }

    private Location getBestLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                break;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
