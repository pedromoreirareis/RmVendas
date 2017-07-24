package com.pedromoreirareisgmail.rmvendas.Utils;

import java.text.NumberFormat;

public class Formatar {

    private static final NumberFormat nFormat = NumberFormat.getCurrencyInstance();


    public static String emCurrency(String str) {

        boolean hasMask =
                (((str.contains("R$")) || (str.contains("$"))) &&
                        ((str.contains(".")) || (str.contains(","))));

        if (hasMask) {

            str = str.replaceAll("[^\\d]", "");
        }

        try {

            return nFormat.format(Double.parseDouble(str) / 100);

        } catch (NumberFormatException e) {

            return "";
        }
    }

    public static double emDouble(String str) {

        boolean hasMask =
                (((str.contains("R$")) || (str.contains("$"))) &&
                        ((str.contains(".")) || (str.contains(","))));

        if (hasMask) {
            str = str.replaceAll("[^\\d]", "");
        }

        try {

            return (Double.parseDouble(str) / 100);

        } catch (NumberFormatException e) {

            return 0;
        }

    }

}
