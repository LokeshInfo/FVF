package com.ics.fvfs.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.ics.fvfs.Model.My_Locatio_model;
import com.ics.fvfs.R;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private static final String TAG = "CustomerAdapter";
    private ArrayList<My_Locatio_model> locList;
    public Context context;
    String resId = "";
    String code = "";
    String requestId;
    String compCode;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_order_no,tv_office_add;
        CardView cardeview;
        int pos;

        public ViewHolder(View view) {
            super(view);

            tv_office_add = (TextView) view.findViewById(R.id.tv_office_add);
            tv_order_no = (TextView) view.findViewById(R.id.tv_order_no);
            cardeview = (CardView) view.findViewById(R.id.card_view);
        }
    }

    public static Context mContext;

    public LocationAdapter(Context mContext, ArrayList<My_Locatio_model> my_order_modelList) {
        context = mContext;
        locList = my_order_modelList;

    }

    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_location, parent, false);

        return new LocationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LocationAdapter.ViewHolder viewHolder, final int position) {
        My_Locatio_model my_locatio_model = locList.get(position);
        viewHolder.tv_office_add.setText(my_locatio_model.getPg_descri());
        viewHolder.tv_order_no.setText(my_locatio_model.getPg_title());
        viewHolder.cardeview.setTag(viewHolder);
        viewHolder.pos = position;


    }

    @Override
    public int getItemCount() {
        return locList.size();
    }

}
