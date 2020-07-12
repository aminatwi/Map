package com.example.maya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MoreInformation extends AppCompatActivity {

    public int InfoID;
    public InfoWindowData InfoWindowData;
    public DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_information);
        Intent intent = getIntent();
        this.InfoID = intent.getIntExtra("InfoID",0);
        this.dbHandler = new DBHandler(this);
        init();
    }

    private void init() {
        this.InfoWindowData = this.dbHandler.getInfoWindowData(this.InfoID);
    }
}
