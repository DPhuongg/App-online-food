package com.example.online_food.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.online_food.Class.ApiService;
import com.example.online_food.Class.District;
import com.example.online_food.Class.Province;
import com.example.online_food.Class.Ward;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DiaChiActivity extends AppCompatActivity {

    ImageView back;
    EditText hoTen, soDienThoai, soNha;
    AutoCompleteTextView tinh, huyen, phuongXa;
    Button hoanThanh;
    List<Province> provincesList = new ArrayList<>();
    ArrayAdapter<String> provinceAdapter, districtAdapter, wardAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String dc = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dia_chi);

        // Tìm id
        back = findViewById(R.id.Back);
        hoTen = findViewById(R.id.HoTen);
        soDienThoai = findViewById(R.id.SoDienThoai);
        soNha = findViewById(R.id.DiaChi);
        tinh = findViewById(R.id.spinnerTinh);
        huyen = findViewById(R.id.spinnerHuyen);
        phuongXa = findViewById(R.id.spinnerXa);
        hoanThanh = findViewById(R.id.btnHoanThanh);

        // Gọi API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://provinces.open-api.vn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getProvinces().enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, retrofit2.Response<List<Province>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provincesList = response.body();
                    loadProvinces();
                } else {
                    Toast.makeText(DiaChiActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                Toast.makeText(DiaChiActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Thông tin giao hàng
        String mak = getIntent().getStringExtra("MaKhach");

        db.collection("KhachHang")
                .whereEqualTo("MaKhachHang", mak)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot doc: querySnapshot) {
                                String hoten = doc.getString("TenKhach");
                                String sodt = doc.getString("SoDienThoai");
                                String diaChi = doc.getString("DiaChi");

                                hoTen.setText(hoten);
                                soDienThoai.setText(sodt);

                                if (diaChi != null) {
                                    String[] parts = diaChi.split(","); // Tách bằng dấu phẩy
                                    if (parts.length == 4) {
                                        soNha.setText(parts[0].trim()); // Số nhà
                                        phuongXa.setText(parts[1].trim()); // Xã/phường
                                        huyen.setText(parts[2].trim()); // Huyện
                                        tinh.setText(parts[3].trim()); // Tỉnh
                                    }
                                }
                            }
                        }
                    }
                });

        // Lưu thông tin
        hoanThanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ht = hoTen.getText().toString();
                String sdt = soDienThoai.getText().toString();
                String sn = soNha.getText().toString();
                String Tinh = tinh.getText().toString();
                String Huyen = huyen.getText().toString();
                String Xa = phuongXa.getText().toString();

                dc = sn + ", " + Xa + ", " + Huyen + ", " + Tinh;

                db.collection("KhachHang")
                        .whereEqualTo("MaKhachHang", mak)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if(task.isSuccessful()) {
                                    for(QueryDocumentSnapshot doc: querySnapshot) {
                                        doc.getReference()
                                            .update(
                                                    "TenKhach", ht,
                                                    "SoDienThoai", sdt,
                                                    "DiaChi", dc)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(DiaChiActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void loadProvinces() {
        List<String> provinceNames = new ArrayList<>();
        for (Province province : provincesList) {
            provinceNames.add(province.name);
        }

        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinceNames);
        tinh.setAdapter(provinceAdapter);

        // Xử lý chọn tỉnh
        tinh.setOnItemClickListener((parent, view, position, id) -> {
            loadDistricts(provincesList.get(position).districts);
            huyen.setText(""); // Reset huyện
            phuongXa.setText(""); // Reset xã/phường
        });
    }

    private void loadDistricts(List<District> districts) {
        List<String> districtNames = new ArrayList<>();
        for (District district : districts) {
            districtNames.add(district.name);
        }

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, districtNames);
        huyen.setAdapter(districtAdapter);

        // Xử lý chọn huyện
        huyen.setOnItemClickListener((parent, view, position, id) -> {
            loadWards(districts.get(position).wards);
            phuongXa.setText(""); // Reset xã/phường
        });
    }

    private void loadWards(List<Ward> wards) {
        List<String> wardNames = new ArrayList<>();
        for (Ward ward : wards) {
            wardNames.add(ward.name);
        }

        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, wardNames);
        phuongXa.setAdapter(wardAdapter);

    }

}