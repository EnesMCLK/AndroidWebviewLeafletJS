package tr.edu.kocaeli.tezuygulamasi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SoketAdapter extends RecyclerView.Adapter<SoketAdapter.SoketHolder> {

    LayoutInflater inflater;
    List<String> soketTuru;
    List<String> soketGucu;

    public SoketAdapter(Context context, List<String> soketTuru, List<String> soketGucu) {
        inflater = LayoutInflater.from(context);
        this.soketTuru = soketTuru;
        this.soketGucu = soketGucu;
    }

    @NonNull
    @Override
    public SoketAdapter.SoketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_soket_info,parent,false);
        SoketHolder holder = new SoketHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SoketHolder holder, int position) {
        holder.tvSoketTuru.setText(soketTuru.get(position));
        holder.tvGuc_kW.setText(soketGucu.get(position) + " kW");
        switch (soketTuru.get(position)) {
            case "AC_TYPE":
                holder.imgSoketTuru.setImageResource(R.mipmap.ac_type2);
            case "DC_CCS":
                holder.imgSoketTuru.setImageResource(R.mipmap.dc_ccs);
            case "DC_CHADEMO":
                holder.imgSoketTuru.setImageResource(R.mipmap.dc_chademo);
        }
    }

    @Override
    public int getItemCount() {
        return soketTuru.size();
    }

    class SoketHolder extends RecyclerView.ViewHolder{
        TextView tvSoketTuru, tvGuc_kW;
        ImageView imgSoketTuru;

        public SoketHolder(@NonNull View itemView) {
            super(itemView);
            tvSoketTuru = itemView.findViewById(R.id.tvSoketTuru);
            tvGuc_kW = itemView.findViewById(R.id.tvGuc_kW);
            imgSoketTuru = itemView.findViewById(R.id.imgSoketTuru);
        }
    }
}