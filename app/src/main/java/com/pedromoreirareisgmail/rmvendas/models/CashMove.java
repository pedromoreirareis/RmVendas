package com.pedromoreirareisgmail.rmvendas.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class CashMove implements Parcelable {

    private long id;
    private Uri uri;
    private String timestamp;
    private int type;
    private Double value;
    private String description;

    public CashMove() {
    }

    public CashMove(long id, Uri uri, String timestamp, int type, Double value, String description) {
        this.id = id;
        this.uri = uri;
        this.timestamp = timestamp;
        this.type = type;
        this.value = value;
        this.description = description;
    }

    protected CashMove(Parcel in) {
        id = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        timestamp = in.readString();
        type = in.readInt();
        if (in.readByte() == 0) {
            value = null;
        } else {
            value = in.readDouble();
        }
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(uri, flags);
        dest.writeString(timestamp);
        dest.writeInt(type);
        if (value == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(value);
        }
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CashMove> CREATOR = new Creator<CashMove>() {
        @Override
        public CashMove createFromParcel(Parcel in) {
            return new CashMove(in);
        }

        @Override
        public CashMove[] newArray(int size) {
            return new CashMove[size];
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
