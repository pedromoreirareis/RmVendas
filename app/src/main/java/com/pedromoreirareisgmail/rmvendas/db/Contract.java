package com.pedromoreirareisgmail.rmvendas.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    /*
     *********************************** TABELAS ***************************************************
     *
     * PRODUCT              - Produtos
     * CASHMOVE             - Movimento do caixa - entradas e retiradas no dia
     * OPENING              - saldo inicial do dia
     * SELL                 - Vendas do dia
     * CLIENT               - cadastro de clientes
     * RECEIVE              - Registro das vendas a prazo
     *
     **********************************************************************************************/

    // Autoridade para o vendas provider
    protected static final String CONTENT_AUTORITY = "com.pedromoreirareisgmail.rmvendas";

    // Um content:// para o vendas provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTORITY);

    // Identificador das tabelas
    private static final String PATH_PRODUCT = EntryProduct.TABLE_PRODUCT;
    private static final String PATH_CASHMOVE = EntryCashMove.TABLE_CASHMOVE;
    private static final String PATH_OPENING = EntryOpening.TABLE_OPENING;
    private static final String PATH_SELL = EntrySeel.TABLE_SELL;
    private static final String PATH_CLIENT = EntryClient.TABLE_CLIENT;
    private static final String PATH_RECEIVE = EntryReceive.TABLE_RECEIVE;

    // Construtor do contrato
    public Contract() {
    }


    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    //**************************************** TABELAS *******************************************//
    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/


    /************************** CASHMOVE - ENTRADAS E RETIRADAS ***********************************/
    public static final class EntryCashMove implements BaseColumns {

        /* Nome da Tabela */
        public static final String TABLE_CASHMOVE = "cashmove";

        /* Constante do Provider - CONTENT URI da tabela de Produtos */
        public static final Uri CONTENT_URI_CASHMOVE = Uri.withAppendedPath(
                BASE_CONTENT_URI,
                PATH_CASHMOVE
        );

        /* Nome das colunas */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_DESCRIPTION = "description";

        /* Criando tabela */
        public static final String CREATE_TABLE_CASHMOVE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_CASHMOVE
                        + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TIMESTAMP + " TEXT NOT NULL, "
                        + COLUMN_TYPE + " INTEGER NOT NULL, "
                        + COLUMN_VALUE + " REAL NOT NULL DEFAULT 0, "
                        + COLUMN_DESCRIPTION + " TEXT NOT NULL "
                        + " );";

        /* Constante do MIME - tabela de Produtos */
        public static final String CONTENT_ITEM_TYPE_CASHMOVE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_CASHMOVE;

        public static final String CONTENT_TYPE_CASHMOVE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_CASHMOVE;
    }


    /******************************* OPENING - SALDO INICIAL **************************************/
    public static final class EntryOpening implements BaseColumns {

        public static final String TABLE_OPENING = "opening";
        public static final Uri CONTENT_URI_OPENING = Uri.withAppendedPath(
                BASE_CONTENT_URI,
                PATH_OPENING
        );

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_VALUE = "value";

        public static final String CREATE_TABLE_OPENING =
                "CREATE TABLE IF NOT EXISTS " + TABLE_OPENING
                        + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TIMESTAMP + " TEXT NOT NULL, "
                        + COLUMN_VALUE + " REAL NOT NULL DEFAULT 0 "
                        + " );";

        public static final String CONTENT_ITEM_TYPE_OPENING =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_OPENING;

        public static final String CONTENT_TYPE_OPENING =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_OPENING;
    }

    /****************************** PRODUCT - PRODUTOS ********************************************/
    public static final class EntryProduct implements BaseColumns {


        public static final String TABLE_PRODUCT = "products";
        public static final Uri CONTENT_URI_PRODUCT = Uri.withAppendedPath(
                BASE_CONTENT_URI,
                PATH_PRODUCT
        );

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";

        public static final String CREATE_TABLE_PRODUCT =
                "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT
                        + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_NAME + " TEXT NOT NULL, "
                        + COLUMN_PRICE + " REAL NOT NULL DEFAULT 0 "
                        + " ); ";

        public static final String CONTENT_TYPE_PRODUTOS =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_PRODUCT;

        public static final String CONTENT_ITEM_TYPE_PRODUTOS =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_PRODUCT;
    }


    /********************************* CLIENT - CLIENTE *******************************************/
    public static final class EntryClient implements BaseColumns {

        public static final String TABLE_CLIENT = "client";
        public static final Uri CONTENT_URI_CLIENT = Uri.withAppendedPath(
                BASE_CONTENT_URI,
                PATH_CLIENT
        );

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FONE = "fone";

        public static final String CREATE_TABLE_CLIENT =
                " CREATE TABLE IF NOT EXISTS " + TABLE_CLIENT
                        + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_NAME + " TEXT NOT NULL, "
                        + COLUMN_FONE + " TEXT "
                        + " ); ";

        public static final String CONTENT_ITEM_TYPE_CLIENT =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_CLIENT;

        public static final String CONTENT_TYPE_CLIENT =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_CLIENT;
    }


    /********************************* SELL - VENDA ***********************************************/
    public static final class EntrySeel implements BaseColumns {

        public static final String TABLE_SELL = "sell";
        public static final Uri CONTENT_URI_SELL = Uri.withAppendedPath(
                BASE_CONTENT_URI,
                PATH_SELL
        );

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_ADD_VALUE = "additional";
        public static final String COLUMN_DISCOUNT_VALUE = "discount";
        public static final String COLUMN_FORWARD_VALUE = "forward";


        //TODO: Criar COLUMN_CARD_VALUE para valor compra com cartão
        public static final String COLUMN_CARD_VALUE = "forward";

        public static final String COLUMN_CLIENT_ID = "client_id";
        public static final String COLUMN_CLIENT_NAME = "client_name";
        public static final String COLUMN_RECEIVE_ID = "receive_id";



        public static final String CREATE_TABLE_SELL =
                "CREATE TABLE IF NOT EXISTS " + TABLE_SELL
                        + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TIMESTAMP + " TEXT NOT NULL, "
                        + COLUMN_NAME + " TEXT NOT NULL, "
                        + COLUMN_PRICE + " REAL NOT NULL DEFAULT 0, "
                        + COLUMN_QUANTITY + " INTEGER NOT NULL, "
                        + COLUMN_ADD_VALUE + " REAL, "
                        + COLUMN_DISCOUNT_VALUE + " REAL, "
                        + COLUMN_FORWARD_VALUE + " REAL, "
                        + COLUMN_CARD_VALUE + " REAL, "
                        + COLUMN_CLIENT_ID + " INTEGER, "
                        + COLUMN_CLIENT_NAME + " TEXT, "
                        + COLUMN_RECEIVE_ID + " INTEGER "
                        + " ); ";

        public static final String CONTENT_ITEM_TYPE_SELL =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_SELL;

        public static final String CONTENT_TYPE_SELL =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_SELL;
    }


    /***************************** RECEIVE - VENDAS A PRAZO ***************************************/
    public static final class EntryReceive implements BaseColumns {

        public static final String TABLE_RECEIVE = "receive";
        public static final Uri CONTENT_URI_RECEIVE = Uri.withAppendedPath(
                BASE_CONTENT_URI,
                PATH_RECEIVE
        );

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CLIENT_ID = "client_id";
        public static final String COLUMN_CLIENT_NAME = "client_name";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VALUE = "value";


        public static final String CREATE_TABLE_RECEIVE =
                " CREATE TABLE IF NOT EXISTS " + TABLE_RECEIVE
                        + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TIMESTAMP + " TEXT NOT NULL, "
                        + COLUMN_TYPE + " INTEGER NOT NULL, "
                        + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                        + COLUMN_VALUE + " REAL NOT NULL DEFAULT 0, "
                        + COLUMN_CLIENT_ID + " INTEGER, "
                        + COLUMN_CLIENT_NAME + " TEXT "
                        + " ); ";

                       /* + " FOREIGN KEY ( " + COLUMN_CLIENT_ID + " ) REFERENCES " + EntryClient.TABLE_CLIENT + " ( " + EntryClient._ID + " ) "
                        + " ); ";*/


        public static final String CONTENT_ITEM_TYPE_RECEIVE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_RECEIVE;

        public static final String CONTENT_TYPE_RECEIVE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_RECEIVE;
    }
}