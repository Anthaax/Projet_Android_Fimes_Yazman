package com.example.guillaume.findyourcd;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;


public class AddCdActivity extends AppCompatActivity {

    RadioGroup cdGenreRadioGrp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.add_cd);

        this.loadComponents();
    }

    protected void loadComponents()
    {
        this.cdGenreRadioGrp = findViewById(R.id.radio_group_genre);
    }
}
