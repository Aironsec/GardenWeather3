package com.example.gardenweather3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCityList extends RecyclerView.Adapter<AdapterCityList.ViewHolder> {
    private DataSourceTextPicTemp dataSource;
    private OnItemClickListener itemClickListener;

    public AdapterCityList(DataSourceTextPicTemp dataSource) {
        this.dataSource = dataSource;
    }

    public void addItem (String city){
        dataSource.addItemTextPicTemp(city);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemTextPicTemp itemTextPicTemp = dataSource.getItemTextPicTemp(position);
        holder.setData(itemTextPicTemp.getTextTitle(), itemTextPicTemp.getPic(), itemTextPicTemp.getTemp());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle;
        private ImageView pic;
        private TextView temp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textView_city);
            pic = itemView.findViewById(R.id.imageView1);
            temp = itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });


        }

        public void setData(String textTitle, int pic, String temp) {
            getTextTitle().setText(textTitle);
            getPic().setImageResource(pic);
            getTemp().setText(temp);
        }

        public TextView getTextTitle() {
            return textTitle;
        }

        public ImageView getPic() {
            return pic;
        }

        public TextView getTemp() {
            return temp;
        }
    }
}
