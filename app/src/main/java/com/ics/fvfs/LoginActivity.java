package com.ics.fvfs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = LoginActivity.class.getSimpleName();

    private Button btn_continue, btn_register;
    private EditText et_password, et_email;
    private TextView tv_password, tv_email,btn_forgot;

    String Email,Password;
    String sever_url;
    Session_management sessionManagement;
     String user_fullname= "";
    String user_phone="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        sessionManagement = new Session_management(LoginActivity.this);

        et_password = (EditText) findViewById(R.id.et_login_pass);
        et_email = (EditText) findViewById(R.id.et_login_email);
        tv_password = (TextView) findViewById(R.id.tv_login_password);
        tv_email = (TextView) findViewById(R.id.tv_login_email);
        btn_continue = (Button) findViewById(R.id.btnContinue);
        btn_register = (Button) findViewById(R.id.btnRegister);
        btn_forgot = (TextView) findViewById(R.id.btnForgot);

        btn_continue.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_forgot.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnContinue) {

            Email = et_email.getText().toString().trim();
            Password = et_password.getText().toString().trim();

            attemptLogin();

            new SendJsonDataToServer().execute();


        } else if (id == R.id.btnRegister) {
            Intent startRegister = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(startRegister);
        }else if (id == R.id.btnForgot) {
            Intent startRegister = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(startRegister);
        }
    }

    private void attemptLogin() {

        tv_email.setText(getResources().getString(R.string.tv_login_email));
        tv_password.setText(getResources().getString(R.string.tv_login_password));

        tv_password.setTextColor(getResources().getColor(R.color.color_3));
        tv_email.setTextColor(getResources().getColor(R.color.color_3));

       // String getpassword = et_password.getText().toString();
       // String getemail = et_email.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(Password)) {
            tv_password.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_password;
            cancel = true;
        } else if (!isPasswordValid(Password)) {
            tv_password.setText(getResources().getString(R.string.password_too_short));
            tv_password.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(Email)) {
            tv_email.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_email;
            cancel = true;
        } else if (!isEmailValid(Email)) {
            tv_email.setText(getResources().getString(R.string.invalide_email_address));
            tv_email.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            if (ConnectivityReceiver.isConnected()) {

               // new GetLoginAsynctask().execute();
               // makeLoginRequest(getemail, getpassword);
            }
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Method to make json object request where json response starts wtih
     */


    //*************************************************************


    class SendJsonDataToServer extends AsyncTask<String, String, String> {

        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.show();

        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(BaseURL.BASEURL2+"/index.php/Api/login");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("user_email", Email);
                postDataParams.put("password", Password);


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

                    /*BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        StringBuffer Ss = sb.append(line);
                        Log.e("Ss", Ss.toString());
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString(); */

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

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                dialog.dismiss();

                // JSONObject jsonObject = null;
                Log.e("SendJsonDataToServer>>>", result.toString());
                try {
                    // result=getJSONUrl(URL);  //<< get json string from server
                    JSONObject jsonObject = new JSONObject(result);
                    //jsonObject = new JSONObject(result);
                    String response = jsonObject.getString("responce");
                   // String message = jsonObject.getString("message");
                    JSONObject message = jsonObject.getJSONObject("data");
                    String user_id = message.getString("user_id");
                    user_fullname = message.getString("user_fullname");
                    String user_email =message.getString("user_email");
                   user_phone = message.getString("user_phone");
                    String user_image = message.getString("user_image");

                    Log.e(">>>>", jsonObject.toString() + " " + response + " " + "");

                    if (response.equalsIgnoreCase("true")) {
                        AppPreference.setName(LoginActivity.this,user_fullname);
                        AppPreference.setMobile(LoginActivity.this,user_phone);
                        AppPreference.setUser_Id(LoginActivity.this,user_id);
                        sessionManagement.createLoginSession(user_phone);
                        Log.e("user_fullname",user_fullname);
                        Log.e("user_phone",user_phone);
                        et_email.setText("");
                        et_password.setText("");
                        new SEND_TOKEN().execute();


//                          sessionManagement.createLoginSession(user_id,user_fullname,user_email,Password);


                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
    }

    //************************************************************

    private class SEND_TOKEN extends AsyncTask<String, String, String> {

        ProgressDialog dialog;

        protected void onPreExecute() {
//            dialog = new ProgressDialog(MainActivity_drawer.this);
//            dialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(BaseURL.BASEURL2+"/Api/test_fcm");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("user_id", AppPreference.getUser_Id(LoginActivity.this));
                postDataParams.put("token",AppPreference.getUserToken(LoginActivity.this));

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

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
//                dialog.dismiss();

                // JSONObject jsonObject = null;
                Log.e("SendJsonDataToServer>>>", result.toString());
                try {
                    JSONObject jsonObject = new JSONObject(result);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
    }

  /*  private void makeLoginRequest(String email, final String password) {

        // Tag used to cancel the request
        String tag_json_obj = "json_login_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_email", email);
        params.put("password", password);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.LOGIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        JSONObject obj = response.getJSONObject("data");
                        String user_id = obj.getString("user_id");
                        String user_fullname = obj.getString("user_fullname");
                        String user_email = obj.getString("user_email");
                        String user_phone = obj.getString("user_phone");
                        String user_image = obj.getString("user_image");

                        Session_management sessionManagement = new Session_management(LoginActivity.this);
                        sessionManagement.createLoginSession(user_id,user_email,user_fullname,user_phone,user_image,"","","","",password);

                        Intent i = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }*/

}
