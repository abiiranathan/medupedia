package com.medicalfileyo.medupedia.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicalfileyo.medupedia.R;

import java.util.ArrayList;

public class FeatureListAdapter extends RecyclerView.Adapter<FeatureListAdapter.ViewHolder> implements Filterable {
    ArrayList<Feature> features;
    ArrayList<Feature> fullFeatures;

    RecyclerViewClickInterface clickInterface;

    FeatureListAdapter(ArrayList<Feature> data, RecyclerViewClickInterface clickInterface) {
        this.features = data;
        this.fullFeatures = (ArrayList<Feature>) data.clone();
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_feature, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(features.get(position).getName());
        holder.getDescription().setText(features.get(position).getDescription());
        if (features.get(position).getDescription().isEmpty()){
            holder.getDescription().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return features.size();
    }

    @Override
    public Filter getFilter() {
        return featureFilter;
    }

    private final Filter featureFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Feature> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullFeatures);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Feature s : fullFeatures) {
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
            features.clear();
            features.addAll((ArrayList<Feature>) results.values);
            notifyDataSetChanged();
        }
    };

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, description;
        private final LinearLayout signRow;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_feature_name);
            description = view.findViewById(R.id.tv_feature_desc);
            signRow = view.findViewById(R.id.feature_layout);

            // attach click listener to layout
            signRow.setOnClickListener(v -> clickInterface.onItemClick(features.get(getAdapterPosition())));
        }
        public TextView getTextView() {
            return name;
        }

        public TextView getDescription(){
            return description;
        }

    }

}
