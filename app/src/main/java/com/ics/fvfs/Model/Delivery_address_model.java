package com.ics.fvfs.Model;

import android.os.Parcel;
import android.os.Parcelable;


public class Delivery_address_model implements Parcelable{

    String location_id;
    String user_id;
    String socity_id;
    String house_no;
    String receiver_name;
    String receiver_mobile;
    String socity_name;
    String pincode;
    String delivery_charge;
    String house_address;
    String city;
    String state;
    String landmark;

    boolean ischeckd;

    public Delivery_address_model(String getlocation_id, String getpin, String gethouse, String getname,
                                  String getphone, String gethouseAddress, String getCity, String getstate) {
        location_id=getlocation_id;
        pincode=getpin;
        house_no=gethouse;
        receiver_name=getname;
        receiver_mobile=getphone;
        house_address=gethouseAddress;
        city=getCity;
        state=getstate;
    }

    protected Delivery_address_model(Parcel in) {
        location_id = in.readString();
        user_id = in.readString();
        socity_id = in.readString();
        house_no = in.readString();
        receiver_name = in.readString();
        receiver_mobile = in.readString();
        socity_name = in.readString();
        pincode = in.readString();
        delivery_charge = in.readString();
        ischeckd = in.readByte() != 0;
    }

    public static final Creator<Delivery_address_model> CREATOR = new Creator<Delivery_address_model>() {
        @Override
        public Delivery_address_model createFromParcel(Parcel in) {
            return new Delivery_address_model(in);
        }

        @Override
        public Delivery_address_model[] newArray(int size) {
            return new Delivery_address_model[size];
        }
    };

    public String getLocation_id(){
        return location_id;
    }

    public String getUser_id(){
        return user_id;
    }

    public String getState() {
        return state;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getCity() {
        return city;
    }

    public String getHouse_address() {
        return house_address;
    }

    public String getSocity_id(){
        return socity_id;
    }

    public String getHouse_no(){
        return house_no;
    }

    public String getReceiver_name(){
        return receiver_name;
    }

    public String getReceiver_mobile(){
        return receiver_mobile;
    }

    public String getSocity_name(){
        return socity_name;
    }

    public String getPincode(){
        return pincode;
    }



    public String getDelivery_charge(){
        return delivery_charge;
    }

    public boolean getIscheckd(){
        return ischeckd;
    }

    public void setIscheckd(boolean ischeckd){
        this.ischeckd = ischeckd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(location_id);
        parcel.writeString(user_id);
        parcel.writeString(socity_id);
        parcel.writeString(house_no);
        parcel.writeString(receiver_name);
        parcel.writeString(receiver_mobile);
        parcel.writeString(socity_name);
        parcel.writeString(pincode);
        parcel.writeString(delivery_charge);
        parcel.writeByte((byte) (ischeckd ? 1 : 0));
    }
}
