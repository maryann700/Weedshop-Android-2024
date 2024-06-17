package com.weedshop.driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by MTPC_51 on 4/17/2017.
 */

public class CarDetailsActivity extends AppCompatActivity implements OnTaskCompleted {

    SegmentedProgressBar segmentedProgressBar;
    private EditText et_car_no, et_brand;
    private Button btn_verification;
    private static final int SELECT_FILE = 1;
    private static final int REQUEST_CAMERA = 0;
    String userChoosenTask;
    SharedPreferences sharedpreferences;
    private Uri fileUri; // file url to store image/video
    private static final String IMAGE_DIRECTORY_NAME = "WeedAppDriver";
    private static String[] PERMISSIONS_CAMERA = {CAMERA,
            WRITE_EXTERNAL_STORAGE};
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 200;
    private String imagepath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        sharedpreferences = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);

        btn_verification = (Button) findViewById(R.id.btn_verification);
        et_car_no = (EditText) findViewById(R.id.et_car_no);
        et_brand = (EditText) findViewById(R.id.et_brand);

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
        segmentedProgressBar.setCompletedSegments(3);

        btn_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasPermission(CAMERA) && hasPermission(WRITE_EXTERNAL_STORAGE)) {
                    String number = et_car_no.getText().toString();
                    String brand = et_brand.getText().toString();
                    if (checkData(number, brand)) {
                        selectImage();
                    }
                } else {
                    requestCameraPermissions();
                }
            }
        });

        findViewById(R.id.btnlogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.logoutAlert(CarDetailsActivity.this, "Are you sure you want to Logout?");
            }
        });

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

    private boolean checkData(String number, String brand) {
        if (!CommonUtils.checkNetworkConnection(this, R.string.internet_error)) {
            return false;
        }

        if (number.length() == 0) {
            CommonUtils.toastShort(this, R.string.car_details_number_error);
            return false;
        }

        if (brand.length() == 0) {
            CommonUtils.toastShort(this, R.string.car_details_brand_error);
            return false;
        }

        return true;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        imagepath = file.getAbsolutePath();
                        fileUri = FileProvider.getUriForFile(CarDetailsActivity.this, getPackageName() + ".provider", file);
                        // intent.setData(fileUri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    } else {
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // start the image capture Intent
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            GALLERY_IMAGE_REQUEST_CODE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            showToast("Sorry! Your device doesn't support camera");
            // will close the app if the device does't have camera
        } else {
            builder.show();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                    //  showToast("Permission Granted, Now you can access camera.");
                } else {
                    // showToast("Permission Denied, You cannot access camera.");
                    //code for deny
                    showDialogNotCancelable("Permission", "Please Grant Permissions to upload profile photo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestCameraPermissions();
                        }
                    });
                }
                break;
        }
    }

    private void showDialogNotCancelable(String title, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    @SuppressLint("NewApi")
    private void requestCameraPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CarDetailsActivity.this, CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(CarDetailsActivity.this, WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
        } else {
            ActivityCompat.requestPermissions(CarDetailsActivity.this, PERMISSIONS_CAMERA, REQUEST_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                try {
                    previewCapturedImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                showToast("User cancelled image capture");
            } else {
                // failed to capture image
                showToast("Sorry! Failed to capture image");
            }
        } else if (requestCode == GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri selectedImageUri = data.getData();

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        File sourceFile = new File(getRealPathFromURI(data.getData()));
                        File destFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        copyFile(sourceFile, destFile);
                        imagepath = destFile.getAbsolutePath();
                    } else {
                        String[] projection = {MediaStore.MediaColumns.DATA};
                        CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                                null);
                        Cursor cursor = cursorLoader.loadInBackground();
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                        cursor.moveToFirst();
                        String selectedImagePath = cursor.getString(column_index);
                        fileUri = Uri.parse(selectedImagePath);
                    }
                    previewCapturedImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                showToast("User cancelled image selection");
            } else {
                // failed to capture image
                showToast("Sorry! Failed to select image");
            }
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri,
                proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void previewCapturedImage() {
        try {
            // bitmap factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            Bitmap bitmap = null;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                bitmap = BitmapFactory.decodeFile(imagepath,
                        options);
            } else {
                bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                        options);
            }
            if (bitmap != null) {
                if (!(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)) {
                    imagepath = fileUri.getPath().toString();
                }
                uploadFile(imagepath);
            } else {
                Log.e("Camera Result:", "Bitmap null");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void uploadFile(String file) {
        String number = et_car_no.getText().toString();
        String brand = et_brand.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
        map.put("type", "car_document");
        map.put("file", file);
        map.put("car_number", number);
        map.put("car_brand", brand);
        ServiceHandler task = new ServiceHandler(CarDetailsActivity.this, Constant.identificationPhoto_Upload, map, true,this.getApplicationContext());
        task.executeAsync();

    }

    @Override
    public void onTaskCompleted(String result, int requestCode) {
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("msg");
                boolean response = jsonObject.getBoolean("response");
                if (response) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(Constant.ISDOCUMENTUPLOAD, true);
                    editor.apply();
                    Intent m_intent = new Intent(CarDetailsActivity.this, VerifyProcessActivity.class);
                    startActivity(m_intent);
                } else {
                    Toast.makeText(CarDetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}