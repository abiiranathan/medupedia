package com.medicalfileyo.medupedia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

public class SymptomDetail extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_detail);

        Gson gson = new Gson();

        String symptomData = getIntent().getStringExtra(SymptomListActivity.SYMPTOM_DATA);
        Symptom symptom = gson.fromJson(symptomData, Symptom.class);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(symptom.getName());
        }

        TextView tvName = (TextView) findViewById(R.id.symptom_detail_name);
        TextView tvDesc = (TextView) findViewById(R.id.symptom_detail_description);

        tvName.setText(symptom.getName());

        if(symptom.getDescription().isEmpty()){
            tvDesc.setVisibility(View.GONE);
        }else{
            tvDesc.setText(symptom.getDescription());
        }


        ArrayList<Disease> differentials = new ArrayList<>();

        for (Disease disease: MainActivity.diseaseData){
            for(Symptom s :disease.getSymptoms()){
                if(s.getId() == symptom.getId()){
                    differentials.add(disease);
                }
            }
        }

        // Display ddx
        LinearLayout layoutDifferentials = findViewById(R.id.layoutDDX);

        if(differentials.size() > 0){
            for (Disease disease : differentials) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setText(Html.fromHtml("&#8226 " + disease.getName()));
                tv.setPadding(8, 8, 8,0);
                tv.setTextSize(16);
                layoutDifferentials.addView(tv);

                tv.setOnClickListener(v -> {
                    String selectedDisease = new Gson().toJson(disease);

                    Intent intent = new Intent(getApplicationContext(), DiseaseDetailActivity.class);
                    intent.putExtra("SELECTED_DISEASE", selectedDisease);
                    startActivity(intent);
                });
            }
        }else{
            layoutDifferentials.setVisibility(View.GONE);
        }
    }
}
