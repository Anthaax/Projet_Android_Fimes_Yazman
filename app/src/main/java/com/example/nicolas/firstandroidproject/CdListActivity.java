package com.example.nicolas.firstandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public class CdListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CdAdapter cdAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cd_list);

        Intent intent = getIntent(); // Intent which started this activity
        String albumInfoStr = intent.getStringExtra("albuminfojson");
        AlbumInfo[] albumInfos = AddCdToDatabaseActivity.ReadResultForAlbumInfo(albumInfoStr);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        cdAdapter = new CdAdapter(albumInfos);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(cdAdapter);
    }
}
