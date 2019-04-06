package com.example.tanika.toombikeapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanika.toombikeapplication.MainBottomActivity;
import com.example.tanika.toombikeapplication.R;
import com.example.tanika.toombikeapplication.Recycler.AddBikeAdapter;
import com.example.tanika.toombikeapplication.Recycler.HistoryAdapter;
import com.example.tanika.toombikeapplication.Recycler.HistoryItem;
import com.example.tanika.toombikeapplication.Recycler.ItemClickSupport;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;


public class HistoryFragment extends Fragment {

    View v ;
    static ArrayList<String> signList = new ArrayList<String>();
    static ArrayList<String> dateList = new ArrayList<String>();
    static ArrayList<String> stateList = new ArrayList<String>();
    static ArrayList<String> statusList = new ArrayList<String>();
    static ArrayList<String> priceList = new ArrayList<String>();
    static ArrayList<HistoryItem> historyItems = new ArrayList<>();
    private String urlVal, bikeSign, historyDate, status,detail,price,phoneNum,hisNum;
    private RequestQueue mQueue;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HistoryAdapter adapter;

    // for test delay.
    Handler handle;
    Runnable runable;


    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            phoneNum = getArguments().getString("phoneNum");
        }catch (Exception e){

        }

        if (mQueue == null){
            mQueue = Volley.newRequestQueue(getContext());
        }
        jsonParse();

        historyItems.clear();

        v = inflater.inflate(R.layout.fragment_history, container, false);

        mRecyclerView = (RecyclerView)v.findViewById(R.id.historyRec);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        initInstance();
        return v;
    }

    private void initInstance() {


        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handle = new Handler();
                runable = new Runnable() {

                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        historyItems.clear();
                        jsonParse();
                        handle.removeCallbacks(runable); // stop runable.
                    }
                };
                handle.postDelayed(runable, 3000); // delay 3 s.
            }
        });
    }



    public void onResume(){
        super.onResume();

        // Set title bar
        ((BottomNavMenu) getActivity())
                .setActionBarTitle("ประวัติและสถานะการแจ้งซ่อม");

    }

    private void jsonParse(){
        String url = "https://toombike.kku.ac.th/allhis/user?phone=0896235853";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");

                            for (int num = 0 ; num < jsonArray.length();num++){
                                JSONObject index = jsonArray.getJSONObject(num);


                                hisNum = index.getString("his_num");
                                bikeSign = index.getString("bike_licence");
                                historyDate = index.getString("his_date");
                                detail = index.getString("detail");
                                price = index.getString("price");
                                status = index.getString("status");

                                signList.add(bikeSign);
                                dateList.add(historyDate);
                                stateList.add(detail);
                                priceList.add(price);
                                statusList.add(status);

                                historyItems.add(new HistoryItem(hisNum,historyDate,bikeSign,detail,status,price));
                            }
                            //mAdapter = new HistoryAdapter(historyItems);

                            adapter = new HistoryAdapter(historyItems,getContext());
                            mRecyclerView.setAdapter(adapter);

                            Log.d("Ans",signList.toString()+dateList.toString()+stateList.toString()+priceList.toString()+stateList.toString());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("errorHistory",error.toString());
            }
        });

        mQueue.add(request);
    }
}