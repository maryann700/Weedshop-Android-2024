package com.weedshop.driver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.weedshop.driver.CurrentOrderActivity;
import com.weedshop.driver.R;
import com.weedshop.driver.model.OrderHistory;
import com.weedshop.driver.utils.RoundedImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MTPC-83 on 5/17/2017.
 */

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {

    private ArrayList<OrderHistory> arrayList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtdate, txtpickupfrom, txtdeliverto, txtproductname, txtamount, txttime;
        public LinearLayout lnrparent;
        public RoundedImageView imgStore, imgUser;

        public MyViewHolder(View view) {
            super(view);
            txtdate = (TextView) view.findViewById(R.id.txtdate);
            txtpickupfrom = (TextView) view.findViewById(R.id.txtpickupfrom);
            txtdeliverto = (TextView) view.findViewById(R.id.txtdeliverto);
            txtproductname = (TextView) view.findViewById(R.id.txtproductname);
            txtamount = (TextView) view.findViewById(R.id.txtamount);
            txttime = (TextView) view.findViewById(R.id.txttime);
            imgStore = (RoundedImageView) view.findViewById(R.id.imgStore);
            imgUser = (RoundedImageView) view.findViewById(R.id.imgUser);
            lnrparent = (LinearLayout)view.findViewById(R.id.lnrparent);
        }
    }

    public OrderHistoryAdapter(ArrayList<OrderHistory> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @Override
    public OrderHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_raw, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrderHistoryAdapter.MyViewHolder holder, int position) {
        final OrderHistory objOrderHistory = arrayList.get(position);

        if (!TextUtils.isEmpty(objOrderHistory.getOrder_date())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date newDate = null;
            try {
                newDate = format.parse(objOrderHistory.getOrder_date());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("MMM dd yyyy");
            String date = format.format(newDate);
            holder.txtdate.setVisibility(View.VISIBLE);
            holder.txtdate.setText("Delivered on : " + date);
        } else {
            holder.txtdate.setVisibility(View.GONE);
        }

        // value for display time

        if (!TextUtils.isEmpty(objOrderHistory.getOrder_time())) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = format.parse(objOrderHistory.getOrder_time());
                format = new SimpleDateFormat("HH:mm");
                String time = format.format(newDate);
                holder.txttime.setText("Time : " + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        holder.txtpickupfrom.setText(objOrderHistory.getStore_name());
        holder.txtdeliverto.setText(objOrderHistory.getUser_name());
        holder.txtproductname.setText(objOrderHistory.getProduct_name());
        holder.txtamount.setText("$" + objOrderHistory.getFinal_total() + ".00");
        SimpleTarget targetUser = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                // do something with the bitmap
                // for demonstration purposes, let's just set it to an ImageView
                holder.imgUser.setImageBitmap(bitmap);
            }
        };
        SimpleTarget targetStore = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                // do something with the bitmap
                // for demonstration purposes, let's just set it to an ImageView
                holder.imgStore.setImageBitmap(bitmap);
            }
        };
        if (!TextUtils.isEmpty(objOrderHistory.getUser_image())) {
            Glide.with(context).load(objOrderHistory.getUser_image()).asBitmap().into(targetUser);
        }
        if (!TextUtils.isEmpty(objOrderHistory.getStore_image())) {
            Glide.with(context).load(objOrderHistory.getStore_image_url()).asBitmap().into(targetStore);
        }

        holder.lnrparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CurrentOrderActivity.class);
                intent.putExtra("orderid",objOrderHistory.getId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}