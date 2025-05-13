package com.example.online_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Data.ChiTietDonHang;
import com.example.online_food.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CTDonHangAdapter extends RecyclerView.Adapter<CTDonHangAdapter.CTDonHangViewHolder>{
    private Context context;
    private ArrayList<ChiTietDonHang> lst;

    public CTDonHangAdapter(Context context, ArrayList<ChiTietDonHang> lst) {
        this.context = context;
        this.lst = lst;
    }

    @NonNull
    @Override
    public CTDonHangAdapter.CTDonHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new CTDonHangAdapter.CTDonHangViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dat_hang, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CTDonHangAdapter.CTDonHangViewHolder holder, int position) {
        ChiTietDonHang item = lst.get(position);

        holder.tenMon.setText(item.getTenMon());

        double sl = item.getSoLuong();

        holder.soLuong.setText(String.valueOf(sl) + "x");
        Glide.with(context).load(item.getAnh()).into(holder.Anh);

        double giaMon = item.getGia();
        double gia = giaMon * sl;
        NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
        String giaFormatted = format.format(gia);
        holder.giaTien.setText(giaFormatted + " đ");
    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

    public static class CTDonHangViewHolder extends RecyclerView.ViewHolder {

        ImageView Anh;
        TextView soLuong, tenMon, giaTien;

        public CTDonHangViewHolder(@NonNull View itemView) {
            super(itemView);

            Anh = itemView.findViewById(R.id.Anh);
            soLuong = itemView.findViewById(R.id.SoLuong);
            tenMon = itemView.findViewById(R.id.TenMon);
            giaTien = itemView.findViewById(R.id.GiaTien);
        }
    }
}
