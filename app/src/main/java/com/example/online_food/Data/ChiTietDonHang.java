package com.example.online_food.Data;

public class ChiTietDonHang {
    private String MaDon, Anh, TenMon, MaMon;
    private double Gia, SoLuong;

    public ChiTietDonHang(){

    }

    public ChiTietDonHang(String anh, String tenMon, String maMon, double gia, double soLuong) {
        Anh = anh;
        TenMon = tenMon;
        MaMon = maMon;
        Gia = gia;
        SoLuong = soLuong;
    }

    public ChiTietDonHang(String maDon, String anh, String tenMon, String maMon, double gia, double soLuong) {
        MaDon = maDon;
        Anh = anh;
        TenMon = tenMon;
        MaMon = maMon;
        Gia = gia;
        SoLuong = soLuong;
    }

    public String getMaDon() {
        return MaDon;
    }

    public void setMaDon(String maDon) {
        MaDon = maDon;
    }

    public String getAnh() {
        return Anh;
    }

    public void setAnh(String anh) {
        Anh = anh;
    }

    public String getTenMon() {
        return TenMon;
    }

    public void setTenMon(String tenMon) {
        TenMon = tenMon;
    }

    public String getMaMon() {
        return MaMon;
    }

    public void setMaMon(String maMon) {
        MaMon = maMon;
    }

    public double getGia() {
        return Gia;
    }

    public void setGia(double gia) {
        Gia = gia;
    }

    public double getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(double soLuong) {
        SoLuong = soLuong;
    }
}
