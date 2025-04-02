package com.example.online_food.Data;

public class KhachHang {
    private String MaKhachHang;
    private String TenKhach;
    private String SoDienThoai;
    private String TenDangNhap;
    private String MatKhau;
    private String DiaChi;
    private String HinhAnh;


    public KhachHang() {
    }

    public KhachHang(String MaKhachHang, String TenKhach, String SoDienThoai, String TenDangNhap, String MatKhau, String DiaChi, String HinhAnh) {
        this.MaKhachHang = MaKhachHang;
        this.TenKhach = TenKhach;
        this.SoDienThoai = SoDienThoai;
        this.TenDangNhap = TenDangNhap;
        this.MatKhau = MatKhau;
        this.DiaChi = DiaChi;
        this.HinhAnh = HinhAnh;
    }

    public String getMaKhachHang() {
        return MaKhachHang;
    }

    public void setMaKhachHang(String MaKhachHang) {
        this.MaKhachHang = MaKhachHang;
    }

    public String getTenKhach() {
        return TenKhach;
    }

    public void setTenKhach(String TenKhach) {
        this.TenKhach = TenKhach;
    }

    public String getSoDienThoai() {
        return SoDienThoai;
    }

    public void setSoDienThoai(String SoDienThoai) {
        this.SoDienThoai = SoDienThoai;
    }

    public String getTenDangNhap() {
        return TenDangNhap;
    }

    public void setTenDangNhap(String TenDangNhap) {
        this.TenDangNhap = TenDangNhap;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String MatKhau) {
        this.MatKhau = MatKhau;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String DiaChi) {
        this.DiaChi = DiaChi;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String HinhAnh) {
        this.HinhAnh = HinhAnh;
    }
}
