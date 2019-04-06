package com.piyush.newu.kiitcab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.UserWriteRecord;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class SignUpActivity extends AppCompatActivity {


    EditText password,confpassword,email,name1;
    ActionCodeSettings actionCodeSettings;
    public void studentsignup(View v){

        if(name1.getText().toString().trim().equals("") || email.getText().toString().trim().equals("") || password.getText().toString().trim().equals("") || confpassword.getText().toString().trim().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Empty field",Toast.LENGTH_SHORT).show();
            return;
        }


        if(!password.getText().toString().equals(confpassword.getText().toString())){
            confpassword.setError("Password Does not match");
            confpassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(!(TextUtils.equals(s,password.getText())) && confpassword.length()>0)
                        confpassword.setError("Password Does Not Match");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        String email1=email.getText().toString()+"@kiit.ac.in";

        FirebaseAuth user1 = FirebaseAuth.getInstance();
        user1.createUserWithEmailAndPassword(email1,password.getText().toString());

        user1.signInWithEmailAndPassword(email1,password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String name2 = name1.getText().toString();

                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name2).build();

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent i = new Intent(getApplicationContext(),ResendEmail.class);
                            startActivity(i);
                        }
                    });
                }else{

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name1 = findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        confpassword = (EditText) findViewById(R.id.confpassword);
        email = findViewById(R.id.Rollno);
    }
}
