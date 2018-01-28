package com.example.nicolas.firstandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

/**
 * Created by Nicolas on 27/01/2018.
 */

public class AddCdFillInActivity extends AppCompatActivity {


    private RadioGroup cdGenreRadioGrp;
    private CdParcelable cd;
    private EditText artistName;
    private EditText albumName;
    private Button swapArtistAlbumButton;

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
    }

    protected void loadComponents()
    {
        this.cdGenreRadioGrp = findViewById(R.id.radio_group_genre);
        this.artistName = findViewById(R.id.text_input_artist);
        this.albumName = findViewById(R.id.text_input_cdname);
        this.swapArtistAlbumButton = findViewById(R.id.swap_artist_album);
    }
}
