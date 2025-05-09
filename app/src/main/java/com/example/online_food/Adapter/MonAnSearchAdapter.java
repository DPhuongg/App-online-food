package com.example.online_food.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.online_food.Activity.CTMonAnActivity;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import com.example.online_food.Util.StringUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.util.Log;

public class MonAnSearchAdapter extends RecyclerView.Adapter<MonAnSearchAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<MonAn> lstMonAn;
    private List<MonAn> databackup;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference, collectionReferenceMN;
    private String maNH;

    public MonAnSearchAdapter(Context context, List<MonAn> lstMonAn) {
        this.context = context;
        this.lstMonAn = lstMonAn;
        this.databackup = new ArrayList<>(lstMonAn);
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("MonAn");
        collectionReferenceMN = firestore.collection("Menu");
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserMaNH", Context.MODE_PRIVATE);
        maNH = sharedPreferences.getString("MaNhaHang", ""); // Lấy mã nhà hàng từ SharedPreferences
        Log.d("MonAnSearchAdapter", "Mã nhà hàng: " + maNH);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonAn monAn = lstMonAn.get(position);
        holder.txtTenMonAn.setText(monAn.getTenMon());


        double giaMon = monAn.getGiaMon();
        NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
        String giaFormatted = format.format(giaMon);
        holder.txtGia.setText("Giá: " + giaFormatted + " VNĐ");

        double luotBan = monAn.getSoLuotBan();
        String luotBanformatted = formatLuotBan(luotBan);
        holder.txtSoLuotBan.setText(luotBanformatted );

        Glide.with(context)
                .load(monAn.getHinhAnh())
                .placeholder(R.drawable.doraemon)
                .into(holder.imgHinhAnh);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CTMonAnActivity.class);
            intent.putExtra("MaMon", monAn.getMaMon());

            String maMonAn = monAn.getMaMon();
            if (maMonAn != null && !maMonAn.isEmpty()) {
                Toast.makeText(context, maMonAn, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Mã món ăn không khả dụng", Toast.LENGTH_SHORT).show();
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lstMonAn.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<MonAn> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(databackup);
                } else {
//                    String filterPattern = constraint.toString().trim();
                    String filterPattern = StringUtils.removeDiacritics(constraint.toString().trim());
                    // Lấy danh sách các mã menu theo mã nhà hàng
                    collectionReferenceMN
                            .whereEqualTo("MaNH", maNH) // So sánh mã nhà hàng trong Firestore
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Lấy mã menu từ kết quả tìm kiếm
                                        String maMenu = document.getString("MaMenu");

                                        // Tìm món ăn dựa trên mã menu và điều kiện tên món ăn
                                        collectionReference
                                                .whereEqualTo("MaMenu", maMenu)
                                                .whereGreaterThanOrEqualTo("TenMonNonDiacritic", filterPattern)
                                                .whereLessThanOrEqualTo("TenMonNonDiacritic", filterPattern + "\uf8ff")
                                                .get()
                                                .addOnCompleteListener(innerTask -> {
                                                    if (innerTask.isSuccessful()) {
                                                        for (QueryDocumentSnapshot innerDocument : innerTask.getResult()) {
                                                            MonAn monAn = innerDocument.toObject(MonAn.class);
                                                            filteredList.add(monAn);
                                                        }
                                                        lstMonAn.clear();
                                                        lstMonAn.addAll(filteredList);
                                                        notifyDataSetChanged();
                                                    } else {
                                                        Toast.makeText(context, "Lỗi khi tìm kiếm món ăn", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                    Log.d("Timkiemnhahang123","Số món adapter "+ lstMonAn.size());
                                } else {
                                    Toast.makeText(context, "Lỗi khi tìm kiếm theo mã nhà hàng", Toast.LENGTH_SHORT).show();
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
        private ImageView imgHinhAnh;
        private TextView txtTenMonAn;
        private TextView txtSoLuotBan;
        private TextView txtGia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHinhAnh = itemView.findViewById(R.id.imgHinhAnhMonAn);
            txtTenMonAn = itemView.findViewById(R.id.txtTenMonAn);
            txtSoLuotBan = itemView.findViewById(R.id.txtSoLuotBan);
            txtGia = itemView.findViewById(R.id.txtGia);
        }
    }
    @SuppressLint("DefaultLocale")
    private String formatLuotBan(double luotBan) {
        if (luotBan < 1000) {
            return String.format("%.0f" + " lượt bán", luotBan);
        } else if (luotBan % 1000 == 0) {
            int result = (int) (luotBan / 1000);
            return result + "k lượt bán";
        } else {
            double result = luotBan / 1000;
            return String.format("%.1fk" + " lượt bán", result);
        }
    }
}
