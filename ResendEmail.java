package com.piyush.newu.kiitcab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ResendEmail extends AppCompatActivity {

    public void resendmail(View v){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"E-mail Sent",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkforverification(View v){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.reload();
        if(user.isEmailVerified()){
            Intent a = new Intent(getApplicationContext(),PhoneVerification.class);
            startActivity(a);
        }
        else{
            Toast.makeText(ResendEmail.this,"Please Verify Your E-mail",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_email);
    }

    @Override
    public void onBackPressed() {

    }
}