package com.ics.fvfs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.R;

public class  NewLogin extends AppCompatActivity {
    EditText edMobile;
    String EdMobile;
    Button buttonContinue;
    String data = "";
    String mobile = "";
    Session_management sessionManagement;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        sessionManagement = new Session_management(NewLogin.this);


        edMobile = (EditText)findViewById(R.id.edMobile);
        buttonContinue = (Button) findViewById(R.id.buttonContinue);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdMobile = edMobile.getText().toString();

                if (ConnectivityReceiver.isConnected()){
                    new CompleteReg().execute();

                }else {
                    Toast.makeText(NewLogin.this, "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //-------------------------------------------------------

    private class CompleteReg extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(NewLogin.this);
            dialog.show();

        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(BaseURL.BASEURL2+"/index.php/api/userlogin");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("mobile", EdMobile);

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
                    JSONObject jsonObject = new JSONObject(result);
                    String responce = jsonObject.getString("responce");
                    data = jsonObject.getString("data");
                   String mobile = jsonObject.getString("mobile");
                    if (responce.equals("true")) {
                        //AppPreference.setOtp(NewLogin.this,data);
                    //    AppPreference.setMobile(NewLogin.this,mobile);
                       // sessionManagement.createLoginSession(mobile);

                        Intent to_completion = new Intent(NewLogin.this, NewRegistation.class);
                        to_completion.putExtra("OTP",data);
                        to_completion.putExtra("mobile",mobile);
                        startActivity(to_completion);
                        finish();
                        Toast.makeText(NewLogin.this, "Otp Sent Successful", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(NewLogin.this, "Could not register the user", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            super.onPostExecute(result);
        }

    }

}
