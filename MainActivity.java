package com.piyush.newu.kiitcab;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
public class MainActivity extends AppCompatActivity implements LocationListener {

    ImageView i;
    RelativeLayout relativeLayout, r1;
    EditText us, pa;
    Handler handler = new Handler();
    SharedPreferences s;
    SharedPreferences.Editor se;
    CheckBox a;
    Boolean savelogin;
    FirebaseAuth usercurrent;
    public int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    LocationListener listener;
    LocationManager manager;

    @Override
    protected void onStart() {
           super.onStart();
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("tag", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("tag", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, 199);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("tag", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("tag", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    public void registernow(View view){
        Intent i =new Intent(getApplicationContext(),Main3Activity.class);
        startActivity(i);
    }

    public void remember(View v) {
        String username = us.getText().toString();
        String password = pa.getText().toString();
        if (a.isChecked())
        {
            se.putString("username", username);
            se.putString("password", password);
            se.putBoolean("saveLogin", true);
            se.commit();
        } else {
            se.clear();
            se.commit();
        }
    }

    public void login(View v) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);


        try {
            gps_enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            displayLocationSettingsRequest(getApplicationContext());
        }

        else {
            String email = us.getText().toString();
            String password1 = pa.getText().toString();
            usercurrent.signInWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                Boolean ismailverified = user.isEmailVerified();
                                String phone = user.getPhoneNumber();
                                if (phone == null) {
                                    Intent i = new Intent(getApplicationContext(), ResendEmail.class);
                                    startActivity(i);
                                } else {
                                    Intent x = new Intent(getApplicationContext(), RiderMapActivity.class);
                                    startActivity(x);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Either username or password is incorrect", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }

    Runnable ar = new Runnable() {
        @Override
        public void run() {
            i.animate().translationYBy(-800);
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relativeLayout.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i = (ImageView) findViewById(R.id.taximg);
        r1 = (RelativeLayout) findViewById(R.id.firstview);
        relativeLayout = (RelativeLayout) findViewById(R.id.log);
        handler.postDelayed(ar, 1000);
        handler.postDelayed(runnable, 1500);
        a = (CheckBox) findViewById(R.id.remembermecheckbox);
        usercurrent = FirebaseAuth.getInstance();

        us = (EditText) findViewById(R.id.username);
        pa = (EditText) findViewById(R.id.password);

        manager = (LocationManager)MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        s=this.getSharedPreferences("com.piyush.newu.kiitcab", Context.MODE_PRIVATE);
        se=s.edit();
        savelogin = s.getBoolean("saveLogin", false);
        if(savelogin){
            us.setText(s.getString("username",""));
            pa.setText(s.getString("password",""));
            a.setChecked(true);
        }
    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        displayLocationSettingsRequest(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //gps_enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //network_enabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}