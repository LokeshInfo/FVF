package com.ics.fvfs.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.ics.fvfs.Model.My_Locatio_model;

import com.ics.fvfs.R;

public class My_office_adapter extends RecyclerView.Adapter<My_office_adapter.MyViewHolder> {

    private List<My_Locatio_model> modelList;

    private Context context;

    public My_office_adapter(Activity activity, List<My_Locatio_model> my_order_modelList) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_orderno, tv_status, tv_office_add, tv_time, tv_price, tv_item;

        public MyViewHolder(View view) {
            super(view);
            tv_office_add = (TextView) view.findViewById(R.id.tv_office_add);

        }
    }

    public My_office_adapter(List<My_Locatio_model> my_order_modelList) {
        this.modelList = my_order_modelList;
    }

    @Override
    public My_office_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_location, parent, false);

        context = parent.getContext();

        return new My_office_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(My_office_adapter.MyViewHolder holder, int position) {
        My_Locatio_model my_locatio_model = modelList.get(position);
        holder.tv_office_add.setText(my_locatio_model.getPg_descri());
        /*My_Locatio_model mList = modelList.get(position);

        holder.tv_office_add.setText(mList.getPg_descri());*/

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}
