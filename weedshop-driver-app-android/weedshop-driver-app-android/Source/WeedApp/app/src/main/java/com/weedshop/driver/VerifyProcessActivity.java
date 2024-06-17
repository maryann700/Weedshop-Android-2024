package com.weedshop.driver;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.weedshop.driver.utils.Constant.ISVERYFINEPERDETAIL;

/**
 * Created by MTPC_51 on 4/17/2017.
 */

public class VerifyProcessActivity extends AppCompatActivity implements OnTaskCompleted {
    private TextView txtVerifyMsg;
    private Button btn_contact_admin;
    private SharedPreferences sharedpreferences;

    private Timer timer;
    private TimerTask timerTask;
    String reason = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_process);
        sharedpreferences = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);

        txtVerifyMsg = (TextView) findViewById(R.id.txtVerifyMsg);
        btn_contact_admin = (Button) findViewById(R.id.btn_contact_admin);

        driverInfoApi(true);


        btn_contact_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = sharedpreferences.getString(Constant.adminRejectReason, "");
                if (reason.equalsIgnoreCase("Identification,Car")) {
                    btn_contact_admin.setText("ADD IDENTIFICATION");
                    Intent intent = new Intent(VerifyProcessActivity.this, AddIdentificationActivity.class);
                    startActivity(intent);
                    finish();
                } else if (reason.equalsIgnoreCase("Identification")) {
                    btn_contact_admin.setText("ADD IDENTIFICATION");
                    Intent intent = new Intent(VerifyProcessActivity.this, AddIdentificationActivity.class);
                    intent.putExtra("redirect", true);
                    startActivity(intent);
                    finish();
                } else if (reason.equalsIgnoreCase("Car")) {
                    btn_contact_admin.setText("UPDATE CAR DETAIL");
                    Intent intent = new Intent(VerifyProcessActivity.this, CarDetailsActivity.class);
                    // intent.putExtra("redirect", true);
                    startActivity(intent);
                    finish();
                } else {
                    btn_contact_admin.setText("CONTACT TO ADMIN");
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "testineed@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Driver Profile approval.");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            }
        });

        findViewById(R.id.btnlogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.logoutAlert(VerifyProcessActivity.this, "Are you sure you want to Logout?");
            }
        });
    }

    private void driverInfoApi(boolean isProgress) {
        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
        ServiceHandler task = new ServiceHandler(VerifyProcessActivity.this, Constant.driver_info, map, isProgress, this.getApplicationContext());
        task.executeAsync();
    }

    @Override
    public void onTaskCompleted(String result, int requestCode) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String msg = jsonObject.getString("msg");
            boolean response = jsonObject.getBoolean("response");
            if (response) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Constant.ISLOGIN, true);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                editor.putString(Constant.PREF_NAME, jsonObject1.getString("name"));
                editor.putString(Constant.PREF_carno, jsonObject1.getString("car_number"));
                editor.putString(Constant.adminRejectReason, jsonObject1.getString("adminRejectReason"));
                editor.putString(Constant.verifymsg, jsonObject1.getString("verifymsg"));
                txtVerifyMsg.setText(jsonObject1.getString("verifymsg"));
                final String reason = jsonObject1.getString("adminRejectReason");
                String status = jsonObject1.getString("adminApproved");

                if (!TextUtils.isEmpty(status) && status.equalsIgnoreCase("Approved")) {
                    editor.putBoolean(ISVERYFINEPERDETAIL, true);
                    editor.apply();
                    timer.cancel();
                    Intent m_intent = new Intent(VerifyProcessActivity.this, MainActivity.class);
                    m_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(m_intent);
                    finish();
                    return;
                } else if (!TextUtils.isEmpty(status) && status.equalsIgnoreCase("Pending")) {
                    editor.putString(Constant.adminRejectReason, "Pending");
                    btn_contact_admin.setText("CONTACT TO ADMIN");
                } else {
                    if (reason.equalsIgnoreCase("Identification,Car")) {
                        btn_contact_admin.setText("ADD IDENTIFICATION");
                    } else if (reason.equalsIgnoreCase("Identification")) {
                        btn_contact_admin.setText("ADD IDENTIFICATION");
                    } else if (reason.equalsIgnoreCase("Car")) {
                        btn_contact_admin.setText("UPDATE CAR DETAIL");
                    } else {
                        btn_contact_admin.setText("CONTACT TO ADMIN");
                    }
                }
                editor.apply();
            } else {
                Toast.makeText(VerifyProcessActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public void onResume() {
        super.onResume();

        try {
            reason = sharedpreferences.getString(Constant.adminRejectReason, "");
            if (reason.equalsIgnoreCase("Identification,Recommendation")) {
                btn_contact_admin.setText("ADD IDENTIFICATION");
            } else if (reason.equalsIgnoreCase("Identification")) {
                btn_contact_admin.setText("ADD IDENTIFICATION");
            } else if (reason.equalsIgnoreCase("Recommendation")) {
                btn_contact_admin.setText("UPLOAD RECOMMENDATION");
            } else {
                btn_contact_admin.setText("CONTACT TO ADMIN");
            }

            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    //Download file here and refresh
                    driverInfoApi(false);
                }
            };
            timer.schedule(timerTask, 5000, 5000);
        } catch (IllegalStateException e) {
            android.util.Log.i("Damn", "resume error");
        }
    }
}