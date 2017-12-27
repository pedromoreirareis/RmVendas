package com.pedromoreirareisgmail.rmvendas.Utils;


import android.text.TextUtils;

import java.text.NumberFormat;

public class Calculos {


    /**
     * Retorna uma string em formato currency com o valor de venda do produto que será apresentado
     * para o usuário. O valor é calculado automaticamente, ao escolher quantidade, valor adicional,
     * e valor de desconto.
     *
     * @param vlQ    Quantidade de bolos
     * @param vlC    Valor adicional, se tiver
     * @param vlD    Valor do desconto, se tiver
     * @param vlProd Valor de cada unidade do produto
     * @return uma string em formato currency do valor total
     */
    public static String calcularValorVendaBolo(String vlQ, String vlC, String vlD, double vlProd) {

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

        double quantValor = valorQ * vlProd;

        double total = quantValor + valorC - valorD;

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        return preco.format(total);
    }


    /**
     * Recebe o valor de cada unidade do produto, a quantidade do produto que sera vendida, o valor
     * adicional e o valor do desconto. Calcula o valor da venda, para ser salvo no banco de dados
     *
     * @param vlProd valor de cada unidade do produto
     * @param valorQ quantidade do produto a ser vendida
     * @param valorC valor adicional, se tiver
     * @param valorD valor do desconto, se tiver
     * @return um double com o valor da venda para ser salvo no banco de dados
     */
    public static double calcularValorVendaBoloDouble(double vlProd, double valorQ, double valorC, double valorD) {


        double quantValor = valorQ * vlProd;

        return quantValor + valorC - valorD;

    }

}
