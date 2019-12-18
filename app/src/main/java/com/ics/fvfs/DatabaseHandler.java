package com.ics.fvfs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static String DB_NAME = "grocery";
    private static int DB_VERSION = 1;
    private SQLiteDatabase db;

    public static final String CART_TABLE = "cart";

    public static final String COLUMN_ID = "product_id";
    public static final String COLUMN_QTY = "qty";
    public static final String COLUMN_IMAGE = "product_image";
    public static final String COLUMN_CAT_ID = "category_id";
    public static final String COLUMN_NAME = "product_name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_UNIT_VALUE = "unit_value";
    public static final String COLUMN_UNIT = "unit";

    public static final String COLUMN_INCREAMENT = "increament";
    public static final String COLUMN_STOCK = "stock";
    public static final String COLUMN_TITLE = "title";

    private static final String PACK1 = "pack1";
    private static final String PACK2 = "pack2";
    private static final String MRP1 = "mrp1";
    private static final String MRP2 = "mrp2";
    private static final String PRICE1 = "price1";
    private static final String PRICE2 = "price2";
    private static final String STATUS = "offer";
    private static double p_total = 0;

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        String exe = "CREATE TABLE IF NOT EXISTS " + CART_TABLE
                + "(" + COLUMN_ID + " integer primary key, "
                + COLUMN_QTY + " DOUBLE NOT NULL,"
                + COLUMN_IMAGE + " TEXT NOT NULL, "
                + COLUMN_CAT_ID + " TEXT NOT NULL, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_PRICE + " DOUBLE NOT NULL, "
                + COLUMN_UNIT_VALUE + " DOUBLE NOT NULL, "
                + COLUMN_UNIT + " TEXT NOT NULL, "
                + COLUMN_INCREAMENT + " DOUBLE , "
                + COLUMN_STOCK + " DOUBLE NOT NULL, "
                + COLUMN_TITLE + " TEXT NOT NULL ,"
                + PACK1 + " TEXT , "
                + PACK2 + " TEXT , "
                + MRP1 + " DOUBLE , "
                + MRP2 + " DOUBLE , "
                + PRICE1 +" DOUBLE , "
                + PRICE2 + " DOUBLE , "
                + STATUS + " integer  "
                + ")";

        db.execSQL(exe);

    }

    public boolean setCart(HashMap<String, String> map, Float Qty) {
        db = getWritableDatabase();
        if (isInCart(map.get(COLUMN_ID))) {
            db.execSQL("update " + CART_TABLE + " set " + COLUMN_QTY + " = '" + Qty + "' where " + COLUMN_ID + "=" + map.get(COLUMN_ID));
            return false;
        } else {
            ContentValues values = new ContentValues();

            values.put(COLUMN_ID, map.get(COLUMN_ID));
            values.put(COLUMN_QTY, Qty);
            values.put(COLUMN_CAT_ID, map.get(COLUMN_CAT_ID));
            values.put(COLUMN_IMAGE, map.get(COLUMN_IMAGE));
            values.put(COLUMN_INCREAMENT, map.get(COLUMN_INCREAMENT));
            values.put(COLUMN_NAME, map.get(COLUMN_NAME));
            values.put(COLUMN_PRICE, map.get(COLUMN_PRICE));
            values.put(COLUMN_STOCK, map.get(COLUMN_STOCK));
            values.put(COLUMN_TITLE, map.get(COLUMN_TITLE));
            values.put(COLUMN_UNIT, map.get(COLUMN_UNIT));
            values.put(COLUMN_UNIT_VALUE, map.get(COLUMN_UNIT_VALUE));
            values.put(PACK1, map.get(PACK1));
            values.put(PACK2, map.get(PACK2));
            values.put(MRP1, map.get(MRP1));
            values.put(MRP2, map.get(MRP2));
            values.put(PRICE1, map.get(PRICE1));
            values.put(PRICE2, map.get(PRICE2));
            values.put(STATUS, map.get(STATUS));

            Log.e("DB INSERTION","   "+values);
            db.insert(CART_TABLE, null, values);
            return true;
        }
    }

    public void update_Status(int stat, String p_id)
    {
        db = getWritableDatabase();

        if (stat == 0 || stat == 1 || stat == 2)
        {
            db.execSQL("update " + CART_TABLE + " set " + STATUS + " = " + stat + " where " + COLUMN_ID + " = " + p_id );

            Log.e("UPDATE QRY : ",""+"update " + CART_TABLE + " set " + STATUS +
                    " = " + stat + " where " + COLUMN_ID + " = " + p_id);
            Log.e("DATABASE : "," ///// // / / / / /   UPDATED ........ ");
        }

        else
        {
            Log.e("DATABASE : ","......... NOT UPDATED ........ ");
        }
    }

    public boolean isInCart(String id) {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_ID + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) return true;

        return false;
    }

    public String getCartItemQty(String id) {

        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_ID + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_QTY));

    }

    public String getInCartItemQty(String id) {
        if (isInCart(id)) {
            db = getReadableDatabase();
            String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_ID + " = " + id;
            Cursor cursor = db.rawQuery(qry, null);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(COLUMN_QTY));
        } else {
            return "0.0";
        }
    }

    public int getCartCount() {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        return cursor.getCount();
    }

    public String getTotalAmount() {
        db = getReadableDatabase();
        String qry = "Select SUM(" + COLUMN_QTY + " * " + COLUMN_PRICE + ") as total_amount  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String total = cursor.getString(cursor.getColumnIndex("total_amount"));
        if (total != null) {

            return total;
        } else {
            return "0";
        }
    }

    public double gettTotalAmount() {
        p_total = 0;
        db = getReadableDatabase();

        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        int row = getrows();

        if (cursor!=null) {

                for (int i = 0; i < cursor.getCount(); i++) {

                    if (cursor.getInt(cursor.getColumnIndex("offer")) == 0) {
                        p_total = p_total + (cursor.getInt(cursor.getColumnIndex("qty")) * cursor.getDouble(cursor.getColumnIndex("price")));
                        Log.e("DB TOTAL : 0", " " + p_total + " for product "+
                                cursor.getString(cursor.getColumnIndex("product_name")));
                        cursor.moveToNext();

                    }

                    else if (cursor.getInt(cursor.getColumnIndex("offer")) == 1) {
                        p_total = p_total + cursor.getDouble(cursor.getColumnIndex("price1"));
                        Log.e("DB TOTAL : 1", " " + p_total + " for product "+
                                cursor.getString(cursor.getColumnIndex("product_name")));
                        cursor.moveToNext();
                    }

                    else if (cursor.getInt(cursor.getColumnIndex("offer")) == 2) {
                        p_total = p_total + cursor.getDouble(cursor.getColumnIndex("price2"));
                        Log.e("DB TOTAL : 2", " " + p_total + " for product "+
                                cursor.getString(cursor.getColumnIndex("product_name")));
                        cursor.moveToNext();
                    }
                }
                return p_total;
        }
            return 0;
    }

    public int getrows() {
        String countQuery = "SELECT  * FROM " + CART_TABLE;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.e("NUM OF ___"," ROWS are "+count);
        cursor.close();
        return count;
    }

    public ArrayList<HashMap<String, String>> getCartAll() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            map.put(COLUMN_QTY, cursor.getString(cursor.getColumnIndex(COLUMN_QTY)));
            map.put(COLUMN_IMAGE, cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
            map.put(COLUMN_CAT_ID, cursor.getString(cursor.getColumnIndex(COLUMN_CAT_ID)));
            map.put(COLUMN_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            map.put(COLUMN_PRICE, cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)));
            map.put(COLUMN_UNIT_VALUE, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_VALUE)));
            map.put(COLUMN_UNIT, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT)));
            map.put(COLUMN_INCREAMENT, cursor.getString(cursor.getColumnIndex(COLUMN_INCREAMENT)));
            map.put(COLUMN_STOCK, cursor.getString(cursor.getColumnIndex(COLUMN_STOCK)));
            map.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            map.put(PACK1,cursor.getString(cursor.getColumnIndex(PACK1)));
            map.put(PACK2,cursor.getString(cursor.getColumnIndex(PACK2)));
            map.put(MRP1,cursor.getString(cursor.getColumnIndex(MRP1)));
            map.put(MRP2,cursor.getString(cursor.getColumnIndex(MRP2)));
            map.put(PRICE1,cursor.getString(cursor.getColumnIndex(PRICE1)));
            map.put(PRICE2,cursor.getString(cursor.getColumnIndex(PRICE2)));
            map.put(STATUS,cursor.getString(cursor.getColumnIndex(STATUS)));

            list.add(map);
            cursor.moveToNext();
        }
        return list;
    }

    public String getFavConcatString() {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String concate = "";
        for (int i = 0; i < cursor.getCount(); i++) {
            if (concate.equalsIgnoreCase("")) {
                concate = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            } else {
                concate = concate + "_" + cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            }
            cursor.moveToNext();
        }
        return concate;
    }

    public void clearCart() {
        db = getReadableDatabase();
        db.execSQL("delete from " + CART_TABLE);
    }

    public void removeItemFromCart(String id) {
        db = getReadableDatabase();
        db.execSQL("delete from " + CART_TABLE + " where " + COLUMN_ID + " = " + id);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
