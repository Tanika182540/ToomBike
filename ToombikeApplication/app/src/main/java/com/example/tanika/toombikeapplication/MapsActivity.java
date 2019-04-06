package com.example.tanika.toombikeapplication;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,TaskLoadedCallback {

    private static final String TAG = "MapActivity" ;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker,toomLocation, srchLocation;
    private static final int Request_User_Location_Code = 99;


    private Button callService , pinLocation , reset ;
    private TextView name,phoneNum, bikeSign, model, address,detailText;
    private String strLat ,strLng,phone,userName,sign,bikeModel,trueLat, trueLng, detail;
    private Double lLat, lLong;
    private Double latSrch, lngSrch ;
    private EditText inTxt;
    private Polyline polyline;
    private LatLng latLngToom;
    private Context mContext;
    private Firebase mRootRef;
    private NotificationManagerCompat compat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        callService = (Button)findViewById(R.id.callServiceBtn);
        address = (TextView)findViewById(R.id.address);
        detailText = (TextView)findViewById(R.id.detailText);
        callService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(address.getText() == ""){
                    Toast.makeText(MapsActivity.this,"กรุณาค้นหาสถานที่เพื่อปักหมุด หรือเลือกปักหมุดด้วยตำแหน่งปัจจุบัน",Toast.LENGTH_LONG).show();
                }else {

                    Intent intent = new Intent(MapsActivity.this,
                            BottomNavMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                    pushNoti();
                }
            }
        });


        reset = (Button)findViewById(R.id.rstBtn);
        name = (TextView)findViewById(R.id.nameTxt);
        phoneNum = (TextView)findViewById(R.id.phoneTxt);
        bikeSign = (TextView)findViewById(R.id.signTxt);
        model = (TextView)findViewById(R.id.modelTxt);

        pinLocation = (Button)findViewById(R.id.pinLocation);
        pinLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPinLocation();

            }
        });

        inTxt = (EditText)findViewById(R.id.inputSearch);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phoneNum");
        userName = intent.getStringExtra("name");
        sign = intent.getStringExtra("bikeSign");
        bikeModel = intent.getStringExtra("bikeModel");
        detail = intent.getStringExtra("detail");

        phoneNum.setText(phone);
        name.setText(userName);
        bikeSign.setText(sign);
        model.setText(bikeModel);
        detailText.setText(detail);

        Log.d("dataActivity",phone+userName+sign+bikeModel);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        compat = NotificationManagerCompat.from(this);


    }

    private void pushNoti() {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String uid = currentFirebaseUser.getUid().toString();

        try{
            mRootRef = new Firebase("https://toombike-41fdf.firebaseio.com/notification");

            String keyPush = mRootRef.child(uid).push().getKey();
            Firebase childRef = mRootRef.child(keyPush);
            childRef.child("uid").setValue(keyPush);
            childRef.child("textNoti").setValue("มีการแจ้งซ่อมจาก " + phone + " อาการ " +detail);
            childRef.child("location").child("lat").setValue(trueLat);
            childRef.child("location").child("lng").setValue(trueLng);
            childRef.child("sign").setValue(sign);
            childRef.child("model").setValue(bikeModel);
            childRef.child("detail").setValue(detail);
            childRef.child("read").setValue("false");

            Log.d("KEYPUSH11111",keyPush);


            Toast.makeText(MapsActivity.this, "แจ้งซ่อมเรียบร้อย", Toast.LENGTH_SHORT).show();
            sendServiceData();
            //Toast.makeText(AddBikeActivity.this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
            finish();

        }catch (Exception e){
            Toast.makeText(MapsActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();

        }
    }

    private void showNotiInApp(View v) {
        String title = "การแจ้งซ่อม";
        Notification notification = new NotificationCompat.Builder(this,App.CH_1)
                .setSmallIcon(R.drawable.toomlogo1)
                .setContentTitle(title)
                .setContentText("การแจ้งซ่อมจักรยานยนต์ทะเบียน" + bikeSign + "จาก " + phoneNum + " ถูกแจ้งแล้ว")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        compat.notify(1,notification);
    }

    private void sendServiceData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String urlMap = "https://toombike.kku.ac.th/insert/history?phone="+ phone +"&number="+bikeSign.getText().toString()+"&lat="+trueLat + "&lng=" + trueLng + "&detail=" + detail;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlMap, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("ResponseText", response.toString() + "Complete ");
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("errorMaps",error.toString() + urlMap);
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);
/*
        final String urlPOST = "https://toombike.kku.ac.th/insert/history";
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPOST,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("ResponseDe", response + detail);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("phone", "0896235853");
                params.put("number", sign);
                params.put("lat",trueLat);
                params.put("lng",trueLng);
                params.put("detail",detail);

                return params;
            }
        };
        queue.add(postRequest);*/
    }

    private void clickPinLocation() {

        if(!latSrch.equals(null) && !lngSrch.equals(null) ){
            strLat = String.valueOf(latSrch);
            strLng = String.valueOf(lngSrch);

            trueLat = strLat;
            trueLng = strLng;

            Geocoder myLocation = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> myList = null;
            try {
                myList = myLocation.getFromLocation(Double.parseDouble(strLat),Double.parseDouble(strLng), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address addressName = (Address) myList.get(0);
            String addressStr = "";
            addressStr += addressName.getAddressLine(0);
            address.setText("");
            address.setText(addressStr.toString());

            try{
                if(latSrch != 0 || lngSrch != 0){
                    srchLocation.remove();
                }
            }catch (Exception e){

            }

            try {
                currentUserLocationMarker.remove();
                polyline.remove();

                LatLng latLngSrch = new LatLng(latSrch,lngSrch);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLngSrch);
                String input = inTxt.getText().toString();
                markerOptions.title(input);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                srchLocation = mMap.addMarker(markerOptions);
                srchLocation.showInfoWindow();


                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(latLngToom);
                markerOptions1.title("ตุ่มมอไซค์");
                markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                polyline = mMap.addPolyline(new PolylineOptions()
                        .add(latLngSrch)
                        .add(latLngToom)
                        .width(8f)
                        .color(R.color.red)
                );

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                //the include method will calculate the min and max bound.
                builder.include(srchLocation.getPosition());
                builder.include(toomLocation.getPosition());

                LatLngBounds bounds = builder.build();

                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.40); // offset from edges of the map 10% of screen

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                mMap.animateCamera(cu);

                String url = getUrl(markerOptions.getPosition(),markerOptions1.getPosition(),"location");
                new FetchUrl(MapsActivity.this).execute(url,"location");


            }catch (Exception e){
                Toast.makeText(MapsActivity.this,"กรุณาค้นหาสถานที่เพื่อปักหมุด หรือเลือกปักหมุดด้วยตำแหน่งปัจจุบัน",Toast.LENGTH_LONG).show();
            }
        }else {
            //lat.setText(strLat);
            //lng.setText(strLng);
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);

        }

        init();

    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            } return false;


            }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if (googleApiClient == null){
                            buildGoogleApiClient();
                        }

                        mMap.setMyLocationEnabled(true);
                    }
                }else{

                    Toast.makeText(this,"permission Denied...",Toast.LENGTH_LONG).show();

                }return;
        }
    }

    protected synchronized  void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if(currentUserLocationMarker != null){
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("คุณอยู่ที่นี่");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currentUserLocationMarker = mMap.addMarker(markerOptions);
        currentUserLocationMarker.showInfoWindow();

        lLat = location.getLatitude();
        lLong = location.getLongitude();

        //lat.setText(String.valueOf(lLat));
        //lng.setText(String.valueOf(lLong));

        strLat = String.valueOf(lLat);
        strLng = String.valueOf(lLong);

        trueLat = strLat;
        trueLng = strLng;

        Geocoder myLocation = new Geocoder(MapsActivity.this, Locale.getDefault());
        List<Address> myList = null;
        try {
            myList = myLocation.getFromLocation(Double.parseDouble(strLat),Double.parseDouble(strLng), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address addressName = (Address) myList.get(0);
        String addressStr = "";
        addressStr += addressName.getAddressLine(0);

        address.setText(addressStr);

        latLngToom = new LatLng(16.464286,102.829843);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(latLngToom);
        markerOptions1.title("ตุ่มมอไซค์");
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        currentUserLocationMarker = mMap.addMarker(markerOptions);
        toomLocation = mMap.addMarker(markerOptions1);
        toomLocation.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.tlt64x64raz));


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //the include method will calculate the min and max bound.
        builder.include(currentUserLocationMarker.getPosition());
        builder.include(toomLocation.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.40); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
        polyline = mMap.addPolyline(new PolylineOptions()
                .add(latLng)
                .add(latLngToom)
                .width(8f)
                .color(R.color.red)
        );

        mMap.isTrafficEnabled();

        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));*/

        if (googleApiClient != null){

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

        String url = getUrl(markerOptions.getPosition(),markerOptions1.getPosition(),"location");
        new FetchUrl(MapsActivity.this).execute(url,"location");
    }

    private String getUrl(LatLng position, LatLng position1, String location) {

       String strOrigin = "origin=" + position.latitude + "," + position.longitude ;
       String strDest = "destination=" + position1.latitude + "," + position1.longitude;
       String mode = "mode=" + location;
       String param = strOrigin + "&" + strDest + "&" + mode;
       String output = "json";
       String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" +getString(R.string.google_maps_key);
       return url;
    }

    private void init(){
        Log.d(TAG,"init: initializing");

        inTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId ==EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching
                    geoLocate();
                    pinLocation.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    private void geoLocate(){
     Log.d(TAG,"geoLocate: geolocating");
     String searchString = inTxt.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e(TAG,"geoLocate: IOException: "+ e.getMessage());

        }

        Geocoder myLocation = new Geocoder(MapsActivity.this, Locale.getDefault());
        List<Address> myList = null;
        try {
            myList = myLocation.getFromLocation(Double.parseDouble(strLat),Double.parseDouble(strLng), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(list.size() > 0){
            Address addressNamefind = list.get(0);

            latSrch = list.get(0).getLatitude();
            lngSrch = list.get(0).getLongitude();

            Log.d(TAG,"geoLocate: found a location: " + addressNamefind.toString());
            Log.d(TAG,"latlng is" + latSrch + " and " + lngSrch);



        }else {
            Toast.makeText(this,"ชื่อสถานที่ผิด หรือไม่มีสถานที่นี้ในแผนที่ กรุณาค้นหาสถานที่ใหม่",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if(polyline != null){
            polyline.remove();
            polyline = mMap.addPolyline((PolylineOptions) values[0]);
        }
    }

}
