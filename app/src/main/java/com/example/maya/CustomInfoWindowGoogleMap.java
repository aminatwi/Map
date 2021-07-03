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
        ImageView img = view.findViewById(R.id.pic);

        try {
            name_tv.setText(marker.getTitle());
            InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
            ImageName=infoWindowData.getImage();
            System.out.println("custom "+ImageName);
            Drawable d = Drawable.createFromStream(assetManager.open(ImageName), ImageName);
            img.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

}
