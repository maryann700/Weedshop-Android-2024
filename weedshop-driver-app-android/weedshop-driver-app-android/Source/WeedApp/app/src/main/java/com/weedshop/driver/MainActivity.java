package com.weedshop.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.triggertrap.seekarc.SeekArc;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.weedshop.driver.Adapter.WizardPagerAdapter;
import com.weedshop.driver.services.MyLocationService;
import com.weedshop.driver.services.OrderRequestService;
import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.MarshMallowPermission;
import com.weedshop.driver.utils.NotificationUtils;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.utils.RoundedImageView;
import com.weedshop.driver.utils.WrapContentViewPager;
import com.weedshop.driver.webservices.ServiceHandler;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static com.weedshop.driver.utils.Constant.ISCURRENTORDER;
import static com.weedshop.driver.utils.Constant.ISDRIVERORDERREQUEST;
import static com.weedshop.driver.utils.Constant.ISPICKUPORDER;
import static com.weedshop.driver.utils.Constant.PREF_order_id;
import static com.weedshop.driver.utils.Constant.getStatuscode_driver_order_deliver;
import static com.weedshop.driver.utils.Constant.getStatuscode_driver_order_pickup;
import static com.weedshop.driver.utils.Constant.statuscode_driver_current_order_detail;
import static com.weedshop.driver.utils.Constant.statuscode_request_action_accept;
import static com.weedshop.driver.utils.Constant.statuscode_request_action_decline;

public class MainActivity extends SideMenuActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnTaskCompleted {

    public static String TAG = MainActivity.class.getSimpleName();

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    private int mCurrentTransitionEffect = JazzyHelper.CARDS;
    private TextView tv_current_location;
    private LinearLayout bottom_layout;

    /*for MarshMallow Permission*/
    private MarshMallowPermission marshMallowPermission;

    LocationManager locationManager;
    private LocationListener locationListener;
    private String current_location = "";
    int reTryCount = 0;
    Location l;
    int animate = 0;

    private Animation animSlideR;
    private Animation animSlideL;
    private ImageView imgdelivery;
    private ImageView imgshop;
    private TextView txtShop;
    private TextView txtdelivery;

    // for bottom view
    private RelativeLayout rltvbottom;
    private TextView txtyouadd;
    private TextView txtshopadd;
    private TextView txtshopkm;
    private TextView txtshoptime;
    private TextView txtshopprice;
    private TextView txtshopfname;
    private TextView txtfirstchoice;
    private TextView txtdeliveryshopadd;
    private TextView txtdeliveryadd;
    private TextView txtkm;
    private TextView txttime;
    private TextView txtprice;
    private TextView txtfname;
    private TextView shopbtn;
    private RoundedImageView imgshopprofile;
    private RoundedImageView imgprofile;


    // for draw green line
    private ArrayList<LatLng> points;
    Polyline line; //added Initialize points in onCreate():

    private SlidingUpPanelLayout sliding_layout;
    private FrameLayout frmcall;
    private FrameLayout frmdeliverphone;

    Marker shopMarker, driverMarker, delevMarker;

    private float zoomLevel = 0;
    private static String[] PERMISSIONS_CALL = {CALL_PHONE};
    private static final int REQUEST_PHONE_CALL = 2;
    private static final int REQUEST_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION};

    AlertDialog.Builder alertDialog;
    private boolean isDialogShow = false;
    private boolean isPermisionDialogShow = false;

    private TextView txtorderid;
    private Dialog dialogAccrptDecline;

    private WrapContentViewPager pager;
    private boolean flagForCurrentOrder = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_main);
        mMenuDrawer.setMenuView(R.layout.activity_base_menu);

        marshMallowPermission = new MarshMallowPermission(MainActivity.this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        bottom_layout = (LinearLayout) findViewById(R.id.bottom_layout);

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mFragment.getMapAsync(MainActivity.this);

        initToolbar(false);
        getClassName(TAG);
        TextView si_txt_title_signup = (TextView) findViewById(R.id.si_txt_title_signup);
        si_txt_title_signup.setText("Your Current Location");

        rltvbottom = (RelativeLayout) findViewById(R.id.rltvbottom);
        txtyouadd = (TextView) findViewById(R.id.txtyouadd);
        txtshopadd = (TextView) findViewById(R.id.txtshopadd);
        txtshopkm = (TextView) findViewById(R.id.txtshopkm);
        txtshoptime = (TextView) findViewById(R.id.txtshoptime);
        txtshopprice = (TextView) findViewById(R.id.txtshopprice);
        txtshopfname = (TextView) findViewById(R.id.txtshopfname);
        txtfirstchoice = (TextView) findViewById(R.id.txtfirstchoice);
        txtdeliveryshopadd = (TextView) findViewById(R.id.txtdeliveryshopadd);
        txtdeliveryadd = (TextView) findViewById(R.id.txtdeliveryadd);
        txtkm = (TextView) findViewById(R.id.txtkm);
        txttime = (TextView) findViewById(R.id.txttime);
        txtprice = (TextView) findViewById(R.id.txtprice);
        txtfname = (TextView) findViewById(R.id.txtfname);
        shopbtn = (TextView) findViewById(R.id.shopbtn);
        imgshopprofile = (RoundedImageView) findViewById(R.id.imgshopprofile);
        imgprofile = (RoundedImageView) findViewById(R.id.imgprofile);
        txtorderid = (TextView) findViewById(R.id.txtorderid);

        tv_current_location = (TextView) findViewById(R.id.tv_current_location);

        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//        sliding_layout.setPanelHeight(0);

        frmcall = (FrameLayout) findViewById(R.id.frmcall);
        frmdeliverphone = (FrameLayout) findViewById(R.id.frmdeliverphone);

        bottom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                callLocation();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!marshMallowPermission.checkPermissionForAccessCorseLocation() && !marshMallowPermission.checkPermissionForAccessFineLocation()) {
                marshMallowPermission.requestPermissionForFineLocation();
                marshMallowPermission.requestPermissionForCorseLocation();
            }
        } else {
            callLocation();
        }

        animSlideR = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.slide_right);
        animSlideL = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.slide_left);
        imgdelivery = (ImageView) findViewById(R.id.imgdelivery);
        imgshop = (ImageView) findViewById(R.id.imgshop);
        txtShop = (TextView) findViewById(R.id.txtShop);
        txtdelivery = (TextView) findViewById(R.id.txtdelivery);

// for draw green line
        points = new ArrayList<LatLng>();

        if (!hasPermission(ACCESS_COARSE_LOCATION)) {
            requestLocationPermissions();
        }

        Intent i = new Intent(MainActivity.this, MyLocationService.class);
        startService(i);

        Intent igetOrder = new Intent(MainActivity.this, OrderRequestService.class);
        startService(igetOrder);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("LocationUpdate"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mDriverOrderReceiver,
                new IntentFilter("DriverOrderRequest"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationCheck,
                new IntentFilter("LocationCheck"));

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(ISDRIVERORDERREQUEST, false);
        editor.apply();

        findViewById(R.id.iv_open_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (sharedpreferences.getBoolean(ISCURRENTORDER, false)) {
//                    startActivity(new Intent(MainActivity.this, CurrentOrderActivity.class));
//                } else {
//                    Toast.makeText(MainActivity.this, "There is not any order", Toast.LENGTH_SHORT).show();
//                }

                startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
            }
        });

//        shopData();

        txtorderid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CurrentOrderActivity.class));
            }
        });

    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "LocationUpdate" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            Double currentLatitude = intent.getDoubleExtra("latitude", 0);
            Double currentLongitude = intent.getDoubleExtra("longitude", 0);

            /*Remove comment before live*/
            boolean isCalifornia = CommonUtils.isCalifornia(MainActivity.this, currentLatitude, currentLongitude);
            if (!isCalifornia) {
                CommonUtils.showDialogNotCancelable(MainActivity.this, getString(R.string.app_name), "Currently we are not Serving in your City.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
            }

            currentLatLng = new LatLng(currentLatitude, currentLongitude);

            if (!TextUtils.isEmpty(sharedpreferences.getString(PREF_order_id, ""))) {
                moveMarker(currentLatitude, currentLongitude, "You", R.drawable.youimage, true);
            } else {
                moveMarker(currentLatitude, currentLongitude);
            }
            Log.e("receiver", "Got message: " + currentLatitude);
            if (flagForCurrentOrder) {
                driverCurrentOrderDetail();
            }
            //       }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    private BroadcastReceiver mLocationCheck = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            if (hasPermission(ACCESS_COARSE_LOCATION) && hasPermission(ACCESS_FINE_LOCATION)) {
                showSettingsAlert();
            } else {
                requestLocationPermissions();
            }
        }
    };

    public void showSettingsAlert() {
        isDialogShow = true;
        alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        isDialogShow = false;
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }


    private BroadcastReceiver mDriverOrderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            String jsonString = intent.getStringExtra("jsonObject");
            try {
                JSONObject jObj = new JSONObject(jsonString);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(ISDRIVERORDERREQUEST, true);
                editor.apply();
                if (jObj.getString("status").equals("Inprocess")) {
//                    (!TextUtils.isEmpty(sharedpreferences.getString(PREF_order_id, ""))) {
                    sharedpreferences.edit().putString(PREF_order_id, jObj.getString("id")).apply();
                    driverCurrentOrderDetail();
                } else {
                    showCurrentOrderPopup(jObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("receiver jsonString", "Got message: " + jsonString);
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void callLocation() {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getString(R.string.gps_not_enabled));
            dialog.setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent, 11);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//
//        if (mLastLocation != null) {
//            //place marker at current position
//            //mGoogleMap.clear();
//            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.title("You");
//            /*int hue = 161;
//
//            markerOptions.icon(BitmapDescriptorFactory
//                    .defaultMarker(hue));*/
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
//
//            currLocationMarker = mGoogleMap.addMarker(markerOptions);
//
//            currLocationMarker.showInfoWindow();
//
//
//            //Move the camera to the user's location and zoom in!
//            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
//
//            if (latLng.latitude != 0.00 && latLng.longitude != 0.00) {
//
////                try {
////                    List<Address> addresses;
////                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
////                    Locale.setDefault(new Locale("en", "US"));
////                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
////
////                    if (addresses != null) {
////
////                        String address = addresses.get(0).getAddressLine(0);
////                        String address11 = addresses.get(0).getAddressLine(1);
////                        String city = addresses.get(0).getLocality();
////                        String state = addresses.get(0).getAdminArea();
////                        String country = addresses.get(0).getCountryName();
////                        String postalCode = addresses.get(0).getPostalCode();
////                        String knownName = addresses.get(0).getFeatureName();
////
////                        tv_current_location.setText(address + ", " + address11 + ", " + city + ", " + state + ", " + country);
////                    }
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//
////                getAddress();
//                new getAddress().execute();
////                LatLng origin = latLng;
////                LatLng dest = new LatLng(22.995727, 72.530887);
////
////                // Getting URL to the Google Directions API
////                String url = getDirectionsUrl(origin, dest);
////
////                DownloadTask downloadTask = new DownloadTask();
////
////                // Start downloading json data from Google Directions API
////                downloadTask.execute(url);
//
//                points.add(latLng);
//                greenRawLine();
//            }
//        }
//
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(5000); //5 seconds
//        mLocationRequest.setFastestInterval(3000); //3 seconds
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

//        //place marker at current position
////        mGoogleMap.clear();
//        if (currLocationMarker != null) {
//            currLocationMarker.remove();
//        }
//
//
//        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        Log.e("change location", "" + latLng.latitude + "  long-> " + latLng.longitude);
//
//        if (latLng.latitude != 0.00 && latLng.longitude != 0.00) {
//            //                List<Address> addresses;
////                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
////                Locale.setDefault(new Locale("en", "US"));
////                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
////
////
////
////                if (addresses != null) {
////
////                    String address = addresses.get(0).getAddressLine(0);
////                    String address11 = addresses.get(0).getAddressLine(1);
////                    String city = addresses.get(0).getLocality();
////                    String state = addresses.get(0).getAdminArea();
////                    String country = addresses.get(0).getCountryName();
////                    String postalCode = addresses.get(0).getPostalCode();
////                    String knownName = addresses.get(0).getFeatureName();
////
////                    tv_current_location.setText(address + ", " + address11 + ", " + city + ", " + state + ", " + country);
////                }
//
////            getAddress();
////            new getAddress().execute();
//            points.add(latLng);
//            greenRawLine();
//        }
//
//// Add  title on marker
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("You");
//
//       /* int hue = 161;
//
//        markerOptions.icon(BitmapDescriptorFactory
//                .defaultMarker(hue));*/
//
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
//
//        currLocationMarker = mGoogleMap.addMarker(markerOptions);
//        currLocationMarker.showInfoWindow();
//        //zoom to current position:
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(latLng).zoom(14).build();
//
//        mGoogleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        MapStyleManager styleManager = MapStyleManager.attachToMap(this, mGoogleMap);
        styleManager.addStyle(0, R.raw.map_style_silver_sparse);


//        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//            // Use default InfoWindow frame
//            @Override
//            public View getInfoWindow(Marker arg0) {
//                // Getting view from the layout file info_window_layout
//                View v = getLayoutInflater().inflate(R.layout.map_infowindow, null);
//
//                TextView tvLat = (TextView) v.findViewById(R.id.tv_infowindow);
//                tvLat.setText("You");
//
//                return (v);
//            }
//
//            // Defines the contents of the InfoWindow
//            @Override
//            public View getInfoContents(Marker arg0) {
//                return null;
//            }
//        });


//        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            // Use default InfoWindow frame
//            @Override
//            public View getInfoWindow(Marker marker) {
//                // Getting view from the layout file info_window_layout
//                View v = getLayoutInflater().inflate(R.layout.map_infowindow, null);
//                TextView tvLat = (TextView) v.findViewById(R.id.tv_infowindow);
//
////                if (marker.getSnippet().toString().equalsIgnoreCase("Shop")) {
////                    tvLat.setBackgroundResource(R.drawable.bubble_black);
////                    tvLat.setText("Shop");
////                } else if (marker.getSnippet().toString().equalsIgnoreCase("You")) {
////                    tvLat.setBackgroundResource(R.drawable.bubble_cyan);
////                    tvLat.setText("You");
////                } else if (marker.getSnippet().toString().equalsIgnoreCase("Driver")) {
////                    tvLat.setBackgroundResource(R.drawable.bubble_green);
////                    tvLat.setText("Driver");
////                }
//                return (v);
//            }
//
//            // Defines the contents of the InfoWindow
//            @Override
//            public View getInfoContents(Marker arg0) {
//                return null;
//            }
//        });

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    private void buildGoogleApiClient() {

        // Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/


    public void deliveryClick(View view) {
//        deliveryData();
        pager.setCurrentItem(1);

    }

    public void onShopClick(View view) {
        pager.setCurrentItem(0);
    }

    private void deliveryData() {
//        findViewById(lnrshop).setVisibility(View.GONE);
        txtShop.setTextColor(Color.parseColor("#A2AAB7"));
        txtdelivery.setTextColor(Color.parseColor("#2CCD9B"));
        imgshop.setImageResource(R.drawable.img_dot_unselected);
        imgdelivery.setImageResource(R.drawable.img_dot_selected);
//        LinearLayout lnrdelivery = (LinearLayout) findViewById(R.id.lnrdelivery);
//        lnrdelivery.setVisibility(View.VISIBLE);
//        lnrdelivery.startAnimation(animSlideL);
    }

    private void shopData() {
//        findViewById(R.id.lnrdelivery).setVisibility(View.GONE);
        txtdelivery.setTextColor(Color.parseColor("#A2AAB7"));
        txtShop.setTextColor(Color.parseColor("#2CCD9B"));
        imgdelivery.setImageResource(R.drawable.img_dot_unselected);
        imgshop.setImageResource(R.drawable.img_dot_selected);
//        LinearLayout lnrshop = (LinearLayout) findViewById(R.id.lnrshop);
//        lnrshop.setVisibility(View.VISIBLE);
//        lnrshop.startAnimation(animSlideR);
    }

    private String getAddress() {
        HttpURLConnection connection = null;
        URL serverAddress = null;
//        Double latitude = 37.422006;
//        Double longitude = -122.084095;

        String responseText = null;
        try {
            // build the URL using the latitude & longitude you want to lookup
            // NOTE: I chose XML return format here but you can choose something else
//            https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=YOUR_API_KEY
//            serverAddress = new URL("http://maps.google.com/maps/geo?q=" + Double.toString(latitude) + "," + Double.toString(longitude) +
//                    "&output=xml&oe=utf8&sensor=true&key=" +R.string.google_maps_key);
            serverAddress = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + Double.toString(latLng.latitude) + "," + Double.toString(latLng.longitude) +
                    "&output=xml&oe=utf8&sensor=true&key=AIzaSyBXsHrxEjAcuFm6QOSQzva-g3UZgKNejIA");
            //set up out communications stuff
            connection = null;

            //Set up the initial connection
            connection = (HttpURLConnection) serverAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);

            connection.connect();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = rd.readLine()) != null) {
                sb.append(line + '\n');
            }
            // now the response is packaged in a string,
            // parse the XML or whatever you need
            responseText = sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseText;
    }


    @Override
    public void onTaskCompleted(String result, int requestCode) {
        if (!TextUtils.isEmpty(result)) {
            if (requestCode == statuscode_request_action_accept) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    if (response) {
                        JSONArray array = jsonObject.getJSONArray("data");
                        if (array.length() > 0) {
                            for (int i = 0; i <= array.length(); i++) {
                                JSONObject jsonObject1 = array.getJSONObject(i);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(PREF_order_id, jsonObject1.getString("id"));
                                editor.apply();

//                                setCurrentDeliveryOrderDetail(jsonObject1);
                                setCurrentShopOrderDetail(jsonObject1);

                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == statuscode_driver_current_order_detail) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    if (response) {
                        JSONArray array = jsonObject.getJSONArray("data");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject1 = array.getJSONObject(i);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(PREF_order_id, jsonObject1.getString("id"));
                                editor.putBoolean(ISCURRENTORDER, true);
                                editor.apply();

//                                setCurrentDeliveryOrderDetail(jsonObject1);
                                setCurrentShopOrderDetail(jsonObject1);

                            }
                        }
                    } else {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(PREF_order_id, "");
                        editor.putBoolean(ISCURRENTORDER, false);
                        editor.apply();
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == getStatuscode_driver_order_pickup) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    if (response) {
                        sharedpreferences.edit().putBoolean(ISPICKUPORDER, true).apply();
//                        deliveryData();
                    } else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == getStatuscode_driver_order_deliver) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    if (response) {
                        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//                        mLayout.setPanelState(PanelState.COLLAPSED);
                        rltvbottom.setVisibility(View.GONE);
                        bottom_layout.setVisibility(View.VISIBLE);
                        txtorderid.setVisibility(View.GONE);
                        mGoogleMap.clear();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(ISCURRENTORDER, false);
                        editor.putString(PREF_order_id, "");
                        editor.putBoolean(ISPICKUPORDER, false);
                        editor.apply();

                    } else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void driverCurrentOrderDetail() {
        flagForCurrentOrder = false;
        if (!TextUtils.isEmpty(sharedpreferences.getString(PREF_order_id, ""))) {
            HashMap<String, String> map = new HashMap<>();
            map.put("order_id", sharedpreferences.getString(PREF_order_id, ""));
//        map.put("order_id","71");
            ServiceHandler task = new ServiceHandler(MainActivity.this, Constant.driver_current_order_detail, map, statuscode_driver_current_order_detail, true, getApplicationContext());
            task.executeAsync();
        }
    }

    @SuppressLint("NewApi")
    private void requestCallPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, CALL_PHONE)) {
            requestPermissions(PERMISSIONS_CALL, REQUEST_PHONE_CALL);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_CALL, REQUEST_PHONE_CALL);
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

    private void setCurrentShopOrderDetail(final JSONObject jsonObject) {

        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        WizardPagerAdapter adapter = new WizardPagerAdapter();
        pager = (WrapContentViewPager) findViewById(R.id.pager1);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    shopData();
                } else {
                    deliveryData();
                }
                pager.reMeasureCurrentPage(pager.getCurrentItem());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        rltvbottom.setVisibility(View.VISIBLE);
//        sliding_layout.setPanelHeight(80);


        bottom_layout.setVisibility(View.GONE);
        deliveryClick(null);
        try {
            txtorderid.setVisibility(View.VISIBLE);
            txtorderid.setText("Order id : " + jsonObject.getString("order_code"));
            txtyouadd.setText(jsonObject.getString("driver_address"));
            txtshopadd.setText(jsonObject.getString("store_address"));
            try {
                double distance = Double.parseDouble(jsonObject.getString("store_distance"));
                txtshopkm.setText(new DecimalFormat("##.##").format(distance) + "km");
            } catch (Exception e) {

            }

            if (!TextUtils.isEmpty(jsonObject.getString("store_image"))) {
                SimpleTarget targetStore = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        imgshopprofile.setImageBitmap(bitmap);
                    }
                };
                if (!TextUtils.isEmpty(jsonObject.getString("store_image_url"))) {
                    Glide.with(MainActivity.this).load(jsonObject.getString("store_image_url")).asBitmap().into(targetStore);
                }
            }

            txtshoptime.setText(jsonObject.getString("store_time"));
            txtshopprice.setText("$" + jsonObject.getString("final_total") + ".00");
            txtshopfname.setText(jsonObject.getString("store_owner"));
            txtfirstchoice.setText(jsonObject.getString("store_name"));
            final String delivery_phone = jsonObject.getString("delivery_phone");
            frmcall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasPermission(CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + delivery_phone));
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);
                    } else {
                        requestCallPermissions();
                    }
                }
            });

            LatLng origin = new LatLng(jsonObject.getDouble("driver_latitude"), jsonObject.getDouble("driver_longitude"));
            LatLng dest = new LatLng(jsonObject.getDouble("store_latitude"), jsonObject.getDouble("store_longitude"));

            final String price = jsonObject.getString("final_total");

            moveMarker(origin.latitude, origin.longitude, "You", R.drawable.youimage, true);
            moveMarker(dest.latitude, dest.longitude, "Shop", R.drawable.shopimage, true);

//            greenRawLine(origin, dest);
            String url = getDirectionsUrl(origin, dest);

            DownloadTask1 downloadTask = new DownloadTask1();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
            final String order_id = jsonObject.getString("id");
            shopbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedpreferences.getBoolean(ISPICKUPORDER, false)) {
                        showConfirmationOrderPopup(order_id, "You Already PickUp Product\nFrom Shop?", "", getStatuscode_driver_order_pickup, price);
                    } else {
                        showConfirmationOrderPopup(order_id, "Have you collected product\nfrom shop ?", "pickup", getStatuscode_driver_order_pickup, price);
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        rltvbottom.setVisibility(View.VISIBLE);
        try {
            txtdeliveryshopadd.setText(jsonObject.getString("store_address"));
            txtdeliveryadd.setText(jsonObject.getString("delivery_address"));

            try {
                double distance = Double.parseDouble(jsonObject.getString("delivery_distance"));
                txtkm.setText(new DecimalFormat("##.##").format(distance) + "km");
            } catch (Exception e) {

            }

            txttime.setText(jsonObject.getString("delivery_time"));
            txtprice.setText("$" + jsonObject.getString("final_total") + ".00");
            txtfname.setText(jsonObject.getString("delivery_name"));

            final String delivery_phone = jsonObject.getString("delivery_phone");
            frmdeliverphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (hasPermission(CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + delivery_phone));
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);
                    } else {
                        requestCallPermissions();
                    }

                }
            });

            if (!TextUtils.isEmpty(jsonObject.getString("user_image"))) {
                SimpleTarget targetStore = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        // do something with the bitmap
                        // for demonstration purposes, let's just set it to an ImageView
                        imgprofile.setImageBitmap(bitmap);
                    }
                };
                if (!TextUtils.isEmpty(jsonObject.getString("user_image_url"))) {
                    Glide.with(MainActivity.this).load(jsonObject.getString("user_image_url")).asBitmap().into(targetStore);
                }
            }

//            LatLng origin = latLng;
            LatLng origin = new LatLng(jsonObject.getDouble("store_latitude"), jsonObject.getDouble("store_longitude"));
            LatLng dest = new LatLng(jsonObject.getDouble("delivery_latitude"), jsonObject.getDouble("delivery_longitude"));
            moveMarker(dest.latitude, dest.longitude, "Delivery", R.drawable.deliveryimage, true);
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API

            final String price = jsonObject.getString("final_total");
            downloadTask.execute(url);

            final String order_id = jsonObject.getString("id");

            findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedpreferences.getBoolean(ISPICKUPORDER, false)) {
                        showConfirmationOrderPopup(order_id, "Have you collected payment\nfrom client ?", "delivered", getStatuscode_driver_order_deliver, price);
                    } else {
                        Toast.makeText(MainActivity.this, "First collect product from shop", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pager.setCurrentItem(0);

    }

    private void setCurrentDeliveryOrderDetail(JSONObject jsonObject) {
        rltvbottom.setVisibility(View.VISIBLE);
        try {
            txtdeliveryshopadd.setText(jsonObject.getString("store_address"));
            txtdeliveryadd.setText(jsonObject.getString("delivery_address"));

            try {
                double distance = Double.parseDouble(jsonObject.getString("delivery_distance"));
                txtkm.setText(new DecimalFormat("##.##").format(distance) + "km");
            } catch (Exception e) {

            }

            txttime.setText(jsonObject.getString("delivery_time"));
            txtprice.setText("$" + jsonObject.getString("final_total") + ".00");
            txtfname.setText(jsonObject.getString("delivery_name"));

            final String delivery_phone = jsonObject.getString("delivery_phone");
            frmdeliverphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (hasPermission(CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + delivery_phone));
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);
                    } else {
                        requestCallPermissions();
                    }

                }
            });

            if (!TextUtils.isEmpty(jsonObject.getString("user_image"))) {
                SimpleTarget targetStore = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        // do something with the bitmap
                        // for demonstration purposes, let's just set it to an ImageView
                        imgprofile.setImageBitmap(bitmap);
                    }
                };
                if (!TextUtils.isEmpty(jsonObject.getString("user_image_url"))) {
                    Glide.with(MainActivity.this).load(jsonObject.getString("user_image_url")).asBitmap().into(targetStore);
                }
            }

//            LatLng origin = latLng;
            LatLng origin = new LatLng(jsonObject.getDouble("store_latitude"), jsonObject.getDouble("store_longitude"));
            LatLng dest = new LatLng(jsonObject.getDouble("delivery_latitude"), jsonObject.getDouble("delivery_longitude"));
            moveMarker(dest.latitude, dest.longitude, "Delivery", R.drawable.deliveryimage, true);
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API

            final String price = jsonObject.getString("final_total");
            downloadTask.execute(url);

            final String order_id = jsonObject.getString("id");

            findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedpreferences.getBoolean(ISPICKUPORDER, false)) {
                        showConfirmationOrderPopup(order_id, "Have you collected payment\nfrom client ?", "delivered", getStatuscode_driver_order_deliver, price);
                    } else {
                        Toast.makeText(MainActivity.this, "First collect product from shop", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Permissions*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  showToast("Permission Granted, Now you can access camera.");

                } else {
                    // showToast("Permission Denied, You cannot access camera.");
                    //code for deny
//                    if (!isPermisionDialogShow) {
                    showDialogNotCancelable("Permission", "Please Grant Permissions to show location.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestLocationPermissions();
                        }
                    });
//                    }
                }
                break;

            case REQUEST_PHONE_CALL:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  showToast("Permission Granted, Now you can access camera.");
                } else {
                    // showToast("Permission Denied, You cannot access camera.");
                    //code for deny
                    showDialogNotCancelable("Permission", "Please Grant Permissions to Call phone.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestCallPermissions();
                        }
                    });
                }
                break;
        }
    }

    @SuppressLint("NewApi")
    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_FINE_LOCATION)) {
            requestPermissions(PERMISSIONS_LOCATION, REQUEST_LOCATION);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    private void showDialogNotCancelable(String title, String message, DialogInterface.OnClickListener okListener) {
        isPermisionDialogShow = true;
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    private void driverOrderActionApi(String order_id, String action, int statusCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
        map.put("order_id", order_id);
        map.put("action", action);
        ServiceHandler task = new ServiceHandler(MainActivity.this, Constant.driver_order_action, map, statusCode, true, this.getApplicationContext());
        task.executeAsync();
    }

    private class getAddress extends AsyncTask {
        private String result;

        @Override
        protected Object doInBackground(Object[] params) {
            result = getAddress();

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject != null) {
                        JSONObject jsonObject1 = jsonObject.getJSONArray("results").getJSONObject(0);
                        // Get the value of the attribute whose name is "formatted_string"
                        if (jsonObject1 != null) {
                            String address = jsonObject1.getString("formatted_address");
                            if (!TextUtils.isEmpty(address)) {
                                tv_current_location.setText(address + "");
                                Log.e("responseText", address);
//                                drawPolyLine();
                            } else {
                                new getAddress().execute();
                            }
                        }
                    }
                }
//                LatLng origin = latLng;
//                LatLng dest = new LatLng(22.995727, 72.530887);
//
//                // Getting URL to the Google Directions API
//                String url = getDirectionsUrl(origin, dest);
//
//                DownloadTask downloadTask = new DownloadTask();
//
//                // Start downloading json data from Google Directions API
//                downloadTask.execute(url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawPolyLine() {
        Polyline line = mGoogleMap.addPolyline(new PolylineOptions()
                .add(new LatLng(latLng.latitude, latLng.longitude), new LatLng(22.995727, 72.530887))
                .width(5)
                .color(Color.RED));
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("draw url", url);
        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    private class DownloadTask1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask1 parserTask = new ParserTask1();
            parserTask.execute(result);

        }
    }

    private class DownloadGreenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            ParserGreenTask parserGreenTask = new ParserGreenTask();
            parserGreenTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);
            }
            if (mGoogleMap != null && lineOptions != null) {
// Drawing polyline in the Google Map for the i-th route
                mGoogleMap.addPolyline(lineOptions);
            }
        }
    }

    private class ParserTask1 extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);
                lineOptions.geodesic(true);
            }
            if (mGoogleMap != null && lineOptions != null) {
// Drawing polyline in the Google Map for the i-th route
                mGoogleMap.addPolyline(lineOptions);
            }
        }
    }

    private class ParserGreenTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);
                lineOptions.geodesic(true);
            }

            if (mGoogleMap != null && lineOptions != null) {
// Drawing polyline in the Google Map for the i-th route
                mGoogleMap.addPolyline(lineOptions);
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private static LatLng currentLatLng;

    private void greenRawLine(LatLng latLng, LatLng destLatLng) {

//        mGoogleMap.clear(); //clears all Markers and Polylines

//        PolylineOptions options = new PolylineOptions().width(10).color(Color.GREEN).geodesic(true);
//        for (int i = 0; i < points.size(); i++) {
//            LatLng point = points.get(i);
//            options.add(point);
//        }
////        addMarker(); //add Marker in current position
//        line = mGoogleMap.addPolyline(options); //add Polyline

//        LatLng dest = new LatLng(23.1653, 72.1302);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(latLng, destLatLng);

        DownloadGreenTask downloadTask = new DownloadGreenTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private void moveMarker(double lat, double lng) {

        latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
//        markerOptions.title("You");
            /*int hue = 161;

            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(hue));*/
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.youimage));
        // clear previous marker when location change
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        currLocationMarker.showInfoWindow();
        if (zoomLevel == 0) {
            zoomLevel = 15.0f;
        } else {
            zoomLevel = mGoogleMap.getCameraPosition().zoom;
        }

        //Move the camera to the user's location and zoom in!
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        new getAddress().execute();

        points.add(latLng);
    }

    private void moveMarker(double lat, double lng, String name, int res, boolean animate) {
        latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
//        markerOptions.snippet(name);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(res));
        // clear previous marker when location change

        Marker tempMarker = mGoogleMap.addMarker(markerOptions);

        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        if (name.equalsIgnoreCase("You")) {
            if (driverMarker != null) {
                driverMarker.remove();
            }
            driverMarker = tempMarker;
        } else if (name.equalsIgnoreCase("Shop")) {
            shopMarker = tempMarker;
        } else if (name.equalsIgnoreCase("Delivery")) {
            delevMarker = tempMarker;
        }


        //Move the camera to the user's location and zoom in!
        if (zoomLevel == 0) {
            zoomLevel = 15.0f;
        } else {
            zoomLevel = mGoogleMap.getCameraPosition().zoom;
        }
        if (animate)
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        new getAddress().execute();

        points.add(latLng);
    }

    // method for show current order popup
    String order_id = "";
    String price;

    private void showCurrentOrderPopup(final JSONObject jsonObject) {
        animate = 0;
        dialogAccrptDecline =
                new Dialog(MainActivity.this, R.style.full_screen_dialog);
//                orderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialogAccrptDecline.setCancelable(true);
        dialogAccrptDecline.setContentView(R.layout.dialog_order_accept_decline);

        dialogAccrptDecline.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        final TextView tv_progress = (TextView) dialogAccrptDecline.findViewById(R.id.tv_progress);
        //final ProgressBar progressBar=(ProgressBar)orderDialog.findViewById(R.id.ProgressBar);

        final SeekArc seekround = (SeekArc) dialogAccrptDecline.findViewById(R.id.seekround);

        Button btn_accept = (Button) dialogAccrptDecline.findViewById(R.id.btn_accept);
        Button btndecline = (Button) dialogAccrptDecline.findViewById(R.id.btndecline);

        TextView txtcurrentLocation = (TextView) dialogAccrptDecline.findViewById(R.id.txtcurrentLocation);
        TextView txtDeliveryadd = (TextView) dialogAccrptDecline.findViewById(R.id.txtDeliveryadd);
        TextView txtkm = (TextView) dialogAccrptDecline.findViewById(R.id.txtkm);
        TextView txttime = (TextView) dialogAccrptDecline.findViewById(R.id.txttime);
        TextView txtprice = (TextView) dialogAccrptDecline.findViewById(R.id.txtprice);

        try {
            if (!TextUtils.isEmpty(jsonObject.getString("driver_address"))) {
                txtcurrentLocation.setText(jsonObject.getString("driver_address"));
            }

            if (!TextUtils.isEmpty(jsonObject.getString("delivery_address"))) {
                txtDeliveryadd.setText(jsonObject.getString("delivery_address"));
            }

            if (!TextUtils.isEmpty(jsonObject.getString("delivery_distance"))) {
                double distance = Double.parseDouble(jsonObject.getString("delivery_distance"));
                txtkm.setText(new DecimalFormat("##.##").format(distance) + "km");
            }

            if (!TextUtils.isEmpty(jsonObject.getString("delivery_time"))) {
                txttime.setText(jsonObject.getString("delivery_time"));
            }

            if (!TextUtils.isEmpty(jsonObject.getString("final_total"))) {
                txtprice.setText("$" + jsonObject.getString("final_total") + ".00");
                price = jsonObject.getString("final_total");
            }

            order_id = jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btndecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAccrptDecline.dismiss();
                showConfirmationPopup("Are you sure decline\nthe order?", false, "decline", statuscode_request_action_decline, price);
            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAccrptDecline.dismiss();
                showConfirmationPopup("Are you sure accept\nthe order?", true, "accept", statuscode_request_action_accept, price);
            }
        });

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //this will be done every 1000 milliseconds ( 1 seconds )
                int progress = (int) (millisUntilFinished / 1000);

                seekround.setProgress(progress);

                tv_progress.setText("" + progress);
            }

            @Override
            public void onFinish() {
                seekround.setProgress(0);
                tv_progress.setText("0");
                dialogAccrptDecline.dismiss();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(ISDRIVERORDERREQUEST, false);
                editor.apply();
            }
        }.start();

        if (!isFinishing()) {
            if (dialogAccrptDecline != null) {
                dialogAccrptDecline.show();
            }
        }
    }

    private void showConfirmationPopup(String msg, final boolean flag, final String action, final int statusCode, String price) {

        final Dialog orderDialog =
                new Dialog(MainActivity.this, R.style.full_screen_dialog);

        orderDialog.setCancelable(true);
        orderDialog.setContentView(R.layout.dialog_reminder_2);

        orderDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        TextView txtMsg = (TextView) orderDialog.findViewById(R.id.txtMsg);
        TextView txtammount = (TextView) orderDialog.findViewById(R.id.txtammount);
        TextView txtprice = (TextView) orderDialog.findViewById(R.id.txtprice);
        if (!TextUtils.isEmpty(price)) {
            txtammount.setText("$" + price + ".00");
        } else {
            txtammount.setVisibility(View.GONE);
            txtprice.setVisibility(View.GONE);
        }
        txtMsg.setText(msg);
        RelativeLayout closeButton = (RelativeLayout) orderDialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDialog.dismiss();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(ISDRIVERORDERREQUEST, false);
                editor.apply();
            }
        });
        View btnYes = orderDialog.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDialog.dismiss();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(ISDRIVERORDERREQUEST, flag);
                editor.apply();
                HashMap<String, String> map = new HashMap<>();
                map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
                map.put("order_id", order_id);
                map.put("action", action);
                ServiceHandler task = new ServiceHandler(MainActivity.this, Constant.driver_request_action, map, statusCode, true, getApplicationContext());
                task.executeAsync();
            }
        });

        orderDialog.show();
    }

    private void showConfirmationOrderPopup(final String order_id, String msg, final String action, final int statusCode, String price) {

        final Dialog orderDialog =
                new Dialog(MainActivity.this, R.style.full_screen_dialog);

        orderDialog.setCancelable(true);
        orderDialog.setContentView(R.layout.dialog_reminder_2);

        orderDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        TextView txtMsg = (TextView) orderDialog.findViewById(R.id.txtMsg);
        TextView txtammount = (TextView) orderDialog.findViewById(R.id.txtammount);
        TextView txtprice = (TextView) orderDialog.findViewById(R.id.txtprice);
        if (!TextUtils.isEmpty(price)) {
            txtammount.setText("$" + price + ".00");
        } else {
            txtammount.setVisibility(View.GONE);
            txtprice.setVisibility(View.GONE);
        }

        txtMsg.setText(msg);
        RelativeLayout closeButton = (RelativeLayout) orderDialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDialog.dismiss();

            }
        });
        View btnYes = orderDialog.findViewById(R.id.btnYes);
        TextView txtok = (TextView) orderDialog.findViewById(R.id.txtok);
        if (TextUtils.isEmpty(action)) {
            txtok.setText("OK");
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderDialog.dismiss();
                }
            });
        } else {
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderDialog.dismiss();
                    driverOrderActionApi(order_id, action, statusCode);
                }
            });
        }

        orderDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

//http://www.journaldev.com/13373/android-google-map-drawing-route-two-points