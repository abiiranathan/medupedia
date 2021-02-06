package com.medicalfileyo.medupedia.presentation;

import android.content.Intent;

import com.google.gson.Gson;


public class SymptomListActivity extends FeatureListActivity implements RecyclerViewClickInterface {
    public static String fileName = "symptoms.json";

    @Override
    public void onItemClick(Feature feature) {
        Intent intent = new Intent(this, FeatureDetailActivity.class);
        Gson gson = new Gson();
        String jsonData = gson.toJson(feature);
        intent.putExtra("TYPE", "SYMPTOM");
        intent.putExtra("FEATURE", jsonData);
        startActivity(intent);

    }

    @Override
    protected RecyclerViewClickInterface getClickInterface() {
        return this;
    }

    @Override
    protected String getType() {
        return FeatureListActivity.SYMPTOMS;
    }

    @Override
    protected String getScreenTitle() {
        return FeatureListActivity.SYMPTOMS;
    }

    @Override
    protected String getOfflineFilePath() {
        return fileName;
    }

}
