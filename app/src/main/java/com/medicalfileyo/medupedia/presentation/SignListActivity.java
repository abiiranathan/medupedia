package com.medicalfileyo.medupedia.presentation;

import android.content.Intent;

import com.google.gson.Gson;


public class SignListActivity extends FeatureListActivity implements RecyclerViewClickInterface {
    public static String fileName = "signs.json";

    @Override
    public void onItemClick(Feature feature) {
        Intent intent = new Intent(this, FeatureDetailActivity.class);
        Gson gson = new Gson();
        String jsonData = gson.toJson(feature);
        intent.putExtra("TYPE", "SIGN");
        intent.putExtra("FEATURE", jsonData);
        startActivity(intent);

    }

    @Override
    protected RecyclerViewClickInterface getClickInterface() {
        return this;
    }

    @Override
    protected String getType() {
        return FeatureListActivity.SIGNS;
    }

    @Override
    protected String getScreenTitle() {
        return FeatureListActivity.SIGNS;
    }

    @Override
    protected String getOfflineFilePath() {
        return fileName;
    }

}
