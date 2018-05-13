package com.pedromoreirareisgmail.rmvendas.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class SellToClient implements Parcelable {

    private Uri uriInitial;
    private Double unitValue;
    private Long clientId;

    public SellToClient(){

    }

    public SellToClient(Uri uriInitial, Double unitValue, Long clientId) {
        this.uriInitial = uriInitial;
        this.unitValue = unitValue;
        this.clientId = clientId;
    }

    protected SellToClient(Parcel in) {
        uriInitial = in.readParcelable(Uri.class.getClassLoader());
        if (in.readByte() == 0) {
            unitValue = null;
        } else {
            unitValue = in.readDouble();
        }
        if (in.readByte() == 0) {
            clientId = null;
        } else {
            clientId = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uriInitial, flags);
        if (unitValue == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(unitValue);
        }
        if (clientId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(clientId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SellToClient> CREATOR = new Creator<SellToClient>() {
        @Override
        public SellToClient createFromParcel(Parcel in) {
            return new SellToClient(in);
        }

        @Override
        public SellToClient[] newArray(int size) {
            return new SellToClient[size];
        }
    };

    public Uri getUriInitial() {
        return uriInitial;
    }

    public void setUriInitial(Uri uriInitial) {
        this.uriInitial = uriInitial;
    }

    public Double getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(Double unitValue) {
        this.unitValue = unitValue;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
