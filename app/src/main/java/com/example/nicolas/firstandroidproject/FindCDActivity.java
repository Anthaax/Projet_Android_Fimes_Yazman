package com.example.nicolas.firstandroidproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class FindCDActivity  extends AppCompatActivity {

    private EditText artistName;
    private EditText albumName;
    private EditText tag;
    private Button search;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                    String[] tags = tag.getText().toString().toLowerCase().trim().split(" ");
                    String artistNameStr = artistName.getText().toString().toLowerCase().trim();
                    String albumNameStr = albumName.getText().toString().toLowerCase().trim();
                    boolean useArtistName = !artistNameStr.isEmpty();
                    boolean useAlbumName = !albumNameStr.isEmpty();

                    int tagNb = 0;
                    for (String tag : tags)
                    {
                        if (tag != null && !tag.isEmpty())
                        {
                            tagNb++;
                        }
                    }
                    boolean useTags = tagNb > 0;

                    AlbumInfo[] cds = localSearch();
                    int[] score = new int[cds.length];
                    int i = 0;
                    for (AlbumInfo cd : cds)
                    {
                        String[] titleArtist = cd.title.split("-");
                        if (titleArtist.length >= 2) {
                            cd.artist = cd.title.split("-")[0].trim();
                        }
                        else{
                            cd.artist = "UnknownArtist";
                        }
                        score[i] = 0;

                        boolean artistMatch = false;
                        boolean albumMatch = false;

                        if (useArtistName)
                        {
                            if (cd.artist.toLowerCase().contains(artistNameStr))
                            {
                                score[i] += 10;
                                artistMatch = true;
                            }
                            else
                            {
                                artistMatch = false;
                            }
                        }

                        if (useAlbumName)
                        {
                            if (cd.title.toLowerCase().contains(albumNameStr))
                            {
                                score[i] += 10;
                                albumMatch = true;
                            }
                            else
                            {
                                albumMatch = false;
                            }
                        }
                        else
                        {
                        }

                        if (useTags)
                        {
                            for (String tag : tags)
                            {
                                if (cd.imageLabels != null && cd.imageLabels.length > 0) {
                                    for (String tagToCompare : cd.imageLabels) {
                                        if ((!useAlbumName || albumMatch) && (!useArtistName || artistMatch)) {
                                            if (tag.equals(tagToCompare)) {
                                                score[i]++;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        i++;
                    }

                    boolean sorted = false;
                    int arrayLen = score.length;

                    while (!sorted)
                    {
                        sorted = true;
                        for ( i = 0; i < arrayLen-1; i++)
                        {
                            if (score[i] < score[i+1])
                            {
                                int tmp = score[i];
                                score[i] = score[i+1];
                                score[i+1] = tmp;
                                AlbumInfo tmpCd;
                                tmpCd = cds[i];
                                cds[i] = cds[i+1];
                                cds[i+1] = tmpCd;
                                sorted = false;
                            }
                        }
                    }

                    if (arrayLen == 0 || (useAlbumName && useArtistName && score[0] < 20)||(score[0] == 0 && (useAlbumName || useArtistName))) {
                        String requestUrl = "https://api.discogs.com/database/search?artist=" + URLEncoder.encode(artistName.getText().toString(), "utf-8") + "&release_title=" + URLEncoder.encode(albumName.getText().toString(), "utf-8") + "&format=album&key=nNJIcOOiuEIRgXEIaOix&secret=AANxnfWVQStJWCjIdLCnVUpDdiAnTCLP";

                        DiscogsSearchService.SearchDiscogsDatabase(FindCDActivity.this, requestUrl);

                    }
                    else
                    {
                        ArrayList<AlbumInfo> foundAlbums = new ArrayList<>();
                        i = 0;
                        while (i < arrayLen && (score[i] > 0 || (!useAlbumName && !useArtistName && !useTags)))
                        {
                            foundAlbums.add(cds[i]);
                            i++;
                        }
                        AlbumInfo[] albumInfos = foundAlbums.toArray(new AlbumInfo[foundAlbums.size()]);
                        String albuminfojson = AddCdToDatabaseActivity.JSONCreation(albumInfos);

                        Intent intent = new Intent(FindCDActivity.this, CdListActivity.class);
                        intent.putExtra("albuminfojson", albuminfojson);

                        startActivity(intent);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        this.addBroadcastListeners();
    }

    protected AlbumInfo[] localSearch()
    {
        String artistNameStr = this.artistName.getText().toString();
        String albumNameStr = this.albumName.getText().toString();
        String tagsStr = this.tag.getText().toString();

        String databaseStr = AddCdToDatabaseActivity.ReadFromDatabase(FindCDActivity.this);

        AlbumInfo[] albuminfos = AddCdToDatabaseActivity.ReadResultForAlbumInfo(databaseStr);
        return albuminfos;
    }

    protected void addBroadcastListeners() {

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String response = intent.getStringExtra("response");
                AlbumInfo[] albums = AddCdToDatabaseActivity.ReadResult(response);
                if (albums == null || albums.length < 1)
                {
                    Intent failIntent = new Intent(FindCDActivity.this, FindCDActivity.class);
                    Toast.makeText(FindCDActivity.this, "Couldn't find cd in local or web database.", Toast.LENGTH_LONG).show();
                    startActivity(failIntent);
                }
                Intent intentAddToDatabase = new Intent(FindCDActivity.this, AddCdToDatabaseActivity.class);

                intentAddToDatabase.putExtra("albuminfojson", AddCdToDatabaseActivity.JSONCreation(albums));

                startActivity(intentAddToDatabase);


            }
        };
    }

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(DiscogsSearchService.DISCOGS_API_RESULT_BROADCAST_CHANNEL));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


}
