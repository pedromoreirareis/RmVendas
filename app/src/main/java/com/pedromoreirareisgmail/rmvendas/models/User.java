package com.pedromoreirareisgmail.rmvendas.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String name;
    private String cpf;
    private String fone;
    private String companyId;
    private String companyName;
    private String token;

    public User(){

    }

    public User(String name, String cpf, String fone, String companyId, String companyName, String token) {
        this.name = name;
        this.cpf = cpf;
        this.fone = fone;
        this.companyId = companyId;
        this.companyName = companyName;
        this.token = token;
    }

    protected User(Parcel in) {
        name = in.readString();
        cpf = in.readString();
        fone = in.readString();
        companyId = in.readString();
        companyName = in.readString();
        token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(cpf);
        dest.writeString(fone);
        dest.writeString(companyId);
        dest.writeString(companyName);
        dest.writeString(token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
