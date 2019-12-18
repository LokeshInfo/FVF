package com.ics.fvfs.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ics.fvfs.Adapter.DealsAdapter;
import com.ics.fvfs.Adapter.Home_adapter;
import com.ics.fvfs.Adapter.HorizontalAdapter;
import com.ics.fvfs.Adapter.OfferAdapter;
import com.ics.fvfs.Adapter.RecyclerViewHorizontalListAdapter;
import com.ics.fvfs.Config.BaseURL;
import com.ics.fvfs.Model.Category_model;
import com.ics.fvfs.Model.Data;
import com.ics.fvfs.Model.DealsModel;
import com.ics.fvfs.Model.Grocery;
import com.ics.fvfs.Model.OfferModel;

import com.ics.fvfs.R;

import com.ics.fvfs.ConnectivityReceiver;
import com.ics.fvfs.CustomVolleyJsonRequest;
import com.ics.fvfs.HttpHandler;
import com.ics.fvfs.RecyclerTouchListener;
import com.ics.fvfs.AppController;
import com.ics.fvfs.MainActivity;


public class Home_fragment extends Fragment {

    private static String TAG = Home_fragment.class.getSimpleName();
    private SliderLayout imgSlider;
    private RecyclerView rv_items;
    //private RelativeLayout rl_view_all;
    private List<Category_model> category_modelList = new ArrayList<>();
    private Home_adapter adapter;
    private ImageView iv_location;
    private boolean isSubcat = false;
    EditText searchview;

    RecyclerView offerlist;
    HorizontalAdapter horizontalAdapter;
    private List<Data> data = new ArrayList<>();
    RecyclerView dealsview;
    private List<Grocery> groceryList = new ArrayList<>();
    private RecyclerView groceryRecyclerView;
    private RecyclerViewHorizontalListAdapter groceryAdapter;
    private String server_url;
    ArrayList<OfferModel> offer_list;
    private OfferAdapter offerAdapter;
    String product_image;
    ArrayList<DealsModel> deals_list;
    private DealsAdapter dealsAdapter;
    // ImageView imageNew;dealsview

    public Home_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).set_style_Title("FVF");
        ((MainActivity) getActivity()).updateHeader();

        // handle the touch event if true
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // check user can press back button or not
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    ((MainActivity) getActivity()).finish();

                    return true;
                }
                return false;
            }
        });

        imgSlider = (SliderLayout) view.findViewById(R.id.home_img_slider);
        rv_items = (RecyclerView) view.findViewById(R.id.rv_home);
        offerlist = (RecyclerView) view.findViewById(R.id.offerlist);
        searchview = (EditText) view.findViewById(R.id.searchview);
        dealsview = (RecyclerView) view.findViewById(R.id.dealsview);
        //   imageNew =  (ImageView) view.findViewById(R.id.imgNew);

        searchview.setText("");
        offer_list = new ArrayList<>();
        deals_list = new ArrayList<>();

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            rv_items.setLayoutManager(new GridLayoutManager(getActivity(), 3,GridLayoutManager.VERTICAL,false));
        }
        else{
            rv_items.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
//        rv_items.setLayoutManager(gridLayoutManager);

        //rv_items.setLayoutManager(new LinearLayoutManager(getActivity()));

        // initialize a SliderLayout
        imgSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imgSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imgSlider.setCustomAnimation(new DescriptionAnimation());
        imgSlider.setDuration(4000);

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetSliderRequest();
            makeGetCategoryRequest("");
        }

        if (ConnectivityReceiver.isConnected()) {
            new GetOfferlist().execute();
        } else {
            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
        }

        if (ConnectivityReceiver.isConnected()) {
            new GetTodaysDealslist().execute();
        } else {
            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
        }
//*****************************************************************************************************
        rv_items.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_items,
                new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String getid = category_modelList.get(position).getId();
                String getcat_title = category_modelList.get(position).getTitle();

                Bundle args = new Bundle();
                Fragment fm = new Product_fragment();
                args.putString("cat_id", getid);
                args.putString("cat_title", getcat_title);
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        dealsview.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), dealsview,
                new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Bundle args = new Bundle();
                        Fragment fm = new Show_pro_detail_fragment();
                        args.putString("product_id", deals_list.get(position).getProductId());
                        args.putString("category_id", deals_list.get(position).getCategoryId());
                        args.putString("product_image", deals_list.get(position).getProductImage());
                        args.putString("increament", deals_list.get(position).getIncreament());
                        args.putString("product_name", deals_list.get(position).getProductName());
                        args.putString("price", deals_list.get(position).getPrice());
                        args.putString("stock", deals_list.get(position).getInStock());
                        args.putString("title", deals_list.get(position).getProductName());
                        args.putString("unit", deals_list.get(position).getUnit());
                        args.putString("Mrp", deals_list.get(position).getMrp());
                        args.putString("unit_value", deals_list.get(position).getUnitValue());
                        args.putString("Prod_description", deals_list.get(position).getProductDescription());
                        args.putString("pack1",deals_list.get(position).getPack1());
                        args.putString("pack2",deals_list.get(position).getPack2());
                        args.putString("mrp1",deals_list.get(position).getMrp1());
                        args.putString("mrp2",deals_list.get(position).getMrp2());
                        args.putString("price1",deals_list.get(position).getPrice1());
                        args.putString("price2",deals_list.get(position).getPrice2());

                        fm.setArguments(args);

                        FragmentManager fragmentManager = (getActivity()).getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));


        offerlist.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), offerlist,
                new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Bundle args = new Bundle();
                        Fragment fm = new Show_pro_detail_fragment();
                        args.putString("product_id", offer_list.get(position).getProductId());
                        args.putString("category_id", offer_list.get(position).getCategoryId());
                        args.putString("product_image", offer_list.get(position).getProductImage());
                        args.putString("increament", offer_list.get(position).getIncreament());
                        args.putString("product_name", offer_list.get(position).getProductName());
                        args.putString("price", offer_list.get(position).getPrice());
                        args.putString("stock", offer_list.get(position).getInStock());
                        args.putString("title", offer_list.get(position).getProductName());
                        args.putString("unit", offer_list.get(position).getUnit());
                        args.putString("Mrp", offer_list.get(position).getMrp());
                        args.putString("unit_value", offer_list.get(position).getUnitValue());
                        args.putString("Prod_description", offer_list.get(position).getProductDescription());
                        args.putString("pack1",offer_list.get(position).getPack1());
                        args.putString("pack2",offer_list.get(position).getPack2());
                        args.putString("mrp1",offer_list.get(position).getMrp1());
                        args.putString("mrp2",offer_list.get(position).getMrp2());
                        args.putString("price1",offer_list.get(position).getPrice1());
                        args.putString("price2",offer_list.get(position).getPrice2());

                        fm.setArguments(args);

                        FragmentManager fragmentManager = (getActivity()).getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

//*****************************************************************************************************
       /* searchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fm = new Search_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        }); */

        searchview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    String product = searchview.getText().toString().trim();
                    Bundle args = new Bundle();
                    Fragment fm = new Search_fragment();
                    args.putString("SProduct",product);
                    fm.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                 return true;
                }
                return false;
            }
        });

        return view;
    }

    /*private void populategroceryList() {
        Grocery potato = new Grocery("Laptop 20% Off", R.drawable.app);
        Grocery onion = new Grocery("Television 15% Off", R.drawable.tv);
        Grocery cabbage = new Grocery("Mobile 30% Off", R.drawable.mobile);
        Grocery cauliflower = new Grocery("Speaker 20% Off", R.drawable.speak);
        groceryList.add(potato);
        groceryList.add(onion);
        groceryList.add(cabbage);
        groceryList.add(cauliflower);
        groceryAdapter.notifyDataSetChanged();
    }*/

    /**
     * Method to make json array request where json response starts wtih {
     */
    private void makeGetSliderRequest() {

        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_SLIDER_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = (JSONObject) response.get(i);

                                HashMap<String, String> url_maps = new HashMap<String, String>();
                                url_maps.put("slider_title", jsonObject.getString("slider_title"));
                                url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));

                                listarray.add(url_maps);
                            }

                            for (HashMap<String, String> name : listarray) {
                                TextSliderView textSliderView = new TextSliderView(getActivity());
                                // initialize a SliderLayout
                                textSliderView
                                        //   .description(name.get("slider_title"))
                                        .image(name.get("slider_image"))
                                        .setScaleType(BaseSliderView.ScaleType.Fit);

                                //add your extra information
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle()
                                        .putString("extra", name.get("slider_title"));

                                imgSlider.addSlider(textSliderView);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
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
    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeGetCategoryRequest(String parent_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_category_req";

        isSubcat = false;

        Map<String, String> params = new HashMap<String, String>();
        if (parent_id != null && parent_id != "") {
            params.put("parent", parent_id);
            isSubcat = true;
        }

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Category_model>>() {
                        }.getType();

                        category_modelList = gson.fromJson(response.getString("data"), listType);

                        adapter = new Home_adapter(category_modelList);
                        rv_items.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);

       /* MenuItem search = menu.findItem(R.id.action_search);
        search.setVisible(true);*/
        MenuItem check = menu.findItem(R.id.action_change_password);
        check.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
              /*  com.tukuri.ics.Fragment fm = new Search_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
                return false;*/
        }
        return false;
    }

    //----------------------------------------------------------------------

    class GetOfferlist extends AsyncTask<String, String, String> {
        String output = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Processing");
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                server_url = BaseURL.BASEURL2+"/index.php/Api/get_pro_offer";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("sever_url>>>>>>>>>", server_url);
            output = HttpHandler.makeServiceCall(server_url);
            //   Log.e("getcomment_url", output);
            System.out.println("getcomment_url" + output);
            return output;
        }

        @Override
        protected void onPostExecute(String output) {
            if (output == null) {
                dialog.dismiss();
            } else {
                try {
                    dialog.dismiss();

                    JSONArray Data_array = new JSONArray(output);
                    for (int i = 0; i < Data_array.length(); i++) {
                        JSONObject c = Data_array.getJSONObject(i);
                        String product_id = c.getString("product_id");
                        String product_name = c.getString("product_name");
                        String product_description = c.getString("product_description");
                        String product_image = c.getString("product_image");
                        String category_id = c.getString("category_id");
                        String in_stock = c.getString("in_stock");
                        String price = c.getString("price");
                        String unit_value = c.getString("unit_value");
                        String unit = c.getString("unit");
                        String increament = c.getString("increament");
                        String Mrp = c.getString("Mrp");
                        String today_deals = c.getString("today_deals");
                        String offers_cat = c.getString("offers_cat");
                        String deals_description = c.getString("deals_description");
                        String offers_cat_desc = c.getString("offers_cat_desc");
                        String emi = c.getString("emi");
                        String warranty = c.getString("warranty");

                        String pack1 = c.getString("pack1");
                        String pack2 = c.getString("pack2");
                        String mrp1 = c.getString("mrp1");
                        String mrp2 = c.getString("mrp2");
                        String price1 = c.getString("price1");
                        String price2 = c.getString("price2");

                        offer_list.add(new OfferModel(product_id, product_name, product_description, product_image, category_id, in_stock, price
                                , unit_value, unit, increament, Mrp,
                                today_deals, offers_cat, deals_description, offers_cat_desc, emi, warranty, pack1 , pack2, mrp1, mrp2, price1, price2));
                    }

                    offerAdapter = new OfferAdapter(getActivity(), offer_list);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    offerlist.setLayoutManager(mLayoutManager);
                    offerlist.setItemAnimator(new DefaultItemAnimator());
                    offerlist.setAdapter(offerAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //  dialog.dismiss();
                }
                super.onPostExecute(output);
            }
        }
    }

    //----------------------------------------------

    class GetTodaysDealslist extends AsyncTask<String, String, String> {
        String output = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Processing");
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                server_url = BaseURL.BASEURL2+"/index.php/Api/get_today_deals";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("sever_url>>>>>>>>>", server_url);
            output = HttpHandler.makeServiceCall(server_url);
            //   Log.e("getcomment_url", output);
            System.out.println("getcomment_url" + output);
            return output;
        }

        @Override
        protected void onPostExecute(String output) {
            if (output == null) {
                dialog.dismiss();
            } else {
                try {
                    dialog.dismiss();

                    JSONArray Data_array = new JSONArray(output);
                    for (int i = 0; i < Data_array.length(); i++) {
                        JSONObject c = Data_array.getJSONObject(i);
                        String product_id = c.getString("product_id");
                        String product_name = c.getString("product_name");
                        String product_description = c.getString("product_description");
                        product_image = c.getString("product_image");
                        String category_id = c.getString("category_id");
                        String in_stock = c.getString("in_stock");
                        String price = c.getString("price");
                        String unit_value = c.getString("unit_value");
                        String unit = c.getString("unit");
                        String increament = c.getString("increament");
                        String Mrp = c.getString("Mrp");
                        String today_deals = c.getString("today_deals");
                        String offers_cat = c.getString("offers_cat");
                        String deals_description = c.getString("deals_description");
                        String offers_cat_desc = c.getString("offers_cat_desc");
                        String emi = c.getString("emi");
                        String warranty = c.getString("warranty");

                        String pack1 = c.getString("pack1");
                        String pack2 = c.getString("pack2");
                        String mrp1 = c.getString("mrp1");
                        String mrp2 = c.getString("mrp2");
                        String price1 = c.getString("price1");
                        String price2 = c.getString("price2");

                        deals_list.add(new DealsModel(product_id, product_name, product_description, product_image, category_id, in_stock, price
                                , unit_value, unit, increament, Mrp, today_deals, offers_cat, deals_description, offers_cat_desc,
                                emi, warranty, pack1 , pack2, mrp1, mrp2, price1, price2));
                    }
                    /*    AnimationDrawable animation = new AnimationDrawable();
                        try {
                            animation.addFrame((Drawable) Glide.with(getActivity())
                                    .load(BaseURL.IMG_PRODUCT_URL+product_image)
                                    .placeholder(R.drawable.shop)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .into(imageNew), 1000);

                        }catch (Exception e)
                        {
                            Log.e("product_image>>>>",""+product_image);
                        }
                        animation.setOneShot(false);
                        imageNew.setBackgroundDrawable(animation);

                        // start the animation!
                        animation.start();
                    }*/

                    dealsAdapter = new DealsAdapter(getActivity(), deals_list);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    dealsview.setLayoutManager(mLayoutManager);
                    dealsview.setItemAnimator(new DefaultItemAnimator());
                    dealsview.setAdapter(dealsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //  dialog.dismiss();
                }
                super.onPostExecute(output);
            }
        }
    }

}
