package com.contest.parking.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
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

    @Override
    public ParcheggioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parcheggio, parent, false);
        return new ParcheggioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParcheggioViewHolder holder, int position) {
        Parcheggio p = parcheggioList.get(position);
        holder.nomeParcheggio.setText(p.getNome());
        holder.postiTot.setText(String.valueOf(p.getPostiTot()));
        holder.prezzo.setText(String.valueOf(p.getPrezzo()));

        // click to open DettaglioParcheggioActivity
        holder.itemView.setOnClickListener(v -> {
            // Intent i = new Intent(context, DettaglioParcheggioActivity.class);
            // i.putExtra("parcheggioId", p.getId());
            // context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return (parcheggioList == null) ? 0 : parcheggioList.size();
    }

    static class ParcheggioViewHolder extends RecyclerView.ViewHolder {
        TextView nomeParcheggio, postiTot, prezzo;

        public ParcheggioViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeParcheggio = itemView.findViewById(R.id.textNomeParcheggio);
            postiTot = itemView.findViewById(R.id.textPostiTot);
            prezzo = itemView.findViewById(R.id.textPrezzoParcheggio);
        }
    }
}
