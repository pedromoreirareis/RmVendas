package com.pedromoreirareisgmail.rmvendas.Utils;


import android.text.TextUtils;

import java.text.NumberFormat;

public class Calculus {

    /**
     * Retorna uma string em formato currency com o valor de venda do produto que será apresentado
     * para o usuário. O valor é calculado automaticamente, ao escolher quantidade, valor adicional,
     * e valor de desconto.
     *
     * @param vlQquantidade Quantidade de bolos
     * @param vlAdicional   Valor adicional, se tiver
     * @param vlDesconto    Valor do desconto, se tiver
     * @param vlPrazo       Valor fiado, se tiver
     * @return uma string em formato currency do valor total
     */
    public static String calcularValorTotalVendaString(String vlQquantidade, double vlProduto, String vlAdicional, String vlDesconto, String vlPrazo) {

        if (TextUtils.isEmpty(vlQquantidade)) {

            vlQquantidade = "0";
        }

        if (TextUtils.isEmpty(vlAdicional)) {

            vlAdicional = "0";
        }

        if (TextUtils.isEmpty(vlDesconto)) {

            vlDesconto = "0";
        }

        if (TextUtils.isEmpty(vlPrazo)) {

            vlPrazo = "0";
        }

        double valorQuantidade = Double.parseDouble(vlQquantidade);
        double valorAdicional = Double.parseDouble(vlAdicional) / 100;
        double valorDesconto = Double.parseDouble(vlDesconto) / 100;
        double valorPrazo = Double.parseDouble(vlPrazo) / 100;

        double quantValor = valorQuantidade * vlProduto;

        double total = quantValor + valorAdicional - valorDesconto - valorPrazo;

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        return preco.format(total);
    }


    /**
     * Recebe o valor de cada unidade do produto, a quantidade do produto que sera vendida, o valor
     * adicional e o valor do desconto. Calcula o valor da venda, para ser salvo no banco de dados
     *
     * @param vlProduto       valor de cada unidade do produto
     * @param valorQuantidade quantidade do produto a ser vendida
     * @param valorAdicional  valor adicional, se tiver
     * @param valorDesconto   valor do desconto, se tiver
     * @return um double com o valor da venda para ser salvo no banco de dados
     */
    public static double calcularValorTotalVendaDouble(
            double valorQuantidade,
            double vlProduto,
            double valorAdicional,
            double valorDesconto) {

        double valorPrecoVenda = valorQuantidade * vlProduto;

        return valorPrecoVenda + valorAdicional - valorDesconto;

    }

    public static double CalcularValorAVistaDouble(
            int valorQuantidade,
            double valorProduto,
            double valorAdicional,
            double valorDesconto,
            double valorPrazo
    ) {

        double valorPrecoVenda = valorProduto * valorQuantidade;

        return valorPrecoVenda + valorAdicional - valorDesconto - valorPrazo;
    }



}
