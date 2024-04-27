package com.example.tezuygulamasi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class BottomSheet extends BottomSheetDialogFragment {

    protected View view;
    private List<String> mData,mBaslik,mMarka;
    private String mSiraNo;
    private TextView baslik,marka;
    private Button btnRoute;
    private DatabaseHelper dbhelper;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbhelper = new DatabaseHelper(getContext());

        view = view.findViewById(R.id.modalBottomSheetContainer);
        baslik = view.findViewById(R.id.baslik);
        marka = view.findViewById(R.id.marka);
        btnRoute = view.findViewById(R.id.btnRoute);

        try {
            Log.e("DATABASE","onViewCreated: "+data(3));





            baslik.setText(baslik().get(0));
            marka.setText(marka().get(0));
        } catch (Exception e){
            e.getLocalizedMessage();
        }
    }
}
