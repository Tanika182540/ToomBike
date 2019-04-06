package com.example.tanika.toombikeapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CallService extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,TaskLoadedCallback {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation ;
    private Marker currentUserLocationMarker ,toomLocation,srchLocation;
    private  static final  int Request_User_Location_Code = 99;
    private  static final  int REQUEST_CALL = 1;
    private ArrayList listPoint;
    private Button cancelService, newPinLocation,editService,saveEdtData,resetPin;
    private TextView address;
    private LinearLayout view1;
    private String strLat ,strLng,phone,userName,sign,bikeModel,trueLat, trueLng, detail, hisNum;
    private Double lLat, lLong;
    private Double latSrch, lngSrch ;
    private LatLng latLngToom;
    private EditText inTxt;
    private static final String TAG = "CallService";
    private Polyline polyline;
    private RequestQueue queue;
    private Firebase mRootRef;



    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_service);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hisNum = getIntent().getStringExtra("hisNum");
        cancelService = (Button)findViewById(R.id.cancelService);
        newPinLocation = (Button)findViewById(R.id.pinLocationEdt);
        editService = (Button)findViewById(R.id.edtService);
        saveEdtData = (Button)findViewById(R.id.saveEdtService);
        address = (TextView) findViewById(R.id.newPinLocationEdt);
        view1 = (LinearLayout)findViewById(R.id.edtView1);
        inTxt = (EditText)findViewById(R.id.inputSearchEdt);
        resetPin = (Button)findViewById(R.id.resetEditMap);

        editService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setVisibility(View.VISIBLE);
                editService.setVisibility(View.GONE);

            }
        });

        newPinLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPinLocation();
                saveEdtData.setVisibility(View.VISIBLE);
                resetPin.setVisibility(View.VISIBLE);
            }
        });

        resetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        saveEdtData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendEditLocation();
            }
        });

        if (queue == null){
            queue = Volley.newRequestQueue(CallService.this);
        }

        cancelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelService();
            }
        });
    }

    private void cancelService() {
        String url = "https://toombike.kku.ac.th/delete/history?his_num=" + hisNum;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        pushNotiDelete();
                        Intent intent = new Intent(CallService.this, BottomNavMenu.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);
        /*
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("his_num", hisNum);
        } catch (Exception e) {
            e.printStackTrace();

        }

        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("onResponse", response.toString());
                Toast.makeText(CallService.this,"ลบให้ละเด้อจ้าเตงงงง",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CallService.this, BottomNavMenu.class);
                startActivity(intent);
                finish();
                pushNotiDelete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String> ();
                headers.put("X-HTTP-Method-Override", "DELETE");
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");

                return headers;
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                String json;
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    try {
                        json = new String(volleyError.networkResponse.data,
                                HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                    } catch (UnsupportedEncodingException e) {
                        return new VolleyError(e.getMessage());
                    }
                    return new VolleyError(json);
                }
                return volleyError;
            }
        };
        queue.add(deleteRequest);*/
    }


    private void pushNotiDelete() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String uid = currentFirebaseUser.getUid().toString();

        try{
            mRootRef = new Firebase("https://toombike-41fdf.firebaseio.com/notification");

            String keyPush = mRootRef.child(uid).push().getKey();
            Firebase childRef = mRootRef.child(keyPush);
            childRef.child("uid").setValue(keyPush);
            childRef.child("textNoti").setValue("การแจ้งซ่อมรหัส " + hisNum + " ถูกยกเลิกแล้ว");
            childRef.child("read").setValue("false");
            childRef.child("hisNum").setValue(hisNum);

            Log.d("KEYPUSH11111",keyPush);
            Toast.makeText(CallService.this,"ยกเลิกการแจ้งซ่อมแล้ว",Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(CallService.this, "Error!!!", Toast.LENGTH_SHORT).show();

        }
    }

    private void pushNotiEdit() {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String uid = currentFirebaseUser.getUid().toString();

        try{
            mRootRef = new Firebase("https://toombike-41fdf.firebaseio.com/notification");

            String keyPush = mRootRef.child(uid).push().getKey();
            Firebase childRef = mRootRef.child(keyPush);
            childRef.child("uid").setValue(keyPush);
            childRef.child("textNoti").setValue("การแจ้งซ่อมรหัส " + hisNum + " มีการเปลี่ยนแปลงตำแหน่งพิกัดบนแผนที่");
            childRef.child("location").child("lat").setValue(trueLat);
            childRef.child("location").child("lng").setValue(trueLng);
            childRef.child("read").setValue("false");
            childRef.child("hisNum").setValue(hisNum);

            Log.d("KEYPUSH11111",keyPush);
            Toast.makeText(CallService.this,"ได้รับการแจ้งเตือนแล้ว",Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(CallService.this, "Error!!!", Toast.LENGTH_SHORT).show();

        }
    }

    private void sendEditLocation() {
        String url = "https://toombike.kku.ac.th/put/history/";
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(CallService.this,"แก้ไขเรียบร้อย",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CallService.this, BottomNavMenu.class);
                        startActivity(intent);
                        finish();
                        pushNotiEdit();
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
                params.put("his_num", hisNum);
                params.put("lat", trueLat);
                params.put("lng",trueLng);

                return params;
            }

        };

        queue.add(putRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if (googleApiClient == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this,"Permission Denied...",Toast.LENGTH_LONG).show();
                }
        }
    }

    protected  synchronized  void buildGoogleApiClient(){

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
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

        Geocoder myLocation = new Geocoder(CallService.this, Locale.getDefault());
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
        new FetchUrl(CallService.this).execute(url,"location");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void clickPinLocation() {

        if(!latSrch.equals(null) && !lngSrch.equals(null) ){
            strLat = String.valueOf(latSrch);
            strLng = String.valueOf(lngSrch);

            trueLat = strLat;
            trueLng = strLng;

            Geocoder myLocation = new Geocoder(CallService.this, Locale.getDefault());
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
                new FetchUrl(CallService.this).execute(url,"location");


            }catch (Exception e){
                Toast.makeText(CallService.this,"กรุณาค้นหาสถานที่เพื่อปักหมุด หรือเลือกปักหมุดด้วยตำแหน่งปัจจุบัน CCCCCC",Toast.LENGTH_LONG).show();
            }
        }else {
            //lat.setText(strLat);
            //lng.setText(strLng);
        }

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
                    newPinLocation.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG,"geoLocate: geolocating");
        String searchString = inTxt.getText().toString();
        Geocoder geocoder = new Geocoder(CallService.this);

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e(TAG,"geoLocate: IOException: "+ e.getMessage());

        }

        Geocoder myLocation = new Geocoder(CallService.this, Locale.getDefault());
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



