package com.contest.parking.data.repository;

import com.contest.parking.data.model.Utente;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UtenteRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference utenteCollection = db.collection("Utenti");

    public Task<Void> addUtente(Utente utente) {
        String docId = utenteCollection.document().getId();
        utente.setId(docId);
        return utenteCollection.document(docId).set(utente);
    }

    public DocumentReference getUtente(String id) {
        return utenteCollection.document(id);
    }

    public CollectionReference getAllUtenti() {
        return utenteCollection;
    }
}
