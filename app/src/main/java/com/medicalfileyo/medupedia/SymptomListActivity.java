package com.medicalfileyo.medupedia;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.medicalfileyo.medupedia.MainActivity.DISEASES_JSON;

public class SymptomListActivity extends AppCompatActivity implements SymptomListInterface {
    private SymptomViewModel viewModel;
    private SymptomListAdaptor adaptor;
    private SearchView searchView;
    ApiInterface apiInterface;

    private String SYMPTOM_JSON = "SYMPTOM_JSON";
    private ArrayList<Symptom> symptomData;
    private final StorageManager storageManager = new StorageManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_list);

        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        searchView = findViewById(R.id.searchView);

        viewModel = new ViewModelProvider(this).get(SymptomViewModel.class);
        final Observer<ArrayList<Symptom>> symptomObserver = this::setupRecyclerView;
        viewModel.getSymptoms().observe(this, symptomObserver);
        viewModel.loadAllSymptoms(this, apiInterface);
    }

    private void setupRecyclerView(ArrayList<Symptom> symptoms) {
        symptomData = symptoms;

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adaptor = new SymptomListAdaptor(symptoms, this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adaptor);

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

    @Override
    public void onItemClicked(Symptom symptom) {
        Log.d("Symptom", "onItemClicked: " + symptom.getName());
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
                saveDiseasesOffline();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadOfflineDiseases() throws IOException {
        if(storageManager.isFilePresent(this, SYMPTOM_JSON)){
            String  jsonData = storageManager.read(this, SYMPTOM_JSON);
            Gson gson = new Gson();

            Type collectionType = new TypeToken<ArrayList<Symptom>>(){}.getType();
            ArrayList<Symptom> loadedSymptoms = gson.fromJson(jsonData, collectionType);
            setupRecyclerView(loadedSymptoms);
            Toast toast = Toast.makeText(this, "Loaded offline symptoms", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    void saveDiseasesOffline() {
        if (symptomData != null) {
            Gson json = new Gson();
            String diseaseDataJSON = json.toJson(symptomData);
            boolean saved = storageManager.create(this, SYMPTOM_JSON, diseaseDataJSON);
            if (saved) {
                Toast.makeText(this, "Symptoms saved offline. It will be loaded while offline", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No symptoms to save!", Toast.LENGTH_SHORT).show();
        }

    }

}