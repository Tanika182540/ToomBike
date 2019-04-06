package com.example.tanika.toombikeapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanika.toombikeapplication.R;
import com.example.tanika.toombikeapplication.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.BindView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String EXTRA_STRING = "com.example.tanika.toombikeapplication.EXTRA_STRING";
    private static final int REQUEST_SIGNUP = 0;

    public String phoneNum, newPhoneNum;
    @BindView(R.id.input_phone) EditText _phoneText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
   // @BindView(R.id.link_signup) TextView _signupLink;
    @BindView(R.id.btn_verify) Button _verifyButton;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    FirebaseAuth mAuth;
    String codeSent;
    ScrollView scrollTo ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

      if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), BottomNavMenu.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
            finish();
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    verifySingInCode();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"กรุณากรอกข้อมูลให้ถูกต้องและครบถ้วน",
                            Toast.LENGTH_LONG).show();
                }

            }
        });



       /* _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
*/
        _verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    sendVerificationCode();

                    phoneNum = _phoneText.getText().toString();
                    newPhoneNum =  phoneNum.substring(3);

                    Toast.makeText(getApplicationContext(),"กำลังส่งรหัสยืนยันไปยังเบอร์โทรศัพท์มือถือของท่าน 0" + newPhoneNum,
                            Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"กรุณากรอกข้อมูลให้ถูกต้องและครบถ้วน",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            phoneNum = _phoneText.getText().toString();
                            newPhoneNum =  phoneNum.substring(3);



                            Log.d("test","fuckuuuuuuuu");
                            Intent intent = new Intent(LoginActivity.this, BottomNavMenu.class);
                            intent.putExtra("phoneNum", "0" + newPhoneNum);
                            startActivity(intent);
                            finish();
                            saveUser();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"Incorrect verification code ",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        }
                    });
                }



    private void saveUser() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //https://toombike.kku.ac.th/insert/user?phone=+6689623585&name=
        /*final String url = "https://toombike.kku.ac.th/insert/user?phone=0"+newPhoneNum+"&name=&email=";

// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);*/

        final String urlPOST = "https://toombike.kku.ac.th/insert/user";
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPOST,
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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("phone", "0" + newPhoneNum);

                return params;
            }
        };
        queue.add(postRequest);
    }


    private void verifySingInCode(){
        String code = _passwordText.getText().toString() ;
        if (code.isEmpty()) {
            _passwordText.setError("Phone number is required");
            _passwordText.requestFocus();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);

    }
    private void sendVerificationCode(){

        String phoneNumber = _phoneText.getText().toString();
        String newPhoneNumber = "0" + phoneNumber.substring(3);

        Intent intent = new Intent(LoginActivity.this,BottomNavMenu.class);
        intent.putExtra("phoneNum", newPhoneNumber);

        if (phoneNumber.isEmpty()) {
            _phoneText.setError("Phone number is required");
            _phoneText.requestFocus();
            return;
        }
        if (phoneNumber.length()<10) {
            _phoneText.setError("Please enter a valid phone");
            _phoneText.requestFocus();
            return;
        }

        phoneNum = _phoneText.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            mResendToken = forceResendingToken;
            codeSent = s;

        }
    };
    public void login() {
        Log.d(TAG, "Login");

        Intent intent = new Intent(getApplicationContext(), BottomNavMenu.class);
        finish();
        /*
        if (!validate()) {
            onLoginFailed();
            return;
        }


        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

                */
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _phoneText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _phoneText.setError("enter a valid email address");
            valid = false;
        } else {
            _phoneText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}