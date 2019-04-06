package com.example.tanika.toombikeapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;


public class BottomNavMenu extends AppCompatActivity {

    String[] bike_info = {};

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private  static final  int REQUEST_CALL = 1;
    boolean doubleBackToExitPressedOnce = false;
    public String phoneNum, phoneSave;
    FirebaseAuth mAuth;
    public static final String PHONE_NUM_SAVE = "MyPhoneNumSave";
    public static final String PHONE_NUM = "phoneNum";
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav_menu);

        fragmentManager = getSupportFragmentManager();
        fragment = new HistoryFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_container, fragment).commit();

        try {
            Intent intent = getIntent();
            Log.d("pleum",intent.getStringExtra("flag"));

                if(intent.getStringExtra("flag").equals("flagsSetbike")){
                    fragment = new MybikeFragment();
                }else {
                    fragment = new UserFragment();
                }



            phoneNum = intent.getStringExtra("phoneNum");
            phoneSave = phoneNum;
            Log.d("puterr", "bottomNav"+phoneNum + phoneSave);

        }catch (Exception e){
            Log.d("puterr", e.toString());
        }
        //loadData();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem item) {
                            Bundle bundle;
                            switch (item.getItemId()) {
                                case R.id.menuHistory:
                                    fragment = new HistoryFragment();
                                    bundle = new Bundle();
                                    bundle.putString("phoneNum",phoneSave);
                                    //Log.d("phoneSave",phoneSave);
                                    fragment.setArguments(bundle);
                                    break;
                                case R.id.menuMyBike:
                                    fragment = new MybikeFragment();
                                    bundle = new Bundle();
                                    bundle.putString("phoneNum",phoneSave);
                                    fragment.setArguments(bundle);
                                    break;
                                case R.id.menuAddBike:
                                    fragment = new AddBikeFragment();
                                    Bundle data = new Bundle();//Use bundle to pass data
                                    data.putString("phoneNum", phoneSave);//put string, int, etc in bundle with a key value
                                    fragment.setArguments(data);//Finally set argument bundle to fragment

                                    break;
                                case R.id.menuMap:
                                    fragment = new MapFragment();
                                    break;
                                case R.id.menuUser:
                                    fragment = new UserFragment();
                                    bundle = new Bundle();
                                    bundle.putString("phoneNum",phoneSave);
                                    fragment.setArguments(bundle);

                            }
                            //final FragmentTransaction transaction = fragmentManager.beginTransaction();
                            //transaction.replace(R.id.main_container, fragment).addToBackStack(null).commit();
                            return loadFragment(fragment);
                            //return true;
                        }
                    });
        final LinearLayout callLayout = (LinearLayout)findViewById(R.id.callLayout);
        final LinearLayout mapLayout = (LinearLayout)findViewById(R.id.mapLayout);
        final FloatingActionButton moreDetail = (FloatingActionButton)findViewById(R
                .id.floatingActionButton2);

        final Animation mShowButton = AnimationUtils.loadAnimation(BottomNavMenu.this,R.anim.show_button);
        final Animation mHideButton = AnimationUtils.loadAnimation(BottomNavMenu.this,R.anim.hide_button);

        final Animation mShowLayout = AnimationUtils.loadAnimation(BottomNavMenu.this,R.anim.show_layout);
        final Animation mHideLayout = AnimationUtils.loadAnimation(BottomNavMenu.this,R.anim.hide_layout);

        moreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callLayout.getVisibility() == View.VISIBLE && mapLayout.getVisibility() == View.VISIBLE){
                    callLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.GONE);
                    callLayout.startAnimation(mHideLayout);
                    mapLayout.startAnimation(mHideLayout);
                    moreDetail.startAnimation(mHideButton);
                }else{
                    callLayout.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.VISIBLE);
                    callLayout.startAnimation(mShowLayout);
                    mapLayout.startAnimation(mShowLayout);
                    moreDetail.startAnimation(mShowButton);
                }
            }
        });
        final FloatingActionButton callToom = (FloatingActionButton) findViewById(R.id.callToom);
        callToom.setImageResource(R.drawable.ic_phone_green_24dp);
        callToom.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.iron)));
        callToom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makePhoneCall();
                callLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                callLayout.startAnimation(mHideLayout);
                mapLayout.startAnimation(mHideLayout);
                moreDetail.startAnimation(mHideButton);
            }
        });

        FloatingActionButton mapToom = (FloatingActionButton)findViewById(R.id.mapToom);
        mapToom.setImageResource(R.drawable.map1);
        mapToom.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.iron)));
        mapToom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                callLayout.startAnimation(mHideLayout);
                mapLayout.startAnimation(mHideLayout);
                moreDetail.startAnimation(mHideButton);
                Intent intent = new Intent(BottomNavMenu.this,
                        CallService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        settingData();

    }

    private void settingData() {
        if (phoneNum != null){
                saveData();
        }else {
            loadData();
        }
    }

    private void saveData() {
        SharedPreferences phoneNumberSave = getSharedPreferences(PHONE_NUM_SAVE,MODE_PRIVATE);
        SharedPreferences.Editor editor = phoneNumberSave.edit();

        editor.putString(PHONE_NUM,phoneSave);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(PHONE_NUM_SAVE,MODE_PRIVATE);
        phoneSave = sharedPreferences.getString(PHONE_NUM,"");
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){

            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();
            return true;
        }


        return false;
    }


    private void makePhoneCall() {
        String number = "+66641751965";
        if(number.trim().length()>0){
            if(ContextCompat.checkSelfPermission(BottomNavMenu.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(BottomNavMenu.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }else {
                String dial = "tel:" + number ;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" +number));
                startActivity(intent);
            }
        }else {
            Toast.makeText(BottomNavMenu.this,"Valid Number",Toast.LENGTH_LONG).show();
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


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
