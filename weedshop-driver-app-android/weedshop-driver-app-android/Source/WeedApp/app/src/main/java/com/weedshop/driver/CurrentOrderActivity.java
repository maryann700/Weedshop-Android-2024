package com.weedshop.driver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.utils.RoundedImageView;
import com.weedshop.driver.webservices.ServiceHandler;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.weedshop.driver.utils.Constant.PREF_order_id;
import static com.weedshop.driver.utils.Constant.PREF_userid;

/**
 * Created by MTPC_51 on 4/18/2017.
 */

public class CurrentOrderActivity extends SideMenuActivity implements OnTaskCompleted {

    private TextView txtorderdate;
    private TextView txtorderno;
    private TextView txtordertotal;
    private TextView txtshipname;
    private TextView txtphoneno1;
    private TextView txtaddress1;
    private TextView txtitems;
    private TextView txtdeliverycharge;
    private TextView txtordertotalsummery;
    private LinearLayout lnrsipment;
    private TextView txtviewmore;
    private TextView si_txt_title_signup;
    private TextView txtshopname;
    private TextView txtshopphone1;
    private TextView txtshopadd1;
    private ViewGroup transitionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_current_order);
        mMenuDrawer.setMenuView(R.layout.activity_base_menu);

        initToolbar(false);
        init();
        currentOrderApi();

    }

    private void init() {
        transitionsContainer = (ViewGroup) findViewById(R.id.transitions_container);
        txtorderdate = (TextView) findViewById(R.id.txtorderdate);
        txtorderno = (TextView) findViewById(R.id.txtorderno);
        txtordertotal = (TextView) findViewById(R.id.txtordertotal);
        txtshipname = (TextView) findViewById(R.id.txtshipname);
        txtphoneno1 = (TextView) findViewById(R.id.txtphoneno1);
        txtaddress1 = (TextView) findViewById(R.id.txtaddress1);
        txtitems = (TextView) findViewById(R.id.txtitems);
        txtdeliverycharge = (TextView) findViewById(R.id.txtdeliverycharge);
        txtordertotalsummery = (TextView) findViewById(R.id.txtordertotalsummery);
        lnrsipment = (LinearLayout) findViewById(R.id.lnrsipment);
        txtviewmore = (TextView) findViewById(R.id.txtviewmore);
        si_txt_title_signup = (TextView) findViewById(R.id.si_txt_title_signup);
        txtshopname = (TextView) findViewById(R.id.txtshopname);
        txtshopphone1 = (TextView) findViewById(R.id.txtshopphone1);
        txtshopadd1 = (TextView) findViewById(R.id.txtshopadd1);


        txtviewmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new Fade());
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.setDuration(1000);
                TransitionManager.beginDelayedTransition(transitionsContainer, transitionSet);
                if (!txtviewmore.getText().toString().equalsIgnoreCase("View less")) {
                    txtviewmore.setText("View less");
                    if (lnrsipment.getChildCount() > 2) {
                        for (int i = 2; i < lnrsipment.getChildCount(); i++) {
                            lnrsipment.getChildAt(i).setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    txtviewmore.setText("View more");
                    if (lnrsipment.getChildCount() > 2) {
                        for (int i = 2; i < lnrsipment.getChildCount(); i++) {
                            lnrsipment.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private void currentOrderApi() {
        String orderid = "";
        Intent intent = getIntent();
// Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("orderid")) {
                orderid = extras.getString("orderid", "");
                si_txt_title_signup.setText("View order details");
            }
        } else {
            si_txt_title_signup.setText("View order details");
            orderid = sharedpreferences.getString(PREF_order_id, "");
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", sharedpreferences.getString(PREF_userid, ""));
        map.put("order_id", orderid);
//        map.put("order_id", "5");
        ServiceHandler task = new ServiceHandler(CurrentOrderActivity.this, Constant.driver_order_detail, map, true, this.getApplicationContext());
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
                    JSONArray array = jsonObject.getJSONArray("data");
                    if (array.length() > 0) {
                        for (int i1 = 0; i1 < array.length(); i1++) {
                            JSONObject jsonObjectDate = array.getJSONObject(i1);
                            if (!TextUtils.isEmpty(jsonObjectDate.getString("order_date"))) {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date newDate = null;
                                try {
                                    newDate = format.parse(jsonObjectDate.getString("order_date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                format = new SimpleDateFormat("MMM dd yyyy");
                                String date = format.format(newDate);
                                txtorderdate.setText(date);
                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("order_code"))) {
                                txtorderno.setText(jsonObjectDate.getString("order_code"));
                            }


                            if (!TextUtils.isEmpty(jsonObjectDate.getString("delivery_name"))) {
                                txtshipname.setText(jsonObjectDate.getString("delivery_name"));
                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("delivery_phone"))) {
                                String phoneNumber = jsonObjectDate.getString("delivery_phone");
                                if (phoneNumber.length() > 10) {
                                    String output = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
                                    txtphoneno1.setText(output);
                                } else {
                                    txtphoneno1.setText(phoneNumber);
                                }
                            }
                            if (!TextUtils.isEmpty(jsonObjectDate.getString("delivery_address"))) {
                                txtaddress1.setText(jsonObjectDate.getString("delivery_address"));
                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("sub_total"))) {
                                txtitems.setText("$" + jsonObjectDate.getString("sub_total") + ".00");
                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("delivery_charge"))) {
                                txtdeliverycharge.setText("$" + jsonObjectDate.getString("delivery_charge") + ".00");
                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("final_total"))) {
                                txtordertotalsummery.setText("$" + jsonObjectDate.getString("final_total") + ".00");
                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("shop_name"))) {
                                txtshopname.setText(jsonObjectDate.getString("shop_name"));
                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("shop_phone"))) {
                                String phoneNumber = jsonObjectDate.getString("shop_phone");
                                if (phoneNumber.length() > 10) {
                                    String output = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
                                    txtshopphone1.setText(output);
                                } else {
                                    txtshopphone1.setText(phoneNumber);
                                }
                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("shop_address"))) {
                                txtshopadd1.setText(jsonObjectDate.getString("shop_address"));
                            }

                            JSONArray jsonArrayProduct = jsonObjectDate.getJSONArray("products");
                            if (jsonArrayProduct.length() > 0) {
                                int totalProduct = jsonArrayProduct.length() - 2;
                                if (totalProduct <= 0) {
                                    txtviewmore.setVisibility(View.GONE);
                                } else {
                                    txtviewmore.setText("View more");
                                }
                                for (int i = 0; i < jsonArrayProduct.length(); i++) {
                                    JSONObject jsonObjectProd = jsonArrayProduct.getJSONObject(i);
                                    String imgUrl = "";
                                    String prodName = "";
                                    String desc = "";
                                    String type = "";
                                    String price = "";
                                    String color = "";
                                    String quantity = "";
                                    boolean flag = false;
                                    if (!TextUtils.isEmpty(jsonObjectProd.getString("product_name"))) {
                                        prodName = jsonObjectProd.getString("product_name");
                                    }
                                    if (!TextUtils.isEmpty(jsonObjectProd.getString("attribute_description"))) {
                                        desc = jsonObjectProd.getString("attribute_description");
                                    }

                                    if (!TextUtils.isEmpty(jsonObjectProd.getString("quantity"))) {
                                        quantity = jsonObjectProd.getString("quantity");
                                    }

                                    if (!TextUtils.isEmpty(jsonObjectProd.getString("type"))) {
                                        type = jsonObjectProd.getString("type");
                                    }
                                    if (!TextUtils.isEmpty(jsonObjectProd.getString("price"))) {
                                        price = jsonObjectProd.getString("price");
                                    }
                                    if (!TextUtils.isEmpty(jsonObjectProd.getString("color"))) {
                                        color = jsonObjectProd.getString("color");
                                    }
                                    if (!TextUtils.isEmpty(jsonObjectProd.getString("image_url"))) {
                                        imgUrl = jsonObjectProd.getString("image_url");
                                    }

                                    if (i == jsonArrayProduct.length() - 1) {
                                        flag = true;
                                    }
                                    addProduct(imgUrl, prodName, desc, type, price, color, flag, quantity);
//                                    if (i == 0) {
//                                        addProduct(imgUrl, prodName, desc, type, price, color, flag);
//                                    }
                                }
                                if (lnrsipment.getChildCount() > 2) {
                                    for (int i = 2; i < lnrsipment.getChildCount(); i++) {
                                        lnrsipment.getChildAt(i).setVisibility(View.GONE);
                                    }
                                }

                            }

                            if (!TextUtils.isEmpty(jsonObjectDate.getString("final_total"))) {
                                txtordertotal.setText("$" + jsonObjectDate.getString("final_total") + ".00 (" + jsonArrayProduct.length() + " Items)");
                            }
                        }
                    }
                } else {
                    Toast.makeText(CurrentOrderActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addProduct(String imgUrl, String prodName, String desc, String type, String price, String color, boolean size, String quantity) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_orderdetail_raw, lnrsipment, false);

        final RoundedImageView imgProduct = (RoundedImageView) view.findViewById(R.id.imgProduct);
        TextView txtpname = (TextView) view.findViewById(R.id.txtpname);
        TextView txtdesc = (TextView) view.findViewById(R.id.txtdesc);
        TextView txttype = (TextView) view.findViewById(R.id.txttype);
        TextView txtproductprice = (TextView) view.findViewById(R.id.txtproductprice);
        View viewdevider = view.findViewById(R.id.viewdevider);
        TextView txtquntity = (TextView) view.findViewById(R.id.txtquntity);
        if (size) {
            viewdevider.setVisibility(View.GONE);
        }

        txtquntity.setText("Quantity: " + quantity);
        txtpname.setText(prodName);
        txtdesc.setText(desc);
        txttype.setText(type);
        txtproductprice.setText("$" + price + ".00");

        SimpleTarget targetStore = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                // do something with the bitmap
                // for demonstration purposes, let's just set it to an ImageView
                imgProduct.setImageBitmap(bitmap);
            }
        };
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(CurrentOrderActivity.this).load(imgUrl).asBitmap().into(targetStore);
        }

        GradientDrawable bgShape = (GradientDrawable) txttype.getBackground();
        bgShape.setColor(Color.parseColor(color));

        lnrsipment.addView(view);
    }

    public void onBack(View view) {
        finish();
    }
}
