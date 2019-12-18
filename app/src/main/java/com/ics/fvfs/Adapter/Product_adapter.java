package com.ics.fvfs.Adapter;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.Fragment.Show_pro_detail_fragment;
import com.ics.fvfs.Model.Product_model;
import com.ics.fvfs.R;
import com.ics.fvfs.DatabaseHandler;
import com.ics.fvfs.MainActivity;


public class Product_adapter extends RecyclerView.Adapter<Product_adapter.MyViewHolder>
        implements Filterable {

    private List<Product_model> modelList;
    private List<Product_model> mFilteredList;
    private Context context;
    private DatabaseHandler dbcart;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_title, tv_price, tv_total, tv_contetiy, tv_add, mrpPrice, dtx1, dtx2, d1, d2, dd1 ,dd2;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;
        public CheckBox ch1, ch2;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);
            mrpPrice = (TextView) view.findViewById(R.id.mrpPrice);
            dtx1 = (TextView) view.findViewById(R.id.tx_dis1);
            dtx2 = (TextView) view.findViewById(R.id.tx_dis2);
            ch1 = (CheckBox) view.findViewById(R.id.chk1);
            ch2 = (CheckBox) view.findViewById(R.id.chk2);
            d1 = (TextView) view.findViewById(R.id.tx_d1);
            dd1 = (TextView) view.findViewById(R.id.tx_dd1);
            d2 = (TextView) view.findViewById(R.id.tx_d2);
            dd2 = (TextView) view.findViewById(R.id.tx_dd2);

            iv_remove.setVisibility(View.GONE);

            iv_minus.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            tv_add.setOnClickListener(this);
            iv_logo.setOnClickListener(this);

            CardView cardView = (CardView) view.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();

            if (id == R.id.iv_subcat_plus) {

                int qty = Integer.valueOf(tv_contetiy.getText().toString());
                qty = qty + 1;

                tv_contetiy.setText(String.valueOf(qty));
                ch1.setEnabled(false);
                ch2.setEnabled(false);

            } else if (id == R.id.iv_subcat_minus) {

                int qty = 1;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(tv_contetiy.getText().toString());

                if (qty > 1) {
                    qty = qty - 1;
                    tv_contetiy.setText(String.valueOf(qty));
                    ch1.setEnabled(false);
                    ch2.setEnabled(false);
                }
                if (qty == 1)
                {
                    ch1.setEnabled(true);
                    ch2.setEnabled(true);
                }

            } else if (id == R.id.tv_subcat_add) {

                HashMap<String, String> map = new HashMap<>();

                map.put("product_id", modelList.get(position).getProduct_id());
                map.put("category_id", modelList.get(position).getCategory_id());
                map.put("product_image", modelList.get(position).getProduct_image());
                map.put("increament", modelList.get(position).getIncreament());
                map.put("product_name", modelList.get(position).getProduct_name());

                map.put("price", modelList.get(position).getPrice());
                map.put("stock", modelList.get(position).getIn_stock());
                map.put("title", modelList.get(position).getTitle());
                map.put("unit", modelList.get(position).getUnit());
                map.put("Mrp", modelList.get(position).getMrp());

                map.put("unit_value", modelList.get(position).getUnit_value());

                map.put("pack1",modelList.get(position).getPack1());
                map.put("pack2",modelList.get(position).getPack2());
                map.put("mrp1",modelList.get(position).getMrp1());
                map.put("mrp2",modelList.get(position).getMrp2());
                map.put("price1",modelList.get(position).getPrice1());
                map.put("price2",modelList.get(position).getPrice2());


                if (ch1.isChecked())
                {
                    map.put("offer","1");
                    Log.e("OFFER 1","11111");
                }
                else if (ch2.isChecked())
                {
                    map.put("offer","2");
                    Log.e("OFFER 2","22222");
                }
                else
                {
                    map.put("offer","0");
                    Log.e("OFFER 0","0000000000");
                }

                if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {

                    if (dbcart.isInCart(map.get("product_id"))) {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    } else {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    }
                } else {
                    dbcart.removeItemFromCart(map.get("product_id"));
                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
                }

             /*   if (ch1.isChecked())
                {
                    map.put("price", modelList.get(position).getPrice1());
                    map.put("Mrp", modelList.get(position).getMrp1());
                    map.put("unit", modelList.get(position).getPack1());
                    map.put("unit_value",modelList.get(position).getUnit_value());
                    Log.e("111111111111","CheckBox ONE is checked");
                }
                else if (ch2.isChecked())
                {
                    map.put("price", modelList.get(position).getPrice2());
                    map.put("Mrp", modelList.get(position).getMrp2());
                    map.put("unit", modelList.get(position).getPack2());
                    map.put("unit_value", modelList.get(position).getUnit_value());
                    Log.e("2222222222222","CheckBox TWO is checked");}
                else
                {
                    map.put("price", modelList.get(position).getPrice());
                    map.put("Mrp", modelList.get(position).getMrp());
                    map.put("unit", modelList.get(position).getUnit());
                    map.put("unit_value", modelList.get(position).getUnit_value());
                    Log.e("00000000000000","NOOOO CheckBox is checked");     }*/

                Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
                Double price = Double.parseDouble(map.get("price"));

                tv_total.setText("" + price * items);
//                mrpPrice.setText(map.get("Mrp"));
                ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());

            } else if (id == R.id.iv_subcat_img) {
//                Intent intent=new Intent(context, ShowProdDetail.class);
//
//                intent.putExtra("product_id", modelList.get(position).getProduct_id());
//                intent.putExtra("category_id", modelList.get(position).getCategory_id());
//                intent.putExtra("product_image", modelList.get(position).getProduct_image());
//                intent.putExtra("increament", modelList.get(position).getIncreament());
//                intent.putExtra("product_name", modelList.get(position).getProduct_name());
//                intent.putExtra("price", modelList.get(position).getPrice());
//                intent.putExtra("stock", modelList.get(position).getIn_stock());
//                intent.putExtra("title", modelList.get(position).getTitle());
//                intent.putExtra("unit", modelList.get(position).getUnit());
//                intent.putExtra("Mrp", modelList.get(position).getMrp());
//                intent.putExtra("unit_value", modelList.get(position).getUnit_value());
//                intent.putExtra("Prod_description", modelList.get(position).getProduct_description());
//
//                context.startActivity(intent);

                Bundle args = new Bundle();
                Fragment fm = new Show_pro_detail_fragment();
                args.putString("product_id", modelList.get(position).getProduct_id());
                args.putString("category_id", modelList.get(position).getCategory_id());
                args.putString("product_image", modelList.get(position).getProduct_image());
                args.putString("increament", modelList.get(position).getIncreament());
                args.putString("product_name", modelList.get(position).getProduct_name());
                args.putString("price", modelList.get(position).getPrice());
                args.putString("stock", modelList.get(position).getIn_stock());
                args.putString("title", modelList.get(position).getTitle());
                args.putString("unit", modelList.get(position).getUnit());
                args.putString("Mrp", modelList.get(position).getMrp());
                args.putString("unit_value", modelList.get(position).getUnit_value());
                args.putString("Prod_description", modelList.get(position).getProduct_description());

                args.putString("pack1",modelList.get(position).getPack1());
                args.putString("pack2",modelList.get(position).getPack2());
                args.putString("mrp1",modelList.get(position).getMrp1());
                args.putString("mrp2",modelList.get(position).getMrp2());
                args.putString("price1",modelList.get(position).getPrice1());
                args.putString("price2",modelList.get(position).getPrice2());

                fm.setArguments(args);

                FragmentManager fragmentManager = ((AppCompatActivity)context).getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();



                //showImage(modelList.get(position).getProduct_image());
            } else if (id == R.id.card_view) {
//                Intent intent=new Intent(context, ShowProdDetail.class);
//
//                intent.putExtra("product_id", modelList.get(position).getProduct_id());
//                intent.putExtra("category_id", modelList.get(position).getCategory_id());
//                intent.putExtra("product_image", modelList.get(position).getProduct_image());
//                intent.putExtra("increament", modelList.get(position).getIncreament());
//                intent.putExtra("product_name", modelList.get(position).getProduct_name());
//                intent.putExtra("price", modelList.get(position).getPrice());
//                intent.putExtra("stock", modelList.get(position).getIn_stock());
//                intent.putExtra("title", modelList.get(position).getTitle());
//                intent.putExtra("unit", modelList.get(position).getUnit());
//                intent.putExtra("Mrp", modelList.get(position).getMrp());
//                intent.putExtra("unit_value", modelList.get(position).getUnit_value());
//                intent.putExtra("Prod_description", modelList.get(position).getProduct_description());
//
//                context.startActivity(intent);

                Bundle args = new Bundle();
                Fragment fm = new Show_pro_detail_fragment();
                args.putString("product_id", modelList.get(position).getProduct_id());
                args.putString("category_id", modelList.get(position).getCategory_id());
                args.putString("product_image", modelList.get(position).getProduct_image());
                args.putString("increament", modelList.get(position).getIncreament());
                args.putString("product_name", modelList.get(position).getProduct_name());
                args.putString("price", modelList.get(position).getPrice());
                args.putString("stock", modelList.get(position).getIn_stock());
                args.putString("title", modelList.get(position).getTitle());
                args.putString("unit", modelList.get(position).getUnit());
                args.putString("Mrp", modelList.get(position).getMrp());
                args.putString("unit_value", modelList.get(position).getUnit_value());
                args.putString("Prod_description", modelList.get(position).getProduct_description());

                args.putString("pack1",modelList.get(position).getPack1());
                args.putString("pack2",modelList.get(position).getPack2());
                args.putString("mrp1",modelList.get(position).getMrp1());
                args.putString("mrp2",modelList.get(position).getMrp2());
                args.putString("price1",modelList.get(position).getPrice1());
                args.putString("price2",modelList.get(position).getPrice2());

                fm.setArguments(args);

                FragmentManager fragmentManager = ((AppCompatActivity)context).getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();


//                showProductDetail(modelList.get(position).getProduct_image(),
//                        modelList.get(position).getTitle(),
//                        modelList.get(position).getProduct_description(),
//                        modelList.get(position).getProduct_name(),
//                        position, tv_contetiy.getText().toString());
            }

        }
    }

    public Product_adapter(List<Product_model> modelList, Context context) {
        this.modelList = modelList;
        this.mFilteredList = modelList;

        dbcart = new DatabaseHandler(context);
        setHasStableIds(true);
    }

    @Override
    public Product_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_rv, parent, false);

        context = parent.getContext();

        return new Product_adapter.MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public void onBindViewHolder(final Product_adapter.MyViewHolder holder, int position) {

        Product_model mList = modelList.get(position);

        ArrayList<HashMap<String, String>> clist = dbcart.getCartAll();

        // for if product is already added.....

        /*for (int i=0 ; i< clist.size() ; i++) {

            HashMap<String, String> cart_list = clist.get(i);

            if (cart_list.get("product_id").equals(mList.getProduct_id())) {
              if (cart_list.get("offer").equals("0")) {
                  holder.ch1.setChecked(false);
                  holder.ch2.setChecked(false);
              }

              else if (cart_list.get("offer").equals("1")) {
                  holder.ch2.setChecked(false);
                  *//*holder.ch1.setEnabled(false);
                  holder.ch2.setEnabled(false);
                  holder.iv_plus.setEnabled(false);
                  holder.iv_minus.setEnabled(false);*//* holder.ch1.setChecked(true);

              }

              else if (cart_list.get("offer").equals("2")) {
                  holder.ch2.setChecked(true);
                  holder.ch1.setChecked(false);
                  *//*holder.ch1.setEnabled(false);
                  holder.ch2.setEnabled(false);
                  holder.iv_plus.setEnabled(false);
                  holder.iv_minus.setEnabled(false);*//*
              }
              break;
          }
        }*/
        /// eof product already added......

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.getProduct_image())
                .placeholder(R.drawable.shoplogo)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);

        holder.tv_title.setText(mList.getProduct_name());
        holder.mrpPrice.setText(mList.getMrp());
        holder.tv_price.setText(mList.getUnit_value() + " " +
                mList.getUnit() + " " + context.getResources().getString(R.string.currency) + " " + mList.getPrice());

        if (!mList.getPack1().isEmpty()) {
        holder.d1.setText(""+mList.getPack1()+" - ");}

        if (!mList.getPack2().isEmpty()){
        holder.d2.setText(""+mList.getPack2()+" - "); }

        holder.dd1.setText("Rs. "+mList.getMrp1());
        holder.dd2.setText("Rs. "+mList.getMrp2());

        holder.dtx1.setText("Rs. "+mList.getPrice1());
        holder.dtx2.setText("Rs. "+mList.getPrice2());


       holder.ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (holder.ch1.isChecked())
               {
                   holder.ch2.setChecked(false);
                   holder.iv_plus.setEnabled(false);
                   holder.iv_minus.setEnabled(false);
               }
               else
               {
                   holder.iv_plus.setEnabled(true);
                   holder.iv_minus.setEnabled(true);
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
                }
                else
                {
                    holder.iv_plus.setEnabled(true);
                    holder.iv_minus.setEnabled(true);
                }
            }
        });

        if (dbcart.isInCart(mList.getProduct_id())) {
            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            holder.tv_contetiy.setText(dbcart.getCartItemQty(mList.getProduct_id()));
        } else {
            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
        }



        Double items = Double.parseDouble(dbcart.getInCartItemQty(mList.getProduct_id()));
        Double price = Double.parseDouble(mList.getPrice());

        holder.tv_total.setText("" + price * items);

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = modelList;
                } else {

                    ArrayList<Product_model> filteredList = new ArrayList<>();

                    for (Product_model androidVersion : modelList) {

                        if (androidVersion.getProduct_name().toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Product_model>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }

    private void showImage(String image) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image_cancle = (ImageView) dialog.findViewById(R.id.iv_dialog_cancle);
        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_dialog_img);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + image)
                //.centerCrop()
                .placeholder(R.drawable.shoplogo)
                .crossFade()
                .into(iv_image);

        iv_image_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void showProductDetail(String image, String title, String description, String detail,
                                   final int position, String qty) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_product_detail);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_product_detail_img);
        ImageView iv_minus = (ImageView) dialog.findViewById(R.id.iv_subcat_minus);
        ImageView iv_plus = (ImageView) dialog.findViewById(R.id.iv_subcat_plus);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_product_detail_title);
        TextView tv_detail = (TextView) dialog.findViewById(R.id.tv_product_detail);
        final TextView tv_contetiy = (TextView) dialog.findViewById(R.id.tv_subcat_contetiy);
        final TextView tv_add = (TextView) dialog.findViewById(R.id.tv_subcat_add);

        tv_title.setText(title);
        tv_detail.setText(detail);
        tv_contetiy.setText(qty);
        tv_detail.setText(description);


        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + image)
                //.centerCrop()
                .placeholder(R.drawable.shoplogo)
                .crossFade()
                .into(iv_image);

        if (dbcart.isInCart(modelList.get(position).getProduct_id())) {
            tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            tv_contetiy.setText(dbcart.getCartItemQty(modelList.get(position).getProduct_id()));
        } else {
            tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
        }

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> map = new HashMap<>();

                map.put("product_id", modelList.get(position).getProduct_id());
                map.put("category_id", modelList.get(position).getCategory_id());
                map.put("product_image", modelList.get(position).getProduct_image());
                map.put("increament", modelList.get(position).getIncreament());
                map.put("product_name", modelList.get(position).getProduct_name());

                map.put("price", modelList.get(position).getPrice());
                map.put("stock", modelList.get(position).getIn_stock());
                map.put("title", modelList.get(position).getTitle());
                map.put("unit", modelList.get(position).getUnit());

                map.put("unit_value", modelList.get(position).getUnit_value());

                if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {

                    if (dbcart.isInCart(map.get("product_id"))) {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    } else {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    }
                } else {
                    dbcart.removeItemFromCart(map.get("product_id"));
                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
                }

                Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
                Double price = Double.parseDouble(map.get("price"));

                ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());

                notifyItemChanged(position);

            }
        });

        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.valueOf(tv_contetiy.getText().toString());
                qty = qty + 1;

                tv_contetiy.setText(String.valueOf(qty));
            }
        });

        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    tv_contetiy.setText(String.valueOf(qty));
                }
            }
        });

    }

}