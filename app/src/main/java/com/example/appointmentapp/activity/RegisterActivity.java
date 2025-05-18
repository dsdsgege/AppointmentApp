package com.example.appointmentapp.activity;


import static com.example.appointmentapp.utils.Dialog.createDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appointmentapp.R;
import com.example.appointmentapp.utils.FormatString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText emailEditText, regTajEditText, regPassEditText, regPassEditTextAgain;
    String email, taj, pass, passAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        emailEditText = findViewById(R.id.emailEditText);
        regTajEditText = findViewById(R.id.regTajEditText);
        regPassEditText = findViewById(R.id.regPassEditText);
        regPassEditTextAgain = findViewById(R.id.regPassEditTextAgain);

        // make the taj edit text XXX-XXX-XXX format
        FormatString.makeTajFormat(regTajEditText);

        firebaseAuth = FirebaseAuth.getInstance();
    }



    // Button for switching to activity_login.xml
    public void goToLogButton(View view) {
        Intent logIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(logIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public void executeRegButton(View view) {
        email = emailEditText.getText().toString();
        taj = regTajEditText.getText().toString();
        pass = regPassEditText.getText().toString();
        passAgain = regPassEditTextAgain.getText().toString();

        // checking for emptiness if anything is empty
        // show a dialog about it and bring the focus on
        // the empty component
        if(taj.isEmpty()) {
            createDialog(this,"Taj-szám  megadása kötelező", "Megadom").show();
            regTajEditText.requestFocus();
            return;
        } else if(email.isEmpty()) {
            createDialog(this,"Email cím megadása kötelező", "Megadom").show();
            emailEditText.requestFocus();
            return;
        } else if(pass.isEmpty()) {
            createDialog(this,"Jelszó megadása kötelező", "Megadom").show();
            regPassEditText.requestFocus();
            return;
        } else if(passAgain.isEmpty()) {
            createDialog(this,"Mindkét jelszó megadása kötelező", "Megadom").show();
            regPassEditTextAgain.requestFocus();
            return;
        }

        if(pass.equals(passAgain)) {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,"Sikeres regisztráció", Toast.LENGTH_LONG).show();

                        // -- register other datas to the database
                        // initialize userId and db
                        if(firebaseAuth.getCurrentUser() != null) {
                            String userId = firebaseAuth.getCurrentUser().getUid();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // making a map that store the values
                            Map<String, Object> user = new HashMap<>();
                            user.put("taj", taj);
                            user.put("email", email);
                            // adding the data to the db
                            // event listeners if it was successful or not
                            db.collection("user").document(userId).set(user)
                                    .addOnSuccessListener(success -> {
                                        Toast.makeText(RegisterActivity.this, "Sikeresen elmentettük a taj számod", Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(failure -> {
                                        Toast.makeText(RegisterActivity.this, "Nem sikerült elmenteni a taj számod", Toast.LENGTH_LONG).show();
                                        //Log.e("FIRESTORE_ERROR", "Hiba: ", failure);
                                    });
                        }
                        Intent hospitalIntent = new Intent(getApplicationContext(), HospitalActivity.class);
                        startActivity(hospitalIntent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "A regisztráció sikertelen: " + task.getException().getMessage() ,Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            // show dialog
            AlertDialog alertDialog = createDialog(this, "A két jelszó nem egyezik!", "Próbáld újra");
            alertDialog.show();
        }
    }
}
