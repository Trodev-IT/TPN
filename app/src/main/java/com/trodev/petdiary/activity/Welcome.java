package com.trodev.petdiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trodev.petdiary.R;

public class Welcome extends AppCompatActivity {

    Button Start;
    LottieAnimationView animationView;
    Animation Top_anim, Bottom_anim, Left_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        Start = findViewById(R.id.Start);
        Top_anim = AnimationUtils.loadAnimation(this, R.anim.topanim);
        Bottom_anim = AnimationUtils.loadAnimation(this, R.anim.bottomanim);
        Left_anim = AnimationUtils.loadAnimation(this, R.anim.leftanim);

        Start.setAnimation(Bottom_anim);


        Start.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                startActivity(new Intent(Welcome.this, SignUp.class));
            } else {
                startActivity(new Intent(Welcome.this, MainActivity.class));
            }


        });


    }
}