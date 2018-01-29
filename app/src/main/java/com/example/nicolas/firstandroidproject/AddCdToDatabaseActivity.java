package com.example.nicolas.firstandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Nicolas on 28/01/2018.
 */

public class AddCdToDatabaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.add_cd_to_database);

        Intent intent = getIntent(); // Intent which started this activity

        CdParcelable cd = intent.getParcelableExtra("cd");
    }
    private AlbumInfo[] readResult(String jsonStr) {
        AlbumInfo[] albumInfos = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            albumInfos = new AlbumInfo[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                AlbumInfo albumInfo = new AlbumInfo();
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                albumInfo.id = jsonobject.getInt("id");
                albumInfo.resource_url = jsonobject.getString("resource_url");
                albumInfo.catno = jsonobject.getString("catno");
                albumInfo.uri = jsonobject.getString("uri");
                albumInfo.country = jsonobject.getString("country");
                albumInfo.thumb = jsonobject.getString("thumb");
                albumInfo.year = jsonobject.getString("year");
                albumInfo.type = jsonobject.getString("year");
                albumInfo.style = JSONArrayToStringArray(jsonobject.getJSONArray("style"));
                albumInfo.format = JSONArrayToStringArray(jsonobject.getJSONArray("format"));
                albumInfo.label = JSONArrayToStringArray(jsonobject.getJSONArray("label"));
                albumInfo.genre = JSONArrayToStringArray(jsonobject.getJSONArray("genre"));
                albumInfo.barcode = JSONArrayToStringArray(jsonobject.getJSONArray("barcode"));
                albumInfo.community = JSONArrayToCommunity(jsonobject.getJSONArray("community"));
                albumInfos[i] = albumInfo;
            }
        } catch ( JSONException ex){

        }
        return  albumInfos;
    }
    private String[] JSONArrayToStringArray(JSONArray json){
        String[] array = new String[json.length()];
        try {
            for (int i = 0; i < json.length(); i++){
                array[i] = json.getString(i);
            }
        }catch (JSONException ex) {
            return null;
        }
        return  array;
    }
    private Community JSONArrayToCommunity(JSONArray jsonArray){
        Community community = new Community();
        try {
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                community.want = jsonobject.getInt("want");
                community.have = jsonobject.getInt("have");
            }
        }catch (JSONException ex) {
            return null;
        }
        return  community;
    }
}
