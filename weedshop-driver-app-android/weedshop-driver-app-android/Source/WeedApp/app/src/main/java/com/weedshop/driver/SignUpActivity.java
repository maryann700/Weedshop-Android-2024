package com.weedshop.driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import org.json.JSONObject;

import java.util.HashMap;

import static com.weedshop.driver.R.id.et_password;
import static com.weedshop.driver.R.id.tv_signin;

/**
 * Created by MTPC-86 on 3/6/2017.
 */

public class SignUpActivity extends BaseActivity implements OnTaskCompleted {
    TextView tvSignin, tvSignup;
    EditText etName, etEmailAddress, etZipCode, etMobileNumber, etPassowrd;
    Button btnSignUp;
    SegmentedProgressBar segmentedProgressBar;
    CircularProgressButton cpButton;
    ImageView ivTerms, iv_privacy;
    int click = 0, click1 = 0;
    SharedPreferences sharedpreferences;
    private final String PREFIX = "+1";
    private int clickShow = 0;
    private TextView txtTermsOfUse, txtPrivacyPolicy, txtDisclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sharedpreferences = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        tvSignin = (TextView) findViewById(tv_signin);
        etName = (EditText) findViewById(R.id.et_name);
        etEmailAddress = (EditText) findViewById(R.id.et_emailaddress);
        etZipCode = (EditText) findViewById(R.id.et_zipcode);
        etMobileNumber = (EditText) findViewById(R.id.et_mobile);
        etPassowrd = (EditText) findViewById(et_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        ivTerms = (ImageView) findViewById(R.id.iv_terms);
        iv_privacy = (ImageView) findViewById(R.id.iv_privacy);

        txtTermsOfUse = (TextView) findViewById(R.id.txtTermsOfUse);
        txtPrivacyPolicy = (TextView) findViewById(R.id.txtPrivacyPolicy);
        txtDisclaimer = (TextView) findViewById(R.id.txtDisclaimer);

        txtDisclaimer.setText(CommonUtils.getSpannableString(txtDisclaimer.getText().toString()));

        txtTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String termsOfUse = "file:///android_asset/terms_condition.html";
                Intent intent = new Intent(SignUpActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra("url", termsOfUse);
                intent.putExtra("title", "Terms & Condition");
                startActivity(intent);
            }
        });

        txtPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disclaimer = "http://high5delivery.com/privacy-policy";
                Intent intent = new Intent(SignUpActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra("url", disclaimer);
                intent.putExtra("title", "Privacy Policy");
                startActivity(intent);
            }
        });

        txtDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disclaimer = "file:///android_asset/disclaimer.html";
                Intent intent = new Intent(SignUpActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra("url", disclaimer);
                intent.putExtra("title", "Legal Disclaimer");
                startActivity(intent);
            }
        });

        etMobileNumber.setText(PREFIX);
        Selection.setSelection(etMobileNumber.getText(), etMobileNumber.getText().length());

        etMobileNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains(PREFIX)) {
                    etMobileNumber.setText(PREFIX);
                    Selection.setSelection(etMobileNumber.getText(), etMobileNumber.getText().length());
                }
            }
        });

        etPassowrd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etPassowrd.getRight() - etPassowrd.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() - 20)) {
                        // your action here
                        etPassowrd.setSelection(etPassowrd.getSelectionStart(), etPassowrd.getSelectionEnd());
                        if (clickShow == 0) {
                            etPassowrd.setTransformationMethod(new PasswordTransformationMethod());
                            clickShow = 1;
                        } else if (clickShow == 1) {
                            etPassowrd.setTransformationMethod(null);
                            clickShow = 0;
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        ivTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click == 0) {
                    ivTerms.setBackgroundResource(R.drawable.img_terms);
                    click = 1;
                } else if (click == 1) {
                    //ivTerms.setBackgroundColor(Color.parseColor("#282B30"));
                    ivTerms.setBackgroundResource(R.drawable.round_corner_terms);
                    click = 0;
                }
            }
        });

        iv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click1 == 0) {
                    iv_privacy.setImageResource(R.drawable.img_terms);
                    click1 = 1;
                } else if (click1 == 1) {
                    //ivTerms.setBackgroundColor(Color.parseColor("#282B30"));
                    iv_privacy.setImageResource(R.drawable.round_corner_terms);
                    click1 = 0;
                }
            }
        });


        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        segmentedProgressBar.setCompletedSegments(0);
        //FancyButton button1 = (FancyButton) findViewById(R.id.btn1);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etName.getText().toString().trim();
                String email = etEmailAddress.getText().toString().trim();
                String mobile = etMobileNumber.getText().toString().trim();
                if (mobile.length() > 3) {
                    mobile = mobile.substring(2);
                }
                String password = etPassowrd.getText().toString().trim();
                String zipcode = etZipCode.getText().toString().trim();
                if (checkData(username, email, mobile, password, zipcode, click, click1)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", username);
                    map.put("email", email);
                    map.put("zipcode", zipcode);
                    map.put("mobile", mobile);
                    map.put("password", password);
                    ServiceHandler task = new ServiceHandler(SignUpActivity.this, Constant.register, map, true, getApplicationContext());
                    task.executeAsync();
                }
            }
        });
        /*View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof FancyButton) {
                    if (((FancyButton) view).isExpanded())
                        ((FancyButton) view).collapse();
                    else
                        ((FancyButton) view).expand();
                }

            }
        };
        button1.setOnClickListener(listener);*/
    }

    boolean checkData(String username, String email, String mobile, String password, String zipcode, int click, int click1) {
        if (!CommonUtils.checkNetworkConnection(this, R.string.internet_error)) {
            return false;
        }

        if (username.length() == 0) {
            CommonUtils.toastShort(this, R.string.name_error);
            return false;
        }

        if (email.length() == 0 || mobile.length() == 0) {
            CommonUtils.toastShort(this, R.string.email_or_mobile_error);
            return false;
        }

        if (email.length() != 0 && !CommonUtils.isValidEmail(email)) {
            CommonUtils.toastShort(this, R.string.email_valid_error);
            return false;
        }

        if (zipcode.length() == 0) {
            CommonUtils.toastShort(this, R.string.zipcode_error);
            return false;
        }

        if (zipcode.length() < 5) {
            CommonUtils.toastShort(this, R.string.zipcode_error_length);
            return false;
        }

        if (mobile.length() != 0 && mobile.length() != 10 || !TextUtils.isDigitsOnly(mobile)) {
            CommonUtils.toastShort(this, R.string.mobile_valid_error);
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

        if (click1 == 0) {
            CommonUtils.toastShort(this, R.string.privacy_policy_error);
            return false;
        }
        return true;
    }

    @Override
    public void onTaskCompleted(String result, int requestCode) {

        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("msg");
                boolean response = jsonObject.getBoolean("response");
                if (response) {
                    JSONObject jObject = jsonObject.getJSONObject("data");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Constant.PREF_userid, jObject.getString("id"));
                    editor.putBoolean(Constant.ISLOGIN, true);
                    editor.apply();

                    WeedApp.updateUserDetail();
                    Intent m_intent = new Intent(SignUpActivity.this, VerificationActivity.class);
                    startActivity(m_intent);
                } else {
                    Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}