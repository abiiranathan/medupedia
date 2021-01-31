package com.medicalfileyo.medupedia;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class DiseaseDetailActivity extends AppCompatActivity {
    TextView name, description, symptoms, signs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_disease_detail);
        name = findViewById(R.id.disease_detail_name);
        description = findViewById(R.id.disease_detail_description);
        symptoms = findViewById(R.id.disease_detail_symptoms);
        signs = findViewById(R.id.disease_detail_signs);

        Intent intent = getIntent();

        Gson gson = new Gson();
        String data = intent.getStringExtra(MainActivity.SELECTED_DISEASE);

        if(data != null){
            Disease disease = gson.fromJson(data, Disease.class);
            name.setText(disease.getName());

            if(disease.getAbout().length() > 0){
                description.setText(disease.getAbout());
            }else{
                LinearLayout layout = (LinearLayout) findViewById(R.id.description_layout);
                layout.setVisibility(View.GONE);
            }

            // Display disease symptoms
            if(disease.getSymptoms().size() > 0){
                StringBuilder sb=new StringBuilder();

                for(Symptom symptom: disease.getSymptoms()){
                    sb.append("&#8226 ").append(symptom.getName()).append("<br>");
                }

                symptoms.setText(Html.fromHtml(sb.toString()));
            }else{
                LinearLayout layout = (LinearLayout) findViewById(R.id.symptom_layout);
                layout.setVisibility(View.GONE);
            }

            // Display disease signs
            if(disease.getSigns().size() > 0){
                StringBuilder sb2=new StringBuilder();

                for(Sign sign: disease.getSigns()){
                    sb2.append("&#8226 ").append(sign.getName()).append("<br>");
                }

                signs.setText(Html.fromHtml(sb2.toString()));
            }else{
                LinearLayout layout = (LinearLayout) findViewById(R.id.sign_layout);
                layout.setVisibility(View.GONE);
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
