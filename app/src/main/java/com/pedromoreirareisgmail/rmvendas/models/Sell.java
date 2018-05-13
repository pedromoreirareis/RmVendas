package com.pedromoreirareisgmail.rmvendas.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Sell implements Parcelable{

    private long id;
    private Uri uri;
    private String timestamp;
    private String name;
    private Double price;
    private int quantity;
    private Double addValue;
    private Double discountValue;
    private Double forwardValue;
    private long clientId;
    private String clientName;
    private long receiveId;

    public Sell() {
    }

    public Sell(long id, Uri uri, String timestamp, String name, Double price, int quantity, Double addValue,
                Double discountValue, Double forwardValue, long clientId, String clientName, long receiveId) {
        this.id = id;
        this.uri = uri;
        this.timestamp = timestamp;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.addValue = addValue;
        this.discountValue = discountValue;
        this.forwardValue = forwardValue;
        this.clientId = clientId;
        this.clientName = clientName;
        this.receiveId = receiveId;
    }

    protected Sell(Parcel in) {
        id = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        timestamp = in.readString();
        name = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        quantity = in.readInt();
        if (in.readByte() == 0) {
            addValue = null;
        } else {
            addValue = in.readDouble();
        }
        if (in.readByte() == 0) {
            discountValue = null;
        } else {
            discountValue = in.readDouble();
        }
        if (in.readByte() == 0) {
            forwardValue = null;
        } else {
            forwardValue = in.readDouble();
        }
        clientId = in.readLong();
        clientName = in.readString();
        receiveId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(uri, flags);
        dest.writeString(timestamp);
        dest.writeString(name);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        dest.writeInt(quantity);
        if (addValue == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(addValue);
        }
        if (discountValue == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(discountValue);
        }
        if (forwardValue == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(forwardValue);
        }
        dest.writeLong(clientId);
        dest.writeString(clientName);
        dest.writeLong(receiveId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sell> CREATOR = new Creator<Sell>() {
        @Override
        public Sell createFromParcel(Parcel in) {
            return new Sell(in);
        }

        @Override
        public Sell[] newArray(int size) {
            return new Sell[size];
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getAddValue() {
        return addValue;
    }

    public void setAddValue(Double addValue) {
        this.addValue = addValue;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Double getForwardValue() {
        return forwardValue;
    }

    public void setForwardValue(Double forwardValue) {
        this.forwardValue = forwardValue;
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

    public long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(long receiveId) {
        this.receiveId = receiveId;
    }
}
