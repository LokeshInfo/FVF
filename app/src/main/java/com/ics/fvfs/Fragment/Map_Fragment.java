package com.ics.fvfs.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ics.fvfs.R;

import java.util.List;
import java.util.Locale;


public  class Map_Fragment extends Fragment
{
    SupportMapFragment mMap;

    private GoogleMap googleMap;
    private Location locationC;

    public Map_Fragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        this.mMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map);

        isLocationEnabled(getActivity());
        Check_Permission();

        return view;
    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    private void Check_Permission()
    {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            locationC = locationManager.getLastKnownLocation(provider);
            getMAP();
           }

        else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },8);
                isLocationEnabled(getActivity());
            } }
    }

    private void getMAP() {
        mMap.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap mMap) {

                googleMap = mMap;
                googleMap.setMyLocationEnabled(true);

                //        Geoaddress(googleMap.getMyLocation().getLatitude(),googleMap.getMyLocation().getLongitude());

            if (locationC!=null) {
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(locationC.getLatitude(), locationC.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(19);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
            }
             /*   LatLng latLng = new LatLng(locationCt.getLatitude(),
                        locationCt.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,10);
                    mMap.moveCamera(cameraUpdate);
                    googleMap.moveCamera(cameraUpdate);
                    googleMap.animateCamera(cameraUpdate);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                   Geoaddress(locationCt.getLatitude(), locationCt.getLongitude());*/

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        googleMap.clear();

                        Geoaddress(latLng.latitude, latLng.longitude);

                        //Log.e("Latitude : "+latitude,"Longitude : "+longitude);

                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Custom Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                       // Toast.makeText(getActivity(), "lat long is" + latLng, Toast.LENGTH_SHORT).show();

                    }
                });
                if (googleMap != null) {
                    Toast.makeText(getActivity(), "not null", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 8: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    mMap.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.setMyLocationEnabled(true);
                            googleMap.getMyLocation();
                        }
                    });
                    LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = locationManager.getBestProvider(criteria, true);
                    try {   locationC = locationManager.getLastKnownLocation(provider); }
                    catch (SecurityException ex)
                    {  ex.printStackTrace();     }
                    getMAP();
                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Check_Permission();
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    private void Geoaddress(double latitude, double longitude) {
        try {
            Geocoder geo = new Geocoder(getActivity(), Locale.ENGLISH);
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                //yourtextfieldname.setText("Waiting for Location");
            } else {
                if (addresses.size() > 0) {

                    String saddress = addresses.get(0).getAddressLine(0);
                    Toast.makeText(getActivity(), ""+saddress, Toast.LENGTH_SHORT).show();
                    Intent in = new Intent("StringAddr");
                    in.putExtra("Addr", saddress);
                    //in.putExtra("latitiude",""+latitude);
                    //in.putExtra("longitude",""+longitude);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(in);
                    getFragmentManager().popBackStack();

                    Log.e("address is", "" + addresses.get(0).getAddressLine(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
  /*  private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }*/
////////////////////////////

    //  googleMap.clear();
//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                googleMap.clear();
//                googleMap.addMarker(new MarkerOptions().position(latLng).title("Custom Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
////                ClickLocation = latLng
//            }
//        });

//////////////////////////////////////////////////////////////////////


