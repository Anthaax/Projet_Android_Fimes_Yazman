package com.example.nicolas.firstandroidproject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Nicolas on 29/01/2018.
 */

public class DiscogsSearchService extends IntentService {

    public static final String DISCOGS_API_RESULT_BROADCAST_CHANNEL = "DISCOGS_RESULT";

    public DiscogsSearchService()
    {
        super("DiscogsSearchService");
    }
    public DiscogsSearchService(String name) {
        super(name);
    }

    public static void SearchDiscogsDatabase(Context context, String requestUrl)
    {
        Intent intent = new Intent(context, DiscogsSearchService.class);
        intent.putExtra("url", requestUrl);
        context.startService(intent);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
           String requestUrl = intent.getStringExtra("url");
           if (requestUrl != null){
               HttpClient httpclient = new DefaultHttpClient();

               try {
                   HttpGet request = new HttpGet();
                   URI uri = new URI(requestUrl);
                   request.setURI(uri);
                   HttpResponse response = httpclient.execute(request);
                   StatusLine statusLine = response.getStatusLine();
                   if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

                       ByteArrayOutputStream out = new ByteArrayOutputStream();
                       response.getEntity().writeTo(out);
                       String responseString = out.toString();

                       Intent resIntent = new Intent();
                       resIntent.putExtra("response", responseString);
                       resIntent.setAction(DISCOGS_API_RESULT_BROADCAST_CHANNEL);
                       LocalBroadcastManager.getInstance(this).sendBroadcast(resIntent);
                       out.close();
                   }



               } catch (IOException e){

               }catch (URISyntaxException e){

                 }
           }
        }
    }
}
