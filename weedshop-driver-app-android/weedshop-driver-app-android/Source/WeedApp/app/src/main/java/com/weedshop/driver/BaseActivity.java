package com.weedshop.driver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//import android.support.v7.app.AppCompatActivity;

//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mtpc on 4/2/16.
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //QuickChippy application = (QuickChippy) getApplication();

    }


    /*@Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }

    public void animatedStartActivityBack(Context context, Class intentClass) {
        // super.onBackPressed();
        final Intent intent = new Intent(context, intentClass);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Mint.initAndStartSession(BaseActivity.this, "fcfa9036");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
