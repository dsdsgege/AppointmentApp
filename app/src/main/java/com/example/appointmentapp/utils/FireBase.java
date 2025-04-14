package com.example.appointmentapp.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class FireBase {
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



    public static FirebaseAuth getAuth() {
        return firebaseAuth;
    }
}
