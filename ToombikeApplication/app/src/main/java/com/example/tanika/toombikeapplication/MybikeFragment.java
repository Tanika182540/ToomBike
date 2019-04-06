package com.example.tanika.toombikeapplication;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanika.toombikeapplication.Recycler.AddBikeAdapter;
import com.example.tanika.toombikeapplication.Recycler.AddBikeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MybikeFragment extends Fragment {


    public View v ;
    static ArrayList<String> bikeSignList = new ArrayList<>();
    static ArrayList<String> bikeModelList = new ArrayList<>();
    static ArrayList<String> bikeBrandList = new ArrayList<>();
    static ArrayList<String> bikeColorList = new ArrayList<>();
    static ArrayList<String> userNameList = new ArrayList<>();
    static ArrayList<String> userPhoneList = new ArrayList<>();
    static ArrayList<AddBikeItem> addBikeItems = new ArrayList<>();
    private String bikeSign, bikeModel,phoneNum, bikeBrand, bikeColor,userName,userPhone;
    private RequestQueue mQueue;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    public MybikeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_my_bike, container, false);
        try{
            phoneNum = getArguments().getString("phoneNum");
        }catch (Exception e){

        }

        if (mQueue == null){
            mQueue = Volley.newRequestQueue(getContext());
        }
        jsonParse();

        recyclerView = (RecyclerView)v.findViewById(R.id.addBikeRec);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        addBikeItems.clear();
        return v;
    }

    public void swipeItem(){
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // view the background view
            }
        };

// attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }


    private void jsonParse() {
        String url = "https://toombike.kku.ac.th/user/bike?phone="+phoneNum;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");

                            for (int num = 0 ; num < jsonArray.length();num++){
                                JSONObject index = jsonArray.getJSONObject(num);

                                bikeSign = index.getString("bike_licence");

                                bikeSignList.add(bikeSign);

                                bikeModel = index.getString("bike_model");
                                bikeModelList.add(bikeModel);

                                bikeBrand = index.getString("bike_brand");
                                bikeBrandList.add(bikeBrand);

                                bikeColor = index.getString("bike_color");
                                bikeColorList.add(bikeColor);

                                userName = index.getString("cus_name");
                                userNameList.add(userName);

                                userPhone = index.getString("cus_phone");
                                userNameList.add(userPhone);

                                addBikeItems.add(new AddBikeItem(bikeBrand,bikeModel,bikeColor,bikeSign,userName,userPhone));
                            }

                            AddBikeAdapter adapter = new AddBikeAdapter(addBikeItems,getContext());
                            recyclerView.setAdapter(adapter);

                            Log.d("Ans",bikeModelList.toString()+bikeModelList.toString());


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

    public void onResume(){
        super.onResume();

        // Set title bar
        ((BottomNavMenu) getActivity())
                .setActionBarTitle("จักกรยานยนต์ขอนฉัน");


    }

    @Override
    public void onStart() {
        super.onStart();
    }
}