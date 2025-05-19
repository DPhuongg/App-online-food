package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.online_food.Adapter.CTDonHangAdapter;
import com.example.online_food.Data.ChiTietDonHang;
import com.example.online_food.Data.ChiTietGioHang;
import com.example.online_food.Data.DonHang;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CTDonHangActivity extends AppCompatActivity {

    ImageView back;
    TextView diaChi, tongTien, phiVanChuyen, khuyenMai, thanhTien, tenNhaHang, hinhThuc, tTien, maDon, tgian, soMon;
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
        viewDonDat = findViewById(R.id.viewDonDat);
        tenNhaHang = findViewById(R.id.TenNhaHang);
        hinhThuc = findViewById(R.id.tvHinhThuc);
        tTien = findViewById(R.id.TongTien);
        maDon = findViewById(R.id.MaDon);
        tgian = findViewById(R.id.TGianDH);
        soMon = findViewById(R.id.SoMon);

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


        // load thong tin
        db.collection("KhachHang")
                .whereEqualTo("MaKhachHang", maKhach)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot != null){
                                for(QueryDocumentSnapshot doc : snapshot){
                                    String ten = doc.getString("TenKhach");
                                    String dc = doc.getString("DiaChi");
                                    String soDienThoai = doc.getString("SoDienThoai");

                                    diaChi.setText(ten + " | " + soDienThoai + "\n\n" + dc);
                                }
                            } else{
                                Toast.makeText(CTDonHangActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(CTDonHangActivity.this, "Lỗi khi lấy dữ liệu người dùng: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection("NhaHang")
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
                            Log.d("CTDonHangActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

        lstDon = new ArrayList<>();


        db.collection("ChiTietDonHang")
                .whereEqualTo("MaDon", maDonHang)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot snapshot = task.getResult();
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot doc : snapshot){
                                ChiTietDonHang chiTietDonHang = doc.toObject(ChiTietDonHang.class);
                                lstDon.add(chiTietDonHang);

                                soMon.setText("Tổng cộng (" + lstDon.size() + " món)");

                                adapter = new CTDonHangAdapter(CTDonHangActivity.this, lstDon);
                                viewDonDat.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            Log.d("CTDonHangActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("DonHang")
                .whereEqualTo("MaDon", maDonHang)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot snapshot = task.getResult();

                        if(task.isSuccessful()){
                            for(DocumentSnapshot doc : snapshot){
                                DonHang donHang = doc.toObject(DonHang.class);

                                NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                                String tTienformatted = format.format(donHang.getTongTien());
                                String pvcformatted = format.format(donHang.getPhiShip());
                                String km = format.format(donHang.getKhuyenMai());
                                double thanhtien = donHang.getTongTien() + donHang.getPhiShip() + donHang.getTongTien() * donHang.getKhuyenMai();
                                String thanhTienformatted = format.format(thanhtien);

                                maDon.setText(maDonHang);
                                hinhThuc.setText(donHang.getPhuongThucTT());
                                tTien.setText(tTienformatted + " đ");
                                phiVanChuyen.setText(pvcformatted + " đ");
                                khuyenMai.setText(km + " đ");
                                thanhTien.setText(thanhTienformatted + " đ");
                                tongTien.setText(tTienformatted + " đ");
                                tgian.setText(donHang.getThoiGianDatHang());
                            }
                        }
                    }
                });
    }

}