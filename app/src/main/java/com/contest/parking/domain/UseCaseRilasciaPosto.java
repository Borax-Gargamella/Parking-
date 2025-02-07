package com.contest.parking.domain;

import com.contest.parking.data.repository.PostoAutoRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class UseCaseRilasciaPosto {
    private final PostoAutoRepository postoRepo;
    private final StoricoRepository storicoRepo;

    public UseCaseRilasciaPosto(PostoAutoRepository pRepo, StoricoRepository sRepo) {
        this.postoRepo = pRepo;
        this.storicoRepo = sRepo;
    }

    /**
     * Libera il posto, aggiorna dataFine nello Storico.
     * @param postoId   ID del PostoAuto da liberare
     * @param utenteId  ID utente che lo occupava
     */
    public void rilasciaPosto(String postoId, String utenteId) {
        // 1. PostoAuto -> statoOccupato = false
        postoRepo.updateStatoPostoAuto(postoId, false);

        // 2. Trovare l'ultimo record in Storico con (postoAutoId = postoId, utenteId = utenteId, dataFine=0)
        Task<QuerySnapshot> task = storicoRepo.getStoricoAperto(postoId, utenteId);
        task.addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                // prendi il primo (o l'unico) record
                String docId = querySnapshot.getDocuments().get(0).getId();
                // setti dataFine
                storicoRepo.updateDataFine(docId, new Date().getTime());
            }
        });
    }
}
