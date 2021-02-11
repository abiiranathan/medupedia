package com.medicalfileyo.medupedia;

import com.medicalfileyo.medupedia.diseases.Disease;
import com.medicalfileyo.medupedia.presentation.Feature;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiInterface {
    @GET("/api/diseases")
    Call<ArrayList<Disease>> getDiseases();

    @GET("/api/symptoms")
    Call<ArrayList<Feature>> getSymptoms();

    @GET("/api/signs")
    Call<ArrayList<Feature>> getSigns();
}
