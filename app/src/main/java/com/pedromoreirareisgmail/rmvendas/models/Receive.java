package com.pedromoreirareisgmail.rmvendas.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Receive implements Parcelable{

    private long id;
    private Uri uri;
    private long clientId;
    private String clientName;
    private String timestamp;
    private int type;
    private String description;
    private Double value;

    public Receive() {
    }

    public Receive(long id, Uri uri, long clientId, String clientName, String timestamp, int type, String description, Double value) {
        this.id = id;
        this.uri = uri;
        this.clientId = clientId;
        this.clientName = clientName;
        this.timestamp = timestamp;
        this.type = type;
        this.description = description;
        this.value = value;
    }

    protected Receive(Parcel in) {
        id = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        clientId = in.readLong();
        clientName = in.readString();
        timestamp = in.readString();
        type = in.readInt();
        description = in.readString();
        if (in.readByte() == 0) {
            value = null;
        } else {
            value = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(uri, flags);
        dest.writeLong(clientId);
        dest.writeString(clientName);
        dest.writeString(timestamp);
        dest.writeInt(type);
        dest.writeString(description);
        if (value == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(value);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Receive> CREATOR = new Creator<Receive>() {
        @Override
        public Receive createFromParcel(Parcel in) {
            return new Receive(in);
        }

        @Override
        public Receive[] newArray(int size) {
            return new Receive[size];
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

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
