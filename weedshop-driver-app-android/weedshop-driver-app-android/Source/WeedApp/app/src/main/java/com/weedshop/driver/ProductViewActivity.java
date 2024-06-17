package com.weedshop.driver;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.weedshop.driver.Adapter.SlidingImage_Adapter;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.ArrayList;


public class ProductViewActivity extends SideMenuActivity {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES = {R.drawable.img_viewpager, R.drawable.img_viewpager, R.drawable.img_viewpager, R.drawable.img_viewpager};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    ImageView[] ivArrayDotsPager;
    ImageView viewPagerDots, ivSelectedDot;
    LinearLayout llSelectedFilter;
    TextView tvSelectedWeight, tvSelectedFilter, tvCountQuantity;
    int qtyCount = 1;
    RelativeLayout rlPlusQuantity, rlMinusQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_product_view);
        mMenuDrawer.setMenuView(R.layout.activity_base_menu);
        initToolbar(false);
        Bundle b = getIntent().getExtras();
        String titleProduct = b.getString("TITLE_PRODUCT");
        String titleWeight = b.getString("TITLE_WEIGHT");
        String titleCategory = b.getString("TITLE_CATEGORY");
        TextView productTitle = (TextView) findViewById(R.id.tv_product_title);
        llSelectedFilter = (LinearLayout) findViewById(R.id.ll_selected_filter);
        tvSelectedWeight = (TextView) findViewById(R.id.tv_selected_weight);
        tvSelectedFilter = (TextView) findViewById(R.id.tv_selected_filter);
        ivSelectedDot = (ImageView) findViewById(R.id.iv_selected_dot);
        tvCountQuantity = (TextView) findViewById(R.id.tv_count_quantity);
        tvCountQuantity.setText("1");
        rlMinusQuantity = (RelativeLayout) findViewById(R.id.rl_minus_quantity_pd);
        rlPlusQuantity = (RelativeLayout) findViewById(R.id.rl_plus_quantity_pd);
        rlMinusQuantity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (qtyCount > 1) {
                    qtyCount--;
                    tvCountQuantity.setText(String.valueOf(qtyCount));
                } else {
                    tvCountQuantity.setText("1");
                }
                qtyCount = Integer.parseInt(tvCountQuantity.getText().toString());
            }
        });
        rlPlusQuantity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //if (qtyCount > 1) {
                qtyCount++;
                tvCountQuantity.setText(String.valueOf(qtyCount));
                /*} else {
                tvCountQuantity.setText("1");
            }*/
                qtyCount = Integer.parseInt(tvCountQuantity.getText().

                        toString());
            }
        });
        GradientDrawable bgShape1 = (GradientDrawable) ivSelectedDot.getBackground();
        bgShape1.setColor(Color.parseColor("#FFFFFF"));
        if (titleCategory != null)

        {
            if (titleCategory.equalsIgnoreCase("SATIVA")) {
                GradientDrawable bgShape = (GradientDrawable) llSelectedFilter.getBackground();
                bgShape.setColor(Color.parseColor("#EBB22D"));
            } else if (titleCategory.equalsIgnoreCase("HYBRID")) {
                GradientDrawable bgShape = (GradientDrawable) llSelectedFilter.getBackground();
                bgShape.setColor(Color.parseColor("#18C6BC"));
            } else if (titleCategory.equalsIgnoreCase("INDICA")) {
                GradientDrawable bgShape = (GradientDrawable) llSelectedFilter.getBackground();
                bgShape.setColor(Color.parseColor("#2693CB"));
            } else if (titleCategory.equalsIgnoreCase("CBD")) {
                GradientDrawable bgShape = (GradientDrawable) llSelectedFilter.getBackground();
                bgShape.setColor(Color.parseColor("#D7476B"));
            }
            tvSelectedFilter.setText(titleCategory.toUpperCase());
            tvSelectedWeight.setText(titleWeight);
        }
        productTitle.setText(titleProduct);
        for (
                int i = 0;
                i < IMAGES.length; i++)

        {
            ImagesArray.add(IMAGES[i]);
        }

        /*mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(ProductViewActivity.this, ImagesArray));*/
        viewPagerDots = (ImageView)

                findViewById(R.id.iv_dots);

        final HorizontalInfiniteCycleViewPager infiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) findViewById(R.id.hicvp);
        infiniteCycleViewPager.setAdapter(new

                SlidingImage_Adapter(ProductViewActivity.this, ImagesArray));
        infiniteCycleViewPager.setScrollDuration(500);
        infiniteCycleViewPager.setInterpolator(null);
        infiniteCycleViewPager.setMediumScaled(true);
        infiniteCycleViewPager.setMaxPageScale(0.84f);
        infiniteCycleViewPager.setMinPageScale(0.8F);
        infiniteCycleViewPager.setCenterPageScaleOffset(30.0F);
        infiniteCycleViewPager.setMinPageScaleOffset(30.0F);
        //setupPagerIndidcatorDots();

        /*infiniteCycleViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < ImagesArray.size(); i++) {
                    ivArrayDotsPager[i].setImageResource(R.drawable.img_dot_unselected);
                }
                ivArrayDotsPager[position].setImageResource(R.drawable.img_dot_selected);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        //infiniteCycleViewPager.setOnInfiniteCyclePageTransformListener(...);
        /*CircleIndicator indicator = (CircleIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);*/

        NUM_PAGES = IMAGES.length;

        // Auto start of viewpager
        /*final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };*/
        /*Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);*/

        // Pager listener over indicator
        /*indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });*/


    }

    private void setupPagerIndidcatorDots() {
        //ivArrayDotsPager = new ImageView[eventImagesUrl.size()];
        for (int i = 0; i < IMAGES.length; i++) {
            ivArrayDotsPager[i] = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            ivArrayDotsPager[i].setLayoutParams(params);
            ivArrayDotsPager[i].setImageResource(R.drawable.img_dot_unselected);
            //ivArrayDotsPager[i].setAlpha(0.4f);
            ivArrayDotsPager[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(1);
                }
            });
            //viewPagerDots.addView(ivArrayDotsPager[i]);
            viewPagerDots.bringToFront();
        }
    }
}
