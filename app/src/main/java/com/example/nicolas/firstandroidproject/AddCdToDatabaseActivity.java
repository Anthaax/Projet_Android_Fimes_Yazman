package com.example.nicolas.firstandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
}
