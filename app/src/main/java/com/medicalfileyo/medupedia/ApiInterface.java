package com.medicalfileyo.medupedia;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiInterface {
    @GET("/diseases")
    Call<ArrayList<Disease>> getDiseases();

    @GET("/symptoms")
    Call<ArrayList<Symptom>> getSymptoms();

    @GET("/signs")
    Call<ArrayList<Sign>> getSigns();
}
