package com.example.maya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MoreInformation extends AppCompatActivity {

    public int InfoID;
    public InfoWindowData InfoWindowData;
    public DBHandler dbHandler;
    public ArrayList<String> images;
    public AssetManager assetManager;
    public String ImageName;
    public ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_information);
        Intent intent = getIntent();
        this.InfoID = intent.getIntExtra("InfoID",0);
        this.dbHandler = new DBHandler(this);
        images=new ArrayList<>();
        list=new ArrayList<>();
        assetManager=this.getAssets();
        init();

    }

    ArrayList<String> stringToArray(String t){

        String s=""+t.subSequence(1, t.length()-1);
        String [] f=s.split(",");
        ArrayList<String> o=new ArrayList<>();
        for(int i=0;i<f.length;i++){
            o.add(f[i]);
        }
        return o;

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
        ListView listR= view.findViewById(R.id.listR);
        ListView listH=view.findViewById(R.id.listH);

        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        try {
            name_tv.setText(this.InfoWindowData.getTitle());

            details_tv.setText(this.InfoWindowData.getSnippet());

            if(this.InfoWindowData.getHotel().equals("none"))
            hotel_tv.setText("No Hotels Near\n");
            else {
                hotel_tv.setText("Hotels Near:\n");
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,stringToArray(this.InfoWindowData.getHotel()));
                listH.setAdapter(adapter);

                listH.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String str = parent.getItemAtPosition(position).toString();
                        if(str.charAt(0)==' ') str=str.substring(1);
                        System.out.println("["+str+"]");
                        String website=dbHandler.getWebsiteH(str).getDescription();
                        Uri webpage = Uri.parse(website);
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(webIntent);
                    }
                });
            }



            if(this.InfoWindowData.getFood().equals("none"))
            food_tv.setText("No Restaurants Near\n");
            else {
                food_tv.setText("Restaurants Near:\n");
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,stringToArray(this.InfoWindowData.getFood()));
                listR.setAdapter(adapter);

                listR.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String str = parent.getItemAtPosition(position).toString();
                        if(str.charAt(0)==' ') str=str.substring(1);
                        System.out.println("["+str+"]");
                        String website=dbHandler.getWebsiteR(str).getDescription();
                        Uri webpage = Uri.parse(website);
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(webIntent);
                    }
                });
            }
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
