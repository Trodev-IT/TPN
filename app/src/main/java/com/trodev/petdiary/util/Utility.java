package com.trodev.petdiary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("SimpleDateFormat")
    public  static String timestampToString(long timestamp){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:aa dd/MM/yyyy", Locale.getDefault());

        // Create a Date object from the timestamp
        Date date = new Date(timestamp);

        // Format the Date object to a string
        return sdf.format(date);

    }

}

