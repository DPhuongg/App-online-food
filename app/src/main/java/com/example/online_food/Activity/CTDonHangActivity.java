package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.online_food.Adapter.CTDonHangAdapter;
import com.example.online_food.Data.ChiTietDonHang;
import com.example.online_food.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CTDonHangActivity extends AppCompatActivity {

    ImageView back;
    TextView diaChi, tongTien, phiVanChuyen, khuyenMai, thanhTien, tenNhaHang, hinhThuc, tTien, maDon, tgian, soMon;
    Button btntrangThai, btnTrangThai1;
    RecyclerView viewDonDat;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<ChiTietDonHang> lstDon;
    CTDonHangAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctdon_hang);

        // Tìm id
        back = findViewById(R.id.Back);
        diaChi = findViewById(R.id.tvDiaChiGH);
        tongTien = findViewById(R.id.TongTienHang);
        phiVanChuyen = findViewById(R.id.PhiVanChuyen);
        khuyenMai = findViewById(R.id.KhuyenMai);
        thanhTien = findViewById(R.id.ThanhTien);
        btntrangThai = findViewById(R.id.btnTT);
        viewDonDat = findViewById(R.id.viewDonDat);
        tenNhaHang = findViewById(R.id.TenNhaHang);
        hinhThuc = findViewById(R.id.tvHinhThuc);
        tTien = findViewById(R.id.TongTien);
        maDon = findViewById(R.id.MaDon);
        tgian = findViewById(R.id.TGianDH);
        soMon = findViewById(R.id.SoMon);
        btnTrangThai1 = findViewById(R.id.btnTT1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        viewDonDat.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        String maDonHang = intent.getStringExtra("MaDon");
        String maKhach = intent.getStringExtra("MaKhach");
        String maNH = intent.getStringExtra("MaNhaHang");

        // trở về
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}