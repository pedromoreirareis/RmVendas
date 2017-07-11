package com.pedromoreirareisgmail.rmvendas.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class VendasContrato {

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

    private static final String TAG = VendasContrato.class.getSimpleName();

    private VendasContrato() {
    }

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

    public static final class AcessoEntRet implements BaseColumns {

        /**
         * Constante do Provider - CONTENT URI
         */
        public static final Uri CONTENTENT_URI_ENT_RET = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ENT_RET);

        /**
         * Nome da Tabela
         */
        public static final String NOME_TABELA_ENT_RET = "ent_ret";

        /**
         * Nome das colunas
         */
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
}