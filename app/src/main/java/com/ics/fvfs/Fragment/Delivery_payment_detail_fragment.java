package com.ics.fvfs.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.R;
import com.ics.fvfs.AppPreference;
import com.ics.fvfs.ConnectivityReceiver;
import com.ics.fvfs.CustomVolleyJsonRequest;
import com.ics.fvfs.DatabaseHandler;
import com.ics.fvfs.Session_management;
import com.ics.fvfs.AppController;
import com.ics.fvfs.MainActivity;


public class Delivery_payment_detail_fragment extends Fragment {

    private static String TAG = Delivery_payment_detail_fragment.class.getSimpleName();

    private TextView tv_timeslot, tv_address, tv_item, tv_total,tv_pay_total;
    private Button btn_order;
    RadioButton radio_online_pay,radio_offline_pay;

    private String getlocation_id = "";
    private String gettime = "";
    private String getdate = "";
    private String getuser_id = "";
    private int deli_charges;
    String Payment_method;

    private DatabaseHandler db_cart;
    private Session_management sessionManagement;

    public Delivery_payment_detail_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.payment_detail));

        db_cart = new DatabaseHandler(getActivity());
        sessionManagement = new Session_management(getActivity());

        tv_timeslot = (TextView) view.findViewById(R.id.textTimeSlot);
        tv_address = (TextView) view.findViewById(R.id.txtAddress);
        //tv_item = (TextView) view.findViewById(R.id.textItems);
        //tv_total = (TextView) view.findViewById(R.id.textPrice);
        tv_total = (TextView) view.findViewById(R.id.txtTotal);
        tv_pay_total = (TextView) view.findViewById(R.id.tv_pay_total);

        radio_online_pay = (RadioButton) view.findViewById(R.id.radio_online_pay);
        radio_offline_pay = (RadioButton) view.findViewById(R.id.radio_offline_pay);

        btn_order = (Button) view.findViewById(R.id.buttonContinue);

        getdate = getArguments().getString("getdate");
        gettime = getArguments().getString("time");
        getlocation_id = getArguments().getString("location_id");
        deli_charges = Integer.parseInt(getArguments().getString("deli_charges"));
        String getaddress = getArguments().getString("address");

        tv_timeslot.setText(getdate + " " + gettime);
        tv_address.setText(getaddress);

        Double total = db_cart.gettTotalAmount() + deli_charges;

        //tv_total.setText("" + db_cart.getTotalAmount());
        //tv_item.setText("" + db_cart.getCartCount());
        tv_total.setText(getResources().getString(R.string.tv_cart_item) + db_cart.getCartCount() + "\n" +
                getResources().getString(R.string.amount) + db_cart.gettTotalAmount() + "\n" +
                getResources().getString(R.string.delivery_charge) + deli_charges + "\n" +
                getResources().getString(R.string.total_amount) +
                db_cart.gettTotalAmount() + " + " + deli_charges + " = " + total + " " + getResources().getString(R.string.currency));

        tv_pay_total.setText("Pay "+total + " " + getResources().getString(R.string.currency));

        //**********************************************************************
        radio_online_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio_offline_pay.setChecked(false);
                RadioButton rd_online = (RadioButton) view;
                btn_order.setText("Proceed To Pay");

            }
        });

        radio_offline_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio_online_pay.setChecked(false);
                RadioButton rd_offline = (RadioButton) view;
                btn_order.setText("Place Order");
            }
        });


        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if (radio_online_pay.isChecked() || radio_offline_pay.isChecked()){
                    if (radio_offline_pay.isChecked()){
                        Payment_method=radio_offline_pay.getText().toString();
                        if (ConnectivityReceiver.isConnected()) {
                            attemptOrder();
                        } else {
                            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                        }
                    }else {
                        Payment_method=radio_online_pay.getText().toString();
                        Toast.makeText(getActivity(), "Online Payment Success", Toast.LENGTH_SHORT).show();
                        if (ConnectivityReceiver.isConnected()) {
                            attemptOrder();
                        } else {
                            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                        }
                    }
                }else {
                    Toast.makeText(getActivity(), "Please Choose Payment Option", Toast.LENGTH_LONG).show();
                }

                /*Payment_method="cash on delivery";
                if (ConnectivityReceiver.isConnected()) {
                    attemptOrder();
                } else {
                    ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                }*/
            }
        });

        return view;
    }

    private void attemptOrder() {

        // retrive data from cart database
        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();
        if (items.size() > 0) {
            JSONArray passArray = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                HashMap<String, String> map = items.get(i);

                JSONObject jObjP = new JSONObject();

                try {
                    jObjP.put("product_id", map.get("product_id"));

                    jObjP.put("qty", map.get("qty"));
                    if (Integer.parseInt(map.get("offer"))==0)
                    {
                    jObjP.put("unit_value", map.get("unit_value"));
                    jObjP.put("unit", map.get("unit"));
                    jObjP.put("price", map.get("price"));
                    }

                    else if (Integer.parseInt(map.get("offer"))==1)
                    {
                        jObjP.put("unit_value", map.get("pack1"));
                        jObjP.put("unit", 1);
                        jObjP.put("price", map.get("price1"));
                    }

                    else if (Integer.parseInt(map.get("offer"))==2)
                    {
                        jObjP.put("unit_value", map.get("pack2"));
                        jObjP.put("unit", 1);
                        jObjP.put("price", map.get("price2"));
                    }

                    passArray.put(jObjP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.e("ADD ORDER..."," _______________________________________________________________________________");
            Log.e("_____________",""+passArray);
            Log.e("ADD ORDER..."," _______________________________________________________________________________");
           // getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
            getuser_id = AppPreference.getUser_Id(getActivity());

            if (ConnectivityReceiver.isConnected()) {

                Log.e(TAG, "  Delivery Charges "+deli_charges+"   " +"from:" + gettime + "\ndate:" + getdate +
                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id + "\ndata:" + passArray.toString());

                makeAddOrderRequest(getdate, gettime, getuser_id, getlocation_id, passArray,Payment_method);
            }
        }
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeAddOrderRequest(String date, String gettime, String userid,
                                     String location, JSONArray passArray, String payment_method) {

        // Tag used to cancel the request
        String tag_json_obj = "json_add_order_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("date", date);
        params.put("time", gettime);
        params.put("user_id", userid);
        params.put("location", location);
        params.put("data", passArray.toString());
        params.put("payment_mode",payment_method );
        params.put("delivery_charge", ""+deli_charges);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ORDER, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                //Toast.makeText(getActivity(), "   "+response, Toast.LENGTH_LONG).show();
                Log.e("payOrder", response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("data");

                        db_cart.clearCart();
                        ((MainActivity) getActivity()).setCartCounter("" + db_cart.getCartCount());

                        Bundle args = new Bundle();
                        Fragment fm = new Thanks_fragment();
                        args.putString("msg", msg);
                        fm.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
