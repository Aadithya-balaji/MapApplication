package com.example.googlemaps.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class DatabaseHepler extends SQLiteOpenHelper{


    public static final String DATABASE_NAME = "Places.db";
    public static final String TABLE_NAME = "Place_table";
    public static final String COL_1 = "id";
    public static final String COL_2 = "address";
    public static final String COL_3 = "lat";
    public static final String COL_4 = "lng";


    public DatabaseHepler(@Nullable Context context) {
        super(context,DATABASE_NAME,null, 4);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table "+ TABLE_NAME + "(id integer primary key, address text,lat Double,lng Double)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME) ;
        onCreate(db);
    }

    public boolean insertdata(String address, Double lat,Double lng){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,address);
        contentValues.put(COL_3,lat);
        contentValues.put(COL_4,lng);


        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result == -1)
            return false;
         else
            return true;


    }

    public Cursor getalldata(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res  = db.rawQuery("select * from " + TABLE_NAME,null);
        return res;
    }


    public Integer deletedata(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_NAME,"id = ?",new String[] { id });

    }
}
