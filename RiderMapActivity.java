package com.piyush.newu.kiitcab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Guideline;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.view.autofill.AutofillValue;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import com.google.zxing.Result;

import static android.Manifest.permission.CAMERA;

public class RiderMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private ViewStub viewStub,gettingYourDriver,ridebookstub;
    private TextView drivernametext,drivervehicletypetext,drivervehiclenotext;
    private View ridenow,ridebookedview,gettingyourdriverview;
    protected static int onlyonce=0;
    private int timeremaining = 30000;
    private MapView mapview;
    private double lat, lon;
    private GoogleMap googleMap;
    private LatLng myposition,pickuploc,destinationloc;
    GoogleApiClient m1;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    private String userId,driverFoundId;
    private DatabaseReference customerpickupRequests,customerdestinationRequests,customerRequests;
    private int REQUEST_FINE_LOCATION,accepted;
    private int radius,ridenowbuttonclicked = 0;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG="RecyclerAdapter";
    private String driverphoneno,drivervehicletype,drivervehicleno,drivername;
    private HashMap<String,Marker> availableDriverLocationMarker;
    private boolean showDrivers = false,driverFound = false;
    private ArrayList<String> mImageUrl = new ArrayList<>();
    private Button ridenowButton;
    private Handler handler;
    private RelativeLayout relativeLayout;
    private CountDownTimer mycountdowntimer;
    private ZXingScannerView scannerView;
    DatabaseReference reference1;
    private Spinner pickupspinner,destinationspinner;
    private LinearLayout ridenowlayout,ridebookedlayout;
    private Marker pickupmarker,destinationmarker,currentdrivermarker;
    int pickupchecker,destinationchecker;
    private GeoQuery query,queryforshowingalldrivers,queryforshowingcurrentdriver;
    private HashMap currentrideob;
    private View mapView;
    RelativeLayout.LayoutParams rlp;
    private FloatingActionButton locationbutton;

    CircleImageView imageView,imageView1,imageView2;
    //private double spinnercoordinate[][]={{21.1932594, 81.3168656},{21.1954501, 81.3365993},{21.1921917, 81.3535447},{21.2034899, 81.3657701}};
    //These are the coordinates of places in kiit in format {lat,lon}
    private double spinnercoordinate[][] =
            {{20.354040, 85.819704},
            {20.353940, 85.819812},
            {20.348332, 85.816121},
            {20.348885, 85.820734},
            {20.353238, 85.817380},
            {20.348496, 85.819391},
            {20.359322, 85.822311},
            {20.359389, 85.822791},
            {20.352776, 85.816880},
            {20.355109, 85.820649},
            {20.355921, 85.823702},
            {20.355985, 85.823699},
            {20.359227, 85.822643},
            {20.362130, 85.822601},
            {20.353983, 85.820670},
            {20.350555, 85.820694},
            {20.353535, 85.815429},
            {20.350522, 85.813431},
            {20.354076, 85.820204},
            {20.354603, 85.819050},
            {20.354374, 85.819545},
            {20.354387, 85.820992},
            {20.356274, 85.819904},
            {20.349729, 85.816478},
            {20.351893, 85.816382},
            {20.360460, 85.823779},
            {20.349562, 85.814590},
            {20.354739, 85.816239},
            {20.351648, 85.821552},
            {20.352381, 85.817344},
            {20.352363, 85.818304},
            {20.352454, 85.816882},
            {20.353147, 85.818183},
            {20.348385, 85.821128},
            {20.352790, 85.821290},
            {20.346198, 85.823545},
            {20.348892, 85.820727},
            {20.348470, 85.820367},
            {20.353615, 85.810548},
            {20.346258, 85.825098},
            {20.356406, 85.815260},
            {20.352683, 85.816123},
            {20.364038, 85.812190}};

    public void showcurrentposition(View v){
        LatLng latLng = new LatLng(lat, lon);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.animateCamera(cameraUpdate);
    }

    public void viewthescanner(View v)
    {
        Intent i = new Intent(RiderMapActivity.this,Scanactivity.class);
        startActivityForResult(i,1);
    }



    public void handleservice6(View v){
        switch (v.getId()){
            case R.id.recyclerimage:imageView.setCircleBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            imageView1.setCircleBackgroundColor(Color.WHITE);
            imageView2.setCircleBackgroundColor(Color.WHITE);
                break;

            case R.id.recyclerimage1:Toast.makeText(getApplicationContext(),"This service is not yet available.",Toast.LENGTH_SHORT).show();
                break;

            case R.id.recyclerimage2:Toast.makeText(getApplicationContext(),"This service is not yet available.",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void calldriver(View view){

    }


    @SuppressLint("RestrictedApi")
    public void makerequest(View v){
        if(pickupchecker!=destinationchecker) {

            //setting the appropriate layout for user as user clicks on ride now
            if(gettingyourdriverview == null)
                gettingyourdriverview = gettingYourDriver.inflate();
            else
                gettingyourdriverview.setVisibility(View.VISIBLE);

            ridenow.setVisibility(View.INVISIBLE);
            locationbutton.setVisibility(View.INVISIBLE);


            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            customerpickupRequests = FirebaseDatabase.getInstance().getReference().child("CustomerRequests").child(userId).child("pickup");
            customerdestinationRequests = FirebaseDatabase.getInstance().getReference().child("CustomerRequests").child(userId).child("destination");

            customerpickupRequests.setValue(pickupspinner.getSelectedItem().toString());
            customerdestinationRequests.setValue(destinationspinner.getSelectedItem().toString());

            ridenowbuttonclicked = 1;

            final int maxTime = 30000;
            accepted = 0;
            timeremaining = maxTime;
            final ProgressBar progressBar = gettingyourdriverview.findViewById(R.id.progress_circular);
            progressBar.setMax(30000);

            handler = new Handler();

            progressBar.setMax(30000);

            mycountdowntimer = new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long l) {
                    progressBar.setProgress((int) l);
                }

                @Override
                public void onFinish() {
                    gettingyourdriverview.setVisibility(View.INVISIBLE);
                    query.removeAllListeners();
                    DatabaseReference customerrequest = FirebaseDatabase.getInstance().getReference().child("CustomerRequests").child(userId);
                    driverFound = false;
                    customerrequest.removeValue();
                    locationbutton.setVisibility(View.VISIBLE);
                    if(ridenow == null)
                        ridenow = viewStub.inflate();
                    else
                        ridenow.setVisibility(View.VISIBLE);
                    ridenowbuttonclicked = 0;
                }
            }.start();

            findDriver();
        }
    }

    private void findDriver(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DriversAvailable");

        GeoFire geoFire = new GeoFire(reference);

        query = geoFire.queryAtLocation(new GeoLocation(pickuploc.latitude,pickuploc.longitude),radius);
        query.removeAllListeners();

        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!driverFound){

                    driverFound = true;

                    reference1 = FirebaseDatabase.getInstance().getReference().child("RequestHandlingCustomer").child(key);
                    HashMap map = new HashMap();
                    map.put("CustomerRide",userId);
                    reference1.updateChildren(map);

                    final DatabaseReference waitforDriver1 = FirebaseDatabase.getInstance().getReference().child("RequestHandlingDriver").child(userId).child("DriverId");
                    final DatabaseReference waitforDriver2 = FirebaseDatabase.getInstance().getReference().child("RequestHandlingDriver").child(userId);

                    waitforDriver1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                accepted = 1;
                                driverFoundId = dataSnapshot.getValue().toString();
                                gettingyourdriverview.setVisibility(View.INVISIBLE);
                                DatabaseReference refForlayout = FirebaseDatabase.getInstance().getReference().child("StartRide").child(userId);
                                refForlayout.setValue("True "+driverFoundId);
                                waitforDriver2.removeValue();
                                reference1.removeValue();
                                mycountdowntimer.cancel();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.d("hello","hello");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(getApplicationContext(),"DatabaseError",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        buildGoogleApiClient();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //Code starts here
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        customerRequests = FirebaseDatabase.getInstance().getReference("CustomerRequests");

        ridenowlayout = findViewById(R.id.ridenowlayout);
        ridebookedlayout = findViewById(R.id.ridebookedlayout);


        //These three  stubs are reference to layout not yet created
        viewStub = findViewById(R.id.ridenowstub);
        gettingYourDriver = findViewById(R.id.gettingyourdriverstub);
        ridebookstub = findViewById(R.id.ridebookedstub);

        scannerView = new ZXingScannerView(this);

        relativeLayout = findViewById(R.id.maprelative);

        locationbutton = findViewById(R.id.floatingActionButton);

        pickupspinner = findViewById(R.id.spinner4);
        destinationspinner = findViewById(R.id.spinner5);

        ArrayAdapter<CharSequence> pickupadapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.pickuplocations,android.R.layout.simple_spinner_item);

        final ArrayAdapter<CharSequence> destinationadapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.destinationlocations,android.R.layout.simple_spinner_item);

        destinationadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        pickupadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        pickupspinner.setAdapter(pickupadapter);
        pickupspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();
                String textonpickupspinner = pickupspinner.getSelectedItem().toString();
                if(!textonpickupspinner.equals("Enter Your Pickup Location")) {
                    if (pickupmarker != null)
                        pickupmarker.remove();
                    pickupchecker = i;
                    pickuploc = new LatLng(spinnercoordinate[i - 1][0], spinnercoordinate[i - 1][1]);
                    pickupmarker = googleMap.addMarker(new MarkerOptions().position(pickuploc).title("Pickup Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
                if(textonpickupspinner.equals("Enter Your Pickup Location") && pickupmarker!=null)
                    pickupmarker.remove();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        destinationspinner.setAdapter(destinationadapter);
        destinationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String textondestinationspinner = destinationspinner.getSelectedItem().toString();
                if(!textondestinationspinner.equals("Enter Your Destination Location")) {
                    destinationloc = new LatLng(spinnercoordinate[i - 1][0], spinnercoordinate[i - 1][1]);
                    destinationchecker = i;
                    if (destinationmarker != null)
                        destinationmarker.remove();
                    destinationmarker = googleMap.addMarker(new MarkerOptions().position(destinationloc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                if(textondestinationspinner.equals("Enter Your Destination Location") && destinationmarker!=null)
                    destinationmarker.remove();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        availableDriverLocationMarker = new HashMap<String, Marker>();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapview = (MapView) findViewById(R.id.mapView);
        mapview.onCreate(mapViewBundle);

        mapview.getMapAsync(this);

        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());

        Location loc = gpsTracker.getLocation();

        if(loc!=null) {
            lat = loc.getLatitude();
            lon = loc.getLongitude();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        startLocationUpdates();

        setAppropriateLayout();

        radius = 1;
    }

    private void getDriverDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("DriverDetails").child(driverFoundId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap details = (HashMap)dataSnapshot.getValue();
                    driverphoneno = (String)details.get("Phoneno");
                    drivervehicleno = (String)details.get("Vehicleno");
                    drivervehicletype = (String)details.get("Vehicle");
                    drivername = (String)details.get("Name");

                    drivernametext = ridebookedview.findViewById(R.id.Drivername);
                    drivervehiclenotext = ridebookedview.findViewById(R.id.vehicleno);
                    drivervehicletypetext = ridebookedview.findViewById(R.id.vehicletype);

                    drivernametext.setText(drivername);
                    drivervehiclenotext.setText(drivervehicleno);
                    drivervehicletypetext.setText(drivervehicletype);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAppropriateLayout() {
        DatabaseReference referenceForLayout = FirebaseDatabase.getInstance().getReference().child("StartRide").child(userId);
        referenceForLayout.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String value = (String) dataSnapshot.getValue();
                    locationbutton.setVisibility(View.VISIBLE);
                    if(value.equals("False")){

                        if(queryforshowingcurrentdriver != null)
                            queryforshowingcurrentdriver.removeAllListeners();

                        if(ridebookedview != null)
                            ridebookedview.setVisibility(View.INVISIBLE);

                        if(ridenow == null)
                            ridenow = viewStub.inflate();

                        else
                            ridenow.setVisibility(View.VISIBLE);


                        ridenowButton = ridenow.findViewById(R.id.ridenowbutton);
                        imageView = ridenow.findViewById(R.id.recyclerimage);
                        imageView1 = ridenow.findViewById(R.id.recyclerimage1);
                        imageView2 = ridenow.findViewById(R.id.recyclerimage2);

                        Glide.with(getApplicationContext()).load(R.drawable.omni).into(imageView);
                        Glide.with(getApplicationContext()).load(R.drawable.ambulance).into(imageView1);
                        Glide.with(getApplicationContext()).load(R.drawable.bus).into(imageView2);

                        showAvailableDrivers();
                    }
                    else if(value.substring(0,4).equals("True")){

                        String s[]=value.split(" ");


                        driverFoundId = s[1];
                        driverFound = true;
                        accepted = 1;
                        getDriverDetails();
                        if(queryforshowingalldrivers != null)
                            queryforshowingalldrivers.removeAllListeners();

                        if(ridenow!=null)
                            ridenow.setVisibility(View.INVISIBLE);

                        if(ridebookedview == null)
                            ridebookedview = ridebookstub.inflate();
                        else
                            ridebookedview.setVisibility(View.VISIBLE);

                        showcurrentdriver();
                    }
                    else if(value.substring(0,6).equals("False+")){
                        DatabaseReference referenceForRestore = FirebaseDatabase.getInstance().getReference().child("StartRide").child(userId);
                        referenceForRestore.setValue("False");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showcurrentdriver(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Locationtouser").child(driverFoundId);

        GeoFire fire = new GeoFire(reference);

        queryforshowingcurrentdriver = fire.queryAtLocation(new GeoLocation(lat,lon),1);
        queryforshowingcurrentdriver.removeAllListeners();

        queryforshowingcurrentdriver.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(key.equals(driverFoundId)){

                    LatLng locationofdriver = new LatLng(location.latitude,location.longitude);

                    currentdrivermarker = googleMap.addMarker(new MarkerOptions().position(locationofdriver));
                }
            }

            @Override
            public void onKeyExited(String key) {
                if(key.equals(driverFoundId)){
                    currentdrivermarker.remove();
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                if(key.equals(driverFoundId)){
                    LatLng locationofdriver = new LatLng(location.latitude,location.longitude);
                    currentdrivermarker.setPosition(locationofdriver);
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void showAvailableDrivers(){

        showDrivers = true;

        DatabaseReference driverAvailable = FirebaseDatabase.getInstance().getReference("DriversAvailable");

        GeoFire fire = new GeoFire(driverAvailable);

        queryforshowingalldrivers = fire.queryAtLocation(new GeoLocation(lat,lon),1);
        queryforshowingalldrivers.removeAllListeners();

        queryforshowingalldrivers.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(availableDriverLocationMarker.containsKey(key))
                    return;

                LatLng driverLocation = new LatLng(location.latitude,location.longitude);

                Marker x = googleMap.addMarker(new MarkerOptions().position(driverLocation));
                availableDriverLocationMarker.put(key,x);
            }

            @Override
            public void onKeyExited(String key) {
                if(availableDriverLocationMarker.containsKey(key))
                {
                    Marker a1 = availableDriverLocationMarker.get(key);
                    a1.remove();
                    availableDriverLocationMarker.remove(key);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                if(availableDriverLocationMarker.containsKey(key)){
                    Marker a = availableDriverLocationMarker.get(key);
                    LatLng pos = new LatLng(location.latitude,location.longitude);
                    a.setPosition(pos);
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapview.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(ridenowbuttonclicked == 1){

        }
        else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rider_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.scanqr) {
            Intent scanintent = new Intent(this,Scanactivity.class);
            startActivityForResult(scanintent,1);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Toast.makeText(getApplicationContext(),result+"rideractivity",Toast.LENGTH_SHORT).show();
                if(result.equals(driverFoundId)){
                    currentrideob = new HashMap();
                    DatabaseReference referenceforDrivernotification = FirebaseDatabase.getInstance().getReference().child("CurrentRide").child(driverFoundId).child(userId);
                    referenceforDrivernotification.setValue(true);
                    Intent x = new Intent(getApplicationContext(),TickActivity.class);
                    startActivity(x);
                    setListenerForEndRide();
                }
            }
            if(resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }


    }

    private void setListenerForEndRide(){
        final DatabaseReference refForEndRide = FirebaseDatabase.getInstance().getReference().child("CurrentRide").child(driverFoundId);
        refForEndRide.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    HashMap map = (HashMap) dataSnapshot.getValue();
                    if(!map.containsKey(userId)) {
                        ridenow.setVisibility(View.VISIBLE);
                        ridebookedview.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        startLocationUpdates();
    }

    private void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        if(location==null)
            return;

        lat = location.getLatitude();
        lon = location.getLongitude();

        if(onlyonce==0) {
            onlyonce = 1;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        m1 = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        m1.connect();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);
        myposition=new LatLng(lat,lon);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myposition));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        setAppropriateLayout();
        View locationButton = ((View) mapview.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
// position on right bottom

        //rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
      //  if(ridenow!=null)
    //        rlp.addRule(RelativeLayout.ALIGN_BOTTOM,ridenow.getId());
  //      else
//            rlp.addRule(RelativeLayout.ALIGN_BOTTOM,ridebookedview.getId());
        //rlp.setMargins(0, 18000, 180, 0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 30);
        //showAvailableDrivers();
    }



    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mapview.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapview.onStop();
        onlyonce=0;
        scannerView.stopCamera();
    }

    @Override
    protected void onPause() {
        mapview.onPause();
        super.onPause();
        onlyonce=0;
        scannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        mapview.onDestroy();
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }
}