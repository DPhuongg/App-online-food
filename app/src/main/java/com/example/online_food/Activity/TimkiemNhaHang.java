package com.example.online_food.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.online_food.Adapter.NhaHangSearchAdapter;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimkiemNhaHang extends AppCompatActivity {

    private ImageView ivback, ivCancel;
    private EditText etTimKiem;
    private RecyclerView lstNhaHangItem1;
    private NhaHangSearchAdapter adapter;
    private ArrayList<MonAn> lstMonan;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timkiem_nha_hang);

        // Ánh xạ các view
        ivback = findViewById(R.id.ivback);
        ivCancel = findViewById(R.id.ivCancel);
        etTimKiem = findViewById(R.id.etSearch);
        lstNhaHangItem1 = findViewById(R.id.lstNhaHangItem); // Ánh xạ RecyclerView

        firestore = FirebaseFirestore.getInstance();
        CollectionReference referenceMenu = firestore.collection("Menu");

        // Khởi tạo danh sách món ăn
        lstMonan = new ArrayList<>();

        // Cấu hình RecyclerView và Adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lstNhaHangItem1.setLayoutManager(layoutManager);
        adapter = new NhaHangSearchAdapter(this, lstMonan); // Truyền danh sách nhóm vào Adapter
        lstNhaHangItem1.setAdapter(adapter);

        // Tìm kiếm khi gõ trong EditText
        etTimKiem.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable searchRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // delay 300ms when typing, avoid filtering to much
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> adapter.getFilter().filter(s);
                handler.postDelayed(searchRunnable, 300);
//                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý sự kiện khi nhấn nút back
        ivback.setOnClickListener(v -> finish());

        // Xử lý sự kiện khi nhấn nút cancel
        ivCancel.setOnClickListener(v -> etTimKiem.setText(""));
    }
}
