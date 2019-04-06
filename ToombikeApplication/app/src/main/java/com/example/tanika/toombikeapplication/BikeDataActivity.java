package com.example.tanika.toombikeapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BikeDataActivity extends AppCompatActivity {

    public Button edit , cancel;
    public EditText bikeSign;
    public String strBikeSign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_bike_data);

        edit = (Button) findViewById(R.id.editBtn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BikeDataActivity.this,
                        "แก้ไขข้อมูลเรียบร้อย!", Toast.LENGTH_LONG).show();
            }
        });
        cancel = (Button) findViewById(R.id.cancelEditBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BikeDataActivity.this,
                        MyBikeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        bikeSign = (EditText) findViewById(R.id.bikeSignData);
        strBikeSign = getIntent().getStringExtra("bikeSign");
        bikeSign.setText(strBikeSign);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // perform your action here

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
