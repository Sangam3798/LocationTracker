package com.usertracker.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    private static  final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private String[] permission  = {""};
    private static  boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.startButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ContextCompat.
                                checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED && ContextCompat.
                                checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_CODE_LOCATION_PERMISSION
                            );
                        }
                        else
                            {
                                startLocationService();
                            }
                    }
                }
        );
        findViewById(R.id.stopButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopLocationService();
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && checkBackgroundLocationPermission())
            {
                startLocationService();
            }
        }
    }

    private boolean checkBackgroundLocationPermission()
    {
        if(runningQOrLater)
        {
          return ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return  true;
    }

    private boolean isLocationServiceRunning()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null)
        {
            for(ActivityManager.RunningServiceInfo serviceInfo:
                    activityManager.getRunningServices(Integer.MAX_VALUE))
            {
                if(LocationService.class.getName().equals(serviceInfo.service.getClassName()))
                {
                    if(serviceInfo.foreground)
                    {
                        return  true;
                    }
                }
            }
            return  false;
        }
        return  false;
    }

    private void startLocationService()
    {
        if(!isLocationServiceRunning())
        {
            System.out.println("isAppInforeground"+isLocationServiceRunning());
            Intent intent = new Intent(getApplicationContext(),LocationService.class);
            intent.setAction(Constant.ACTION_START_LOCATION_SERVICE);
            startService(intent);

        }
    }

    private void stopLocationService()
    {
        Intent intent = new Intent(getApplicationContext(),LocationService.class);
        intent.setAction(Constant.ACTION_STOP_LOCATION_SERVICE);
        startService(intent);
    }
}