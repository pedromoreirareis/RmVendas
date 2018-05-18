package com.pedromoreirareisgmail.rmvendas.constant;

public class ConstIntents {

    /* Adicionar nova venda */
    public static final String INTENT_ADD_SELL = "add_sell";

    /* Identifica qual Activity chamou RegisterClientActivity */
    public static final String INTENT_CALLED_CLIENT = "called_client";

    /* Identifica os dados do cliente e passa para vendas a prazo e registro */
    public static final String INTENT_CLIENT_DATA = "client_data";

    /* Identifica dados da activity Sell para ListClientSale */
    public static final String INTENT_SELL_TO_CLIENT = "sell_to_client";

    /* Identifica dados da Activity ListClientSale para Sell */
    public static final String INTENT_CLIENT_TO_SELL = "client_to_sell";

    /* Identifica dados da activity Main para MoneyBack */
    public static final String INTENT_MONEY_BACK = "money_back";


    /* Indetifica REQUEST_CODE para SignIn result na GoogleApi */
    public static final int RC_SIGN_IN = 9001;
}
