package com.medicalfileyo.medupedia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickInterface {
    public static final String SELECTED_DISEASE = "SELECTED_DISEASE";

    Context context;
    ApiInterface apiInterface;
    DiseaseListAdaptor adaptor;

    private ProgressBar spinner;
    private SearchView searchView;
    protected DiseaseViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        // Disease api interface
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView);

        // Start with view model
        // ViewModel provider requires "androidx.lifecycle:lifecycle-extensions:2.2.0"
        viewModel = new ViewModelProvider(this).get(DiseaseViewModel.class);

        // Create the observer which updates the UI.
        // Method reference instead of lambda
        final Observer<ArrayList<Disease>> diseaseObserver = this::setupRecyclerView;

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getDiseases().observe(this, diseaseObserver);

        // Fetch the data
        getDiseases();
    }

    void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }
    void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }

    void setupRecyclerView(ArrayList<Disease> diseases) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adaptor = new DiseaseListAdaptor(diseases, this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adaptor);

        // Set up Search view
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

    public void getDiseases() {
        this.showSpinner();
        Call<ArrayList<Disease>> call = apiInterface.getDiseases();

        call.enqueue(new Callback<ArrayList<Disease>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Disease>> call, @NonNull Response<ArrayList<Disease>> response) {
                hideSpinner();

                if (response.isSuccessful()) {
                    // Set data for the view model
                    viewModel.getDiseases().setValue((ArrayList<Disease>) response.body());
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Error fetching diseases";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Disease>> call, @NonNull Throwable t) {
                hideSpinner();
                Context context = getApplicationContext();
                CharSequence text = t.getLocalizedMessage();
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    @Override
    public void onItemClicked(Disease disease) {
        Gson gson = new Gson();
        String selectedDisease = gson.toJson(disease);

        Intent intent = new Intent(context, DiseaseDetailActivity.class);
        intent.putExtra(SELECTED_DISEASE, selectedDisease);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.symptom_menu:
                Toast.makeText(getApplicationContext(), "Symptoms feature not yet implemented", Toast.LENGTH_LONG).show();
                return true;
            case R.id.sign_menu:
                Toast.makeText(getApplicationContext(), "Signs feature not yet implemented.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.exit_menu:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}