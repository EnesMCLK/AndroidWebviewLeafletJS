package com.example.tezuygulamasi;

import static com.example.tezuygulamasi.Soket.getImgSoketTuru;
import static com.example.tezuygulamasi.Soket.getTvGuc_kW;
import static com.example.tezuygulamasi.Soket.getTvSoketTuru;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class BottomSheet extends BottomSheetDialogFragment {

    protected View view;
    private List<String> mData,mBaslik,mMarka,mSarjAgiIsletmecisi,mYesilSarj,mAdres,mSoketNo,mSoketTipi,mSoketTuru,mSoketGucu,mX,mY;
    private String mSiraNo;
    private TextView baslik,marka;
    private Button btnRoute;
    private DatabaseHelper dbhelper;
    private RecyclerView recyclerView;

    public static BottomSheet newInstance(String data) {
        BottomSheet fragment = new BottomSheet();
        Bundle args = new Bundle();
        args.putString("data_key", data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSiraNo = getArguments().getString("data_key");
            mSiraNo = String.valueOf(mSiraNo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Burada layout'unuzu inflate edin ve myData ile UI'ı güncelleyin
        view = inflater.inflate(R.layout.modal_bottom_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbhelper = new DatabaseHelper(getContext());

        view = view.findViewById(R.id.modalBottomSheetContainer);
        baslik = view.findViewById(R.id.baslik);
        marka = view.findViewById(R.id.marka);
        btnRoute = view.findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(v -> {
            // Buraya tıklama eylemleri yazılacak.
        });


        try {
            Log.e("DATABASE","onViewCreated: "+data(3));
            baslik.setText(baslik().get(0));
            marka.setText(marka().get(0));

            try {
                Soket soket = new Soket(getImgSoketTuru(),getTvSoketTuru(),getTvGuc_kW());

                switch (String.valueOf(dbhelper.getSoketTuru(mSiraNo).get(0))){
                    case "AC_TYPE2":
                        Log.e("AC_TYPE2", String.valueOf(R.mipmap.ac_type2));
                        soket.setImgSoketTuru(R.mipmap.ac_type2);
                    case "DC_CCS":
                        Log.e("DC_CCS", String.valueOf(R.mipmap.dc_css2));
                        soket.setImgSoketTuru(R.mipmap.dc_css2);
                    case "DC_CHADEMO":
                        Log.e("DC_CHADEMO", String.valueOf(R.mipmap.dc_chademo));
                        soket.setImgSoketTuru(R.mipmap.dc_chademo);
                }

                String guc_KW = dbhelper.getSoketGucu(mSiraNo).get(0) + " kW";
                soket.setTvGuc_kW(guc_KW);
                soket.setTvSoketTuru(String.valueOf(dbhelper.getSoketTuru(mSiraNo).get(0)));

                soket = new Soket(getImgSoketTuru(), getTvSoketTuru(), getTvGuc_kW());
                ArrayList<Soket> soketArrayList = new ArrayList<>();
                soketArrayList.add(soket);
            } catch (Exception e){
                Log.e("Soket getData",String.valueOf(e.getLocalizedMessage()));
            }

            try {
                SoketAdapter soketAdapter = new SoketAdapter(Soket.getData(),getContext());
                recyclerView = view.findViewById(R.id.bsSoketLayout);
                recyclerView.setAdapter(soketAdapter);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(manager);
            } catch (Exception e){
                Log.e("ADAPTER",String.valueOf(e.getLocalizedMessage()));
            }
        } catch (Exception e){
            Log.e("BottomSheet onViewCreated",String.valueOf(e.getLocalizedMessage()));
        }
    }

    public List<String> data(int sutunNo){
        mData = dbhelper.getData(mSiraNo, sutunNo);
        return mData;
    }

    public List<String> baslik(){
        mBaslik = dbhelper.getBaslik(mSiraNo);
        return mBaslik;
    }

    public List<String> marka(){
        mMarka = dbhelper.getMarka(mSiraNo);
        return mMarka;
    }

    public List<String> sarjAgiIsletmecisi(){
        mSarjAgiIsletmecisi = dbhelper.getSarjAgiIsletmecisi(mSiraNo);
        return mSarjAgiIsletmecisi;
    }

    public List<String> YesilSarj(){
        mYesilSarj = dbhelper.getYesilSarj(mSiraNo);
        return mYesilSarj;
    }

    public List<String> Adres(){
        mAdres = dbhelper.getAdres(mSiraNo);
        return mAdres;
    }

    public List<String> SoketNo(){
        mSoketNo = dbhelper.getSoketNo(mSiraNo);
        return mSoketNo;
    }

    public List<String> SoketTipi(){
        mSoketTipi = dbhelper.getSoketTipi(mSiraNo);
        return mSoketTipi;
    }

    public List<String> SoketTuru(){
        mSoketTuru = dbhelper.getSoketTuru(mSiraNo);
        return mSoketTuru;
    }

    public List<String> SoketGucu(){
        mSoketGucu = dbhelper.getSoketGucu(mSiraNo);
        return mSoketGucu;
    }
}
