package com.weedshop.driver;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.triggertrap.seekarc.SeekArc;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.weedshop.driver.utils.Constant.PREF_userImage;
import static com.weedshop.driver.utils.Constant.getStatuscode_driver_profile_edit;
import static com.weedshop.driver.utils.Constant.getStatuscode_driver_profile_list;

public class ProfileActivity1 extends SideMenuActivity implements DatePickerDialog.OnDateSetListener, OnTaskCompleted {

    public static String TAG = ProfileActivity1.class.getSimpleName();
    private SeekArc seekround;
    ViewGroup llSeek;
    int animate = 0;
    RelativeLayout frame_profile;
    TextView txtCancel;
    ImageView imgProfile, imgFrame, imgEdit, imgTemp;
    EditText edtName, edtDob, edtphone, edtemail, edtadd, edtstate, edtcardno, edtcarbrand;
    View v1, v2, v3, v4, v5, v6, v7;
    boolean isEditMode = false;
    ViewGroup transitionsContainer;
    private String imagepath = "";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 200;
    private Uri fileUri; // file url to store image/video
    private static final String IMAGE_DIRECTORY_NAME = "WeedAppDriver";
    private static String[] PERMISSIONS_CAMERA = {CAMERA,
            WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CAMERA = 2;
    String phoneNumber;
    private final String PREFIX = "+1";

    private String name, birthdate, mobile, email, address, carnumber, carbrand, image, caridentification;
    private String serverDate;
//    ad
//    car number
//    car brand
//    state iss

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_profile1);
        mMenuDrawer.setMenuView(R.layout.activity_base_menu);

        initToolbar(false);
        getClassName(TAG);

        init();

        if (savedInstanceState != null)
            fileUri = savedInstanceState.getParcelable("file_uri");


        /*final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                seekround.setProgress(seekround.getProgress() + 4);
                if (seekround.getProgress() >= 100) {
                    animate = 1;
                } else {
                    handler.postDelayed(this, 30);
                }
            }
        };
        handler.postDelayed(r, 1500);*/

        driverProfile("list");
    }

    private void driverProfile(String action) {
        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
        if (action.equals("list")) {
            map.put("action", "list");
        } else {
            map.put("action", "edit");
            map.put("name", "edit");
            map.put("mobile", "edit");
            map.put("birthdate", "edit");
            map.put("address", "edit");
            map.put("image", "edit");
        }
        ServiceHandler task = new ServiceHandler(ProfileActivity1.this, Constant.driver_profile, map, getStatuscode_driver_profile_list, true, this.getApplicationContext());
        task.executeAsync();
    }

    private void init() {
        TextView txt_screen_title = (TextView) findViewById(R.id.tv_shop_product_title);
        txt_screen_title.setText(R.string.profile);
        transitionsContainer = (ViewGroup) findViewById(R.id.transitions_container);
        llSeek = (ViewGroup) findViewById(R.id.llSeek);
        frame_profile = (RelativeLayout) findViewById(R.id.frame_profile);
        txtCancel = (TextView) findViewById(R.id.txtCancel);
        seekround = (SeekArc) findViewById(R.id.seekround);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        imgFrame = (ImageView) findViewById(R.id.imgFrame);
        imgEdit = (ImageView) findViewById(R.id.imgEdit);
        edtName = (EditText) findViewById(R.id.edtName);
        edtDob = (EditText) findViewById(R.id.edtDob);
        edtphone = (EditText) findViewById(R.id.edtphone);
        edtemail = (EditText) findViewById(R.id.edtemail);
        edtadd = (EditText) findViewById(R.id.edtadd);
        edtstate = (EditText) findViewById(R.id.edtstate);
        edtcardno = (EditText) findViewById(R.id.edtcardno);
        edtcarbrand = (EditText) findViewById(R.id.edtcarbrand);
        imgTemp = (ImageView) findViewById(R.id.imgTemp);

        v1 = findViewById(R.id.v1);
        v2 = findViewById(R.id.v2);
        v3 = findViewById(R.id.v3);
        v4 = findViewById(R.id.v4);
        v5 = findViewById(R.id.v5);
        v6 = findViewById(R.id.v6);
        v7 = findViewById(R.id.v7);

        setDisableViews();

        edtphone.setText(PREFIX);
        Selection.setSelection(edtphone.getText(), edtphone.getText().length());

        edtphone.addTextChangedListener(new TextWatcher() {

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
                    edtphone.setText(PREFIX);
                    Selection.setSelection(edtphone.getText(), edtphone.getText().length());
                }
            }
        });


        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                seekround.setProgress(seekround.getProgress() + 4);
                if (seekround.getProgress() >= 100) {
                    animate = 1;
                    imgTemp.setVisibility(View.VISIBLE);
                    seekround.setVisibility(View.GONE);
                } else {
                    handler.postDelayed(this, 30);
                }
            }
        };
        handler.postDelayed(r, 1500);

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (!isEditMode) {
                    //seekround.setVisibility(View.INVISIBLE);
                }*/

                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new Fade());
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.setDuration(1000);
                TransitionManager.beginDelayedTransition(transitionsContainer, transitionSet);

                /*if (isEditMode) {
                    seekround.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Transition transition = new Fade();
                            transition.setDuration(500);
                            TransitionManager.beginDelayedTransition(llSeek, transition);
                            //seekround.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }*/
                if (isEditMode) {
                    String name = edtName.getText().toString().trim();
                    String phone = edtphone.getText().toString().trim();
                    if (phone.length() > 3) {
                        phone = phone.substring(2);
                    }
                    String date = edtDob.getText().toString().trim();
                    String address = edtadd.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(ProfileActivity1.this, "Please enter name.", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(phone)) {
                        Toast.makeText(ProfileActivity1.this, "Please enter phone number.", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(date)) {
                        Toast.makeText(ProfileActivity1.this, "Please select birth date.", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(address)) {
                        Toast.makeText(ProfileActivity1.this, "Please enter address.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("driver_id", sharedpreferences.getString(Constant.PREF_userid, ""));
                        map.put("action", "edit");
                        map.put("name", name);
                        map.put("mobile", phone);
                        map.put("birthdate", serverDate);
                        map.put("address", address);
                        if (!TextUtils.isEmpty(imagepath)) {
                            map.put("image", imagepath);
                        }
                        ServiceHandler task = new ServiceHandler(ProfileActivity1.this, Constant.driver_profile, map, getStatuscode_driver_profile_edit, true, getApplicationContext());
                        task.executeAsync();
                    }

                    isEditMode = false;
                    setDisableViews();
                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frame_profile.getLayoutParams();
                    params.height = size;
                    params.width = size;
                    frame_profile.setLayoutParams(params);

                    size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) imgEdit.getLayoutParams();
                    params1.height = size;
                    params1.width = size;
                    imgEdit.setLayoutParams(params1);
                    imgEdit.setImageResource(R.drawable.edit_profile);

                } else {
                    isEditMode = true;
                    setEnableViews();

                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frame_profile.getLayoutParams();
                    params.height = size;
                    params.width = size;
                    frame_profile.setLayoutParams(params);

                    size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, getResources().getDisplayMetrics());
                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) imgEdit.getLayoutParams();
                    params1.height = size;
                    params1.width = size;
                    imgEdit.setLayoutParams(params1);
                    imgEdit.setImageResource(R.drawable.save_profile);
                }
            }
        });

        frame_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEditMode) return;
                CommonUtils.hideKeyboard(ProfileActivity1.this, frame_profile);
                if (hasPermission(CAMERA) && hasPermission(WRITE_EXTERNAL_STORAGE)) {
                    selectImage();
                } else {
                    requestCameraPermissions();
                }
            }
        });

        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ProfileActivity1.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMaxDate(now);
                dpd.show(getFragmentManager(), "Select Birth Date...");

            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (!isEditMode) {
                    //seekround.setVisibility(View.INVISIBLE);
                }*/

                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new Fade());
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.setDuration(1000);
                TransitionManager.beginDelayedTransition(transitionsContainer, transitionSet);

                /*if (isEditMode) {
                    seekround.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Transition transition = new Fade();
                            transition.setDuration(500);
                            TransitionManager.beginDelayedTransition(llSeek, transition);
                            seekround.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }*/
                if (isEditMode) {
                    isEditMode = false;
                    setDisableViews();
                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frame_profile.getLayoutParams();
                    params.height = size;
                    params.width = size;
                    frame_profile.setLayoutParams(params);

                    size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) imgEdit.getLayoutParams();
                    params1.height = size;
                    params1.width = size;
                    imgEdit.setLayoutParams(params1);
                    imgEdit.setImageResource(R.drawable.edit_profile);
                }
                setProfileData(null);
            }
        });
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
                        fileUri = FileProvider.getUriForFile(ProfileActivity1.this, getPackageName() + ".provider", file);
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

    private void setDisableViews() {
        edtName.setEnabled(false);
        edtDob.setEnabled(false);
        edtphone.setEnabled(false);
        edtemail.setEnabled(false);
        edtadd.setEnabled(false);
        edtstate.setEnabled(false);
        edtcardno.setEnabled(false);
        edtcarbrand.setEnabled(false);
        imgFrame.setVisibility(View.INVISIBLE);
        // seekround.setVisibility(View.VISIBLE);
        v1.setVisibility(View.GONE);
        v2.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
        v4.setVisibility(View.GONE);
        v5.setVisibility(View.GONE);
        v6.setVisibility(View.GONE);
        v7.setVisibility(View.GONE);
        txtCancel.setVisibility(View.INVISIBLE);
    }

    private void setEnableViews() {
        edtName.setEnabled(true);
        edtDob.setEnabled(true);
        edtphone.setEnabled(true);
        edtemail.setEnabled(true);
        edtadd.setEnabled(true);
        edtstate.setEnabled(true);
        edtcardno.setEnabled(true);
        edtcarbrand.setEnabled(true);
        //seekround.setVisibility(View.INVISIBLE);
        imgFrame.setVisibility(View.VISIBLE);
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.VISIBLE);
        v3.setVisibility(View.VISIBLE);
        v4.setVisibility(View.VISIBLE);
        v5.setVisibility(View.VISIBLE);
        v6.setVisibility(View.VISIBLE);
        v7.setVisibility(View.VISIBLE);
        txtCancel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskCompleted(String result, int requestCode) {
        if (!TextUtils.isEmpty(result)) {
            if (requestCode == getStatuscode_driver_profile_list) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    if (response) {
                        JSONArray array = jsonObject.getJSONArray("data");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject1 = array.getJSONObject(i);
                                setProfileData(jsonObject1);
                            }
                        }
                    } else {
                        Toast.makeText(ProfileActivity1.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == getStatuscode_driver_profile_edit) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("msg");
                    boolean response = jsonObject.getBoolean("response");
                    if (response) {
                        JSONArray array = jsonObject.getJSONArray("data");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject1 = array.getJSONObject(i);
                                setProfileData(jsonObject1);
                                Toast.makeText(ProfileActivity1.this, "Profile Updated Successfully.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(ProfileActivity1.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setProfileData(JSONObject jsonObject1) {
        try {
            if (jsonObject1 != null) {
                name = jsonObject1.getString("name");
                birthdate = jsonObject1.getString("birthdate");
                if (birthdate.equalsIgnoreCase("0000-00-00") || TextUtils.isEmpty(birthdate)) {
                    birthdate = "";
                }
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("birthdate", birthdate);
                editor.commit();
                mobile = jsonObject1.getString("mobile");
                email = jsonObject1.getString("email");
                address = jsonObject1.getString("address");
                carnumber = jsonObject1.getString("car_number");
                carbrand = jsonObject1.getString("car_brand");
                image = jsonObject1.getString("image_url");


                if (!TextUtils.isEmpty(jsonObject1.getString("identification_id"))) {
                    caridentification = jsonObject1.getString("identification_id");
                }
            }
            edtName.setText(name);
            phoneNumber = mobile;
            edtphone.setText(PREFIX + phoneNumber);
            edtemail.setText(email);
            edtadd.setText(address);
            edtcardno.setText(carnumber);
            edtcarbrand.setText(carbrand);
            edtstate.setText(caridentification);

            serverDate = sharedpreferences.getString("birthdate", "");
            if (TextUtils.isEmpty(serverDate)) {
                edtDob.setText("");
            } else {
                try {
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = format1.parse(serverDate);
                    SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
                    String dd = format.format(date);
                    edtDob.setText(dd);
                } catch (ParseException e) {
                    e.printStackTrace();
                    edtDob.setText("");
                }
            }
            SimpleTarget targetStore = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    // do something with the bitmap
                    // for demonstration purposes, let's just set it to an ImageView
                    imgProfile.setImageBitmap(bitmap);
                }
            };
            if (!TextUtils.isEmpty(image)) {
                sharedpreferences.edit().putString(PREF_userImage, image).apply();
                Glide.with(ProfileActivity1.this).load(image).asBitmap().into(targetStore);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
                // imgFrame.setImageBitmap(bitmap);
                imgProfile.setImageResource(0);
                imgProfile.setBackgroundResource(0);
                imgProfile.setImageBitmap(bitmap);
                // Glide.with(this).load(new File(imagepath)).placeholder(R.drawable.profile_img).into(imgProfile);
            } else {
                Log.e("Camera Result:", "Bitmap null");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        // get the file url
        if (savedInstanceState != null)
            fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
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

    @SuppressLint("NewApi")
    private void requestCameraPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity1.this, CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity1.this, WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
        } else {
            ActivityCompat.requestPermissions(ProfileActivity1.this, PERMISSIONS_CAMERA, REQUEST_CAMERA);
        }
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


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
        String date = format.format(calendar.getTime());
        serverDate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        edtDob.setText(date);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity1.this, MainActivity.class));
        finish();
    }
}
