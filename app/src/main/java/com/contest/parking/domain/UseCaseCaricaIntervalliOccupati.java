package com.contest.parking.domain;

import com.contest.parking.data.model.Range;
import com.contest.parking.data.model.Storico;
import com.contest.parking.data.repository.StoricoRepository;

import java.util.ArrayList;
import java.util.List;

public class UseCaseCaricaIntervalliOccupati {
    private StoricoRepository storicoRepository;

    public UseCaseCaricaIntervalliOccupati(StoricoRepository storicoRepository) {
        this.storicoRepository = storicoRepository;
    }

    public interface OnRangesLoadedListener {
        void onRangesLoaded(List<Range> ranges);
        void onError(Exception e);
    }

    public void execute(String spotId, final OnRangesLoadedListener listener) {
        storicoRepository.getStoricoBySpotId(spotId, new StoricoRepository.OnStoricoLoadedListener() {
            @Override
            public void onStoricoLoaded(List<Storico> listaStorico) {
                List<Range> dateOccupate = new ArrayList<>();
                for (Storico s : listaStorico) {
                    // Supponendo che s.getDataInizio() e s.getDataFine() restituiscano valori long (millisecondi)
                    dateOccupate.add(new Range(s.getDataInizio(), s.getDataFine()));
                }
                listener.onRangesLoaded(dateOccupate);
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }
}
