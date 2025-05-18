package com.example.appointmentapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appointmentapp.R;
import com.example.appointmentapp.adapter.HospitalAdapter;
import com.example.appointmentapp.dao.HospitalDAO;
import com.example.appointmentapp.model.Hospital;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class BookAppActivity extends AppCompatActivity {
    private FirebaseUser user;
    private HospitalAdapter hospitalAdapter;
    private final HospitalDAO hospitalDAO = new HospitalDAO();
    private Bundle intentExtras;

    private TextView tvHospitalName;
    private TextView tvNextAppointment;
    private RadioGroup rdDoctors;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_app_activity);

        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvNextAppointment = findViewById(R.id.tvNextAppointment);
        rdDoctors = findViewById(R.id.rgDoctors);


        intentExtras = getIntent().getExtras();
        if(intentExtras != null) {
            String hospital_name = intentExtras.getString("hospital_name");
            List<String> doctors = Arrays.asList(intentExtras.getStringArray("doctors"));
            String nextApp = intentExtras.getString("nextApp");
            tvHospitalName.setText(hospital_name);
            tvNextAppointment.setText(nextApp);
            for(String doctor : doctors) {
                RadioButton rb = new RadioButton(this);
                rb.setText(doctor);
                rdDoctors.addView(rb);
            }
        }
    }

    public void goToHospitalActivity(View view) {
        Intent regIntent = new Intent(getApplicationContext(), HospitalActivity.class);
        startActivity(regIntent);
        overridePendingTransition(R.anim.from_left, R.anim.from_right);
        finish();
    }

    public void bookAppointment(View view) {
        LocalDate date;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.parse(tvNextAppointment.getText());
            date = date.plusDays(1);
            tvNextAppointment.setText(date.toString());
            Hospital currentHospital = new Hospital();
            currentHospital.setName(tvHospitalName.getText().toString());
            currentHospital.setNextApp(date.toString());
            hospitalDAO.updateAppointment(currentHospital);
        }

    }
}
