package com.example.tanika.toombikeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tanika.toombikeapplication.Recycler.HistoryAdapter;
import com.example.tanika.toombikeapplication.Recycler.HistoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServiceDataActivity extends AppCompatActivity {

    Button editData , cancelService;
    static ArrayList<String> signList = new ArrayList<String>();
    static ArrayList<String> dateList = new ArrayList<String>();
    static ArrayList<String> stateList = new ArrayList<String>();
    static ArrayList<String> statusList = new ArrayList<String>();
    static ArrayList<String> priceList = new ArrayList<String>();
    static ArrayList<HistoryItem> historyItems = new ArrayList<>();
    private String urlVal, bikeSign, historyDate, status,detail,price;
    private RequestQueue mQueue;
    private Spinner spnBrand , spnModel , spnColor;
    private EditText edBikeSign , edBikeModel;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_data);

        editData = (Button)findViewById(R.id.editServiceData);
        cancelService = (Button)findViewById(R.id.cancelServiceData);

        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"แก้ไขสำเร็จ!",
                        Toast.LENGTH_LONG).show();
            }
        });

        cancelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"ยกเลิกการแจ้งงซ่อมแล้ว",
                        Toast.LENGTH_LONG).show();
            }
        });

        try{
            Intent intent = getIntent();
            num = Integer.parseInt(intent.getStringExtra("index"));

        }catch (Exception e){

        }

    }

    private void jsonParse(){
        String url = "https://toombike.kku.ac.th/allhis";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");

                                JSONObject index = jsonArray.getJSONObject(num);

                                bikeSign = index.getString("bike_licence");

                                historyDate = index.getString("his_date");

                                detail = index.getString("detail");

                                price = index.getString("price");

                                status = index.getString("status");



                            Log.d("Ans",signList.toString()+dateList.toString()+stateList.toString()+priceList.toString()+stateList.toString());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }
}
