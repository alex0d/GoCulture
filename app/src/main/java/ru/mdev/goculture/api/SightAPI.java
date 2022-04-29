package ru.mdev.goculture.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.mdev.goculture.model.Sight;

public interface SightAPI {

    @GET("/getSights")
    Call<ArrayList<Sight>> getSight();
}
