package com.example.admin.lakshya;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.admin.lakshya.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    Button save, clear;
    List<LatLng> latLngs;
    List<Double> latitude1;
    List<Double> longitude2;
    LatLng MyLocation;
    LocationRequest mLocationRequest;
    double latitude, longitude;
    GoogleApiClient mGoogleApiClient;
    double area;
    PolygonOptions rectOptions;
    GoogleMap map;
    EditText ed1,ed2,ed3,ed4;
    int state=0;
    TextView landArea;
    AlertDialog a;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        landArea = findViewById(R.id.area);
        clear = findViewById(R.id.clear);
        save = findViewById(R.id.save);
        latitude1=new ArrayList<>();
        longitude2=new ArrayList<>();
        // Write a message to the database


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        latLngs = new ArrayList<>();
        rectOptions = new PolygonOptions();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFarm);
        mapFragment.getMapAsync(this);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                latLngs.clear();
                rectOptions.getPoints().clear();
                landArea.setText("");
                landArea.setVisibility(View.INVISIBLE);

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder b = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater l = getLayoutInflater();
                View v = l.inflate(R.layout.farm_details, null);

                 ed1=v.findViewById(R.id.editeText1);
                 ed2=v.findViewById(R.id.editeText2);
                 ed3=v.findViewById(R.id.editeText3);
                 ed4=v.findViewById(R.id.editeText4);
                Button save=v.findViewById(R.id.saveData);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String name=ed1.getText().toString();
                        String no=ed2.getText().toString();
                        String locality=ed3.getText().toString();
                        String type=ed4.getText().toString();
                         database = FirebaseDatabase.getInstance();
                         myRef = database.getReference(no);

                        writeNewUser(name,no,locality,type);

                        //myRef.setValue("Hello, World!");

                        myRef.child(no).setValue(latLngs);
                        a.dismiss();
                        latLngs.clear();
                        rectOptions.getPoints().clear();
                        landArea.setText("");
                        landArea.setVisibility(View.INVISIBLE);

                    }
                });

                b.setView(v);
                a = b.create();
                a.show();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this
        );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        MyLocation = new LatLng(latitude, longitude);
        if (latitude != 0 && longitude != 0 && state==0 ) {
            //Log.i("Location", "" + latitude + "," + longitude);
            CameraPosition camPos = new CameraPosition.Builder().target(MyLocation)
                    .zoom(15)
                    .build();

            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
            map.animateCamera(camUpd3);
            state=1;

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latLngs.add(latLng);
                latitude1.add(latLng.latitude);
                longitude2.add(latLng.longitude);
                landArea.setVisibility(View.VISIBLE);
                rectOptions.add(latLng);
                Log.i("Data", "computeArea " + SphericalUtil.computeArea(latLngs));//Area in sqaure meters
                Polygon polygon = map.addPolygon(rectOptions);
                polygon.setStrokeColor(Color.GREEN);
                polygon.setFillColor(Color.RED);
                Log.i("data","polyline");
                map.addMarker(new MarkerOptions()
                        .draggable(true)
                        .alpha(0.7f)
                        .position(latLng));
                area=SphericalUtil.computeArea(latLngs)*0.00024711;
                landArea.setText("Land Area: "+area+ " Acre");
            }
        });

    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000*10);
        mLocationRequest.setFastestInterval(1000*5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void writeNewUser(String username, String sno, String local,String cat) {
        User user = new User(username,sno,local,cat);

        myRef.child("users").child(sno).setValue(user);
    }
}
