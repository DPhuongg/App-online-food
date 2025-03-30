package com.example.online_food.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Activity.CTNhaHangActivity;
import com.example.online_food.Data.NhaHang;
import com.example.online_food.R;

import java.util.ArrayList;

public class CTNhaHangItemsAdapter extends RecyclerView.Adapter<CTNhaHangItemsAdapter.CTNhaHangItemsViewHodel> {
    Context context;
    ArrayList<NhaHang> lstNhaHang;

    public CTNhaHangItemsAdapter(Context context, ArrayList<NhaHang> lstNhaHang) {
        this.context = context;
        this.lstNhaHang = lstNhaHang;
    }

    @NonNull
    @Override
    public CTNhaHangItemsViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CTNhaHangItemsViewHodel(LayoutInflater.from(context).inflate(R.layout.nha_hang_item_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CTNhaHangItemsViewHodel holder, int position) {
        NhaHang nhaHang = lstNhaHang.get(position);
        holder.tvNhaHang.setText(nhaHang.getTenNhaHang());
        Glide.with(context).load(nhaHang.getHinhAnh()).into(holder.ivNhaHang);
        String maNH = nhaHang.getMaNH();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserMaNH", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("MaNhaHang", maNH); // Lưu mã nhà hàng vào SharedPreferences
                editor.apply();
                Intent intent = new Intent(context, CTNhaHangActivity.class);
                intent.putExtra("MaNhaHang", maNH);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstNhaHang.size();
    }

    public class CTNhaHangItemsViewHodel extends RecyclerView.ViewHolder{
        ImageView ivNhaHang;
        TextView tvNhaHang;

        public CTNhaHangItemsViewHodel(@NonNull View itemView) {
            super(itemView);

            ivNhaHang = itemView.findViewById(R.id.ivNhaHang);
            tvNhaHang = itemView.findViewById(R.id.tvNhaHang);
        }
    }
}
