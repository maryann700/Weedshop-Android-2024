package com.weedshop.driver.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.weedshop.driver.SignInActivity;
import com.weedshop.driver.services.MyLocationService;
import com.weedshop.driver.services.OrderRequestService;
import com.weedshop.driver.webservices.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by MTPC-83 on 6/15/2016.
 */
public class CommonUtils {
    private static AlertDialog dialog = null;

    public static SpannableString getSpannableString(String string) {
        SpannableString content = new SpannableString(string);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    //show short toast with string id
    public static void toastShort(Context c, int res) {
        Toast.makeText(c, res, Toast.LENGTH_SHORT).show();
    }

    //check if network available
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean checkNetworkConnection(final Context context, final int res) {
        if (isNetworkConnected(context)) {
            return true;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    toastShort(context, res);
                }
            });
            return false;
        }
    }

    // check email validation
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    // check mobile validation
    public final static boolean isValidMobile(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static int getResourceId(Context context, String resName, String resType) {
        return context.getResources().getIdentifier(resName, resType, context.getPackageName());
    }

    public static void logoutAlert(final Context context, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        logoutAPI(context);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private static void logoutAPI(final Context context) {
        final SharedPreferences prefLogin = context.getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        String userId = prefLogin.getString(Constant.PREF_userid, "");
        if (TextUtils.isEmpty(userId) || userId.equalsIgnoreCase("null")) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("driver_id", userId);
        params.put("token", regId);

        new AsyncTask<Void, Void, String>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(context);
                dialog.setMessage("Please wait");
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return ServiceHandler.getResponse(Constant.logout, params);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String msg = jsonObject.getString("msg");
                        boolean response = jsonObject.getBoolean("response");
                        if (response) {
                            NotificationUtils.clearNotifications(context);

                            SharedPreferences.Editor editor = prefLogin.edit();
                            editor.putBoolean(Constant.ISLOGIN, false);
                            editor.clear();
                            editor.apply();
                            Intent intent = new Intent(context, MyLocationService.class);
                            context.stopService(intent);
                            Intent intent1 = new Intent(context, OrderRequestService.class);
                            context.stopService(intent1);
                            Intent m_intent = new Intent(context, SignInActivity.class);
                            context.startActivity(m_intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }


    public static boolean isCalifornia(Context context, double latitude, double longitude) {

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            try {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
               /* Log.e("address", address);
                Log.e("city", city);
                Log.e("state", state);
                Log.e("country", country);
                Log.e("postalCode", postalCode);
                Log.e("knownName", knownName);*/
               /*for testing purpose, have added Gujarat as allowed state*/
                return state.equalsIgnoreCase("California")||state.equalsIgnoreCase("Gujarat");
                //return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("GeoCoder", "Failed to get Address.");
        }
        return true;
    }

    public static AlertDialog showDialogNotCancelable(Context context, String title, String message, DialogInterface.OnClickListener okListener) {
        try {
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
            dialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    .setCancelable(false)
                    .create();
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
