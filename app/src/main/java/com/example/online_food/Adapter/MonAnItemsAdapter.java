package com.example.online_food.Adapter;

import android.annotation.SuppressLint;
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
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class MonAnItemsAdapter extends RecyclerView.Adapter<MonAnItemsAdapter.MonAnItemsViewHodel>{
    private Context context;
    private ArrayList<MonAn> lstMonAn;

    public MonAnItemsAdapter(Context context, ArrayList<MonAn> lstMonAn) {
        this.context = context;
        this.lstMonAn = lstMonAn;
    }

    @NonNull
    @Override
    public MonAnItemsAdapter.MonAnItemsViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MonAnItemsViewHodel(LayoutInflater.from(context).inflate(R.layout.layout_mon_an_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MonAnItemsAdapter.MonAnItemsViewHodel holder, int position) {
        MonAn monAn = lstMonAn.get(position);

        holder.tvTenMon.setText(monAn.getTenMon());
        Glide.with(context).load(monAn.getHinhAnh()).into(holder.ivAnhMonAn);

        double giaMon = monAn.getGiaMon();
        NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
        String giaFormatted = format.format(giaMon);
        holder.tvGia.setText(giaFormatted);

        double luotBan = monAn.getSoLuotBan();
        String luotBanformatted = formatLuotBan(luotBan);
        holder.tvLuotBan.setText(luotBanformatted);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, CTMonAnActivity.class);
//                intent.putExtra("MaMon", monAn.getMaMon());
//                context.startActivity(intent);
            }
        });

    }

    @SuppressLint("DefaultLocale")
    private String formatLuotBan(double luotBan) {
        if (luotBan < 1000) {
            return String.format("%.0f" + " lượt bán", luotBan);
        } else if (luotBan % 1000 == 0) {
            int result = (int) (luotBan / 1000);
            return result + "k lượt bán";
        } else {
            double result = luotBan / 1000;
            return String.format("%.1fk" + " lượt bán", result);
        }
    }

    @Override
    public int getItemCount() {
        return lstMonAn.size();
    }

    public static class MonAnItemsViewHodel extends RecyclerView.ViewHolder{
        ImageView ivAnhMonAn, ivTang, ivGiam;
        TextView tvTenMon, tvLuotBan, tvSoLuong, tvGia;

        public MonAnItemsViewHodel(@NonNull View itemView) {
            super(itemView);

            ivAnhMonAn = itemView.findViewById(R.id.ivAnhMonAn);
            tvTenMon = itemView.findViewById(R.id.tvTenMon);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvLuotBan = itemView.findViewById(R.id.tvLuotBan);
        }
    }
}
