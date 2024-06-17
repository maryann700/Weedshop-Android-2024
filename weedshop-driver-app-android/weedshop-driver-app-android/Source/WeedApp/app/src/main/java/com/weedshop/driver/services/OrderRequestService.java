package com.weedshop.driver.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.weedshop.driver.utils.Constant.ISDRIVERORDERREQUEST;
import static com.weedshop.driver.utils.Constant.PREF_order_id;

/**
 * Created by MTPC-83 on 5/12/2017.
 */

public class OrderRequestService extends Service implements OnTaskCompleted {

    private static final String TAG = "OrderRequestService";
    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedpreferences = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        int delay = 10000; // delay for 10 sec.
        int period = 30000; // repeat every 30 sec.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!sharedpreferences.getBoolean(ISDRIVERORDERREQUEST, false)) {
                    if (TextUtils.isEmpty(sharedpreferences.getString(PREF_order_id, ""))) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
                        ServiceHandler task = new ServiceHandler(OrderRequestService.this, Constant.driver_get_order_request, map, false, getApplicationContext());
                        task.executeAsync(); // display the data
                    }
                }
            }
        }, delay, period);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskCompleted(String result, int requestCode) {

        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("msg");
                boolean response = jsonObject.getBoolean("response");
                if (response) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Intent intent = new Intent("DriverOrderRequest");
                            // You can also include some extra data.
                            intent.putExtra("jsonObject", jsonObject1.toString());
                            LocalBroadcastManager.getInstance(OrderRequestService.this).sendBroadcast(intent);
                        }
                    }
                } else {
//                    Toast.makeText(OrderRequestService.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        stopForeground(true);
    }
}
