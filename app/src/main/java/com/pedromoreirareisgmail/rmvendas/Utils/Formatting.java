package com.pedromoreirareisgmail.rmvendas.Utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.text.NumberFormat;

public class Formatting {

    private static final NumberFormat numberFormatting = NumberFormat.getCurrencyInstance();

    /**
     * Faz o contole da apresentação Currency
     * Ao digitar o numero é adicionado no fim da string currency, mas posição da virgula
     * estara errada, para corrigir, retira-se a mascara currency, converte para double
     * e formata novamente para currency
     * Formatação para apresentação, já que os dados estão salvos em double e não currency
     *
     * @param str Recebe um currency String (Currency - R$ 25,95)
     * @return formata novamente para currency  (Currency - R$ 25,95)
     */
    public static String currencyToStringToCurrency(String str) {

        //  Verifica se a String contém cifrão (R$), ponto( . ) ou virgula ( , ) Exemplo: R$25,95
        boolean hasMask =
                (((str.contains("R$")) || (str.contains("$"))) &&
                        ((str.contains(".")) || (str.contains(","))));

        //  Se tiver cifrão, ponto, ou virgula substitui esse caracteres por vazio Exemplo: 2595
        if (hasMask) {

            str = str.replaceAll("[^\\d]", "");
        }

        // Do valor resultante, divide por 100 Exemplo: 2595/100 = 25.95
        try {

            // Formata para Currency novamente Exemplo: R$ 25,95
            return numberFormatting.format(Double.parseDouble(str) / 100);

        } catch (NumberFormatException e) {

            return "";
        }
    }

    /**
     * Recebe o valor apresentado na tela em formato currency  e formata para double que
     * é utilizado para fazer os cálculos e para salvamento no BD
     *
     * @param str Recebe uma string em formato currency (Currency - R$ 25,95)
     * @return Double que é utilizado para fazer os calculo e salvamento no BD (Double - 25.95)
     */
    public static double currencyToDouble(String str) {

        //  Verifica se a String contém cifrão (R$), ponto( . ) ou virgula ( , )
        boolean hasMask =
                (((str.contains("R$")) || (str.contains("$"))) &&
                        ((str.contains(".")) || (str.contains(","))));

        //  Se tiver cifrão, ponto, ou virgula substitui esse caracteres por vazio
        if (hasMask) {

            str = str.replaceAll("[^\\d]", "");
        }

        try {

            // Retorna um Double
            return (Double.parseDouble(str) / 100);

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    /**
     * Faz a formatação simples e direta de um double para um currency,
     * para apresentação para o usuario  1.50 -> R$ 1,50
     *
     * @param value valor double que sera formatado para currency
     * @return um string em formato currency
     */
    public static String doubleToCurrency(double value) {

        NumberFormat formatCurrency = NumberFormat.getCurrencyInstance();

        return formatCurrency.format(value);
    }

    /**
     * Recebe um double direto do banco de dados e prepara para apresentação no edit
     *
     * @param value Valor salvo no DB
     * @return string para ser inserida no edit e apresentada
     */
    public static String doubleDBToString(Double value) {

        return String.valueOf(value * 100);
    }


    /* Retira as mascara, e converte a String numerica para Double */
    public static double charSequenceToDouble(CharSequence charSequence) {

        String str = charSequence.toString().trim().replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(str)) {

            str = "0";
        }

        try {

            // Retorna um Double
            return (Double.parseDouble(str) / 100);

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    /* Retira maskara de uma String, retorna a String sem formtação */
    public static String charSequenceToString(CharSequence charSequence) {

        String str = charSequence.toString().trim().replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(str)) {

            str = "0";
        }

        return str;
    }

    /* recebe um EditTxet, captura seus dados, retira a mascara e retorna uma String sem formatação */
    public static String editToString(EditText editText) {

        String str = editText.getText().toString().trim().replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(str)) {

            str = "0";
        }

        return str;
    }

    /* recebe o EditText, captura seus dados, retira a mascara e converte a String numerica para inteiro */
    public static int editToInteger(EditText editText) {

        String str = editText.getText().toString().trim().replaceAll("[^\\d]", "");


        if (TextUtils.isEmpty(str)) {

            str = "0";
        }

        try {

            // Retorna um Double
            return (Integer.parseInt(str));

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    /* recebe o EditText, captura seus dados, retira a mascara e converte a String numerica para Double */
    public static double editToDouble(EditText editText) {

        String str = editText.getText().toString().trim().replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(str)) {

            str = "0";
        }

        try {

            // Retorna um Double
            return (Double.parseDouble(str) / 100);

        } catch (NumberFormatException e) {

            return 0;
        }

    }
}
