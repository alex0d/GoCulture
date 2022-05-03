package ru.mdev.goculture.ui.map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mdev.goculture.api.SightAPI;
import ru.mdev.goculture.model.Sight;

public class SightsCollector {

    private static final String OPENTRIPMAP_API_KEY = "5ae2e3f221c38a28845f05b66b92b2e91309df2206631a5e06d496b3";
    private static final String BASE_URL = "https://api.opentripmap.com/";
    private ArrayList<Sight> sights = new ArrayList<>();
    private SightAPI sightAPI;

    public SightsCollector() {
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        sightAPI = retrofit.create(SightAPI.class);
    }

    public ArrayList<Sight> getAll() {
        // RUSSIA: Latitude from 41.28413 to 71.69002 and longitude from 19.90929 to 177.5103.
        // But it includes a few more countries...
        sightAPI.getSightsByBbox(
                32.0,
                60.0,
                52.5,
                60.0,
                "cultural",
                "1",
                1000,
                "json",
                OPENTRIPMAP_API_KEY
        ).enqueue(new Callback<ArrayList<Sight>>() {
            @Override
            public void onResponse(Call<ArrayList<Sight>> call, Response<ArrayList<Sight>> response) {
                if (response.code() == 200) {
                    sights.addAll(response.body());
                }
                Log.d("OpenTripMapAPI", response.toString());
            }

            @Override
            public void onFailure(Call<ArrayList<Sight>> call, Throwable t) {
                Log.d("OpenTripManAPI", t.getMessage());
            }
        });
        return sights;
    }
}
