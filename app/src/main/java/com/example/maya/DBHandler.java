package com.example.maya;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Map_DataBase";
    private static final String KEY_ID = "Id";

    private static final String TABLE_MapInformations = "MapInformations";
    private static final String MapInformation_V = "V";
    private static final String MapInformation_V1 = "V1";
    private static final String MapInformation_Title = "Title";
    private static final String MapInformation_Snippet = "Snippet";
    private static final String MapInformation_Hotel = "Hotel";
    private static final String MapInformation_Food = "Food";
    private static final String MapInformation_Image = "Image";
    private static final String MapInformation_Transport = "Transport";

    public DBHandler(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MapInformation_TABLE =  "CREATE TABLE " + TABLE_MapInformations + "("
                                     + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                                     + MapInformation_V + " REAL,"
                                     + MapInformation_V1 + " REAL,"
                                     + MapInformation_Title + " TEXT,"
                                     + MapInformation_Snippet+ " TEXT,"
                                     + MapInformation_Hotel+ " TEXT,"
                                     + MapInformation_Food+ " TEXT,"
                                     + MapInformation_Image+ " TEXT,"
                                     + MapInformation_Transport + " TEXT "
                                     +")";
        db.execSQL(CREATE_MapInformation_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public  InfoWindowData getInfoWindowDataByAddress(double v){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.query(  TABLE_MapInformations ,
                new String[]{KEY_ID,MapInformation_V,MapInformation_V1,MapInformation_Image,MapInformation_Title,MapInformation_Snippet,MapInformation_Hotel,MapInformation_Food,MapInformation_Transport},
                MapInformation_V + "=?",
                new String[] { String.valueOf(v) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            InfoWindowData _infoWindowData = new InfoWindowData(Integer.parseInt(cursor.getString(0)),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(3),cursor.getString(4),
                    cursor.getString(5),cursor.getString(6),
                    cursor.getString(7),cursor.getString(8));
            db.close();
            return _infoWindowData;
        }else{
            db.close();
            return null;
        }
    }
    public int AddInfoWindowData(InfoWindowData _InfoWindowData){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MapInformation_V , _InfoWindowData.getV());
            values.put(MapInformation_V1 , _InfoWindowData.getV1());
            values.put(MapInformation_Title ,_InfoWindowData.getTitle());
            values.put(MapInformation_Snippet,_InfoWindowData.getSnippet());
            values.put(MapInformation_Hotel,_InfoWindowData.getHotel());
            values.put(MapInformation_Food,_InfoWindowData.getFood());
            values.put(MapInformation_Image,_InfoWindowData.getImage());
            values.put(MapInformation_Transport,_InfoWindowData.getTransport());
            long i = db.insert(TABLE_MapInformations,null,values);
            db.close();
            return (int)i;
        }catch (Exception ex){
            return -1;
        }

    }

    public InfoWindowData getInfoWindowData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.query(  TABLE_MapInformations ,
                new String[]{KEY_ID,MapInformation_V,MapInformation_V1,MapInformation_Image,MapInformation_Title,MapInformation_Snippet,MapInformation_Hotel,MapInformation_Food,MapInformation_Transport},
                KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            InfoWindowData _infoWindowData = new InfoWindowData(Integer.parseInt(cursor.getString(0)),
                                                                Double.parseDouble(cursor.getString(1)),
                                                                Double.parseDouble(cursor.getString(2)),
                                                                cursor.getString(3),cursor.getString(4),
                                                                cursor.getString(5),cursor.getString(6),
                                                                cursor.getString(7),cursor.getString(8));
            db.close();
            return _infoWindowData;
        }else{
            db.close();
            return null;
        }
    }

    public ArrayList<InfoWindowData> getAllInfoWindowData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_MapInformations;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor == null)
            return null;
        ArrayList<InfoWindowData> InfoWindowDataList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                double v = cursor.getDouble(1);
                double v1 = cursor.getDouble(2);
                String Title = ""+cursor.getString(3);
                String Snippet = ""+cursor.getString(4);
                String Hotel = ""+cursor.getString(5);
                String Food = ""+cursor.getString(6);
                String Image = ""+cursor.getString(7);
                String Transport = ""+cursor.getString(8);
                InfoWindowData InfoWindowData = new InfoWindowData(id,v,v1,Image,Title,Snippet,Hotel,Food,Transport);
                InfoWindowDataList.add(InfoWindowData);
            }while(cursor.moveToNext());
        }
        db.close();
        return InfoWindowDataList;
    }
}
