package com.example.online_food.Data;

public class DanhGia {
    private String MaDanhGia, MaDon, MaKH, MaNH, BinhLuan, ThoiGianDanhGia;
    private float SoDiem;

    public DanhGia() {
    }

    public DanhGia(String maDanhGia, String maDon, String maKH, String maNH, String binhLuan, String thoiGianDanhGia, float soDiem) {
        MaDanhGia = maDanhGia;
        MaDon = maDon;
        MaKH = maKH;
        MaNH = maNH;
        BinhLuan = binhLuan;
        ThoiGianDanhGia = thoiGianDanhGia;
        SoDiem = soDiem;
    }

    public String getMaDanhGia() {
        return MaDanhGia;
    }

    public void setMaDanhGia(String maDanhGia) {
        MaDanhGia = maDanhGia;
    }

    public String getMaDon() {
        return MaDon;
    }

    public void setMaDon(String maDon) {
        MaDon = maDon;
    }

    public String getMaKH() {
        return MaKH;
    }

    public void setMaKH(String maKH) {
        MaKH = maKH;
    }

    public String getMaNH() {
        return MaNH;
    }

    public void setMaNH(String maNH) {
        MaNH = maNH;
    }

    public String getBinhLuan() {
        return BinhLuan;
    }

    public void setBinhLuan(String binhLuan) {
        BinhLuan = binhLuan;
    }

    public String getThoiGianDanhGia() {
        return ThoiGianDanhGia;
    }

    public void setThoiGianDanhGia(String thoiGianDanhGia) {
        ThoiGianDanhGia = thoiGianDanhGia;
    }

    public float getSoDiem() {
        return SoDiem;
    }

    public void setSoDiem(float soDiem) {
        SoDiem = soDiem;
    }
}
