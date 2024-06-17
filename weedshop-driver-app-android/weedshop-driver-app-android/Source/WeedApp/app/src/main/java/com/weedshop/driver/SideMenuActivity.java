package com.weedshop.driver;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.weedshop.driver.Adapter.RecyclerAdapter;
import com.weedshop.driver.model.NavDrawerItem;
import com.weedshop.driver.utils.CommonUtils;
import com.weedshop.driver.utils.Constant;

import net.simonvt.menudrawer.MenuDrawer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.weedshop.driver.utils.Constant.PREF_NAME;
import static com.weedshop.driver.utils.Constant.PREF_carno;
import static com.weedshop.driver.utils.Constant.PREF_userImage;

/**
 * Created by MTPC-83 on 6/24/2016.
 */
public class SideMenuActivity extends BaseActivity {
    public static String TAG = SideMenuActivity.class.getSimpleName();
    private static final String STATE_MENUDRAWER = "net.simonvt.menudrawer.samples.WindowSample.menuDrawer";
    public MenuDrawer mMenuDrawer;
    private ArrayList<NavDrawerItem> navDrawerItems;
    Toolbar toolbar;
    private String className;
    private ViewGroup mDrawerList;
    CircleImageView profileImage;
    private static final int SELECT_FILE = 1;
    private static final int REQUEST_CAMERA = 0;
    String userChoosenTask;
    ImageView ivCloseMenu, ivOpenCart;
    TextView tvLogout;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    public static LinearLayout llCharge;
    public static Button btnCheckout;
    SharedPreferences sharedpreferences;
    RelativeLayout rlCart;
    TextView txtname;
    TextView txtcarno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getClassName(String className) {
        this.className = className;
    }

    public Toolbar initToolbar(boolean openManu) {
        sharedpreferences = getSharedPreferences(Constant.LOGIN_USER_PREF, Context.MODE_PRIVATE);
        mDrawerList = (ViewGroup) findViewById(R.id.list_slidermenu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        rlCart = (RelativeLayout) findViewById(R.id.rlCart);
        ivOpenCart = (ImageView) findViewById(R.id.iv_open_cart);
        ivCloseMenu = (ImageView) findViewById(R.id.iv_close_menu);
        tvLogout = (TextView) findViewById(R.id.tv_logout);
        txtname = (TextView) findViewById(R.id.txtname);
        txtname.setText(sharedpreferences.getString(PREF_NAME, ""));
        txtcarno = (TextView) findViewById(R.id.txtcarno);
        txtcarno.setText("Car number : " + sharedpreferences.getString(PREF_carno, ""));
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.logoutAlert(SideMenuActivity.this, "Are you sure you want to Logout?");
            }
        });
        ivCloseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int drawerState = mMenuDrawer.getDrawerState();
                if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
                    mMenuDrawer.closeMenu();
                    return;
                } else {
                    mMenuDrawer.openMenu();
                }
                CommonUtils.hideKeyboard(SideMenuActivity.this, toolbar);
            }
        });
        profileImage = (CircleImageView) findViewById(R.id.profile_image);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ProfileActivity1.TAG.equals(className)) {
                    Intent intent = new Intent(SideMenuActivity.this, ProfileActivity1.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                mMenuDrawer.closeMenu();
            }
        });

        if (openManu)
            mMenuDrawer.openMenu();
        mMenuDrawer.setDropShadow(new ColorDrawable(getResources().getColor(R.color.menu_shadow_color)));
        mMenuDrawer.setDropShadowSize(3);
        float width = getResources().getDisplayMetrics().widthPixels / 1.25f;
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int drawerState = mMenuDrawer.getDrawerState();
                if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
                    mMenuDrawer.closeMenu();
                    return;
                } else {
                    mMenuDrawer.openMenu();
                }
                CommonUtils.hideKeyboard(SideMenuActivity.this, toolbar);
            }
        });

//        ivOpenCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final int drawerState = mMenuDrawerRight.getDrawerState();
//                if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
//                    mMenuDrawerRight.closeMenu();
//                    return;
//                } else {
//                    mMenuDrawerRight.openMenu();
//                }
//                CommonUtils.hideKeyboard(SideMenuActivity.this, ivCloseMenu);
//            }
//        });

//        rlCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mMenuDrawerRight.isMenuVisible()) {
//                    mMenuDrawerRight.closeMenu();
//                } else {
//                    mMenuDrawerRight.openMenu();
//                    CommonUtils.hideKeyboard(SideMenuActivity.this, ivCloseMenu);
//                }
//            }
//        });


        mMenuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_OPEN) {
                    if (!TextUtils.isEmpty(sharedpreferences.getString(PREF_userImage, ""))) {
                        Glide.with(SideMenuActivity.this).load(sharedpreferences.getString(PREF_userImage, "")).placeholder(R.drawable.profile_img).listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                //  Log.e("Glide", "onException" + e.getMessage());
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                Log.e("Glide", "onResourceReady Success.");
                                profileImage.setImageResource(0);
                                profileImage.setBackgroundResource(0);
                                profileImage.setImageDrawable(resource);
                                return false;
                            }
                        }).into(profileImage);
                    }

                    txtname.setText(sharedpreferences.getString(PREF_NAME, ""));
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {

            }
        });

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem("Profile", R.drawable.name));
        navDrawerItems.add(new NavDrawerItem("Current order status", R.drawable.img_currentorder));
        navDrawerItems.add(new NavDrawerItem("Order history", R.drawable.orderhistory));
        navDrawerItems.add(new NavDrawerItem("Terms of Use", R.drawable.term));
        navDrawerItems.add(new NavDrawerItem("Privacy Policy", R.drawable.privacy));

        for (int i = 0; i < navDrawerItems.size(); i++) {
            final View layout2 = LayoutInflater.from(this).inflate(R.layout.drawer_list_item, mDrawerList, false);
            TextView txtTitle = (TextView) layout2.findViewById(R.id.titleqc);
            ImageView imgicon = (ImageView) layout2.findViewById(R.id.imgicon);
            txtTitle.setText(navDrawerItems.get(i).getTitle());
            imgicon.setImageResource(navDrawerItems.get(i).getId());
            layout2.setTag(i);
            mDrawerList.addView(layout2);
            final int position = i;
            layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    switch (position) {
                        case 0:
                            if (!ProfileActivity1.TAG.equals(className)) {
                                intent = new Intent(SideMenuActivity.this, ProfileActivity1.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            mMenuDrawer.closeMenu();
                            break;

                        case 1:
                            if (!MainActivity.TAG.equals(className)) {
//                                startActivity(new Intent(SideMenuActivity.this, MainActivity.class));
//                                finish();
                                intent = new Intent(SideMenuActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            mMenuDrawer.closeMenu();
                            break;

                        case 2:
                            if (!OrderHistoryActivity.TAG.equals(className)) {
//                                startActivity(new Intent(SideMenuActivity.this, OrderHistoryActivity.class));
//                                finish();
                                intent = new Intent(SideMenuActivity.this, OrderHistoryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            mMenuDrawer.closeMenu();
                            break;

                        case 3:
                            String termsOfUse = "file:///android_asset/terms_condition.html";
                            intent = new Intent(SideMenuActivity.this, PrivacyPolicyActivity.class);
                            intent.putExtra("url", termsOfUse);
                            intent.putExtra("title", "Terms & Condition");
                            startActivity(intent);
                            mMenuDrawer.closeMenu();
                            break;
                        case 4:
                            String disclaimer = "http://high5delivery.com/privacy-policy";
                            intent = new Intent(SideMenuActivity.this, PrivacyPolicyActivity.class);
                            intent.putExtra("url", disclaimer);
                            intent.putExtra("title", "Privacy Policy");
                            startActivity(intent);
                            mMenuDrawer.closeMenu();
                            break;
                    }
                }
            });
        }
//        Add view in linearlayout
        toolbar.setNavigationIcon(R.drawable.menu_icon);
        return toolbar;
    }

    private List<String> createList(int n) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            list.add("View " + i);
        }
        return list;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SideMenuActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(SideMenuActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
//code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profileImage.setImageBitmap(bm);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        mMenuDrawer.restoreState(inState.getParcelable(STATE_MENUDRAWER));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // if (Constant.fromCommentScreen == 0) {
        outState.putParcelable(STATE_MENUDRAWER, mMenuDrawer.saveState());
        //  }
    }

    @Override
    public void onBackPressed() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if (!MainActivity.TAG.equals(className)) {
            if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
                mMenuDrawer.closeMenu();
                return;
            }
        } else {
            finish();
        }
        super.onBackPressed();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}