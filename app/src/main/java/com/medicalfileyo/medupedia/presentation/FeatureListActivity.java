package com.medicalfileyo.medupedia.presentation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicalfileyo.medupedia.ApiClient;
import com.medicalfileyo.medupedia.ApiInterface;
import com.medicalfileyo.medupedia.R;
import com.medicalfileyo.medupedia.StorageManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

abstract public class FeatureListActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private FeatureListAdapter adaptor;
    private SearchView searchView;
    private ArrayList<Feature> data;
    private final StorageManager storageManager = new StorageManager();

    protected static final String MyPREFERENCES = "MyPrefs" ;
    protected static final String LOAD_OFFLINE_DATA = "LOAD_OFFLINE_DATA";
    protected SharedPreferences sharedpreferences;

    protected static final String SIGNS = "Signs";
    protected static final String SYMPTOMS = "Symptoms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_list);

        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        searchView = findViewById(R.id.searchView);

        getSupportActionBar().setTitle(getScreenTitle());

        // init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);

        FeatureViewModel viewModel = new ViewModelProvider(this).get(FeatureViewModel.class);
        final Observer<ArrayList<Feature>> featureObserver = this::setupRecyclerView;
        viewModel.getFeatures().observe(this, featureObserver);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        if(sharedpreferences.getBoolean(LOAD_OFFLINE_DATA, false)){
            loadOfflineFeatures();
        }else{
            viewModel.loadAllFeatures(this, apiInterface, getType());
        }

    }

    private void setupRecyclerView(ArrayList<Feature> features) {
        data = features;

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adaptor = new FeatureListAdapter(features, this.getClickInterface());
        recyclerView.setAdapter(adaptor);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptor.getFilter().filter(newText);
                return false;
            }
        });
    }

    protected abstract RecyclerViewClickInterface getClickInterface();
    protected abstract String getScreenTitle();
    protected abstract String getType();
    protected abstract String getOfflineFilePath();

    public void showProgressDialog() {
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.symptom_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.back_to_home:
                finish();
                return true;
            case R.id.save_symptoms:
                saveFeaturesOffline();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadOfflineFeatures(){
        if (storageManager.isFilePresent(this, getOfflineFilePath())) {
            try {
                String jsonData = storageManager.read(this, getOfflineFilePath());
                Gson gson = new Gson();

                Type collectionType = new TypeToken<ArrayList<Feature>>() {
                }.getType();
                ArrayList<Feature> loadedData = gson.fromJson(jsonData, collectionType);
                setupRecyclerView(loadedData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    void saveFeaturesOffline() {
        if (data != null) {
            Gson json = new Gson();
            String toSave = json.toJson(data);
            boolean saved = storageManager.create(this, getOfflineFilePath(), toSave);
            if (saved) {
                Toast.makeText(this, "Saved ok!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No data to save!", Toast.LENGTH_SHORT).show();
        }

    }

}
