package com.example.nicolas.firstandroidproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Nicolas on 27/01/2018.
 */

public class CdParcelable implements Parcelable {
    private ArrayList<String> labels;
    private ArrayList<String> texts;
    private ArrayList<String> landmarks;


    // Constructor
    public CdParcelable(ArrayList<String> labels, ArrayList<String> texts, ArrayList<String> landmarks)
    {
        this.labels = labels;
        this.texts = texts;
        this.landmarks = landmarks;
    }

    // Parcelable part
    protected CdParcelable(Parcel in) {
        Object[] labelObjs = in.readArray(getClass().getClassLoader());
        Object[] textObjs = in.readArray(getClass().getClassLoader());
        Object[] landmarkObjs = in.readArray(getClass().getClassLoader());
        this.labels = new ArrayList<>(Arrays.asList(Arrays.copyOf(labelObjs, labelObjs.length, String[].class)));
        this.texts = new ArrayList<>(Arrays.asList(Arrays.copyOf(textObjs, textObjs.length, String[].class)));
        this.landmarks = new ArrayList<>(Arrays.asList(Arrays.copyOf(landmarkObjs, landmarkObjs.length, String[].class)));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(labels.toArray());
        dest.writeArray(texts.toArray());
        dest.writeArray(landmarks.toArray());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CdParcelable> CREATOR = new Creator<CdParcelable>() {
        @Override
        public CdParcelable createFromParcel(Parcel in) {
            return new CdParcelable(in);
        }

        @Override
        public CdParcelable[] newArray(int size) {
            return new CdParcelable[size];
        }
    };

    public ArrayList<String> getLabels()
    {
        return this.labels;
    }

    public ArrayList<String> getTexts()
    {
        return this.texts;
    }

    public ArrayList<String> getLandmarks()
    {
        return this.landmarks;
    }
}
