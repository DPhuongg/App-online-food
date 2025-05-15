package com.example.online_food.Adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.online_food.Activity.CTNhaHangActivity;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import com.example.online_food.Util.StringUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NhaHangSearchAdapter extends RecyclerView.Adapter<NhaHangSearchAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<MonAn> lstMonAn;
    private List<MonAn> databackup; // Bản sao dữ liệu ban đầu
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference1, referenceMenu, collectionNhaHang;
    private NhaHangSearchItemAdapter adapteritems;
    String maNhaHang1 = "";
    HashMap<String, List<MonAn>> map;

    public NhaHangSearchAdapter(Context context, List<MonAn> lstMonAn) {
        this.context = context;
        this.lstMonAn = lstMonAn;
        this.databackup = new ArrayList<>(lstMonAn); // Sao lưu dữ liệu
        firestore = FirebaseFirestore.getInstance();
        collectionReference1 = firestore.collection("MonAn");
        referenceMenu = firestore.collection("Menu");
        collectionNhaHang = firestore.collection("NhaHang");
        map = new HashMap<>();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nhahang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        ArrayList<List<MonAn>> groupedMonAnList = new ArrayList<>(map.values());
        Log.d("TKH", "ListA " + groupedMonAnList.size());
        List<MonAn> nhaHangItems = groupedMonAnList.get(position);
        Log.d("TKH", "ListB " + nhaHangItems.size());
        MonAn firstItem = nhaHangItems.get(0); // Lấy món đầu tiên trong danh sách
        String menuId = firstItem.getMaMenu(); // Lấy mã menu từ phần tử đầu tiên

        // Truy vấn bảng Menu để tìm tài liệu với mã menu phù hợp
        referenceMenu.whereEqualTo("MaMenu", menuId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot menuDocument = queryDocumentSnapshots.getDocuments().get(0);
                        maNhaHang1 = menuDocument.getString("MaNH");

                        collectionNhaHang.whereEqualTo("MaNH", maNhaHang1)
                                .get()
                                .addOnSuccessListener(queryNhaHangSnapshots -> {
                                    if (!queryNhaHangSnapshots.isEmpty()) {
                                        DocumentSnapshot nhaHangDocument = queryNhaHangSnapshots.getDocuments().get(0);
                                        String tenNhaHang = nhaHangDocument.getString("TenNhaHang");
                                        String diaChiNhaHang = nhaHangDocument.getString("DiaChi");
                                        String hinhAnh = nhaHangDocument.getString("HinhAnh");

                                        holder.txtTenNhaHang1.setText(tenNhaHang);
                                        holder.txtDiaChiNhaHang1.setText(diaChiNhaHang);

                                        // Sử dụng Glide để tải ảnh
                                        Glide.with(context)
                                                .load(hinhAnh)
                                                .placeholder(R.drawable.doraemon)
                                                .into(holder.imgHinhAnh1);
                                    } else {
                                        Log.e("FirebaseError", "Không tìm thấy nhà hàng với mã " + maNhaHang1);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirebaseError", "Lỗi khi kiểm tra thông tin nhà hàng", e);
                                    Toast.makeText(context, "Không thể kiểm tra thông tin nhà hàng", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e("FirebaseError", "Không tìm thấy menu với mã " + menuId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Lỗi khi lấy thông tin menu", e);
                    Toast.makeText(context, "Không thể lấy thông tin menu", Toast.LENGTH_SHORT).show();
                });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.dsmon.setLayoutManager(layoutManager);
        adapteritems = new NhaHangSearchItemAdapter(context, nhaHangItems);
        holder.dsmon.setAdapter(adapteritems);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CTNhaHangActivity.class);
            intent.putExtra("MaNhaHang", maNhaHang1); // Sử dụng phần tử đầu tiên để lấy ID nhà hàng
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return map != null ? map.size() : 0; // Kiểm tra nếu map null thì trả về 0
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<MonAn> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    // filteredList.addAll(databackup);
                    map.clear();
                } else {
                    String filterPattern = StringUtils.normalizeTK(constraint.toString().trim());
                    collectionReference1
                            .whereGreaterThanOrEqualTo("TenMonNonDiacritic", filterPattern)
                            .whereLessThanOrEqualTo("TenMonNonDiacritic", filterPattern + "\uf8ff")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    filteredList.clear();
                                    lstMonAn.clear();
                                    map.clear();
                                    // xoa truoc khi them

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        MonAn monAn = document.toObject(MonAn.class);
                                        filteredList.add(monAn);
                                    }
                                    lstMonAn.addAll(filteredList);
                                    Log.d("TKH", "So List " + lstMonAn.size());


                                    for (MonAn monAn : lstMonAn) {
                                        String menuId = monAn.getMaMenu(); // Lấy mã menu của món ăn

                                        // Truy vấn lên Firebase để lấy mã nhà hàng dựa trên menuId
                                        referenceMenu.whereEqualTo("MaMenu", menuId)
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                    if (!queryDocumentSnapshots.isEmpty()) {
                                                        DocumentSnapshot menuDocument = queryDocumentSnapshots.getDocuments().get(0);
                                                        String maNhaHang = menuDocument.getString("MaNH");

                                                        // Kiểm tra nếu mã nhà hàng đã tồn tại trong map
                                                        if (!map.containsKey(maNhaHang)) {
                                                            map.put(maNhaHang, new ArrayList<>()); // Nếu chưa, tạo danh sách mới cho mã nhà hàng này
                                                        }
                                                        map.get(maNhaHang).add(monAn); // Thêm món ăn vào danh sách của nhà hàng
                                                        notifyDataSetChanged();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Xử lý lỗi khi truy vấn Firebase thất bại
                                                    Log.e("FirebaseError", "Không thể lấy dữ liệu từ Firebase", e);
                                                });
                                    }
                                    Log.d("TKH", "map " + map.size());
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
                                }
                            });


                }

                results.values = lstMonAn;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                @SuppressWarnings("unchecked")
                List<MonAn> filteredList = (List<MonAn>) results.values;
                lstMonAn.clear();
                lstMonAn.addAll(filteredList);
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgHinhAnh1;
        private TextView txtTenNhaHang1;
        private TextView txtDiaChiNhaHang1;
        private RecyclerView dsmon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHinhAnh1 = itemView.findViewById(R.id.imgHinhAnh);
            txtTenNhaHang1 = itemView.findViewById(R.id.txtTenNhaHang);
            txtDiaChiNhaHang1 = itemView.findViewById(R.id.txtDiaChiNhaHang);
            dsmon = itemView.findViewById(R.id.DSMonAn);
        }
    }
}