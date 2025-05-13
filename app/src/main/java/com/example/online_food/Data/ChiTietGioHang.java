package com.example.online_food.Data;

public class ChiTietGioHang {
    private String Anh,MaKH,MaMon,MaNH,TenMon;
    private int SoLuong;
    private double GiaMon;

    public ChiTietGioHang() {
    }

    public ChiTietGioHang(String anh, String maKH, String maMon, String maNH, String tenMon, int soLuong, double giaMon) {
        Anh = anh;
        MaKH = maKH;
        MaMon = maMon;
        MaNH = maNH;
        TenMon = tenMon;
        SoLuong = soLuong;
        GiaMon = giaMon;
    }

    public double getGiaMon() {
        return GiaMon;
    }

    public void setGiaMon(double giaMon) {
        GiaMon = giaMon;
    }

    public String getAnh() {
        return Anh;
    }

    public void setAnh(String anh) {
        Anh = anh;
    }

    public String getMaKH() {
        return MaKH;
    }

    public void setMaKH(String maKH) {
        MaKH = maKH;
    }

    public String getMaMon() {
        return MaMon;
    }

    public void setMaMon(String maMon) {
        MaMon = maMon;
    }

    public String getMaNH() {
        return MaNH;
    }

    public void setMaNH(String maNH) {
        MaNH = maNH;
    }

    public String getTenMon() {
        return TenMon;
    }

    public void setTenMon(String tenMon) {
        TenMon = tenMon;
    }

    public int getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(int soLuong) {
        SoLuong = soLuong;
    }

    @Override
    public String toString() {
        return "ChiTietGioHang{" +
                "maMon='" + MaMon + '\'' +
                ", tenMon='" + TenMon + '\'' +
                ", giaMon=" + GiaMon +
                ", soLuong=" + SoLuong +
                ", anh='" + Anh + '\'' +
                '}';
    }
}
