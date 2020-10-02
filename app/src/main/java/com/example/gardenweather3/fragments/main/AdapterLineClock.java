package com.example.gardenweather3.fragments.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardenweather3.DataSourceTextPicTemp;
import com.example.gardenweather3.ItemTextPicTemp;
import com.example.gardenweather3.R;

public class AdapterLineClock extends RecyclerView.Adapter<AdapterLineClock.ViewHolder> {
    private DataSourceTextPicTemp dataSource;

    public AdapterLineClock(DataSourceTextPicTemp dataSource) {
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liner_hours, parent, false);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private ImageView pic;
        private TextView temp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textView1);
            pic = itemView.findViewById(R.id.imageView1);
            temp = itemView.findViewById(R.id.textView2);
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
