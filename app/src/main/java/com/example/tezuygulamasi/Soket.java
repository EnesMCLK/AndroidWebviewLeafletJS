package com.example.tezuygulamasi;

import java.util.ArrayList;

public class Soket {
    private DatabaseHelper dbhelper;
    private int imgSoketTuru;
    private String tvSoketTuru, tvGuc_kW;


    public Soket(int imgSoketTip, String tvSoketTip, String tvGuc_kW) {
        this.imgSoketTuru = imgSoketTip;
        this.tvSoketTuru = tvSoketTip;
        this.tvGuc_kW = tvGuc_kW;
    }


    public int getImgSoketTip() {
        return imgSoketTuru;
    }

    public void setImgSoketTip(int imgSoketTip) {
        this.imgSoketTuru = imgSoketTip;
    }

    public String getTvSoketTuru() {
        return tvSoketTuru;
    }

    public void setTvSoketTuru(String tvSoketTuru) {
        this.tvSoketTuru = tvSoketTuru;
    }

    public String getTvGuc_kW() {
        return tvGuc_kW;
    }

    public void setTvGuc_kW(String tvGuc_kW) {
        this.tvGuc_kW = tvGuc_kW;
    }

    public static ArrayList<Soket> getData(){
        int imgSoketTuru;
        String tvSoketTuru, tvGuc_kW;
        BottomSheet bottomSheet = new BottomSheet();
        //SoketAdapter.SoketHolder soketHolder = new SoketAdapter.SoketHolder(new View(bottomSheet.getContext()));
        imgSoketTuru = R.mipmap.ac_type2;
        tvSoketTuru = "AC TYPE2";
        tvGuc_kW = "220 kW";
        Soket soket = new Soket(imgSoketTuru,tvSoketTuru,tvGuc_kW);
        ArrayList<Soket> soketArrayList = new ArrayList<>();
        soketArrayList.add(soket);

        return soketArrayList;
    }
}
