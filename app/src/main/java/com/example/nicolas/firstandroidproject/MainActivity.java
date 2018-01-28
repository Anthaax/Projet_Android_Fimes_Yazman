package com.example.nicolas.firstandroidproject;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by Nicolas on 06/01/2018.
 */

public class MainActivity extends AppCompatActivity {

    private Button addCdButton;
    private Button searchForCdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.addCdButton = (Button) findViewById(R.id.add_cd_button);
        this.searchForCdButton = (Button) findViewById(R.id.search_cd_button);

        addCdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCDActivity.class);
                startActivity(intent);
            }
        });

        searchForCdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FindCDActivity.class);
                startActivity(intent);
            }
        });

    }

    protected void onResume(){
        super.onResume();
    }

    protected void onPause (){
        super.onPause();
     }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId)
        {
            case R.id.search_cd_menu:
                break;
            case R.id.add_cd_menu:
                break;
        }
        return true;
    }


}
