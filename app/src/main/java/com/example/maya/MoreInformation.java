package com.example.maya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MoreInformation extends AppCompatActivity {

    public int InfoID;
    public InfoWindowData InfoWindowData;
    public DBHandler dbHandler;
    public ArrayList<String> images;
    public AssetManager assetManager;
    public String ImageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_information);
        Intent intent = getIntent();
        this.InfoID = intent.getIntExtra("InfoID",0);
        this.dbHandler = new DBHandler(this);
        images=new ArrayList<>();
        assetManager=this.getAssets();
        init();
    }

    private void init() {
        this.InfoWindowData = this.dbHandler.getInfoWindowData(this.InfoID);
        ImageName=this.InfoWindowData.getImage();
        images=dbHandler.getImages(this.InfoID);
        LinearLayout view=findViewById(R.id.layout);
        TextView name_tv = view.findViewById(R.id.name);
        TextView details_tv = view.findViewById(R.id.details);
        TextView hotel_tv = view.findViewById(R.id.hotels);
        TextView food_tv = view.findViewById(R.id.food);
        TextView transport_tv = view.findViewById(R.id.transport);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        try {
            name_tv.setText(this.InfoWindowData.getTitle());
            details_tv.setText(this.InfoWindowData.getSnippet());
            hotel_tv.setText(this.InfoWindowData.getHotel());
            food_tv.setText(this.InfoWindowData.getFood());
            transport_tv.setText(this.InfoWindowData.getTransport());
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2f
            );
            param.gravity= Gravity.CENTER;
            for (int i = 0; i < images.size(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setId(i);
                imageView.setPadding(2, 2, 2, 2);
                final String pic=images.get(i);
                // imageView.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setLayoutParams(param);
                Drawable d = Drawable.createFromStream(assetManager.open(images.get(i)), images.get(i));
                imageView.setImageDrawable(d);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                layout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MoreInformation.this,SimpleImage.class);
                        intent.putExtra("imgName",pic);
                        startActivity(intent);
                    }
                });
            }
            System.out.println("more "+images.size());
            // Drawable d = Drawable.createFromStream(assetManager.open(ImageName), ImageName);
            // img.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
