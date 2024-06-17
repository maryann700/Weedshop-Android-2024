package com.weedshop.driver.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.GPSTracker;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import java.util.HashMap;

/**
 * Created by MTPC-83 on 5/11/2017.
 */

public class MyLocationService extends Service implements OnTaskCompleted {

    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private SharedPreferences sharedpreferences;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Context context;
    private Handler handler;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Intent intent = new Intent("LocationUpdate");
            // You can also include some extra data.
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            LocalBroadcastManager.getInstance(MyLocationService.this).sendBroadcast(intent);

            // update_driver_location api call when driver location change
            HashMap<String, String> map = new HashMap<>();
            map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
            map.put("latitude", String.valueOf(latitude));
            map.put("longitude", String.valueOf(longitude));
            ServiceHandler task = new ServiceHandler(MyLocationService.this, Constant.update_driver_location, map, false,getApplicationContext());
            task.executeAsync();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        context = this;
        sharedpreferences = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        Log.e(TAG, "onCreate");


        initializeLocationManager(context);


        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }


    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        if (handler != null) {
            handler.removeCallbacks(runnableLocation);
        }
    }

    private void initializeLocationManager(final Context context) {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnableLocation, 10000);


    }

    Runnable runnableLocation = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "Call main loop");
            handler.postDelayed(runnableLocation, 10000);
            GPSTracker objGpsTracker = new GPSTracker(context);
            if (!objGpsTracker.canGetLocation()) {
                Intent intent = new Intent("LocationCheck");
                LocalBroadcastManager.getInstance(MyLocationService.this).sendBroadcast(intent);
            }
        }
    };

    @Override
    public void onTaskCompleted(String result, int requestCode) {

    }


}
