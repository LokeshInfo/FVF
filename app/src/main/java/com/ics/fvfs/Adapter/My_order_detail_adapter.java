package com.ics.fvfs.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.Model.My_order_detail_model;
import com.ics.fvfs.R;


public class My_order_detail_adapter extends RecyclerView.Adapter<My_order_detail_adapter.MyViewHolder> {

    private List<My_order_detail_model> modelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_price, tv_qty, tv_unitval;
        public ImageView iv_img;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_order_Detail_title);
            tv_price = (TextView) view.findViewById(R.id.tv_order_Detail_price);
            tv_qty = (TextView) view.findViewById(R.id.tv_order_Detail_qty);
            iv_img = (ImageView) view.findViewById(R.id.iv_order_detail_img);
            tv_unitval = (TextView) view.findViewById(R.id.tx_unitvalue);
        }
    }

    public My_order_detail_adapter(List<My_order_detail_model> modelList) {
        this.modelList = modelList;
    }

    @Override
    public My_order_detail_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_order_detail_rv, parent, false);

        context = parent.getContext();

        return new My_order_detail_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(My_order_detail_adapter.MyViewHolder holder, int position) {
        My_order_detail_model mList = modelList.get(position);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.getProduct_image())
               // .centerCrop()
                .placeholder(R.drawable.shoplogo)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_img);

        holder.tv_title.setText(mList.getProduct_name());
        holder.tv_price.setText(mList.getPrice());
        holder.tv_qty.setText(mList.getQty());
        holder.tv_unitval.setText(mList.getUnit_value());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}