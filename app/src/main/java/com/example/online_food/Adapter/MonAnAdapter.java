package com.example.online_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.online_food.Class.CategoryMonAn;
import com.example.online_food.R;

import java.util.ArrayList;

public class MonAnAdapter extends RecyclerView.Adapter<MonAnAdapter.MonAnViewHoder>{
    private Context context;
    private ArrayList<CategoryMonAn> categorieslist;
    private MonAnItemsAdapter adapter;

    public MonAnAdapter(Context context, ArrayList<CategoryMonAn> categorieslist) {
        this.context = context;
        this.categorieslist = categorieslist;
    }

    @NonNull
    @Override
    public MonAnAdapter.MonAnViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MonAnAdapter.MonAnViewHoder(LayoutInflater.from(context).inflate(R.layout.mon_an, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MonAnAdapter.MonAnViewHoder holder, int position) {
        CategoryMonAn cateMA = categorieslist.get(position);
        holder.TenMenu.setText(cateMA.getCategoryName());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.DSMonAn.setLayoutManager(gridLayoutManager);
        adapter = new MonAnItemsAdapter(context, cateMA.getLstMonAn());
        holder.DSMonAn.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return categorieslist.size();
    }

    public class MonAnViewHoder extends RecyclerView.ViewHolder{

        private RecyclerView DSMonAn;
        private TextView TenMenu;

        public MonAnViewHoder(@NonNull View itemView) {
            super(itemView);

            DSMonAn = itemView.findViewById(R.id.DSMonAn);
            TenMenu = itemView.findViewById(R.id.tvTenMenu);
        }
    }
}
