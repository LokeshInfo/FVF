package com.ics.fvfs;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ics.fvfs.R;

public class CustomDialogClass extends Dialog implements
        View.OnClickListener {

    public Activity context;

    private EditText et_add_adres_name,et_add_adres_phone,et_add_adres_pin,et_add_adres_home;
    private Button btn_add_adres_edit;
    private String getName ,getPhone ,getPincode,getHomeAddrs;

    public CustomDialogClass(@NonNull Activity context) {
        super(context);

        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialog_class);

        et_add_adres_name = (EditText)findViewById(R.id.et_add_adres_name);
        et_add_adres_phone = (EditText)findViewById(R.id.et_add_adres_phone);
        et_add_adres_pin = (EditText)findViewById(R.id.et_add_adres_pin);
        et_add_adres_home = (EditText)findViewById(R.id.et_add_adres_home);
        btn_add_adres_edit = (Button)findViewById(R.id.btn_add_adres_edit);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_add_adres_edit :

                getName = et_add_adres_name.getText().toString();
                getPhone = et_add_adres_phone.getText().toString();
                getPincode = et_add_adres_pin.getText().toString();
                getHomeAddrs = et_add_adres_home.getText().toString();

                break;
        }
    }
}
