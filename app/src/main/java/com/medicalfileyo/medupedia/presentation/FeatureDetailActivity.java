package com.medicalfileyo.medupedia.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.medicalfileyo.medupedia.MainActivity;
import com.medicalfileyo.medupedia.R;
import com.medicalfileyo.medupedia.diseases.Disease;
import com.medicalfileyo.medupedia.diseases.DiseaseDetailActivity;

import java.util.ArrayList;

public class FeatureDetailActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_detail);

        Gson gson = new Gson();

        String data = getIntent().getStringExtra("FEATURE");
        String type = getIntent().getStringExtra("TYPE");

        Feature feature = gson.fromJson(data, Feature.class);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(feature.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView tvName = findViewById(R.id.feature_name);
        TextView tvDesc = findViewById(R.id.feature_description);

        tvName.setText(feature.getName());

        if (feature.getDescription().isEmpty()) {
            tvDesc.setVisibility(View.GONE);
        } else {
            tvDesc.setText(feature.getDescription());
        }

        ArrayList<Disease> differentials = new ArrayList<>();

        if (type.equals(FeatureListActivity.SYMPTOMS)) {
            for (Disease disease : MainActivity.diseaseData) {
                for (Feature symptom : disease.getSymptoms()) {
                    if (symptom.getId() == feature.getId()) {
                        differentials.add(disease);
                    }
                }
            }

        } else {
            for (Disease disease : MainActivity.diseaseData) {
                for (Feature sign : disease.getSigns()) {
                    if (sign.getId() == feature.getId()) {
                        differentials.add(disease);
                    }
                }
            }
        }
//
        // Display ddx
        LinearLayout layoutDifferentials = findViewById(R.id.layoutDDX);

        if (differentials.size() > 0) {
            for (Disease disease : differentials) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setText(Html.fromHtml("&#8226 " + disease.getName()));
                tv.setPadding(8, 8, 8, 0);
                tv.setTextSize(16);
                layoutDifferentials.addView(tv);

                tv.setOnClickListener(v -> {
                    String selectedDisease = new Gson().toJson(disease);

                    Intent intent = new Intent(getApplicationContext(), DiseaseDetailActivity.class);
                    intent.putExtra("SELECTED_DISEASE", selectedDisease);
                    startActivity(intent);
                });
            }
        } else {
            layoutDifferentials.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
