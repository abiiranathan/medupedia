package com.medicalfileyo.medupedia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SymptomListAdaptor extends RecyclerView.Adapter<SymptomListAdaptor.ViewHolder> implements Filterable {
    ArrayList<Symptom> symptoms;
    ArrayList<Symptom> fullSymptoms;

    SymptomListInterface symptomListInterface;

    SymptomListAdaptor(ArrayList<Symptom> data, SymptomListInterface symptomListInterface) {
        this.symptoms = data;
        this.fullSymptoms = (ArrayList<Symptom>) data.clone();
        this.symptomListInterface = symptomListInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_symptom, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(symptoms.get(position).getName());
        holder.getDescription().setText(symptoms.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

    @Override
    public Filter getFilter() {
        return symptomFilter;
    }

    private final Filter symptomFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Symptom> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullSymptoms);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Symptom s : fullSymptoms) {
                    if (s.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(s);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            symptoms.clear();
            symptoms.addAll((ArrayList<Symptom>) results.values);
            notifyDataSetChanged();
        }
    };

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, description;
        private final LinearLayout symptomRow;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.symptom_name);
            description = view.findViewById(R.id.symptom_desc);
            symptomRow = view.findViewById(R.id.symptoms_row_item);

            // attach click listener to layout
            symptomRow.setOnClickListener(v -> symptomListInterface.onItemClicked(symptoms.get(getAdapterPosition())));
        }

        public TextView getTextView() {
            return name;
        }

        public TextView getDescription(){
            return description;
        }

    }

}
