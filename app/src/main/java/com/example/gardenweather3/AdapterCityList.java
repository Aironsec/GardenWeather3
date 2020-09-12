package com.example.gardenweather3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCityList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private DataSourceTextPicTemp dataSource;
    private OnItemClickListener itemClickListener;
    private OnItemClickListenerFooter itemClickListenerFooter;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public AdapterCityList(DataSourceTextPicTemp dataSource) {
        this.dataSource = dataSource;
    }

    public void addItem(String city) {
        dataSource.addItemTextPicTemp(city);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position >= dataSource.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_list, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_layout,
                    parent, false);
            return new FooterViewHolder(view);
        }
        throw new RuntimeException("нет типа, который соответствует типу " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemTextPicTemp itemTextPicTemp = dataSource.getItemTextPicTemp(position);
            ((ItemViewHolder) holder).setData(itemTextPicTemp.getTextTitle(), itemTextPicTemp.getPic(), itemTextPicTemp.getTemp());
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.size() + 1;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemClickListenerFooter {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListenerFooter(OnItemClickListenerFooter itemClickListener) {
        this.itemClickListenerFooter = itemClickListener;
    }

    public void SetOnItemClickListenerItem(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if (itemClickListenerFooter != null) {
                    itemClickListenerFooter.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private ImageView pic;
        private TextView temp;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.item_list_cites);
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
