package com.example.appointmentapp.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.appointmentapp.R;
import com.example.appointmentapp.dao.HospitalDAO;
import com.example.appointmentapp.model.Hospital;

import java.util.Arrays;
import java.util.List;

public class AddHospitalDialogFragment extends DialogFragment {

    private EditText editTextHospitalName;
    private EditText editTextDoctors;
    private Button buttonAddHospital;
    private HospitalDAO hospitalDAO = new HospitalDAO();

    public static AddHospitalDialogFragment getInstance() {
        return new AddHospitalDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_hospital_dialog, null);

        editTextHospitalName = view.findViewById(R.id.editTextHospitalName);
        editTextDoctors = view.findViewById(R.id.editTextDoctors);
        buttonAddHospital = view.findViewById(R.id.addHospital);;
        buttonAddHospital.setBackgroundResource(R.drawable.rounded_edit_text_bg);

        // Set click listener for the add button
        buttonAddHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHospital();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void saveHospital() {
        String hospitalName = editTextHospitalName.getText().toString().trim();
        String doctors = editTextDoctors.getText().toString().trim();

        // validate input
        if (hospitalName.isEmpty()) {
            editTextHospitalName.setError("Kórház neve kötelező");
            return;
        }

        // save data to db
        Hospital hospital = new Hospital();
        hospital.setName(hospitalName);
        String[] doctorsArr = doctors.split(",");
        hospital.setDoctors(Arrays.asList(doctorsArr));
        hospitalDAO.add(hospital, new HospitalDAO.HospitalCallback() {
            @Override
            public void onCallback(List<Hospital> hospitalList) {

            }

            @Override
            public void onDoctorsCallback(String[] doctors) {

            }
            @Override
            public void onDoctorCountCallback(int count) {

            }
        });

        // show success message
        Toast.makeText(getContext(), "Kórház sikeresen hozzáadva: " + hospitalName, Toast.LENGTH_SHORT).show();

        // close the dialog
        dismiss();
    }
}
