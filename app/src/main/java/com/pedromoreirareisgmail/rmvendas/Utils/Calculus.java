package com.pedromoreirareisgmail.rmvendas.Utils;


import android.text.TextUtils;

import java.text.NumberFormat;

public class Calculus {

    /**
     * ValueInCash =  Quantity*VlProduct + vlAdd - vlDiscount - vlForward - vlCard
     * <p>
     * Retorna uma string em formato currency com o valor  a vista do produto, que será apresentado
     * para o usuário. O valor é calculado automaticamente, ao escolher quantidade, valor adicional,
     * ,valor de desconto, valor a prazo e valor no cartão.
     *
     * @param vlQuantity Quantidade do produto
     * @param vlProduct  Valor cada unidade do produto
     * @param vlAdd      Valor adicional sobre a venda
     * @param vlDiscount Valor de desconto na venda
     * @param vlForward  Valor a prazo
     * @param vlCard     Valor no cartão
     * @return string em formato currency
     */
    public static String calculateInCashValueString(String vlQuantity, double vlProduct,
                                                    String vlAdd, String vlDiscount,
                                                    String vlForward, String vlCard) {

        if (TextUtils.isEmpty(vlQuantity)) {

            vlQuantity = "0";
        }

        if (TextUtils.isEmpty(vlAdd)) {

            vlAdd = "0";
        }

        if (TextUtils.isEmpty(vlDiscount)) {

            vlDiscount = "0";
        }

        if (TextUtils.isEmpty(vlForward)) {

            vlForward = "0";
        }

        if (TextUtils.isEmpty(vlCard)) {

            vlCard = "0";
        }


        double valueQuantity = Double.parseDouble(vlQuantity);
        double valueAdd = Double.parseDouble(vlAdd) / 100;
        double valueDiscount = Double.parseDouble(vlDiscount) / 100;
        double valueForward = Double.parseDouble(vlForward) / 100;
        double valueCard = Double.parseDouble(vlCard) / 100;


        double productsValue = valueQuantity * vlProduct;

        double total = productsValue + valueAdd - valueDiscount - valueForward - valueCard;

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        return preco.format(total);
    }


    /**
     * valueSell =  Quantity*VlProduct + vlAdd - vlDiscount
     * <p>
     * Retorna uma string em formato currency com o valor de venda do produto, que será apresentado
     * para o usuário. O valor é calculado automaticamente, ao escolher quantidade, valor adicional,
     * e valor de desconto.
     *
     * @param vlQuantity Quantidade do produto
     * @param vlProduct  Valor cada unidade do produto
     * @param vlAdd      Valor adicional sobre a venda
     * @param vlDiscount Valor de desconto na venda
     * @return string em formato currency
     */
    public static String calculateSaleValueString(String vlQuantity, double vlProduct,
                                                  String vlAdd, String vlDiscount) {

        if (TextUtils.isEmpty(vlQuantity)) {

            vlQuantity = "0";
        }

        if (TextUtils.isEmpty(vlAdd)) {

            vlAdd = "0";
        }

        if (TextUtils.isEmpty(vlDiscount)) {

            vlDiscount = "0";
        }


        double valueQuantity = Double.parseDouble(vlQuantity);
        double valueAdd = Double.parseDouble(vlAdd) / 100;
        double valueDiscount = Double.parseDouble(vlDiscount) / 100;

        double productsValue = valueQuantity * vlProduct;

        double total = productsValue + valueAdd - valueDiscount;

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        return preco.format(total);
    }


    /**
     * @param vlQuantity valor de cada unidade do produto
     * @param vlProduct  quantidade do produto a ser vendida
     * @param vlAdd      valor adicional, se tiver
     * @param vlDiscount valor do desconto, se tiver
     * @param vlForward  valor a prazo, se tiver
     * @return um double com o valor da venda a vista
     */
    public static double calculateInCashValueDouble(int vlQuantity, double vlProduct, double vlAdd,
                                                    double vlDiscount, double vlForward, double vlCard) {

        double valorPrecoVenda = vlProduct * vlQuantity;

        return valorPrecoVenda + vlAdd - vlDiscount - vlForward - vlCard;
    }


    /**
     * Recebe o valor de cada unidade do produto, a quantidade do produto que sera vendida, o valor
     * adicional e o valor do desconto. Calcula o valor da venda, para ser salvo no banco de dados
     *
     * @param vlProduct  valor de cada unidade do produto
     * @param vlQuantity quantidade do produto a ser vendida
     * @param vlAdd      valor adicional, se tiver
     * @param vlDiscount valor do desconto, se tiver
     * @return um double com o valor da venda para ser salvo no banco de dados
     */
    public static double calculateSaleValueDouble(double vlQuantity, double vlProduct,
                                                  double vlAdd, double vlDiscount) {

        double valorPrecoVenda = vlQuantity * vlProduct;

        return valorPrecoVenda + vlAdd - vlDiscount;
    }


}
