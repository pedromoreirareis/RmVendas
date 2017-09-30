package com.pedromoreirareisgmail.rmvendas.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contrato {


    /************************************ TABELAS *************************************************
     * produtos         - lista com os bolos e/ou outros produtos vendidos
     * ent_ret          - entradas e retiradas de valores do caixa em um dia especifico
     * saldo_inicial    - saldo inicial em um dia especifico
     * vendas           - resumo das vendas em um dia especifico
     *********************************************************************************************/

    /**
     * Autoridade para o vendas provider
     */
    public static final String CONTENT_AUTORITY = "com.pedromoreirareisgmail.rmvendas";


    /**
     * Um content:// para o vendas provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTORITY);

    /**
     * Identificador das tabelas
     */
    public static final String PATH_PRODUTOS = AcessoProdutos.NOME_TABELA_PRODUTO;
    public static final String PATH_ENT_RET = AcessoEntRet.NOME_TABELA_ENT_RET;
    public static final String PATH_SALDO = AcessoSaldo.NOME_TABELA_SALDO;
    public static final String PATH_VENDA = AcessoVenda.NOME_TABELA_VENDA;


    private Contrato() {
    }

    /********************************* PRODUTOS ***************************************************/
    public static final class AcessoProdutos implements BaseColumns {

        /**
         * Constante do Provider - CONTENT URI da tabela de Produtos
         */
        public static final Uri CONTENT_URI_PRODUTO = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUTOS);

        /**
         * Nome da Tabela
         */
        public static final String NOME_TABELA_PRODUTO = "produtos";

        /**
         * Nome das colunas
         */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_PRODUTO_NOME = "nome";
        public static final String COLUNA_PRODUTO_PRECO = "preco";

        public static final String CRIAR_TABELA_PRODUTO =
                "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_PRODUTO + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUNA_PRODUTO_NOME + " TEXT NOT NULL, "
                        + COLUNA_PRODUTO_PRECO + " REAL NOT NULL DEFAULT 0 );";

        /**
         * Constante do MIME - tabela de Produtos
         */
        public static final String CONTENT_TYPE_PRODUTO =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_PRODUTOS;

        public static final String CONTENT_ITEM_TYPE_PRODUTO =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_PRODUTOS;
    }

    /********************************** ENT_RET ***************************************************/
    public static final class AcessoEntRet implements BaseColumns {

        public static final Uri CONTENT_URI_ENT_RET = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ENT_RET);

        public static final String NOME_TABELA_ENT_RET = "ent_ret";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_ENT_RET_DATA = "data";
        public static final String COLUNA_ENT_RET_TIPO = "tipo";
        public static final String COLUNA_ENT_RET_VALOR = "valor";
        public static final String COLUNA_ENT_RET_DESC = "descricao";

        public static final String CRIAR_TABELA_ENT_RET =
                "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_ENT_RET + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUNA_ENT_RET_DATA + " TEXT NOT NULL, "
                        + COLUNA_ENT_RET_TIPO + " INTEGER NOT NULL, "
                        + COLUNA_ENT_RET_VALOR + " REAL NOT NULL DEFAULT 0, "
                        + COLUNA_ENT_RET_DESC + " TEXT NOT NULL );";

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

        public static final Uri CONTENT_URI_SALDO = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SALDO);

        public static final String NOME_TABELA_SALDO = "saldo_inicial";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_SALDO_VALOR = "valor";
        public static final String COLUNA_SALDO_DATA = "data";

        public static final String CRIAR_TABELA_SALDO =
                "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_SALDO + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUNA_SALDO_DATA + " TEXT NOT NULL, "
                        + COLUNA_SALDO_VALOR + " REAL NOT NULL DEFAULT 0 );";

        public static final String CONTENT_ITEM_TYPE_SALDO =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_SALDO;

        public static final String CONTENT_TYPE_SALDO =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_SALDO;

    }

    /*********************************** VENDAS ***************************************************/
    public static final class AcessoVenda implements BaseColumns {

        public static final Uri CONTENT_URI_VENDA = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VENDA);

        public static final String NOME_TABELA_VENDA = "vendas";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_VENDA_NOME_PROD = "nome_prod";
        public static final String COLUNA_VENDA_VALOR_PROD = "valor_prod";
        public static final String COLUNA_VENDA_DATA = "data";
        public static final String COLUNA_VENDA_PRAZO = "prazo";
        public static final String COLUNA_VENDA_QUANT = "quantidade";
        public static final String COLUNA_VENDA_TEM_COBERTURA = "tem_cobertura";
        public static final String COLUNA_VENDA_TEM_DESCONTO = "tem_desconto";
        public static final String COLUNA_VENDA_VALOR_COBERTURA = "valor_cobertura";
        public static final String COLUNA_VENDA_VALOR_DESCONTO = "valor_desconto";
        public static final String COLUNA_VENDA_PRECO_UM_BOLO = "preco_um_bolo";

        public static final String CRIAR_TABELA_VENDA =
                "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_VENDA + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUNA_VENDA_NOME_PROD + " TEXT NOT NULL, "
                        + COLUNA_VENDA_VALOR_PROD + " REAL NOT NULL DEFAULT 0, "
                        + COLUNA_VENDA_PRECO_UM_BOLO + " REAL NOT NULL DEFAULT 0, "
                        + COLUNA_VENDA_DATA + " TEXT NOT NULL, "
                        + COLUNA_VENDA_PRAZO + " INTEGER NOT NULL, "
                        + COLUNA_VENDA_QUANT + " INTEGER NOT NULL, "
                        + COLUNA_VENDA_TEM_COBERTURA + " INTEGER NOT NULL, "
                        + COLUNA_VENDA_VALOR_COBERTURA + " REAL, "
                        + COLUNA_VENDA_TEM_DESCONTO + " INTEGER NOT NULL, "
                        + COLUNA_VENDA_VALOR_DESCONTO + " REAL );";

        public static final String CONTENT_ITEM_TYPE_VENDA =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_VENDA;

        public static final String CONTENT_TYPE_VENDA =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_VENDA;
    }
}