package com.example.online_food.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Activity.CTMonAnActivity;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class NhaHangSearchItemAdapter extends RecyclerView.Adapter<NhaHangSearchItemAdapter.NhaHangSearchItemsViewHodel>{
    private Context context;
    private List<MonAn> lstMonAn;

    public NhaHangSearchItemAdapter(Context context, List<MonAn> lstMonAn) {
        this.context = context;
        this.lstMonAn = lstMonAn;
    }
    @NonNull
    @Override
    public NhaHangSearchItemsViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NhaHangSearchItemsViewHodel(LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NhaHangSearchItemsViewHodel holder, int position) {
        MonAn monAn = lstMonAn.get(position);
        holder.txtTenMonAn.setText(monAn.getTenMon());


        double giaMon = monAn.getGiaMon();
        NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
        String giaFormatted = format.format(giaMon);
        holder.txtGia.setText("Giá: " + giaFormatted + " VNĐ");

        double luotBan = monAn.getSoLuotBan();
        String luotBanformatted = formatLuotBan(luotBan);
        holder.txtSoLuotBan.setText(luotBanformatted );

        Glide.with(context)
                .load(monAn.getHinhAnh())
                .placeholder(R.drawable.doraemon)
                .into(holder.imgHinhAnh);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CTMonAnActivity.class);
            intent.putExtra("MaMon", monAn.getMaMon());

            String maMonAn = monAn.getMaMon();
            if (maMonAn != null && !maMonAn.isEmpty()) {
                Toast.makeText(context, maMonAn, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Mã món ăn không khả dụng", Toast.LENGTH_SHORT).show();
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lstMonAn.size();
    }
    public static class NhaHangSearchItemsViewHodel extends RecyclerView.ViewHolder{
        private ImageView imgHinhAnh;
        private TextView txtTenMonAn;
        private TextView txtSoLuotBan;
        private TextView txtGia;

        public NhaHangSearchItemsViewHodel(@NonNull View itemView) {
            super(itemView);

            imgHinhAnh = itemView.findViewById(R.id.imgHinhAnhMonAn);
            txtTenMonAn = itemView.findViewById(R.id.txtTenMonAn);
            txtSoLuotBan = itemView.findViewById(R.id.txtSoLuotBan);
            txtGia = itemView.findViewById(R.id.txtGia);
        }

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
}
