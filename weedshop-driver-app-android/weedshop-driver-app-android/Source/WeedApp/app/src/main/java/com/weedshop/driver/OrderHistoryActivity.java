package com.weedshop.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.weedshop.driver.Adapter.OrderHistoryAdapter;
import com.weedshop.driver.listener.EndlessRecyclerOnScrollListener;
import com.weedshop.driver.model.OrderHistory;
import com.weedshop.driver.utils.Constant;
import com.weedshop.driver.utils.OnTaskCompleted;
import com.weedshop.driver.webservices.ServiceHandler;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.weedshop.driver.utils.Constant.PREF_userid;

public class OrderHistoryActivity extends SideMenuActivity implements OnTaskCompleted {

    public static String TAG = OrderHistoryActivity.class.getSimpleName();
    private ArrayList<OrderHistory> mainOrderHistoryList = new ArrayList<>();
    private int pageNumberLoadMore = 1;
    private RecyclerView lstorder;
    private OrderHistoryAdapter orderHistoryAdapter;
    private boolean loading = true;
    private Date oldDate;
    private int totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_order_history);
        mMenuDrawer.setMenuView(R.layout.activity_base_menu);

        initToolbar(false);
        getClassName(TAG);

        lstorder = (RecyclerView) findViewById(R.id.lstorder);
        lstorder.setHasFixedSize(true);
        final LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(OrderHistoryActivity.this, LinearLayoutManager.VERTICAL, false);
        lstorder.setLayoutManager(horizontalLayoutManagaer);
        lstorder.setItemAnimator(new DefaultItemAnimator());


        orderHistoryApi(String.valueOf(pageNumberLoadMore), true);

        lstorder.addOnScrollListener(new EndlessRecyclerOnScrollListener(horizontalLayoutManagaer) {
            @Override
            public void onLoadMore(int current_page) {
                if (pageNumberLoadMore < totalPages) {
                    pageNumberLoadMore++;
                    orderHistoryApi(String.valueOf(pageNumberLoadMore), false);
                }
            }
        });


    }

    private void orderHistoryApi(String page, boolean flag) {
        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", sharedpreferences.getString(PREF_userid, ""));
        map.put("page", page);
        ServiceHandler task = new ServiceHandler(OrderHistoryActivity.this, Constant.driver_order_history, map, flag,this.getApplicationContext());
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
                    totalPages = jsonObject.getInt("totalPages");
                    JSONArray array = jsonObject.getJSONArray("data");
                    if (array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject1 = array.getJSONObject(i);
                            OrderHistory objOrderHistory = new OrderHistory();
                            if (!TextUtils.isEmpty(jsonObject1.getString("id"))) {
                                objOrderHistory.setId(jsonObject1.getString("id"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("order_code"))) {
                                objOrderHistory.setOrder_code(jsonObject1.getString("order_code"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("user_id"))) {
                                objOrderHistory.setUser_id(jsonObject1.getString("user_id"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("driver_id"))) {
                                objOrderHistory.setDriver_id(jsonObject1.getString("driver_id"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("store_id"))) {
                                objOrderHistory.setStore_id(jsonObject1.getString("store_id"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("order_date"))) {
                                // set value for display time
                                objOrderHistory.setOrder_time(jsonObject1.getString("order_date"));
                                // set value for display time end
                                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                                if (pageNumberLoadMore == 1 && i == 0) {
                                    oldDate = formatDate.parse(jsonObject1.getString("order_date"));
                                    objOrderHistory.setOrder_date(jsonObject1.getString("order_date"));
                                } else {
                                    Date date2 = formatDate.parse(jsonObject1.getString("order_date"));
                                    if (oldDate.compareTo(date2) == 0) {
                                        objOrderHistory.setOrder_date("");
                                    } else {
                                        oldDate = formatDate.parse(jsonObject1.getString("order_date"));
                                        objOrderHistory.setOrder_date(jsonObject1.getString("order_date"));
                                    }
                                }
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("final_total"))) {
                                objOrderHistory.setFinal_total(jsonObject1.getString("final_total"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("status"))) {
                                objOrderHistory.setStatus(jsonObject1.getString("status"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("store_name"))) {
                                objOrderHistory.setStore_name(jsonObject1.getString("store_name"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("user_name"))) {
                                objOrderHistory.setUser_name(jsonObject1.getString("user_name"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("user_image"))) {
                                objOrderHistory.setUser_image(jsonObject1.getString("user_image"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("store_image"))) {
                                objOrderHistory.setStore_image(jsonObject1.getString("store_image"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("user_image_url"))) {
                                objOrderHistory.setUser_image_url(jsonObject1.getString("user_image_url"));
                            }

                            if (!TextUtils.isEmpty(jsonObject1.getString("store_image_url"))) {
                                objOrderHistory.setStore_image_url(jsonObject1.getString("store_image_url"));
                            }

                            JSONArray arrayProduct = jsonObject1.getJSONArray("products");
                            if (arrayProduct.length() > 0) {
                                for (int j = 0; j < arrayProduct.length(); j++) {
                                    JSONObject jsonObject2 = arrayProduct.getJSONObject(j);
                                    if (!TextUtils.isEmpty(jsonObject2.getString("product_id"))) {
                                        objOrderHistory.setProduct_id(jsonObject2.getString("product_id"));
                                    }

                                    if (!TextUtils.isEmpty(jsonObject2.getString("product_name"))) {
                                        objOrderHistory.setProduct_name(jsonObject2.getString("product_name"));
                                    }
                                }
                            }
//                            ArrayList<OrderHistory> orderHistoryArrayList = new ArrayList<>();
//                            orderHistoryArrayList.add(objOrderHistory);
                            mainOrderHistoryList.add(objOrderHistory);

                        }
                        if (pageNumberLoadMore == 1) {
                            orderHistoryAdapter = new OrderHistoryAdapter(mainOrderHistoryList, OrderHistoryActivity.this);
                            lstorder.setAdapter(orderHistoryAdapter);
                        } else {
                            orderHistoryAdapter.notifyDataSetChanged();
                        }
                    }

                } else {
//                    Toast.makeText(OrderHistoryActivity.this, msg, Toast.LENGTH_LONG).show();
                    findViewById(R.id.txtmsg).setVisibility(View.VISIBLE);
                    lstorder.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onBack(View view) {
        startActivity(new Intent(OrderHistoryActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderHistoryActivity.this, MainActivity.class));
        finish();
    }
}
