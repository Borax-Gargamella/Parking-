package com.contest.parking.data.repository;

import com.contest.parking.data.FirestoreDataSource;
import com.contest.parking.data.model.Storico;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class StoricoRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference storicoCollection = db.collection("Storici");

    public Task<Void> addStorico(Storico storico) {
        String docId = storicoCollection.document().getId();
        storico.setId(docId);
        return storicoCollection.document(docId).set(storico);
    }

    public CollectionReference getAllStorici() {
        return storicoCollection;
    }

    // Other methods CRUD if needed
}
