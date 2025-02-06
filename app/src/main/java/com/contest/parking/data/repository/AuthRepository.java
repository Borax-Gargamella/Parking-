package com.contest.parking.data.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {

    private FirebaseAuth auth;

    public  AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void registerUser(String email, String password, OnCompleteListener onCompleteListener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
    }

    public void loginUser(String email, String password, OnCompleteListener onCompleteListener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
    }

    public void logoutUser() {
        auth.signOut();
    }
}
