package com.contest.parking.domain;

import com.contest.parking.data.model.Storico;
import com.contest.parking.data.repository.StoricoRepository;

import java.util.List;

public class UseCaseCaricaPrenotazioniNonPagate  {

    private StoricoRepository storicoRepository;

    // Callback per restituire il risultato
    public interface Callback {
        void onSuccess(List<Storico> storiciNonPagati);
        void onError(Exception e);
    }

    public UseCaseCaricaPrenotazioniNonPagate(StoricoRepository storicoRepository) {
        this.storicoRepository = storicoRepository;
    }

    // Esegue il caso d'uso
    public void execute(String currentUid, Callback callback) {
        storicoRepository.getStoricoNonPagatoByUtente(currentUid, new StoricoRepository.OnStoricoLoadedListener() {
            @Override
            public void onStoricoLoaded(List<Storico> storiciNonPagati) {
                callback.onSuccess(storiciNonPagati);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
