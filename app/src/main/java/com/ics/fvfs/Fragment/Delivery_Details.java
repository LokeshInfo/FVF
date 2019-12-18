package com.ics.fvfs.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.MainActivity;
import com.ics.fvfs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class Delivery_Details extends Fragment {

    private static String TAG = Cart_fragment.class.getSimpleName();
    private TextView name,number,time;
    ImageView img_profile;
    String orderid;

    public Delivery_Details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delivery_details, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.tv_delivery_details));

        name = view.findViewById(R.id.D_Name);
        number = view.findViewById(R.id.D_num);
        img_profile = view.findViewById(R.id.Img_profile);

        orderid = getArguments().getString("OrderId");
        Log.e("ORDER ID IS : ",""+orderid);

        new GetCat().execute();

        return view;
    }

////--------------------------------------------

    private class GetCat extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.show();

        }


        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL(BaseURL.BASEURL2+"/index.php/Api/get_assign_user");
//https://jntrcpl.com/MyTukari/api/
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("order_id", orderid);

                Log.e("postDataParams", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds*/);
                conn.setConnectTimeout(15000  /*milliseconds*/);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        result.append(line);
                    }
                    r.close();
                    return result.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }


        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                dialog.dismiss();


                Log.e("PostRegistration", result.toString());

                try {
                    JSONObject obj = new JSONObject(result);
                    String responce = obj.getString("responce");
                    JSONArray Data_array = obj.getJSONArray("data");
                    if (Data_array.length()==0)
                    {
                        name.setText("No Delivery Person Assigned Yet");
                        number.setText("");
                    }
                    for (int i = 0; i < Data_array.length(); i++) {

                        JSONObject c = Data_array.getJSONObject(i);
                        String cname = c.getString("name");
                        String phone = c.getString("user_phone");
                        // String crate = c.getString("cat_rate");
                        String image = c.getString("image");

                        name.setText("Name : "+cname);
                        number.setText("Number : "+phone);

                        Glide.with(getActivity())
                                .load(BaseURL.IMG_DELIVERY_URL + image)
                                //  .centerCrop()
                                .placeholder(R.drawable.shoplogo)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .into(img_profile);

                        Log.e(">>>>>>>",""+cname+phone+image);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            super.onPostExecute(result);
        }

    }

}