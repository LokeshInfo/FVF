package com.ics.fvfs.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ics.fvfs.Adapter.My_order_adapter;
import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.Model.My_order_model;
import com.ics.fvfs.AppController;
import com.ics.fvfs.MainActivity;
import com.ics.fvfs.R;
import com.ics.fvfs.AppPreference;
import com.ics.fvfs.ConnectivityReceiver;
import com.ics.fvfs.CustomVolleyJsonArrayRequest;
import com.ics.fvfs.Session_management;


public class My_order_fragment extends Fragment {

    private static String TAG = My_order_fragment.class.getSimpleName();

    private RecyclerView rv_myorder;

    private List<My_order_model> my_order_modelList = new ArrayList<>();

    public My_order_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.my_order));

        // handle the touch event if true
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // check user can press back button or not
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    Fragment fm = new Home_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });

        rv_myorder = (RecyclerView) view.findViewById(R.id.rv_myorder);
        rv_myorder.setLayoutManager(new LinearLayoutManager(getActivity()));

        Session_management sessionManagement = new Session_management(getActivity());
       // String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        String user_id = AppPreference.getUser_Id(getActivity());

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetOrderRequest(user_id);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        // recyclerview item click listener
       /* rv_myorder.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                rv_myorder, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                int vid = view.getId();
                int idd =R.id.Delivery_details_btn;
                if (vid == R.id.Delivery_details_btn)
                {
                    Fragment fm = new Delivery_Details();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.contentPanel, fm, "Home_fragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }

                else {
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
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));*/

        return view;
    }


    private void makeGetOrderRequest(String userid) {

        // Tag used to cancel the request
        String tag_json_obj = "json_socity_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.GET_ORDER, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                Log.e("my_order", response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_model>>() {
                }.getType();

                my_order_modelList = gson.fromJson(response.toString(), listType);

                My_order_adapter adapter = new My_order_adapter(my_order_modelList);
                rv_myorder.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if(my_order_modelList.isEmpty()){
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
}
