package ru.mdev.goculture.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.osmdroid.LocationListenerProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import ru.mdev.goculture.R;
import ru.mdev.goculture.model.Sight;

public class MapFragment extends Fragment implements SightResponseCallback {

    private MapView map;
    private IMapController mapController;
    private Context context;
    private LocationListenerProxy locationListener;
    private LocationManager locationManager;
    private MyLocationNewOverlay locationOverlay;
    private Criteria fineCriteria;

    SightsCollector sightsCollector;
    private ArrayList<Sight> sights;

    private float POINT_RADIUS = 50;
    private final int PROX_ALERT_EXPIRATION = -1;
    private final String PROX_ALERT_INTENT = "ru.mdev.goculture.ui.map";
    private final double distance = 0.0003;
    private final Intent intent = new Intent(PROX_ALERT_INTENT);

    private SharedPreferences sharedPref;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getActivity().getSharedPreferences("mapCenter", Context.MODE_PRIVATE);

        fineCriteria = new Criteria();
        fineCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        fineCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        fineCriteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        fineCriteria.setBearingRequired(true);

        sightsCollector = new SightsCollector(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        context = inflater.getContext();

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        map = view.findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setTilesScaledToDpi(false);
        map.setTilesScaleFactor((float) 1.5);

        map.setMultiTouchControls(true);
        map.setMinZoomLevel(4.5);
        map.setMaxZoomLevel(21.0);
        map.setScrollableAreaLimitLatitude(85, -85, 0);

        // TODO: Разобраться с LocationOverlay.
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        mapController = map.getController();

        float oldZoom = sharedPref.getFloat("mapZoom", 0);
        if (oldZoom != 0) {
            mapController.setZoom(oldZoom);
        } else {
            mapController.setZoom(15.0);
        }

        double oldCenterLatitude = sharedPref.getFloat("mapCenterLatitude", 0);
        double oldCenterLongitude = sharedPref.getFloat("mapCenterLongitude", 0);
        GeoPoint startPoint;
        if (oldCenterLatitude != 0 && oldCenterLongitude != 0) {
            startPoint = new GeoPoint(oldCenterLatitude, oldCenterLongitude);
        } else {
            startPoint = new GeoPoint(55.756, 37.618);
        }
        mapController.setCenter(startPoint);

        // TODO: Разобраться с LocationOverlay.
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationOverlay = new MyLocationNewOverlay(map);
        locationListener = new LocationListenerProxy(locationManager);

        setupSightsMarkers();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        if (locationManager.getBestProvider(fineCriteria, true) == null &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "У вас геолокация выключена.\nВключите, пожалуйста -_-", Toast.LENGTH_LONG).show();
            return; // FIXME: Сделать запрос на включение GPS. Иначе приложение сразу вылетает
        }
        locationOverlay.enableMyLocation();
        locationListener.startListening(new GeoUpdateHandler(this), 1000, 3);

//        Log.d("Permission", locationManager.getAllProviders().toString());
//        try {
//            for (int i = 0; i < locationManager.getAllProviders().size(); i++) {
//                Log.d("Permission", locationManager.getLastKnownLocation(locationManager.g).toString());
//            }
//        } catch (Exception e) {
//            Log.d("Permission", e.getMessage());
//        }

        PendingIntent proximityIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null){return;}
        if (sights != null) {
            for (Sight sight : sights) {
                if ((Math.abs(sight.getPoint().getLat() - location.getLatitude()) < distance) && (Math.abs(sight.getPoint().getLon() - location.getLongitude()) < distance)) {
                    Log.i("SIGHT", "work");
                    locationManager.addProximityAlert(sight.getPoint().getLat(),
                            sight.getPoint().getLon(),
                            POINT_RADIUS,
                            PROX_ALERT_EXPIRATION,
                            proximityIntent
                    );
                }
            }
        }
        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        context.registerReceiver(new ProximityIntentReceiver(), filter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mViewModel = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        locationListener.stopListening();
        locationOverlay.disableMyLocation();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("mapCenterLatitude", (float)map.getMapCenter().getLatitude());
        editor.putFloat("mapCenterLongitude", (float)map.getMapCenter().getLongitude());
        editor.putFloat("mapZoom", (float)map.getZoomLevelDouble());
        editor.apply();

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onStop() {
        super.onStop();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private void setupSightsMarkers() {
        if (sights == null) {
            return;
        }
        for (Sight sight : sights) {
            Marker marker = new Marker(map);
//            marker.setIcon(getResources().getDrawable(R.drawable.ic_location_pin));
            marker.setTitle(sight.getName());
            map.getOverlays().add(marker);

            GeoPoint point = new GeoPoint(sight.getPoint().getLat(), sight.getPoint().getLon());
            marker.setPosition(point);
        }
        map.invalidate();
    }

    @Override
    public void onSightResponse() {
        sights = sightsCollector.getAll();
        setupSightsMarkers();
    }
}
