package com.pedromoreirareisgmail.rmvendas.Fire;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pedromoreirareisgmail.rmvendas.constant.ConstFire;

public class Fire {


    private static String deviceToken;
    private static FirebaseAuth auth;
    private static FirebaseUser user;


    private static CollectionReference refColProduct;
    private static CollectionReference refColUser;


    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  Gets @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    public static String getDeviceToken() {

        if (deviceToken == null) {

            deviceToken = FirebaseInstanceId.getInstance().getToken();
        }
        return deviceToken;
    }


    public static FirebaseAuth getAuth() {

        if (auth == null) {

            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }


    public static FirebaseUser getUser() {

        if (user == null) {

            user = getAuth().getCurrentUser();
        }

        return user;
    }


    public static CollectionReference getRefColProduct(String CompanyID) {

        if (refColProduct == null) {

            refColProduct = FirebaseFirestore.getInstance().collection(ConstFire.FIRE_COL_COMPANY).document(CompanyID).collection(ConstFire.FIRE_COL_PRODUCT);
        }

        return refColProduct;
    }


    public static CollectionReference getRefColUser(String CompanyID) {

        if (refColUser == null) {

            refColUser = FirebaseFirestore.getInstance().collection(ConstFire.FIRE_COL_COMPANY).document(CompanyID).collection(ConstFire.FIRE_COL_USERS);
        }

        return refColUser;
    }


    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  Sets @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    public static void setDeviceToken() {

        Fire.deviceToken = null;
    }

    public static void setAuth() {
        Fire.auth = null;
    }

    public static void setUser() {
        Fire.user = null;
    }


    public static void setRefColProduct() {
        Fire.refColProduct = null;
    }

    public static void setRefColUser() {
        Fire.refColUser = null;
    }
}
