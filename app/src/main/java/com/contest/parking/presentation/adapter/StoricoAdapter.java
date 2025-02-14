package com.contest.parking.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Storico;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoricoAdapter extends RecyclerView.Adapter<StoricoAdapter.ViewHolder> {

    public interface OnPagaClickListener {
        void onPagaClick(Storico storicoItem);
    }

    private List<Storico> listaStorico;
    private OnPagaClickListener pagaClickListener;

    public StoricoAdapter(List<Storico> lista, OnPagaClickListener listener) {
        this.listaStorico = lista;
        this.pagaClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_storico, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Storico s = listaStorico.get(position);
        holder.bind(s, pagaClickListener);
    }

    @Override
    public int getItemCount() {
        return (listaStorico != null) ? listaStorico.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId, tvData;
        private Button btnPaga;

        public ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvIdPrenotazione);
            tvData = itemView.findViewById(R.id.tvDataPrenotazione);
            btnPaga = itemView.findViewById(R.id.btnPaga);
        }

        public void bind(Storico item, OnPagaClickListener listener) {
            tvId.setText(item.getPostoAutoId());// o un substring se è troppo lungo
            tvId.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            String dataString = convertMillisToDate(item.getDataInizio())+ " " + convertMillisToDate(item.getDataFine());
            tvData.setText(dataString);
            tvId.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Se 'pagato' è false, mostra il bottone. Altrimenti potresti nasconderlo
            if (!item.isPagato()) {
                btnPaga.setVisibility(View.VISIBLE);
                btnPaga.setOnClickListener(v -> listener.onPagaClick(item));
            } else {
                btnPaga.setVisibility(View.GONE);
            }
        }

        private String convertMillisToDate(long ms) {
            if (ms == 0) return "N/D";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(new Date(ms));
        }
    }
}

