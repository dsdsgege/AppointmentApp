package com.example.appointmentapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.appointmentapp.R;
import com.example.appointmentapp.adapter.HospitalAdapter;
import com.example.appointmentapp.dao.HospitalDAO;
import com.example.appointmentapp.model.Hospital;
import com.example.appointmentapp.utils.AddHospitalDialogFragment;
import com.example.appointmentapp.utils.Dialog;
import com.example.appointmentapp.utils.FireBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HospitalActivity extends AppCompatActivity {

    private FirebaseUser user;
    private RecyclerView recyclerView;
    private List<Hospital> hospitalList;
    private HospitalAdapter hospitalAdapter;
    private int gridNumber = 1;
    private final HospitalDAO hospitalDAO = new HospitalDAO();
    private ListenerRegistration listenerRegistration;
    private Context context = HospitalActivity.this;

    private void initDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("hospital")
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot != null && !snapshot.isEmpty()) {
                        // if there is data return
                        return;
                    }

                    String[] hospitalNames = getResources().getStringArray(R.array.hospital_names);
                    String[] appointments = getResources().getStringArray(R.array.appointments);
                    List<String> doctors1 = Arrays.asList(getResources().getStringArray(R.array.doctors1));
                    List<String> doctors2 = Arrays.asList(getResources().getStringArray(R.array.doctors2));

                    for (int i = 0; i < hospitalNames.length; i++) {
                        Hospital h = new Hospital();
                        h.setName(hospitalNames[i]);
                        h.setNextApp(appointments[i]);
                        h.setDoctors(i % 2 == 0 ? doctors1 : doctors2);
                        db.collection("hospital").add(h);
                    }
                    listenerRegistration = db.collection("hospital")
                            .addSnapshotListener((snapshots, e) -> {
                                if (e != null) {
                                    return;
                                }

                                if (snapshots != null && !snapshots.isEmpty()) {
                                    // Update UI with the new data
                                    List<Hospital> hospitals = new ArrayList<>();
                                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                        Hospital hospital = doc.toObject(Hospital.class);
                                        if (hospital != null) {
                                            hospitals.add(hospital);
                                        }
                                    }
                                    Log.d("Firestore", "Hospitals size: " + hospitals.size());
                                    hospitalAdapter.setHospitalList(hospitals);
                                    runOnUiThread(() -> hospitalAdapter.notifyDataSetChanged());
                                }
                            });
                });

    }

    // lifecycle hook :)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) listenerRegistration.remove();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatabase();
        setContentView(R.layout.hospital_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getColor(R.color.lightyellow));
        setSupportActionBar(toolbar);


        user = FirebaseAuth.getInstance().getCurrentUser();
        // if there is no user logged in then
        // close tha activity
        if(user == null) {
            finish();
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        hospitalList = new ArrayList<>();
        hospitalAdapter = new HospitalAdapter(this, hospitalList);
        recyclerView.setAdapter(hospitalAdapter);


        initalizeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.hospital_menu, menu);
        MenuItem  menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        hospitalAdapter.getFilter().filter(s);
                        return true;
                    }
                }
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.newHospital) {
            AddHospitalDialogFragment dialog = AddHospitalDialogFragment.getInstance();
            dialog.show(getSupportFragmentManager(), "AddHospitalDialog");
            return true;
        } else if(item.getItemId() == R.id.inTenDays) {
            hospitalDAO.filterByDate(new HospitalDAO.HospitalCallback() {
                @Override
                public void onCallback(List<Hospital> hospitalList) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (var h : hospitalList) {
                        stringBuilder.append(h.getName()).append(", ");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 2);
                    Dialog.createDialog(context,"A következő " + hospitalList.size() + " kórházban van 10 napon belül időpont: " + stringBuilder.toString().trim(), "Rendben").show();
                }

                @Override
                public void onDoctorsCallback(String[] doctors) {

                }
                @Override
                public void onDoctorCountCallback(int count) {

                }
            });
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void initalizeData() {
        hospitalList.clear();
        hospitalAdapter.notifyDataSetChanged();

        Hospital hospital = new Hospital();
        hospitalDAO.find(hospital, new HospitalDAO.HospitalCallback() {
            @Override
            public void onCallback(List<Hospital> result) {
                hospitalList.addAll(result);
                hospitalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDoctorsCallback(String[] doctors) {

            }
            @Override
            public void onDoctorCountCallback(int count) {

            }
        });
    }

}
