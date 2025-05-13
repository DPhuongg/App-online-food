package com.example.online_food.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Activity.CTDonHangActivity;
import com.example.online_food.Activity.DonHangActivity;
import com.example.online_food.Data.ChiTietDonHang;
import com.example.online_food.Data.DonHang;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.DonHangHolder>{
    private Context context;
    private ArrayList<DonHang> lst;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public DonHangAdapter(Context context, ArrayList<DonHang> lst) {
        this.context = context;
        this.lst = lst;
    }

    @NonNull
    @Override
    public DonHangHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DonHangHolder(LayoutInflater.from(context).inflate(R.layout.item_don_hang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DonHangHolder holder, int i) {
        DonHang donHang = lst.get(i);

        firestore.collection("ChiTietDonHang")
                .whereEqualTo("MaDon", donHang.getMaDon())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ChiTietDonHang> ctdh = new ArrayList<>();

                        // Lấy tất cả các tài liệu và thêm vào danh sách
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ChiTietDonHang chiTiet = document.toObject(ChiTietDonHang.class);
                            ctdh.add(chiTiet);
                        }

                        if (!ctdh.isEmpty()) {
                            // Lấy phần tử đầu tiên trong danh sách
                            ChiTietDonHang firstChiTiet = ctdh.get(0);

                            // Hiển thị dữ liệu lên các View của item trong RecyclerView
                            holder.tenMon.setText(firstChiTiet.getTenMon());
                            int sl = (int) firstChiTiet.getSoLuong();

                            holder.soLuong.setText(String.valueOf(sl) + "x");
                            double giaMon = firstChiTiet.getGia();
                            NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                            String giaFormatted = format.format(giaMon);
                            holder.giaTien.setText(giaFormatted + " đ");

                            // Sử dụng Glide để tải hình ảnh nếu có
                            Glide.with(context)
                                    .load(firstChiTiet.getAnh())
                                    .into(holder.Anh);
                        } else {

                        }
                    } else {

                    }
                });

        firestore.collection("DonHang")
                .whereEqualTo("MaDon", donHang.getMaDon())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(task.isSuccessful()) {
                            if(querySnapshot != null) {
                                for (QueryDocumentSnapshot doc : querySnapshot) {
                                    String trangThaiShip = doc.getString("TrangThaiShip");
                                    String trangThai = doc.getString("TrangThai");
                                    String trangThaiDG = doc.getString("TrangThaiDanhGia");
                                    Object tong = doc.get("ThanhTien");
                                    double tt = 0;
                                    if (tong instanceof Number) {
                                        tt = ((Number) tong).doubleValue();
                                    } else {
                                        try {
                                            tt = Double.parseDouble(String.valueOf(tong));
                                        } catch (NumberFormatException e) {
                                            Log.w("MainActivity", "Invalid quantity format: " + tong);
                                            continue;
                                        }
                                    }
                                    NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                                    String tongFormatted = format.format(tt);
                                    holder.tongTien.setText("Tổng số tiền: " + tongFormatted + " đ");
                                }
                            }

                            else {
                                Log.e("FirebaseError", "Lỗi khi truy vấn: ", task.getException());
                            }
                        }

                        else {
                            Log.e("FirebaseError", "Lỗi khi truy vấn: ", task.getException());
                        }
                    }
                });

        firestore.collection("NhaHang")
                .whereEqualTo("MaNH", donHang.getMaNH())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(task.isSuccessful()) {
                            if(querySnapshot != null) {
                                for(QueryDocumentSnapshot doc: querySnapshot) {
                                    String tenNH = doc.getString("TenNhaHang");
                                    holder.nhaHang.setText(tenNH);
                                }
                            }

                            else {
                                Log.e("FirebaseError", "Lỗi khi truy vấn: ", task.getException());
                            }
                        }

                        else {
                            Log.e("FirebaseError", "Lỗi khi truy vấn: ", task.getException());
                        }
                    }
                });

        holder.xemChiTiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CTDonHangActivity.class);
                intent.putExtra("MaDon", donHang.getMaDon());
                intent.putExtra("MaKhach", donHang.getMaKH());
                intent.putExtra("MaNhaHang", donHang.getMaNH());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

    public class DonHangHolder extends RecyclerView.ViewHolder {
        ImageView Anh;
        TextView tenMon, soLuong, tongTien, xemChiTiet, giaTien, nhaHang;
        Button daNhanHang, muaLai;

        public DonHangHolder(@NonNull View itemView) {
            super(itemView);

            Anh = itemView.findViewById(R.id.anh);
            soLuong = itemView.findViewById(R.id.SoLuong);
            tenMon = itemView.findViewById(R.id.TenMon);
            tongTien = itemView.findViewById(R.id.TongTien);
            giaTien = itemView.findViewById(R.id.GiaTien);
            xemChiTiet = itemView.findViewById(R.id.ctdh);
            daNhanHang = itemView.findViewById(R.id.btnDaNhanhang);
            nhaHang = itemView.findViewById(R.id.NhaHang);
            muaLai = itemView.findViewById(R.id.btnMuaLai);
        }
    }
}
