package com.medicalfileyo.medupedia.diseases;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.medicalfileyo.medupedia.MainActivity;
import com.medicalfileyo.medupedia.R;
import com.medicalfileyo.medupedia.presentation.Feature;

public class DiseaseDetailActivity extends AppCompatActivity {
    TextView name, description, symptoms, signs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_disease_detail);
        name = findViewById(R.id.disease_detail_name);
        description = findViewById(R.id.disease_detail_description);

        Intent intent = getIntent();

        Gson gson = new Gson();
        String data = intent.getStringExtra(MainActivity.SELECTED_DISEASE);

        if (data != null) {
            Disease disease = gson.fromJson(data, Disease.class);
            name.setText(disease.getName());

            if (disease.getAbout().length() > 0) {
                description.setText(disease.getAbout());
            } else {
                LinearLayout layout = (LinearLayout) findViewById(R.id.description_layout);
                layout.setVisibility(View.GONE);
            }

            // Display disease symptoms
            LinearLayout symptomLayout = findViewById(R.id.symptom_layout);
            if (disease.getSymptoms().size() > 0) {
                for (Feature symptom : disease.getSymptoms()) {
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    StringBuilder sb = new StringBuilder();
                    sb.append("&#8226 ").append(symptom.getName());
                    tv.setText(Html.fromHtml(sb.toString()));
                    tv.setPadding(8, 8, 8,0);
                    tv.setTextSize(16);
                    symptomLayout.addView(tv);
                }
            } else {
                symptomLayout.setVisibility(View.GONE);
            }

            // Display disease signs
            LinearLayout linearLayout2 = findViewById(R.id.sign_layout);
            if (disease.getSigns().size() > 0) {
                for (Feature sign : disease.getSigns()) {
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    StringBuilder sb = new StringBuilder();
                    sb.append("&#8226 ").append(sign.getName());
                    tv.setText(Html.fromHtml(sb.toString()));
                    tv.setPadding(8, 8, 8,0);
                    tv.setTextSize(16);
                    linearLayout2.addView(tv);
                }
            } else {
                linearLayout2.setVisibility(View.GONE);
            }

            try {
                getSupportActionBar().setTitle(disease.getName());  // provide compatibility to all the versions
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
