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
    public static final String PATH_PRODUTOS = AcessoProdutos.TABELA_PRODUTOS;
    public static final String PATH_ENT_RET = AcessoEntRet.TABELA_ENT_RET;
    public static final String PATH_SALDO_INICIAL = AcessoSaldo.TABELA_SALDO_INICIAL;
    public static final String PATH_VENDA = AcessoVenda.TABELA_VENDAS;


    private Contrato() {
    }

    /********************************* PRODUTOS ***************************************************/
    public static final class AcessoProdutos implements BaseColumns {

        /**
         * Constante do Provider - CONTENT URI da tabela de Produtos
         */
        public static final Uri CONTENT_URI_PRODUTOS = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUTOS);

        /**
         * Nome da Tabela
         */
        public static final String TABELA_PRODUTOS = "produtos";

        /**
         * Nome das colunas
         */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_PRODUTOS_NOME = "nome";
        public static final String COLUNA_PRODUTOS_VALOR = "preco";

        public static final String CRIAR_TABELA_PRODUTOS =
                "CREATE TABLE IF NOT EXISTS " + TABELA_PRODUTOS + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUNA_PRODUTOS_NOME + " TEXT NOT NULL, "
                        + COLUNA_PRODUTOS_VALOR + " REAL NOT NULL DEFAULT 0 );";

        /**
         * Constante do MIME - tabela de Produtos
         */
        public static final String CONTENT_TYPE_PRODUTOS =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_PRODUTOS;

        public static final String CONTENT_ITEM_TYPE_PRODUTOS =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTORITY +
                        "/" + PATH_PRODUTOS;
    }

    /********************************** ENT_RET ***************************************************/
    public static final class AcessoEntRet implements BaseColumns {

        public static final Uri CONTENT_URI_ENT_RET = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ENT_RET);

        public static final String TABELA_ENT_RET = "ent_ret";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_ENT_RET_DATA = "data";
        public static final String COLUNA_ENT_RET_TIPO = "tipo";
        public static final String COLUNA_ENT_RET_VALOR = "valor";
        public static final String COLUNA_ENT_RET_DESCRICAO = "descricao";

        public static final String CRIAR_TABELA_ENT_RET =
                "CREATE TABLE IF NOT EXISTS " + TABELA_ENT_RET + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUNA_ENT_RET_DATA + " TEXT NOT NULL, "
                        + COLUNA_ENT_RET_TIPO + " INTEGER NOT NULL, "
                        + COLUNA_ENT_RET_VALOR + " REAL NOT NULL DEFAULT 0, "
                        + COLUNA_ENT_RET_DESCRICAO + " TEXT NOT NULL );";

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

        public static final String TABELA_SALDO_INICIAL = "saldo_inicial";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_SALDO_INICIAL_DATA = "data";
        public static final String COLUNA_SALDO_INICIAL_VALOR = "valor";


        public static final String CRIAR_TABELA_SALDO_INICIAL =
                "CREATE TABLE IF NOT EXISTS " + TABELA_SALDO_INICIAL + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUNA_SALDO_INICIAL_DATA + " TEXT NOT NULL, "
                        + COLUNA_SALDO_INICIAL_VALOR + " REAL NOT NULL DEFAULT 0 );";

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

        public static final String TABELA_VENDAS = "vendas";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_VENDAS_DATA = "data";
        public static final String COLUNA_VENDAS_NOME_PRODUTO = "nome_prod";
        public static final String COLUNA_VENDAS_VALOR_UMA_UNIDADE_PRODUTO = "preco_um_bolo";
        public static final String COLUNA_VENDAS_QUANTIDADE_VENDIDA = "quantidade";
        public static final String COLUNA_VENDAS_TEM_COBERTURA = "tem_cobertura";
        public static final String COLUNA_VENDAS_VALOR_COBERTURA = "valor_cobertura";
        public static final String COLUNA_VENDAS_TEM_DESCONTO = "tem_desconto";
        public static final String COLUNA_VENDAS_VALOR_DESCONTO = "valor_desconto";
        public static final String COLUNA_VENDAS_A_PRAZO = "prazo";
        public static final String COLUNA_VENDAS_VALOR_TOTAL_VENDA = "valor_prod";

        public static final String CRIAR_TABELA_VENDA =
                "CREATE TABLE IF NOT EXISTS " + TABELA_VENDAS + " ( "
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUNA_VENDAS_DATA + " TEXT NOT NULL, "
                        + COLUNA_VENDAS_NOME_PRODUTO + " TEXT NOT NULL, "
                        + COLUNA_VENDAS_VALOR_UMA_UNIDADE_PRODUTO + " REAL NOT NULL DEFAULT 0, "
                        + COLUNA_VENDAS_QUANTIDADE_VENDIDA + " INTEGER NOT NULL, "
                        + COLUNA_VENDAS_TEM_COBERTURA + " INTEGER NOT NULL, "
                        + COLUNA_VENDAS_VALOR_COBERTURA + " REAL, "
                        + COLUNA_VENDAS_TEM_DESCONTO + " INTEGER NOT NULL, "
                        + COLUNA_VENDAS_VALOR_DESCONTO + " REAL, "
                        + COLUNA_VENDAS_A_PRAZO + " INTEGER NOT NULL, "
                        + COLUNA_VENDAS_VALOR_TOTAL_VENDA + " REAL NOT NULL DEFAULT 0 );";

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