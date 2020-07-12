package com.example.maya;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter{

    private Context context;
    private AssetManager assetManager;
    public String ImageName;

    public CustomInfoWindowGoogleMap(Context ctx,AssetManager _assetManager,String imageName){
        context = ctx;
        ImageName = imageName;
        assetManager = _assetManager;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.map_custom_infowindow, null);

        TextView name_tv = view.findViewById(R.id.name);
        TextView details_tv = view.findViewById(R.id.details);
        ImageView img = view.findViewById(R.id.pic);
        TextView hotel_tv = view.findViewById(R.id.hotels);
        TextView food_tv = view.findViewById(R.id.food);
        TextView transport_tv = view.findViewById(R.id.transport);
        try {
            name_tv.setText(marker.getTitle());
            details_tv.setText(marker.getSnippet());
            InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
            hotel_tv.setText(infoWindowData.getHotel());
            food_tv.setText(infoWindowData.getFood());
            transport_tv.setText(infoWindowData.getTransport());
            Drawable d = Drawable.createFromStream(assetManager.open(ImageName), ImageName);
            img.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

}
