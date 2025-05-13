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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.online_food.Adapter.DatHangAdapter;
import com.example.online_food.Data.ChiTietGioHang;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


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
    String ptThanhToan = "";
    String dcgh;



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

        // Pt Thanh toán
        btnTTKhiNhanHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTTKhiNhanHang.setChecked(true);
                btnTTMomo.setChecked(false);
                btnTTZalopay.setChecked(false);
                btnTTVNPay.setChecked(false);
                ptThanhToan = "Thanh toán khi nhận hàng";
            }
        });

        btnTTMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTTKhiNhanHang.setChecked(false);
                btnTTMomo.setChecked(true);
                btnTTZalopay.setChecked(false);
                btnTTVNPay.setChecked(false);
                ptThanhToan = "Thanh toán qua ví Momo";
            }
        });

        btnTTVNPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTTKhiNhanHang.setChecked(false);
                btnTTMomo.setChecked(false);
                btnTTZalopay.setChecked(false);
                btnTTVNPay.setChecked(true);
                ptThanhToan = "Thanh toán qua ví VNPay";
            }
        });

        btnTTZalopay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTTKhiNhanHang.setChecked(false);
                btnTTMomo.setChecked(false);
                btnTTZalopay.setChecked(true);
                btnTTVNPay.setChecked(false);
                ptThanhToan = "Thanh toán qua ví Zalopay";
            }
        });

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

        datHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ptThanhToan.isEmpty()) {
                    new AlertDialog.Builder(DatHangActivity.this)
                            .setTitle("Thông báo")
                            .setMessage("Bạn phải chọn phương thức thanh toán!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    return;
                }

                referenceDH.orderBy("MaDon", Query.Direction.DESCENDING).limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    QuerySnapshot snapshot = task.getResult();
                                    String newMaDH = "DH01";  // Mã đơn hàng mặc định nếu chưa có đơn hàng

                                    if (snapshot != null && !snapshot.isEmpty()) {
                                        // Lấy mã đơn hàng cuối cùng và tăng lên 1
                                        for (QueryDocumentSnapshot doc : snapshot) {
                                            String maDH = doc.getString("MaDon");
                                            if (maDH != null && maDH.startsWith("DH")) {
                                                int lastMaDH = Integer.parseInt(maDH.substring(2));
                                                newMaDH = "DH" + String.format("%02d", lastMaDH + 1);
                                            }
                                        }
                                    }

                                    if (ptThanhToan.equals("Thanh toán qua ví Zalopay")) {
                                        new AlertDialog.Builder(DatHangActivity.this)
                                                .setTitle("Thông báo")
                                                .setMessage("Vui lòng chọn phương thức thanh toán khác!")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }

                                    else if(ptThanhToan.equals("Thanh toán qua ví VNPay")) {
                                        new AlertDialog.Builder(DatHangActivity.this)
                                                .setTitle("Thông báo")
                                                .setMessage("Vui lòng chọn phương thức thanh toán khác!")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                    else if(ptThanhToan.equals("Thanh toán qua ví Momo")) {
                                        new AlertDialog.Builder(DatHangActivity.this)
                                                .setTitle("Thông báo")
                                                .setMessage("Vui lòng chọn phương thức thanh toán khác!")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }

                                    else {
                                        themDonHang(newMaDH);
                                    }
                                } else {
                                    Log.e("FirebaseError", "Lỗi khi truy vấn mã đơn hàng: ", task.getException());
                                    Toast.makeText(DatHangActivity.this, "Lỗi khi lấy mã đơn hàng mới.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void themDonHang(String maDon) {
        CollectionReference referenceChiTietGH = firestore.collection("ChiTietGioHang");
        CollectionReference referenceDH = firestore.collection("DonHang");
        CollectionReference referenceCTDH = firestore.collection("ChiTietDonHang");

        SharedPreferences sharedPreferences = getSharedPreferences("dataMaNH", MODE_PRIVATE);
        String maNH = sharedPreferences.getString("manh","");
        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        String makhDN = sharedPreferences1.getString("Makh","");

        // Tạo bản ghi mới cho đơn hàng
        Map<String, Object> donHangMoi = new HashMap<>();
        donHangMoi.put("MaDon", maDon);
        donHangMoi.put("MaKH", makhDN);
        donHangMoi.put("MaNH", maNH);
        donHangMoi.put("KhuyenMai", KM);
        donHangMoi.put("KiemTraDonHang", false);
        donHangMoi.put("PhiShip", phiVC);
        donHangMoi.put("PhuongThucTT", ptThanhToan);
        donHangMoi.put("TrangThai", "Chờ xác nhận");
        donHangMoi.put("TrangThaiShip", "Chờ shipper xác nhận");
        donHangMoi.put("MaShipper", "");
        donHangMoi.put("TongTien", TongTien);
        donHangMoi.put("ThanhTien", ThanhTien);
        donHangMoi.put("ThoiGianDatHang", thoiGianDatHang);
        donHangMoi.put("ThoiGianGiaoHang", "");
        donHangMoi.put("TrangThaiDanhGia", "Chưa đánh giá");

        referenceDH.document(maDon).set(donHangMoi)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DatHangActivity.this, "Thêm đơn hàng thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    //  Toast.makeText(DatHangActivity.this, "Lỗi khi thêm đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        for(ChiTietGioHang items : lst){
            Map<String, Object> ctdh = new HashMap<>();
            ctdh.put("MaDon", maDon);
            ctdh.put("Anh", items.getAnh());
            ctdh.put("Gia", items.getGiaMon());
            ctdh.put("MaMon", items.getMaMon());
            ctdh.put("SoLuong", items.getSoLuong());
            ctdh.put("TenMon", items.getTenMon());
            referenceCTDH.document("CTDH_"+maDon+"_"+items.getMaMon()).set(ctdh)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DatHangActivity.this, "Thêm đơn hàng thành công", Toast.LENGTH_SHORT).show();
                        referenceChiTietGH.whereEqualTo("MaKH", makhDN)
                                .whereEqualTo("MaNH", maNH)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot snapshot1 = task.getResult();
                                            if (snapshot1 != null && !snapshot1.isEmpty()) {
                                                for (QueryDocumentSnapshot doc1 : snapshot1) {
                                                    // Xóa mục trong giỏ hàng
                                                    referenceChiTietGH.document(doc1.getId()).delete()
                                                            .addOnSuccessListener(aVoid2 -> {
                                                                Log.d("Firebase", "Đã xóa mục trong giỏ hàng thành công");
                                                            })
                                                            .addOnFailureListener(e1 -> {
                                                                Log.e("Firebase", "Lỗi khi xóa mục giỏ hàng: ", e1);
                                                            });
                                                }
                                            }
                                        } else {
                                            Log.e("FirebaseError", "Lỗi khi truy vấn giỏ hàng: ", task.getException());
                                        }
                                    }
                                });

                        firestore.collection("MonAn")
                                .whereEqualTo("MaMon", items.getMaMon())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if(task.isSuccessful()) {
                                            if (querySnapshot != null) {
                                                for (QueryDocumentSnapshot doc: querySnapshot) {
                                                    MonAn monAn = doc.toObject(MonAn.class);
                                                    double slt = monAn.getSoLuongTon();
                                                    double slb = monAn.getSoLuotBan();

                                                    doc.getReference()
                                                            .update("SoLuongTon", slt - 1,
                                                                    "SoLuotBan", slb + 1)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(slt - 1 == 0) {
                                                                        doc.getReference()
                                                                                .update("TrangThai", false)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        Log.d("Firebase", "Thay đổi trạng thái thành công");
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.d("Firebase", "Thay đổi không không thành công");
                                                                                    }
                                                                                });
                                                                    }

                                                                    Log.d("Firebase", "Thay đổi slt, slb thành công");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d("Firebase", "Thay đổi slt, slb không thành công");
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
//                        Toast.makeText(DatHangActivity.this, "Lỗi khi thêm đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        Intent intent1 = new Intent(DatHangActivity.this, DonHangActivity.class);
        startActivity(intent1);
    }

    @Override
    protected void onResume() {
        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        String makhDN = sharedPreferences1.getString("Makh","");
        super.onResume();

        firestore.collection("KhachHang")
                .whereEqualTo("MaKhachHang", makhDN)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String ten = doc.getString("TenKhach");
                                String dc = doc.getString("DiaChi");
                                String soDienThoai = doc.getString("SoDienThoai");

                                dcgh = dc;

                                if(dc.isEmpty()){
                                    diaChi.setText(ten + " | " + soDienThoai);
                                }

                                else {
                                    diaChi.setText(ten + " | " + soDienThoai + "\n\n" + dc);
                                }
                            }
                        }
                    }
                });

    }

}
