package com.example.appointmentapp.dao;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.appointmentapp.R;
import com.example.appointmentapp.model.Hospital;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
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
        void onDoctorCountCallback(int count);
    }

    // ÖSSZETETT LEKÉRDEZÉS
    public void getDoctors(Hospital hospital, HospitalCallback callback) {
        Query query = hospitalRef;
        if (hospital.getName() != null) {
            query = query.whereEqualTo("name", hospital.getName());
            query = query.orderBy("name");
        }
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                List<String> doctorsList = (List<String>) document.get("doctors");
                String[] doctors = doctorsList != null ? doctorsList.toArray(new String[0]) : new String[]{};
                callback.onDoctorsCallback(doctors);
            } else {
                callback.onDoctorsCallback(new String[]{});
            }
        }).addOnFailureListener(e -> {
            callback.onDoctorsCallback(new String[]{});
        });
    }



    // CRUD - create
    public void add(Hospital hospital, HospitalCallback callback) {
        // set next appointment of the hospital
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hospital.setNextApp(LocalDate.now().toString());
        }

        Hospital onlyNameHospital = new Hospital();
        onlyNameHospital.setName(hospital.getName());
        // check if there is a hospital with the same name
        find(onlyNameHospital, new HospitalCallback() {
            @Override
            public void onCallback(List<Hospital> hospitalList) {
                if (hospitalList != null && !hospitalList.isEmpty()) {
                    callback.onCallback(new ArrayList<>());
                    return;
                }

                // there is no hospital with the same name
                hospitalRef.add(hospital)
                        .addOnSuccessListener(documentReference -> {
                            List<Hospital> result = new ArrayList<>();
                            result.add(hospital);
                            callback.onCallback(result);
                        })
                        .addOnFailureListener(e -> {
                            callback.onCallback(new ArrayList<>());
                        });
            }

            @Override
            public void onDoctorsCallback(String[] doctors) {

            }

            @Override
            public void onDoctorCountCallback(int count) {

            }
        });

        //TODO: addOnSuccessListener
    }

    // CRUD - delete
    public void delete(Hospital hospital, HospitalCallback callback) {
        if (hospital.getName() != null) {
            Log.d("delete method", "meghívódik a hospitalDAO " + hospital.getName());

            hospitalRef.whereEqualTo("name", hospital.getName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (var doc : queryDocumentSnapshots) {
                                doc.getReference().delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("delete success", "Sikeresen törölted a kórházat: " + hospital.getName());
                                            List<Hospital> deletedHospitalList = new ArrayList<>();
                                            deletedHospitalList.add(hospital);
                                            callback.onCallback(deletedHospitalList);
                                        })
                                        .addOnFailureListener(e -> {
                                            callback.onCallback(new ArrayList<>());
                                        });
                                break;
                            }
                        } else {
                            callback.onCallback(new ArrayList<>());
                        }
                    })
                    .addOnFailureListener(e -> {
                        callback.onCallback(new ArrayList<>());
                    });
        } else {
            callback.onCallback(new ArrayList<>());
        }
    }

    // ÖSSZETETT LEKÉRDEZÉS!!
    // filters the hospitals that have appointment in the next 10 days
    public void filterByDate( HospitalCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Query query = hospitalRef
                    .whereGreaterThan("nextApp", LocalDate.now().toString())
                    .whereLessThan("nextApp", LocalDate.now().plusDays(10).toString())
                    .orderBy("nextApp")
                    .limit(5);

            query.get().addOnSuccessListener(queryDocumentSnapshots ->
            {
                List<Hospital> hospitals = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Hospital h = doc.toObject(Hospital.class);
                    hospitals.add(h);
                }
                callback.onCallback(hospitals);
            }).addOnFailureListener(e -> {
                //TODO: ERROR
            });
        }

    }

    // ÖSSZETETT lekérdezés, mert van benne orderBy
    // CRUD - read
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
        query = query.orderBy("nextApp");

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

    // CRUD - update
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

