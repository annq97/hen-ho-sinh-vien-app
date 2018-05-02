package com.santteam.apphenhosinhvien.model;



/**
 * Created by Songtuyen on 9/15/2017.
 */

public class KhachHang {
    private String username;
    private String truong;
    private String ngaysinh;
    private String gioitinh;
    private String sothich;
    private String anhdaidien;
    private String ID;
    private Object online;
    public KhachHang() {
    }

    public KhachHang(String username, String truong, String ngaysinh, String gioitinh, String sothich, String anhdaidien,String ID) {
        this.username = username;
        this.truong = truong;
        this.ngaysinh = ngaysinh;
        this.gioitinh = gioitinh;
        this.sothich = sothich;
        this.anhdaidien = anhdaidien;
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTruong() {
        return truong;
    }

    public void setTruong(String truong) {
        this.truong = truong;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getSothich() {
        return sothich;
    }

    public void setSothich(String sothich) {
        this.sothich = sothich;
    }

    public String getAnhdaidien() {
        return anhdaidien;
    }

    public void setAnhdaidien(String anhdaidien) {
        this.anhdaidien = anhdaidien;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Object getOnline() {
        return online;
    }

    public void setOnline(Object online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return String.format("KhachHang{username='%s', truong='%s', ngaysinh='%s', gioitinh='%s', sothich='%s', anhdaidien='%s', ID='%s'}", username, truong, ngaysinh, gioitinh, sothich, anhdaidien, ID);
    }
}

