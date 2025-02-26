package com.example.online_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import com.example.online_food.Class.Banner;
import com.example.online_food.R;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Banner> lstBanner;

    public BannerAdapter(Context context, ArrayList<Banner> lstBanner) {
        this.context = context;
        this.lstBanner = lstBanner;
    }

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.banner_item, null);

        ImageView ivBanner = view.findViewById(R.id.ivBanner);

        String anh = "";
        switch (position){
            case 0:
                anh = "banner_5";
                break;
            case 1:
                anh = "banner_1";
                break;
            case 2:
                anh = "banner_2";
                break;
        }

        int resourceAnh = context.getResources().getIdentifier(anh, "drawable", context.getPackageName());

        Glide.with(context).load(resourceAnh).into(ivBanner);

        container.addView(view);
        return view;
    }

    public int getCount() {
        return lstBanner.size();
    }

    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object){
        container.removeView((View) object);
    }
}
