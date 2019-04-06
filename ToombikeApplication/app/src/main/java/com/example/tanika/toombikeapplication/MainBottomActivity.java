package com.example.tanika.toombikeapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainBottomActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private  static final  int REQUEST_CALL = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            android.support.v4.app.Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.menuHistory:
                   selectedFragment = new HistoryFragment();
                    break;
                case R.id.menuMyBike:
                    selectedFragment = new MybikeFragment();
                    break;
                case R.id.menuAddBike:
                    selectedFragment = new AddBikeFragment();
                    break;
                case R.id.menuMap:
                    selectedFragment = new ChatFragment();
                    break;
                case R.id.menuUser:
                    selectedFragment = new ChatFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bottom);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.callToom);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }

    private void makePhoneCall() {
        String number = "+66641751965";
        if(number.trim().length()>0){
            if(ContextCompat.checkSelfPermission(MainBottomActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(MainBottomActivity.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }else {
                String dial = "tel:" + number ;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number));
                startActivity(intent);
            }
        }else {
            Toast.makeText(MainBottomActivity.this,"Valid Number",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }else {
                Toast.makeText(this,"Permission DENIED",Toast.LENGTH_LONG).show();
            }
        }
    }


}
