package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Adapter.MonAnAdapter;
import com.example.online_food.Class.CategoryMonAn;
import com.example.online_food.Data.DanhGia;
import com.example.online_food.Data.MonAn;
import com.example.online_food.Data.NhaHang;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CTNhaHangActivity extends AppCompatActivity {

    private LinearLayout linear_head, layout_muaHang;
    private ImageView ivAnhNH, ivBack, ivBack_head, Cart_head, Shopping_Cart;
    private NestedScrollView layout_ct;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();;
    private TextView tvTen, tvDiaChi, tvTTNhaHang, tvTongTien, tvSoLuongMon, tvSoBL;
    private TabLayout tabMenu;
    private RecyclerView viewMonAn;
    private ArrayList<CategoryMonAn> categoryMonAns;
    private MonAnAdapter adapter;
    private EditText etTimKiem;
    private String maNhaHang = "";
    private RatingBar ratingBar;
    private float tongDiem = 0;
    private double tongLB = 0, tong = 0.0;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctnha_hang);

        // Lấy collection trong firebase
        CollectionReference referenceMenu = firestore.collection("Menu");
        CollectionReference referenceNH = firestore.collection("NhaHang");

        // Tìm id
        linear_head = findViewById(R.id.linear_head);
        ivAnhNH = findViewById(R.id.ivAnhNH);
        ivBack = findViewById(R.id.ivBack);
        ivBack_head = findViewById(R.id.ivBack_head);
        layout_ct = findViewById(R.id.layout_ct);
        tvTen = findViewById(R.id.tvTen);
//        tvTTNhaHang = findViewById(R.id.tvTTNhaHang);
        tvDiaChi = findViewById(R.id.tvDiaChi);
        tabMenu = findViewById(R.id.tabMenu);
        viewMonAn = findViewById(R.id.viewDSMonAn);
        Cart_head = findViewById(R.id.ivCart);
        Shopping_Cart = findViewById(R.id.ivShopping_cart);
//        tvTongTien = findViewById(R.id.TongTien);
//        btnGiaoHang = findViewById(R.id.btnGiaoHang);
//        layout_muaHang = findViewById(R.id.layout_muaHang);
        etTimKiem = findViewById(R.id.etTimKiem_head);
//        tvSoLuongMon = findViewById(R.id.tvSoLuongSP);
        ratingBar = findViewById(R.id.ratingBar);
        tvSoBL = findViewById(R.id.tvSoBL);

        maNhaHang = getIntent().getStringExtra("MaNhaHang");
        SharedPreferences sharedPreferences = getSharedPreferences("dataMaNH", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("manh", maNhaHang);
        editor.apply();

        //sk timkiem
        etTimKiem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Tạo Intent để chuyển đến TimkiemNhahangActivity
                    Intent intent = new Intent(CTNhaHangActivity.this, TimKiemMonAn.class);
                    startActivity(intent);
                    return true; // Ngăn sự kiện tiếp tục đến các xử lý khác
                }
                return false;
            }
        });

        // Quay về
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivBack_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // lướt scroll hiện thị thanh tìm kiếm
        linear_head.setAlpha(0);

        layout_ct.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 20) {
                    float alpha = Math.min(1, scrollY / 100f);
                    linear_head.setAlpha(alpha);
                } else {
                    linear_head.setAlpha(0);
                }
            }
        });

        // Hiện giỏ hàng
        Cart_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isUserLoggedIn()) {
                    startActivity(new Intent(CTNhaHangActivity.this, DangNhapVaDangKyActivity.class));
                }

                else {
                    startActivity(new Intent(CTNhaHangActivity.this, GioHangActivity.class));
                }
            }
        });

        Shopping_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isUserLoggedIn()) {
                    startActivity(new Intent(CTNhaHangActivity.this, DangNhapVaDangKyActivity.class));
                }

                else {
                    startActivity(new Intent(CTNhaHangActivity.this, GioHangActivity.class));
                }
            }
        });

        // Lấy thông tin nhà hàng
        referenceNH.whereEqualTo("MaNH", maNhaHang).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot != null) {
                                NhaHang nhaHang = new NhaHang();
                                for(QueryDocumentSnapshot doc: snapshot) {
                                    String tenNhaHang = doc.getString("TenNhaHang");
                                    String hinhAnhNH = doc.getString("HinhAnh");
                                    String diaChi = doc.getString("DiaChi");

                                    Glide.with(CTNhaHangActivity.this).load(hinhAnhNH).into(ivAnhNH);
                                    tvTen.setText(tenNhaHang);
                                    tvDiaChi.setText(diaChi);
//                                    tvTTNhaHang.setText(trangThai);

                                }

                            }
                        }else {
                            Log.d("MainActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

        categoryMonAns = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        viewMonAn.setLayoutManager(layoutManager);

        Map<String, Double> averageSales = new HashMap<>();

        // Lấy ds menu cùng ds món của menu đó của từng nhà hàng
        referenceMenu.whereEqualTo("MaNH", maNhaHang).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot != null) {
                                boolean isFirstTab = true;
                                List<Double> salesData = new ArrayList<>();
                                for(QueryDocumentSnapshot doc: snapshot) {
                                    String maMenu = doc.getString("MaMenu");
                                    String tenMenu = doc.getString("TenMenu");

                                    TabLayout.Tab tab = tabMenu.newTab();
                                    tab.setText(tenMenu);
                                    tabMenu.addTab(tab);

                                    if (isFirstTab) {
                                        getDS(maMenu, tenMenu);
                                    }


                                }

                                // Lọc dữ liệu của tab
                                tabMenu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        String selectedMenuName = tab.getText().toString();
                                        if ("Tất cả".equals(selectedMenuName)) {
                                            categoryMonAns.clear();
                                            for (QueryDocumentSnapshot doc : snapshot) {
                                                String maMenu = doc.getString("MaMenu");
                                                String tenMenu = doc.getString("TenMenu");
                                                getDS(maMenu, tenMenu);
                                            }
                                        } else {
                                            for (QueryDocumentSnapshot doc : snapshot) {
                                                String maMenu = doc.getString("MaMenu");
                                                String tenMenu = doc.getString("TenMenu");
                                                if (tenMenu.equals(selectedMenuName)) {
                                                    categoryMonAns.clear();
                                                    getDS(maMenu, tenMenu);
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {

                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {

                                    }
                                });

                            }
                        }else {
                            Log.d("CTNhaHangActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // lấy lượt đánh giá nhà hàng
        firestore.collection("DanhGia")
                .whereEqualTo("MaNH", maNhaHang).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            ArrayList<DanhGia> allReviews = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                String madanhgia = doc.getString("MaDanhGia");
                                String madonhang = doc.getString("MaDon");
                                String makhachhang = doc.getString("MaKhachHang");
                                String binhluan = doc.getString("BinhLuan");
                                String tgiandg = doc.getString("ThoiGianDanhGia");

                                Object sodiem = doc.get("SoDiem");
                                float sd = 0;

                                if (sodiem instanceof Number) {
                                    sd = ((Number) sodiem).floatValue();
                                } else {
                                    try {
                                        sd = Float.parseFloat(String.valueOf(sodiem));
                                    } catch (NumberFormatException e) {
                                        Log.w("MainActivity", "Invalid quantity format: " + sodiem);
                                        continue;
                                    }
                                }

                                DanhGia dg = new DanhGia(madanhgia, madonhang, makhachhang, maNhaHang, binhluan, tgiandg, sd);

                                allReviews.add(dg);
                            }

                            Log.d("TAG", "Total reviews: " + allReviews.size());
                            count = allReviews.size();

                            for (DanhGia review : allReviews) {
                                tongDiem += review.getSoDiem();
                            }
                            Log.d("TAG", "Total reviews point: " + tongDiem);
                            Log.d("TAG", "Avg reviews: " + tongDiem / count);
                            String sobl = formatBinhLuan(count);
                            float rating = formatRating(tongDiem, count);
                            float ratingFormat = Math.round(rating * 10) / 10f;
                            Log.d("TAG", "Rating: " + ratingFormat);

                            tvSoBL.setText(ratingFormat + " (" + sobl + " Bình luận)");
                            ratingBar.setRating(ratingFormat);

                        } else {
                            Log.e("TAG", "Error getting reviews: ", task.getException());
                        }
                    }
                });

    }

    @SuppressLint("DefaultLocale")
    private float formatRating(float totalRating, int numberRating) {
        float rating = (float) totalRating / numberRating;
        float minRating = Math.min(rating, 5.0f);
        return minRating;
    }

    @SuppressLint("DefaultLocale")
    private String formatBinhLuan(int luotBL) {
        if (luotBL < 1000) {
            return String.format("%d" , luotBL);
        } else {
            return String.format("999+");
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String tenDangNhap = preferences.getString("TenDangNhap", "");
        return !tenDangNhap.isEmpty();
    }

    // Lấy ds món ăn của từng menu
    private void getDS(String maMenu, String tenMenu) {
        CollectionReference reference = firestore.collection("MonAn");

        reference.whereEqualTo("MaMenu", maMenu).orderBy("MaMenu").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot != null) {
                                ArrayList<MonAn> lst = new ArrayList<>();

                                for(QueryDocumentSnapshot doc: snapshot) {
                                    String maMon = doc.getString("MaMon");
                                    String tenMon = doc.getString("TenMon");
                                    String anh = doc.getString("HinhAnh");
                                    String moTa = doc.getString("MoTa");
                                    Object soLuotBan = doc.get("SoLuotBan");
                                    Object giaTien = doc.get("GiaMon");

                                    double lb = 0, gia = 0;

                                    if (soLuotBan instanceof Number) {
                                        lb = ((Number) soLuotBan).doubleValue();
                                    } else {
                                        try {
                                            lb = Double.parseDouble(String.valueOf(soLuotBan));
                                        } catch (NumberFormatException e) {
                                            Log.w("MainActivity", "Invalid quantity format: " + soLuotBan);
                                            continue;
                                        }
                                    }

                                    if (giaTien instanceof Number) {
                                        gia = ((Number) giaTien).doubleValue();
                                    } else {
                                        try {
                                            gia = Double.parseDouble(String.valueOf(giaTien));
                                        } catch (NumberFormatException e) {
                                            Log.w("CTNhaHangActivity", "Invalid quantity format: " + giaTien);
                                            continue;
                                        }
                                    }

                                    MonAn monAn = new MonAn();
                                    monAn.setMaMon(maMon);
                                    monAn.setTenMon(tenMon);
                                    monAn.setHinhAnh(anh);
                                    monAn.setMoTa(moTa);
                                    monAn.setSoLuotBan(lb);
                                    monAn.setGiaMon(gia);

                                    lst.add(monAn);

                                }

                                if(lst.size() == 0) {
                                    adapter = new MonAnAdapter(CTNhaHangActivity.this, categoryMonAns);
                                    viewMonAn.setAdapter(adapter);
                                }

                                else  {
                                    categoryMonAns.add(new CategoryMonAn(tenMenu + " (" + lst.size() + ")", lst));
                                    adapter = new MonAnAdapter(CTNhaHangActivity.this, categoryMonAns);
                                    viewMonAn.setAdapter(adapter);
                                }

                            }
                        }else {
                            Log.d("CTNhaHangActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}