package com.medicalfileyo.medupedia;

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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SymptomListActivity extends AppCompatActivity implements SymptomListInterface {
    private ProgressDialog progressDialog;
    private SymptomListAdaptor adaptor;
    private SearchView searchView;

    ApiInterface apiInterface;

    public static final String SYMPTOM_JSON = "SYMPTOM_JSON";
    public static String SYMPTOM_DATA = "SYMPTOM_DATA";

    private ArrayList<Symptom> symptomData;
    private final StorageManager storageManager = new StorageManager();

    protected static final String MyPREFERENCES = "MyPrefs" ;
    protected static final String LOAD_OFFLINE_DATA = "LOAD_OFFLINE_DATA";
    protected SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_list);

        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        searchView = findViewById(R.id.searchView);

        // init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);

        SymptomViewModel viewModel = new ViewModelProvider(this).get(SymptomViewModel.class);
        final Observer<ArrayList<Symptom>> symptomObserver = this::setupRecyclerView;
        viewModel.getSymptoms().observe(this, symptomObserver);

        // Load data based on state
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        if(sharedpreferences.getBoolean(LOAD_OFFLINE_DATA, false)){
            try {
                loadOfflineSymptoms();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to load offline data", Toast.LENGTH_SHORT).show();
            }
        }else{
            viewModel.loadAllSymptoms(this, apiInterface);
        }

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
        Intent intent = new Intent(this, SymptomDetail.class);

        Gson gson = new Gson();
        String symptomJSON = gson.toJson(symptom);
        intent.putExtra(SYMPTOM_DATA, symptomJSON);
        startActivity(intent);
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
                saveSymptomsOffline();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadOfflineSymptoms() throws IOException {
        if (storageManager.isFilePresent(this, SYMPTOM_JSON)) {
            String jsonData = storageManager.read(this, SYMPTOM_JSON);
            Gson gson = new Gson();

            Type collectionType = new TypeToken<ArrayList<Symptom>>() {
            }.getType();
            ArrayList<Symptom> loadedSymptoms = gson.fromJson(jsonData, collectionType);

            if(loadedSymptoms.size() == 0) throw new IOException("No data available");

            setupRecyclerView(loadedSymptoms);
            Toast toast = Toast.makeText(this, "Loaded offline symptoms", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    void saveSymptomsOffline() {
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

    public void showProgressDialog() {
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }
}