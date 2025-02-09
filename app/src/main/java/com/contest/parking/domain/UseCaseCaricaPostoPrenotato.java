package com.contest.parking.domain;

import com.contest.parking.data.repository.StoricoRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UseCaseCaricaPostoPrenotato {

    private StoricoRepository storicoRepository;

    public UseCaseCaricaPostoPrenotato() {
        storicoRepository = new StoricoRepository();
    }

    /**
     * Carica il record del posto auto prenotato per l'utente.
     *
     * @param uid      l'UID dell'utente
     * @param listener callback che restituisce l'ID del posto (oppure null se non prenotato) o un errore
     */
    public void loadPostoPrenotato(String uid, OnPostoPrenotatoLoadedListener listener) {
        storicoRepository.getStoricoApertoByUtente(uid)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            // Supponiamo che l'utente abbia al massimo un record aperto
                            QueryDocumentSnapshot doc = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                            String postoId = doc.getString("postoAutoId");
                            listener.onSuccess(postoId);
                        } else {
                            // Nessun record trovato: l'utente non ha prenotato un posto
                            listener.onSuccess(null);
                        }
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnPostoPrenotatoLoadedListener {
        /**
         * @param postoId L'ID del posto auto prenotato oppure null se non presente.
         */
        void onSuccess(String postoId);
        void onFailure(Exception e);
    }
}
