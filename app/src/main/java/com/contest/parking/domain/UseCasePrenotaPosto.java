package com.contest.parking.domain;

import com.contest.parking.data.model.Storico;
import com.contest.parking.data.repository.PostoAutoRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

public class UseCasePrenotaPosto {

    private PostoAutoRepository postoAutoRepository;
    private StoricoRepository storicoRepository;

    public UseCasePrenotaPosto(PostoAutoRepository postoAutoRepository, StoricoRepository storicoRepository) {
        this.postoAutoRepository = postoAutoRepository;
        this.storicoRepository = storicoRepository;
    }

    // Book a parking spot and save the booking in the history
    public void prenotaPosto(final String postoAutoId, final String utenteId, final String targa, OnSuccessListener<Void> onSuccessListener) {
        postoAutoRepository.updateStatoPostoAuto(postoAutoId, true)
                .addOnSuccessListener(aVoid -> {
                    Storico storico = new Storico();
                    storico.setId(""); // verrà generato in push
                    storico.setPostoAutoID(postoAutoId);
                    storico.setUtenteID(utenteId);
                    storico.setDataInizio(new Date().getTime());
                    storico.setTarga(targa);

                    storicoRepository.addStorico(storico).addOnSuccessListener(onSuccessListener);
                });
    }
}
