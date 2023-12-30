package com.trodev.petdiary.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trodev.petdiary.R;
import com.trodev.petdiary.model.User;
import com.trodev.petdiary.util.Utility;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    TextView Toplogin_txt, bottomSignin_Txt;
    Button B_register;
    TextInputEditText edName, edEmail, edPass, ed_confirm_pass;
    TextInputLayout nameContainer, passContainer, emailContainer, confirmPassContainer;
    ProgressBar progressbar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialization();

        bottomSignin_Txt.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, LoginActivity.class));
            finish();
        });

        B_register.setOnClickListener(v -> createAccount());

    }

    //----------------------- onCreate end here ---------------------------------------------------------------


    void initialization() {

        edEmail = findViewById(R.id.edEmail);
        edName = findViewById(R.id.edName);
        edPass = findViewById(R.id.edPass);
        ed_confirm_pass = findViewById(R.id.ed_confirm_pass);

        nameContainer = findViewById(R.id.nameContainer);
        passContainer = findViewById(R.id.passContainer);
        emailContainer = findViewById(R.id.emailContainer);
        confirmPassContainer = findViewById(R.id.confirmPassContainer);

        bottomSignin_Txt = findViewById(R.id.bottomSignin_Txt);
        B_register = findViewById(R.id.B_register);

        progressbar = findViewById(R.id.progressbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    void createAccount() {
        String name = edName.getText().toString();
        String email = edEmail.getText().toString();
        String password = edPass.getText().toString();
        String confirmPassword = ed_confirm_pass.getText().toString();

        boolean isValidated = validateData(name, email, password, confirmPassword);

        if (!isValidated) {
            return;
        }

        createAccountInFirebase(name, email, password);

    }

    void createAccountInFirebase(String name, String email, String password) {

        changeInProgress(true);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this,
                task -> {
                    changeInProgress(false);

                    if (task.isSuccessful()) {
                        Utility.showToast(SignUp.this, "!! Successfully create account !!");
                        // firebaseAuth.getCurrentUser().sendEmailVerification();

                        saveUserInfoToDatabase(name, email, password);

                        edName.setText(null);
                        edEmail.setText(null);
                        edPass.setText(null);
                        ed_confirm_pass.setText(null);
                        firebaseAuth.signOut();

                        startActivity(new Intent(SignUp.this, LoginActivity.class));
                    } else {
                        Utility.showToast(SignUp.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    }
                }
        );


    }


    void saveUserInfoToDatabase(String name, String email, String pass) {

        DatabaseReference userRef = databaseReference.child("UserInfo");

        String uID = firebaseAuth.getCurrentUser().getUid();

        User user = new User(uID, name, email, pass);

        userRef.child(uID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Utility.showToast(SignUp.this, "Registration Complete!!!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showToast(SignUp.this, Objects.requireNonNull(e.getMessage()));
            }
        });

    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressbar.setVisibility(View.VISIBLE);
            B_register.setVisibility(View.GONE);
        } else {
            progressbar.setVisibility(View.GONE);
            B_register.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String name, String email, String pass, String confirmPass) {
        //validate the data that are input by user
        if (name.isEmpty()) {
            edName.setError("Write your name");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Email is invalid");
            return false;
        }

        if (pass.length() < 6) {
            edPass.setError("Minimum 6 character password");
            return false;
        }

        if (!pass.matches(confirmPass)) {
            ed_confirm_pass.setError("Password not matched");
            return false;
        }


        return true;


    }

}