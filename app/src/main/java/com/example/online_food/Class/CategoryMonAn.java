package com.example.online_food.Class;

import com.example.online_food.Data.MonAn;

import java.util.ArrayList;

public class CategoryMonAn {
    private static int currentId = 0; // Biến tĩnh để theo dõi ID tối đa

    private String categoryName;
    private int categoryId;
    private ArrayList<MonAn> lstMonAn;

    public CategoryMonAn(String categoryName, ArrayList<MonAn> lstMonAn) {
        this.categoryName = categoryName;
        this.lstMonAn = lstMonAn;
    }

    private static int generateId() {
        return ++currentId; // Tăng giá trị ID hiện tại và trả về
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

    public ArrayList<MonAn> getLstMonAn() {
        return lstMonAn;
    }

    public void setLstMonAn(ArrayList<MonAn> lstMonAn) {
        this.lstMonAn = lstMonAn;
    }
}
