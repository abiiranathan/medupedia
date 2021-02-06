package com.medicalfileyo.medupedia.diseases;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicalfileyo.medupedia.R;

import java.util.ArrayList;

public class DiseaseListAdaptor extends RecyclerView.Adapter<DiseaseListAdaptor.ViewHolder> implements Filterable {
    ArrayList<Disease> diseases;
    ArrayList<Disease> fullDiseases;

    DiseaseClickInterface diseaseClickInterface;

    public DiseaseListAdaptor(ArrayList<Disease> data, DiseaseClickInterface diseaseClickInterface) {
        this.diseases = data;
        this.fullDiseases = (ArrayList<Disease>) data.clone();
        this.diseaseClickInterface = diseaseClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_disease, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(diseases.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return diseases.size();
    }

    @Override
    public Filter getFilter() {
        return diseaseFilter;
    }

    private final Filter diseaseFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Disease> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullDiseases);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Disease d : fullDiseases) {
                    if (d.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(d);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            diseases.clear();
            diseases.addAll((ArrayList<Disease>) results.values);
            notifyDataSetChanged();
        }
    };

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.disease_name);
            textView.setOnClickListener(v -> diseaseClickInterface.onItemClicked(diseases.get(getAdapterPosition())));
        }

        public TextView getTextView() {
            return textView;
        }
    }

}
