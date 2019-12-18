package com.ics.fvfs.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.R;
import com.ics.fvfs.DatabaseHandler;


public class Cart_adapter extends RecyclerView.Adapter<Cart_adapter.ProductHolder> {
    //private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    //ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    ArrayList<HashMap<String, String>> list;
    Activity activity;
    int lastpostion;
    /*CommonClass common;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;*/
    DatabaseHandler dbHandler;

    public Cart_adapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        this.list = list;
        this.activity = activity;

        dbHandler = new DatabaseHandler(activity);
        /*common = new CommonClass(activity);
        File cacheDir = StorageUtils.getCacheDirectory(activity);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(activity)
                .build();
        ImageLoader.getInstance().init(imgconfig);*/
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);

        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final HashMap<String, String> map = list.get(position);

        Glide.with(activity)
                .load(BaseURL.IMG_PRODUCT_URL + map.get("product_image"))
              //  .centerCrop()
                .placeholder(R.drawable.shoplogo)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);

        holder.tv_title.setText(map.get("product_name"));

        try
        {
        if (!map.get("pack1").isEmpty()) {
            holder.d1.setText(""+map.get("pack1")+" - ");}

        if (!map.get("pack2").isEmpty()){
            holder.d2.setText(""+map.get("pack2")+" - "); }  }
        catch (Exception ex)
        {          ex.printStackTrace();    }

        holder.dd1.setText("Rs. "+map.get("mrp1"));
        holder.dd2.setText("Rs. "+map.get("mrp2"));

        holder.dtx1.setText("Rs. "+map.get("price1"));
        holder.dtx2.setText("Rs. "+map.get("price2"));

        Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
        Double price;

        //holder.ttl.setText("Total: "+activity.getResources().getString(R.string.currency));

        if (map.get("offer").equals("0"))
        {
            holder.tv_price.setText(map.get("unit_value") + " " +
                    map.get("unit") +" "+activity.getResources().getString(R.string.currency)+" "+ map.get("price"));
            Log.e("OFFER  : ",">>>>>>>>>>>> "+map.get("unit_value") + "     " +
                    "......"+ map.get("unit") +" _____ "+ map.get("price"));
            holder.tv_contetiy.setText(map.get("qty"));
            price =  Double.parseDouble(map.get("price"));
            holder.tv_total.setText("" + price * items );
        }

        else if (map.get("offer").equals("1"))
        {
            holder.tv_price.setText(map.get("pack1") + " " + map.get("price1"));
            Log.e("OFFER 2 : ",">>>>>>>>>>>> "+map.get("pack1") + "     ......");
            holder.tv_total.setText(activity.getResources().getString(R.string.currency)+" " + map.get("price1"));
            holder.ch1.setChecked(true);
        }

        else if (map.get("offer").equals("2"))
        {
            holder.tv_price.setText(map.get("pack2") + " " + map.get("price2"));
            Log.e("OFFER 2 : ",">>>>>>>>>>>> "+map.get("pack2") + "     ......");
            holder.tv_total.setText(activity.getResources().getString(R.string.currency)+" " + map.get("price2"));
            holder.ch2.setChecked(true);
        }

        //holder.tv_total.setText("" + price * items);

        if (Integer.parseInt(map.get("qty")) == 1)
        {
            holder.ch1.setEnabled(true);
            holder.ch2.setEnabled(true);
        }
        else if (Integer.parseInt(map.get("qty")) > 1)
        {
            holder.ch1.setEnabled(false);
            holder.ch2.setEnabled(false);
        }

        holder.ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.ch1.isChecked())
                {
                    holder.ch2.setChecked(false);
                    holder.iv_plus.setEnabled(false);
                    holder.iv_minus.setEnabled(false);
                    dbHandler.update_Status(1,map.get("product_id"));
                    holder.tv_price.setText(map.get("pack1") + " " + map.get("price1"));
                    holder.tv_total.setText(activity.getResources().getString(R.string.currency)+" " + map.get("price1"));
                    updateintent();
                }
                else if (holder.ch2.isChecked())
                {
                    holder.tv_price.setText(map.get("pack2") + " " + map.get("price2"));
                    holder.tv_total.setText(activity.getResources().getString(R.string.currency)+" " + map.get("price2"));
                }
                else
                {
                    holder.iv_plus.setEnabled(true);
                    holder.iv_minus.setEnabled(true);

                    holder.tv_price.setText(map.get("unit_value") + " " +
                            map.get("unit") +" "+activity.getResources().getString(R.string.currency)+" "+ map.get("price"));
                    holder.tv_contetiy.setText(map.get("qty"));
                    Double price =  Double.parseDouble(map.get("price"));
                    Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
                    holder.tv_total.setText("" + price * items );
                    dbHandler.update_Status(0,map.get("product_id"));
                    updateintent();
                }
            }
        });

        holder.ch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.ch2.isChecked())
                {
                    holder.ch1.setChecked(false);
                    holder.iv_plus.setEnabled(false);
                    holder.iv_minus.setEnabled(false);
                    dbHandler.update_Status(2,map.get("product_id"));
                    holder.tv_price.setText(map.get("pack2") + " " + map.get("price2"));
                    holder.tv_total.setText(activity.getResources().getString(R.string.currency)+" " + map.get("price2"));
                    updateintent();
                }

                else if (holder.ch1.isChecked())
                {
                    holder.tv_price.setText(map.get("pack1") + " " + map.get("price1"));
                    holder.tv_total.setText(activity.getResources().getString(R.string.currency)+" " + map.get("price1"));
                }

                else
                {
                    holder.iv_plus.setEnabled(true);
                    holder.iv_minus.setEnabled(true);

                    holder.tv_price.setText(map.get("unit_value") + " " +
                            map.get("unit") +" "+activity.getResources().getString(R.string.currency)+" "+ map.get("price"));
                    holder.tv_contetiy.setText(map.get("qty"));
                    Double price =  Double.parseDouble(map.get("price"));
                    Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
                    holder.tv_total.setText("" + price * items );
                    dbHandler.update_Status(0,map.get("product_id"));
                    updateintent();
                }
            }
        });

        holder.iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 1;
                if (!holder.tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(holder.tv_contetiy.getText().toString());

                if (qty > 1) {
                    qty = qty - 1;
                    holder.tv_contetiy.setText(String.valueOf(qty));
                    holder.ch1.setEnabled(false);
                    holder.ch1.setChecked(false);
                    holder.ch2.setEnabled(false);
                    holder.ch2.setChecked(false);
                }

                if (holder.tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
                    dbHandler.removeItemFromCart(map.get("product_id"));
                    list.remove(position);
                    notifyDataSetChanged();

                    updateintent();
                }
                if (qty == 1)
                {
                    holder.ch1.setEnabled(true);
                    holder.ch2.setEnabled(true);
                }

                dbHandler.update_Status(0,map.get("product_id"));

                dbHandler.setCart(map, Float.valueOf(holder.tv_contetiy.getText().toString()));

                Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
                Double price = Double.parseDouble(map.get("price"));
                holder.tv_total.setText("" + price * items );
                //holder.tv_total.setText(activity.getResources().getString(R.string.tv_cart_total) + price * items + " " +activity.getResources().getString(R.string.currency));
                updateintent();
            }
        });   // eof icon minus

        holder.iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int qty = Integer.valueOf(holder.tv_contetiy.getText().toString());
                qty = qty + 1;

                holder.tv_contetiy.setText(String.valueOf(qty));

                dbHandler.setCart(map, Float.valueOf(holder.tv_contetiy.getText().toString()));
                dbHandler.update_Status(0,map.get("product_id"));

                Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
                Double price = Double.parseDouble(map.get("price"));

                holder.tv_total.setText("" + price * items );
                //holder.tv_total.setText(activity.getResources().getString(R.string.tv_cart_total) + price * items + " " +activity.getResources().getString(R.string.currency));
                updateintent();

                holder.ch1.setEnabled(false);
                holder.ch1.setChecked(false);
                holder.ch2.setEnabled(false);
                holder.ch2.setChecked(false);

            }
        });   // eof icon plus

//        holder.tv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                dbHandler.setCart(map, Float.valueOf(holder.tv_contetiy.getText().toString()));
//
//                Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
//                Double price = Double.parseDouble(map.get("price"));
//
//                holder.tv_total.setText("" + price * items );
//                //holder.tv_total.setText(activity.getResources().getString(R.string.tv_cart_total) + price * items + " " +activity.getResources().getString(R.string.currency));
//                updateintent();
//            }
//        });

        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.removeItemFromCart(map.get("product_id"));
                list.remove(position);
                notifyDataSetChanged();

                updateintent();
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_price, tv_total, tv_contetiy, tv_add, ttl ,dtx1, dtx2, d1, d2, dd1 ,dd2,
                tv_unit,tv_unit_value;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;
        public CheckBox ch1 , ch2;

        public ProductHolder(View view) {
            super(view);

            ttl = (TextView) view.findViewById(R.id.textView4);
            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            //tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);

            dtx1 = (TextView) view.findViewById(R.id.tx_dis1);
            dtx2 = (TextView) view.findViewById(R.id.tx_dis2);
            ch1 = (CheckBox) view.findViewById(R.id.chk1);
            ch2 = (CheckBox) view.findViewById(R.id.chk2);
            d1 = (TextView) view.findViewById(R.id.tx_d1);
            dd1 = (TextView) view.findViewById(R.id.tx_dd1);
            d2 = (TextView) view.findViewById(R.id.tx_d2);
            dd2 = (TextView) view.findViewById(R.id.tx_dd2);

            //tv_add.setText(R.string.tv_pro_update);

        }
    }

    private void updateintent(){
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        activity.sendBroadcast(updates);
    }
}

