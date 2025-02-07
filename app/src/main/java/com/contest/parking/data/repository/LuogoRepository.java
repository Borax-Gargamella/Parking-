package com.contest.parking.data.repository;

import com.contest.parking.data.model.Luogo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LuogoRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference luogoCollection = db.collection("Luoghi");

    public Task<Void> addLuogo(Luogo luogo) {
        String docId = luogoCollection.document().getId();
        luogo.setId(docId);
        return luogoCollection.document(docId).set(luogo);
    }

    public Task<Void> updateLuogo(Luogo luogo) {
        return luogoCollection.document(luogo.getId()).set(luogo);
    }

    public Task<Void> deleteLuogo(String luogoId) {
        return luogoCollection.document(luogoId).delete();
    }

    public  CollectionReference getAllLuoghi() {
        return luogoCollection;
    }

    // Other methods CRUD if needed
}
