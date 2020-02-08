package com.example.coronaMapProject;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class CoronaLocationNearAlertService extends Service {

    Location[] locations;
    String result;
    public static ArrayList<CoronaLocation> coronaLocations;
    public static ArrayList<CoronaLocation> list_loc_enter = new ArrayList<>();
    Vibrator vibrator;
    String state = "NONE";

    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    Intent intent;

    PendingIntent pendingIntent;
    NotificationCompat.Builder mBuilder;
    NotificationManager notificationManager;


    class MyBinder extends Binder {
        public CoronaLocationNearAlertService getService() {
            return CoronaLocationNearAlertService.this;
        }
    }

    IBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

//    public ArrayList<CoronaLocation> getList() {
//        return coronaLocations;
//    }


    public CoronaLocationNearAlertService() {
    }

    @Override
    public void onCreate() {
        intent = new Intent(BROADCAST_ACTION);
        SharedPreferences sp = getSharedPreferences("sp", Activity.MODE_PRIVATE);
        if (sp != null && sp.contains("data")) {
            result = sp.getString("data", "");
        }
        coronaLocations = new ArrayList<>();
        JSONArray ja = null;
        try {
            ja = new JSONArray(result);
            JSONArray ja_location = null;
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String id = jo.getString("id");
                String name = jo.getString("name");
                String sex = jo.getString("sex");
                int age = jo.getInt("age");
                String nationality = jo.getString("nationality");
                int meet = jo.getInt("meet");
                int risk = jo.getInt("risk");
                String img = jo.getString("img");
                ja_location = jo.getJSONArray("location");
                ArrayList<CoronaLocation> list_location = new ArrayList<>();
                for (int j = 0; j < ja_location.length(); j++) {
                    JSONObject jo_location = ja_location.getJSONObject(j);
                    int no = jo_location.getInt("no");
                    String name_location = jo_location.getString("name");
                    Double lat = jo_location.getDouble("lat");
                    Double lon = jo_location.getDouble("lon");
                    CoronaLocation location = new CoronaLocation(no, name_location, lat, lon);
                    list_location.add(location);
                    Log.d("---", ":::" + list_location.get(j).getName() + "");
                    CoronaLocation map = new CoronaLocation(no, name_location, lat, lon, name);
                    coronaLocations.add(map);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), CoronaMapAllActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder =
                new NotificationCompat.Builder(CoronaLocationNearAlertService.this)
                        .setSmallIcon(R.drawable.marker_red)
                        .setContentTitle("코로나 확진자 장소에 접근하였습니다.")
                        .setContentText("확인하기")
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//
//        String[] permissions = {
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//        };

//        ActivityCompat.requestPermissions(this, permissions, 101);
        Log.d("--- service: ", "onCreate");
    }


    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("--- service: ", "onStartCommand");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }


    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("*****", "Location changed");
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                Location curr_location = new Location("");
                curr_location.setLatitude(loc.getLatitude());
                curr_location.setLongitude(loc.getLongitude());
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);
                Log.d("---", loc.getLatitude() + " : " + loc.getLongitude());
                locations = new Location[coronaLocations.size()];
                for (int i = 0; i < coronaLocations.size(); i++) {
                    Location location = new Location("");
                    location.setLatitude(coronaLocations.get(i).getLat());
                    location.setLongitude(coronaLocations.get(i).getLon());
                    locations[i] = location;
                }
                for (int i = 0; i < coronaLocations.size(); i++) {
                    float distance = (curr_location.distanceTo(locations[i])) / 1000;
//                            Log.d("--- service: ", "distance: " + distance + " km");
                    if (distance < 1) {
                        Log.d("--- service: ", "distance < 1: " + distance + " km");
                    }
                }

                // 내 장소로 화면을 이동하면서 나의 위치가 확진자가 다녀간 장소 중
                // 1키로 미터 반경 안에 포함되는 경우 (1)진동, (2)토스트, (3)푸시를 한다.
                for (int i = 0; i < coronaLocations.size(); i++) {
//                        Log.d("---d ", coronaLocations.get(i).getName() + ": " + curr_location.distanceTo(locations[i]) / 1000 + "");
                    if (curr_location.distanceTo(locations[i]) / 1000 < 1 && !state.equals("ENTER")) {
                        list_loc_enter.add(coronaLocations.get(i));
//                            Log.d("---d list_loc_enter.size(): ", list_loc_enter.size() + "");
                        if (Build.VERSION.SDK_INT >= 26) {
                            Toast.makeText(CoronaLocationNearAlertService.this, "Warning!", Toast.LENGTH_SHORT).show();
                            vibrator.vibrate(VibrationEffect.createOneShot(1000, 255));
                        } else {
                            Toast.makeText(CoronaLocationNearAlertService.this, "Warning!", Toast.LENGTH_SHORT).show();
                            vibrator.vibrate(1000);
                        }
                        state = "ENTER";
//                            Log.d("---d enter: ", list_loc_enter.get(list_loc_enter.size() - 1).getPatientName() + " " + list_loc_enter.get(list_loc_enter.size() - 1).getName());
                        notificationManager.notify(0, mBuilder.build());
                    } else if (curr_location.distanceTo(locations[i]) / 1000 < 1 && state.equals("ENTER")) {
//                            Log.d("---d ", "do nothing");
                    } else if (curr_location.distanceTo(locations[i]) / 1000 > 1 && state.equals("ENTER")) {
//                            Log.d("---d ", "should change to exit");
                        for (int j = 0; j < list_loc_enter.size(); j++) {
//                                Log.d("---d", i + " : " + coronaLocations.get(i).getName() + "");
//                                Log.d("---d", j + " : " + list_loc_enter.get(j).getName() + "");
                            if (coronaLocations.get(i).getName().equals(list_loc_enter.get(j).getName())) {
                                state = "EXIT";
//                                    Log.d("---d exit: ", list_loc_enter.get(j).getPatientName() + " " + list_loc_enter.get(j).getName());
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
//            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
//            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }
}


