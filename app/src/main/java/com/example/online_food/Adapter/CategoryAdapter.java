package com.example.online_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.online_food.Class.Category;
import com.example.online_food.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{
    private Context context;
    private ArrayList<Category> lstCate;

    public CategoryAdapter(Context context, ArrayList<Category> lstCate) {
        this.context = context;
        this.lstCate = lstCate;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.category_main_item, parent, false));
    }

    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category cate = lstCate.get(position);
        holder.tvCate.setText(cate.getTen());

        String anh = "";
        String cateName = "";
        switch (position){
            case 0:
                anh = "food";
                break;
            case 1:
                anh = "drink";
                break;
            case 2:
                anh = "fast_food";
                break;
            case 3:
                anh = "muffin";
                break;
        }

        int resourceAnh = holder.itemView.getContext().getResources()
                .getIdentifier(anh, "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.ivItem.getContext()).load(resourceAnh).into(holder.ivItem);
    }

    @Override
    public int getItemCount() {
        return lstCate.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItem;
        private TextView tvCate;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivItem = itemView.findViewById(R.id.ivItem);
            tvCate = itemView.findViewById(R.id.tvCate);
        }

    }
}
