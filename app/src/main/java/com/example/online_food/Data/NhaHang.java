package com.example.online_food.Data;

public class NhaHang {
    private String MaNH;
    private String TenNhaHang;
    private String TrangThaiNhaHang;
    private String TGhoatdong, DiaChi, LoaiNH;
    private String HinhAnh;
    private int SoLuotBan;

    public NhaHang() {
    }


    public String getMaNH() {
        return MaNH;
    }

    public void setMaNH(String maNH) {
        MaNH = maNH;
    }

    public String getTenNhaHang() {
        return TenNhaHang;
    }

    public void setTenNhaHang(String tenNhaHang) {
        TenNhaHang = tenNhaHang;
    }

    public String getTrangThaiNhaHang() {
        return TrangThaiNhaHang;
    }

    public void setTrangThaiNhaHang(String trangThaiNhaHang) {
        TrangThaiNhaHang = trangThaiNhaHang;
    }

    public String getTGhoatdong() {
        return TGhoatdong;
    }

    public void setTGhoatdong(String TGhoatdong) {
        this.TGhoatdong = TGhoatdong;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public String getLoaiNH() {
        return LoaiNH;
    }

    public void setLoaiNH(String loaiNH) {
        LoaiNH = loaiNH;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public int getSoLuotBan() {
        return SoLuotBan;
    }

    public void setSoLuotBan(int soLuotBan) {
        SoLuotBan = soLuotBan;
    }
}
