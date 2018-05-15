package com.pedromoreirareisgmail.rmvendas.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {


    private long id;
    private Uri uri;
    private String name;
    private double price;
    private Boolean addSell;

    public Product() {

    }

    public Product(long id, Uri uri, String name, double price, Boolean addSell) {
        this.id = id;
        this.uri = uri;
        this.name = name;
        this.price = price;
        this.addSell = addSell;
    }

    protected Product(Parcel in) {
        id = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        name = in.readString();
        price = in.readDouble();
        byte tmpAddSell = in.readByte();
        addSell = tmpAddSell == 0 ? null : tmpAddSell == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(uri, flags);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeByte((byte) (addSell == null ? 0 : addSell ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Boolean getAddSell() {
        return addSell;
    }

    public void setAddSell(Boolean addSell) {
        this.addSell = addSell;
    }
}
