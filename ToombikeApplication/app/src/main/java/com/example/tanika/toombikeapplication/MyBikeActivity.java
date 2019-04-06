package com.example.tanika.toombikeapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MyBikeActivity extends AppCompatActivity {
    private  ArrayList<Bikeitem> bikeItemArrayList;
    private RecyclerView mRecyclerView;
    private MyBikeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonDel ;
    private EditText editDel ;
    public int stateAddItem = 0 ;
    public int countView = 1 ;
    public int position;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bike);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyBikeActivity.this,
                        AddBikeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        createBikeList();
        buildRecyclerView();


        try {
            Intent intent = getIntent();
            int intValue = getIntent().getExtras().getInt("state");
            stateAddItem = intValue;

        }catch(Throwable e){

        }
        if(stateAddItem == 1){
            int positionAdd = position +1 ;
            addBike(positionAdd);
        }
        buttonDel = findViewById(R.id.deleteBtn);
        editDel = findViewById(R.id.editext_delete);

        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    position = Integer.parseInt(editDel.getText().toString());
                    deleteBike(position);
                }catch (Throwable e){

                }
            }
        });


    }

    private void addBike(int position) {

        bikeItemArrayList.add(new Bikeitem(R.drawable.ic_directions_bike,"ทะเบียน","ยี่ห้อ รุ่น สี"));
        mAdapter.notifyItemInserted(position);

    }

    public void deleteBike(int position){
        bikeItemArrayList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager =  new LinearLayoutManager(this);
        mAdapter = new MyBikeAdapter(bikeItemArrayList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new MyBikeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MyBikeActivity.this,
                        MainBottomActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                String strName = null;
                intent.putExtra("fromMyBike", strName);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createBikeList() {

        bikeItemArrayList = new ArrayList<>();
        bikeItemArrayList.add(new Bikeitem(R.drawable.ic_directions_bike,"ทะเบียน 1","ยี่ห้อ รุ่น สี"));
        bikeItemArrayList.add(new Bikeitem(R.drawable.ic_directions_bike,"ทะเบียน 2","ยี่ห้อ รุ่น สี"));
        bikeItemArrayList.add(new Bikeitem(R.drawable.ic_directions_bike,"ทะเบียน 3","ยี่ห้อ รุ่น สี"));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // perform your action here

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
