package com.pedromoreirareisgmail.rmvendas.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoProdutos;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoVenda;


public class Provider extends ContentProvider {

    private static final String TAG = Provider.class.getSimpleName();

    // Indica se Uri é de uma unica linha do banco de dados - MATCH_PRODUTO_ID = 1;
    // Indica Uri de todas as linhas do banco de dados - MATCH_PRODUTOS = 2;
    private static final int MATCH_PRODUTO_ID = 1;
    private static final int MATCH_PRODUTOS = 2;
    private static final int MATCH_ENT_RET_ID = 3;
    private static final int MATCH_ENT_RET = 4;
    private static final int MATCH_SALDO_ID = 5;
    private static final int MATCH_SALDO = 6;
    private static final int MATCH_VENDA_ID = 7;
    private static final int MATCH_VENDA = 8;


    /**
     * Inicia o UriMatcher com identificador dizendo
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        /*  com.pedromoreirareisgmail.minhasvendas/nomeDaTabela/       - Uri geral toda a tabela */
        /*  com.pedromoreirareisgmail.minhasvendas/nomeDaTabela/1  _ID - Uri especifico uma unica linha */

        sUriMatcher.addURI(Contrato.CONTENT_AUTORITY, AcessoProdutos.TABELA_PRODUTOS, MATCH_PRODUTOS);
        sUriMatcher.addURI(Contrato.CONTENT_AUTORITY, AcessoProdutos.TABELA_PRODUTOS + "/#", MATCH_PRODUTO_ID);

        sUriMatcher.addURI(Contrato.CONTENT_AUTORITY, AcessoEntRet.TABELA_ENT_RET, MATCH_ENT_RET);
        sUriMatcher.addURI(Contrato.CONTENT_AUTORITY, AcessoEntRet.TABELA_ENT_RET + "/#", MATCH_ENT_RET_ID);

        sUriMatcher.addURI(Contrato.CONTENT_AUTORITY, AcessoSaldo.TABELA_SALDO_INICIAL, MATCH_SALDO);
        sUriMatcher.addURI(Contrato.CONTENT_AUTORITY, AcessoSaldo.TABELA_SALDO_INICIAL + "/#", MATCH_SALDO_ID);

        sUriMatcher.addURI(Contrato.CONTENT_AUTORITY, AcessoVenda.TABELA_VENDAS, MATCH_VENDA);
        sUriMatcher.addURI(Contrato.CONTENT_AUTORITY, AcessoVenda.TABELA_VENDAS + "/#", MATCH_VENDA_ID);
    }

    /**
     * Cria o objeto DbHelper
     */
    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        /*
         * Cria uma nova instancia do banco de dados
         */
        mDbHelper = new DbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        final int match = sUriMatcher.match(uri);

        switch (match) {

            // Produtos
            case MATCH_PRODUTOS:

                cursor = database.query(
                        AcessoProdutos.TABELA_PRODUTOS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MATCH_PRODUTO_ID:

                selection = AcessoProdutos._ID + " =? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoProdutos.TABELA_PRODUTOS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder
                );
                break;

            // Entradas e Retiradas
            case MATCH_ENT_RET:

                cursor = database.query(
                        AcessoEntRet.TABELA_ENT_RET,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MATCH_ENT_RET_ID:

                selection = AcessoEntRet._ID + " = ?";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoEntRet.TABELA_ENT_RET,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder
                );
                break;

            // Saldo Inicial
            case MATCH_SALDO:

                cursor = database.query(
                        AcessoSaldo.TABELA_SALDO_INICIAL,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MATCH_SALDO_ID:

                selection = AcessoSaldo._ID + " =? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoSaldo.TABELA_SALDO_INICIAL,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder
                );
                break;

            // Vendas
            case MATCH_VENDA:

                cursor = database.query(
                        AcessoVenda.TABELA_VENDAS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MATCH_VENDA_ID:

                selection = AcessoVenda._ID + " =? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoVenda.TABELA_VENDAS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder
                );
                break;

            default:

                throw new IllegalArgumentException(getContext() != null ?
                        getContext().getString(R.string.provider_exception_query)
                                + uri : null
                );
        }

        if (getContext() != null && cursor != null) {

            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        } else {

            Log.e(TAG, getContext() != null ?
                    getContext().getString(R.string.provider_log_query) : null);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            // Produtos
            case MATCH_PRODUTOS:
                return AcessoProdutos.CONTENT_TYPE_PRODUTOS;

            case MATCH_PRODUTO_ID:
                return AcessoProdutos.CONTENT_ITEM_TYPE_PRODUTOS;

            // Entradas e retiradas
            case MATCH_ENT_RET:
                return AcessoEntRet.CONTENT_TYPE_ENT_RET;

            case MATCH_ENT_RET_ID:
                return AcessoEntRet.CONTENT_ITEM_TYPE_ENT_RET;

            // Saldo inicial
            case MATCH_SALDO:
                return AcessoSaldo.CONTENT_TYPE_SALDO_INICIAL;

            case MATCH_SALDO_ID:
                return AcessoSaldo.CONTENT_ITEM_TYPE_SALDO_INICIAL;

            // Vendas
            case MATCH_VENDA:
                return AcessoVenda.CONTENT_TYPE_VENDA;

            case MATCH_VENDA_ID:
                return AcessoVenda.CONTENT_ITEM_TYPE_VENDA;

            default:

                throw new IllegalArgumentException(getContext() != null ?
                        getContext().getString(R.string.provider_exception_getType_uri)
                                + uri
                                + getContext().getString(R.string.provider_exception_getType_match)
                                + match
                        : null
                );
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case MATCH_PRODUTOS:
                return inserirProvider(AcessoProdutos.TABELA_PRODUTOS, uri, values);

            case MATCH_ENT_RET:
                return inserirProvider(AcessoEntRet.TABELA_ENT_RET, uri, values);

            case MATCH_SALDO:
                return inserirProvider(AcessoSaldo.TABELA_SALDO_INICIAL, uri, values);

            case MATCH_VENDA:
                return inserirProvider(AcessoVenda.TABELA_VENDAS, uri, values);

            default:

                throw new IllegalArgumentException(getContext() != null ?
                        getContext().getString(R.string.provider_exception_insert)
                                + uri
                        : null
                );
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            // Produtos
            case MATCH_PRODUTOS:

                excluir(uri, AcessoProdutos.TABELA_PRODUTOS, selection, selectionArgs);

            case MATCH_PRODUTO_ID:

                return excluirID(uri, AcessoProdutos.TABELA_PRODUTOS, AcessoProdutos._ID);

            // Entradas e retiradas
            case MATCH_ENT_RET:

                return excluir(uri, AcessoEntRet.TABELA_ENT_RET, selection, selectionArgs);

            case MATCH_ENT_RET_ID:

                return excluirID(uri, AcessoEntRet.TABELA_ENT_RET, AcessoEntRet._ID);

            // Saldo inicial
            case MATCH_SALDO:

                return excluir(uri, AcessoSaldo.TABELA_SALDO_INICIAL, selection, selectionArgs);

            case MATCH_SALDO_ID:

                return excluirID(uri, AcessoSaldo.TABELA_SALDO_INICIAL, AcessoSaldo._ID);

            // Vendas
            case MATCH_VENDA:

                return excluir(uri, AcessoVenda.TABELA_VENDAS, selection, selectionArgs);

            case MATCH_VENDA_ID:

                return excluirID(uri, AcessoVenda.TABELA_VENDAS, AcessoVenda._ID);

            default:
                throw new IllegalArgumentException(getContext() != null ?
                        getContext().getString(R.string.provider_exception_delete) : null);

        }

    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            // Produtos
            case MATCH_PRODUTOS:

                return editarProvider(uri, values, AcessoProdutos.TABELA_PRODUTOS, selection, selectionArgs);

            case MATCH_PRODUTO_ID:

                return editarProviderID(uri, values, AcessoProdutos.TABELA_PRODUTOS, AcessoProdutos._ID);

            // Entradas e retiradas
            case MATCH_ENT_RET:

                return editarProvider(uri, values, AcessoEntRet.TABELA_ENT_RET, selection, selectionArgs);

            case MATCH_ENT_RET_ID:

                return editarProviderID(uri, values, AcessoEntRet.TABELA_ENT_RET, AcessoEntRet._ID);

            // Saldo inicial
            case MATCH_SALDO:

                return editarProvider(uri, values, AcessoSaldo.TABELA_SALDO_INICIAL, selection, selectionArgs);

            case MATCH_SALDO_ID:

                return editarProviderID(uri, values, AcessoSaldo.TABELA_SALDO_INICIAL, AcessoSaldo._ID);

            // Vendas
            case MATCH_VENDA:

                return editarProvider(uri, values, AcessoVenda.TABELA_VENDAS, selection, selectionArgs);

            case MATCH_VENDA_ID:

                return editarProviderID(uri, values, AcessoVenda.TABELA_VENDAS, AcessoVenda._ID);

            default:

                throw new IllegalArgumentException(getContext() != null ?
                        getContext().getString(R.string.provider_exception_update) + uri : null);
        }
    }


    /******************************     UTILS - CRUD   ********************************************/

    /********************************     INSERIR    **********************************************/

    private Uri inserirProvider(String nomeTabela, Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(
                nomeTabela,
                null,
                values
        );

        if (id == -1) {

            Log.e(TAG, getContext() != null ?
                    getContext().getString(R.string.provider_log_inserir_prod) + uri : null);
        }

        if (getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, id);
    }


    /********************************     EDITAR    ***********************************************/

    private int editarProvider(Uri uri, ContentValues values, String nomeTabela, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int editadas;

        editadas = database.update(
                nomeTabela,
                values, selection,
                selectionArgs
        );

        if (editadas > 0 && getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return editadas;
    }

    private int editarProviderID(Uri uri, ContentValues values, String nomeTabela, String idLinha) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int editadas;

        String selection = idLinha + "=?";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

        editadas = database.update(
                nomeTabela,
                values, selection,
                selectionArgs
        );

        if (editadas > 0 && getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return editadas;
    }


    /********************************     EXCLUIR   ***********************************************/

    private int excluir(Uri uri, String nomeTabela, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int excluidas = database.delete(
                nomeTabela,
                selection,
                selectionArgs
        );

        if (excluidas > 0 && getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return excluidas;

    }

    private int excluirID(Uri uri, String nomeTabela, String idLinha) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        String selection = idLinha + " =? ";

        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

        int excluidas = database.delete(
                nomeTabela,
                selection,
                selectionArgs);

        if (excluidas > 0 && getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return excluidas;
    }
}
