package com.example.tanika.toombikeapplication;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanika.toombikeapplication.ImageUpLoad.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.Set;

public class SetBikeActivity extends AppCompatActivity {

    private int REQUEST_CODE = 1;

    private Button mButtonChooseImage, mButtonUpload, goService,deleteBike;
    private TextView mFileName,bikSignName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private String imgName, phone, userName, sign, bikeModel,detail,index;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    private CardView cardViewBike, cardViewSign;
    private RequestQueue queue;
    private EditText edDetail ;
    private RadioButton radioButton1,radioButton;
    private RadioGroup group1, group2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bike);

        final Intent intent = getIntent();
        phone = intent.getStringExtra("phoneNum");
        userName = intent.getStringExtra("name");
        sign = intent.getStringExtra("bikeSign");
        bikeModel = intent.getStringExtra("bikeModel");
        index = intent.getStringExtra("index");

        if (queue == null){
            queue = Volley.newRequestQueue(SetBikeActivity.this);
        }

        mButtonChooseImage = (Button) findViewById(R.id.chooseFile);
        mButtonUpload = (Button) findViewById(R.id.uploadImg);
        mFileName = (TextView) findViewById(R.id.textFileName);
        mImageView = (ImageView) findViewById(R.id.imageView_bike);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        goService = (Button) findViewById(R.id.goService);
        cardViewBike = (CardView)findViewById(R.id.cardViewBike);
        cardViewSign = (CardView)findViewById(R.id.cardViewSign);
        deleteBike = (Button)findViewById(R.id.btnDeleteBike);
        bikSignName = (TextView)findViewById(R.id.bringBikeSign);
        edDetail = (EditText)findViewById(R.id.fixCause);


        bikSignName.setText(sign);

        final Animation mShowLayout = AnimationUtils.loadAnimation(SetBikeActivity.this,R.anim.show_layout);
        final Animation mHideLayout = AnimationUtils.loadAnimation(SetBikeActivity.this,R.anim.hide_layout);

        /*cardViewSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cardViewBike.getVisibility() == View.VISIBLE){
                    cardViewBike.setVisibility(View.GONE);
                    cardViewBike.startAnimation(mHideLayout);
                }else {
                    cardViewBike.setVisibility(View.VISIBLE);
                    cardViewBike.startAnimation(mShowLayout);
                }

            }
        });
        */

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(SetBikeActivity.this, "Upload in progress", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });

        goService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetail();
                if(detail == "" || detail == null || detail.isEmpty()){
                    Toast.makeText(SetBikeActivity.this,"กรุณาแจ้งสาเหตุการซ่อม",Toast.LENGTH_SHORT).show();
                }else {

                    Intent intent2 = new Intent(SetBikeActivity.this, MapsActivity.class);
                    intent2.putExtra("name", userName);
                    intent2.putExtra("bikeSign", sign);
                    intent2.putExtra("phoneNum", phone);
                    intent2.putExtra("bikeModel", bikeModel);
                    intent2.putExtra("detail",detail);
                    startActivity(intent2);
                }
            }
        });

        deleteBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBikeData();
            }
        });

        group1 = (RadioGroup) findViewById(R.id.rg1);
        group2 = (RadioGroup) findViewById(R.id.rg2);

    }

    private void getDetail() {
        int selectedId;

        if(group1.getCheckedRadioButtonId() == -1){
            group1.clearCheck();
            selectedId = group2.getCheckedRadioButtonId();
            radioButton1 = (RadioButton)findViewById(selectedId);
            detail = radioButton1.getText().toString();
        }else {
            group2.clearCheck();
            selectedId = group1.getCheckedRadioButtonId();
            radioButton = (RadioButton)findViewById(selectedId);
            detail = radioButton.getText().toString();
        }

       // Toast.makeText(SetBikeActivity.this,detail,Toast.LENGTH_SHORT).show();
    }

    private void deleteBikeData() {
        final String url = "https://toombike.kku.ac.th/delete/bike?number=" + sign.trim();
        Log.d("useURL", url);


        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("ResponseText", response.toString());
                        Toast.makeText(SetBikeActivity.this,"ลบข้อมูลสำเร็จ",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SetBikeActivity.this,BottomNavMenu.class);
                        intent.putExtra("flag","flagsSetbike");
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("errorSetBike",error.toString());
                        Log.d("url", url);
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "เลือกภาพสำหรับรถจักรยานยนต์"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImageView.setImageBitmap(bitmap);
                imgName = mImageUri.getPath().toString();
                mFileName.setText(sign);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
