package com.weedshop.driver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.weedshop.driver.utils.Config;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.webservices.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class WeedApp extends MultiDexApplication {
    public static String TAG = WeedApp.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static Context mContext;
    //for app open close
    private ArrayList<String> runningActivities;
    private ActivityLifecycleCallbacks activityLifecycleCallbacks;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        /*Remove comment before live*/
        Fabric.with(this, new Crashlytics());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                updateUserDetail();
            }
        };

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        runningActivities = new ArrayList<>();
        activityLifecycleCallbacks = new LifecycleCallbacks();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public static synchronized Context getAppContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        try {
            setAppTerminate();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onTerminate();
    }

    public int getRunningActivitySize() {
        return runningActivities == null ? 0 : runningActivities.size();
    }

    public void addRunningActivity(String name) {
        if (getRunningActivitySize() == 0) {
            Log.e(TAG, "App start from background .. *_*");
            //if user login is there, check if user logged in here
            updateUserDetail();
        }
        runningActivities.add(name);
        setAppVisible();
    }

    public void removeRunningActivity(final String name) {
        //delay remove for resume/pause issue
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runningActivities.remove(name);
                setAppVisible();
            }
        }, 500);
    }

    public void setAppVisible() {
        boolean visible = getRunningActivitySize() > 0;
        Context context = getAppContext();
        SharedPreferences prefLogin = context.getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        prefLogin.edit().putBoolean("AppState", visible).apply();
        if (!visible) {
            Log.e(TAG, "App went to background .. @_@");
        }
    }

    public void setAppTerminate() {
        Context context = getAppContext();
        SharedPreferences prefLogin = context.getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        prefLogin.edit().putBoolean("AppState", false).apply();
    }

    /*class for life cycle call backs*/
    class LifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.w(TAG, activity.getLocalClassName() + ".onResume");
            addRunningActivity(activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.w(TAG, activity.getLocalClassName() + ".onPause");
            removeRunningActivity(activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }


    public static void updateUserDetail() {
        Context context = getAppContext();
        SharedPreferences prefLogin = context.getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", "");
        String userId = prefLogin.getString(Constant.PREF_userid, "");
        if (TextUtils.isEmpty(regId)) {
            Log.e(TAG, "onReceive reg id Empty");
            return;
        }
        if (TextUtils.isEmpty(userId) || userId.equalsIgnoreCase("null")) {
            Log.e(TAG, "User not LoggedIn");
            return;
        }

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("driver_id", userId);
        params.put("uniqueid", android_id);
        params.put("token", regId);
        params.put("device_type", Constant.ANDROID);

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return ServiceHandler.getResponse(Constant.driver_device, params);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String msg = jsonObject.getString("msg");
                        boolean response = jsonObject.getBoolean("response");
                        if (response) {
                            Log.e(TAG, msg);
                        } else {
                            Log.e(TAG, msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "User Detail response not available");
                }
            }
        }.execute();
    }
}