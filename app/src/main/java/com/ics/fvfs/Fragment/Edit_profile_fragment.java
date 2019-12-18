package com.ics.fvfs.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.R;
import com.ics.fvfs.AppPreference;
import com.ics.fvfs.ConnectivityReceiver;
import com.ics.fvfs.JSONParser;
import com.ics.fvfs.NameValuePair;
import com.ics.fvfs.Session_management;
import com.ics.fvfs.Utility;
import com.ics.fvfs.AppController;
import com.ics.fvfs.MainActivity;

import de.hdodenhof.circleimageview.CircleImageView;


public class Edit_profile_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Edit_profile_fragment.class.getSimpleName();

    private EditText et_phone, et_name, et_email, et_house,et_pro_city,et_pro_state;
    private Button btn_update;
    private TextView tv_phone, tv_name, tv_email, tv_house, tv_pro_country;
    private CircleImageView iv_profile;
    private ImageView iccamera;
    //private Spinner sp_socity;

    private String getsocity = "";
    private String filePath = "";
    private static final int GALLERY_REQUEST_CODE1 = 201;
    private Bitmap bitmap;
    private Uri imageuri;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;

    private Session_management sessionManagement;
     File destination;
     String filenew="";

    public Edit_profile_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.edit_profile));

        sessionManagement = new Session_management(getActivity());

        et_phone = (EditText) view.findViewById(R.id.et_pro_phone);
        et_name = (EditText) view.findViewById(R.id.et_pro_name);
        et_email = (EditText) view.findViewById(R.id.et_pro_email);
        iv_profile = (CircleImageView) view.findViewById(R.id.iv_pro_img);

        tv_phone = (TextView) view.findViewById(R.id.tv_pro_phone);
        tv_name = (TextView) view.findViewById(R.id.tv_pro_name);
        tv_email = (TextView) view.findViewById(R.id.tv_pro_email);
//       et_house = (EditText) view.findViewById(R.id.et_pro_home);
//        et_pro_city = (EditText) view.findViewById(R.id.et_pro_city);
//        et_pro_state = (EditText) view.findViewById(R.id.et_pro_state);
//        tv_house = (TextView) view.findViewById(R.id.tv_pro_home);
//        tv_pro_country = (TextView) view.findViewById(R.id.tv_pro_country);

        btn_update = (Button) view.findViewById(R.id.btn_pro_edit);
        iccamera = (ImageView) view.findViewById(R.id.icon_camera);

        String getemail = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
        String getname = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        String getphone = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);

        String getpin = sessionManagement.getUserDetails().get(BaseURL.KEY_PINCODE);
        String gethouse = sessionManagement.getUserDetails().get(BaseURL.KEY_HOUSE);
        getsocity = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);
        String getsocity_name = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);


        if (ConnectivityReceiver.isConnected()) {
            GetProfileRequest();
        }

//*************************************************************************
        //et_name.setText(getname);
       // et_phone.setText(getphone);

        /*if (!TextUtils.isEmpty(getsocity_name)) {
            btn_socity.setText(getsocity_name);
        }*/

        if (!TextUtils.isEmpty(getimage)) {

            Glide.with(this)
                    .load(BaseURL.IMG_PROFILE_URL + getimage)
                    .placeholder(R.drawable.shoplogo)
                    .crossFade()
                    .into(iv_profile);
        }

//        if (!TextUtils.isEmpty(getemail)){
//       //     et_email.setText(getemail);
//        }

        /*if (!TextUtils.isEmpty(gethouse)){
            et_house.setText(gethouse);
        }*/

        btn_update.setOnClickListener(this);
        //btn_socity.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        iccamera.setOnClickListener(this);

        return view;
    }
//*********************************************************************************
    private void GetProfileRequest() {
       // Toast.makeText(getActivity(), "userid "+AppPreference.getUser_Id(getActivity()), Toast.LENGTH_SHORT).show();
        Log.e("UserId",""+AppPreference.getUser_Id(getActivity()));
        StringRequest req = new StringRequest (Request.Method.GET,BaseURL.GET_PROFILE+AppPreference.getUser_Id(getActivity()),
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
//********************************************************************************
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_pro_edit) {
            attemptEditProfile();
        } /*else if (id == R.id.btn_pro_socity) {

            String getpincode = et_pin.getText().toString();

            if (!TextUtils.isEmpty(getpincode)) {

                Bundle args = new Bundle();
                com.tukuri.ics.Fragment fm = new Socity_fragment();
                args.putString("pincode", getpincode);
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }else{
                Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_pincode), Toast.LENGTH_SHORT).show();
            }

        } */
        else if (id == R.id.iv_pro_img) {

            selectImage();

//            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            // Start the Intent
//            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE1);
        }
        else if(id == R.id.icon_camera)
        {
            selectImage();
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(i, SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void attemptEditProfile() {

        tv_phone.setText(getResources().getString(R.string.et_login_phone_hint));
        tv_email.setText(getResources().getString(R.string.tv_login_email));
        tv_name.setText(getResources().getString(R.string.tv_reg_name_hint));
        /*tv_house.setText(getResources().getString(R.string.tv_reg_house));
        tv_socity.setText(getResources().getString(R.string.tv_reg_socity));*/

        tv_name.setTextColor(getResources().getColor(R.color.color_3));
        tv_phone.setTextColor(getResources().getColor(R.color.color_3));
        tv_email.setTextColor(getResources().getColor(R.color.color_3));
        /*tv_house.setTextColor(getResources().getColor(R.color.dark_gray));
        tv_socity.setTextColor(getResources().getColor(R.color.dark_gray));*/

        String getphone = et_phone.getText().toString();
        String getname = et_name.getText().toString();
        String getemail = et_email.getText().toString();
        /*String gethouse = et_house.getText().toString();
        String getsocity = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);*/

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(getphone)) {
            tv_phone.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_phone;
            cancel = true;
        } else if (!isPhoneValid(getphone)) {
            tv_phone.setText(getResources().getString(R.string.phone_too_short));
            tv_phone.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_phone;
            cancel = true;
        }

        if (TextUtils.isEmpty(getname)) {
            tv_name.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_name;
            cancel = true;
        }

        if (TextUtils.isEmpty(getemail)) {
            tv_email.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_email;
            cancel = true;
        }
/*
        if (filenew.isEmpty())
        {
            Toast.makeText(getActivity(), "Please Add Profile Picture", Toast.LENGTH_SHORT).show();
            cancel = true;
        }*/

        /*if (TextUtils.isEmpty(getsocity) && getsocity == null) {
            tv_socity.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = btn_socity;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            if (ConnectivityReceiver.isConnected()) {

               // String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
                String user_id = AppPreference.getUser_Id(getActivity());

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
                    new editProfile(user_id, getname, getphone,getemail).execute();
                }
            }
        }
    }

    private boolean isPhoneValid(String phoneno) {
        //TODO: Replace this with your own logic
        return phoneno.length() > 9;
    }
//********************************************************************************
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // if the result is capturing Image
//        if ((requestCode == GALLERY_REQUEST_CODE1)) {
//            if (resultCode == getActivity().RESULT_OK) {
//                try {
//                    Uri selectedImage = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                    // Get the cursor
//                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String imgDecodableString = cursor.getString(columnIndex);
//                    cursor.close();
//
//                    //filePath = imgDecodableString;
//
//                    Bitmap b = BitmapFactory.decodeFile(imgDecodableString);
//                    Bitmap out = Bitmap.createScaledBitmap(b, 1200, 1024, false);
//
//                    //getting image from gallery
//                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//
//
//                    File file = new File(imgDecodableString);
//                    filePath = file.getAbsolutePath();
//                    FileOutputStream fOut;
//                    try {
//                        fOut = new FileOutputStream(file);
//                        out.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
//                        fOut.flush();
//                        fOut.close();
//                        //b.recycle();
//                        //out.recycle();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    if (requestCode == GALLERY_REQUEST_CODE1) {
//
//                        // Set the Image in ImageView after decoding the String
//                        iv_profile.setImageBitmap(bitmap);
//                    }
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//*********************************************************************************
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == SELECT_FILE) {
            onSelectFromGalleryResult(data);
        } else if (requestCode == REQUEST_CAMERA)
            onCaptureImageResult(data);
    }
}

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {

            Uri pickedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            destination = new File(cursor.getString(cursor.getColumnIndex(filePath[0])));
            cursor.close();

            if(destination !=null)
            {
               filenew=destination.getAbsolutePath();

            }else {
                Toast.makeText(getActivity(), "something wrong", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(this, ""+destination, Toast.LENGTH_SHORT).show();
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        iv_profile.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            if(destination !=null)
            {
                filenew=destination.getAbsolutePath();
               // Toast.makeText(getActivity(), "path is"+destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), "something wrong", Toast.LENGTH_SHORT).show();
            }
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        iv_profile.setImageBitmap(thumbnail);
    }


    // asynctask for upload data with image or not image using HttpOk
    public class editProfile extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        JSONParser jsonParser;
        ArrayList<NameValuePair> nameValuePairs;
        ArrayList<NameValuePair> nameValuePairs1;
        boolean response;
        String error_string, success_msg;

        String getuid;
        String getphone;
        String getname;
        String getemail;

        String getimage;

        public editProfile(String user_id, String name, String phone, String email) {

            nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new NameValuePair("user_id", user_id));
            nameValuePairs.add(new NameValuePair("user_fullname", name));
            nameValuePairs.add(new NameValuePair("user_mobile", phone));
            nameValuePairs.add(new NameValuePair("user_email", email));


            nameValuePairs1=new ArrayList<NameValuePair>();
            nameValuePairs1.add(new NameValuePair("user_id", user_id));
            nameValuePairs1.add(new NameValuePair("user_fullname", name));
            nameValuePairs1.add(new NameValuePair("user_mobile", phone));
            nameValuePairs1.add(new NameValuePair("user_email", email));
            nameValuePairs1.add(new NameValuePair("image", filenew));


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.uploading_profile_data), true);
            jsonParser = new JSONParser(getActivity());
        }

        @Override
        protected Void doInBackground(Void... params) {

            String json_responce = null;
            try {
                if (filenew.isEmpty()) {
                    json_responce = jsonParser.execPostScriptJSON(BaseURL.EDIT_PROFILE, nameValuePairs);
                } else {
                   // json_responce = jsonParser.execPostScriptJSON(BaseURL.EDIT_PROFILE, nameValuePairs1);
                    json_responce = jsonParser.execMultiPartPostScriptJSON(BaseURL.EDIT_PROFILE, nameValuePairs, "image/png", filenew, "image");
                   }
                Log.e("ImagePath", filenew);
                Log.e("Pro_update" ,json_responce.toString());

                JSONObject jObj = new JSONObject(json_responce);

                if (jObj.getBoolean("responce")) {
                    response = true;
                    //success_msg = jObj.getString("data");

                    JSONObject obj = jObj.getJSONObject("data");

                    getuid = obj.getString("uid");
                    getphone = obj.getString("user_phone");
                    getname = obj.getString("user_fullname");
                    getemail = obj.getString("user_email");

                    getimage = obj.getString("user_image");

//                    AppPreference.setUser_Id(getActivity(),getuid);
//                    AppPreference.setName(getActivity(),getname);
//                    AppPreference.setMobile(getActivity(),getphone);

                } else {
                    response = false;
                    error_string = jObj.getString("error");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null) {
                progressDialog.hide();
                progressDialog.dismiss();
                progressDialog = null;
            }

            if (response) {

                sessionManagement.updateData(getname,getphone,getemail,getimage);

                ((MainActivity) getActivity()).updateHeader();

                Toast.makeText(getActivity(), getResources().getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                iv_profile.setImageResource(R.drawable.shoplogo);
                tv_name.setTextColor(getResources().getColor(R.color.grey));
                tv_phone.setTextColor(getResources().getColor(R.color.grey));
                tv_email.setTextColor(getResources().getColor(R.color.grey));
                et_name.getText().clear();
                et_email.getText().clear();
                et_phone.getText().clear();
                ((MainActivity) getActivity()).onBackPressed();
            } else {
                Toast.makeText(getActivity(), "" + error_string, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (progressDialog != null) {
                progressDialog.hide();
                progressDialog.dismiss();
                progressDialog = null;
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem cart = menu.findItem(R.id.action_cart);
        cart.setVisible(false);
        MenuItem change_pass = menu.findItem(R.id.action_change_password);
        change_pass.setVisible(false);
        MenuItem search = menu.findItem(R.id.action_search);
        search.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_change_password:
                Fragment fm = new Change_password_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
                return false;
        }
        return false;
    }

}
