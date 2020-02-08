package com.example.coronaMapProject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CoronaMapAllActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter {
    SupportMapFragment sMapFragment;
    GoogleMap gmap;
    Location curr_location;
    LocationManager manager;
    Vibrator vibrator;
    ArrayList<CoronaLocation> locationList;
    ArrayList<CoronaLocation> mapList;
    String state = "NONE";
    boolean flag2 = true;
    static ArrayList<CoronaLocation> list_loc_enter = new ArrayList<>();

    Intent serviceIntent;

    double marker_lat;
    double marker_lon;

    ArrayList<Item> items;


    CoronaLocationNearAlertService myService;
    ServiceConnection sconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CoronaLocationNearAlertService.MyBinder mb = (CoronaLocationNearAlertService.MyBinder) service;
            myService = mb.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corona_map_all);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Intent intent = getIntent();

        if (intent != null) {
            String result = null;
            items = (ArrayList<Item>) intent.getSerializableExtra("itemList_from_Second");
            if (items == null) {
                Log.v("----", "no itemList from second");
                SharedPreferences sp = getSharedPreferences("sp", Activity.MODE_PRIVATE);
                if (sp != null && sp.contains("data")) {
                    result = sp.getString("data", "");
                }
                JSONArray ja = null;
                try {
                    ja = new JSONArray(result);
                    JSONArray ja_location = null;
                    items = new ArrayList<>();
                    mapList = new ArrayList<>();
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
//                        String id = jo.getString("id");
                        String name = jo.getString("name");
//                        String sex = jo.getString("sex");
//                        int age = jo.getInt("age");
//                        String nationality = jo.getString("nationality");
//                        int meet = jo.getInt("meet");
//                        int risk = jo.getInt("risk");
//                        String img = jo.getString("img");
                        ja_location = jo.getJSONArray("location");

                        locationList = new ArrayList<>();
                        for (int j = 0; j < ja_location.length(); j++) {
                            JSONObject jo_location = ja_location.getJSONObject(j);
                            int no = jo_location.getInt("no");
                            String name_location = jo_location.getString("name");
                            Double lat = jo_location.getDouble("lat");
                            Double lon = jo_location.getDouble("lon");
                            CoronaLocation location = new CoronaLocation(no, name_location, lat, lon);
                            CoronaLocation map = new CoronaLocation(no, name_location, lat, lon, name);
                            locationList.add(map);
                            mapList.add(map);
                        }
                        Item item = new Item(null, null, null, 0, null, 0, 0.0, "", locationList);
                        items.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }

        sMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAll);
        sMapFragment.getMapAsync(this);

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(this, permissions, 101);

        Intent serviceIntent = new Intent(getApplicationContext(), CoronaLocationNearAlertService.class);
//        serviceIntent.putExtra("mapList", mapList);
        startService(serviceIntent);
//        bindService(serviceIntent, sconn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gmap = googleMap;
        gmap.setMyLocationEnabled(true);
        gmap.setOnInfoWindowClickListener(this);
        gmap.setInfoWindowAdapter(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocation myLocation = new MyLocation();
        long minTime = 1000;
        float minDistance = 100;
        manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                myLocation
        );
        manager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                myLocation
        );
        // 현재 위치 가져와서 맵 화면 처음 실행할 때 현재 위치 보여주기
        curr_location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curr_location.getLatitude(), curr_location.getLongitude()), 8));

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getApplicationContext(), CoronaMapActivity.class);
        ArrayList<CoronaLocation> locationOnePerson = new ArrayList<>();
        Log.v("====", marker.getTitle() + "");
        Log.v("====", "mapList.size(): " + mapList.size() + "");

        for (int k = 0; k < items.size(); k++) {
            for (int j = 0; j < items.get(k).getArrayList().size(); j++) {
//                Log.v("====", "MapAllActivity : \"[\" + (items.get(k).getArrayList().get(j).getPatientName() + \"] \" + items.get(k).getArrayList().get(j).getName()) : " + "[" + (items.get(k).getArrayList().get(j).getPatientName() + "] " + items.get(k).getArrayList().get(j).getName()));
                if (("[" + (items.get(k).getArrayList().get(j).getPatientName() + "] " + items.get(k).getArrayList().get(j).getName())).equals(marker.getTitle())) {
                    locationOnePerson = items.get(k).getArrayList();
                    break;
                }
            }
        }
        Toast.makeText(CoronaMapAllActivity.this, locationOnePerson.get(0).getPatientName() + "의 맵으로 이동", Toast.LENGTH_SHORT).show();
        intent.putExtra("locationListSelected", locationOnePerson);
        startActivity(intent);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = getLayoutInflater().inflate(R.layout.map_info, null);
//        TextView tv_map_info_patientName = v.findViewById(R.id.tv_map_info_patientName);
        TextView tv_map_info_location = v.findViewById(R.id.tv_map_info_location);
        TextView tv_map_info_distance = v.findViewById(R.id.tv_map_info_distance);
//        TextView tv_map_info_others = v.findViewById(R.id.tv_map_info_others);
//        tv_map_info_patientName.setText("" + marker.getTitle());
        tv_map_info_location.setText(marker.getTitle());
        tv_map_info_distance.setText(marker.getSnippet() + " km away from me");

        return v;
    }
// CoronaMapAllActivity End


    class MyLocation implements LocationListener {
        int count = 0;

        @Override
        public void onLocationChanged(final Location curr_loc) {
            Log.d("---d count: ", ++count + "");
            gmap.setMyLocationEnabled(true);
            double lat = curr_loc.getLatitude();
            double lon = curr_loc.getLongitude();

            // Circle
            int radiusM = 1000; // set radius in meters
            int d = 500; // diameter
            Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            Paint p = new Paint();
            p.setColor(Color.GREEN);
            c.drawCircle(d / 2, d / 2, d / 2, p);
            // generate BitmapDescriptor from circle Bitmap
            BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);


            List<Marker> markerList = new ArrayList<Marker>();
            Marker marker;
            gmap.clear();
            markerList.clear();

            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_red);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);

//            float distance = ;
            LatLng[] latLngList = new LatLng[mapList.size()];
            Location[] locations = new Location[mapList.size()];
            for (int i = 0; i < mapList.size(); i++) {
                LatLng latLng = new LatLng(mapList.get(i).getLat(), mapList.get(i).getLon());
                final Location marker_loc = new Location("");
                marker_loc.setLatitude(mapList.get(i).getLat());
                marker_loc.setLongitude(mapList.get(i).getLon());
                locations[i] = marker_loc;

                DecimalFormat df = new DecimalFormat("0.00");
                MarkerOptions myMarkerOptions = new MarkerOptions()
                        .position(new LatLng(mapList.get(i).getLat(), mapList.get(i).getLon()))
                        .title("[" + mapList.get(i).getPatientName() + "] " + mapList.get(i).getName())
                        .snippet(df.format(curr_loc.distanceTo(marker_loc) / 1000))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                float distance = curr_loc.distanceTo(marker_loc);
                //                Log.d("---distance", distance + "");
                marker = gmap.addMarker(myMarkerOptions);
                markerList.add(marker);

                latLngList[i] = latLng;

                // draw circle
                gmap.addGroundOverlay(new GroundOverlayOptions()
                        .image(bmD)
                        .position(latLng, radiusM * 2, radiusM * 2)
                        .transparency(0.8f));
            }

            for (int i = 0; i < mapList.size() - 1; i++) {
                Polyline polyline = gmap.addPolyline(new PolylineOptions()
                        .add(latLngList[i], latLngList[i + 1])
                        .width(5)
                        .color(Color.BLUE));
            }

            // 내 장소로 화면을 이동하면서 나의 위치가 확진자가 다녀간 장소 중
            // 1키로 미터 반경 안에 포함되는 경우 (1)진동, (2)토스트, (3)푸시를 한다.
            for (int i = 0; i < mapList.size(); i++) {
                Log.d("---d ", mapList.get(i).getName() + ": " + curr_loc.distanceTo(locations[i]) / 1000 + "");
                if (curr_loc.distanceTo(locations[i]) / 1000 < 1 && !state.equals("ENTER")) {
                    list_loc_enter.add(mapList.get(i));
                    Log.d("---d list_loc_enter.size(): ", list_loc_enter.size() + "");
                    if (Build.VERSION.SDK_INT >= 26) {
                        Toast.makeText(CoronaMapAllActivity.this, "위험위험!", Toast.LENGTH_SHORT).show();
                        vibrator.vibrate(VibrationEffect.createOneShot(1000, 255));
                        MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.r);
                        player.start();
                    } else {
                        Toast.makeText(CoronaMapAllActivity.this, "위험위험!", Toast.LENGTH_SHORT).show();
                        vibrator.vibrate(1000);
                    }
                    state = "ENTER";
                    Log.d("---d enter: ", list_loc_enter.get(list_loc_enter.size() - 1).getPatientName() + " " + list_loc_enter.get(list_loc_enter.size() - 1).getName());
                } else if (curr_loc.distanceTo(locations[i]) / 1000 < 1 && state.equals("ENTER")) {
                    Log.d("---d ", "do nothing");
                } else if (curr_loc.distanceTo(locations[i]) / 1000 > 1 && state.equals("ENTER")) {
                    Log.d("---d ", "should change to exit");
                    for (int j = 0; j < list_loc_enter.size(); j++) {
                        Log.d("---d", i + " : " + mapList.get(i).getName() + "");
                        Log.d("---d", j + " : " + list_loc_enter.get(j).getName() + "");
                        if (mapList.get(i).getName().equals(list_loc_enter.get(j).getName())) {
                            state = "EXIT";
                            Log.d("---d exit: ", list_loc_enter.get(j).getPatientName() + " " + list_loc_enter.get(j).getName());
                        }
                    }
                }
            }
            Log.d("---d state::", state);
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