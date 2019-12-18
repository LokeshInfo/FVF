package com.ics.fvfs.fcm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import static com.android.volley.VolleyLog.TAG;


public class MyFirebaseRegister {

    Activity _context;
    public SharedPreferences settings;
//    ConnectivityReceiver cd;

    public MyFirebaseRegister(Activity context) {
        this._context = context;
      //  settings = context.getSharedPreferences(BaseURL.PREFS_NAME, 0);
//        cd = new ConnectivityReceiver();

    }

    public void RegisterUser(String user_id) {
        // [START subscribe_topics]
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token: " + token);

//        FirebaseMessaging.getInstance().subscribeToTopic("grocery");
//        String token = FirebaseInstanceId.getInstance().getToken();
//         [END subscribe_topics]
//          mLogTask = new fcmRegisterTask();
//           mLogTask.execute(user_id,token);
//        checkLogin(user_id, token);
    }


//    private void checkLogin(String user_id, String token) {
//        // Tag used to cancel the request
//        String tag_json_obj = "json_obj_login_req";
//
//        //showpDialog();
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("user_id", user_id);
//        params.put("token", token);
//        params.put("device", "android");
//
//        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
//                BaseURL.RIGISTER_FCM, params, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//
//
//                try {
//                    Boolean status = response.getBoolean("responce");
//                    if (status) {
//
//                        JSONObject obj = new JSONObject();
//
//
//                        //onBackPressed();
//
//
//                    } else {
//                        String error = response.getString("error");
//                        Toast.makeText(_context, "" + error, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                //hidepDialog();
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Toast.makeText(_context,
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
//                //hidepDialog();
//            }
//        }
//        );
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//    }


}
