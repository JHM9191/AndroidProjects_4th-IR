package com.example.coronaMapProject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class CoronaMapActivity extends AppCompatActivity {
    SupportMapFragment sMapFragment;
    GoogleMap gmap;
    LocationManager manager;
    TextView tv_patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corona_map);
        tv_patientName = findViewById(R.id.tv_map_patientName);

        Intent intent = getIntent();
        final ArrayList<CoronaLocation> locList = (ArrayList<CoronaLocation>) intent.getSerializableExtra("locationListSelected");
        tv_patientName.setText(locList.get(0).getPatientName()+"");
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        ActivityCompat.requestPermissions(this,
                permissions, 101);

        sMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        sMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;
                LatLng[] latLngList = new LatLng[locList.size()];
                Double latSum = 0.0;
                Double lonSum = 0.0;
                for (int i = 0; i < locList.size(); i++) {
                    LatLng latLng = new LatLng(locList.get(i).getLat(), locList.get(i).getLon());
                    gmap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(locList.get(i).getNo() + ". " + locList.get(i).getName()));

                    latLngList[i] = latLng;
                    latSum += locList.get(i).getLat();
                    lonSum += locList.get(i).getLon();
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                for (int i = 0; i < locList.size() - 1; i++) {
                    Polyline line = gmap.addPolyline(new PolylineOptions()
                            .add(latLngList[i], latLngList[i + 1])
                            .width(5)
                            .color(Color.RED));

                }
                Log.d("---", latSum / locList.size() + "");
                Log.d("---", lonSum / locList.size() + "");
                LatLng avglatlng = new LatLng(latSum / locList.size(), lonSum / locList.size());
                gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(avglatlng, 8));
                gmap.setMyLocationEnabled(true);
                manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                MyLocation myLocation = new MyLocation();
                long minTime = 1;
                float minDistance = 0;
                manager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        minTime,
                        minDistance,
                        myLocation
                );
            }
        });
    }

    class MyLocation implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            String loc = lat + " " + lon;
//            textView.setText(loc);
            LatLng latLng = new LatLng(lat, lon);
//            gmap.addMarker(new MarkerOptions()
//                    .position(latLng));
//            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
