package com.example.tezuygulamasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SoketAdapter extends RecyclerView.Adapter<SoketAdapter.SoketHolder> {

    ArrayList<Soket> soketArrayList;
    LayoutInflater inflater;
    BottomSheet bottomSheet;

    public SoketAdapter(ArrayList<Soket> soketArrayList, Context context) {
        inflater = LayoutInflater.from(context);
        this.soketArrayList = soketArrayList;
        bottomSheet = new BottomSheet();
    }

    @NonNull
    @Override
    public SoketAdapter.SoketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_soket_info,parent,false);
        SoketHolder holder = new SoketHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SoketHolder holder, int position) {
        Soket soket =soketArrayList.get(position);
        holder.setData(soket);
    }

    @Override
    public int getItemCount() {
        return soketArrayList.size();
    }

    class SoketHolder extends RecyclerView.ViewHolder{
        TextView tvSoketTip, tvGuc_kW;
        ImageView imgSoketTip;

        public SoketHolder(@NonNull View itemView) {
            super(itemView);
            tvSoketTip = itemView.findViewById(R.id.tvSoketTuru);
            tvGuc_kW = itemView.findViewById(R.id.tvGuc_kW);
            imgSoketTip = itemView.findViewById(R.id.imgSoketTip);
        }

        public void setData(Soket soket){
            this.tvSoketTip.setText(Soket.getTvSoketTuru());
            this.tvGuc_kW.setText(Soket.getTvGuc_kW());
            this.imgSoketTip.setImageResource(Soket.getImgSoketTuru());
        }
    }

}
