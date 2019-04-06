package com.example.tanika.toombikeapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserFragment extends Fragment {

    private View v;
    private Button signOut, editData, saveData;
    private FirebaseAuth mAuth;
    private EditText name, phone, email;
    private String phoneNum, cusPhone,cusName,cusMail,editName,editEmail;
    private RequestQueue queue;

    public UserFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user,container,false);

        try {
            phoneNum = getArguments().getString("phoneNum");
            Log.d("puterr", "UserFragment"+phoneNum);
        }catch (Exception e){

        }

        if (queue == null){
            queue = Volley.newRequestQueue(getContext());
        }
        //editUserData();
        getUserData();


       /* signOut = (Button)v.findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(),
                        LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
*/
        name = (EditText) v.findViewById(R.id.userName);
        name.setEnabled(false);

        phone = (EditText) v.findViewById(R.id.userPhoneNum);
        phone.setEnabled(false);
        phone.setBackgroundColor(Color.TRANSPARENT);

        email = (EditText) v.findViewById(R.id.userEmail);
        email.setEnabled(false);
        saveData = (Button)v.findViewById(R.id.saveData);
        saveData.setVisibility(View.GONE);

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName = name.getText().toString();
                editEmail = email.getText().toString();


                name.setEnabled(false);

                email.setEnabled(false);

                saveData();
                saveData.setVisibility(View.GONE);
                editData.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),"แก้ไขเรียบร้อย!",Toast.LENGTH_LONG).show();

            }
        });


        editData = (Button)v.findViewById(R.id.editDataBtn);
        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData.setVisibility(View.VISIBLE);
                name.setEnabled(true);
                email.setEnabled(true);
                editData.setVisibility(View.GONE);
            }
        });

        setHasOptionsMenu(true);
        return v;

    }

    private void saveData() {
        //https://toombike.kku.ac.th/update/user
        String url = "https://toombike.kku.ac.th/update/user";
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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
                Map<String, String>  params = new HashMap<String, String> ();
                params.put("phone", phoneNum);
                params.put("name", editName);
                params.put("email",editEmail);

                return params;
            }

        };

        queue.add(putRequest);
    }

    private void getUserData() {

        RequestQueue mQueue = Volley.newRequestQueue(getContext());  // this = context

        Bundle bundle = this.getArguments();
        try {
            phoneNum = bundle.get("phoneNun").toString();
        }catch (Exception e){

        }
        String url = "https://toombike.kku.ac.th/user?phone=" + phoneNum;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");

                            int num = 0;
                                JSONObject index = jsonArray.getJSONObject(num);

                                 cusPhone = index.getString("cus_phone");
                                 cusName = index.getString("cus_name");
                                 cusMail = index.getString("cus_email");

                                 Log.d("Name",cusName);

                                name.setText(cusName);
                                phone.setText(cusPhone);
                                email.setText(cusMail);
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


    private void editUserData() {

    }

    public void onResume(){
        super.onResume();

        // Set title bar
        ((BottomNavMenu) getActivity())
                .setActionBarTitle("ข้อมูลผู้ใช้");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menusetting,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSubLogout :
                mAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(),
                        LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
