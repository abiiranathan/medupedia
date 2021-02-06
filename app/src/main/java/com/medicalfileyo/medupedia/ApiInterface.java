package com.medicalfileyo.medupedia;

import com.medicalfileyo.medupedia.diseases.Disease;
import com.medicalfileyo.medupedia.presentation.Feature;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiInterface {
    @GET("/diseases")
    Call<ArrayList<Disease>> getDiseases();

    @GET("/symptoms")
    Call<ArrayList<Feature>> getSymptoms();

    @GET("/signs")
    Call<ArrayList<Feature>> getSigns();
}
