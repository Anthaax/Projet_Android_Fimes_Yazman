package com.example.nicolas.firstandroidproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Created by Nicolas on 27/01/2018.
 */

public class AddCdFillInActivity extends AppCompatActivity {


    private RadioGroup cdGenreRadioGrp;
    private CdParcelable cd;
    private EditText artistName;
    private EditText albumName;
    private Button swapArtistAlbumButton;
    private Button addCdToDatabaseButton;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_cd_fill_in);

        this.loadComponents();

        Intent cdIntent = getIntent();
        cd = cdIntent.getParcelableExtra("cd");

        if (cd.getTexts().size() == 1)
        {
            this.albumName.setText(cd.getTexts().get(0));
        }
       else if (cd.getTexts().size() >= 2)
       {
           this.albumName.setText(cd.getTexts().get(0));
           this.artistName.setText(cd.getTexts().get(1));
       }

       this.swapArtistAlbumButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String tmp = albumName.getText().toString();
               albumName.setText(artistName.getText());
               artistName.setText(tmp);
           }
       });

        this.addCdToDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, look up information on cd
                try {
                    String requestUrl = "https://api.discogs.com/database/search?artist=" + URLEncoder.encode(artistName.getText().toString(), "utf-8") + "&release_title=" + URLEncoder.encode(albumName.getText().toString(), "utf-8") + "&format=ambum&key=nNJIcOOiuEIRgXEIaOix&secret=AANxnfWVQStJWCjIdLCnVUpDdiAnTCLP";

                    DiscogsSearchService.SearchDiscogsDatabase(AddCdFillInActivity.this, requestUrl);
                }catch (UnsupportedEncodingException e){

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
                for (int i = 0; i < albums.length; i++)
                {
                    albums[i].imageLabels = Arrays.copyOf(cd.getLabels().toArray(), cd.getLabels().toArray().length, String[].class);
                }
                Intent intentAddToDatabase = new Intent(AddCdFillInActivity.this, AddCdToDatabaseActivity.class);

                intentAddToDatabase.putExtra("albuminfojson", AddCdToDatabaseActivity.JSONCreation(albums));

                startActivity(intentAddToDatabase);
            }
        };

        //..more logic
    }

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(DiscogsSearchService.DISCOGS_API_RESULT_BROADCAST_CHANNEL));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    protected void loadComponents()
    {
        this.cdGenreRadioGrp = findViewById(R.id.radio_group_genre);
        this.artistName = findViewById(R.id.text_input_artist);
        this.albumName = findViewById(R.id.text_input_cdname);
        this.swapArtistAlbumButton = findViewById(R.id.swap_artist_album);
        this.addCdToDatabaseButton = findViewById(R.id.add_cd_to_database);
    }

    private final void createNotification(){
        final int NOTIFICATION_ID = 001;
        final NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final Intent launchNotifiactionIntent = new Intent(this, AddCdFillInActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.notification_title))
                .setContentText(getResources().getString(R.string.notification_content))
                .setContentIntent(pendingIntent);

        mNotification.notify(NOTIFICATION_ID, builder.build());
    }
}
