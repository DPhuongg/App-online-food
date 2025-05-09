package com.example.online_food.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.online_food.Adapter.MonAnSearchAdapter;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import java.util.ArrayList;

public class TimKiemMonAn extends AppCompatActivity {

    private ImageView ivback, ivCancel;
    private EditText etTimKiem;
    private RecyclerView lstMonAnItem;
    private MonAnSearchAdapter adapter;
    private ArrayList<MonAn> lstMonAn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_kiem_mon_an);

        ivback = findViewById(R.id.ivback);
        ivCancel = findViewById(R.id.ivCancel);
        etTimKiem = findViewById(R.id.etSearch);
        lstMonAnItem = findViewById(R.id.lstMonAnItem);

        lstMonAn = new ArrayList<>();
        Log.d("Timkiemnhahang123","Số món TKmon "+ lstMonAn.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lstMonAnItem.setLayoutManager(layoutManager);
        adapter = new MonAnSearchAdapter(this, lstMonAn);
        lstMonAnItem.setAdapter(adapter);

        etTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivback.setOnClickListener(v -> finish());
        ivCancel.setOnClickListener(v -> etTimKiem.setText(""));
    }
}
