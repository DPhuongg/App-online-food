package com.example.online_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.online_food.Data.ChiTietGioHang;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DatHangAdapter extends RecyclerView.Adapter<DatHangAdapter.DatHangViewHolder> {

    Context context;
    ArrayList<ChiTietGioHang> lst;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DatHangAdapter(Context context, ArrayList<ChiTietGioHang> lst) {
        this.context = context;
        this.lst = lst;
    }

    @NonNull
    @Override
    public DatHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DatHangViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dat_hang, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DatHangViewHolder holder, int position) {
        ChiTietGioHang item = lst.get(position);

        db.collection("MonAn").whereEqualTo("MaMon", item.getMaMon())
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if(task.isSuccessful()) {
                        if(querySnapshot != null) {
                            for(QueryDocumentSnapshot doc: querySnapshot) {
                                MonAn monAn = doc.toObject(MonAn.class);

                                if(monAn.getSoLuongTon() != 0) {
                                    int sl = item.getSoLuong();

                                    holder.soLuong.setText(String.valueOf(sl) + "x");
                                    Glide.with(context).load(item.getAnh()).into(holder.Anh);

                                    double giaMon = item.getGiaMon();
                                    double gia = giaMon * sl;
                                    NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
                                    String giaFormatted = format.format(gia);
                                    holder.giaTien.setText(giaFormatted + " đ");
                                    holder.tenMon.setText(item.getTenMon());
                                }

                                else {
                                    lst.remove(position);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

    public static class DatHangViewHolder extends RecyclerView.ViewHolder {

        ImageView Anh;
        TextView soLuong, tenMon, giaTien;

        public DatHangViewHolder(@NonNull View itemView) {
            super(itemView);

            Anh = itemView.findViewById(R.id.Anh);
            soLuong = itemView.findViewById(R.id.SoLuong);
            tenMon = itemView.findViewById(R.id.TenMon);
            giaTien = itemView.findViewById(R.id.GiaTien);
        }
    }
}
