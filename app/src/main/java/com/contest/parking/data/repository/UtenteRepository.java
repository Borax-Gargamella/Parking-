package com.contest.parking.data.repository;

import com.contest.parking.data.model.Utente;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UtenteRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference utenteCollection = db.collection("Utente");

    public Task<Void> addUtente(Utente utente) {
        String docId = utente.getId();
        return utenteCollection.document(docId).set(utente);
    }

    public DocumentReference getUtente(String id) {
        return utenteCollection.document(id);
    }

    public CollectionReference getAllUtenti() {
        return utenteCollection;
    }

    /**
     * Aggiorna i dati dell'utente identificato da uid.
     *
     * @param uid L'ID dell'utente.
     * @param updatedData Una mappa con i campi e i nuovi valori.
     * @return Un Task che indica il successo o il fallimento dell'operazione.
     */
    public Task<Void> updateUtente(String uid, Map<String, Object> updatedData) {
        return db.collection("Utente")
                .document(uid)
                .update(updatedData);
    }
}
