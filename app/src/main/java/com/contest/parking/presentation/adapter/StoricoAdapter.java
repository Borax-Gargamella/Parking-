package com.contest.parking.presentation.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
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
        private TextView tvId, tvDataInizio, tvDataFine;
        private Button btnPaga;

        public ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvIdPrenotazione);
            tvDataInizio = itemView.findViewById(R.id.tvDataPrenotazioneInizio);
            tvDataFine = itemView.findViewById(R.id.tvDataPrenotazioneFine);
            btnPaga = itemView.findViewById(R.id.btnPaga);
        }

        public void bind(Storico item, OnPagaClickListener listener) {
            // Imposta i dati
            tvId.setText(item.getPostoAutoId());
            tvDataInizio.setText(convertMillisToDate(item.getDataInizio()));
            tvDataFine.setText(convertMillisToDate(item.getDataFine()));
            tvId.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            long currentTime = System.currentTimeMillis();

            if (!item.isPagato()) {
                // Prenotazione non pagata: bottone abilitato e cliccabile
                btnPaga.setVisibility(View.VISIBLE);
                btnPaga.setEnabled(true);
                btnPaga.setOnClickListener(v -> listener.onPagaClick(item));
            } else {
                // Prenotazione pagata
                if (item.getDataFine() > currentTime) {
                    // Se la data di scadenza non è ancora passata:
                    // Mostra il bottone disabilitato e impostalo con colore grigio
                    btnPaga.setVisibility(View.VISIBLE);
                    btnPaga.setEnabled(false);
                    btnPaga.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                    // Rimuove eventuali listener
                    btnPaga.setOnClickListener(null);
                } else {
                    // Se la data di scadenza è passata, puoi decidere di nascondere il bottone
                    btnPaga.setVisibility(View.GONE);
                }
            }
        }

        private String convertMillisToDate(long ms) {
            if (ms == 0) return "N/D";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(new Date(ms));
        }
    }
}

