package com.pedromoreirareisgmail.rmvendas.Utils;


import android.text.TextUtils;

import java.text.NumberFormat;

public class Calculos {


    /**
     * Retorna uma string em formato currency com o valor de venda do bolo que será apresentado
     * para o usuário. O valor é calculado automaticamente, ao escolher quantidade, sem tem cobertura,
     * se tera desconto.
     *
     * @param vlQ    quantidade de bolos
     * @param vlC    valor da cobertura, se tiver
     * @param vlD    valor do desconto, se tiver
     * @param vlBolo valor de cada unidade de bolo
     * @return uma string em formato currency para se apresentada para usuario com o valor do bolo
     */
    public static String calcularValorVendaBolo(String vlQ, String vlC, String vlD, double vlBolo) {

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
    }


    /**
     * Recebe o valor de cada unidade de bolo, a quantidade de bolo que sera vendida, o valor da
     * cobertura e o valor do desconto. Calcula o valor da venda, para ser salvo no banco de dados
     *
     * @param vlBolo valor de cada unidade de bolo
     * @param valorQ quantidade de bolos a ser vendida
     * @param valorC valor da cobertura, se tiver
     * @param valorD valor do desconto, se tiver
     * @return um double com o valor da venda para ser salvo no banco de dados
     */
    public static double calcularValorVendaBoloDouble(double vlBolo, double valorQ, double valorC, double valorD) {


        double quantValor = valorQ * vlBolo;

        return quantValor + valorC - valorD;

    }

}
