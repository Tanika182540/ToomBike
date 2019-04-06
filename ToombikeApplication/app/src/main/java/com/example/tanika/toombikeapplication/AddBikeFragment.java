package com.example.tanika.toombikeapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AddBikeFragment extends Fragment {

    private Spinner spinnerBrand, spinnerColor, spinnerProvince;
    private EditText bikeSign, bikeState, edbikeModel;
    private Button createBtn;
    private String phoneNum,bikeBrand,bikeModel,bikeSign1,bikeColor, province;
    private ImageView imageViewDelete;
    private RequestQueue queue;
    View v ;

    public AddBikeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_addbike, container, false);
        spinnerBrand = v.findViewById(R.id.spinnerBrand);
        edbikeModel = v.findViewById(R.id.edBikeModel);
        spinnerColor = v.findViewById(R.id.spinnerColor);
        bikeSign = v.findViewById(R.id.bikeSign);
        bikeState = v.findViewById(R.id.bikeState);
        createBtn = v.findViewById(R.id.createBtnFm);
        spinnerProvince = v.findViewById(R.id.spinnerProvince);

        if (queue == null){
            queue = Volley.newRequestQueue(getContext());
        }

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bikeBrand = spinnerBrand.getSelectedItem().toString();
                bikeModel = edbikeModel.getText().toString();
                bikeColor = spinnerColor.getSelectedItem().toString();
                bikeSign1 = bikeSign.getText().toString().trim()+ " " + spinnerProvince.getSelectedItem().toString();
                sendPostData();
            }
        });

        //phoneNum = getArguments().getString("phoneNum");
        phoneNum = "0896235853";
        setHasOptionsMenu(true);
        return v;

    }


    private void sendPostData() {

        Log.d("logGuuuuu", "wtffffffffffff");

        final String urlPOST = "https://toombike.kku.ac.th/insert/bike";
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPOST,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(getContext(),"เพิ่มข้อมูลสำเร็จ",Toast.LENGTH_SHORT).show();
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
                params.put("phone", phoneNum);
                params.put("number", bikeSign1);
                params.put("brand",bikeBrand);
                params.put("model",bikeModel);
                params.put("color",bikeColor);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void onResume(){
        super.onResume();

        // Set title bar
        ((BottomNavMenu) getActivity())
                .setActionBarTitle("เพิ่มรถจักรยานยนต์");

    }

}