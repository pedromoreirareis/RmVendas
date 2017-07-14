package com.pedromoreirareisgmail.rmvendas.Utils;


import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Switch;

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
        double valorC = Double.parseDouble(vlC);
        double valorD = Double.parseDouble(vlD);

        double quantValor = valorQ * vlBolo;

        double total = quantValor + valorC + valorD;

        return String.valueOf(total);
    }
}
