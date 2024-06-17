package com.weedshop.driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.GPSTracker;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/*
 * This is a splash screen, will be display for a short time to user.
 */
public class SplashActivity extends BaseActivity /*implements OnTaskCompleted */ {
    private static final int REQUEST_LOCATION = 2;
    private static String[] PERMISSIONS_LOCATION = {ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION};
    private Handler m_handler;
    public static String TAG = SplashActivity.class.getSimpleName();
    String stringLatitude, stringLongitude;
    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermission(ACCESS_FINE_LOCATION) && hasPermission(ACCESS_COARSE_LOCATION)) {
            gpsTracker = new GPSTracker(this);
            if (gpsTracker.canGetLocation()) {
                stringLatitude = String.valueOf(gpsTracker.getLatitude());
                stringLongitude = String.valueOf(gpsTracker.getLongitude());
                Log.e("LAT LONG : ", stringLatitude + " - " + stringLongitude);

                boolean isCalifornia = CommonUtils.isCalifornia(this, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                if (!isCalifornia) {
                    CommonUtils.showDialogNotCancelable(this, getString(R.string.app_name), "Currently we are not Serving in your City.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                } else {
                    splashScreen();
                }
            } else {
                gpsTracker.showSettingsAlert();
                return;
            }
        } else {
            requestLocationPermissions();
            return;
        }
    }

    private void splashScreen() {
        m_handler = new Handler();
        m_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
                Intent m_intent = null;

                if (!prefs.getBoolean(Constant.ISPHOTOUPLOAD, false) &&
                        !prefs.getBoolean(Constant.ISLOGIN, false) &&
                        !prefs.getBoolean(Constant.ISDOCUMENTUPLOAD, false) &&
                        !prefs.getBoolean(Constant.ISVERYFINEPERDETAIL, false)
                        ) {
                    m_intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(m_intent);
                    finish();
                } else if (!prefs.getBoolean(Constant.ISVERIFYEMAIL, false)) {
                    m_intent = new Intent(SplashActivity.this, VerificationActivity.class);
                    startActivity(m_intent);
                    finish();
                } else if (!prefs.getBoolean(Constant.ISPHOTOUPLOAD, false)) {
                    m_intent = new Intent(SplashActivity.this, AddIdentificationActivity.class);
                    startActivity(m_intent);
                    finish();
                } else if (!prefs.getBoolean(Constant.ISDOCUMENTUPLOAD, false)) {
                    m_intent = new Intent(SplashActivity.this, CarDetailsActivity.class);
                    startActivity(m_intent);
                    finish();
                } else if (!prefs.getBoolean(Constant.ISVERYFINEPERDETAIL, false)) {
                    m_intent = new Intent(SplashActivity.this, VerifyProcessActivity.class);
                    startActivity(m_intent);
                    finish();
                } else {
                    m_intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(m_intent);
                    finish();
                }

//                if (prefs.getBoolean(Constant.ISLOGIN, false)) {
//                    m_intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(m_intent);
//                    finish();
//                } else {
//                    m_intent = new Intent(SplashActivity.this, SignInActivity.class);
//                    startActivity(m_intent);
//                    finish();
//                }
            }
        }, 3000);
    }

    /**
     * Requests the Contacts permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    @SuppressLint("NewApi")
    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            requestPermissions(PERMISSIONS_LOCATION, REQUEST_LOCATION);
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    @SuppressLint("NewApi")
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /*Permissions*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  showToast("Permission Granted, Now you can access camera.");
                    if (gpsTracker == null) gpsTracker = new GPSTracker(this);
                    if (gpsTracker.canGetLocation()) {
                        boolean isCalifornia = CommonUtils.isCalifornia(this, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                        if (!isCalifornia) {
                            CommonUtils.showDialogNotCancelable(this, getString(R.string.app_name), "Currently we are not Serving in your City.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                        } else {
                            splashScreen();
                        }
                    } else {
                        gpsTracker.showSettingsAlert();
                        return;
                    }
                } else {
                    // showToast("Permission Denied, You cannot access application.");
                    //code for deny
                    CommonUtils.showDialogNotCancelable(this, "Permission", "Please grant permissions to access application.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestLocationPermissions();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

