package com.weedshop.driver.webservices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.weedshop.driver.R;
import com.weedshop.driver.utils.ConnectionDetector;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by MTPC-83 on 6/14/2016.
 */
public class ServiceHandler extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private OnTaskCompleted listener;
    private HashMap<String, String> params;
    private ProgressDialog dialog;
    private String response, url;
    private int requestCode = 0;
    private boolean showProgressDialog = true;

    public ServiceHandler(Context mContext, String url, HashMap<String, String> params, boolean showDialog, Context progressDialogcon) {
        this.mContext = mContext;
        this.params = params;
        this.url = url;
        this.listener = (OnTaskCompleted) mContext;
        showProgressDialog = showDialog;
    }

    public ServiceHandler(Activity activity, String url, HashMap<String, String> params, OnTaskCompleted listener, boolean showDialog, Context progressDialogcon) {
        this.mContext = activity;
        this.params = params;
        this.url = url;
        this.listener = listener;
        showProgressDialog = showDialog;
    }

    public ServiceHandler(Context mContext, String url, HashMap<String, String> params, int requestCode, boolean showDialog, Context progressDialogcon) {
        this.mContext = mContext;
        this.params = params;
        this.url = url;
        this.listener = (OnTaskCompleted) mContext;
        this.requestCode = requestCode;
        showProgressDialog = showDialog;
    }

    public ServiceHandler(Activity activity, String url, HashMap<String, String> params, OnTaskCompleted listener, int requestCode, boolean showDialog, Context progressDialogcon) {
        this.mContext = activity;
        this.params = params;
        this.url = url;
        this.listener = listener;
        this.requestCode = requestCode;
        showProgressDialog = showDialog;
    }


    public ServiceHandler(Fragment mContext, String url, HashMap<String, String> params, int requestCode, boolean showDialog, Context progressDialogcon) {
        this.mContext = mContext.getActivity();
        this.params = params;
        this.url = url;
        this.listener = (OnTaskCompleted) mContext;
        this.requestCode = requestCode;
        showProgressDialog = showDialog;
    }


    public ServiceHandler(Fragment mContext, String url, HashMap<String, String> params, boolean showDialog, Context progressDialogcon) {
        this.mContext = mContext.getActivity();
        this.params = params;
        this.url = url;
        this.listener = (OnTaskCompleted) mContext;
        showProgressDialog = showDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (showProgressDialog) {
                if (mContext != null) {
                    dialog = new ProgressDialog(mContext);
                    dialog.setMessage("Please wait");
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
    }

    public static String getResponse(String url, HashMap<String, String> params) {
        String response = "";
        try {
            final OkHttpClient client = getOkHttpClient();

            MultipartBody.Builder data = new MultipartBody.Builder();
            data.setType(MultipartBody.FORM);

            Log.e("Params", "" + url + "..>" + params);
            for (String key : params.keySet()) {
                data.addFormDataPart(key, params.get(key));
            }

            RequestBody requestBody = data.build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("APPKEY", Constant.apikey)
                    .post(requestBody)
                    .build();

            Response res = client.newCall(request).execute();
            response = res.body().string();
            Log.e("responce ser_handle = ", response + "");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        if (ConnectionDetector.isConnection(mContext)) {
            try {
                final OkHttpClient client = getOkHttpClient();

                MultipartBody.Builder data = new MultipartBody.Builder();
                data.setType(MultipartBody.FORM);

                Log.e("Params", "" + url + "..>" + params);

//                if (params.get("messagetype").equalsIgnoreCase("Image")) {
//                    for (String key : params.keySet()) {
//                        if (key.equals("message")) {
//                            File sourceFile = new File(params.get(key));
//                            MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
//                            data.addFormDataPart(key, "msg.png", RequestBody.create(MEDIA_TYPE_PNG, sourceFile));
//                        } else {
//                            data.addFormDataPart(key, params.get(key));
//                        }
//                    }
//                } else {
//                    for (String key : params.keySet()) {
//                        data.addFormDataPart(key, params.get(key));
//                    }
//                }

                for (String key : params.keySet()) {
                    if (key.equals("file") || key.equals("image")) {
                        File sourceFile = new File(params.get(key));
                        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                        data.addFormDataPart(key, "image.png", RequestBody.create(MEDIA_TYPE_PNG, sourceFile));
                    } else {
                        data.addFormDataPart(key, params.get(key));
                    }
//                    data.addFormDataPart(key, params.get(key));
                }

                RequestBody requestBody = data.build();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("APPKEY", Constant.apikey)
                        .post(requestBody)
                        .build();

                Response res = client.newCall(request).execute();
                response = res.body().string();
                Log.e("responce ser_handle == ", response + "");
                if (!res.isSuccessful()) throw new IOException("Unexpected code " + res);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            showToast(mContext.getString(R.string.internet_error));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        try {
            if (showProgressDialog) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response != null && response.length() > 0) {
            Log.e("result ", url + "..." + response);
        }

        listener.onTaskCompleted(response, requestCode);
    }

    @Override
    protected void onCancelled() {
        try {
            if (showProgressDialog) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCancelled();
    }

    public void executeAsync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            this.execute();
        }
    }

    private void showToast(final String toast) {
        try {
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgress() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
        }

    }
}
