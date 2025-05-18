package com.example.appointmentapp.dao;

import android.content.Context;
import com.example.appointmentapp.R;
import com.example.appointmentapp.model.Hospital;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HospitalDAO {
    private final CollectionReference hospitalRef;

    public HospitalDAO() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        hospitalRef = db.collection("hospital");
    }

    public interface HospitalCallback {
        void onCallback(List<Hospital> hospitalList);
        void onDoctorsCallback(String[] doctors);
    }

    public void getDoctors(Hospital hospital, HospitalCallback callback) {
        Query query = hospitalRef;
        if (hospital.getName() != null) {
            query = query.whereEqualTo("name", hospital.getName());
        }
        query.get().addOnSuccessListener( queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                String[] doctors = (String[]) document.get("doctors");
                callback.onDoctorsCallback(doctors != null ? doctors : new String[]{});
            } else {
                callback.onDoctorsCallback(new String[]{});
            }
        }).addOnFailureListener(e -> {
            callback.onDoctorsCallback(new String[]{});
        });

    }

    public void find(Hospital hospital, HospitalCallback callback) {

        Query query = hospitalRef;
        if(hospital.getName() != null) {
            query = query.whereEqualTo("name", hospital.getName());
        }
        if (hospital.getDoctors() != null) {
            query = query.whereEqualTo("doctors", hospital.getDoctors());
        }
        if(hospital.getNextApp() != null) {
            query = query.whereEqualTo("nextApp", hospital.getNextApp());
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Hospital> result = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                Hospital hosp = doc.toObject(Hospital.class);
                result.add(hosp);
            }
            callback.onCallback(result);
        }).addOnFailureListener(e -> {
            callback.onCallback(new ArrayList<>());
        });
    }

    public void updateAppointment(Hospital hospital) {
            if (hospital.getName() == null || hospital.getNextApp() == null) return;

            Query query = hospitalRef.whereEqualTo("name", hospital.getName());

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    document.getReference().update("nextApp", hospital.getNextApp());
                } else {
                    //TODO: SIKERTELEN
                }
            }).addOnFailureListener(e -> {
                //TODO: ERROR
            });
    }
}

