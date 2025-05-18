package com.example.appointmentapp.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FireBase {
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static FirebaseAuth getAuth() {
        return firebaseAuth;
    }

    public static void checkDatabaseInitialized(DatabaseCheckCallback callback) {
        firestore.collection("hospital")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        callback.onChecked(snapshot != null && !snapshot.isEmpty());
                    } else {
                        callback.onChecked(false);
                    }
                });
    }

    public interface DatabaseCheckCallback {
        void onChecked(boolean isInitialized);
    }
}