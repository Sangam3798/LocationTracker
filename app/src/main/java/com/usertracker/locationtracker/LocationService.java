package com.usertracker.locationtracker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class LocationService extends Service {
    private LocationRequest mLocationRequest;

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull @org.jetbrains.annotations.NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d("location_of_user", "latitude" + "longitude 3");
            if (locationResult != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Log.d("location_of_user", "latitude" + latitude + "longitude" + longitude);

            }

        }
    };

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationService() {
        Log.d("location_of_user", "latitude" + "longitude");
        String channelId = "location_notification_channel_id";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(getApplicationContext(), LocationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle("LocationTracker");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running......");
        builder.setAutoCancel(false);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null &&
                    notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This Channel is used by Location Tracker");
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d("location_of_user", "latitude" + "longitude 1");
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Log.d("location_of_user", "latitude" + "longitude 2");
        startForeground(Constant.LOCATION_SERVICE_ID, builder.build());
    }


    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            Log.d("location_of_user", "latitude" + "longitude 1"+locationResult.getLastLocation().getLatitude());
                        }
                    },
                    Looper.myLooper());
            return;
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action =  intent.getAction();
            if(action != null)
            {
                if(action.equals(Constant.ACTION_START_LOCATION_SERVICE))
                {
                    //startLocationUpdates();
                    startLocationService();
                }
                else if(action.equals(Constant.ACTION_STOP_LOCATION_SERVICE))
                    {
                        stopLocationService();
                    }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
