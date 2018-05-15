package com.pedromoreirareisgmail.rmvendas.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Opening implements Parcelable{

    private long id;
    private Uri uri;
    private String timestamp;
    private double value;

    public Opening(){}

    public Opening(long id, Uri uri, String timestamp, double value) {
        this.id = id;
        this.uri = uri;
        this.timestamp = timestamp;
        this.value = value;
    }

    protected Opening(Parcel in) {
        id = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        timestamp = in.readString();
        value = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(uri, flags);
        dest.writeString(timestamp);
        dest.writeDouble(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Opening> CREATOR = new Creator<Opening>() {
        @Override
        public Opening createFromParcel(Parcel in) {
            return new Opening(in);
        }

        @Override
        public Opening[] newArray(int size) {
            return new Opening[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
