package com.example.online_food.Class;

import com.example.online_food.Data.NhaHang;

import java.util.ArrayList;

public class CategoryNhaHang {
    private String categoryName;
    private int categoryId;
    private ArrayList<NhaHang> lstNhaHang;

    public CategoryNhaHang(String categoryName, ArrayList<NhaHang> lstNhaHang) {
        this.categoryName = categoryName;
        this.lstNhaHang = lstNhaHang;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public ArrayList<NhaHang> getLstNhaHang() {
        return lstNhaHang;
    }

    public void setLstNhaHang(ArrayList<NhaHang> lstNhaHang) {
        this.lstNhaHang = lstNhaHang;
    }
}
