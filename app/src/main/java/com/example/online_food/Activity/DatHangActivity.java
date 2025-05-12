package com.example.online_food.Activity;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
    RadioButton btnTTKhiNhanHang, btnTTMomo, btnTTVietCodeQR, btnTTZalopay;
    LinearLayout layout_diaChi;
    Double TongTien;
    Double ThanhTien = 0.0, phiVC = 0.0, KM = 0.0;
    ArrayList<ChiTietGioHang> lst;
    DatHangAdapter datHangAdapter;
    String thoiGianDatHang;

    private AppCompatButton btnThanhToan;

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
        btnThanhToan = findViewById(R.id.btnDatDon);

        viewDonDat = findViewById(R.id.viewDonDat);
        tenNhaHang = findViewById(R.id.TenNhaHang);
        btnTTKhiNhanHang = findViewById(R.id.btnTTKhiNhanHang);
        btnTTMomo = findViewById(R.id.btnTTMomo);
        btnTTVietCodeQR = findViewById(R.id.btnTTVietCodeQR);
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



        btnTTKhiNhanHang.setOnClickListener(v -> clearAllExcept(btnTTKhiNhanHang));
        btnTTMomo.setOnClickListener(v -> clearAllExcept(btnTTMomo));
        btnTTVietCodeQR.setOnClickListener(v -> clearAllExcept(btnTTVietCodeQR));
        btnTTZalopay.setOnClickListener(v -> clearAllExcept(btnTTZalopay));


        // QUAN TRỌNG: ThanhToan là nút để chuyển Activity nên cần thêm OnClickListener
        if (btnThanhToan != null) {
            // Xử lý khi nhấn nút Thanh Toán
            Log.d(TAG, "Nút Thanh Toán được nhấn.");
            btnThanhToan.setOnClickListener(view -> {
                String phuongThuc = "";

                if (btnTTKhiNhanHang.isChecked()) {
                    phuongThuc = "Phương thức đã chọn: Thanh toán khi nhận hàng";
                    Log.d(TAG, phuongThuc);
                    openPaymentWhenReceive();
                } else if (btnTTMomo.isChecked()) {
                    phuongThuc = "Phương thức đã chọn: Thanh toán qua ví Momo";
                    Log.d(TAG, phuongThuc);
                } else if (btnTTVietCodeQR.isChecked()) {
                    phuongThuc = "Phương thức đã chọn: Thanh toán qua ví VNPay";
                    openPaymentQRActivity();
                    Log.d(TAG, phuongThuc);
                } else if (btnTTZalopay.isChecked()) {
                    phuongThuc = "Phương thức đã chọn: Thanh toán qua ví Zalopay";
                    Log.d(TAG, phuongThuc);
                    openPaymentAppToAppZaloPay();
                }

                if (phuongThuc.isEmpty()) {
                    Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Log.e(TAG, "Không tìm thấy Button với ID R.id.ThanhToan trong layout activity_ctnha_hang.xml!");
            Toast.makeText(this,"Lỗi giao diện (Nút Thanh Toán)", Toast.LENGTH_SHORT).show();
        }
    }

//    Xử lý logic chọn phương thức thanh toán
    private void clearAllExcept(RadioButton selected) {
        RadioButton[] allButtons = {btnTTKhiNhanHang, btnTTMomo, btnTTVietCodeQR, btnTTZalopay};
        for (RadioButton btn : allButtons) {
            btn.setChecked(btn == selected);
        }
    }

//  Thanh toán qua mã QR
    private void openPaymentQRActivity() {
        Intent intent = new Intent(this, ThanhToanActivity.class); // Dùng this hoặc view.getContext() đều được

        // --- !!! Lấy dữ liệu thực tế cho đơn hàng ---
        // Ví dụ: lấy từ giỏ hàng, hoặc một món ăn cụ thể đang được xem,...

        String currentOrderId = "_ORDER_" + System.currentTimeMillis(); // Tạo mã đơn hàng tạm

        // Dữ liệu QR cần phải theo định dạng hệ thống thanh toán của zalopay

        Log.d(TAG, "Chuẩn bị mở ThanhToanActivity với: OrderID=" + currentOrderId + ", amountTotal=" + ThanhTien);

        // --- Đặt dữ liệu vào Intent dùng ĐÚNG KEY đã định nghĩa trong ThanhToanActivity ---
        intent.putExtra(ThanhToanActivity.EXTRA_ORDER_ID, currentOrderId);
        double amountTotal = ThanhTien;
        intent.putExtra(ThanhToanActivity.EXTRA_TOTAL_AMOUNT, amountTotal); // Truyền số lượng


        startActivity(intent);
    }

//    Thanh toán qua ứng dụng zalopay
    private void openPaymentAppToAppZaloPay() {

    }
//    Thanh toán khi nhận hàng
    private void openPaymentWhenReceive(){

    }
}