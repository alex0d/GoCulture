package ru.mdev.goculture.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.mdev.goculture.model.Geoname;
import ru.mdev.goculture.model.Sight;

public interface SightAPI {

    @GET("/0.1/ru/places/bbox")
    Call<ArrayList<Sight>> getSightsByBbox(@Query("lon_min") Double lon_min,
                                    @Query("lon_max") Double lon_max,
                                    @Query("lat_min") Double lat_min,
                                    @Query("lat_max") Double lat_max,
                                    @Query("kinds") String kinds,
                                    @Query("rate") String rate,
                                    @Query("limit") Integer limit,
                                    @Query("format") String format,
                                    @Query("apikey") String apikey);

    @GET("/0.1/ru/places/geoname/")
    Call<Geoname> getGeoname(@Query("name") String name,
                             @Query("country") String country,
                             @Query("apikey") String apikey);

    @GET("/0.1/ru/places/radius")
    Call<ArrayList<Sight>> getSightsByRadius(@Query("radius") Double radius_m,
                                             @Query("lon") Double lon_max,
                                             @Query("lat") Double lat_min,
                                             @Query("kinds") String kinds,
                                             @Query("rate") String rate,
                                             @Query("limit") Integer limit,
                                             @Query("format") String format,
                                             @Query("apikey") String apikey);
}
