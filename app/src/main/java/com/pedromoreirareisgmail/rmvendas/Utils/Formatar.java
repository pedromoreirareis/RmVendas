package com.pedromoreirareisgmail.rmvendas.Utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.text.NumberFormat;

public class Formatar {

    private static final NumberFormat formatarNumero = NumberFormat.getCurrencyInstance();


    /**
     * Recebe uma string diretamente do teclado e juntamente com as strings ja recebidas faz a
     * formatação para currency. Essa formatação é apenas para apresentação, já que os dados estão
     * salvos em double e não currency
     *
     * @param str Recebe string do teclado numérico sem formato (String - 25,95)
     * @return String  em formato currency  (Currency - R$ 25,95)
     */
    public static String formatarParaCurrency(String str) {

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
            return formatarNumero.format(Double.parseDouble(str) / 100);

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
    public static double formatarParaDouble(String str) {

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
     * Faz uma formatação simples e direta de um double para um currency, retornando uma string
     * em formato currency para apresentação para o usuario
     *
     * @param valorParaFormatar valor double que sera formatado para currency
     * @return um string em formato currency
     */
    public static String formatarDoubleParaCurrency(double valorParaFormatar) {

        NumberFormat formatarCurrency = NumberFormat.getCurrencyInstance();

        return formatarCurrency.format(valorParaFormatar);
    }


    public static Double formatarEditsDouble(EditText editText) {

        String texto = editText.getText().toString().trim().replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(texto)) {

            texto = "0";
        }

        return Double.parseDouble(texto);
    }

    public static Double formatarCharSequenceDouble(CharSequence charSequence) {

        String texto = charSequence.toString().trim().replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(texto)) {

            texto = "0";
        }

        return Double.parseDouble(texto);
    }

    public static String formatarEditsString(EditText editText) {

        String texto = editText.getText().toString().trim().replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(texto)) {

            texto = "0";
        }

        return texto;
    }

    public static String formatarCharSequenceString(CharSequence charSequence) {

        String texto = charSequence.toString().trim().replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(texto)) {

            texto = "0";
        }

        return texto;
    }

}
