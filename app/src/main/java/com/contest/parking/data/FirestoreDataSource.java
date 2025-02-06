package com.contest.parking.data;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreDataSource {
    private static FirebaseFirestore db;

    public static FirebaseFirestore getFirestore() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }
}
