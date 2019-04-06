package com.piyush.newu.kiitcab;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

public class GPSTracker extends Service implements LocationListener {
    private final Context context;
    boolean isGpsenabled=false;
    boolean isnetworkenabled=false;
    boolean cangetlocation=false;

    Location lcl;
    protected LocationManager lcm;

    public GPSTracker(Context context) {
        this.context = context;
    }


    public Location getLocation(){
        try{
            lcm= (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGpsenabled=lcm.isProviderEnabled(lcm.GPS_PROVIDER);
            isnetworkenabled=lcm.isProviderEnabled(lcm.NETWORK_PROVIDER);
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGpsenabled) {
                    if (lcl == null) {
                        lcm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
                        if (lcm != null) {
                            lcl = lcm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }

                if (lcl == null) {
                    if (isnetworkenabled) {

                        lcm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
                        if (lcm != null) {
                            lcl = lcm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }

                    }
                }
            }
        }catch(Exception ex){

        }
        return lcl;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }
}