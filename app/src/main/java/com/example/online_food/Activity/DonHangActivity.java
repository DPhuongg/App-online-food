package com.example.online_food.Activity;

import static java.lang.System.load;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.online_food.Adapter.DonHangAdapter;
import com.example.online_food.Data.DonHang;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DonHangActivity extends AppCompatActivity {
    private LinearLayout Home, DonHang, LichSu, TaiKhoan, viewDH, layout;
    private TabLayout tabMenu;
    private ImageView ivDH;
    private TextView tvDH;
    private RecyclerView viewDHItem;
    private DonHangAdapter donHangAdapter;
    private ArrayList<DonHang> lstDH;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;

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
        tabMenu = findViewById(R.id.tabMenu);
        viewDH = findViewById(R.id.viewDH);
        ivDH = findViewById(R.id.ivDH);
        tvDH = findViewById(R.id.tvDH);
        viewDHItem = findViewById(R.id.viewDHItem);
        progressBar = findViewById(R.id.progressBar);
        layout = findViewById(R.id.linearLayout2);

        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        viewDHItem.setLayoutManager(layoutManager);

        // Tab Menu
        tabMenu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadData(0);
                        break;

                    case 1:
                        loadData(1);
                        break;

                    case 2:
                        loadData(2);
                        break;

                    case 3:
                        loadData(3);
                        break;

                    default:
                        loadData(4);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // CHUYEN TAB
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonHangActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        LichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonHangActivity.this, LichSuActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        TaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonHangActivity.this, TaiKhoanActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        int selectedTab = getIntent().getIntExtra("SelectedTab", 0);
        TabLayout.Tab tab = tabMenu.getTabAt(selectedTab);
        if (tab != null) {
            tab.select();
            loadData(selectedTab);
        }
    }

    private void loadData(int position) {
        switch (position) {
            case 0:
                load("Chờ xác nhận");
                break;
            case 1:
                load("Chờ giao hàng");
                break;
            case 2:
                load("Đã giao");
                break;
            case 3:
                load("Đã hủy");
                break;
            default:
                loadDanhGia();
                break;
        }

    }

    private void load(String trangThai) {
        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        String makhDN = sharedPreferences1.getString("Makh","");

        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

        firestore.collection("DonHang")
                .whereEqualTo("TrangThai", trangThai)
                .whereEqualTo("MaKH", makhDN)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(task.isSuccessful()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }, 1000);
                            if(querySnapshot != null) {
                                lstDH = new ArrayList<>();
                                for (QueryDocumentSnapshot doc: querySnapshot) {
                                    DonHang donHang = doc.toObject(DonHang.class);
                                    lstDH.add(donHang);
                                }
                                donHangAdapter = new DonHangAdapter(DonHangActivity.this, lstDH);
                                viewDHItem.setAdapter(donHangAdapter);
                                donHangAdapter.notifyDataSetChanged();

                                layout.setVisibility(View.VISIBLE);

                                if (!lstDH.isEmpty()) {
                                    viewDH.setVisibility(View.GONE);
                                }

                                else {
                                    viewDH.setVisibility(View.VISIBLE);
                                    ivDH.setImageResource(R.drawable.smartphone);
                                    tvDH.setText("Bạn chưa có đơn hàng nào");
                                }

                            }
                        }

                        else {
                            Log.e("FirebaseError", "Lỗi khi truy vấn: ", task.getException());
                        }
                    }
                });
    }

    private void loadDanhGia() {

    }
}