package com.contest.parking.data.repository;

import com.contest.parking.data.FirestoreDataSource;
import com.contest.parking.data.model.PostoAuto;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostoAutoRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference postoAutoRepositoryCollection = db.collection("PostiAuto");

    public Task<Void> addPostoAuto(PostoAuto postoAuto) {
        String docId = postoAutoRepositoryCollection.document().getId();
        postoAuto.setId(docId);
        return postoAutoRepositoryCollection.document(docId).set(postoAuto);
    }

    public CollectionReference getAllPostiAuto() {
        return postoAutoRepositoryCollection;
    }

    public void updateStatoPostoAuto(String id, boolean stato) {
        postoAutoRepositoryCollection.document(id).update("stato", stato);
    }
    // Other methods CRUD if needed
}
