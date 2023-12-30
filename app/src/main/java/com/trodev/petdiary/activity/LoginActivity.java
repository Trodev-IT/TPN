package com.trodev.petdiary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.trodev.petdiary.R;
import com.trodev.petdiary.util.Utility;

public class LoginActivity extends AppCompatActivity {

    TextView BottomRegisterNow_txt;
    Button B_SignIn;
    TextInputEditText edEmail, edPass;
    TextInputLayout passContainer, emailContainer;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        BottomRegisterNow_txt = findViewById(R.id.BottomRegisterNow_txt);
        B_SignIn = findViewById(R.id.B_SignIn);

        progressbar = findViewById(R.id.progressbar);

        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);

        passContainer = findViewById(R.id.passContainer);
        emailContainer = findViewById(R.id.emailContainer);


        BottomRegisterNow_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUp.class));
                finish();
            }
        });

        B_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }//-----------onCreate end here ----------------------------------------------------

    private void loginUser() {

        String email = edEmail.getText().toString();
        String password = edPass.getText().toString();


        boolean isValidated = validateData(email, password);

        if (!isValidated) {
            return;
        }

        loginAccountInFirebase(email, password);

    }

    void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                changeInProgress(false);
                if (task.isSuccessful()) {
                    //login is successful

                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        //go to activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    //login failed
                    Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                }

            }
        });
    }


    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressbar.setVisibility(View.VISIBLE);
            B_SignIn.setVisibility(View.GONE);
        } else {
            progressbar.setVisibility(View.GONE);
            B_SignIn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String pass) {

        //validate the data that are input by user
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Email is invalid");
            return false;
        }

        if (pass.length() == 0) {
            edPass.setError("Enter password");
            return false;
        }

        return true;
    }

}

