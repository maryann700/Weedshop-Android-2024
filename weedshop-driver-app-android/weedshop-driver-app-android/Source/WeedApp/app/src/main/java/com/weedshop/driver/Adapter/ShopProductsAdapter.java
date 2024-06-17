package com.weedshop.driver.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weedshop.driver.R;

/**
 * Created by MTPC-86 on 3/10/2017.
 */

public class ShopProductsAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] productTitle;
    private final String[] productWeight;
    private final String[] productCategory;
    private final int[] Imageid;

    public ShopProductsAdapter(Context c, String[] web, String[] websub, String[] category, int[] Imageid) {
        mContext = c;
        this.Imageid = Imageid;
        this.productTitle = web;
        this.productCategory = category;
        this.productWeight = websub;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return productTitle.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shop_products_item, null);
            holder = new ViewHolder();
            holder.tvProductTitle = (TextView) convertView.findViewById(R.id.tv_product_title);
            holder.tvWeight = (TextView) convertView.findViewById(R.id.tv_weight);
            holder.tvCategory = (TextView) convertView.findViewById(R.id.tv_category);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvProductTitle.setText(productTitle[position]);
        holder.tvWeight.setText(productWeight[position]);
        holder.tvCategory.setText(productCategory[position]);
        if (productCategory[position].equalsIgnoreCase("SATIVA")) {
            GradientDrawable bgShape = (GradientDrawable) holder.tvCategory.getBackground();
            bgShape.setColor(Color.parseColor("#EBB22D"));
        } else if (productCategory[position].equalsIgnoreCase("HYBRID")) {
            GradientDrawable bgShape = (GradientDrawable) holder.tvCategory.getBackground();
            bgShape.setColor(Color.parseColor("#18C6BC"));
        } else if (productCategory[position].equalsIgnoreCase("INDICA")) {
            GradientDrawable bgShape = (GradientDrawable) holder.tvCategory.getBackground();
            bgShape.setColor(Color.parseColor("#2693CB"));
        } else if (productCategory[position].equalsIgnoreCase("CBD")) {
            GradientDrawable bgShape = (GradientDrawable) holder.tvCategory.getBackground();
            bgShape.setColor(Color.parseColor("#D7476B"));
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvProductTitle, tvWeight, tvCategory, tvPrice;
    }
}
