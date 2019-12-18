package com.ics.fvfs.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.ics.fvfs.Adapter.LocationAdapter;
import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.Model.My_Locatio_model;

import com.ics.fvfs.R;
import com.ics.fvfs.ConnectivityReceiver;
import com.ics.fvfs.HttpHandler;


public class LocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static String TAG = My_order_fragment.class.getSimpleName();

    private RecyclerView rv_myorder;
    private LocationAdapter locationAdapter;
    private ArrayList<My_Locatio_model> my_order_modelList;
   // private OnFragmentInteractionListener mListener;

    public LocationFragment() {
        // Required empty public constructor
    }


    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location, container, false);
        rv_myorder = (RecyclerView) view.findViewById(R.id.rv_myorder);

        my_order_modelList = new ArrayList<>();

        if (ConnectivityReceiver.isConnected()) {
            new GetLocation().execute();
        }else {
            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    //----------------------------------------------------------------------

    class GetLocation extends AsyncTask<String, String, String> {
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
        protected String doInBackground(String... params) {

            //String sever_url = "https://ihisaab.in/MyTukari/index.php/api/location";
            String sever_url = BaseURL.BASEURL2+"/index.php/api/location";

            Log.e("sever_url>>>>>>>>>", sever_url);
            output = HttpHandler.makeServiceCall(sever_url);
            System.out.println("getcomment_url" + output);
            return output;
        }

        @Override
        protected void onPostExecute(String output) {
            if (output == null) {
                dialog.dismiss();
            } else {
                try {
                    dialog.dismiss();

                  JSONObject obj = new JSONObject(output);
                  String responce = obj.getString("responce");
                  JSONObject dataObj = obj.getJSONObject("data");
                    String id = dataObj.getString("id");
                    String pg_title = dataObj.getString("pg_title");
                    String pg_slug = dataObj.getString("pg_slug");
                    String pg_descri = dataObj.getString("pg_descri");
                    String pg_status = dataObj.getString("pg_status");
                    String pg_foot = dataObj.getString("pg_foot");
                    String crated_date = dataObj.getString("crated_date");

                    String newString = pg_descri.replace("<p><strong>", "");
                    String newString2 = newString.replace("</strong></p>", "");
                    String newString3 = newString2.replace("<p>", "");
                    String newString4 = newString3.replace("</p>", "");

                    my_order_modelList.add(new My_Locatio_model(newString4,pg_title));

                    locationAdapter = new LocationAdapter(getActivity(),my_order_modelList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rv_myorder.setLayoutManager(mLayoutManager);
                    rv_myorder.setItemAnimator(new DefaultItemAnimator());
                    rv_myorder.setAdapter(locationAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }

                super.onPostExecute(output);
            }
        }
    }


    //..........................................................................

 /*   class GetLocation extends AsyncTask<String, String, String> {
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
        protected String doInBackground(String... params) {

            String sever_url1 = BaseURL.GET_LOCATION;

            Log.e("sever_url>>>>>>>>>", sever_url1);
            output = HttpHandler.makeServiceCall(sever_url1);
            System.out.println("getcomment_url" + output);
            return output;
        }

        @Override
        protected void onPostExecute(String output) {
            if (output == null) {
                dialog.dismiss();
            } else {
                try {
                    dialog.dismiss();
                    JSONArray json = null;

                    JSONObject jsonObject1 = new JSONObject(output);
                    String responce = jsonObject1.getString("responce");
                    JSONObject data = jsonObject1.getJSONObject("data");
                    String id = data.getString("id");
                    String location = data.getString("location");
                    String pg_slug = data.getString("pg_slug");
                    String pg_descri = data.getString("pg_descri");
                    String pg_status = data.getString("pg_status");
                    String pg_foot = data.getString("pg_foot");
                    String crated_date = data.getString("crated_date");

                    my_order_modelList.add(new My_Locatio_model(pg_descri));

                    adapter = new My_office_adapter(my_order_modelList);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_myorder.setLayoutManager(linearLayoutManager);
                    rv_myorder.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


   /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
