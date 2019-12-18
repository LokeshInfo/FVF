package com.ics.fvfs.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.ics.fvfs.Adapter.Cart_adapter;
import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.R;
import com.ics.fvfs.ConnectivityReceiver;
import com.ics.fvfs.DatabaseHandler;
import com.ics.fvfs.HttpHandler;
import com.ics.fvfs.Session_management;
import com.ics.fvfs.AppController;
import com.ics.fvfs.LoginActivity;
import com.ics.fvfs.MainActivity;


public class Cart_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Cart_fragment.class.getSimpleName();

    private RecyclerView rv_cart;
    private TextView tv_clear, tv_total, tv_item,tv_pay_total;
    private Button btn_checkout;

    private DatabaseHandler db;
    private String deli_charges,final_amount;

    private Session_management sessionManagement;

    public Cart_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.tv_cart_title));

        sessionManagement = new Session_management(getActivity());
        sessionManagement.cleardatetime();

        tv_clear = (TextView) view.findViewById(R.id.tv_cart_clear);
        tv_total = (TextView) view.findViewById(R.id.tv_cart_total);
        tv_item = (TextView) view.findViewById(R.id.tv_cart_item);
        btn_checkout = (Button) view.findViewById(R.id.btn_cart_checkout);
        rv_cart = (RecyclerView) view.findViewById(R.id.rv_cart);
        tv_pay_total = (TextView) view.findViewById(R.id.tv_pay_total);
        rv_cart.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = new DatabaseHandler(getActivity());

        ArrayList<HashMap<String, String>> map = db.getCartAll();

        Cart_adapter adapter = new Cart_adapter(getActivity(), map);
        rv_cart.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        updateData();
        new Get_DCharge().execute();

        tv_clear.setOnClickListener(this);
        btn_checkout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.tv_cart_clear) {
            // showdialog
            showClearDialog();
        } else if (id == R.id.btn_cart_checkout) {

            if (ConnectivityReceiver.isConnected()) {
                makeGetLimiteRequest();
            } else {
                ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
            }

        }
    }

    // update UI
    private void updateData() {

        //tv_total.setText("Rs. " + db.getTotalAmount());
        //tv_pay_total.setText("Rs. " + db.getTotalAmount());

        tv_item.setText("" + db.getCartCount());
        ((MainActivity) getActivity()).setCartCounter("" + db.getCartCount());

        Double tt = db.gettTotalAmount();

        tv_total.setText("Rs. " + db.gettTotalAmount());
        tv_pay_total.setText("Rs. " + db.gettTotalAmount());

        Log.e("TTTOOTTAALL >>> ","  >>>>>>>>>>> "+tt);

    }

    private void showClearDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Are you sure!\nyou want to delete all cart product");
        alertDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // clear cart data
                db.clearCart();
                ArrayList<HashMap<String, String>> map = db.getCartAll();

                Cart_adapter adapter = new Cart_adapter(getActivity(), map);
                rv_cart.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                updateData();

                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * Method to make json array request where json response starts wtih
     */
    private void makeGetLimiteRequest() {

        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_LIMITE_SETTING_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        Double total_amount = db.gettTotalAmount();

                        try {
                            // Parsing json array response
                            // loop through each json object

                            boolean issmall = false;
                            boolean isbig = false;

                            // arraylist list variable for store data;
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = (JSONObject) response
                                        .get(i);
                                int value;

                                if (jsonObject.getString("id").equals("1")) {
                                    value = Integer.parseInt(jsonObject.getString("value"));

                                    if (total_amount < value) {
                                        issmall = true;
                                        Toast.makeText(getActivity(), "" + jsonObject.getString("title") + " : " + value, Toast.LENGTH_SHORT).show();
                                    }
                                } else if (jsonObject.getString("id").equals("2")) {
                                    value = Integer.parseInt(jsonObject.getString("value"));

                                    if (total_amount > value) {
                                        isbig = true;

                                        Toast.makeText(getActivity(), "" + jsonObject.getString("title") + " : " + value, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            if (!issmall && !isbig) {
                                if(sessionManagement.isLoggedIn()) {
                                    Bundle args = new Bundle();
                                    Fragment fm = new Delivery_fragment();
                                    args.putString("famount",final_amount);
                                    args.putString("deliCharges",deli_charges);
                                    fm.setArguments(args);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                            .addToBackStack(null).commit();
                                }else {
                                    //Toast.makeText(getActivity(), "Please login or regiter.\n continue", Toast.LENGTH_SHORT).show();

                                   /* Intent i = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(i);*/

                                    //Intent i = new Intent(getActivity(), ContinueActivity.class);
                                    Intent i = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(i);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Connection Time out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
    }

    @Override
    public void onResume() {
        super.onResume();
        // register reciver
      //  getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
        getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
    }

    // broadcast reciver for receive data
    private BroadcastReceiver mCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("update")) {
                updateData();
            }
        }
    };

    class Get_DCharge extends AsyncTask<String, String, String>
    {
        String url;
        String output = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Processing");
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = BaseURL.BASEURL2+"/index.php/Api/delivery_charge";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("sever_url>>>>>>>>>", url);
            output = HttpHandler.makeServiceCall(url);
            //   Log.e("getcomment_url", output);
            System.out.println("getcomment_url" + output);
            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            if (output == null) {
                dialog.dismiss();
            } else {
                try {
                    dialog.dismiss();
                    JSONObject obj = new JSONObject(output);
                    String responce = obj.getString("responce");
                    JSONObject ob = obj.getJSONObject("data");
                    for (int i = 0; i < ob.length(); i++) {

                        String id = ob.getString("id");
                        final_amount = ob.getString("amount");
                        deli_charges = ob.getString("delivery_charge");
                        String status = ob.getString("status");

                        Log.e("OFFER FOR MAX",""+id+final_amount+deli_charges+status);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            super.onPostExecute(s);
        }
    }

}
