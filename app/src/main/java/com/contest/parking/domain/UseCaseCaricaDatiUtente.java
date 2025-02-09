package com.contest.parking.domain;

import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.UtenteRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class UseCaseCaricaDatiUtente {

    private UtenteRepository utenteRepository;

    public UseCaseCaricaDatiUtente() {
        utenteRepository = new UtenteRepository();
    }

    /**
     * Carica i dati dell'utente in base all'UID.
     *
     * @param uid      l'UID dell'utente
     * @param listener callback che restituisce l'oggetto Utente o un errore
     */
    public void loadUserData(String uid, OnUserDataLoadedListener listener) {
        utenteRepository.getUtente(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Utente utente = documentSnapshot.toObject(Utente.class);
                            if (utente != null) {
                                listener.onSuccess(utente);
                            } else {
                                listener.onFailure(new Exception("Dati utente non disponibili"));
                            }
                        } else {
                            listener.onFailure(new Exception("Documento utente non trovato"));
                        }
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnUserDataLoadedListener {
        void onSuccess(Utente utente);
        void onFailure(Exception e);
    }
}
