package com.contest.parking.domain;

import com.contest.parking.data.repository.PostoAutoRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class UseCaseCompletaPagamento {

    private PostoAutoRepository postoRepo;
    private StoricoRepository storicoRepo;

    public UseCaseCompletaPagamento(PostoAutoRepository postoRepo, StoricoRepository storicoRepo) {
        this.postoRepo = postoRepo;
        this.storicoRepo = storicoRepo;
    }

    /**
     * Esegue pagamento e libera il posto auto:
     * 1) Trova record Storico "aperto" (dataFine = 0) per un certo postoId
     * 2) Imposta dataFine = now, pagato = true (se hai un campo)
     * 3) Rende statoOccupato = false nel PostoAuto
     */
    public void completaPagamento(String postoId,
                                  String utenteId,
                                  OnSuccessListener<Void> onSuccess) {
        // 1) Trova nello Storico
        storicoRepo.getStoricoAperto(postoId, utenteId)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // prendi il primo record (o tutti, se potenzialmente sono più)
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        String storicoDocId = doc.getId();

                        long now = new Date().getTime();
                        // aggiorna dataFine e pagato = true
                        storicoRepo.updateDataFine(storicoDocId, now)
                                .addOnSuccessListener(aVoid -> {
                                    // 2) PostoAuto -> statoOccupato = false
                                    postoRepo.updateStatoPostoAuto(postoId, false)
                                            .addOnSuccessListener(onSuccess)
                                            .addOnFailureListener(e ->
                                                    // Errore su update posto
                                            {});
                                })
                                .addOnFailureListener(e ->
                                        // Errore su update dataFine
                                {});
                    } else {
                        // Nessun record trovato (strano, vuol dire che non era aperto)
                    }
                })
                .addOnFailureListener(e ->
                        // Errore query Storico
                {});
    }
}
