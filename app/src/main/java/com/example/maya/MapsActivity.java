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
        InfoWindowData info = new InfoWindowData();
        info.setV(33.4510800);
        info.setV1(35.48870000);
        info.setTitle("Maya Home");
        info.setSnippet("Maya Home located in Ain Qana.");
        info.setHotel("Hotel : excellent hotels available");
        info.setFood("Food : all types of restaurants available");
        info.setTransport("Reach the site by bus, car and train.");
        info.setImage("png/Maya.png");
        dbHandler.AddInfoWindowData(info);
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