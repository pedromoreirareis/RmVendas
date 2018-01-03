package com.pedromoreirareisgmail.rmvendas.Utils;


public class Constantes {

    /* RETIRADA - usado no BD para deifnir tipo do registro*/
    public static final int TIPO_RETIRADA_CAIXA = 0;
    /* ENTRADA - usado no BD para deifnir tipo do registro*/
    public static final int TIPO_ENTRADA_CAIXA = 1;

    /*VENDA - usado no BD para registrar que o cliente comprou a prazo*/
    public static final int TIPO_A_RECEBER_VENDA = 0;
    /*RECEBIMENTO - usado no BD para registrar que o cliente efetuou um pagamento*/
    public static final int TIPO_A_RECEBER_RECEBIMENTO = 1;

    public static final int ADICIONAL_SIM = 1;
    public static final int ADICIONAL_NAO = 0;
    public static final int DESCONTO_SIM = 1;
    public static final int DESCONTO_NAO = 0;
    public static final int PRAZO_SIM = 1;
    public static final int PRAZO_NAO = 0;


    public static final int NUMERO_ZERO = 0;
    public static final int MIN_QUANT_CARACT = 10;


    public static final String UMA_UNIDADE = "1";


    /*Informa que é um novo registro e não uma edição*/
    public static final String VENDA_ADICIONAR = "adicionar";
    public static final String VEZES_X = " x ";
}
