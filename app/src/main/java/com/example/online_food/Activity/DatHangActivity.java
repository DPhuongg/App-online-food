package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.online_food.Adapter.DatHangAdapter;
import com.example.online_food.Data.ChiTietGioHang;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class DatHangActivity extends AppCompatActivity {
    ImageView back;
    TextView diaChi, tongTien, phiVanChuyen, khuyenMai, thanhTien, tongThanhToan, tenNhaHang, vanChuyen;
    Button datHang;
    RecyclerView viewDonDat;
    RadioButton btnTTKhiNhanHang, btnTTMomo, btnTTVNPay, btnTTZalopay;
    LinearLayout layout_diaChi;
    Double TongTien;
    Double ThanhTien = 0.0, phiVC = 0.0, KM = 0.0;
    ArrayList<ChiTietGioHang> lst;
    DatHangAdapter datHangAdapter;
    String thoiGianDatHang;



    FirebaseFirestore firestore = FirebaseFirestore.getInstance();;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dat_hang);

        // Ket noi firebase
        CollectionReference referenceChiTietGH = firestore.collection("ChiTietGioHang");
        CollectionReference referenceNH = firestore.collection("NhaHang");
        CollectionReference referenceShip = firestore.collection("Shipper");
        CollectionReference referenceDH = firestore.collection("DonHang");
        CollectionReference referenceCTDH = firestore.collection("ChiTietDonHang");

        // Tìm id
        back = findViewById(R.id.Back);
        diaChi = findViewById(R.id.tvDiaChiGH);
        tongTien = findViewById(R.id.TongTienHang);
        phiVanChuyen = findViewById(R.id.PhiVanChuyen);
        khuyenMai = findViewById(R.id.KhuyenMai);
        thanhTien = findViewById(R.id.ThanhTien);
        tongThanhToan = findViewById(R.id.TongThanhToan);
        datHang = findViewById(R.id.btnDatDon);
        viewDonDat = findViewById(R.id.viewDonDat);
        tenNhaHang = findViewById(R.id.TenNhaHang);
        btnTTKhiNhanHang = findViewById(R.id.btnTTKhiNhanHang);
        btnTTMomo = findViewById(R.id.btnTTMomo);
        btnTTVNPay = findViewById(R.id.btnTTVNPay);
        btnTTZalopay = findViewById(R.id.btnTTZalopay);
        layout_diaChi = findViewById(R.id.layout_diaChi);


        // trở về
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        // Lấy thông tin
        SharedPreferences sharedPreferences = getSharedPreferences("dataMaNH", MODE_PRIVATE);
        String maNH = sharedPreferences.getString("manh","");
        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        String makhDN = sharedPreferences1.getString("Makh","");

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null) {
            TongTien = b.getDouble("TongTien");
            ThanhTien = TongTien + KM * TongTien + phiVC;

            String dcgh = b.getString("DiaChi");

            if(dcgh == null || dcgh.trim().isEmpty()){
                new AlertDialog.Builder(this)
                        .setTitle("Thông báo")
                        .setMessage("Hãy nhập địa chỉ giao hàng của bạn!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(DatHangActivity.this, DiaChiActivity.class);
                                intent.putExtra("MaKhach", makhDN);
                                startActivity(intent);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }

        NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
        String giaFormatted = format.format(TongTien);
        String thanhTienFormatted = format.format(ThanhTien);
        String phiVCformatted = format.format(phiVC);
        String KMformatted = format.format(KM);

        tongTien.setText(giaFormatted + " đ");
        thanhTien.setText(thanhTienFormatted + " đ");
        phiVanChuyen.setText(phiVCformatted + " đ");
        khuyenMai.setText(KMformatted + " đ");
        tongThanhToan.setText(thanhTienFormatted + " đ");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        viewDonDat.setLayoutManager(layoutManager);


        // địa chỉ giao hàng
        layout_diaChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DatHangActivity.this, DiaChiActivity.class);
                intent1.putExtra("MaKhach", makhDN);
                startActivity(intent1);
            }
        });


        // tên nhà hàng
        referenceNH
                .whereEqualTo("MaNH", maNH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot snapshot = task.getResult();
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot doc: snapshot) {
                                String tenNH = doc.getString("TenNhaHang");
                                tenNhaHang.setText(tenNH);
                            }
                        }
                        else {
                            Log.d("DatHangActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });


        // ds món muốn đặt
        lst = new ArrayList<>();

        referenceChiTietGH
                .whereEqualTo("MaNH", maNH)
                .whereEqualTo("MaKH", makhDN)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot != null) {
                                for(QueryDocumentSnapshot doc: snapshot) {
                                    ChiTietGioHang chiTietGioHang = doc.toObject(ChiTietGioHang.class);
                                    lst.add(chiTietGioHang);
                                }

                                datHangAdapter = new DatHangAdapter(DatHangActivity.this, lst);
                                viewDonDat.setAdapter(datHangAdapter);
                                datHangAdapter.notifyDataSetChanged();
                            }
                        }

                        else {
                            Log.e("FirebaseError", "Lỗi khi truy vấn: ", task.getException());
                        }
                    }
                });


        // Định dạng ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd - MM - yyyy");
        thoiGianDatHang = dateFormat.format(new Date());

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}