package com.ics.fvfs.Adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import com.ics.fvfs.Fragment.Delivery_Details;
import com.ics.fvfs.Fragment.My_order_detail_fragment;
import com.ics.fvfs.Model.My_order_model;
import com.ics.fvfs.R;

public class My_order_adapter extends RecyclerView.Adapter<My_order_adapter.MyViewHolder> {

    private List<My_order_model> my_order_modelList;


    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_orderno, tv_status, tv_date, tv_time, tv_price, tv_item;
        public Button tv_delivery;

        public MyViewHolder(View view) {
            super(view);
            tv_orderno = (TextView) view.findViewById(R.id.tv_order_no);
            tv_status = (TextView) view.findViewById(R.id.tv_order_status);
            tv_date = (TextView) view.findViewById(R.id.tv_order_date);
            tv_time = (TextView) view.findViewById(R.id.tv_order_time);
            tv_price = (TextView) view.findViewById(R.id.tv_order_price);
            tv_item = (TextView) view.findViewById(R.id.tv_order_item);
            tv_delivery = (Button) view.findViewById(R.id.Delivery_details_btn);
        }
    }

    public My_order_adapter(List<My_order_model> modelList) {
        this.my_order_modelList = modelList;
    }

    @Override
    public My_order_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_order_rv, parent, false);

        context = parent.getContext();

        return new My_order_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(My_order_adapter.MyViewHolder holder, final int position) {
        final My_order_model mList = my_order_modelList.get(position);

        holder.tv_orderno.setText(context.getResources().getString(R.string.order_no) + mList.getSale_id());

        if(mList.getStatus().equals("0")) {
            holder.tv_status.setText(context.getResources().getString(R.string.pending));
        }else if(mList.getStatus().equals("1")){
            holder.tv_status.setText(context.getResources().getString(R.string.confirm));
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.color_1));
        }else if(mList.getStatus().equals("2")){
            holder.tv_status.setText(context.getResources().getString(R.string.delivered));
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.color_2));
        }else if(mList.getStatus().equals("3")){
            holder.tv_status.setText(context.getResources().getString(R.string.cancle));
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.color_3));
        }

        holder.tv_date.setText(context.getResources().getString(R.string.date) + mList.getOn_date());
        holder.tv_time.setText(context.getResources().getString(R.string.time) + mList.getDelivery_time_from() + "-" + mList.getDelivery_time_to());
        holder.tv_price.setText(context.getResources().getString(R.string.currency) + mList.getTotal_amount());
        holder.tv_item.setText(context.getResources().getString(R.string.tv_cart_item) + mList.getTotal_items());

        holder.tv_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fm = new Delivery_Details();
                FragmentManager fragmentManager = activity.getFragmentManager();
                Bundle bn = new Bundle();

                bn.putString("OrderId",mList.getSale_id());
                fm.setArguments(bn);
                fragmentManager.beginTransaction()
                        .replace(R.id.contentPanel, fm, "Home_fragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, "ITEM CLICKED", Toast.LENGTH_SHORT).show();
                String sale_id = my_order_modelList.get(position).getSale_id();
                String date = my_order_modelList.get(position).getOn_date();
                String time = my_order_modelList.get(position).getDelivery_time_from() + "-" +
                        my_order_modelList.get(position).getDelivery_time_to();
                String total = my_order_modelList.get(position).getTotal_amount();
                String status = my_order_modelList.get(position).getStatus();
                String deli_charge = my_order_modelList.get(position).getDelivery_charge();

                Bundle args = new Bundle();
                Fragment fm = new My_order_detail_fragment();
                args.putString("sale_id", sale_id);
                args.putString("date", date);
                args.putString("time", time);
                args.putString("total", total);
                args.putString("status", status);
                args.putString("deli_charge", deli_charge);
                fm.setArguments(args);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return my_order_modelList.size();
    }

}
