package com.example.nicolas.firstandroidproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

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

        String albumInfoStr = intent.getStringExtra("albuminfojson");
        AlbumInfo[] albumInfo = AddCdToDatabaseActivity.ReadResultForAlbumInfo(albumInfoStr);

        if (albumInfo.length >= 1) {
            WriteToDatabase(albumInfo[0]);
        }
        else{
            Toast.makeText(AddCdToDatabaseActivity.this, "Error couldn't find album", Toast.LENGTH_LONG);
        }
    }

    public static AlbumInfo[] ReadResult(String jsonStr) {
        AlbumInfo[] albumInfos = new AlbumInfo[1];
        SearchResponse response = new SearchResponse();
        Gson gson = new Gson();
        Type list = new TypeToken<SearchResponse>(){}.getType();
        response = gson.fromJson(jsonStr, list);
        return  response.results;
    }

    public static AlbumInfo[] ReadResultForAlbumInfo(String jsonStr){
        AlbumInfo[] albumInfos = new AlbumInfo[1];
        Gson gson = new Gson();
        Type list = new TypeToken<AlbumInfo[]>(){}.getType();
        albumInfos = gson.fromJson(jsonStr, list);
        return  albumInfos;
    }

    public static String JSONCreation(AlbumInfo[] albumInfos){
        Gson gson = new Gson();
        return  gson.toJson(albumInfos);
    }

    private void WriteToDatabase(AlbumInfo albumInfo){
        AlbumInfo[] albumInfosOld = ReadResultForAlbumInfo(ReadFromDatabase());
        AlbumInfo[] albumInfosNew = new AlbumInfo[albumInfosOld != null ? albumInfosOld.length + 1 : 1];
        if(albumInfosOld != null)
            System.arraycopy(albumInfosOld,0, albumInfosNew, 0, albumInfosOld.length);


        albumInfosNew[albumInfosOld != null ? albumInfosOld.length : 0] = albumInfo;

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

           Log.e("EXCEPTION", "Error:", ex);
        }
        return  result;
    }
    private void CreateFileIfNotExist() throws IOException {
        File file = new File(this.getFilesDir(), _fileName);
        if(!file.exists()){
            file.createNewFile();
        }
    }
}
