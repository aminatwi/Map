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
    //use this function if u want to send data to arduino!!

    public void sendData(String message) {
        Log.i("[BLUETOOTH]", "Attempting to send data");
        if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule

                btt.write(message.getBytes());
            System.out.println("if==="+message);

        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong"+message, Toast.LENGTH_LONG).show();
        }
    }



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
        String a=""+vv.substring(0,5);
        String b=""+vv1.substring(0,5);
        r[0]=a;
        r[1]=b;
        return r;
    }
    public ArrayList<String> nearRestaurants(double v,double v1){
        ArrayList<String> nearest=new ArrayList<>();

        for(int i=0;i<dbHandler.getAllRestaurants().size();i++){
            if(Arrays.equals(nbs(v,v1),nbs(dbHandler.getAllRestaurants().get(i).getV(),dbHandler.getAllRestaurants().get(i).getV1()))){
                nearest.add(dbHandler.getAllRestaurants().get(i).getName());
            }
        }
        return nearest;
    }
    public ArrayList<String> nearHotels(double v,double v1){
        ArrayList<String> nearest=new ArrayList<>();

        for(int i=0;i<dbHandler.getAllHotels().size();i++){
            if(Arrays.equals(nbs(v,v1),nbs(dbHandler.getAllHotels().get(i).getV(),dbHandler.getAllHotels().get(i).getV1()))){
                nearest.add(dbHandler.getAllHotels().get(i).getName());
            }
        }
        return nearest;
    }
    private void insertInformation() {

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

        Restaurant restaurant4=new Restaurant();
        //restaurant3.setId(3);
        restaurant4.setV(34.0123168);
        restaurant4.setV1(35.8149145);
        restaurant4.setName("Chwarat Restaurant");
        restaurant4.setDescription("https://m.facebook.com/ChwaratRestaurant/");
        dbHandler.AddRestautrant(restaurant4);

        Restaurant restaurant5=new Restaurant();
        //restaurant3.setId(3);
        restaurant5.setV(34.0128933);
        restaurant5.setV1(35.8189363);
        restaurant5.setName("Pancho vino");
        restaurant5.setDescription("https://m.facebook.com/panchovinofaraya/#_=_");
        dbHandler.AddRestautrant(restaurant5);

        Restaurant restaurant6=new Restaurant();
        //restaurant3.setId(3);
        restaurant6.setV(33.5408892);
        restaurant6.setV1(35.5774865);
        restaurant6.setName("L'Os Jezzine Bkassine");
        restaurant6.setDescription("http://www.restaurantlos.com/");
        dbHandler.AddRestautrant(restaurant6);

        Restaurant restaurant7=new Restaurant();
        //restaurant3.setId(3);
        restaurant7.setV(34.0047058);
        restaurant7.setV1(36.2022851);
        restaurant7.setName("Lakkis Farm Restaurant");
        restaurant7.setDescription("https://lakkisfarm.com/");
        dbHandler.AddRestautrant(restaurant7);

        Restaurant restaurant8=new Restaurant();
        //restaurant3.setId(3);
        restaurant8.setV(34.0047058);
        restaurant8.setV1(36.2022851);
        restaurant8.setName("Al Rawabi Restaurant");
        restaurant8.setDescription("https://m.facebook.com/RawabiBaalbeck/");
        dbHandler.AddRestautrant(restaurant8);

        Restaurant restaurant9=new Restaurant();
        //restaurant3.setId(3);
        restaurant9.setV(34.2520958);
        restaurant9.setV1(35.9988386);
        restaurant9.setName("Al Zaytouni");
        restaurant9.setDescription("http://www.alzaytouni.com/");
        dbHandler.AddRestautrant(restaurant9);

        Restaurant restaurant10=new Restaurant();
        //restaurant3.setId(3);
        restaurant10.setV(34.2547058);
        restaurant10.setV1(35.9922851);
        restaurant10.setName("River Rock Restaurant");
        restaurant10.setDescription("http://riverrock.restaurant/");
        dbHandler.AddRestautrant(restaurant10);

        Restaurant restaurant11=new Restaurant();
        //restaurant3.setId(3);
        restaurant11.setV(34.123054);
        restaurant11.setV1(35.6431734);
        restaurant11.setName("Locanda A La Granda");
        restaurant11.setDescription("https://locandaalagranda.business.site/");
        dbHandler.AddRestautrant(restaurant11);

        Restaurant restaurant12=new Restaurant();
        //restaurant3.setId(3);
        restaurant12.setV(34.123054);
        restaurant12.setV1(35.6431734);
        restaurant12.setName("Feniqia");
        restaurant12.setDescription("https://www.facebook.com/Feniqia-615306411819947/");
        dbHandler.AddRestautrant(restaurant12);

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

        Hotel hotel2=new Hotel();
        //hotel.setId(2);
        hotel2.setV(34.0122567);
        hotel2.setV1(35.8149894);
        hotel2.setName("Villetta Di Faraya");
        hotel2.setDescription("http://www.villettadifaraya.com/");
        dbHandler.AddHotel(hotel2);

        Hotel hotel3=new Hotel();
        //hotel.setId(2);
        hotel3.setV(33.54085499);
        hotel3.setV1(35.5774888);
        hotel3.setName("L'etoile du loup");
        hotel3.setDescription("http://www.etoileduloup.com/");
        dbHandler.AddHotel(hotel3);

        Hotel hotel5=new Hotel();
        //hotel.setId(2);
        hotel5.setV(34.0047058);
        hotel5.setV1(36.2022851);
        hotel5.setName("Kanaan Hotel");
        hotel5.setDescription("https://kanaanhotel.com/");
        dbHandler.AddHotel(hotel5);

        Hotel hotel6=new Hotel();
        //hotel.setId(2);
        hotel6.setV(34.2520958);
        hotel6.setV1(35.9988386);
        hotel6.setName("Bauhaus Chalets & Motel");
        hotel6.setDescription("http://www.bauhauslb.com/");
        dbHandler.AddHotel(hotel6);

        Hotel hotel7=new Hotel();
        //hotel.setId(2);
        hotel7.setV(34.1230889);
        hotel7.setV1(35.6431734);
        hotel7.setName("Aleph Boutique Hotel");
        hotel7.setDescription("https://www.alephboutiquehotel.com/");
        dbHandler.AddHotel(hotel7);

        Hotel hotel8=new Hotel();
        //hotel.setId(2);
        hotel8.setV(34.1230889);
        hotel8.setV1(35.6431734);
        hotel8.setName("Byblos Comfort Hotel");
        hotel8.setDescription("http://www.bybloscomforthotel.com/");
        dbHandler.AddHotel(hotel8);



        InfoWindowData info = new InfoWindowData();
        //info.setId(1);
        info.setV(34.0122819);
        info.setV1(35.8149145);
        info.setTitle("Faraiya");
        info.setSnippet("\nFaraya is a village in the Keserwan District of the Mount Lebanon Governorate, Lebanon\n");
        ArrayList<String> listH1=nearHotels(info.getV(),info.getV1());
        if(listH1.size()==0) info.setHotel("none");
        else info.setHotel(listH1.toString());

        ArrayList<String> listR1=nearRestaurants(info.getV(),info.getV1());
        if(listR1.size()==0)
            info.setFood("none");
        else info.setFood(listR1.toString());

        info.setTransport("Reach the site by bus, car and train.");
        info.setImage("png/faraya.png");
        info.setAudio_number(1);
        dbHandler.AddInfoWindowData(info);

        InfoWindowData info2 = new InfoWindowData();
        //info2.setId(2);
        info2.setV(33.9795587);
        info2.setV1(35.6508818);
        info2.setTitle("Harissa");
        info2.setSnippet("Lady of Lebanon");
        ArrayList<String> listH2=nearHotels(info2.getV(),info2.getV1());
        if(nearHotels(info2.getV(),info2.getV1()).size()==0) info2.setHotel("none");
        else info2.setHotel(listH2.toString());
        ArrayList<String> listR2=nearRestaurants(info2.getV(),info2.getV1());
        if(nearRestaurants(info2.getV(),info2.getV1()).size()==0) info2.setFood("none");
        else info2.setFood(listR2.toString());
        info2.setTransport("Reach the site by bus, car and train.");
        info2.setImage("png/Harissa.png");
        info2.setAudio_number(2);
        dbHandler.AddInfoWindowData(info2);

        InfoWindowData info3 = new InfoWindowData();
        //info2.setId(2);
        info3.setV(34.0047058);
        info3.setV1(36.2022851);
        info3.setTitle("Baalbek");
        info3.setSnippet("\nBaalbek is a city located east of the Litani River in Lebanon's Beqaa Valley.\n");
        ArrayList<String> listH3=nearHotels(info3.getV(),info3.getV1());
        if(listH3.size()==0) info3.setHotel("none");
        else info3.setHotel(listH3.toString());
        ArrayList<String> listR3=nearRestaurants(info3.getV(),info3.getV1());
        if(listR3.size()==0) info3.setFood("none");
        else info3.setFood(listR3.toString());
        info3.setTransport("Reach the site by bus, car and train.");
        info3.setImage("png/baalbak.png");
        info3.setAudio_number(3);
        dbHandler.AddInfoWindowData(info3);

        InfoWindowData info4 = new InfoWindowData();
        //info2.setId(2);
        info4.setV(34.2520609);
        info4.setV1(35.9988386);
        info4.setTitle("Bsharri");
        info4.setSnippet("\nBsharri is located in the Bsharri District of the North Governorate in Lebanon\n");
        ArrayList<String> listH4=nearHotels(info4.getV(),info4.getV1());
        if(listH4.size()==0) info4.setHotel("none");
        else info4.setHotel(listH4.toString());
        ArrayList<String> listR4=nearRestaurants(info4.getV(),info4.getV1());
        if(listR4.size()==0) info4.setFood("none");
        else info4.setFood(listR4.toString());
        info4.setTransport("Reach the site by bus, car and train.");
        info4.setImage("png/bsharri.png");
        info4.setAudio_number(4);
        dbHandler.AddInfoWindowData(info4);

        InfoWindowData info5 = new InfoWindowData();
        //info2.setId(2);
        info5.setV(34.1230191);
        info5.setV1(35.6431734);
        info5.setTitle("Byblos");
        info5.setSnippet("\nByblos is a city in the Keserwan-Jbeil Governorate of Lebanon\n");
        ArrayList<String> listH5=nearHotels(info5.getV(),info5.getV1());
        if(listH5.size()==0) info5.setHotel("none");
        else info5.setHotel(listH5.toString());
        ArrayList<String> listR5=nearRestaurants(info5.getV(),info5.getV1());
        if(listR5.size()==0) info5.setFood("none");
        else info5.setFood(listR5.toString());
        info5.setTransport("Reach the site by bus, car and train.");
        info5.setImage("png/byblos.png");
        info5.setAudio_number(5);
        dbHandler.AddInfoWindowData(info5);

        InfoWindowData info6 = new InfoWindowData();
        //info2.setId(2);
        info6.setV(33.5408541);
        info6.setV1(35.5774865);
        info6.setTitle("Jezzine");
        info6.setSnippet("\nJezzine is a town in Lebanon, located 40 km south of Beirut.\n");
        ArrayList<String> listH6=nearHotels(info6.getV(),info6.getV1());
        if(listH6.size()==0) info6.setHotel("none");
        else info6.setHotel(listH6.toString());
        ArrayList<String> listR6=nearRestaurants(info6.getV(),info6.getV1());
        if(listR6.size()==0) info6.setFood("none");
        else info6.setFood(listR6.toString());
        info6.setTransport("Reach the site by bus, car and train.");
        info6.setImage("png/jezzine.png");
        info6.setAudio_number(6);
        dbHandler.AddInfoWindowData(info6);

        for(int i=0;i<dbHandler.getAllInfoWindowData().size();i++){
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

        Image img4=new Image();
        img4.setId_place(3);
        img4.setPic("png/baalbak2.png");

        Image img5=new Image();
        img5.setId_place(3);
        img5.setPic("png/baalbak3.png");

        Image img6=new Image();
        img6.setId_place(4);
        img6.setPic("png/bsharri2.png");

        Image img7=new Image();
        img7.setId_place(4);
        img7.setPic("png/bsahrri5.png");

        Image img16=new Image();
        img16.setId_place(4);
        img16.setPic("png/bsharri4.png");


        Image img8=new Image();
        img8.setId_place(5);
        img8.setPic("png/byblos2.png");

        Image img9=new Image();
        img9.setId_place(5);
        img9.setPic("png/byblos3.png");

        Image img19=new Image();
        img19.setId_place(5);
        img19.setPic("png/byblos4.png");

        Image img10=new Image();
        img10.setId_place(1);
        img10.setPic("png/faraya3.png");

        Image img110=new Image();
        img110.setId_place(1);
        img110.setPic("png/faraya4.png");

        Image img11=new Image();
        img11.setId_place(6);
        img11.setPic("png/jezzine3.png");

        Image img12=new Image();
        img12.setId_place(6);
        img12.setPic("png/jezzine2.png");

        dbHandler.AddImage(img3); dbHandler.AddImage(img2); dbHandler.AddImage(img1);
        dbHandler.AddImage(img4); dbHandler.AddImage(img5); dbHandler.AddImage(img6);
        dbHandler.AddImage(img7); dbHandler.AddImage(img8); dbHandler.AddImage(img9);
        dbHandler.AddImage(img10); dbHandler.AddImage(img11); dbHandler.AddImage(img16);
        dbHandler.AddImage(img19); dbHandler.AddImage(img110); dbHandler.AddImage(img12);
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
                //sendData(""+info.getAudio_number());
                startActivity(intent);

            }
        });

        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                new LatLng(33.837469, 35.582914), 7);
        mMap.animateCamera(location);
    }
}