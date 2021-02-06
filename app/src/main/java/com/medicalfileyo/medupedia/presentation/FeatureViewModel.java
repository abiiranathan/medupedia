package com.medicalfileyo.medupedia.presentation;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medicalfileyo.medupedia.ApiInterface;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeatureViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Feature>> features;

    public MutableLiveData<ArrayList<Feature>> getFeatures(){
        if (features == null){
            features = new MutableLiveData<>();
        }
        return features;
    }

    public void loadAllFeatures(FeatureListActivity context, ApiInterface apiInterface, String type){
        context.showProgressDialog();
        Call<ArrayList<Feature>> call;

        if (type.equals(FeatureListActivity.SYMPTOMS)){
           call = apiInterface.getSymptoms();
        }else if (type.equals(FeatureListActivity.SIGNS)){
            call = apiInterface.getSigns();
        }else{
            throw new Error("Unsupported type");
        }

        FeatureViewModel viewModel = this;

        call.enqueue(new Callback<ArrayList<Feature>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Feature>> call, @NonNull Response<ArrayList<Feature>> response) {
                context.hideProgressDialog();
                if (response.isSuccessful()) {
                    viewModel.getFeatures().setValue(response.body());
                } else {
                    CharSequence text = "Something went wrong";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    context.loadOfflineFeatures();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Feature>> call, @NonNull Throwable t) {
                context.hideProgressDialog();
                CharSequence text = t.getLocalizedMessage();
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                context.loadOfflineFeatures();
            }
        });

    }

}
