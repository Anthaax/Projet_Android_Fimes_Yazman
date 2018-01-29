package com.example.nicolas.firstandroidproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Nicolas on 28/01/2018.
 */

public class AddCdToDatabaseActivity extends AppCompatActivity {
    private String _fileName = "findYourCd.txt";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.add_cd_to_database);

        Intent intent = getIntent(); // Intent which started this activity

        CdParcelable cd = intent.getParcelableExtra("cd");
    }

    private AlbumInfo[] ReadResult(String jsonStr) {
        AlbumInfo[] albumInfos = new AlbumInfo[1];
        Gson gson = new Gson();
        Type list = new TypeToken<AlbumInfo[]>(){}.getType();
        albumInfos = gson.fromJson(jsonStr, list);
        return  albumInfos;
    }

    private String JSONCreation(AlbumInfo[] albumInfos){
        Gson gson = new Gson();
        return  gson.toJson(albumInfos);
    }
    private void WriteToDatabase(AlbumInfo albumInfo){
        AlbumInfo[] albumInfosOld = ReadResult(ReadFromDatabase());
        AlbumInfo[] albumInfosNew = new AlbumInfo[albumInfo != null ? albumInfosOld.length + 1 : 1];
        if(albumInfo != null)
            System.arraycopy(albumInfosOld,0, albumInfosNew, 0, albumInfosOld.length);
        albumInfosNew[albumInfo != null ? albumInfosOld.length : 0] = albumInfo;

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            try {
                CreateFileIfNotExist();
                String json = JSONCreation(albumInfosNew);
                OutputStreamWriter writer = new OutputStreamWriter(this.openFileOutput(_fileName, Context.MODE_PRIVATE));
                writer.write(json);
                writer.close();
            }catch (Exception ex){

            }
        }
    }
    private String ReadFromDatabase(){
        String result ="";

        try {
            CreateFileIfNotExist();
            InputStream inputStream = this.openFileInput(_fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String build = "";
            StringBuilder builder = new StringBuilder();
            while ( (build = bufferedReader.readLine()) != null){
                builder.append(build);
            }
            inputStream.close();
            result = builder.toString();

        }catch (Exception ex){

        }
        return  result;
    }
    private void CreateFileIfNotExist() throws IOException {
        File file = new File(_fileName);
        if(!file.exists()){
            file.createNewFile();
        }
    }
}
