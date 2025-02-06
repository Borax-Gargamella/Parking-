package com.contest.parking.data.repository;

import com.contest.parking.data.FirestoreDataSource;
import com.contest.parking.data.model.Parcheggio;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ParcheggioRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference parcheggioCollection = db.collection("Parcheggi");

    public Task<Void> addParcheggio(Parcheggio parcheggio) {
        String docId = parcheggioCollection.document().getId();
        parcheggio.setId(docId);
        return parcheggioCollection.document(docId).set(parcheggio);
    }

    public  CollectionReference getAllParcheggi() {
        return parcheggioCollection;
    }

    public Query getParcheggiByLuogo(String luogoId) {
        return parcheggioCollection.whereEqualTo("luogoId", luogoId);
    }

    // Other methods CRUD if needed
}
