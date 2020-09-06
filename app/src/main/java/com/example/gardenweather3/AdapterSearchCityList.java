package com.example.gardenweather3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class AdapterSearchCityList extends RecyclerView.Adapter<AdapterSearchCityList.ViewHolder> implements Filterable {
    private ArrayList<String> dataSource;
    private ArrayList<String> dataSourceFull;
    private OnItemClickListener itemClickListener;

    public AdapterSearchCityList(ArrayList<String> dataSource) {
        this.dataSource = dataSource;
        dataSourceFull = new ArrayList<>(dataSource);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = dataSource.get(position);
        holder.textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    @Override
    public Filter getFilter() {
        return dataSourceFilter;
    }

    private Filter dataSourceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<String> filteredData = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredData.addAll(dataSourceFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (String item: dataSourceFull) {
                    if (item.toLowerCase().contains(filterPattern)){
                        filteredData.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dataSource.clear();
            dataSource.addAll((ArrayList) filterResults.values);
            if (getItemCount() == 0) dataSource.add("Добавить город");
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(AdapterSearchCityList.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.city_name);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
