package com.example.maya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;

public class SimpleImage extends AppCompatActivity {

    public String imgName;
    public DBHandler dbHandler;
    public AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_image);
        Intent intent = getIntent();
        this.imgName = intent.getStringExtra("imgName");
        this.dbHandler = new DBHandler(this);
        assetManager=this.getAssets();
        init();
    }
    private void init(){
        ImageView imageView=(ImageView)findViewById(R.id.pic);
        try {
            Drawable d = Drawable.createFromStream(assetManager.open(imgName), imgName);
            imageView.setImageDrawable(d);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}