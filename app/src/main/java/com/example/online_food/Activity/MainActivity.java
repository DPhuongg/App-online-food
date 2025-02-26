package com.example.online_food.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.online_food.Adapter.BannerAdapter;
import com.example.online_food.Adapter.CategoryAdapter;
import com.example.online_food.Adapter.NhaHangItemsAdapter;
import com.example.online_food.Class.Banner;
import com.example.online_food.Class.Category;
import com.example.online_food.Class.CategoryNhaHang;
import com.example.online_food.Data.NhaHang;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {
    //banner
    private ViewPager vpBanner;
    private ArrayList<Banner> banners;
    private BannerAdapter bannerAdapter;
    private CircleIndicator indicator;
    private Timer timer;
    private int currentPage = 0;
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 3000;

    // List Category
    private RecyclerView viewCate;
    private CategoryAdapter categoryAdapter;

    // List NHA HANG
    private RecyclerView viewNhaHang;
    private ArrayList<CategoryNhaHang> catergoryNhaHangs = new ArrayList<>();
    private NhaHangItemsAdapter adapter;

    //ProcessBar
    private ProgressBar progressBar;

    //Chuyen tab
    private LinearLayout DonHang, LichSu, TaiKhoan;


    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tìm id
        vpBanner = findViewById(R.id.vpBanner);
        indicator = findViewById(R.id.indicator);
        viewCate = findViewById(R.id.viewCate);
        viewNhaHang = findViewById(R.id.viewNhaHang);
        progressBar = findViewById(R.id.progressBar);
        DonHang = findViewById(R.id.DonHang);
        LichSu = findViewById(R.id.LichSu);
        TaiKhoan = findViewById(R.id.TaiKhoan);

        // Lay bang nha hang trong firebase
        CollectionReference referenceNH = firestore.collection("NhaHang");



        // BANNER
        banners = new ArrayList<>();
        banners.add(new Banner("banner_5"));
        banners.add(new Banner("banner_1"));
        banners.add(new Banner("banner_2"));

        bannerAdapter = new BannerAdapter(this, banners);
        vpBanner.setAdapter(bannerAdapter);
        indicator.setViewPager(vpBanner);
        bannerAdapter.registerDataSetObserver(indicator.getDataSetObserver());

        //chay Banner
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currentPage == vpBanner.getAdapter().getCount() - 1) {
                    currentPage = 0;
                } else {
                    currentPage++;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vpBanner.setCurrentItem(currentPage, true);
                    }
                });
            }
        }, DELAY_MS, PERIOD_MS);



        // CATEGORY
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        viewCate.setLayoutManager(layoutManager);

        ArrayList<Category> categories = new ArrayList<>();

        categories.add(new Category("Đồ ăn", "food"));
        categories.add(new Category("Đồ uống", "drink"));
        categories.add(new Category("Đồ ăn vặt", "fast_food"));
        categories.add(new Category("Đồ ngọt", "muffin"));

        categoryAdapter  = new CategoryAdapter(this, categories);
        viewCate.setAdapter(categoryAdapter);



        // NHA HANG
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        viewNhaHang.setLayoutManager(layoutManager1);
        // List Nha Hang
        progressBar.setVisibility(View.VISIBLE);
        referenceNH.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    if(snapshot != null){
                        ArrayList<NhaHang> lstNhaHang = new ArrayList<>();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String maNH = doc.getString("MaNH");
                            String tenNhaHang = doc.getString("TenNhaHang");
                            String hinhAnhNH = doc.getString("HinhAnh");

                            NhaHang nhaHang = new NhaHang();
                            nhaHang.setTenNhaHang(tenNhaHang);
                            nhaHang.setHinhAnh(hinhAnhNH);
                            nhaHang.setMaNH(maNH);

                            lstNhaHang.add(nhaHang);
                        }

                        catergoryNhaHangs.add(new CategoryNhaHang("Quán ăn ngon", lstNhaHang));
                        catergoryNhaHangs.add(new CategoryNhaHang("Bữa trưa vui vẻ", lstNhaHang));
                        adapter = new NhaHangItemsAdapter(MainActivity.this, catergoryNhaHangs);
                        viewNhaHang.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                }else {
                    Log.d("MainActivity", "Error getting documents: ", task.getException());
                }
            }
        });



        // CHUYEN TAB
        DonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DonHangActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        LichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LichSuActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        TaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TaiKhoanActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }
}