package com.pedromoreirareisgmail.rmvendas.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.pedromoreirareisgmail.rmvendas.R;

public class PrefsUser {


    public static String getCompanyName(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(context.getString(R.string.pref_key_compnay_name), "");
    }

    public static String getCompanyCnpj(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(context.getString(R.string.pref_key_cnpj), "");
    }

    public static String getCompanyCpf(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(context.getString(R.string.pref_key_cpf), "");
    }

}
