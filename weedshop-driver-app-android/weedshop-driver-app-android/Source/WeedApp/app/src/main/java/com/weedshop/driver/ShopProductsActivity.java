package com.weedshop.driver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.weedshop.driver.Adapter.ShopProductsAdapter;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public class ShopProductsActivity extends SideMenuActivity {
    LinearLayout llSearchView, llfilter, llFilter1, llFilter2, llFilter3, llFilter4;
    TextView tvFilter1, tvFilter2, tvFilter3, tvFilter4;
    ImageView ivSearchbtn, ivFilter1, ivFilter2, ivFilter3, ivFilter4;
    ListView grid;
    Button btn_search_product;
    Spinner spin;
    String[] title = {
            "Valhalla tangerine Gummies",
            "Breez Royal Mints (Large)",
            "Valhalla Sour Watermelon Gummies",
            "Moon Bar - Mint",
            "Breez Cinnamon CBD Mints (Large)",
            "Valhalla tangerine Gummies",
            "Breez Royal Mints (Large)",
            "Valhalla Sour Watermelon Gummies",
            "Moon Bar - Mint"
    };
    String[] webSub = {
            "SATIVA",
            "HYBRID",
            "INDICA",
            "CBD"
    };
    String[] weight = {
            "60mg THC",
            "1000mg THC",
            "60mg THC",
            "250mg THC",
            "200mg THC",
            "60mg THC",
            "1000mg THC",
            "60mg THC",
            "250mg THC"
    };
    String[] category = {
            "SATIVA",
            "HYBRID",
            "INDICA",
            "HYBRID",
            "CBD",
            "SATIVA",
            "HYBRID",
            "INDICA",
            "HYBRID"
    };
    int[] imageId = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img1
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_shop_products);
        mMenuDrawer.setMenuView(R.layout.activity_base_menu);

        llSearchView = (LinearLayout) findViewById(R.id.llSearchView);
        llfilter = (LinearLayout) findViewById(R.id.llfilter);
        llFilter1 = (LinearLayout) findViewById(R.id.ll_filter1);
        llFilter2 = (LinearLayout) findViewById(R.id.ll_filter2);
        llFilter3 = (LinearLayout) findViewById(R.id.ll_filter3);
        llFilter4 = (LinearLayout) findViewById(R.id.ll_filter4);

        tvFilter1 = (TextView) findViewById(R.id.tv_filter1);
        tvFilter2 = (TextView) findViewById(R.id.tv_filter2);
        tvFilter3 = (TextView) findViewById(R.id.tv_filter3);
        tvFilter4 = (TextView) findViewById(R.id.tv_filter4);

        ivFilter1 = (ImageView) findViewById(R.id.iv_filter1);
        ivFilter2 = (ImageView) findViewById(R.id.iv_filter2);
        ivFilter3 = (ImageView) findViewById(R.id.iv_filter3);
        ivFilter4 = (ImageView) findViewById(R.id.iv_filter4);

        initToolbar(false);
        Bundle b = getIntent().getExtras();
        String ShopName = b.getString("TITLE");
        TextView si_txt_title_signup = (TextView) findViewById(R.id.tv_shop_product_title);
        si_txt_title_signup.setText(ShopName);
        ShopProductsAdapter adapter = new ShopProductsAdapter(ShopProductsActivity.this, title, weight, category, imageId);
        grid = (ListView) findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(ShopProductsActivity.this, ProductViewActivity.class);
                intent.putExtra("TITLE_PRODUCT", title[position]);
                intent.putExtra("TITLE_WEIGHT", weight[position]);
                intent.putExtra("TITLE_CATEGORY", category[position]);
                startActivity(intent);
            }
        });
        ivSearchbtn = (ImageView) findViewById(R.id.ivSearchbtn);
        spin = (Spinner) findViewById(R.id.spn_filter_product);
        btn_search_product = (Button) findViewById(R.id.btn_search_product);
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_text_item, webSub);
        spin.setAdapter(aa);

        btn_search_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llSearchView.getVisibility() == View.VISIBLE) {
                    slideToTop(llSearchView);
                    llSearchView.setVisibility(View.GONE);
                }
                String item = spin.getSelectedItem().toString();
                if (item.equalsIgnoreCase("sativa") && llFilter1.getVisibility() == View.GONE) {
                    llfilter.setVisibility(View.VISIBLE);
                    llFilter1.setVisibility(View.VISIBLE);
                    tvFilter1.setText(item.toUpperCase());
                    GradientDrawable bgShape = (GradientDrawable) ivFilter1.getBackground();
                    bgShape.setColor(Color.parseColor("#EBB22D"));
                } else if (item.equalsIgnoreCase("hybrid") && llFilter2.getVisibility() == View.GONE) {
                    llfilter.setVisibility(View.VISIBLE);
                    llFilter2.setVisibility(View.VISIBLE);
                    tvFilter2.setText(item.toUpperCase());
                    GradientDrawable bgShape = (GradientDrawable) ivFilter2.getBackground();
                    bgShape.setColor(Color.parseColor("#18C6BC"));
                } else if (item.equalsIgnoreCase("indica") && llFilter3.getVisibility() == View.GONE) {
                    llfilter.setVisibility(View.VISIBLE);
                    llFilter3.setVisibility(View.VISIBLE);
                    tvFilter3.setText(item.toUpperCase());
                    GradientDrawable bgShape = (GradientDrawable) ivFilter3.getBackground();
                    bgShape.setColor(Color.parseColor("#2693CB"));
                } else if (item.equalsIgnoreCase("cbd") && llFilter4.getVisibility() == View.GONE) {
                    llfilter.setVisibility(View.VISIBLE);
                    llFilter4.setVisibility(View.VISIBLE);
                    tvFilter4.setText(item.toUpperCase());
                    GradientDrawable bgShape = (GradientDrawable) ivFilter4.getBackground();
                    bgShape.setColor(Color.parseColor("#D7476B"));
                }
            }
        });

        ivSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llSearchView.getVisibility() == View.VISIBLE) {
                    slideToTop(llSearchView);
                    llSearchView.setVisibility(View.GONE);
                } else {
                    slideToBottom(llSearchView);
                    llSearchView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void slideToTop(View view) {
        //view.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.slide_up);
        view.startAnimation(animation);
    }

    private void slideToBottom(View view) {
        //view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.slide_bottom);
        view.startAnimation(animation);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/
}
