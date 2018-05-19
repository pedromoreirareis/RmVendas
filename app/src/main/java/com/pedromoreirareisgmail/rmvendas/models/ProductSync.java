package com.pedromoreirareisgmail.rmvendas.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductSync implements Parcelable{

    private long id;
    private String name;
    private double price;

    public ProductSync(){

    }

    public ProductSync(long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    protected ProductSync(Parcel in) {
        id = in.readLong();
        name = in.readString();
        price = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeDouble(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductSync> CREATOR = new Creator<ProductSync>() {
        @Override
        public ProductSync createFromParcel(Parcel in) {
            return new ProductSync(in);
        }

        @Override
        public ProductSync[] newArray(int size) {
            return new ProductSync[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
