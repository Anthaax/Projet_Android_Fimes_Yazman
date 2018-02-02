package com.example.nicolas.firstandroidproject;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by Nicolas on 06/01/2018.
 */

public class AddCDActivity extends AppCompatActivity {
    private static String accessToken;
    static final int REQUEST_GALLERY_IMAGE = 10;
    static final int REQUEST_CODE_PICK_ACCOUNT = 11;
    static final int REQUEST_ACCOUNT_AUTHORIZATION = 12;
    static final int REQUEST_PERMISSIONS = 13;
    private final String LOG_TAG = "MainActivity";
    private ImageView selectedImage;
    private Button selectImageButton;
    private TextView resultTextView;
    private ArrayAdapter<String> labelAdapter;
    private GridView labelGrid;
    private EditText imageURL;
    private EditText addLabelEditText;

    private Button downloadImageButton;
    private Button addTagButton;
    private Button fillInDetailsButton;

    private int mode;
    private Bitmap cdimage;

    private AlertDialog.Builder builder;
    private int tagToDeletePosition;

    public static final int MODE_GALLERY = 0;
    public static final int MODE_URL = 1;
    Account mAccount;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cd);

        selectImageButton = (Button) findViewById(R.id.select_image_button);
        selectedImage = (ImageView) findViewById(R.id.selected_image);
        resultTextView = (TextView) findViewById(R.id.result);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_GALLERY;
                ActivityCompat.requestPermissions(AddCDActivity.this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        REQUEST_PERMISSIONS);
            }
        });

        addBroadcastReceivers();

        this.labelGrid = (GridView) findViewById(R.id.gridview);
        this.labelAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item1);
        this.labelGrid.setAdapter(labelAdapter);
        this.downloadImageButton = (Button) findViewById(R.id.download_image_url_button);
        this.imageURL = (EditText) findViewById(R.id.image_url);

        this.downloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = MODE_URL;
                ActivityCompat.requestPermissions(AddCDActivity.this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        REQUEST_PERMISSIONS);

            }
        });
        this.addTagButton = (Button) findViewById(R.id.add_tag_button);
        this.fillInDetailsButton = (Button) findViewById(R.id.fill_in_info_button);

        this.fillInDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddCDActivity.this, "Must first download cd from URL or upload from gallery.", Toast.LENGTH_SHORT).show();
            }
        });

        this.addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddCDActivity.this, "Wait until image loaded and labels found.", Toast.LENGTH_LONG).show();
            }
        });
        this.addTagButton.setClickable(true);

        this.addLabelEditText = (EditText) findViewById(R.id.add_tag_input_field);
    }

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(CDImageAnalysisService.IMAGE_API_RESULT_BROADCAST_CHANNEL));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
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

    protected void addBroadcastReceivers()
    {
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final CdParcelable imgReconRes = intent.getParcelableExtra(CDImageAnalysisService.CD_IMG_RECON_EXTRA_RES);

                int nLabels = imgReconRes.getLabels().size();
                labelAdapter.clear();
                labelAdapter.addAll(imgReconRes.getLabels());

                addTagButton.setVisibility(View.VISIBLE);
                addTagButton.setClickable(true);
                addTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String label = addLabelEditText.getText().toString();
                        label = label.trim();
                        boolean contains = false;
                        for (int i = 0; i < labelAdapter.getCount(); i++)
                        {
                            if (labelAdapter.getItem(i).equals(label))
                                contains = true;
                        }
                        if (!contains)
                        {
                            labelAdapter.add(label);
                        }
                        else
                        {
                            Toast.makeText(AddCDActivity.this, "Label already exists.", Toast.LENGTH_LONG);
                        }
                    }
                });
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                labelAdapter.remove(labelAdapter.getItem(tagToDeletePosition));
                                labelAdapter.notifyDataSetChanged();
                                //Yes button clicked
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                builder = new AlertDialog.Builder(AddCDActivity.this);
                builder.setMessage("Do you want to delete this tag?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener);

                labelGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        //  Toast.makeText(MainActivity.this, "" + position,
                        //          Toast.LENGTH_SHORT).show();

                        builder.setMessage(getString(R.string.confirmation_tag_deletion) + " " + labelAdapter.getItem(position));
                        tagToDeletePosition = position;
                        builder.show();
                    }
                });

                fillInDetailsButton.setClickable(true);
                fillInDetailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent addCdFillInIntent = new Intent(AddCDActivity.this, AddCdFillInActivity.class);
                        imgReconRes.getLabels().clear();
                        for (int i = 0; i < labelAdapter.getCount(); i++) {
                            imgReconRes.getLabels().add(labelAdapter.getItem(i));
                        }
                        addCdFillInIntent.putExtra("cd", imgReconRes);
                        startActivity(addCdFillInIntent);
                    }
                });
            }
        };
        // IntentFilter filter = new IntentFilter(CDImageAnalysisService.IMAGE_API_RESULT_BROADCAST_CHANNEL);
        //  registerReceiver(broadcastReceiver, filter);
    }

    private void launchImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"),
                REQUEST_GALLERY_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAuthToken();
                } else {
                    Toast.makeText(AddCDActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                AccountManager am = AccountManager.get(this);
                Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                for (Account account : accounts) {
                    if (account.name.equals(email)) {
                        mAccount = account;
                        break;
                    }
                }
                getAuthToken();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No Account Selected", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == REQUEST_ACCOUNT_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                onTokenReceived(extra.getString("authtoken"));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Authorization Failed", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                CDImageAnalysisService.pictureAnalysis(this.getApplicationContext(), bitmap, CDImageAnalysisService.ANALYSE_PICTURE_AND_GET_RESULTS, accessToken);

                selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            Log.e(LOG_TAG, "Null image was returned.");
        }
    }


    private void pickUserAccount() {
        String[] accountTypes = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);

        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    private void getAuthToken() {
        String SCOPE = "oauth2:https://www.googleapis.com/auth/cloud-platform";
        if (mAccount == null) {
            pickUserAccount();
        } else {
            new GetTokenTask(AddCDActivity.this, mAccount, SCOPE, REQUEST_ACCOUNT_AUTHORIZATION)
                    .execute();
        }
    }

    public void onTokenReceived(String token){
        accessToken = token;

        if (mode == MODE_GALLERY)
        {
            launchImagePicker();
        }
        else if (mode == MODE_URL)
        {
            String url = imageURL.getText().toString();
            cdimage = CDImageAnalysisService.pictureAnalysis(this, url, CDImageAnalysisService.ANALYSE_PICTURE_AND_GET_RESULTS, accessToken);
            if (cdimage != null)
                runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    selectedImage.setImageBitmap(cdimage);
                }
            });
            else
                Toast.makeText(AddCDActivity.this, "Error while loading image from url.", Toast.LENGTH_LONG).show();
        }
    }
}
