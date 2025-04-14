package com.example.appointmentapp.activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appointmentapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HospitalsActivity extends AppCompatActivity {

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital_activity);


        user = FirebaseAuth.getInstance().getCurrentUser();

        // if there is no user logged in then
        // close tha activity
        if(user == null) {
            finish();
        }




    }
}
