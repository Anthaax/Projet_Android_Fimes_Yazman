package com.example.nicolas.firstandroidproject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Nicolas on 05/01/2018.
 */

public class BeerService extends IntentService {

    private static final int GET_ALL_BEERS = 1;

    public BeerService()
    {
        super("BeerService");
    }
    public BeerService(String name) {
        super(name);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    public static void GetBeers(Activity activity)
    {
        BeerService.GetBeers(activity.getBaseContext());
    }

    public static void GetBeers(Context context)
    {
        Intent intent = new Intent(context, BeerService.class);
        intent.putExtra("command", GET_ALL_BEERS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null)
        {
            int command = intent.getIntExtra("command", 0);
            switch (command)
            {
               case GET_ALL_BEERS:
                   getAllBeers();
                default:
                    break;
            }
        }
    }

    protected void getAllBeers()
    {
        Intent intent = new Intent();
        intent.setAction("testalacon");
        sendBroadcast(intent);
    }
}
