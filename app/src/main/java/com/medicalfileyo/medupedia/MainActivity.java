package com.medicalfileyo.medupedia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicalfileyo.medupedia.diseases.Disease;
import com.medicalfileyo.medupedia.diseases.DiseaseClickInterface;
import com.medicalfileyo.medupedia.diseases.DiseaseDetailActivity;
import com.medicalfileyo.medupedia.diseases.DiseaseListAdaptor;
import com.medicalfileyo.medupedia.diseases.DiseaseViewModel;
import com.medicalfileyo.medupedia.presentation.SignListActivity;
import com.medicalfileyo.medupedia.presentation.SymptomListActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements DiseaseClickInterface, NavigationView.OnNavigationItemSelectedListener {
    public static final String SELECTED_DISEASE = "SELECTED_DISEASE";
    public static final String DISEASES_JSON = "diseases.json";
    private static int backButtonPressCount = 0;
    private final StorageManager storageManager = new StorageManager();
    public static ArrayList<Disease> diseaseData;
    public static SwitchMaterial switch1;

    Context context;
    ApiInterface apiInterface;
    DiseaseListAdaptor adaptor;

    private ProgressBar spinner;
    private SearchView searchView;
    protected DiseaseViewModel viewModel;

    // Navigation Drawer
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // Shared preferences
    protected static final String MyPREFERENCES = "MyPrefs";
    protected static final String LOAD_OFFLINE_DATA = "LOAD_OFFLINE_DATA";
    protected SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar and Navigation drawer
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#008080")));
        }

        drawerLayout = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation View Listeners
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        context = this;

        // Disease api interface
        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);

        spinner = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView);

        // Start with view model
        // ViewModel provider requires "androidx.lifecycle:lifecycle-extensions:2.2.0"
        viewModel = new ViewModelProvider(this).get(DiseaseViewModel.class);

        // Create the observer which updates the UI.
        // Method reference instead of lambda
        final Observer<ArrayList<Disease>> diseaseObserver = this::setupRecyclerView;

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getDiseases().observe(this, diseaseObserver);

        // Load data based on state
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        // Gave me headache to get the switch in header view
        switch1 = navigationView.getHeaderView(0).findViewById(R.id.switch_element);
        switch1.setChecked(sharedpreferences.getBoolean(LOAD_OFFLINE_DATA, false));

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(LOAD_OFFLINE_DATA, isChecked);
            editor.apply();

            // Capture the data
            if (isChecked) {
                saveDiseasesOffline();
            } else {
                getDiseases();
            }
        });

        if (switch1.isChecked()) {
            try {
                loadOfflineDiseases();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to load offline data", Toast.LENGTH_LONG).show();
            }
        } else {
            getDiseases();
        }

    }

    void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
    }

    void hideSpinner() {
        spinner.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
    }

    void setupRecyclerView(ArrayList<Disease> diseases) {
        diseaseData = diseases;

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adaptor = new DiseaseListAdaptor(diseases, this);
        recyclerView.setAdapter(adaptor);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

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
                    viewModel.getDiseases().setValue(response.body());
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Error fetching diseases";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    try {
                        loadOfflineDiseases();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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

                // Attempt to load offline data
                try {
                    loadOfflineDiseases();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void loadOfflineDiseases() throws IOException {
        hideSpinner();
        if (storageManager.isFilePresent(this, DISEASES_JSON)) {
            String jsonData = storageManager.read(this, DISEASES_JSON);
            if (jsonData == null) {
                throw new IOException("No data available");
            } else {
                Gson gson = new Gson();

                Type collectionType = new TypeToken<ArrayList<Disease>>() {
                }.getType();
                ArrayList<Disease> loadedDiseases = gson.fromJson(jsonData, collectionType);
                setupRecyclerView(loadedDiseases);
            }
        }

    }

    void saveDiseasesOffline() {
        if (diseaseData != null) {
            Gson json = new Gson();
            String diseaseDataJSON = json.toJson(diseaseData);
            boolean saved = storageManager.create(this, DISEASES_JSON, diseaseDataJSON);
            if (saved) {
                Toast.makeText(this, "Diseases data saved offline", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No diseases loaded!", Toast.LENGTH_SHORT).show();
        }

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

    @Override
    protected void onResume() {
        backButtonPressCount = 0;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backButtonPressCount++;

            if (backButtonPressCount > 1) {
                finish();
                // System.exit(0);
            } else {
                Toast.makeText(this, "Press the back button again to quit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.symptom_menu:
                Intent intent = new Intent(this, SymptomListActivity.class);
                startActivity(intent);
                return true;
            case R.id.sign_menu:
                Intent intent2 = new Intent(this, SignListActivity.class);
                startActivity(intent2);
                return true;
            case R.id.reload_page:
                getDiseases();
                return true;
            case R.id.exit_menu:
                finish();
            case R.id.save_diseases:
                saveDiseasesOffline();
            case R.id.delete_offline_data:
                try {
                    storageManager.delete(this, DISEASES_JSON);
                    storageManager.delete(this, SymptomListActivity.fileName);
                    storageManager.delete(this, SignListActivity.fileName);
                } catch (IOException e) {
                    //
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.navi_diseases:
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.navi_symptoms:
                Intent i = new Intent(MainActivity.this, SymptomListActivity.class);
                startActivity(i);
                break;
            case R.id.navi_signs:
                Intent i2 = new Intent(MainActivity.this, SignListActivity.class);
                startActivity(i2);
                break;
            case R.id.navi_share:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Medupedia");
                    String shareMessage = "Medupedia\n\nHigh Yield eponymous medical signs, symptoms and differentials.\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                } catch (Exception e) {
                    Toast.makeText(this, "Error sharing app", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.navi_disclaimer:
                Intent disclaimerIntent = new Intent(this, DisclaimerActivity.class);
                startActivity(disclaimerIntent);
                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}