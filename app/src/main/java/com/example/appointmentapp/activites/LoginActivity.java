package com.example.appointmentapp.activites;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;
import static com.example.appointmentapp.utils.Dialog.createDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appointmentapp.R;
import com.example.appointmentapp.utils.Dialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 776622;
    EditText logEmailEditText, logPassEditText;
    Button logButton, goToRegButton;
    ImageView icon;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignIn;



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

        // icon fade-in animation
        icon = findViewById(R.id.icon);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        icon.startAnimation(anim);

        // request to google for auth
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignIn = GoogleSignIn.getClient(this,gso);

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

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if(req == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
            } catch (ApiException e) {
                Dialog.createDialog(this, e.getMessage(), "Bezár");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Sikeres bejelentkezés", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this,HospitalsActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.from_left, R.anim.from_right);
                            finish();
                        } else {
                            System.out.println("not success");
                            // show dialog
                            AlertDialog alertDialog = createDialog(LoginActivity.this, "Nem megfelelő jelszó vagy e-mail cím", "Próbáld újra");
                            alertDialog.show();
                        }
                    }
                }
        );
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
        System.out.println("ez még lefut");
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Sikeres bejelentkezés", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this,HospitalsActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.from_left, R.anim.from_right);
                            finish();
                        } else {
                            System.out.println("not success");
                            // show dialog
                            AlertDialog alertDialog = createDialog(LoginActivity.this, "Nem megfelelő jelszó vagy e-mail cím", "Próbáld újra");
                            alertDialog.show();
                        }
                    }
                }
        );
    }

    public void googleLoginButton(View view) {
        Intent intent = googleSignIn.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }
}