package com.example.nicolas.firstandroidproject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by Nicolas on 14/01/2018.
 */

public class CDImageAnalysisService extends IntentService {

    public CDImageAnalysisService()
    {
        super("CDImageAnalysisService");
    }
    public CDImageAnalysisService(String name) {
        super(name);
    }


    public static final int ANALYSE_PICTURE_AND_GET_RESULTS = 1;
    public static final int ANALYSE_PICTURE_AND_ADD_RESULTS_TO_DATABASE = 2;
    public static final int ANALYSE_PICTURE_AND_GET_RESULTS_AND_ADD_TO_DATABASE = 3;
    public static final int MIN_COMMAND_NB = 1;
    public static final int NUMBER_OF_COMMANDS = 3;

    private static final int IMG_SRC_URL = 1;
    private static final int IMG_SRC_BITMAP = 2;

    public static final String LOG_TAG = "CDImageAnalysisService";
    public static final String IMAGE_API_RESULT_BROADCAST_CHANNEL = "ImageApiResult";

    public static final String CD_EXTRA_COMMAND = "command";
    public static final String CD_EXTRA_IMGSRC = "imgSrc";
    public static final String CD_EXTRA_IMG = "img";
    public static final String CD_EXTRA_ACCESS_TOKEN = "accessToken";
    public static final String CD_IMG_RECON_EXTRA_RES = "imgRecognitionResult";
    public static final String NULL_TXT = "null";

    public static Bitmap pictureAnalysis(Context context, String url, int command, String accessToken)
    {
        URI imgUri;

        if (!basicErrorCheck(context, command, accessToken)) return null;
        if (url == null)
        {
            Log.e(TAG, "pictureAnalysis: Url null");
            Toast.makeText(context, "Url null", Toast.LENGTH_LONG);
            return null;
        }

        try {
            // Download Image from URL
            InputStream input = new java.net.URL(url).openStream();
            // Decode Bitmap
            Bitmap image = BitmapFactory.decodeStream(input);
            image = resizeBitmap(image);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            analysePicture(context, byteArray, command, IMG_SRC_URL, accessToken, url);

            return image;
           // Toast.makeText(context, "Correct URL", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Incorrect URL", Toast.LENGTH_LONG);
        }
        return null;
    }



    public static Bitmap pictureAnalysis(Context context,  Bitmap image, int command, String accessToken)
    {
        if (!basicErrorCheck(context, command, accessToken)) return null;
        if (image == null)
        {
            Log.e(TAG, "pictureAnalysis: image null");
            Toast.makeText(context, "Image null", Toast.LENGTH_LONG);
            return null;
        }


        image = resizeBitmap(image);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

       analysePicture(context, byteArray, command, IMG_SRC_BITMAP, accessToken, "local");

       return image;
    }

    private static boolean basicErrorCheck(Context context, int commandNb, String accessToken)
    {
        if (context == null)
        {
            Log.e(TAG, "pictureAnalysis: context null.");
            return false;
        }
        if (commandNb < MIN_COMMAND_NB || commandNb >= MIN_COMMAND_NB+NUMBER_OF_COMMANDS)
        {
            Log.e(TAG, "pictureAnalysis: invalid command number.");
            Toast.makeText(context, "PictureAnalysis: invalid command number: " + commandNb, Toast.LENGTH_LONG);
            return false;
        }
        if (accessToken == null)
        {
            Log.e(TAG, "pictureAnalysis: accessToken null.");
            Toast.makeText(context, "PictureAnalysis: accessToken null: ", Toast.LENGTH_LONG);
        }
        return true;
    }


    private static void analysePicture(Context context, Object img, int analysisCommand, int imgSource, String accessToken, Object extra)
    {
        Intent intent = new Intent(context, CDImageAnalysisService.class);
        intent.putExtra(CD_EXTRA_COMMAND, analysisCommand);
        intent.putExtra(CD_EXTRA_IMGSRC, imgSource);
        intent.putExtra(CD_EXTRA_ACCESS_TOKEN, accessToken);
        switch (imgSource)
        {
            case IMG_SRC_BITMAP:
                intent.putExtra(CD_EXTRA_IMG, (byte[])img);
              break;
            case IMG_SRC_URL:
                intent.putExtra(CD_EXTRA_IMG, (byte[])img);
                intent.putExtra("url", (String)extra);
                break;
        }

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
            Bitmap imgbitmap;

            int command = intent.getIntExtra(CD_EXTRA_COMMAND, -1);
            int imgSrc = intent.getIntExtra(CD_EXTRA_IMGSRC, -1);
            String accessToken = intent.getStringExtra(CD_EXTRA_ACCESS_TOKEN);
            if (accessToken == null)
            {
                Log.e(TAG, "onHandleIntent: accessToken null");
                return;
            }

            if (imgSrc == IMG_SRC_URL)
            {
                byte[] byteArray = intent.getByteArrayExtra(CD_EXTRA_IMG);
                imgbitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }
            else
            {
                byte[] byteArray = intent.getByteArrayExtra(CD_EXTRA_IMG);
                imgbitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }
            handleCommand(accessToken, imgbitmap, command);
    }

    protected void handleCommand(String accessToken, Bitmap bitmap, int commandNb)
    {
        CdParcelable res = callGoogleCloudVision(accessToken, bitmap);
        Intent intent = new Intent();
        if (res == null)
            intent.putExtra(CD_IMG_RECON_EXTRA_RES, (Parcelable)null);
        else
            intent.putExtra(CD_IMG_RECON_EXTRA_RES, res);
        intent.setAction(IMAGE_API_RESULT_BROADCAST_CHANNEL);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //sendBroadcast(intent);
    }

    protected CdParcelable callGoogleCloudVision(String accessToken, Bitmap bitmap)
    {
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            Vision.Builder builder = new Vision.Builder
                    (httpTransport, jsonFactory, credential);
            Vision vision = builder.build();

            List<Feature> featureList = new ArrayList<>();
            Feature labelDetection = new Feature();
            labelDetection.setType("LABEL_DETECTION");
            labelDetection.setMaxResults(20);
            featureList.add(labelDetection);

            Feature textDetection = new Feature();
            textDetection.setType("TEXT_DETECTION");
            textDetection.setMaxResults(20);
            featureList.add(textDetection);

            Feature landmarkDetection = new Feature();
            landmarkDetection.setType("LANDMARK_DETECTION");
            landmarkDetection.setMaxResults(20);
            featureList.add(landmarkDetection);

            List<AnnotateImageRequest> imageList = new ArrayList<>();
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
            Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
            annotateImageRequest.setImage(base64EncodedImage);
            annotateImageRequest.setFeatures(featureList);
            imageList.add(annotateImageRequest);

            BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                    new BatchAnnotateImagesRequest();
            batchAnnotateImagesRequest.setRequests(imageList);

            Vision.Images.Annotate annotateRequest =
                    vision.images().annotate(batchAnnotateImagesRequest);
            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            annotateRequest.setDisableGZipContent(true);
            Log.d(LOG_TAG, "sending request");

            BatchAnnotateImagesResponse response = annotateRequest.execute();
            return convertResponseToCdParcelable(response);
        } catch (IOException e)
        {
            return null;
        }
    }

    private CdParcelable convertResponseToCdParcelable(BatchAnnotateImagesResponse response) {
        ArrayList<String> labelAnnotations = new ArrayList<>();
        ArrayList<String> textAnnotations = new ArrayList<>();
        ArrayList<String> landmarkAnnotations = new ArrayList<>();

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                labelAnnotations.add(label.getDescription());
            }
        }

        List<EntityAnnotation> texts = response.getResponses().get(0).getTextAnnotations();
        if (texts != null) {
            for (EntityAnnotation text : texts) {
               textAnnotations.add(text.getDescription());
            }
        }

        List<EntityAnnotation> landmarks = response.getResponses().get(0).getLandmarkAnnotations();
        if (landmarks != null) {
            for (EntityAnnotation landmark : landmarks) {
                 landmarkAnnotations.add(landmark.getDescription());
            }
        }

        return new CdParcelable(labelAnnotations, textAnnotations, landmarkAnnotations);
    }

    // ------------------------------- Image Conversion Operations ---------------------------------------
    public static Bitmap resizeBitmap(Bitmap bitmap) {

        int maxDimension = 1024;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }
}
