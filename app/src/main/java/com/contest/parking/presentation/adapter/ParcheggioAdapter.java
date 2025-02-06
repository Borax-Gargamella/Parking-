package com.contest.parking.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.data.model.Parcheggio;

import java.util.List;

public class ParcheggioAdapter extends RecyclerView.Adapter<ParcheggioAdapter.ParcheggioViewHolder> {
    private List<Parcheggio> parcheggioList;
    private Context context;

    public ParcheggioAdapter(List<Parcheggio> parcheggioList, Context context) {
        this.parcheggioList = parcheggioList;
        this.context = context;
    }

    public void setParcheggioList(List<Parcheggio> list) {
        this.parcheggioList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ParcheggioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_parcheggio, parent, false);
        return new ParcheggioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParcheggioViewHolder holder, int position) {
        Parcheggio p = parcheggioList.get(position);
        holder.nomeParcheggio.setText(p.getNome());
        holder.postiTot.setText(String.valueOf(p.getPostiTot()));
        holder.prezzo.setText(String.valueOf(p.getPrezzo()));

        // Se vuoi gestire un clic su questo item
        holder.itemView.setOnClickListener(v -> {
            // ad es. aprire un DettaglioParcheggioActivity
        });
    }

    @Override
    public int getItemCount() {
        return (parcheggioList != null) ? parcheggioList.size() : 0;
    }

    public static class ParcheggioViewHolder extends RecyclerView.ViewHolder {
        TextView nomeParcheggio, postiTot, prezzo;

        public ParcheggioViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeParcheggio = itemView.findViewById(R.id.textNomeParcheggio);
            postiTot = itemView.findViewById(R.id.textPostiTot);
            prezzo = itemView.findViewById(R.id.textPrezzoParcheggio);
        }
    }
}
