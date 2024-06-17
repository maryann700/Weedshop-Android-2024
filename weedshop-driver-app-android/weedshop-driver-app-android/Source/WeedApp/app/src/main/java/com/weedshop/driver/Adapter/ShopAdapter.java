package com.weedshop.driver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weedshop.driver.R;

/**
 * Created by MTPC-86 on 3/10/2017.
 */

public class ShopAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] web;
    private final String[] webSub;
    private final int[] Imageid;

    public ShopAdapter(Context c, String[] web, String[] websub, int[] Imageid) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
        this.webSub = websub;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.activity_grid_item, null);
            TextView textView = (TextView) grid.findViewById(R.id.tv_title);
            TextView textView2 = (TextView) grid.findViewById(R.id.tv_title2);
            ImageView imageView = (ImageView) grid.findViewById(R.id.img1);
            textView.setText(web[position]);
            textView2.setText(webSub[position]);
            imageView.setImageResource(Imageid[position]);
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}
