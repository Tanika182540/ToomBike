package com.example.tanika.toombikeapplication;

import android.app.Activity;
import android.app.Application;
import android.widget.ArrayAdapter;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class AddBikeAdapter extends Application{

    @Override
    public void onCreate() {

        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}

