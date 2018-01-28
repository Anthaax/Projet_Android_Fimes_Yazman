package com.example.nicolas.firstandroidproject;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

/**
 * Created by Nicolas on 07/01/2018.
 */

public class FindCDActivity  extends AppCompatActivity {


    RadioGroup genreRadioGrp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.find_cd);

        this.loadComponents();
        this.addListeners();

        int checkedGenre = this.genreRadioGrp.getCheckedRadioButtonId();


    }

    protected void loadComponents()
    {
        this.genreRadioGrp = findViewById(R.id.radio_group_genre);

    }

    protected void addListeners()
    {

    }


}
