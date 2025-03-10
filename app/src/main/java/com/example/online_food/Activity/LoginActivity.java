package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.online_food.R;
import com.example.online_food.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    TextView DangNhap, DangKy, tvQuenMK;
    EditText etTenDangNhap, etMatKhau, etTDN, etMK, etHoTen, etSDT;
    LinearLayout layout_DN, layout_DK;
    Button btnDNDK;
    ImageView back;
    FirebaseFirestore firestore;
    TextView tvErrorMatKhau, tvErrorTenDangNhap, tvErrorHoTen, tvErrorSDT, tvErrorTDN, tvErrorMK;
    boolean isPasswordVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        firestore = FirebaseFirestore.getInstance();
        CollectionReference referenceKH = firestore.collection("KhachHang");

        // Tìm id
        DangNhap = findViewById(R.id.DangNhap);
        DangKy = findViewById(R.id.DangKy);
        etTenDangNhap = findViewById(R.id.etTenDangNhap);
        etMatKhau = findViewById(R.id.etMatKhau);
        etTDN = findViewById(R.id.etTDN);
        etMK = findViewById(R.id.etMK);
        etHoTen = findViewById(R.id.etHoTen);
        etSDT = findViewById(R.id.etSDT);
        layout_DK = findViewById(R.id.layout_DangKy);
        layout_DN = findViewById(R.id.layout_DangNhap);
        btnDNDK = findViewById(R.id.btnDNDK);
        back = findViewById(R.id.back);
        tvQuenMK = findViewById(R.id.tvQuenMatKhau);
        tvErrorTenDangNhap = findViewById(R.id.tvErrorTenDangNhap);
        tvErrorMatKhau = findViewById(R.id.tvErrorMatKhau);
        tvErrorHoTen = findViewById(R.id.tvErrorHoTen);
        tvErrorSDT = findViewById(R.id.tvErrorSDT);
        tvErrorTDN = findViewById(R.id.tvErrorTDN);
        tvErrorMK = findViewById(R.id.tvErrorMK);

        // Ẩn/Hiện mật khẩu cho etMatKhau
        etMatKhau.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Kiểm tra vị trí touch có phải là drawableEnd không
                if (event.getRawX() >= (etMatKhau.getRight() - etMatKhau.getCompoundDrawables()[2].getBounds().width())) {
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        etMatKhau.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etMatKhau.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye, 0);
                        isPasswordVisible = false;
                    } else {
                        // Hiển thị mật khẩu
                        etMatKhau.setTransformationMethod(null);
                        etMatKhau.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye_active, 0);
                        isPasswordVisible = true;
                    }
                    // Đặt lại con trỏ về cuối văn bản
                    etMatKhau.setSelection(etMatKhau.length());
                    return true;
                }
            }
            return false;
        });

        // Ẩn/Hiện mật khẩu cho etMK
        etMK.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Kiểm tra vị trí touch có phải là drawableEnd không
                if (event.getRawX() >= (etMK.getRight() - etMK.getCompoundDrawables()[2].getBounds().width())) {
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        etMK.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etMK.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye, 0);
                        isPasswordVisible = false;
                    } else {
                        // Hiển thị mật khẩu
                        etMK.setTransformationMethod(null);
                        etMK.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.eye_active, 0);
                        isPasswordVisible = true;
                    }
                    // Đặt lại con trỏ về cuối văn bản
                    etMK.setSelection(etMK.length());
                    return true;
                }
            }
            return false;
        });


        // hiện layout Đăng ký
        DangKy.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                DangKy.setBackgroundResource(R.drawable.background_button_2);
                DangKy.setTextColor(getResources().getColor(R.color.white, null));
                layout_DK.setVisibility(View.VISIBLE);
                btnDNDK.setText("Đăng ký");

                DangNhap.setBackground(null);
                DangNhap.setTextColor(getResources().getColor(R.color.maincolor, null));
                layout_DN.setVisibility(View.GONE);
            }
        });

        // hiện layout Đăng nhập
        DangNhap.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                DangNhap.setBackgroundResource(R.drawable.background_button_2);
                DangNhap.setTextColor(getResources().getColor(R.color.white, null));
                layout_DN.setVisibility(View.VISIBLE);
                btnDNDK.setText("Đăng nhập");

                DangKy.setBackground(null);
                DangKy.setTextColor(getResources().getColor(R.color.maincolor, null));
                layout_DK.setVisibility(View.GONE);
            }
        });

        // Trở về
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Quên mật khẩu
        tvQuenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // đăng nhập và đăng ký
        btnDNDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đăng nhập
                if (layout_DN.getVisibility() == View.VISIBLE) {
                    String tenDangNhap = etTenDangNhap.getText().toString();
                    String matKhau = etMatKhau.getText().toString();

                    if (validateInput(tenDangNhap, matKhau)) {
                        referenceKH.whereEqualTo("TenDangNhap", tenDangNhap)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot snapshot = task.getResult();
                                            if (snapshot != null && !snapshot.isEmpty()) {
                                                for (QueryDocumentSnapshot doc : snapshot) {
                                                    String mkHashed = doc.getString("MatKhau");
                                                    String makh = doc.getString("MaKhachHang");

                                                    if (mkHashed != null && BCrypt.checkpw(matKhau, mkHashed)) {
                                                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("TenDangNhap", tenDangNhap);
                                                        editor.putString("Makh", makh);
                                                        editor.putBoolean("isLoggedIn", true);
                                                        editor.apply();

                                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(LoginActivity.this, TaiKhoanActivity.class));
                                                    } else {
                                                        etMatKhau.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.error_background));
                                                        tvErrorMatKhau.setText("Mật khẩu không chính xác");
                                                        tvErrorMatKhau.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            } else {
                                                etTenDangNhap.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.error_background));
                                                tvErrorTenDangNhap.setText("Tên đăng nhập không tồn tại");
                                                tvErrorTenDangNhap.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Lỗi khi đăng nhập: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }


                // Đăng ký
                if (layout_DK.getVisibility() == View.VISIBLE) {
                    String tenDN = etTDN.getText().toString();
                    String mk = etMK.getText().toString();
                    String soDienThoai = etSDT.getText().toString();
                    String hoTen = etHoTen.getText().toString();
                    String hinhAnh = "";
                    String diaChi = "";

                    if (validateInputDK(tenDN, mk, hoTen, soDienThoai)) {
                        referenceKH.orderBy("MaKhachHang", Query.Direction.DESCENDING).limit(1)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot snapshot = task.getResult();
                                            String newMaKH = "KH01";
                                            if (!snapshot.isEmpty()) {
                                                for (QueryDocumentSnapshot doc : snapshot) {
                                                    String maKH = doc.getString("MaKhachHang");
                                                    if (maKH != null && maKH.startsWith("KH")) {
                                                        int lastMaKH = Integer.parseInt(maKH.substring(2));
                                                        newMaKH = "KH" + String.format("%02d", lastMaKH + 1);
                                                    }
                                                }
                                            }

                                            String finalNewMaKH = newMaKH;
                                            checkTenDangNhap(tenDN, new OnCheckCompleteListener() {
                                                @Override
                                                public void onComplete(boolean exists) {
                                                    if (exists) {
                                                        etTDN.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.error_background));
                                                        tvErrorTDN.setText("Tên đăng nhập đã tồn tại");
                                                        tvErrorTDN.setVisibility(View.VISIBLE);
                                                    } else {
                                                        if (!validPassword(mk)) {
                                                            etMK.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.error_background));
                                                            tvErrorMK.setText("Mật khẩu không đúng định dạng:\n\n- Có ít nhất 8 ký tự\n\n- Bắt đầu bằng chữ hoa\n\n- Có ít nhất 1 chữ thường và số");
                                                            tvErrorMK.setVisibility(View.VISIBLE);
                                                        } else {
                                                            // Mã hóa mật khẩu trước khi lưu
                                                            String hashedPassword = BCrypt.hashpw(mk, BCrypt.gensalt());

                                                            Map<String, Object> khachHangMoi = new HashMap<>();
                                                            khachHangMoi.put("MaKhachHang", finalNewMaKH);
                                                            khachHangMoi.put("TenKhach", hoTen);
                                                            khachHangMoi.put("SoDienThoai", soDienThoai);
                                                            khachHangMoi.put("TenDangNhap", tenDN);
                                                            khachHangMoi.put("MatKhau", hashedPassword);
                                                            khachHangMoi.put("DiaChi", diaChi);
                                                            khachHangMoi.put("HinhAnh", hinhAnh);

                                                            referenceKH.document(finalNewMaKH).set(khachHangMoi)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Toast.makeText(LoginActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                                                        // Chuyển sang giao diện đăng nhập
                                                                        DangNhap.setBackgroundResource(R.drawable.background_button_2);
                                                                        DangNhap.setTextColor(getResources().getColor(R.color.white, null));
                                                                        layout_DN.setVisibility(View.VISIBLE);
                                                                        btnDNDK.setText("Đăng nhập");

                                                                        DangKy.setBackground(null);
                                                                        DangKy.setTextColor(getResources().getColor(R.color.maincolor, null));
                                                                        layout_DK.setVisibility(View.GONE);
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(LoginActivity.this, "Lỗi khi đăng ký: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                }


            }
        });

    }

    private boolean validateInputDK(String tenDN, String matKhau, String hoTen, String soDienThoai) {
        boolean isValid = true;

        if (tenDN.isEmpty()) {
            etTDN.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            tvErrorTDN.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            etTDN.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
            tvErrorTDN.setVisibility(View.GONE);
        }

        if (matKhau.isEmpty()) {
            etMK.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            tvErrorMK.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            etMK.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
            tvErrorMK.setVisibility(View.GONE);
        }

        if (hoTen.isEmpty()) {
            etHoTen.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            tvErrorHoTen.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            etHoTen.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
            tvErrorHoTen.setVisibility(View.GONE);
        }

        if (soDienThoai.isEmpty()) {
            etSDT.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            tvErrorSDT.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            etSDT.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
            tvErrorSDT.setVisibility(View.GONE);
        }

        return isValid;
    }

    private boolean validateInput(String tenDN, String matKhau) {
        boolean isValid = true;

        if (tenDN.isEmpty()) {
            etTenDangNhap.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            tvErrorTenDangNhap.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            etTenDangNhap.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
            tvErrorTenDangNhap.setVisibility(View.GONE);
        }

        if (matKhau.isEmpty()) {
            etMatKhau.setBackground(ContextCompat.getDrawable(this, R.drawable.error_background));
            tvErrorMatKhau.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            etMatKhau.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background_2));
            tvErrorMatKhau.setVisibility(View.GONE);        }

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

    private boolean validSoDienThoai(String soDienThoai) {
        String regex = "^(03|05|07|08|09)\\d{8}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(soDienThoai);

        return matcher.matches();
    }

    private boolean validHoTen(String hoTen) {
        String regex = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯưẠ-ỹ\\s]{2,50}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(hoTen);

        return matcher.matches();
    }

    private void checkTenDangNhap(String tenDangNhap, OnCheckCompleteListener listener) {
        firestore = FirebaseFirestore.getInstance();
        CollectionReference referenceKH = firestore.collection("KhachHang");

        referenceKH.whereEqualTo("TenDangNhap", tenDangNhap)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            boolean exists = snapshot != null && !snapshot.isEmpty();
                            listener.onComplete(exists);
                        } else {
                            Toast.makeText(LoginActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            listener.onComplete(false);
                        }
                    }
                });
    }

    public interface OnCheckCompleteListener {
        void onComplete(boolean exists);
    }
}