package ru.mdev.goculture.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.LocationListenerProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
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

    private TextView gpsOff;

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

        gpsOff = view.findViewById(R.id.gps_off);
        gpsOff.setOnClickListener(view1 -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));

        map = view.findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setTilesScaledToDpi(false);
        map.setTilesScaleFactor((float) 1.5);

        map.setMultiTouchControls(true);
        map.setMinZoomLevel(4.5);
        map.setMaxZoomLevel(21.0);
        map.setScrollableAreaLimitLatitude(85, -85, 0);

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

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationOverlay = new MyLocationNewOverlay(map);
        map.getOverlays().add(locationOverlay);

        locationListener = new LocationListenerProxy(locationManager) {
            @Override
            public void onProviderDisabled(String arg0) {
                super.onProviderDisabled(arg0);
                gpsOff.setVisibility(View.VISIBLE);
                gpsOff.setEnabled(true);
                locationOverlay.disableMyLocation();
            }

            @Override
            public void onProviderEnabled(String arg0) {
                super.onProviderEnabled(arg0);
                gpsOff.setVisibility(View.GONE);
                gpsOff.setEnabled(false);
                locationOverlay.enableMyLocation();
            }
        };

        setupSightsMarkers();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();

        if (locationManager.getBestProvider(fineCriteria, true) == null) {
            gpsOff.setVisibility(View.VISIBLE);
            gpsOff.setEnabled(true);
            return;
        }
        gpsOff.setVisibility(View.INVISIBLE);
        gpsOff.setEnabled(false);

        locationListener.startListening(new GeoUpdateHandler(this), 1000, 3);
        locationOverlay.enableMyLocation();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        try {
            for (Sight sight : sights) {
                Marker marker = new Marker(map);
                marker.setTitle(sight.getName());
                map.getOverlays().add(marker);

                GeoPoint point = new GeoPoint(sight.getPoint().getLat(), sight.getPoint().getLon());
                marker.setPosition(point);
            }
            map.invalidate();
        }
        catch (NullPointerException ex) {
            // MapView is detached or destroyed
            Log.d("MapFragment", ex.getMessage());
        }
    }

    @Override
    public void onSightResponse() {
        sights = sightsCollector.getAll();
        setupSightsMarkers();
    }
}
