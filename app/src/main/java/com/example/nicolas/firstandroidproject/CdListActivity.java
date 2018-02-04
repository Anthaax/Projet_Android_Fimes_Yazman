package com.example.nicolas.firstandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;


public class CdListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CdAdapter cdAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cd_list);

        Intent intent = getIntent(); // Intent which started this activity
        String albumInfoStr = intent.getStringExtra("albuminfojson");
        final AlbumInfo[] albumInfos = AddCdToDatabaseActivity.ReadResultForAlbumInfo(albumInfoStr);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        cdAdapter = new CdAdapter(albumInfos, CdListActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(cdAdapter);


        this.cdAdapter.setOnItemClickListener(new OnItemClickListener(){

            public void onItemClick(View view, int position, String id){
                Intent intent = new Intent(CdListActivity.this, CdInformationActivity.class);
                AlbumInfo selectedAlbum = albumInfos[position];
                AlbumInfo[] selectedAlbumInArray = new AlbumInfo[1];
                selectedAlbumInArray[0] = selectedAlbum;

                String json = AddCdToDatabaseActivity.JSONCreation(selectedAlbumInArray);
                intent.putExtra("albuminfojson", json);
                startActivity(intent);
            }
                                             }

        );



    }
}
