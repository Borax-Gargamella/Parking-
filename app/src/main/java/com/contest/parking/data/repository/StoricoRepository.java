package com.contest.parking.data.repository;

import com.contest.parking.data.model.Storico;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class StoricoRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference storicoCollection = db.collection("Storico");

    public Task<Void> addStorico(Storico storico) {
        String docId = storicoCollection.document().getId();
        storico.setId(docId);
        return storicoCollection.document(docId).set(storico);
    }

    public CollectionReference getAllStorici() {
        return storicoCollection;
    }

    public Task<QuerySnapshot> getStoricoAperto(String postoId, String utenteId) {
        return storicoCollection.whereEqualTo("postoAutoId", postoId)
                .whereEqualTo("utenteId", utenteId)
                .whereEqualTo("dataFine", 0)
                .get();
    }

    // e updateDataFine:
    public Task<Void> updateDataFine(String docId, long fine) {
        return storicoCollection.document(docId).update("dataFine", fine);
    }

    public Task<QuerySnapshot> getStoricoApertoByUtente(String utenteId) {
        // Filtra con dataFine = 0, utenteId = ...
        return storicoCollection.whereEqualTo("utenteId", utenteId)
                .whereEqualTo("dataFine", 0)
                .get();
    }

    // Other methods CRUD if needed
}
