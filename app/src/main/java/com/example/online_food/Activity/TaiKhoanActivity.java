package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
        setContentView(R.layout.activity_don_hang);


        // Tim id
        Home = findViewById(R.id.Home);
        DonHang = findViewById(R.id.DonHang);
        LichSu = findViewById(R.id.LichSu);
        TaiKhoan = findViewById(R.id.TaiKhoan);

    }
}