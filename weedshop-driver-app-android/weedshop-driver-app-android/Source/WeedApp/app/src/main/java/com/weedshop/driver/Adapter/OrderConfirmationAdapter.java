package com.weedshop.driver.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.weedshop.driver.R;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationAdapter extends RecyclerView.Adapter {
    private List<String> mDataSet = new ArrayList<>();
    private LayoutInflater mInflater;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    int currentItemPosition;

    public OrderConfirmationAdapter(Context context, List<String> dataSet) {
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);

        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.order_confirmation_item, parent, false);

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
        TextView tvFinalQuantity, tvProductPrice;
        //private TextView textView;
        int qtyCount = 1;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFinalQuantity = (TextView) itemView.findViewById(R.id.tv_final_quantity);
            tvProductPrice = (TextView) itemView.findViewById(R.id.tv_product_price);


        }

        public void bind(String data) {


            /*if (swipeLayout.isOpened()) {
                swipeLayout.setBackgroundColor(Color.parseColor("#3F434E"));
            } else {
                swipeLayout.setBackgroundColor(Color.parseColor("#282B30"));
            }*/
            //textView.setText(data);
        }
    }
}