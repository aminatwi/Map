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
    private static final String TABLE_Restaurant="restaurant";
    private static final String TABLE_Hotel="hotel";
    private static final String TABLE_Images="images";

    private static final String Restaurant_name="nameR";
    private static final String Restaurant_desc="descR";
    private static final String Restaurant_V = "V";
    private static final String Restaurant_V1 = "V1";
    private static final String Hotel_name="nameH";
    private static final String Hotel_desc="descH";
    private static final String Hotel_V = "V";
    private static final String Hotel_V1 = "V1";
    private static final String Images_Id_place="idPlace";
    private static final String Images_pic="pic";
    private static final String MapInformation_V = "V";
    private static final String MapInformation_V1 = "V1";
    private static final String MapInformation_Title = "Title";
    private static final String MapInformation_Snippet = "Snippet";
    private static final String MapInformation_Hotel = "Hotel";
    private static final String MapInformation_Food = "Food";
    private static final String MapInformation_Image = "Image";
    private static final String MapInformation_Transport = "Transport";
    private static final String MapInformation_Audio_number="audio_number";

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
                + MapInformation_Transport + " TEXT,"
                + MapInformation_Audio_number + " INTEGER "
                +")";
        db.execSQL(CREATE_MapInformation_TABLE);

        String CREATE_restaurant_TABLE =  "CREATE TABLE " + TABLE_Restaurant + "("
                + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Restaurant_V + " REAL,"
                + Restaurant_V1 + " REAL,"
                + Restaurant_name+ " TEXT,"
                + Restaurant_desc + " TEXT "
                +")";
        db.execSQL(CREATE_restaurant_TABLE);

        String CREATE_hotel_TABLE =  "CREATE TABLE " + TABLE_Hotel + "("
                + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Hotel_V + " REAL,"
                + Hotel_V1 + " REAL,"
                + Hotel_name+ " TEXT,"
                + Hotel_desc + " TEXT "
                +")";
        db.execSQL(CREATE_hotel_TABLE);

        String CREATE_image_TABLE =  "CREATE TABLE " + TABLE_Images + "("
                + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Images_Id_place+ " INTEGER,"
                + Images_pic + " TEXT "
                +")";
        db.execSQL(CREATE_image_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public  InfoWindowData getInfoWindowDataByAddress(double v){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.query(  TABLE_MapInformations ,
                new String[]{KEY_ID,MapInformation_V,MapInformation_V1,MapInformation_Image,MapInformation_Title,MapInformation_Snippet,MapInformation_Hotel,MapInformation_Food,MapInformation_Transport,MapInformation_Audio_number},
                MapInformation_V + "=?",
                new String[] { String.valueOf(v) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            InfoWindowData _infoWindowData = new InfoWindowData(Integer.parseInt(cursor.getString(0)),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(3),cursor.getString(4),
                    cursor.getString(5),cursor.getString(6),
                    cursor.getString(7),cursor.getString(8),
                    Integer.parseInt(cursor.getString(9)));
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
            values.put(MapInformation_Audio_number,_InfoWindowData.getAudio_number());
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
                new String[]{KEY_ID,MapInformation_V,MapInformation_V1,MapInformation_Image,MapInformation_Title,MapInformation_Snippet,MapInformation_Hotel,MapInformation_Food,MapInformation_Transport,MapInformation_Audio_number},
                KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            InfoWindowData _infoWindowData = new InfoWindowData(Integer.parseInt(cursor.getString(0)),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(3),cursor.getString(4),
                    cursor.getString(5),cursor.getString(6),
                    cursor.getString(7),cursor.getString(8),
                    Integer.parseInt(cursor.getString(9)));
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
                int audio_number=cursor.getInt(9);
                InfoWindowData InfoWindowData = new InfoWindowData(id,v,v1,Image,Title,Snippet,Hotel,Food,Transport,audio_number);
                InfoWindowDataList.add(InfoWindowData);
            }while(cursor.moveToNext());
        }
        db.close();
        return InfoWindowDataList;
    }
    public Restaurant getRestaurantById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.query(  TABLE_Restaurant ,
                new String[]{KEY_ID,Restaurant_V,Restaurant_V1,Restaurant_name,Restaurant_desc},
                KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            Restaurant resto = new Restaurant(Integer.parseInt(cursor.getString(0)),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(3),
                    cursor.getString(4));
            db.close();
            return resto;
        }else{
            db.close();
            return null;
        }
    }
    public int AddRestautrant(Restaurant resto){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Restaurant_V,resto.getV());
            values.put(Restaurant_V1,resto.getV1());
            values.put(Restaurant_name ,resto.getName());
            values.put(Restaurant_desc,resto.getDescription());

            long i = db.insert(TABLE_Restaurant,null,values);
            db.close();
            return (int)i;
        }catch (Exception ex){
            return -1;
        }

    }
    public ArrayList<Restaurant> getAllRestaurants(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_Restaurant;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor == null)
            return null;
        ArrayList<Restaurant> restaurantList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                double v = cursor.getDouble(1);
                double v1 = cursor.getDouble(2);
                String Name = ""+cursor.getString(3);
                String Desc = ""+cursor.getString(4);

                Restaurant restaurant = new Restaurant(id,v,v1,Name,Desc);
                restaurantList.add(restaurant);
            }while(cursor.moveToNext());
        }
        db.close();
        return restaurantList;
    }
    public Hotel getHotelById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.query(  TABLE_Hotel ,
                new String[]{KEY_ID,Hotel_V,Hotel_V1,Hotel_name,Hotel_desc},
                KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            Hotel hotel = new Hotel(Integer.parseInt(cursor.getString(0)),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(3),
                    cursor.getString(4));
            db.close();
            return hotel;
        }else{
            db.close();
            return null;
        }
    }
    public int AddHotel(Hotel hotel){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Hotel_V,hotel.getV());
            values.put(Hotel_V1,hotel.getV1());
            values.put(Hotel_name ,hotel.getName());
            values.put(Hotel_desc,hotel.getDescription());

            long i = db.insert(TABLE_Hotel,null,values);
            db.close();
            return (int)i;
        }catch (Exception ex){
            return -1;
        }

    }
    public ArrayList<Hotel> getAllHotels(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_Hotel;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor == null)
            return null;
        ArrayList<Hotel> hotels = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                double v = cursor.getDouble(1);
                double v1 = cursor.getDouble(2);
                String Name = ""+cursor.getString(3);
                String Desc = ""+cursor.getString(4);

                Hotel hotel = new Hotel(id,v,v1,Name,Desc);
                hotels.add(hotel);
            }while(cursor.moveToNext());
        }
        db.close();
        return hotels;
    }
    public ArrayList<String> getImages(int id_place){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.query(  TABLE_Images ,
                new String[]{KEY_ID,Images_Id_place,Images_pic},
                Images_Id_place + "=?",
                new String[] { String.valueOf(id_place) }, null, null, null, null);
        if(cursor == null)
            return null;
        ArrayList<String> images = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{

                String image = ""+cursor.getString(2);

                images.add(image);
            }while(cursor.moveToNext());
        }
        db.close();
        return images;
    }
    public int AddImage(Image img){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Images_Id_place ,img.getId_place());
            values.put(Images_pic,img.getPic());

            long i = db.insert(TABLE_Images,null,values);
            db.close();
            return (int)i;
        }catch (Exception ex){
            return -1;
        }

    }
    public Restaurant getWebsiteR(String s){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.query(  TABLE_Restaurant ,
                new String[]{KEY_ID,Restaurant_V,Restaurant_V1,Restaurant_name,Restaurant_desc},
                Restaurant_name + "=?",
                new String[] { s }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            Restaurant resto = new Restaurant(Integer.parseInt(cursor.getString(0)),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(3),
                    cursor.getString(4));
            db.close();
            return resto;
        }else{
            db.close();
            return null;
        }
    }
    public Hotel getWebsiteH(String s){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.query(  TABLE_Hotel ,
                new String[]{KEY_ID,Hotel_V,Hotel_V1,Hotel_name,Hotel_desc},
                Hotel_name + "=?",
                new String[] { s }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            Hotel hotel = new Hotel(Integer.parseInt(cursor.getString(0)),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(3),
                    cursor.getString(4));
            db.close();
            return hotel;
        }else{
            db.close();
            return null;
        }
    }
}
