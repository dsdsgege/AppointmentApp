package com.example.appointmentapp.activites;

import static com.example.appointmentapp.utils.Dialog.createDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText logEmailEditText, logPassEditText;
    Button logButton, goToRegButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logEmailEditText = findViewById(R.id.logEmailEditText);
        logPassEditText = findViewById(R.id.logPassEditText);
        logButton = findViewById(R.id.logButton);
        goToRegButton = findViewById(R.id.goToRegButton);

        // get the FirebaseAuth singleton object
        firebaseAuth = FirebaseAuth.getInstance();

        // -- making the button and Edit Text half the screen size
        // get the width of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // calculate 50% of screen
        int halfScreenWidth = (int) (screenWidth * 0.5);


        // set the width
        int marginInDp = 15;
        int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);

        ViewGroup.LayoutParams paramsMail = logEmailEditText.getLayoutParams();
        paramsMail.width = halfScreenWidth - (2 * marginInPx);
        logEmailEditText.setLayoutParams(paramsMail);

        ViewGroup.LayoutParams paramsPass = logPassEditText.getLayoutParams();
        paramsPass.width = halfScreenWidth - (2 * marginInPx);
        logPassEditText.setLayoutParams(paramsPass);

        ViewGroup.LayoutParams paramsLogBtn = logButton.getLayoutParams();
        paramsLogBtn.width = halfScreenWidth - (2 * marginInPx);
        logButton.setLayoutParams(paramsLogBtn);

        ViewGroup.LayoutParams paramsRegBtn = goToRegButton.getLayoutParams();
        paramsRegBtn.width = halfScreenWidth - (2 * marginInPx);
        goToRegButton.setLayoutParams(paramsRegBtn);
    }

    // Button for switching to activity_regisert.xml
    public void goToReg(View view) {
        Intent regIntent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(regIntent);
        overridePendingTransition(R.anim.from_left, R.anim.from_right);
        finish();
    }

    public void executeLogButton(View view) {
        String email = logEmailEditText.getText().toString();
        String pass = logPassEditText.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Sikeres bejelentkezés", Toast.LENGTH_LONG);

                        } else {
                            // show dialog
                            AlertDialog alertDialog = createDialog(LoginActivity.this, "Nem megfelelő jelszó vagy e-mail cím", "Próbáld újra");
                            alertDialog.show();
                        }
                    }
                }
        );
    }

    public void googleLoginButton(View view) {
    }
}