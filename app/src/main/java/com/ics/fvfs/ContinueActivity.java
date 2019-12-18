package com.ics.fvfs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;

import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.R;

public class ContinueActivity extends AppCompatActivity {

    EditText et_username, et_mobile_no;
    Button btnContinue,btnReg;
    String getphone, getname;
    Session_management sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_continue);

        et_username = (EditText) findViewById(R.id.et_username);
        et_mobile_no = (EditText) findViewById(R.id.et_mobile_no);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnReg = (Button) findViewById(R.id.btnReg);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getphone = et_mobile_no.getText().toString();
                getname = et_username.getText().toString();

                new SendJsonDataToServer().execute();

            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(ContinueActivity.this, NewRegistation.class);
             startActivity(intent);
             finish();
            }
        });
    }

    class SendJsonDataToServer extends AsyncTask<String, String, String> {

        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(ContinueActivity.this);
            dialog.show();

        }

        protected String doInBackground(String... arg0) {

                String urlParameters = "";
                try {

                    urlParameters = "&user_name=" + URLEncoder.encode(getname, "UTF-8") +
                            "&user_mobile=" + URLEncoder.encode(getphone, "UTF-8");


                    Log.e("urlParameters", urlParameters);


                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
                String result = Utilities.postParamsAndfindJSON(BaseURL.BASEURL2+"/index.php/Api/signup", urlParameters);
                return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                dialog.dismiss();

                JSONObject jsonObject = null;
                Log.e("SendJsonDataToServer>>>", result.toString());
                try {
                    jsonObject = new JSONObject(result);
                    String responc = jsonObject.getString("responce");
                    if (!responc.equals("true")) {
                        String message = jsonObject.getString("message");
                        Toast.makeText(ContinueActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    } else {
                        String responce = jsonObject.getString("responce");
                        String message = jsonObject.getString("message");
                        JSONObject data = jsonObject.getJSONObject("data");
                        String user_phone = data.getString("user_phone");
                        String user_fullname = data.getString("user_fullname");
                        String user_id = data.getString("user_id");

                        Log.e(">>>>", jsonObject.toString() + " " + responce + " " + message);

                        if (responce.equals("true")) {

                            Session_management.setUsername(ContinueActivity.this, user_fullname);
                            Session_management.setMobileNo(ContinueActivity.this, user_phone);

                            sessionManagement = new Session_management(ContinueActivity.this);
                            sessionManagement.createGuestLogin(user_id, user_fullname, user_phone);

                            Intent i = new Intent(ContinueActivity.this, MainActivity.class);
                            startActivity(i);
                            et_username.setText("");
                            et_mobile_no.setText("");

                        }
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
}
