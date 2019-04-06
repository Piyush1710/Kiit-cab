package com.piyush.newu.kiitcab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {

    EditText phoneNumber,otp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    String verificationCode;
    SharedPreferences store;

    public void generateotp(View v){
        String phoneNumberentered="+91"+phoneNumber.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumberentered,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                PhoneVerification.this,        // Activity (for callback binding)
                mCallback);
    }

    public void verifyotp(View v){
        String otpre = otp.getText().toString();
        String userid = FirebaseAuth.getInstance().getUid();

        SharedPreferences h = getSharedPreferences("Name",Context.MODE_PRIVATE);

        String name = h.getString("Name",null);

        DatabaseReference namestore = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Name");
        namestore.setValue(name);

        if(otpre.equals(verificationCode)){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            store.edit().putBoolean("phoneverification",true).apply();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        phoneNumber = findViewById(R.id.phonenoenterd);

        otp = findViewById(R.id.otprecieved);

        store = getSharedPreferences("PhoneVerified",Context.MODE_PRIVATE);

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updatePhoneNumber(phoneAuthCredential);
                String userid = user.getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("StartRide").child(userid);
                ref.setValue("False");
                Intent u = new Intent(getApplicationContext(),RiderMapActivity.class);
                startActivity(u);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneVerification.this,"verification failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(PhoneVerification.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };

    }
}
