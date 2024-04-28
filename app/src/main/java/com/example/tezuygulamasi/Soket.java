package com.example.tezuygulamasi;

import java.util.ArrayList;

public class Soket {
    private DatabaseHelper dbhelper;
    private static int imgSoketTuru;
    private static String tvSoketTuru, tvGuc_kW;


    public Soket(int iSoketTip, String sSoketTip, String sGuc_kW) {
        imgSoketTuru = iSoketTip;
        tvSoketTuru = sSoketTip;
        tvGuc_kW = sGuc_kW;
    }


    public static int getImgSoketTuru() {
        return imgSoketTuru;
    }

    public void setImgSoketTuru(int imgSoketTip) {
        imgSoketTuru = imgSoketTip;
    }

    public static String getTvSoketTuru() {
        return tvSoketTuru;
    }

    public void setTvSoketTuru(String sSoketTuru) {
        tvSoketTuru = sSoketTuru;
    }

    public static String getTvGuc_kW() {
        return tvGuc_kW;
    }

    public void setTvGuc_kW(String sGuc_kW) {
        tvGuc_kW = sGuc_kW;
    }

    public static ArrayList<Soket> getData(){
        int imgSoketTuru;
        String tvSoketTuru, tvGuc_kW;
        BottomSheet bottomSheet = new BottomSheet();
        //SoketAdapter.SoketHolder soketHolder = new SoketAdapter.SoketHolder(new View(bottomSheet.getContext()));

        Soket soket = new Soket(getImgSoketTuru(),getTvSoketTuru(),getTvGuc_kW());
        ArrayList<Soket> soketArrayList = new ArrayList<>();
        soketArrayList.add(soket);

        return soketArrayList;
    }
}
