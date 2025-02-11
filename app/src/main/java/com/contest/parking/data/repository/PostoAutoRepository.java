package com.contest.parking.data.repository;

import com.contest.parking.data.model.PostoAuto;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostoAutoRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference postoAutoRepositoryCollection = db.collection("PostoAuto");

    public Task<Void> addPostoAuto(PostoAuto postoAuto) {
        String docId = postoAutoRepositoryCollection.document().getId();
        postoAuto.setId(docId);
        return postoAutoRepositoryCollection.document(docId).set(postoAuto);
    }

    public Task<Void> addPostoAutoWithId(PostoAuto postoAuto) {
        return postoAutoRepositoryCollection.document(postoAuto.getId()).set(postoAuto);
    }

    public CollectionReference getAllPostiAuto() {
        return postoAutoRepositoryCollection;
    }

    //CRUD
    public Task<Void> updateStatoPostoAuto(String postoId, boolean occupato) {
        return postoAutoRepositoryCollection.document(postoId).update("statoOccupato", occupato);
    }

    //Query
    public Query getPostiByParcheggio(String parcheggioId) {
        return postoAutoRepositoryCollection.whereEqualTo("parcheggioId", parcheggioId);
    }
}
