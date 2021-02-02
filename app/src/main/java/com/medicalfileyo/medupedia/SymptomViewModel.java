package com.medicalfileyo.medupedia;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SymptomViewModel  extends ViewModel {
    private MutableLiveData<ArrayList<Symptom>> symptoms;

    public MutableLiveData<ArrayList<Symptom>> getSymptoms() {
        if (symptoms == null) {
            symptoms = new MutableLiveData<>();
        }
        return symptoms;
    }

    public void loadAllSymptoms(SymptomListActivity context, ApiInterface apiInterface){
        Call<ArrayList<Symptom>> call = apiInterface.getSymptoms();
        SymptomViewModel viewModel = this;

        call.enqueue(new Callback<ArrayList<Symptom>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Symptom>> call, @NonNull Response<ArrayList<Symptom>> response) {
                if (response.isSuccessful()) {
                    viewModel.getSymptoms().setValue(response.body());
                } else {
                    CharSequence text = "Error fetching symptoms";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    try {
                        context.loadOfflineDiseases();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Symptom>> call, @NonNull Throwable t) {
                CharSequence text = t.getLocalizedMessage();
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                try {
                    context.loadOfflineDiseases();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
