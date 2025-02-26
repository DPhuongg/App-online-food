package com.example.online_food.Class;

public class Category {
    private String ten;
    private String anh;

    public Category(String ten, String anh) {
        this.ten = ten;
        this.anh = anh;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }
}
