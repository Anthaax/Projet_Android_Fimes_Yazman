package com.example.nicolas.firstandroidproject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.api.client.util.StringUtils;

import java.util.ArrayList;

/**
 * Created by Nicolas on 03/02/2018.
 */

public class CdInformationActivity extends AppCompatActivity {

    private Button removeFromDatabaseButton;
    private TextView artist;
    private TextView album;
    private TextView format;
    private TextView barcode;
    private TextView style;
    private TextView genre;
    private TextView have;
    private TextView want;
    private TextView imageLabels;
    private TextView country;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cd_information);

        Intent creationIntent = getIntent();
        String albumInfoJson = creationIntent.getStringExtra("albuminfojson");
        AlbumInfo[] albuminfos = AddCdToDatabaseActivity.ReadResultForAlbumInfo(albumInfoJson);
        if  (albumInfoJson.length() < 1)
        {
            return;
        }
        final AlbumInfo albuminfo = albuminfos[0];
        this.loadComponents();

        this.setTextViews(albuminfo);

        this.removeFromDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumInfo[] albumsFromDatabase = AddCdToDatabaseActivity.ReadResultForAlbumInfo(AddCdToDatabaseActivity.ReadFromDatabase(CdInformationActivity.this));

                ArrayList<AlbumInfo> albuminfoarraylist = new ArrayList<>();
                for (AlbumInfo ai : albumsFromDatabase)
                {
                    if (ai.id.intValue() != albuminfo.id.intValue())
                    {
                        albuminfoarraylist.add(ai);
                    }
                }
                AlbumInfo[] newalbuminfos = albuminfoarraylist.toArray(new AlbumInfo[(albuminfoarraylist.size())]);
                AddCdToDatabaseActivity.OverwriteDatabase(newalbuminfos, CdInformationActivity.this);

                Intent intent = new Intent(CdInformationActivity.this, FindCDActivity.class);
                Toast.makeText(CdInformationActivity.this, "Removed cd.", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });
    }

    public void loadComponents()
    {
        this.artist = findViewById(R.id.artist_name_text_view);
        this.album = findViewById(R.id.album_name_text_view);
        this.format = findViewById(R.id.format_text_view);
        this.barcode = findViewById(R.id.barcode_text_view);
        this.style = findViewById(R.id.style_text_view);
        this.genre = findViewById(R.id.genre_text_view);
        this.have = findViewById(R.id.have_text_view);
        this.want = findViewById(R.id.want_text_view);
        this.imageLabels = findViewById(R.id.image_labels_text_view);
        this.country = findViewById(R.id.country_text_view);
        this.removeFromDatabaseButton = findViewById(R.id.delete_from_database_button);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        Intent intent;
        switch (itemId)
        {
            case R.id.search_cd_menu:
                intent = new Intent(this, FindCDActivity.class);
                startActivity(intent);
                break;
            case R.id.add_cd_menu:
                intent = new Intent(this, AddCDActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void setTextViews(AlbumInfo albumInfo)
    {
        if (albumInfo.artist == null || albumInfo.artist.isEmpty()) {

            String[] decompTitle = albumInfo.title.split("-");
            if (decompTitle.length >= 2) {
                albumInfo.artist = decompTitle[0];
            }else{
                albumInfo.artist = "UnknownArtist";
            }
        }
        this.artist.setText(albumInfo.artist);
        this.album.setText(albumInfo.title);
        this.format.setText(stringArrayTostring(albumInfo.format, ", "));
        this.genre.setText(stringArrayTostring(albumInfo.genre, ", "));
        this.barcode.setText(stringArrayTostring(albumInfo.barcode, " "));
        if (albumInfo.imageLabels != null && albumInfo.imageLabels.length >= 1)
            this.imageLabels.setText(stringArrayTostring(albumInfo.imageLabels, ", "));
        else
            this.imageLabels.setText("No image label found.");
        this.want.setText(String.valueOf(albumInfo.community.want));
        this.have.setText(String.valueOf(albumInfo.community.have));
        this.country.setText(albumInfo.country);
        this.style.setText(stringArrayTostring(albumInfo.style,", "));
        ImageView image = findViewById(R.id.image_cd);
        Glide.with(CdInformationActivity.this).load(albumInfo.thumb).into(image);


        TextView[] allTextViews = new TextView[10];
        allTextViews[0] = this.artist;
        allTextViews[1] = this.album;
        allTextViews[2] = this.format;
        allTextViews[3] = this.genre;
        allTextViews[4] = this.style;
        allTextViews[5] = this.barcode;
        allTextViews[6] = this.imageLabels;
        allTextViews[7] = this.want;
        allTextViews[8] = this.have;
        allTextViews[9] = this.country;
        for (TextView tv : allTextViews){
            String text = tv.getText().toString();
            if (text.length() > 30)
            {
                final String cpy = text;
                tv.setText(text.substring(0,30)+"...");
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CdInformationActivity.this, cpy, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

    }

    public static String stringArrayTostring(String[] strArray, String delim)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strArray.length; i++){
            builder.append(strArray[i]);
            if (i == strArray.length-1)
                return builder.toString();
            builder.append(delim);
        }
        return builder.toString();
    }
}
