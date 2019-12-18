package com.ics.fvfs.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.R;
import com.ics.fvfs.AppPreference;
import com.ics.fvfs.ConnectivityReceiver;
import com.ics.fvfs.Session_management;
import com.ics.fvfs.AppController;
import com.ics.fvfs.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_fragment extends Fragment
{
    private static String TAG = Profile_fragment.class.getSimpleName();

    private EditText et_phone, et_name, et_email;
    private ImageView btn_update;
    private TextView tv_phone, tv_name, tv_email,lginfrst ;
    private CircleImageView iv_profile;
    //private Spinner sp_socity;

    private String getsocity = "";
    private String filePath = "";
    private static final int GALLERY_REQUEST_CODE1 = 201;
    private Bitmap bitmap;
    private LinearLayout layout;
    private Uri imageuri;

    private Session_management sessionManagement;

    public Profile_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setTitle("Profile");

        sessionManagement = new Session_management(getActivity());

        et_phone = (EditText) view.findViewById(R.id.et_pro_phone);
        et_name = (EditText) view.findViewById(R.id.et_pro_name);
        et_email = (EditText) view.findViewById(R.id.et_pro_email);
        iv_profile = (CircleImageView) view.findViewById(R.id.iv_pro_img);
        layout = (LinearLayout) view.findViewById(R.id.layout);
        lginfrst = (TextView) view.findViewById(R.id.loginfrst);

        tv_phone = (TextView) view.findViewById(R.id.tv_pro_phone);
        tv_name = (TextView) view.findViewById(R.id.tv_pro_name);
        tv_email = (TextView) view.findViewById(R.id.tv_pro_email);

        btn_update = (ImageView) view.findViewById(R.id.btn_pro_edit);

        String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
        getsocity = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);

        if (sessionManagement.isLoggedIn()) {
            if (ConnectivityReceiver.isConnected()) {
                GetProfileRequest();
            }
        }
        else
        {
            lginfrst.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(getimage)) {

            Glide.with(this)
                    .load(BaseURL.IMG_PROFILE_URL + getimage)
                    .placeholder(R.drawable.shoplogo)
                    .crossFade()
                    .into(iv_profile);
        }

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fm = new Edit_profile_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        });

        return  view;
    }

    private void GetProfileRequest() {
        // Toast.makeText(getActivity(), "userid "+AppPreference.getUser_Id(getActivity()), Toast.LENGTH_SHORT).show();
        Log.e("UserId",""+ AppPreference.getUser_Id(getActivity()));
        StringRequest req = new StringRequest (Request.Method.GET, BaseURL.GET_PROFILE+AppPreference.getUser_Id(getActivity()),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        Log.e("get_profile",response);

                        try {
                            JSONObject json = new JSONObject(response);
                            Boolean status = json.getBoolean("responce");
                            if (status) {
                                // Gson gson = new Gson();
                                JSONArray data = json.getJSONArray("data");

                                if(data.length ()> 0){
                                    // ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject object = (JSONObject) data.get(i);

                                        String user_id=object.getString("user_id");
                                        String user_phone=object.getString("user_phone");
                                        String user_fullname=object.getString("user_fullname");
                                        String user_email=object.getString("user_email");
                                        String user_image=object.getString("user_image");

                                        et_phone.setText(user_phone);
                                        et_name.setText(user_fullname);
                                        et_email.setText(user_email);

                                        if (!user_image.isEmpty()){
                                            Glide.with(getActivity())
                                                    .load(BaseURL.IMG_PROFILE_URL + user_image)
                                                    .placeholder(R.drawable.shoplogo)
                                                    .crossFade()
                                                    .into(iv_profile);
                                        }

                                    }
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
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
        AppController.getInstance().addToRequestQueue(req);



    }

}
