package com.pedromoreirareisgmail.rmvendas.Utils;


import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Switch;

import java.text.NumberFormat;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utilidades {

    public static void fecharTecladoSwitch(Context context, Switch switchCD) {

        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(switchCD.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String calculaValorBolo(String vlQ, String vlC, String vlD, double vlBolo) {

        if (TextUtils.isEmpty(vlQ)) {

            vlQ = "0";
        }

        if (TextUtils.isEmpty(vlC)) {

            vlC = "0";
        }

        if (TextUtils.isEmpty(vlD)) {

            vlD = "0";
        }

        double valorQ = Double.parseDouble(vlQ);
        double valorC = Double.parseDouble(vlC) / 100;
        double valorD = Double.parseDouble(vlD) / 100;

        double quantValor = valorQ * vlBolo;

        double total = quantValor + valorC - valorD;

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        return preco.format(total);

        //return String.valueOf(total);
    }

    public static double calculaValorBoloDouble(double vlBolo, double valorQ, double valorC, double valorD) {


        double quantValor = valorQ * vlBolo;

        return quantValor + valorC - valorD;

    }

}
