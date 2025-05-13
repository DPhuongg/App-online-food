package com.example.online_food.Data;

public class DonHang {
    private String MaDon, MaNH, MaKH, MaShipper, PhuongThucTT, ThoiGianDatHang,ThoiGianGiaoHang,
            TrangThai,TrangThaiShip, TrangThaiDanhGia;
    private double KhuyenMai, PhiShip, TongTien, ThanhTien;
    private Boolean KiemTraDonHang;
    public DonHang() {
    }

    public DonHang(String maDon, String maNH, String maKH, String maShipper, String phuongThucTT,
                   String thoiGianDatHang, String thoiGianGiaoHang, String trangThai, String trangThaiShip,
                   double khuyenMai, double phiShip, double tongTien, double thanhTien, Boolean kiemTraDonHang, String trangThaiDanhGia) {
        MaDon = maDon;
        MaNH = maNH;
        MaKH = maKH;
        MaShipper = maShipper;
        PhuongThucTT = phuongThucTT;
        ThoiGianDatHang = thoiGianDatHang;
        ThoiGianGiaoHang = thoiGianGiaoHang;
        TrangThai = trangThai;
        TrangThaiShip = trangThaiShip;
        KhuyenMai = khuyenMai;
        PhiShip = phiShip;
        TongTien = tongTien;
        KiemTraDonHang = kiemTraDonHang;
        TrangThaiDanhGia = trangThaiDanhGia;
        ThanhTien = thanhTien;
    }

    public String getMaDon() {
        return MaDon;
    }

    public void setMaDon(String maDon) {
        MaDon = maDon;
    }

    public String getMaNH() {
        return MaNH;
    }

    public void setMaNH(String maNH) {
        MaNH = maNH;
    }

    public String getMaKH() {
        return MaKH;
    }

    public void setMaKH(String maKH) {
        MaKH = maKH;
    }

    public String getMaShipper() {
        return MaShipper;
    }

    public void setMaShipper(String maShipper) {
        MaShipper = maShipper;
    }

    public String getPhuongThucTT() {
        return PhuongThucTT;
    }

    public void setPhuongThucTT(String phuongThucTT) {
        PhuongThucTT = phuongThucTT;
    }

    public String getThoiGianDatHang() {
        return ThoiGianDatHang;
    }

    public void setThoiGianDatHang(String thoiGianDatHang) {
        ThoiGianDatHang = thoiGianDatHang;
    }

    public String getThoiGianGiaoHang() {
        return ThoiGianGiaoHang;
    }

    public void setThoiGianGiaoHang(String thoiGianGiaoHang) {
        ThoiGianGiaoHang = thoiGianGiaoHang;
    }

    public String getTrangThaiDanhGia() {
        return TrangThaiDanhGia;
    }

    public void setTrangThaiDanhGia(String trangThaiDanhGia) {
        TrangThaiDanhGia = trangThaiDanhGia;
    }

    public String getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(String trangThai) {
        TrangThai = trangThai;
    }

    public String getTrangThaiShip() {
        return TrangThaiShip;
    }

    public void setTrangThaiShip(String trangThaiShip) {
        TrangThaiShip = trangThaiShip;
    }

    public double getKhuyenMai() {
        return KhuyenMai;
    }

    public void setKhuyenMai(double khuyenMai) {
        KhuyenMai = khuyenMai;
    }

    public double getPhiShip() {
        return PhiShip;
    }

    public void setPhiShip(double phiShip) {
        PhiShip = phiShip;
    }

    public double getTongTien() {
        return TongTien;
    }

    public void setTongTien(double tongTien) {
        TongTien = tongTien;
    }

    public double getThanhTien() {
        return ThanhTien;
    }

    public void setThanhTien(double thanhTien) {
        ThanhTien = thanhTien;
    }

    public Boolean getKiemTraDonHang() {
        return KiemTraDonHang;
    }

    public void setKiemTraDonHang(Boolean kiemTraDonHang) {
        KiemTraDonHang = kiemTraDonHang;
    }
}
