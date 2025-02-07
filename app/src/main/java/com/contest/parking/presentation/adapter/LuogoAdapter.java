package com.contest.parking.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Luogo;
import com.contest.parking.presentation.ParcheggiActivity;

import java.util.List;

public class LuogoAdapter extends RecyclerView.Adapter<LuogoAdapter.LuogoViewHolder> {

    private Context context;
    private List<Luogo> luoghiList;

    public LuogoAdapter(Context context, List<Luogo> luoghi) {
        this.context = context;
        this.luoghiList = luoghi;
    }

    public void setLuoghiList(List<Luogo> luoghi) {
        this.luoghiList = luoghi;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LuogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_luogo, parent, false);
        return new LuogoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LuogoViewHolder holder, int position) {
        Luogo luogo = luoghiList.get(position);
        holder.nomeText.setText(luogo.getNome());
        holder.indirizzoText.setText(luogo.getIndirizzo());

        // Click to open ParcheggiActivity
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ParcheggiActivity.class);
            i.putExtra("luogoId", luogo.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return luoghiList.size();
    }

    public static class LuogoViewHolder extends RecyclerView.ViewHolder {
        TextView nomeText, indirizzoText;

        public LuogoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeText = itemView.findViewById(R.id.itemLuogoNome);
            indirizzoText = itemView.findViewById(R.id.itemLuogoIndirizzo);
        }
    }
}
