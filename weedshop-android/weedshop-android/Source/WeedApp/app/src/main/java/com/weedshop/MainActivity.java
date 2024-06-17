package com.weedshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.weedshop.Adapter.ShopAdapter;
import com.weedshop.model.Shop;
import com.weedshop.utils.CommonUtils;
import com.weedshop.utils.Constant;
import com.weedshop.utils.EndlessScrollListener;
import com.weedshop.utils.NotificationUtils;
import com.weedshop.utils.ObservableScrollViewCallbacks;
import com.weedshop.utils.ObservableWebView;
import com.weedshop.utils.OnTaskCompleted;
import com.weedshop.utils.ScrollState;
import com.weedshop.utils.Scrollable;
import com.weedshop.webservices.CommonTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends SideMenuActivity implements OnTaskCompleted {
    private static final int REQUEST_LOCATION = 2;
    private static String[] PERMISSIONS_LOCATION = {ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION};
    private static final int SHOP_LIST_REQUEST_CODE = 1001;
    private static final int COMMON_REQUEST_CODE = 1002;
    private LinearLayout llSearchView;
    private ImageView ivSearchbtn;
    private Spinner spinnerRegion;
    private GridView gridView;
    private ShopAdapter shopAdapter;
    private int totalPages = 0;
    private Boolean isSearch = false;

    private TextView txtRegion, txtRangeMin, txtRangeMax;
    private String region = "";
    private SeekBar seekbarRange;
    private Button btn_search;

    private ArrayList<Shop> shopList;
    private ArrayList<String> regionList;
    private GPSTracker gpsTracker;
    private RelativeLayout rlmarket;
    private  View lnbtn;
    private ImageView imgprevious;
    private ImageView imgnext;
    boolean isMarket = false;
    private LinearLayout lnregion;
    private RelativeLayout rlShops;
    private ObservableWebView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpMenu(this, R.layout.activity_main);

        //shop view set here
//        initShopView();
        initMarketView();
        /*Update cart*/
        getCartList();


        lnregion = (LinearLayout) findViewById(R.id.lnregion);
        lnregion.setVisibility(View.GONE);
        rlShops = (RelativeLayout) findViewById(R.id.rlShops);
        rlShops.setVisibility(View.GONE);


        llSearchView = (LinearLayout) findViewById(R.id.llSearchView);
        btn_search = (Button) findViewById(R.id.btn_search);
        ivSearchbtn = (ImageView) findViewById(R.id.ivSearchbtn);
        spinnerRegion = (Spinner) findViewById(R.id.spinnerRegion);

        txtRegion = (TextView) findViewById(R.id.txtRegion);
        txtRangeMin = (TextView) findViewById(R.id.txtRangeMin);
        txtRangeMax = (TextView) findViewById(R.id.txtRangeMax);
        seekbarRange = (SeekBar) findViewById(R.id.seekbarRange);
       /* txt_screen_title.setText(R.string.title_shops);*/
        gridView = (GridView) findViewById(R.id.grid);

        gpsTracker = new GPSTracker(MainActivity.this);


        loadNextDataFromApi(1, false);

        HashMap<String, String> map = new HashMap<>();
        map.put("type", "region");
        CommonTask task = new CommonTask(MainActivity.this, Constant.common, map, COMMON_REQUEST_CODE, false);
        task.executeAsync();

        gridView.setOnScrollListener(endlessScrollListener);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, ShopProductsActivity.class);
                intent.putExtra("data", shopAdapter.getItem(position));
                startActivity(intent);
            }
        });

        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (regionList != null) {
                    endlessScrollListener.resetState();
                    txtRegion.setText(regionList.get(position));
                    region = regionList.get(position);
                    if (position == 0) {
                        region = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llSearchView.getVisibility() == View.VISIBLE) {
                    slideToTop(llSearchView);
                    llSearchView.setVisibility(View.GONE);
                    // txtRegion.setVisibility(View.INVISIBLE);
                }
                loadNextDataFromApi(1, true);
            }
        });
        ivSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llSearchView.getVisibility() == View.VISIBLE) {
                    slideToTop(llSearchView);
                    llSearchView.setVisibility(View.GONE);
                    // txtRegion.setVisibility(View.INVISIBLE);
                } else {
                    slideToBottom(llSearchView);
                    llSearchView.setVisibility(View.VISIBLE);
                    //txtRegion.setVisibility(View.VISIBLE);
                }
            }
        });

        seekbarRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress > 0) {
                    txtRangeMax.setVisibility(View.VISIBLE);
                } else {
                    txtRangeMax.setVisibility(View.GONE);
                }
                txtRangeMax.setText(progress + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (hasPermission(ACCESS_FINE_LOCATION) && hasPermission(ACCESS_COARSE_LOCATION)) {
            if (gpsTracker.canGetLocation()) {
                boolean isCalifornia = CommonUtils.isCalifornia(this, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                if (!isCalifornia) {
                    CommonUtils.showDialogNotCancelable(this, getString(R.string.app_name), "Currently we are not Serving in your City.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
            } else {
                showSettingsAlert();
                return;
            }
        } else {
            requestLocationPermissions();
            return;
        }
        txt_screen_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt_screen_title.setBackgroundDrawable(getResources().getDrawable(R.drawable.line_textview));
                txt_screen_title2.setBackgroundDrawable(getResources().getDrawable(R.drawable.line_textview_black));
                //marketplace view set here
                isMarket = false;

                initShopView();
            }
        });
        txt_screen_title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //marketplace view set here
                txt_screen_title.setBackgroundDrawable(getResources().getDrawable(R.drawable.line_textview_black));
                txt_screen_title2.setBackgroundDrawable(getResources().getDrawable(R.drawable.line_textview));

                isMarket = true;
                initMarketView();
            }
        });
    }

    public void initMarketView() {

        //todo hide shop view
        lnregion = (LinearLayout) findViewById(R.id.lnregion);
        lnregion.setVisibility(View.GONE);
        rlShops = (RelativeLayout) findViewById(R.id.rlShops);
        rlShops.setVisibility(View.GONE);

        //todo visible market view
        rlmarket = (RelativeLayout) findViewById(R.id.rlmarket);
        rlmarket.setVisibility(View.VISIBLE);
        //lnbtn = (LinearLayout)findViewById(R.id.lnbtn);
        lnbtn=findViewById(R.id.lnbtn);
      /*  lnbtn.postDelayed(new Runnable() {
            @Override
            public void run() {
                lnbtn.setVisibility(View.GONE);
            }
        }, 1000);*/
        imgprevious = (ImageView) findViewById(R.id.imgprevious);
        imgnext = (ImageView) findViewById(R.id.imgnext);

        imgprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scroll.canGoBack()) {
                    scroll.goBack();
                } else {
                //    finish();
                }
            }
        });
        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scroll.canGoForward())
                    scroll.goForward();

            }
        });

        scroll = (ObservableWebView) findViewById(R.id.scroll);
        scroll.loadUrl("https://high5appstore.myshopify.com"/*"http://aacg.org/"*/);
        ViewCompat.setNestedScrollingEnabled(scroll, false);


        scroll.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "Try again! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        scroll.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                if (lnbtn == null) {
                    return;
                }
                if (scrollState == ScrollState.UP) {
                    if (lnbtn.getVisibility() == View.VISIBLE) {
                        lnbtn.setVisibility(View.GONE);

                    }
                } else if (scrollState == ScrollState.DOWN) {
                    if (lnbtn.getVisibility()==View.GONE) {
                        lnbtn.setVisibility(View.VISIBLE);
                      //  moveTabLayout(0);
                    }
                }
            }
        });

    }

    protected int getScreenHeight() {
        Activity activity = MainActivity.this;
        if (activity == null) {
            return 0;
        }
        return activity.findViewById(android.R.id.content).getHeight();
    }


    public void initShopView() {
        //todo hide market place view
        rlmarket = (RelativeLayout) findViewById(R.id.rlmarket);
        rlmarket.setVisibility(View.GONE);


        //todo visible shops view
        lnregion = (LinearLayout) findViewById(R.id.lnregion);
        lnregion.setVisibility(View.VISIBLE);
        rlShops = (RelativeLayout) findViewById(R.id.rlShops);
        rlShops.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public boolean onLoadMore(int page, int totalItemsCount) {
            // Triggered only when new data needs to be appended to the list
            // Add whatever code is needed to append new items to your AdapterView
            loadNextDataFromApi(page, false);
            // or loadNextDataFromApi(totalItemsCount);
            return true;
        }
    };

    private void loadNextDataFromApi(int page, boolean isSearch) {
        this.isSearch = isSearch;
        if (totalPages > 0) {
            if (page > totalPages && page != totalPages) {
                return;
            }
        }

        if (gpsTracker.canGetLocation()) {
            Constant.LATITUDE = "" + gpsTracker.getLatitude();
            Constant.LONGITUDE = "" + gpsTracker.getLongitude();

            HashMap<String, String> map = new HashMap<>();
            map.put("page", page + "");
            if (Constant.LATITUDE != null && Constant.LONGITUDE != null) {
                map.put("latitude", Constant.LATITUDE);
                map.put("longitude", Constant.LONGITUDE);
            } else {
                map.put("latitude", "0.0");
                map.put("longitude", "0.0");
            }
            if (isSearch) {
          /*  String distance = txtRangeMax.getText().toString().trim();
            distance = distance.replaceAll("\\D+", "");
            map.put("distance", distance);
            map.put("region", region);*/
            }
            map.put("region", region);
            CommonTask task = new CommonTask(MainActivity.this, Constant.shop_List, map, SHOP_LIST_REQUEST_CODE, true);
            task.executeAsync();
        } else {
            showSettingsAlert();
        }
    }

    private void slideToTop(View view) {
        //view.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.slide_up);
        view.startAnimation(animation);
    }

    private void slideToBottom(View view) {
        //view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.slide_bottom);
        view.startAnimation(animation);
    }

    @Override
    public void onTaskCompleted(String result, int requestCode) {
        if (TextUtils.isEmpty(result))
            return;
        if (SHOP_LIST_REQUEST_CODE == requestCode) {
            shopList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("msg");
                boolean response = jsonObject.getBoolean("response");
                if (response) {
                    totalPages = jsonObject.getInt("totalPages");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Shop shop = new Shop();
                        shop.id = object.getString("id");
                        shop.name = object.getString("name");
                        shop.owner = object.getString("owner");
                        shop.email = object.getString("email");
                        shop.password = object.getString("password");
                        shop.image = object.getString("image");
                        shop.logo = object.getString("logo");
                        shop.description = object.getString("description");
                        shop.phone = object.getString("phone");
                        shop.address = object.getString("address");
                        shop.region = object.getString("region");
                        shop.zipcode = object.getString("zipcode");
                        shop.latitude = object.getString("latitude");
                        shop.longitude = object.getString("longitude");
                        shop.resetCode = object.getString("reset_code");
                        shop.imageUrl = object.getString("image_url");
                        shop.logoUrl = object.getString("logo_url");
                        shop.distance = object.getString("distance");

                        if (object.getString("status").equalsIgnoreCase("Active"))
                            shopList.add(shop);
                    }
                    if (isSearch) {
                        shopAdapter = null;
                        gridView.setAdapter(shopAdapter);
                    }
                    if (shopAdapter == null) {
                        shopAdapter = new ShopAdapter(MainActivity.this, shopList);
                        gridView.setAdapter(shopAdapter);
                    } else {
                        shopAdapter.updateList(shopList);
                    }
                } else {
                    endlessScrollListener.resetState();
                    totalPages = 0;
                    shopAdapter = null;
                    gridView.setAdapter(shopAdapter);
                    showToast(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (COMMON_REQUEST_CODE == requestCode) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("msg");
                boolean response = jsonObject.getBoolean("response");
                if (response) {
                    regionList = new ArrayList<>();
                    regionList.add("All Region");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        regionList.add(object.getString("name"));
                    }
                    ArrayAdapter regionAdapter = new ArrayAdapter(this, R.layout.spinner_text_item, regionList);
                    spinnerRegion.setAdapter(regionAdapter);
                } else {
                    showToast(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Requests the Contacts permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    @SuppressLint("NewApi")
    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_FINE_LOCATION)) {
            requestPermissions(PERMISSIONS_LOCATION, REQUEST_LOCATION);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
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
                    if (gpsTracker == null)
                        gpsTracker = new GPSTracker(this);
                    if (gpsTracker.canGetLocation()) {

                        boolean isCalifornia = CommonUtils.isCalifornia(this, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                        if (!isCalifornia) {
                            CommonUtils.showDialogNotCancelable(this, getString(R.string.app_name), "Currently we are not Serving in your City.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                        }
                    } else {
                        gpsTracker.showSettingsAlert();
                        return;
                    }
                } else {
                    // showToast("Permission Denied, You cannot access application.");
                    //code for deny
                    showDialogNotCancelable("Permission", "Please grant permissions to access application.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestLocationPermissions();
                        }
                    });
                }
                break;
        }
    }

    private void showDialogNotCancelable(String title, String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * launch Settings Options
     */
    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setCancelable(false);
        // Setting Dialog Title
        alertDialog.setTitle("Location settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("Location is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

      /*  // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });*/

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (isMarket) {
                        if (scroll.canGoBack()) {
                            scroll.goBack();

                        }
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void slideUp(View v, View bv) {
        //view.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.slide_up_view);
        bv.startAnimation(animation);
        v.startAnimation(animation);
        v.setVisibility(View.VISIBLE);
        lnbtn.setVisibility(View.VISIBLE);


    }

    private void slideDown(View v, View bv) {
        //view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.slide_bottom_view);
        bv.startAnimation(animation);

        v.startAnimation(animation);

        v.setVisibility(View.GONE);
        lnbtn.setVisibility(View.GONE);

    }


}
