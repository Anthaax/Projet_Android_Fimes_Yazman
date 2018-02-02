package com.example.nicolas.firstandroidproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

public class FindCDActivity  extends AppCompatActivity {

    private EditText artistName;
    private EditText albumName;
    private EditText tag;
    private Button search;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.find_cd);

        this.loadComponents();
        this.addListeners();
    }

    protected void loadComponents()
    {
        this.artistName = findViewById(R.id.text_input_artist);
        this.albumName = findViewById(R.id.text_input_cdname);
        this.tag = findViewById(R.id.text_input_tag);
        this.search = findViewById(R.id.find_cd_button);
    }

    protected void addListeners()
    {
        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String requestUrl = "https://api.discogs.com/database/search?artist=" + URLEncoder.encode(artistName.getText().toString(), "utf-8") + "&release_title=" + URLEncoder.encode(albumName.getText().toString(), "utf-8") + "&format=ambum&key=nNJIcOOiuEIRgXEIaOix&secret=AANxnfWVQStJWCjIdLCnVUpDdiAnTCLP";

                    DiscogsSearchService.SearchDiscogsDatabase(FindCDActivity.this, requestUrl);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        this.addBroadcastListeners();
    }

    protected void addBroadcastListeners(){

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String response = intent.getStringExtra("response");
                AlbumInfo[] albums = AddCdToDatabaseActivity.ReadResult(response);
                Intent intentAddToDatabase = new Intent(FindCDActivity.this, AddCdToDatabaseActivity.class);

                intentAddToDatabase.putExtra("albuminfojson", AddCdToDatabaseActivity.JSONCreation(albums));

                startActivity(intentAddToDatabase);
            }
        };

        //..more logic
    }


}
