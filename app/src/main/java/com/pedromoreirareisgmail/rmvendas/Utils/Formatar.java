package com.pedromoreirareisgmail.rmvendas.Utils;

import java.text.NumberFormat;

public class Formatar {

    private static final NumberFormat formatarNumero = NumberFormat.getCurrencyInstance();


    /**
     * Recebe uma string diretamente do teclado e juntamente com as strings ja recebidas faz a
     * formatação para currency. Essa formatação é apenas para apresentação, já que os dados estão
     * salvos em double e não currency
     *
     * @param str Recebe string diretamente do teclado numérico
     * @return uma string para ser apresentada em formato currency com cifrão
     */
    public static String formatarParaCurrency(String str) {

        boolean hasMask =
                (((str.contains("R$")) || (str.contains("$"))) &&
                        ((str.contains(".")) || (str.contains(","))));

        if (hasMask) {

            str = str.replaceAll("[^\\d]", "");
        }

        try {

            return formatarNumero.format(Double.parseDouble(str) / 100);

        } catch (NumberFormatException e) {

            return "";
        }
    }

    /**
     * Pega o número apresentado na tela em formato currency e com cifrão e formata para double que
     * é utilizado para fazer os cálculos e para salvamento no banco de dados.
     *
     * @param str entra uma string em formato currency
     * @return um double que é utilizado para fazer os calculo e salvamento no banco de dados
     */
    public static double formatarParaDouble(String str) {

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

    public static String formatarDoubleParaCurrency(double valorParaFormatar) {

        NumberFormat formatarCurrency = NumberFormat.getCurrencyInstance();

        return formatarCurrency.format(valorParaFormatar);
    }


}
