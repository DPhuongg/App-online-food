package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class TTTaiKhoanActivity extends AppCompatActivity {

    private ImageView ivTroVe, imageView;
    private EditText etHoTenKH, etTenDN, etSoDienThoai;
    private Button btnLuu;
    private FirebaseFirestore firestore;
    private String maKhachHang = ""; // Cập nhật mã khách hàng phù hợp
    private Uri imageUri;
    String imageUrl1;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                Glide.with(getApplicationContext()).load(imageUri).into(imageView);
                // Chỉ cần lưu URL vào Firestore, không cần tải lên Storage
                saveImageUrlToFirestore(imageUri.toString());
            } else {
                Toast.makeText(TTTaiKhoanActivity.this, "Vui lòng chọn một ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tttai_khoan);

        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        maKhachHang = sharedPreferences1.getString("Makh","");

        // Tìm id
        ivTroVe = findViewById(R.id.ivTroVe);
        etHoTenKH = findViewById(R.id.etHoTenKH);
        etTenDN = findViewById(R.id.etTenDN);
        etSoDienThoai = findViewById(R.id.etSoDienThoai);
        btnLuu = findViewById(R.id.btnLuu);
        imageView = findViewById(R.id.ivAnhKH);

        firestore.collection("KhachHang").document(maKhachHang)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("HinhAnh");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                // Sử dụng Glide để hiển thị ảnh
                                Glide.with(TTTaiKhoanActivity.this).load(imageUrl).into(imageView);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TTTaiKhoanActivity.this, "Lỗi khi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Xử lý sự kiện chọn ảnh
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        // Xử lý các sự kiện
        ivTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUrl1 != null && !imageUrl1.isEmpty()) {
                    Intent resultIntent = new Intent();
                    Log.d("ImageUpload", "Back imageUrl1 URL: " + imageUrl1);
                    resultIntent.putExtra("imageUrl", imageUrl1);
                    setResult(RESULT_OK, resultIntent);
                } else {
                    Toast.makeText(TTTaiKhoanActivity.this, "URL ảnh không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomerData();
            }
        });
    }

    // Hàm lưu URL ảnh vào Firestore
    private void saveImageUrlToFirestore(String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("HinhAnh", imageUrl);

        firestore.collection("KhachHang").document(maKhachHang)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(TTTaiKhoanActivity.this, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
                        imageUrl1 = imageUrl; // Lưu URL vào biến để sử dụng sau này
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TTTaiKhoanActivity.this, "Lỗi cập nhật ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveCustomerData() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String tenDangNhap = preferences.getString("TenDangNhap", "");

        if (tenDangNhap.isEmpty()) {
            Toast.makeText(TTTaiKhoanActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        String hoTenMoi = etHoTenKH.getText().toString().trim();
        String soDienThoaiMoi = etSoDienThoai.getText().toString().trim();

        if (hoTenMoi.isEmpty() || soDienThoaiMoi.isEmpty()) {
            Toast.makeText(TTTaiKhoanActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference referenceKH = firestore.collection("KhachHang");
        referenceKH.whereEqualTo("TenDangNhap", tenDangNhap)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference docRef = document.getReference();

                                docRef.update("TenKhach", hoTenMoi, "SoDienThoai", soDienThoaiMoi)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(TTTaiKhoanActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(TTTaiKhoanActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(TTTaiKhoanActivity.this, "Lỗi khi tìm thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
