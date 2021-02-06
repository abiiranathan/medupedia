package com.medicalfileyo.medupedia.diseases;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class DiseaseViewModel  extends ViewModel {
    private MutableLiveData<ArrayList<Disease>> diseases;

    public MutableLiveData<ArrayList<Disease>> getDiseases() {
        if (diseases == null) {
            diseases = new MutableLiveData<>();
        }
        return diseases;
    }

}
