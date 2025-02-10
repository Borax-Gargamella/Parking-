package com.contest.parking.domain;

import com.contest.parking.data.model.Storico;
import com.contest.parking.data.repository.StoricoRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UseCaseOccupato {

    private StoricoRepository storicoRepository;

    public UseCaseOccupato(StoricoRepository storicoRepository) {
        this.storicoRepository = storicoRepository;
    }

    // Interfaccia per il callback della verifica
    public interface OnOccupiedCheckListener {
        void onOccupied();
        void onFree();
        void onFailure(Exception e);
    }

    /**
     * Verifica se un posto auto (spotId) è occupato per una data richiesta.
     *
     * @param spotId       l'ID del posto auto da verificare
     * @param desiredStart la data richiesta (in millisecondi)
     * @param listener     il callback che notifica se il posto è occupato, libero o in caso di errore
     */
    public void isSpotOccupied(String spotId, long desiredStart, OnOccupiedCheckListener listener) {
        // Recupera tutti i record di prenotazione per il posto auto
        storicoRepository.getStoricoForSpot(spotId)
                .addOnSuccessListener((QuerySnapshot querySnapshot) -> {
                    boolean occupied = false;
                    // Se non ci sono record, consideriamo il posto libero
                    if (querySnapshot.isEmpty()) {
                        listener.onFree();
                        return;
                    }
                    // Itera tra i documenti per verificare se desiredStart ricade in uno degli intervalli
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Storico s = doc.toObject(Storico.class);
                        if (s != null) {
                            long start = s.getDataInizio();
                            long end = s.getDataFine();
                            // Se desiredStart è compreso tra start e end, il posto è occupato
                            if (desiredStart >= start && desiredStart <= end) {
                                occupied = true;
                                break;
                            }
                        }
                    }
                    if (occupied) {
                        listener.onOccupied();
                    } else {
                        listener.onFree();
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }
}
