package com.medicalfileyo.medupedia;

import com.medicalfileyo.medupedia.diseases.Disease;

public interface DiseaseClickInterface {
    void onItemClicked(Disease disease);
    String getTitle();
    String getActivityName();
    String getJsonFileName();
}


