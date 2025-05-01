package com.example.online_food.Activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import com.example.online_food.Util.CircleAnimationUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CTMonAnActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    ImageView ivTroVe, ivAnhMon, ivCart, ivGiamSL, ivTangSL, ivAnhMon1;
    TextView tvTenMonAn, tvMoTa, tvLuotBan, tvGia, tvSoLuong, tvSLT, tvDGBL;
    Button btnTGH;
    int soluong = 1;
    String ten = "";
    String anh = "";
    Object giaTien = "";
    String maMon = "";
    String makhDN = "";
    String maNH = "";
    double slt = 0;
//    MADanhGiaAdapter maDanhGiaAdapter;
    RecyclerView viewDanhGia;
//    float diemTrungBinh = 0, tongDiem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_chi_tiet_mon_an);

        // Lấy collection trong firebase
        firestore = FirebaseFirestore.getInstance();
        CollectionReference referenceMA = firestore.collection("MonAn");
        CollectionReference referenceDG = firestore.collection("DanhGia");

        // Tìm id
        ivTroVe = findViewById(R.id.ivTroVe);
        ivAnhMon = findViewById(R.id.ivAnhMA);
        ivTangSL = findViewById(R.id.TangSL);
        ivCart = findViewById(R.id.ivShopping_cart);
        ivGiamSL = findViewById(R.id.GiamSL);
        tvTenMonAn = findViewById(R.id.tvTenMA);
        tvMoTa = findViewById(R.id.tvMT);
        tvLuotBan = findViewById(R.id.tvSLB);
        tvGia = findViewById(R.id.tvGiaMon);
        btnTGH = findViewById(R.id.btnAddToCart);
        tvSoLuong = findViewById(R.id.SL);
        ivAnhMon1 = findViewById(R.id.ivAnhMA1);
        viewDanhGia = findViewById(R.id.viewDanhGia);
        tvSLT = findViewById(R.id.tvSLT);
        tvDGBL = findViewById(R.id.tvDGBL);

        // Tăng giảm số lượng món ăn
        ivTangSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soluong += 1;
                tvSoLuong.setText(String.valueOf(soluong));
            }
        });

        ivGiamSL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soluong <= 1) {
                    soluong = 1;
                } else{
                    soluong -= 1;
                }
                tvSoLuong.setText(String.valueOf(soluong));
            }
        });

        // Đến giỏ hàng
        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isUserLoggedIn()){
                    startActivity(new Intent(CTMonAnActivity.this, DangNhapVaDangKyActivity.class));
                }else{
                    startActivity(new Intent(CTMonAnActivity.this,GioHangActivity.class));
                }
            }
        });

        // sự kiện trở về
        ivTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Thông tin món ăn
        maMon = getIntent().getStringExtra("MaMon");
        SharedPreferences sharedPreferences = getSharedPreferences("dataMaNH", MODE_PRIVATE);
        maNH = sharedPreferences.getString("manh","");

        referenceMA.whereEqualTo("MaMon", maMon).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot != null) {
                                MonAn monAn = new MonAn();
                                for(QueryDocumentSnapshot doc: snapshot) {
                                    ten = doc.getString("TenMon");
                                    anh = doc.getString("HinhAnh");
                                    String moTa = doc.getString("MoTa");

                                    Object soLuotBan = doc.get("SoLuotBan");
                                    giaTien = doc.get("GiaMon");

                                    Object soLuongTon = doc.get("SoLuongTon");

                                    if (soLuongTon instanceof Number) {
                                        slt = ((Number) soLuongTon).doubleValue();
                                    } else {
                                        try {
                                            slt = Double.parseDouble(String.valueOf(soLuongTon));
                                        } catch (NumberFormatException e) {
                                            Log.w("MainActivity", "Invalid quantity format: " + soLuongTon);
                                            continue;
                                        }
                                    }

                                    double lb = 0, gia = 0;

                                    if (soLuotBan instanceof Number) {
                                        lb = ((Number) soLuotBan).doubleValue();
                                    } else {
                                        try {
                                            lb = Double.parseDouble(String.valueOf(soLuotBan));
                                        } catch (NumberFormatException e) {
                                            Log.w("MainActivity", "Invalid quantity format: " + soLuotBan);
                                            continue;
                                        }
                                    }

                                    if (giaTien instanceof Number) {
                                        gia = ((Number) giaTien).doubleValue();
                                    } else {
                                        try {
                                            gia = Double.parseDouble(String.valueOf(giaTien));
                                        } catch (NumberFormatException e) {
                                            Log.w("MainActivity", "Invalid quantity format: " + giaTien);
                                            continue;
                                        }
                                    }

                                    NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                                    String giaFormatted = format.format(gia);
                                    String sltFormatted = format.format(slt);
                                    String luotBanformatted = formatLuotBan(lb);

                                    // set thông tin
                                    Glide.with(CTMonAnActivity.this).load(anh).into(ivAnhMon);
                                    Glide.with(CTMonAnActivity.this).load(anh).into(ivAnhMon1);
                                    tvTenMonAn.setText(ten);
                                    tvMoTa.setText(moTa);
                                    tvGia.setText(giaFormatted + " đ");
                                    tvLuotBan.setText(luotBanformatted);
                                    tvSLT.setText("Còn lại: " + sltFormatted);
                                }

                            }
                        }else {
                            Log.d("MainActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });



        // ĐÁNH GIÁ




        //Kiểm tra đăng nhập, thêm món ăn vào giỏ hàng
        CollectionReference referenceCTGH = firestore.collection("ChiTietGioHang");
        btnTGH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                boolean isLoggedIn =  sharedPreferences.getBoolean("isLoggedIn", false);
                makhDN = sharedPreferences.getString("Makh", "");

                if(isLoggedIn) {
                    if(slt == 0) {
                        new AlertDialog.Builder(CTMonAnActivity.this)
                                .setTitle("Thông báo")
                                .setMessage("Hết hàng!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setCancelable(false)
                                .show();
                        return;
                    }
                    else if((int) slt < soluong){
                        new AlertDialog.Builder(CTMonAnActivity.this)
                                .setTitle("Thông báo")
                                .setMessage("Số lượng bạn yêu cầu lớn hơn số lượng món còn lại!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setCancelable(false)
                                .show();
                        return;
                    }

                    makeFlyAnimation(ivAnhMon);

                    // Truy vấn để kiểm tra sự tồn tại của sản phẩm trong giỏ hàng
                    referenceCTGH.whereEqualTo("MaKH", makhDN)
                            .whereEqualTo("MaMon", maMon)
                            .whereEqualTo("MaNH", maNH)  // nếu có MaNhaHang
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {
                                            // Sản phẩm đã tồn tại, cập nhật số lượng
                                            for (QueryDocumentSnapshot document : querySnapshot) {
                                                String documentId = document.getId();
                                                int currentQuantity = document.getLong("SoLuong").intValue();

                                                // Cộng số lượng mới với số lượng cũ
                                                referenceCTGH.document(documentId)
                                                        .update("SoLuong", currentQuantity + soluong)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(CTMonAnActivity.this, "Update success!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(CTMonAnActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        } else {
                                            // Sản phẩm chưa có, thêm mới
                                            Map<String, Object> item = new HashMap<>();
                                            item.put("MaKH", makhDN);
                                            item.put("MaMon", maMon);
                                            item.put("MaNH", maNH);  // nếu có MaNhaHang
                                            item.put("SoLuong", soluong);
                                            item.put("TenMon", ten);
                                            item.put("Anh", anh);
                                            item.put("GiaMon",giaTien);

                                            referenceCTGH.document(makhDN +"_"+maNH+"_"+maMon).set(item)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Thêm mới thành công
                                                            Toast.makeText(getApplicationContext(), "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Thêm mới thất bại
                                                            Toast.makeText(getApplicationContext(), "Thêm mới thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Xử lý lỗi truy vấn
                                        Toast.makeText(CTMonAnActivity.this, "Error checking cart!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    startActivity(new Intent(CTMonAnActivity.this, DangNhapVaDangKyActivity.class));
                }
            }
        });

    }

    //check login
    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String tenDangNhap = preferences.getString("TenDangNhap", "");
        return !tenDangNhap.isEmpty();
    }


    // Thêm hiệu ứng khi click thêm giỏ hàng
    private void makeFlyAnimation(ImageView targetView){
        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(500).setDestView(ivCart).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                addItemToCart();
//                Toast.makeText(MainActivity.this, "Continue Shopping...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();
    }

    //format lượt bán
    @SuppressLint("DefaultLocale")
    private String formatLuotBan(double luotBan) {
        if(luotBan < 1000) {
            return String.format("%.0f" + " lượt bán", luotBan);
        } else if (luotBan % 1000 == 0) {
            int result =(int) (luotBan / 1000);
            return result + "k lượt bán";
        } else {
            double result = luotBan / 1000;
            return String.format("%.1f" + "k lượt bán", result);
        }
    }


    //format Bình luận
//    @SuppressLint("DefaultLocale")
//    private String formatBinhLuan(int luotBL) {
//        if (luotBL < 1000) {
//            return String.format("%d" , luotBL);
//        } else {
//            return String.format("999+");
//        }
//    }
}