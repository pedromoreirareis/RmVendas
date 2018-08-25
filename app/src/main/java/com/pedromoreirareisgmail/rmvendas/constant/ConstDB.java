package com.pedromoreirareisgmail.rmvendas.constant;

public class ConstDB {

    /* ADD / REMOVE - indica se o movimento é uma entrada ou retirada */
    public static final int TYPE_REMOVE_MONEY_CASHMOVE = 0;
    public static final int TYPE_ADD_MONEY_CASHMOVE = 1;

    /* DEBIT / CREDIT - indica se é um credito ou debito para o cliente*/
    public static final int TYPE_DEBIT = 0;
    public static final int TYPE_CREDIT_CASH = 1;
    public static final int TYPE_CREDIT_CARD = 2;
}
