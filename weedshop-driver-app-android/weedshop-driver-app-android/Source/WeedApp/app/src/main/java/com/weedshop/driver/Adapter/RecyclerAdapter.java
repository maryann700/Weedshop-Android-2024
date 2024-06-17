package com.weedshop.driver.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.weedshop.driver.R;
import com.weedshop.driver.SideMenuActivity;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter {
    private List<String> mDataSet = new ArrayList<>();
    private LayoutInflater mInflater;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    int currentItemPosition;

    public RecyclerAdapter(Context context, List<String> dataSet) {
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);

        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_swipe_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        final ViewHolder holder = (ViewHolder) h;

        if (mDataSet != null && 0 <= position && position < mDataSet.size()) {
            final String data = mDataSet.get(position);
            currentItemPosition = position;
            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
            binderHelper.bind(holder.swipeLayout, data);
            binderHelper.setOpenOnlyOne(true);
            // Bind your data here
            holder.bind(data);
        }
    }


    @Override
    public int getItemCount() {
        if (mDataSet == null)

            return 0;
        return mDataSet.size();
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeRevealLayout swipeLayout;
        private View deleteLayout;
        RelativeLayout rlPlueQuantity, rlMinusQuantity;
        TextView tvFinalQuantity, tvProductPrice, tvTotalQuantity;
        //private TextView textView;
        int qtyCount = 1;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.iv_delete_product);
            rlMinusQuantity = (RelativeLayout) itemView.findViewById(R.id.rl_minus_quantity);
            rlPlueQuantity = (RelativeLayout) itemView.findViewById(R.id.rl_plus_quantity);
            tvTotalQuantity = (TextView) itemView.findViewById(R.id.tv_total_quantity);
            tvFinalQuantity = (TextView) itemView.findViewById(R.id.tv_final_quantity);
            tvProductPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
            tvFinalQuantity.setText(tvTotalQuantity.getText().toString());
            rlMinusQuantity.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (qtyCount > 1) {
                        qtyCount--;
                        tvTotalQuantity.setText(String.valueOf(qtyCount));
                        tvFinalQuantity.setText(String.valueOf(qtyCount));
                    } else {
                        tvTotalQuantity.setText("1");
                    }
                    qtyCount = Integer.parseInt(tvTotalQuantity.getText().toString());
                }
            });
            rlPlueQuantity.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    qtyCount++;
                    tvTotalQuantity.setText(String.valueOf(qtyCount));
                    tvFinalQuantity.setText(String.valueOf(qtyCount));
                    qtyCount = Integer.parseInt(tvTotalQuantity.getText().toString());
                }
            });
        }

        public void bind(String data) {
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataSet.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    if (mDataSet.size() == 0) {
                        SideMenuActivity.btnCheckout.setVisibility(View.GONE);
                        SideMenuActivity.llCharge.setVisibility(View.GONE);
                    }
                }
            });

            /*if (swipeLayout.isOpened()) {
                swipeLayout.setBackgroundColor(Color.parseColor("#3F434E"));
            } else {
                swipeLayout.setBackgroundColor(Color.parseColor("#282B30"));
            }*/
            //textView.setText(data);
        }
    }
}