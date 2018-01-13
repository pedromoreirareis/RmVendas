package com.pedromoreirareisgmail.rmvendas.Utils;


public class Constantes {

    /* RETIRADA / ENTRADA- usado no BD para deifnir tipo do registro*/
    public static final int TIPO_RETIRADA = 0;
    public static final int TIPO_ENTRADA = 1;

    /*VENDA / RECEBIMENTO- usado no BD para registrar que o cliente comprou a prazo*/
    public static final int TIPO_VENDA = 0;
    public static final int TIPO_RECEBIMENTO = 1;

    /* Parametros de comparação */
    public static final int MIN_CARACT_3 = 3;
    public static final int MIN_CARACT_5 = 5;
    public static final int MIN_CARACT_10 = 10;
    public static final int MIN_NUM_FONE = 8;
    public static final int MENOS_UM = -1;
    public static final double NUMERO_ZERO = 0;
    public static final String NUMERO_ZERO_STRING = "0";
    public static final String UMA_UNIDADE_STRING = "1";

    /*Constantes para Intents*/
    public static final String ADICIONAR_VENDA = "adicionar";
    public static final String ADICIONAR = "adicionar";

    /* Chamadas Intents*/
    public static final String CLIENTE_ID = "clienteId";
    public static final String CLIENTE_NOME = "clienteNome";
    public static final String CLIENTE_FONE = "clienteFone";
    public static final String VALOR_VENDA_TROCO = "valor_troco";
    public static final String ACTIVITY_CHAMOU = "activity_chamou";
    public static final String CLIENTES_LIST_ACTIVITY = "activity_clientes_list";
    public static final String VEND_LIST_CLIENTES_ACTIVITY = "activity_vend_list_cliente";

    /*Códigos de Result - startActivityForResult*/
    public static final int COD_RESULT_VENDA_CLIENTES = 101;
}
