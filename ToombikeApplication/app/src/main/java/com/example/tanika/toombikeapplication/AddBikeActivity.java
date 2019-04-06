package com.example.tanika.toombikeapplication;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AddBikeActivity extends AppCompatActivity {



    public Spinner spinnerBrand,spinnerModel,spinnerColors;
    Firebase mRootRef;
    EditText bikeSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button cancel = (Button) findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button create = (Button) findViewById(R.id.createBtn);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                String uid = currentFirebaseUser.getUid().toString();
              try{
                  mRootRef = new Firebase("https://toombike-41fdf.firebaseio.com/userBike");

                  String bikeBrand = spinnerBrand.getSelectedItem().toString();
                  String bikeModel = spinnerModel.getSelectedItem().toString();
                  String bikeColor = spinnerColors.getSelectedItem().toString();
                  String bikeSigndata = bikeSign.getText().toString();
                  String bikeNo ;

                  String keyPush = mRootRef.child(uid).push().getKey();
                  Firebase childRef = mRootRef.child(uid).child(keyPush);
                  childRef.child("brand").setValue(bikeBrand);
                  childRef.child("model").setValue(bikeModel);
                  childRef.child("color").setValue(bikeColor);
                  childRef.child("sign").setValue(bikeSigndata);


                  Log.d("KEYPUSH11111",keyPush);


                  Toast.makeText(AddBikeActivity.this, "Add bike successfully", Toast.LENGTH_SHORT).show();
                  bikeSign.setText("");
                  //Toast.makeText(AddBikeActivity.this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
                  finish();
              }catch (Exception e){
                  Toast.makeText(AddBikeActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();

              }
            }
        });

        spinnerBrand = (Spinner)findViewById(R.id.spinnerBrand);
        spinnerModel =(Spinner)findViewById(R.id.spinnerModel);
        spinnerColors = (Spinner)findViewById(R.id.spinnerColor);
        bikeSign = (EditText)findViewById(R.id.bikeSign);



        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String spinnerValue = spinnerBrand.getSelectedItem().toString();
                if (spinnerValue.equals("HONDA")) {
                    ArrayAdapter<CharSequence> selectHonda =  ArrayAdapter
                            .createFromResource(AddBikeActivity.this, R.array.honda,
                                    android.R.layout.simple_spinner_dropdown_item);
                    spinnerModel.setAdapter(selectHonda);
                }else if (spinnerValue.equals("YAMAHA")){
                    ArrayAdapter<CharSequence> selectYamaha =  ArrayAdapter
                            .createFromResource(AddBikeActivity.this, R.array.yamaha,
                                    android.R.layout.simple_spinner_dropdown_item);
                    spinnerModel.setAdapter(selectYamaha);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

       // databaseBike = FirebaseDatabase.getInstance().getReference("usersBike");

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        // perform your action here

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
