package com.example.appointmentapp.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.appointmentapp.R;
import com.example.appointmentapp.adapter.HospitalAdapter;
import com.example.appointmentapp.dao.HospitalDAO;
import com.example.appointmentapp.model.Hospital;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class BookAppActivity extends AppCompatActivity {
    private FirebaseUser user;
    private HospitalAdapter hospitalAdapter;
    private final HospitalDAO hospitalDAO = new HospitalDAO();
    private Bundle intentExtras;

    private TextView tvHospitalName;
    private TextView tvNextAppointment;
    private RadioGroup rdDoctors;

    private static final int CALENDAR_PERMISSION_CODE = 100;

    private boolean hasCalendarPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCalendarPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                CALENDAR_PERMISSION_CODE);
    }

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
                rb.setTextColor(Color.parseColor("#FEE715"));
                rb.setPadding(10, 10, 10, 10);
                rb.setTypeface(rb.getTypeface(), Typeface.BOLD);

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

            if (!hasCalendarPermission()) {
                requestCalendarPermission();
                return;
            }

            ZonedDateTime startDateTime = ZonedDateTime.of(date, LocalTime.of(9,0), ZoneId.systemDefault());
            ZonedDateTime endDateTime = ZonedDateTime.of(date, LocalTime.of(12,0), ZoneId.systemDefault());

            long start = startDateTime.toInstant().toEpochMilli();
            long end = endDateTime.toInstant().toEpochMilli();

            // A TELEFON ALAP CALENDARJABA RAKJA AZ IDŐPONTOT, HA TÖBBET FOGLALSZ,
            // AKKOR A LEGUTOLSÓ FOGLALÁS NAPJÁNÁL LESZ OTT CSAK!

            // android erőforrás
            long calendarId = getPrimaryCalendarId();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.DTSTART,start);
            values.put(CalendarContract.Events.DTEND,end);
            values.put(CalendarContract.Events.TITLE, "Időpont: " + tvHospitalName.getText());
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);

            if(uri != null) {
                Toast.makeText(this, "Időpont hozzáadva a naptárhoz!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiba a naptárba íráskor!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private long getPrimaryCalendarId() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    CalendarContract.Calendars.CONTENT_URI,
                    new String[]{CalendarContract.Calendars._ID},
                    null, null, null
            );
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return 1;
    }


    // A VIBRALAS NEM MINDEN ESZKÖZÖN MŰKÖDÖTT, DE A LOGOK A VIBRÁLÁSNAK MEGFELELŐEN FUTNAK LE,
    // EZZEL LE TUDJÁTOK TESZTELNI, HOGY MŰKÖDIK
    public void deleteHospital(View view) {
        Log.d("tag", "meghívódik a deleteHospital");
        Hospital currentHospital = new Hospital();
        currentHospital.setName(tvHospitalName.getText().toString());
        hospitalDAO.delete(currentHospital, new HospitalDAO.HospitalCallback() {
            @Override
            public void onCallback(List<Hospital> hospitalList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            if (vibrator != null && vibrator.hasVibrator()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                                    Log.d("DeleteHospital", "Vibrálás megtörtént");
                                } else {
                                    vibrator.vibrate(1000);
                                    Log.d("DeleteHospital", "Vibrálás megtörtént");
                                }
                                Log.d("DeleteHospital", "Vibrálás megpróbálva.");
                            } else {
                                Log.w("DeleteHospital", "A vibrátor nem elérhető.");
                            }

                        Log.d("INTENT", "INTENT INDUL.");
                        Intent regIntent = new Intent(getApplicationContext(), HospitalActivity.class);
                            startActivity(regIntent);
                            overridePendingTransition(R.anim.from_left, R.anim.from_right);
                            finish();
                            Log.d("DeleteHospital", "Intent elindítva és finish() meghívva.");
                        }
                });
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
