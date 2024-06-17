package com.weedshop.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import org.json.JSONObject;

import java.util.HashMap;

import static com.weedshop.driver.utils.Constant.ISDOCUMENTUPLOAD;
import static com.weedshop.driver.utils.Constant.ISPHOTOUPLOAD;
import static com.weedshop.driver.utils.Constant.ISVERIFYEMAIL;
import static com.weedshop.driver.utils.Constant.ISVERYFINEPERDETAIL;

/**
 * Created by MTPC-86 on 3/6/2017.
 */

public class SignInActivity extends BaseActivity implements OnTaskCompleted {
    TextView tvSignUp;
    Button btnSignin;
    private EditText edtEmail;
    private EditText editPassword;
    SharedPreferences sharedpreferences;
    private TextView txtTermsOfUse, txtDisclaimer;
    private int click = 0;
    ImageView iv_terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        sharedpreferences = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);

        tvSignUp = (TextView) findViewById(R.id.tv_signup);
        btnSignin = (Button) findViewById(R.id.btn_signin);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        iv_terms = (ImageView) findViewById(R.id.iv_terms);
        txtTermsOfUse = (TextView) findViewById(R.id.txtTermsOfUse);
        txtDisclaimer = (TextView) findViewById(R.id.txtDisclaimer);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String password = editPassword.getText().toString();
                if (checkData(email, password,click)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("email", email);
                    map.put("password", password);
                    ServiceHandler task = new ServiceHandler(SignInActivity.this, Constant.login_api, map, true, getApplicationContext());
                    task.executeAsync();
                }
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent m_intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(m_intent);
            }
        });

        txtDisclaimer.setText(CommonUtils.getSpannableString(txtDisclaimer.getText().toString()));

        txtTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String termsOfUse = "file:///android_asset/terms_condition.html";
                Intent intent = new Intent(SignInActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra("url", termsOfUse);
                intent.putExtra("title", "Terms & Condition");
                startActivity(intent);
            }
        });

        txtDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disclaimer = "file:///android_asset/disclaimer.html";
                Intent intent = new Intent(SignInActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra("url", disclaimer);
                intent.putExtra("title", "Legal Disclaimer");
                startActivity(intent);
            }
        });

        iv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click == 0) {
                    iv_terms.setImageResource(R.drawable.img_terms);
                    click = 1;
                } else if (click == 1) {
                    //ivTerms.setBackgroundColor(Color.parseColor("#282B30"));
                    iv_terms.setImageResource(R.drawable.round_corner_terms);
                    click = 0;
                }
            }
        });

        findViewById(R.id.txtforgotpass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgotActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finish();
        finishAffinity();
    }

    private boolean checkData(String email, String password, int click) {
        if (!CommonUtils.checkNetworkConnection(this, R.string.internet_error)) {
            return false;
        }

        if (email.length() == 0) {
            CommonUtils.toastShort(this, R.string.email_or_mobile_error);
            return false;
        }

        if (email.length() != 0 && !CommonUtils.isValidEmail(email)) {
            CommonUtils.toastShort(this, R.string.email_valid_error);
            return false;
        }

        if (password.length() == 0) {
            CommonUtils.toastShort(this, R.string.password_error);
            return false;
        }

        if (password.length() != 0 && password.length() < 8 || password.length() > 20) {
            CommonUtils.toastShort(this, R.string.password_length_error);
            return false;
        }

        if (click == 0) {
            CommonUtils.toastShort(this, R.string.term_condition_error);
            return false;
        }
        return true;
    }

    @Override
    public void onTaskCompleted(String result, int requestCode) {
        if (!TextUtils.isEmpty(result)) {

//            {
//                "response": "true",
//                    "msg": "Success.",
//                    "data": {
//                "id": 15,
//                        "name": "driver",
//                        "email": "driver3@mailinator.com",
//                        "password": "16d7a4fca7442dda3ad93c9a726597e4",
//                        "address": "",
//                        "zipcode": "568956",
//                        "mobile": "8956895689",
//                        "verification_code": "",
//                        "identification_photo": "wd_1493890665191.png",
//                        "car_number": "565456",
//                        "car_brand": "xyz",
//                        "car_document": "wd_149389071875.png",
//                        "image": "",
//                        "identification_id": "",
//                        "adminRejectReason": "",
//                        "adminApproved": "Pending",
//                        "created_date": "2017-05-04 10:36:47",
//                        "token": "",
//                        "status": "Active",
//                        "birthdate": "0000-00-00"
//            }
//            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("msg");
                boolean response = jsonObject.getBoolean("response");
                if (response) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(Constant.ISLOGIN, true);
                    editor.putBoolean(ISVERIFYEMAIL, true);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    editor.putString(Constant.PREF_userid, jsonObject1.getString("id"));
                    editor.putString(Constant.PREF_NAME, jsonObject1.getString("name"));
                    editor.putString(Constant.PREF_carno, jsonObject1.getString("car_number"));
                    editor.putString(Constant.adminRejectReason, jsonObject1.getString("adminRejectReason"));
                    editor.apply();
//                    if (TextUtils.isEmpty(jsonObject1.getString("verification_code"))) {
//                        editor.putBoolean(ISVERYFINEPERDETAIL, false);
//                        Intent m_intent = new Intent(SignInActivity.this, VerificationActivity.class);
//                        startActivity(m_intent);
//                        finish();
//                        return;
//                    } else {
//                        editor.putBoolean(ISVERYFINEPERDETAIL, true);
//                        editor.apply();
//                    }
                    if (TextUtils.isEmpty(jsonObject1.getString("identification_photo"))) {
                        editor.putBoolean(ISPHOTOUPLOAD, false);
                        Intent m_intent = new Intent(SignInActivity.this, AddIdentificationActivity.class);
                        startActivity(m_intent);
                        finish();
                        return;
                    } else {
                        editor.putBoolean(ISPHOTOUPLOAD, true);
                        editor.apply();
                    }

                    if (TextUtils.isEmpty(jsonObject1.getString("car_document"))) {
                        editor.putBoolean(ISDOCUMENTUPLOAD, false);
                        Intent m_intent = new Intent(SignInActivity.this, CarDetailsActivity.class);
                        startActivity(m_intent);
                        finish();
                        return;
                    } else {
                        editor.putBoolean(ISDOCUMENTUPLOAD, true);
                        editor.apply();
                    }
                    if (!jsonObject1.getString("adminApproved").equalsIgnoreCase("Approved")) {
                        editor.putBoolean(ISVERYFINEPERDETAIL, false);
                        if (jsonObject1.getString("adminApproved").equalsIgnoreCase("Pending")) {
                            editor.putString(Constant.adminRejectReason, "Pending");
                        }
                        editor.apply();
                        Intent m_intent = new Intent(SignInActivity.this, VerifyProcessActivity.class);
                        startActivity(m_intent);
                        finish();
                        return;
                    } else {
                        editor.putBoolean(ISVERYFINEPERDETAIL, true);
                        editor.apply();
                    }

                    editor.putBoolean(ISVERIFYEMAIL, true);
                    editor.apply();

                    WeedApp.updateUserDetail();
                    Intent m_intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(m_intent);
                    finish();
                } else {
                    Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}