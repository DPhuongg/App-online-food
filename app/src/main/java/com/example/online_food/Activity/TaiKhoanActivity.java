package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.online_food.R;

public class TaiKhoanActivity extends AppCompatActivity {

    private LinearLayout Home, DonHang, LichSu, TaiKhoan;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tai_khoan);


        // Tim id
        Home = findViewById(R.id.Home);
        DonHang = findViewById(R.id.DonHang);
        LichSu = findViewById(R.id.LichSu);
        TaiKhoan = findViewById(R.id.TaiKhoan);


        // CHUYEN TAB
        DonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaiKhoanActivity.this, DonHangActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        LichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaiKhoanActivity.this, LichSuActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaiKhoanActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
            }
        });

    }
}