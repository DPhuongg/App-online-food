package com.example.online_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.online_food.Class.CategoryNhaHang;
import com.example.online_food.R;

import java.util.ArrayList;

public class NhaHangItemsAdapter  extends RecyclerView.Adapter<NhaHangItemsAdapter.NhaHangItemsViewHodel>{
    private Context context;
    private ArrayList<CategoryNhaHang> categorieslist;
    private CTNhaHangItemsAdapter adapter;

    public NhaHangItemsAdapter(Context context, ArrayList<CategoryNhaHang> categorieslist) {
        this.context = context;
        this.categorieslist = categorieslist;
    }

    @NonNull
    @Override
    public NhaHangItemsViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NhaHangItemsViewHodel(LayoutInflater.from(context).inflate(R.layout.nha_hang_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NhaHangItemsViewHodel holder, int position) {
        CategoryNhaHang cate = categorieslist.get(position);
        holder.TenNH.setText(cate.getCategoryName());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.DSNhaHang.setLayoutManager(linearLayoutManager);
        adapter = new CTNhaHangItemsAdapter(context, cate.getLstNhaHang());
        holder.DSNhaHang.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return categorieslist.size();
    }


    public class NhaHangItemsViewHodel extends RecyclerView.ViewHolder{
        private RecyclerView DSNhaHang;
        private TextView TenNH;

        public NhaHangItemsViewHodel(@NonNull View itemView) {
            super(itemView);

            DSNhaHang = itemView.findViewById(R.id.DSNhaHang);
            TenNH = itemView.findViewById(R.id.TenNH);
        }
    }
}
