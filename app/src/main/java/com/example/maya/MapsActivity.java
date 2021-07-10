package com.example.maya;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public final static String MODULE_MAC = "98:D3:34:90:6F:A1";
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter bta;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    ConnectedThread btt = null;
    Button switchLight, switchRelay;
    TextView response;
    boolean lightflag = false;
    boolean relayFlag = true;
    public Handler mHandler;

    private GoogleMap mMap;
    private AssetManager assetManager;
    protected String images[];
    private ArrayList<MapInformation> _MapInformation;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
        bta = BluetoothAdapter.getDefaultAdapter();

        //if bluetooth is not enabled then create Intent for user to turn it on
        if(!bta.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }else{
            initiateBluetoothProcess();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
            initiateBluetoothProcess();
        }
    }
    public void initiateBluetoothProcess() {

        if (bta.isEnabled()) {

            //attempt to connect to bluetooth module
            BluetoothSocket tmp = null;
            mmDevice = bta.getRemoteDevice(MODULE_MAC);

            //create socket
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
                Log.i("[BLUETOOTH]", "Connected to: " + mmDevice.getName());
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException c) {
                    return;
                }
            }

            Log.i("[BLUETOOTH]", "Creating handler");
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    //super.handleMessage(msg);
                    if (msg.what == ConnectedThread.RESPONSE_MESSAGE) {
                        String txt = (String) msg.obj;
                        if (response.getText().toString().length() >= 30) {
                            response.setText("");
                            response.append(txt);
                        } else {
                            response.append("\n" + txt);
                        }
                    }
                }
            };

            Log.i("[BLUETOOTH]", "Creating and running Thread");
            btt = new ConnectedThread(mmSocket, mHandler);
            btt.start();


        }
    }
    //use this function if u want to send data to arduino!!!!!!!!!!!!
    /*********************
    public void sendData(String message) {
        Log.i("[BLUETOOTH]", "Attempting to send data");
        if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule

                btt.write(message.getBytes());

        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
*********************/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        assetManager = this.getAssets();
        dbHandler = new DBHandler(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(10);
        //insertInformation();
        SetMapInformation();

    }

    public String[] nbs(double v,double v1){
        String [] r=new String[2];
        String vv=""+v;
        String vv1=""+v1;
        String a=""+vv.charAt(3)+vv.charAt(4);
        String b=""+vv1.charAt(3)+vv1.charAt(4);
        r[0]=a;
        r[1]=b;
        return r;
    }
    public ArrayList<Restaurant> nearRestaurants(double v,double v1){
        ArrayList<Restaurant> nearest=new ArrayList<>();

        for(int i=0;i<dbHandler.getAllRestaurants().size();i++){
            if(Arrays.equals(nbs(v,v1),nbs(dbHandler.getAllRestaurants().get(i).getV(),dbHandler.getAllRestaurants().get(i).getV1()))){
                nearest.add(dbHandler.getAllRestaurants().get(i));
            }
        }
        return nearest;
    }
    public ArrayList<Hotel> nearHotels(double v,double v1){
        ArrayList<Hotel> nearest=new ArrayList<>();

        for(int i=0;i<dbHandler.getAllHotels().size();i++){
            if(Arrays.equals(nbs(v,v1),nbs(dbHandler.getAllHotels().get(i).getV(),dbHandler.getAllHotels().get(i).getV1()))){
                nearest.add(dbHandler.getAllHotels().get(i));
            }
        }
        return nearest;
    }
    private void insertInformation() {
/*
        Restaurant restaurant=new Restaurant();
        //restaurant.setId(1);
        restaurant.setV(33.4511800);
        restaurant.setV1(35.48880000);
        restaurant.setName("Tayyoun Restaurant");
        restaurant.setDescription("https://www.facebook.com/Tayyounrestaurant/");
        dbHandler.AddRestautrant(restaurant);

        Restaurant restaurant2=new Restaurant();
        //restaurant2.setId(2);
        restaurant2.setV(33.9796587);
        restaurant2.setV1(35.6509818);
        restaurant2.setName("Amar Harissa Restaurant");
        restaurant2.setDescription("http://www.amarrestaurants.com/");
        dbHandler.AddRestautrant(restaurant2);

        Restaurant restaurant3=new Restaurant();
        //restaurant3.setId(3);
        restaurant3.setV(33.9796587);
        restaurant3.setV1(35.6509818);
        restaurant3.setName("Beit Youssef Restaurant");
        restaurant3.setDescription("https://www.facebook.com/BeitYoussefRestaurant/");
        dbHandler.AddRestautrant(restaurant3);

        Hotel hotel=new Hotel();
        //hotel.setId(1);
        hotel.setV(33.9795586);
        hotel.setV1(35.6518918);
        hotel.setName("Madisson Hotel");
        hotel.setDescription("http://madissonhotel.com/");
        dbHandler.AddHotel(hotel);

        Hotel hotel1=new Hotel();
        //hotel.setId(2);
        hotel1.setV(33.9795585);
        hotel1.setV1(35.6518933);
        hotel1.setName("Lamedina Hotel");
        hotel1.setDescription("https://lamedinagroup.com/");
        dbHandler.AddHotel(hotel1);

        InfoWindowData info = new InfoWindowData();
        //info.setId(1);
        info.setV(33.4510800);
        info.setV1(35.48870000);
        info.setTitle("Maya Home");
        info.setSnippet("Maya Home located in Ain Qana.");
        ArrayList<String> listH1=new ArrayList<>();

        for(int i=0;i<nearHotels(info.getV(),info.getV1()).size();i++){
            listH1.add(nearHotels(info.getV(),info.getV1()).get(i).getName());
        }
        if(nearHotels(info.getV(),info.getV1()).size()==0) info.setHotel("none");
        else info.setHotel(listH1.toString());

        ArrayList<String> listR1=new ArrayList<>();
        for(int i=0;i<nearRestaurants(info.getV(),info.getV1()).size();i++){
            listR1.add(nearRestaurants(info.getV(),info.getV1()).get(i).getName());
        }
        if(nearRestaurants(info.getV(),info.getV1()).size()==0)
            info.setFood("none");
        else info.setFood(listR1.toString());

        info.setTransport("Reach the site by bus, car and train.");
        info.setImage("png/Maya.png");
        //info.setAudio_number(1);
        dbHandler.AddInfoWindowData(info);

        InfoWindowData info2 = new InfoWindowData();
        //info2.setId(2);
        info2.setV(33.9795587);
        info2.setV1(35.6508818);
        info2.setTitle("Harissa");
        info2.setSnippet("Lady of Lebanon");
        ArrayList<String> listH2=new ArrayList<>();
        for(int i=0;i<nearHotels(info2.getV(),info2.getV1()).size();i++){
            listH2.add(nearHotels(info2.getV(),info2.getV1()).get(i).getName());
        }
        if(nearHotels(info2.getV(),info2.getV1()).size()==0) info2.setHotel("none");
        else info2.setHotel(listH2.toString());
        ArrayList<String> listR2=new ArrayList<>();
        for(int i=0;i<nearRestaurants(info2.getV(),info2.getV1()).size();i++){
            listR2.add(nearRestaurants(info2.getV(),info2.getV1()).get(i).getName());
        }
        if(nearRestaurants(info2.getV(),info2.getV1()).size()==0) info2.setFood("none");
        else info2.setFood(listR2.toString());
        info2.setTransport("Reach the site by bus, car and train.");
        info2.setImage("png/Harissa.png");
        //info2.setAudio_number(1);
        dbHandler.AddInfoWindowData(info2);
        for(int i=0;i<2;i++){
            Image def=new Image();
            def.setId_place(dbHandler.getAllInfoWindowData().get(i).getId());
            def.setPic(dbHandler.getAllInfoWindowData().get(i).getImage());
            dbHandler.AddImage(def);
        }


        Image img1=new Image();
        img1.setId_place(2);
        img1.setPic("png/telefrique.png");

        Image img2=new Image();
        img2.setId_place(2);
        img2.setPic("png/church.png");

        Image img3=new Image();
        img3.setId_place(2);
        img3.setPic("png/view.png");

        dbHandler.AddImage(img3);
        dbHandler.AddImage(img2);
        dbHandler.AddImage(img1);
*/
    }


    private void init() {
        try {
            _MapInformation = new ArrayList<>();
            assetManager = this.getAssets();
            images = assetManager.list("png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SetMapInformation() {

        ArrayList<InfoWindowData> _infoWindowDataList = dbHandler.getAllInfoWindowData();

        for (InfoWindowData info:_infoWindowDataList) {
            LatLng Maya = new LatLng(info.getV(),info.getV1());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(Maya)
                    .title(info.getTitle())
                    .snippet(info.getSnippet())
                    .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED));
            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this,assetManager,info.getImage());
            mMap.setInfoWindowAdapter(customInfoWindow);
            Marker m = mMap.addMarker(markerOptions);
            m.setTag(info);
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                InfoWindowData info = dbHandler.getInfoWindowDataByAddress(marker.getPosition().latitude);
                Intent intent = new Intent(MapsActivity.this,MoreInformation.class);
                intent.putExtra("InfoID", info.getId());
                startActivity(intent);

            }
        });

        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                new LatLng(33.837469, 35.582914), 7);
        mMap.animateCamera(location);
    }
}