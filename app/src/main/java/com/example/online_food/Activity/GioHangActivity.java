package com.example.online_food.Activity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Adapter.GiohangAdapter;
import com.example.online_food.Class.OnQuantityChangeListener;
import com.example.online_food.Class.OnSLTLoadedListener;
import com.example.online_food.Data.ChiTietGioHang;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import android.app.AlertDialog;
public class GioHangActivity extends AppCompatActivity {

    TextView xoaTC, TongTien;
    ImageView ivDong;
    Button btnGiaoHang;
    RecyclerView viewGH;
    GiohangAdapter adapter;
    ArrayList<ChiTietGioHang> lst;
    FirebaseFirestore db;
    String maNH = "";
    String makhDN = "";
    Double tongtien = 0.0;
    LinearLayout dh;
    String dcgh;
    String maMon;
    ProgressBar progressBar;
    LinearLayout view;
    int sl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        xoaTC = findViewById(R.id.tvXoaTC);
        ivDong = findViewById(R.id.ivDong);
        viewGH = findViewById(R.id.viewGioHang);
        btnGiaoHang = findViewById(R.id.btnMuaHang);
        TongTien = findViewById(R.id.tvTinhTien);
        dh =findViewById(R.id.layout_muaHang);
        progressBar = findViewById(R.id.progressBar);
        view = findViewById(R.id.viewGH);

        // Thoát khỏi giỏ hàng
        ivDong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Khởi tạo danh sách nhà hàng
        lst = new ArrayList<ChiTietGioHang>();

        // Cấu hình RecyclerView và Adapter
        SharedPreferences sharedPreferences = getSharedPreferences("dataMaNH", MODE_PRIVATE);
        maNH = sharedPreferences.getString("manh","");
        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        makhDN = sharedPreferences1.getString("Makh","");
        CollectionReference ctghfirebase = db.collection("ChiTietGioHang");
        Log.d("GioHangActivity", "manh: " + maNH);
        Log.d("GioHangActivity", "makhDN" + makhDN);

        progressBar.setVisibility(View.VISIBLE);

        // Truy vấn để lấy các món ăn với điều kiện maKH và maNH
        ctghfirebase
                .whereEqualTo("MaKH", makhDN)
                .whereEqualTo("MaNH", maNH)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        lst.clear(); // Xóa danh sách cũ

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        }, 1000);

                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            ChiTietGioHang chiTietGioHang = document.toObject(ChiTietGioHang.class);
                            double gia = chiTietGioHang.getGiaMon();
                            maMon = chiTietGioHang.getMaMon();
                            sl = chiTietGioHang.getSoLuong();

                            tongtien += gia * sl;

                            // Kiểm tra số lượng tồn trong Firestore
                            loadSLT(maMon, new OnSLTLoadedListener() {
                                @Override
                                public void onSLTLoaded(double slt) {
                                    // Nếu số lượng tồn là 0, trừ đi giá trị
                                    if (slt == 0) {
                                        tongtien -= gia * sl; // Trừ tổng tiền
                                    }

                                    // Cập nhật lại TextView sau khi tổng tiền đã thay đổi
                                    NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                                    String giaFormatted = format.format(tongtien);
                                    TongTien.setText(giaFormatted + " đ");

                                    // Cập nhật giỏ hàng
                                    lst.add(chiTietGioHang);
                                    viewGH.setLayoutManager(new LinearLayoutManager(GioHangActivity.this));

                                    // Khởi tạo adapter và listener để xử lý thay đổi số lượng
                                    adapter = new GiohangAdapter(GioHangActivity.this, lst, new OnQuantityChangeListener() {
                                        @Override
                                        public void onQuantityChanged(double price, int quantity) {
                                            // Cập nhật lại tổng tiền khi số lượng thay đổi
                                            tongtien += price; // Cập nhật tổng tiền
                                            if (quantity == 0) {

                                            }
                                            Log.d("Tongtien", "Tien = " + tongtien);

                                            // Cập nhật tổng tiền trên giao diện
                                            NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                                            String giaFormatted = format.format(tongtien);
                                            TongTien.setText(giaFormatted + " đ");

                                            // Ẩn/hiện nút đặt hàng
                                            if (tongtien == 0) {
                                                dh.setVisibility(View.GONE);
                                            } else {
                                                dh.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                    if(lst.size() != 0) {
                                        view.setVisibility(View.GONE);
                                    }

                                    else {
                                        view.setVisibility(View.VISIBLE);
                                    }
                                    // Gán adapter cho RecyclerView
                                    viewGH.setAdapter(adapter);
                                    // Cập nhật adapter sau khi dữ liệu thay đổi
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }


                    } else {
                        Log.e("FirebaseError", "Lỗi khi truy vấn: ", task.getException());
                    }
                });

        xoaTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog for confirmation
                new AlertDialog.Builder(GioHangActivity.this)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc muốn xóa tất cả không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Proceed with deletion if "Yes" is clicked
                            CollectionReference xoagh = db.collection("ChiTietGioHang");
                            tongtien = 0.0;
                            NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                            String giaFormatted = format.format(tongtien);

                            TongTien.setText(giaFormatted + " đ");

                            // Query to find items to delete based on MaKH and MaNH
                            xoagh
                                    .whereEqualTo("MaKH", makhDN)
                                    .whereEqualTo("MaNH", maNH)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                document.getReference().delete()
                                                        .addOnSuccessListener(aVoid -> {
                                                            Log.d("GioHangActivity", "Xóa thành công món: " + document.getString("TenMon"));
                                                            Toast.makeText(GioHangActivity.this, "Đã xóa món: " + document.getString("TenMon"), Toast.LENGTH_SHORT).show();
                                                        })
                                                        .addOnFailureListener(e -> Log.e("FirebaseError", "Lỗi khi xóa: ", e));
                                            }
                                            // Clear the list and update the adapter
                                            lst.clear();
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Log.e("FirebaseError", "Lỗi khi truy vấn: ", task.getException());
                                        }
                                    });
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            // Dismiss the dialog if "No" is clicked
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        // Đặt hàng
        btnGiaoHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GioHangActivity.this, DatHangActivity.class);
                Bundle b = new Bundle();
                b.putDouble("TongTien", tongtien);
                b.putString("DiaChi", dcgh);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    private void loadSLT(String ma, OnSLTLoadedListener listener) {
        db.collection("MonAn")
                .whereEqualTo("MaMon", ma)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                MonAn monAn = doc.toObject(MonAn.class);
                                double slt = monAn.getSoLuongTon();
                                // Gọi listener khi đã có số lượng tồn
                                listener.onSLTLoaded(slt);
                            }
                        } else {
                            listener.onSLTLoaded(0); // Nếu không tìm thấy món ăn
                        }
                    } else {
                        Log.e("FirebaseError", "Lỗi khi lấy thông tin món ăn: ", task.getException());
                        listener.onSLTLoaded(0); // Nếu gặp lỗi, trả về 0
                    }
                });
    }

    @Override
    protected void onResume() {
        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        String makhDN = sharedPreferences1.getString("Makh","");
        super.onResume();

        db.collection("KhachHang")
                .whereEqualTo("MaKhachHang", makhDN)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String dc = doc.getString("DiaChi");
                                dcgh = dc;
                            }
                        }
                    }
                });
    }
}