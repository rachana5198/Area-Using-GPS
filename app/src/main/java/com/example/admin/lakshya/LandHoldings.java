package com.example.admin.lakshya;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class LandHoldings extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng myloc;
    PolygonOptions rectOptions,rectOptions2;
    List<LatLng> latLngs;
    List<Double> latitude1;
    List<Double> longitude2;
    double area;
    Button save, clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_holdings);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFarm);
        mapFragment.getMapAsync(this);
        clear = findViewById(R.id.clear);
        save = findViewById(R.id.save);
        latLngs = new ArrayList<>();
        latitude1=new ArrayList<>();
        longitude2=new ArrayList<>();
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                latLngs.clear();
                rectOptions.getPoints().clear();
                GetLocations getLocations=new GetLocations();
                getLocations.execute();
              /*  landArea.setText("");
                landArea.setVisibility(View.INVISIBLE);*/

            }
        });
        rectOptions = new PolygonOptions();
        rectOptions2 = new PolygonOptions();
        GetLocations getLocations=new GetLocations();
        getLocations.execute();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latLngs.add(latLng);
                latitude1.add(latLng.latitude);
                longitude2.add(latLng.longitude);
                rectOptions2.add(latLng);
                Log.i("Data2", "computeArea " + SphericalUtil.computeArea(latLngs));//Area in sqaure meters
                Polygon polygon = mMap.addPolygon(rectOptions2);
                polygon.setStrokeColor(Color.WHITE);
                polygon.setFillColor(Color.CYAN);
                Log.i("data2","polyline");
                mMap.addMarker(new MarkerOptions()
                        .draggable(true)
                        .alpha(0.7f)
                        .position(latLng));
                area=SphericalUtil.computeArea(latLngs)*0.00024711;
               // landArea.setText("Land Area: "+area+ " Acre");
            }
        });


    }

    class GetLocations extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL("https://ethereal-icon-198304.firebaseio.com/54321/-L8b439lnJNcHWxY5qpn.json");

                URLConnection conn = url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    System.out.println(" bufferedreader response :" + bufferedReader);

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);


                    }
                    bufferedReader.close();
                    Log.e("Data", stringBuilder.toString());
                    return stringBuilder.toString();
                } finally {

                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jsonArray=new JSONArray(s);
                for (int i=0;i<jsonArray.length();i++){

                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    Log.i("LatLong",jsonObject.getString("latitude")+","+jsonObject.getString("longitude"));
                    rectOptions.add(new LatLng(Double.valueOf(jsonObject.getString("latitude")),Double.valueOf(jsonObject.getString("longitude"))));
                    myloc = new LatLng(Double.valueOf(jsonObject.getString("latitude")),Double.valueOf(jsonObject.getString("longitude")));
                    Polygon polygon = mMap.addPolygon(rectOptions);
                    polygon.setStrokeColor(Color.GREEN);
                    polygon.setFillColor(Color.RED);
                    CameraPosition camPos = new CameraPosition.Builder().target(myloc)
                            .zoom(28)
                            .build();

                    CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                    mMap.animateCamera(camUpd3);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
