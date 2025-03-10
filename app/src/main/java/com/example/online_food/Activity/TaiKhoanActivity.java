package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;


import java.util.UUID;


public class TaiKhoanActivity extends AppCompatActivity {
    private LinearLayout Home, DonHang, LichSu, TaiKhoan;
    private TextView erMKHT, erMKM, erXNMK, tvKhachHang;
    private Button btnTTTaiKhoan, btnDiaChi, btnDoiMatKhau, btnDangXuat;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView imageView;
    private String maKhachHang = "";
    private static final int REQUEST_CODE_UPDATE_ACCOUNT = 1;
    private EditText matKhauHienTai, matKhauMoi, xacNhanMatKhau;
    private boolean isPasswordVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tai_khoan);

        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        maKhachHang = sharedPreferences1.getString("Makh","");

        // Tìm id
        Home = findViewById(R.id.Home);
        DonHang = findViewById(R.id.DonHang);
        LichSu = findViewById(R.id.LichSu);
        TaiKhoan = findViewById(R.id.TaiKhoan);
        btnTTTaiKhoan = findViewById(R.id.btnTTTaiKhoan);
        btnDangXuat = findViewById(R.id.btnDangXuat);
        btnDiaChi = findViewById(R.id.btnDiaChi);
        btnDoiMatKhau = findViewById(R.id.btnDoiMatKhau);
        imageView = findViewById(R.id.ivHA);
        tvKhachHang = findViewById(R.id.tvKhachHang);

        // Khởi tạo Firebase Storage và Firestore

        if (!isUserLoggedIn()) {
            tvKhachHang.setText("Đăng nhập/Đăng ký");
            tvKhachHang.setClickable(true);
            btnDangXuat.setVisibility(View.GONE);
        }
        else {
            loadUserData();

            db.collection("KhachHang").document(maKhachHang)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String imageUrl = documentSnapshot.getString("HinhAnh");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    // Sử dụng Glide để hiển thị ảnh
                                    Glide.with(TaiKhoanActivity.this).load(imageUrl).into(imageView);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TaiKhoanActivity.this, "Lỗi khi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Xử lý sự kiện nhấn các nút
        tvKhachHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaiKhoanActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



        // Thêm, sửa địa chỉ
        btnDiaChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if(!isLoggedIn) {
                    startActivity(new Intent(TaiKhoanActivity.this, LoginActivity.class));
                }

//                else {
//                    Intent intent = new Intent(TaiKhoanActivity.this, DiaChiActivity.class);
//                    intent.putExtra("MaKhach", maKhachHang);
//                    startActivity(intent);
//                }
            }
        });

        // đăng xuất
        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // sửa thông tin
        btnTTTaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaiKhoanActivity.this, TTTaiKhoanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_ACCOUNT);
            }
        });

        // đổi mật khẩu
        btnDoiMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if(!isLoggedIn) {
                    startActivity(new Intent(TaiKhoanActivity.this, LoginActivity.class));
                }

                else {
                    showDialog();
                }
            }
        });

        // Chuyển tab
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaiKhoanActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        DonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if(isLoggedIn) {
                    startActivity(new Intent(TaiKhoanActivity.this, DonHangActivity.class));
                    overridePendingTransition(0, 0);
                }

                else {
                    startActivity(new Intent(TaiKhoanActivity.this, LoginActivity.class));
                }
            }
        });

        LichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                if(isLoggedIn) {
                    startActivity(new Intent(TaiKhoanActivity.this, LichSuActivity.class));
                    overridePendingTransition(0, 0);
                }

                else {
                    startActivity(new Intent(TaiKhoanActivity.this, LoginActivity.class));
                }
            }
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String tenDangNhap = preferences.getString("TenDangNhap", "");
        return !tenDangNhap.isEmpty();
    }

    // dialog đổi mật khẩu
    private void showDialog() {
        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        maKhachHang = sharedPreferences1.getString("Makh","");

        Dialog dialog;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_doi_mat_khau);

        matKhauHienTai = dialog.findViewById(R.id.MatKhauHT);
        matKhauMoi = dialog.findViewById(R.id.MatKhauMoi);
        xacNhanMatKhau = dialog.findViewById(R.id.XacNhanMK);
        Button doiMatKhau = dialog.findViewById(R.id.btnDoiMK);
        Button huy = dialog.findViewById(R.id.btnHuy);
        erMKHT = dialog.findViewById(R.id.tvErrorMKHT);
        erMKM = dialog.findViewById(R.id.tvErrorMKM);
        erXNMK = dialog.findViewById(R.id.tvErrorXNMK);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);

        matKhauHienTai.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Kiểm tra vị trí touch có phải là drawableEnd không
                if (event.getRawX() >= (matKhauHienTai.getRight() - matKhauHienTai.getCompoundDrawables()[2].getBounds().width())) {
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        matKhauHienTai.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        matKhauHienTai.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye, 0);
                        isPasswordVisible = false;
                    } else {
                        // Hiển thị mật khẩu
                        matKhauHienTai.setTransformationMethod(null);
                        matKhauHienTai.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye_active, 0);
                        isPasswordVisible = true;
                    }
                    // Đặt lại con trỏ về cuối văn bản
                    matKhauHienTai.setSelection(matKhauHienTai.getText().length());
                    return true;
                }
            }
            return false;
        });

        matKhauMoi.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Kiểm tra vị trí touch có phải là drawableEnd không
                if (event.getRawX() >= (matKhauMoi.getRight() - matKhauMoi.getCompoundDrawables()[2].getBounds().width())) {
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        matKhauMoi.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        matKhauMoi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye, 0);
                        isPasswordVisible = false;
                    } else {
                        // Hiển thị mật khẩu
                        matKhauMoi.setTransformationMethod(null);
                        matKhauMoi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye_active, 0);
                        isPasswordVisible = true;
                    }
                    // Đặt lại con trỏ về cuối văn bản
                    matKhauMoi.setSelection(matKhauMoi.getText().length());
                    return true;
                }
            }
            return false;
        });

        xacNhanMatKhau.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Kiểm tra vị trí touch có phải là drawableEnd không
                if (event.getRawX() >= (xacNhanMatKhau.getRight() - xacNhanMatKhau.getCompoundDrawables()[2].getBounds().width())) {
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        xacNhanMatKhau.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        xacNhanMatKhau.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye, 0);
                        isPasswordVisible = false;
                    } else {
                        // Hiển thị mật khẩu
                        xacNhanMatKhau.setTransformationMethod(null);
                        xacNhanMatKhau.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye_active, 0);
                        isPasswordVisible = true;
                    }
                    // Đặt lại con trỏ về cuối văn bản
                    xacNhanMatKhau.setSelection(xacNhanMatKhau.getText().length());
                    return true;
                }
            }
            return false;
        });

        doiMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mkht = matKhauHienTai.getText().toString();
                String mkm = matKhauMoi.getText().toString();
                String xnmk = xacNhanMatKhau.getText().toString();

                if(validInput(mkht, mkm, xnmk)) {
                    if(mkm.equals(xnmk)) {

                        db.collection("KhachHang")
                                .whereEqualTo("MaKhachHang", maKhachHang)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (task.isSuccessful()) {
                                            for(QueryDocumentSnapshot doc: querySnapshot) {
                                                String mk = doc.getString("MatKhau");

                                                if(!mk.equals(mkht)) {
                                                    matKhauHienTai.setBackground(ContextCompat.getDrawable(TaiKhoanActivity.this, R.drawable.error_background));
                                                    erMKHT.setVisibility(View.VISIBLE);
                                                    erMKHT.setText("Mật khẩu không chính xác");
                                                    return;
                                                }

                                                else if(!validPassword(mkm)) {
                                                    matKhauMoi.setBackground(ContextCompat.getDrawable(TaiKhoanActivity.this, R.drawable.error_background));
                                                    erMKM.setVisibility(View.VISIBLE);
                                                    erMKM.setText("Mật khẩu không đúng định dạng:\n\n- Có ít nhất 8 ký tự\n\n- Bắt đầu bao chữ hoa\n\n- Có ít nhất 1 chữ thường và số");
                                                    return;
                                                }

                                                doc.getReference().update("MatKhau", xnmk)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(TaiKhoanActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                    }
                    else {
                        xacNhanMatKhau.setBackground(ContextCompat.getDrawable(TaiKhoanActivity.this, R.drawable.error_background));
                        erXNMK.setVisibility(View.VISIBLE);
                        erXNMK.setText("Xác nhận mật khẩu không trùng khớp");
                    }
                }
            }
        });

        huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private boolean validInput(String mkht, String mkm, String xnmk) {
        boolean isValid = true;

        if(mkht.isEmpty()) {
            matKhauHienTai.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            erMKHT.setVisibility(View.VISIBLE);
            erMKHT.setText("Hãy nhập đủ thông tin");
            isValid = false;
        }
        else {
            matKhauHienTai.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
            erMKHT.setVisibility(View.GONE);
        }

        if(mkm.isEmpty()) {
            matKhauMoi.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            isValid = false;
        }
        else {
            matKhauMoi.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
        }

        if(xnmk.isEmpty()) {
            xacNhanMatKhau.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            isValid = false;
        }
        else {
            xacNhanMatKhau.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
        }

        return isValid;
    }

    private boolean validPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        if (!Character.isUpperCase(password.charAt(0))) {
            return false;
        }

        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasLowerCase && hasDigit) {
                break;
            }
        }

        return hasLowerCase && hasDigit;
    }

    private void loadUserData() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String tenDangNhap = preferences.getString("TenDangNhap", "");

        CollectionReference referenceKH = db.collection("KhachHang");
        referenceKH.whereEqualTo("TenDangNhap", tenDangNhap)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot != null) {
                                for(QueryDocumentSnapshot doc: snapshot) {
                                    String Hoten = doc.getString("TenKhach");
                                    tvKhachHang.setText(Hoten);
                                    tvKhachHang.setClickable(false);
                                    btnDangXuat.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(TaiKhoanActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TaiKhoanActivity.this, "Lỗi khi lấy dữ liệu người dùng: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(TaiKhoanActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        tvKhachHang.setText("Đăng nhập/ Đăng ký");
        tvKhachHang.setClickable(true);
        btnDangXuat.setVisibility(View.GONE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_ACCOUNT && resultCode == RESULT_OK) {
            String imageUrl1 = data.getStringExtra("imageUrl");
            // Kiểm tra giá trị của imageUrl
            Log.d("TaiKhoanActivity", "Image URL received: " + imageUrl1);

            if (imageUrl1 != null) {
                // Load lại ảnh vào ImageView bằng Glide
                Glide.with(TaiKhoanActivity.this).load(imageUrl1).into(imageView);

                // Kiểm tra lại URL đã được set cho ImageView chưa
                Log.d("TaiKhoanActivity", "ImageView updated with URL: " + imageUrl1);
            } else {
                Log.d("TaiKhoanActivity", "Image URL is null");
            }
        }
    }
}