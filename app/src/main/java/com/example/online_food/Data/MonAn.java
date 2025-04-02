package com.example.online_food.Data;

public class MonAn {
    private String MaMon,MaMenu;
    private String TenMon;
    private boolean TrangThai;
    private double GiaMon;
    private boolean CheckComBo;
    private String MoTa;
    private double SoLuotBan;
    private String HinhAnh;
    private double SoLuongTon;

    public MonAn() {
    }

    public MonAn(String maMon, String maMenu, String tenMon, boolean trangThai, double giaMon, boolean checkComBo, String moTa, double soLuotBan, String hinhAnh, double soLuongTon) {
        MaMon = maMon;
        MaMenu = maMenu;
        TenMon = tenMon;
        TrangThai = trangThai;
        GiaMon = giaMon;
        CheckComBo = checkComBo;
        MoTa = moTa;
        SoLuotBan = soLuotBan;
        HinhAnh = hinhAnh;
        SoLuongTon = soLuongTon;
    }

    public String getMaMon() {
        return MaMon;
    }

    public void setMaMon(String maMon) {
        MaMon = maMon;
    }

    public String getMaMenu() {
        return MaMenu;
    }

    public void setMaMenu(String maMenu) {
        MaMenu = maMenu;
    }

    public String getTenMon() {
        return TenMon;
    }

    public void setTenMon(String tenMon) {
        TenMon = tenMon;
    }

    public boolean isTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(boolean trangThai) {
        TrangThai = trangThai;
    }

    public double getGiaMon() {
        return GiaMon;
    }

    public void setGiaMon(double giaMon) {
        GiaMon = giaMon;
    }

    public boolean isCheckComBo() {
        return CheckComBo;
    }

    public void setCheckComBo(boolean checkComBo) {
        CheckComBo = checkComBo;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        MoTa = moTa;
    }

    public double getSoLuotBan() {
        return SoLuotBan;
    }

    public void setSoLuotBan(double soLuotBan) {
        SoLuotBan = soLuotBan;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public double getSoLuongTon() {
        return SoLuongTon;
    }

    public void setSoLuongTon(double soLuongTon) {
        SoLuongTon = soLuongTon;
    }
}
