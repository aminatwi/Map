package com.example.maya;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
         insertInformation();
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
        Restaurant restaurant=new Restaurant();
        //restaurant.setId(1);
        restaurant.setV(33.4511800);
        restaurant.setV1(35.48880000);
        restaurant.setName("McDonalds");
        restaurant.setDescription("Good food");
        dbHandler.AddRestautrant(restaurant);

        Restaurant restaurant2=new Restaurant();
        //restaurant2.setId(2);
        restaurant2.setV(33.9796587);
        restaurant2.setV1(35.6509818);
        restaurant2.setName("Pizza Hut");
        restaurant2.setDescription("Good food");
        dbHandler.AddRestautrant(restaurant2);

        Restaurant restaurant3=new Restaurant();
        //restaurant3.setId(3);
        restaurant3.setV(33.9796587);
        restaurant3.setV1(35.6509818);
        restaurant3.setName("Burger King");
        restaurant3.setDescription("Good food");
        dbHandler.AddRestautrant(restaurant3);

        Hotel hotel=new Hotel();
        //hotel.setId(1);
        hotel.setV(33.9795586);
        hotel.setV1(35.6518918);
        hotel.setName("Harissa Hotel");
        hotel.setDescription("5 stars hotel");
        dbHandler.AddHotel(hotel);

        Hotel hotel1=new Hotel();
        //hotel.setId(2);
        hotel1.setV(33.9795585);
        hotel1.setV1(35.6518933);
        hotel1.setName("Grand Hotel");
        hotel1.setDescription("4 stars hotel");
        dbHandler.AddHotel(hotel1);

        InfoWindowData info = new InfoWindowData();
        //info.setId(1);
        info.setV(33.4510800);
        info.setV1(35.48870000);
        info.setTitle("Maya Home");
        info.setSnippet("Maya Home located in Ain Qana.");
        String h="Hotels near:\n";
        for(int i=0;i<nearHotels(info.getV(),info.getV1()).size();i++){
            h+="\n"+nearHotels(info.getV(),info.getV1()).get(i).getName();
        }
        if(nearHotels(info.getV(),info.getV1()).size()==0) info.setHotel("No Hotels near");
        else info.setHotel(h);

        String r="Restaurants near:\n";
        for(int i=0;i<nearRestaurants(info.getV(),info.getV1()).size();i++){
            r+="\n"+nearRestaurants(info.getV(),info.getV1()).get(i).getName();
        }
        if(nearRestaurants(info.getV(),info.getV1()).size()==0) info.setFood("No near restaurants");
        else info.setFood(r);

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
        String hh="Hotels near:\n";
        for(int i=0;i<nearHotels(info2.getV(),info2.getV1()).size();i++){
            hh+="\n"+nearHotels(info2.getV(),info2.getV1()).get(i).getName();
        }
        if(nearHotels(info2.getV(),info2.getV1()).size()==0) info2.setHotel("No near hotels");
        else info2.setHotel(hh);
        String rr="Restaurants near:\n";
        for(int i=0;i<nearRestaurants(info2.getV(),info2.getV1()).size();i++){
            rr+="\n"+nearRestaurants(info2.getV(),info2.getV1()).get(i).getName();
        }
        if(nearRestaurants(info2.getV(),info2.getV1()).size()==0) info2.setFood("No near restaurants");
        else info2.setFood(rr);
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