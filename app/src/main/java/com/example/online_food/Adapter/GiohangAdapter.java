package com.example.online_food.Adapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Activity.CTMonAnActivity;

import com.example.online_food.Class.OnQuantityChangeListener;
import com.example.online_food.Data.ChiTietGioHang;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GiohangAdapter extends RecyclerView.Adapter<GiohangAdapter.GiohangViewHolder> {
    private Context context;
    private ArrayList<ChiTietGioHang> litmonan;
    private OnQuantityChangeListener quantityChangeListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    int soLuong;
    int slt;
    int sl1;
    public GiohangAdapter(Context context, ArrayList<ChiTietGioHang> lma, OnQuantityChangeListener quantityChangeListener) {
        this.context = context;
        this.litmonan = lma;
        this.quantityChangeListener = quantityChangeListener;
    }

    @NonNull
    @Override
    public GiohangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gio_hang, parent, false);
        return new GiohangViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull GiohangViewHolder holder, int position) {

        ChiTietGioHang monAn = litmonan.get(position);
        Log.d("Soluong", "Du lieu " + monAn.getTenMon() +" "+ monAn.getMaNH() +" "+ monAn.getSoLuong());

        db.collection("MonAn")
                .whereEqualTo("MaMon", monAn.getMaMon())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MonAn monAnData = document.toObject(MonAn.class);
                            double slt = monAnData.getSoLuongTon(); // Lấy SLT từ bảng MonAn

                            if(slt > 0) {
                                holder.tvHetHang.setText("");
                                holder.layoutSL.setVisibility(View.VISIBLE);
                            }

                            else {
                                holder.tvHetHang.setText("Hết hàng");
                                holder.layoutSL.setVisibility(View.GONE);
                                holder.tvTenMon.setTextColor(R.color.color6);
                                holder.tvGia.setTextColor(R.color.color6);
                            }
                        }
                    } else {
                        Log.e("FirebaseQuery", "Failed to fetch SLT from MonAn: " + task.getException());
                    }
                });

        holder.tvTenMon.setText(monAn.getTenMon());
        //holder.tvghichu.setText(monAn.getMoTa());
        Glide.with(context).load(monAn.getAnh()).into(holder.ivAnhMonAn);

        soLuong = monAn.getSoLuong();
        holder.tvSoLuong.setText(String.valueOf(soLuong));

        double giaMon = monAn.getGiaMon();
        NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
        String giaFormatted = format.format(giaMon);
        holder.tvGia.setText(giaFormatted + " đ");

        // Khởi tạo số lượng món ăn (có thể khởi tạo từ dữ liệu của bạn)
        //int[] soLuong = {0};// Số lượng ban đầu

        // Hiển thị hoặc ẩn nút giảm và số lượng tùy thuộc vào giá trị ban đầu của soLuong
        if (soLuong == 0) {
            holder.ivGiam.setVisibility(View.GONE);
            holder.tvSoLuong.setVisibility(View.GONE);
            holder.itemgh.setVisibility(View.GONE);

        } else {
            holder.ivGiam.setVisibility(View.VISIBLE);
            holder.tvSoLuong.setVisibility(View.VISIBLE);
            holder.itemgh.setVisibility(View.VISIBLE);
        }
        holder.ivTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slt = monAn.getSoLuong();
                Log.d("Soluong", "lt : "+slt);
                if(slt > 0){

                    db.collection("MonAn").whereEqualTo("MaMon", monAn.getMaMon())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if(task.isSuccessful()) {
                                        if(querySnapshot != null) {
                                            for(QueryDocumentSnapshot doc: querySnapshot) {
                                                MonAn ma = doc.toObject(MonAn.class);

                                                if(ma.getSoLuongTon() > slt) {
                                                    slt += 1;
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                    // Truy vấn để tìm món ăn theo MaKH, MaNH, và MaMon
                                                    db.collection("ChiTietGioHang")
                                                            .whereEqualTo("MaKH", monAn.getMaKH())  // Điều kiện mã khách hàng
                                                            .whereEqualTo("MaNH", monAn.getMaNH())  // Điều kiện mã nhà hàng
                                                            .whereEqualTo("MaMon", monAn.getMaMon()) // Điều kiện mã món ăn
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            // Cập nhật số lượng cho mỗi document tìm được
                                                                            db.collection("ChiTietGioHang")
                                                                                    .document(document.getId()) // Lấy document ID từ kết quả truy vấn
                                                                                    .update("SoLuong", slt)
                                                                                    .addOnSuccessListener(aVoid -> {
                                                                                        Log.d("FirebaseUpdate", "Cập nhật số lượng món ăn thành công!");
                                                                                    })
                                                                                    .addOnFailureListener(e -> {
                                                                                        Log.e("FirebaseUpdate", "Cập nhật thất bại: " + e.getMessage());
                                                                                    });
                                                                        }
                                                                    } else {
                                                                        Log.e("FirebaseQuery", "Không tìm thấy món ăn phù hợp");
                                                                    }
                                                                }
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Log.e("FirebaseQuery", "Lỗi truy vấn: " + e.getMessage());
                                                            });
                                                    holder.tvSoLuong.setText(String.valueOf(slt));
                                                    monAn.setSoLuong(slt);
                                                    quantityChangeListener.onQuantityChanged(giaMon, slt);
                                                    // Hiển thị nút giảm và số lượng nếu số lượng > 0
                                                    if (slt > 0) {
                                                        holder.ivGiam.setVisibility(View.VISIBLE);
                                                        holder.tvSoLuong.setVisibility(View.VISIBLE);
                                                        holder.itemgh.setVisibility(View.VISIBLE);
                                                    }
                                                }

                                                else {
                                                    new AlertDialog.Builder(context)
                                                            .setTitle("Thông báo")
                                                            .setMessage("Số lượng món còn lại không đủ để tăng thêm!")
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setCancelable(false)
                                                            .show();
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                }

            }
        });

        holder.ivGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sl1 = monAn.getSoLuong();
                Log.d("Soluong", "Số lượng hiện tại: " + sl1);

                sl1 -= 1;  // Giảm số lượng
                // Cập nhật lại TextView hiển thị số lượng
                holder.tvSoLuong.setText(String.valueOf(sl1));

                // Cập nhật tổng tiền (giảm giá món ăn khi số lượng giảm)
                quantityChangeListener.onQuantityChanged(-giaMon, sl1);
                Log.d("Soluong", "Số lượng sau khi giảm: " + sl1);


                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Truy vấn để tìm món ăn theo MaKH, MaNH, và MaMon
                db.collection("ChiTietGioHang")
                        .whereEqualTo("MaKH", monAn.getMaKH())  // Điều kiện mã khách hàng
                        .whereEqualTo("MaNH", monAn.getMaNH())  // Điều kiện mã nhà hàng
                        .whereEqualTo("MaMon", monAn.getMaMon()) // Điều kiện mã món ăn
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (sl1 == 0) {

                                        // Xóa document khỏi Firestore khi số lượng bằng 0
                                        db.collection("ChiTietGioHang")
                                                .document(document.getId())
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("FirebaseDelete", "Xóa món ăn thành công khỏi Firebase!");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("FirebaseDelete", "Xóa món ăn thất bại: " + e.getMessage());
                                                });
                                    } else {
                                        // Cập nhật số lượng cho mỗi document tìm được
                                        db.collection("ChiTietGioHang")
                                                .document(document.getId())
                                                .update("SoLuong", sl1)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("FirebaseUpdate", "Cập nhật số lượng món ăn thành công!");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("FirebaseUpdate", "Cập nhật thất bại: " + e.getMessage());
                                                });
                                    }
                                }
                            } else {
                                Log.e("FirebaseQuery", "Không tìm thấy món ăn phù hợp");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirebaseQuery", "Lỗi truy vấn: " + e.getMessage());
                        });

                monAn.setSoLuong(sl1);

                // Nếu số lượng bằng 0, ẩn nút giảm và item
                if (sl1 == 0) {
                    holder.ivGiam.setVisibility(View.GONE);
                    holder.tvSoLuong.setVisibility(View.GONE);
                    holder.itemgh.setVisibility(View.GONE);
                }

            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa món này khỏi giỏ hàng?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Lấy SLT từ bảng MonAn
                            db.collection("MonAn")
                                    .whereEqualTo("MaMon", monAn.getMaMon())
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                MonAn monAnData = document.toObject(MonAn.class);
                                                double slt = monAnData.getSoLuongTon(); // Lấy SLT từ bảng MonAn

                                                // Thực hiện xóa trong bảng ChiTietGioHang
                                                db.collection("ChiTietGioHang")
                                                        .whereEqualTo("MaKH", monAn.getMaKH())
                                                        .whereEqualTo("MaNH", monAn.getMaNH())
                                                        .whereEqualTo("MaMon", monAn.getMaMon())
                                                        .get()
                                                        .addOnCompleteListener(taskChiTiet -> {
                                                            if (taskChiTiet.isSuccessful() && !taskChiTiet.getResult().isEmpty()) {
                                                                for (QueryDocumentSnapshot chiTietDoc : taskChiTiet.getResult()) {
                                                                    db.collection("ChiTietGioHang")
                                                                            .document(chiTietDoc.getId())
                                                                            .delete()
                                                                            .addOnSuccessListener(aVoid -> {
                                                                                Log.d("FirebaseDelete", "Item deleted successfully from Firebase!");

                                                                                int position = holder.getAdapterPosition();
                                                                                litmonan.remove(position);

                                                                                notifyItemRemoved(position);
                                                                                notifyItemRangeChanged(position, litmonan.size());

                                                                                // Chỉ cập nhật tổng tiền nếu SLT > 0
                                                                                if (slt > 0) {
                                                                                    quantityChangeListener.onQuantityChanged(-giaMon * monAn.getSoLuong(), 0);
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                Log.e("FirebaseDelete", "Failed to delete item: " + e.getMessage());
                                                                            });
                                                                }
                                                            } else {
                                                                Log.e("FirebaseQuery", "Item not found for deletion.");
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("FirebaseQuery", "Query failed: " + e.getMessage());
                                                        });
                                            }
                                        } else {
                                            Log.e("FirebaseQuery", "Failed to fetch SLT from MonAn: " + task.getException());
                                        }
                                    });
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CTMonAnActivity.class);
                intent.putExtra("MaMon", monAn.getMaMon());
                context.startActivity(intent);
            }
        });

    }

    private void capNhatSoLuongMonAn(ChiTietGioHang monAn, int soLuongMoi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn để tìm món ăn theo MaKH, MaNH, và MaMon
        db.collection("ChiTietGioHang")
                .whereEqualTo("MaKH", monAn.getMaKH())  // Điều kiện mã khách hàng
                .whereEqualTo("MaNH", monAn.getMaNH())  // Điều kiện mã nhà hàng
                .whereEqualTo("MaMon", monAn.getMaMon()) // Điều kiện mã món ăn
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Cập nhật số lượng cho mỗi document tìm được
                            db.collection("ChiTietGioHang")
                                    .document(document.getId()) // Lấy document ID từ kết quả truy vấn
                                    .update("SoLuong", soLuongMoi)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FirebaseUpdate", "Cập nhật số lượng món ăn thành công!" + soLuongMoi);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirebaseUpdate", "Cập nhật thất bại: " + e.getMessage());
                                    });
                        }
                    } else {
                        Log.e("FirebaseQuery", "Không tìm thấy món ăn phù hợp");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseQuery", "Lỗi truy vấn: " + e.getMessage());
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
        return litmonan.size();
    }

    public static class GiohangViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAnhMonAn, ivTang, ivGiam, ivDelete;
        TextView tvTenMon, tvLuotBan, tvSoLuong, tvGia, tvghichu, tvHetHang;
        LinearLayout itemgh, layoutSL;

        public GiohangViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAnhMonAn = itemView.findViewById(R.id.Anh);
            ivDelete = itemView.findViewById(R.id.delete);
            ivTang = itemView.findViewById(R.id.TangSL);
            ivGiam = itemView.findViewById(R.id.GiamSL);
            tvghichu = itemView.findViewById(R.id.GhiChu);
            tvTenMon = itemView.findViewById(R.id.TenMon);
            tvSoLuong = itemView.findViewById(R.id.SL);
            tvGia = itemView.findViewById(R.id.GiaMonAn);
            itemgh = itemView.findViewById(R.id.itemgh);
            tvHetHang = itemView.findViewById(R.id.tvHetHang);
            layoutSL = itemView.findViewById(R.id.layoutSL);
        }
    }
}
