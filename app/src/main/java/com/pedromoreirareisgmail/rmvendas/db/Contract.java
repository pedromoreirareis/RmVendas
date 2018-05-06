package com.pedromoreirareisgmail.rmvendas.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {


    /*
     *********************************** TABELAS ***************************************************
     *
     * Product              - Tabela com os dados dos produtos
     *
     * Entradas_Retiradas   - entradas e retiradas de valores do caixa em um dia especifico
     * Saldo_Inicial        - saldo inicial em um dia especifico
     * Vendas               - resumo das vendas em um dia especifico
     * Clientes             - cadastro de clientes
     * A_Receber            - registro de vendas a prazo - fiado
     *
     **********************************************************************************************/

    // Autoridade para o vendas provider
    protected static final String CONTENT_AUTORITY = "com.pedromoreirareisgmail.rmvendas";


    // Um content:// para o vendas provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTORITY);

    // Identificador das tabelas
    private static final String PATH_PRODUCT = EntryProduct.TABLE_PRODUCT;
    private static final String PATH_ENT_RET = AcessoEntRet.TABELA_ENT_RET;
    private static final String PATH_SALDO_INICIAL = AcessoSaldo.TABELA_SALDO_INICIAL;
    private static final String PATH_VENDA = AcessoVenda.TABELA_VENDAS;
    private static final String PATH_CLIENTES = AcessoClientes.TABELA_CLIENTES;
    private static final String PATH_A_RECEBER = AcessoAReceber.TABELA_A_RECEBER;

    // Construtor do contrato
    public Contract() {
    }




    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    //**************************************** TABELAS *******************************************//

    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/


    /********************************* PRODUTOS ***************************************************/
    public static final class EntryProduct implements BaseColumns {

        /* Constante do Provider - CONTENT URI da tabela de Produtos */
        public static final Uri CONTENT_URI_PRODUCT =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        /* Nome da Tabela */
        public static final String TABLE_PRODUCT = "products";

        /* Nome das colunas */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";

        /* Criando tabela de Produtos */
        public static final String CREATE_TABLE_PRODUCT =
                "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_NAME + " TEXT NOT NULL, "
                        + COLUMN_PRICE + " REAL NOT NULL DEFAULT 0 );";

        /* Constante do MIME - tabela de Produtos */
        public static final String CONTENT_TYPE_PRODUTOS =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_PRODUCT;

        public static final String CONTENT_ITEM_TYPE_PRODUTOS =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_PRODUCT;
    }


    /********************************** ENT_RET ***************************************************/
    public static final class AcessoEntRet implements BaseColumns {

        public static final Uri CONTENT_URI_ENT_RET = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ENT_RET);

        public static final String TABELA_ENT_RET = "Entradas_Retiradas";

        public static final String _ID = BaseColumns._ID;
        public static final String DATA_HORA = "data_hora";
        public static final String TIPO = "tipo";
        public static final String VALOR = "valor";
        public static final String DESCRICAO = "descricao";

        public static final String CRIAR_TABELA_ENT_RET =
                "CREATE TABLE IF NOT EXISTS " + TABELA_ENT_RET + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + DATA_HORA + " TEXT NOT NULL, "
                        + TIPO + " INTEGER NOT NULL, "
                        + VALOR + " REAL NOT NULL DEFAULT 0, "
                        + DESCRICAO + " TEXT NOT NULL );";

        public static final String CONTENT_ITEM_TYPE_ENT_RET =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_ENT_RET;

        public static final String CONTENT_TYPE_ENT_RET =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_ENT_RET;
    }


    /******************************** SALDO_INICIAL ***********************************************/
    public static final class AcessoSaldo implements BaseColumns {

        public static final Uri CONTENT_URI_SALDO_INICIAL = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SALDO_INICIAL);

        public static final String TABELA_SALDO_INICIAL = "Saldo_Inicial";

        public static final String _ID = BaseColumns._ID;
        public static final String DATA_HORA = "data_hora";
        public static final String VALOR = "valor";


        public static final String CRIAR_TABELA_SALDO_INICIAL =
                "CREATE TABLE IF NOT EXISTS " + TABELA_SALDO_INICIAL + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + DATA_HORA + " TEXT NOT NULL, "
                        + VALOR + " REAL NOT NULL DEFAULT 0 );";

        public static final String CONTENT_ITEM_TYPE_SALDO_INICIAL =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_SALDO_INICIAL;

        public static final String CONTENT_TYPE_SALDO_INICIAL =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_SALDO_INICIAL;

    }


    /*********************************** VENDAS ***************************************************/
    public static final class AcessoVenda implements BaseColumns {

        public static final Uri CONTENT_URI_VENDA = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VENDA);

        public static final String TABELA_VENDAS = "Vendas";

        public static final String _ID = BaseColumns._ID;
        public static final String DATA_HORA = "data_hora";
        public static final String NOME_PRODUTO = "nome_produto";
        public static final String VALOR_UNIDADE = "valor_unidade_produto";
        public static final String QUANTIDADE = "quantidade";
        public static final String VALOR_ADICIONAL = "valor_adicional";
        public static final String VALOR_DESCONTO = "valor_desconto";
        public static final String VALOR_PRAZO = "valor_prazo";
        public static final String ID_CLIENTE = "id_cliente";

        public static final String CRIAR_TABELA_VENDA =
                "CREATE TABLE IF NOT EXISTS " + TABELA_VENDAS + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + DATA_HORA + " TEXT NOT NULL, "
                        + NOME_PRODUTO + " TEXT NOT NULL, "
                        + VALOR_UNIDADE + " REAL NOT NULL DEFAULT 0, "
                        + QUANTIDADE + " INTEGER NOT NULL, "
                        + VALOR_ADICIONAL + " REAL, "
                        + VALOR_DESCONTO + " REAL, "
                        + VALOR_PRAZO + " REAL, "
                        + ID_CLIENTE + " INTEGER );";

        public static final String CONTENT_ITEM_TYPE_VENDA =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_VENDA;

        public static final String CONTENT_TYPE_VENDA =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_VENDA;
    }


    /*********************************** CLIENTES *************************************************/

    public static final class AcessoClientes implements BaseColumns {


        public static final Uri CONTENT_URI_CLIENTES = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLIENTES);

        public static final String TABELA_CLIENTES = "Clientes";

        public static final String _ID = BaseColumns._ID;
        public static final String NOME = "nome";
        public static final String TELEFONE = "telefone";

        public static final String CRIAR_TABELA_CLIENTES =
                " CREATE TABLE IF NOT EXISTS " + TABELA_CLIENTES + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + NOME + " TEXT NOT NULL, "
                        + TELEFONE + " TEXT );";

        public static final String CONTENT_ITEM_TYPE_CLIENTES =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_CLIENTES;

        public static final String CONTENT_TYPE_CLIENTES =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_CLIENTES;


    }


    /*********************************** A RECEBER ************************************************/

    public static final class AcessoAReceber implements BaseColumns {

        public static final Uri CONTENT_URI_ARECEBER = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_A_RECEBER);

        public static final String TABELA_A_RECEBER = "A_Receber";


        public static final String _ID = BaseColumns._ID;
        public static final String CLIENTE_ID = "id_cliente";
        public static final String CLIENTE_NOME = "nome_cliente";
        public static final String DATA_HORA = "data_hora";
        public static final String TIPO_ENTRADA = "tipo_entrada";
        public static final String DESCRICAO = "descricao";
        public static final String VALOR = "valor";


        public static final String CRIAR_TABELA_A_RECEBER =
                " CREATE TABLE IF NOT EXISTS " + TABELA_A_RECEBER + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + CLIENTE_ID + " INTEGER NOT NULL, "
                        + CLIENTE_NOME + " TEXT NOT NULL, "
                        + DATA_HORA + " TEXT NOT NULL, "
                        + TIPO_ENTRADA + " INTEGER NOT NULL, "
                        + DESCRICAO + " TEXT NOT NULL, "
                        + VALOR + " REAL NOT NULL DEFAULT 0 ); ";


        public static final String CONTENT_ITEM_TYPE_A_RECEBER =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_A_RECEBER;

        public static final String CONTENT_TYPE_A_RECEBER =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_A_RECEBER;

    }
}