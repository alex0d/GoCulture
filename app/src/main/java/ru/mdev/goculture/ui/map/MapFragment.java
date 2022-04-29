package ru.mdev.goculture.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.osmdroid.LocationListenerProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.LocationUtils;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mdev.goculture.R;
import ru.mdev.goculture.api.SightAPI;
import ru.mdev.goculture.model.Sight;

public class MapFragment extends Fragment {

    private MapView map;
    private IMapController mapController;
    private Context context;
    private LocationListenerProxy locationListener;
    private LocationManager locationManager;
    private MyLocationNewOverlay locationOverlay;

    public static final String BASE_URL = "https://mdev-goculture.herokuapp.com";
    private SightAPI sightAPI;
    private ArrayList<Sight> sights = new ArrayList<>();

    private float POINT_RADIUS = 100;
    private final int PROX_ALERT_EXPIRATION = -1;
    private final String PROX_ALERT_INTENT = "ru.mdev.goculture.ui.map";
    private final double distance = 0.0003;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        sightAPI = retrofit.create(SightAPI.class);

        sightAPI.getSight().enqueue(new Callback<ArrayList<Sight>>() {
            @Override
            public void onResponse(Call<ArrayList<Sight>> call, Response<ArrayList<Sight>> response) {
                if (response.code() == 200) {
                    sights.addAll(response.body());
                    setupSightsMarkers();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Sight>> call, Throwable t) {
                Log.d("ApiResponse", t.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false);
//        MapViewModel mapViewModel =
//                new ViewModelProvider(this).get(MapViewModel.class);

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
        mapController.setZoom(15.0);

        GeoPoint startPoint = new GeoPoint(55.756, 37.618);
        mapController.setCenter(startPoint);

        // TODO: Разобраться с LocationOverlay.
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationOverlay = new MyLocationNewOverlay(map);
        locationListener = new LocationListenerProxy(locationManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupSightsMarkers();
        locationOverlay.enableMyLocation();
        locationListener.startListening(new GeoUpdateHandler(this), 1000, 3);

        Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getAllProviders().get(0));
        for(Sight sight: sights){
            if((Math.abs(sight.getLatitude() - location.getLatitude()) < distance) && (Math.abs(sight.getLongitude() - location.getLongitude()) < distance)){
                Log.i("SIGHT", "work");
                locationManager.addProximityAlert(sight.getLatitude(), // the latitude of the central point of the alert region
                        sight.getLongitude(), // the longitude of the central point of the alert region
                        POINT_RADIUS, // the radius of the central point of the alert region, in meters
                        PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                        proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
                );
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

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void setupSightsMarkers() {
        for (Sight sight : sights) {
            Marker marker = new Marker(map);
//            marker.setIcon(getResources().getDrawable(R.drawable.ic_location_pin));
            map.getOverlays().add(marker);

            GeoPoint point = new GeoPoint(sight.getLatitude(), sight.getLongitude());
            marker.setPosition(point);
        }
    }

}
