package com.weedshop.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.weedshop.driver.utils.Constant.ISVERIFYEMAIL;
import static com.weedshop.driver.utils.Constant.PREF_userid;
import static com.weedshop.driver.utils.Constant.status_driver_resend_verification;
import static com.weedshop.driver.utils.Constant.status_driver_verification;

/**
 * Created by MTPC-86 on 3/6/2017.
 */

public class VerificationActivity extends BaseActivity implements OnTaskCompleted {
    Button btnSubmit;
    SegmentedProgressBar segmentedProgressBar;
    private EditText et_name;
    SharedPreferences sharedpreferences;
    private TextView txtResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        sharedpreferences = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        et_name = (EditText) findViewById(R.id.et_name);
        txtResend = (TextView) findViewById(R.id.txtResend);

        segmentedProgressBar = (SegmentedProgressBar) findViewById(R.id.segmented_progressbar);
        // number of segments in your bar
        segmentedProgressBar.setSegmentCount(4);
        //empty segment color
        segmentedProgressBar.setContainerColor(Color.parseColor("#121317"));
        //fill segment color
        segmentedProgressBar.setFillColor(Color.parseColor("#2CCD9B"));
        //pause segment
        segmentedProgressBar.pause();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //play next segment specifying its duration
                segmentedProgressBar.playSegment(1000);

            }
        }, 600);
        //set filled segments directly
        segmentedProgressBar.setCompletedSegments(1);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et_name.getText().toString().trim())) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("code", et_name.getText().toString().trim());
                    map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
                    ServiceHandler task = new ServiceHandler(VerificationActivity.this, Constant.register_verification, map, status_driver_verification, true,getApplicationContext());
                    task.executeAsync();
                } else {
                    Toast.makeText(VerificationActivity.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btnlogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.logoutAlert(VerificationActivity.this, "Are you sure you want to Logout? Your Progress and account will be deleted. You can re-register with same email id.");
            }
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtResend.getText().toString().equals("Resend Verification Code.")) {
                    //verification code send without 60sec
                    HashMap<String, String> map = new HashMap<>();
                    map.put("driver_id", sharedpreferences.getString(PREF_userid, ""));
                    ServiceHandler task = new ServiceHandler(VerificationActivity.this, Constant.driver_resend_verification, map, status_driver_resend_verification, true,getApplicationContext());
                    task.executeAsync();
                }
            }
        });

       /* new CountDownTimer(60000, 1000) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                txtResend.setText("" + String.format("Resend Verification code after %d Second",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
            }

            public void onFinish() {
                txtResend.setText("Resend Verification Code.");
            }
        }.start();*/
    }

    @Override
    public void onTaskCompleted(String result, int requestCode) {

        if (!TextUtils.isEmpty(result)) {
            if (requestCode == status_driver_verification) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    if (response) {
                        sharedpreferences.edit().putBoolean(ISVERIFYEMAIL, true).apply();
                        Toast.makeText(VerificationActivity.this, msg, Toast.LENGTH_LONG).show();
                        Intent m_intent = new Intent(VerificationActivity.this, AddIdentificationActivity.class);
                        startActivity(m_intent);
                    } else {
                        Toast.makeText(VerificationActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == status_driver_resend_verification) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    Toast.makeText(VerificationActivity.this, msg, Toast.LENGTH_LONG).show();
                    if (response) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}