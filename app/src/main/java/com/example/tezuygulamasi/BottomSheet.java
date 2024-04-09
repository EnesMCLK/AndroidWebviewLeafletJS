package com.example.tezuygulamasi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet extends BottomSheetDialogFragment {

    protected View view;
    private String myData;
    private TextView bsTextView;

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
            myData = getArguments().getString("data_key");

            // bsTextView.setText(String.valueOf(myData));
            // HATA VERİYOR. EKRANDAKİ BOTTOMSHEET GÜNCELLENMİYOR.

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
        view = view.findViewById(R.id.modalBottomSheetContainer);
        bsTextView = view.findViewById(R.id.mbsTextView);
        bsTextView.setText(myData);


    }
}
