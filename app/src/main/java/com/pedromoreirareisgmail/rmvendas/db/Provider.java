package com.pedromoreirareisgmail.rmvendas.db;

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
import com.pedromoreirareisgmail.rmvendas.constantes.ConstTag;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoAReceber;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoClientes;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoVenda;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.CONTENT_AUTORITY;


public class Provider extends ContentProvider {

    private static final String TAG = ConstTag.TAG_MAIN + Provider.class.getSimpleName();

    /* Uri de todas as linhas da tabela - MATCH_PRODUCTS = 100; */
    /* Uri de uma unica linha do BD - MATCH_PRODUCT_ID = 101; */
    private static final int MATCH_PRODUCTS = 100;
    private static final int MATCH_PRODUCT_ID = 101;

    private static final int MATCH_CASHMOVE = 200;
    private static final int MATCH_CASHMOVE_ID = 201;

    private static final int MATCH_OPENING = 300;
    private static final int MATCH_OPENING_ID = 301;

    private static final int MATCH_SELL = 400;
    private static final int MATCH_SELL_ID = 401;

    private static final int MATCH_CLIENT = 500;
    private static final int MATCH_CLIENT_ID = 501;

    private static final int MATCH_RECEIVE = 600;
    private static final int MATCH_RECEIVE_ID = 601;


    /* Inicia o UriMatcher com identificador dizendo */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        /*  com.pedromoreirareisgmail.minhasvendas/tableName/       - Uri geral toda a tabela */
        /*  com.pedromoreirareisgmail.minhasvendas/TableName/id     - Uri especifico uma unica linha */

        sUriMatcher.addURI(CONTENT_AUTORITY, EntryProduct.TABLE_PRODUCT, MATCH_PRODUCTS);
        sUriMatcher.addURI(CONTENT_AUTORITY, EntryProduct.TABLE_PRODUCT + "/#", MATCH_PRODUCT_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoEntRet.TABELA_ENT_RET, MATCH_CASHMOVE);
        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoEntRet.TABELA_ENT_RET + "/#", MATCH_CASHMOVE_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoSaldo.TABELA_SALDO_INICIAL, MATCH_OPENING);
        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoSaldo.TABELA_SALDO_INICIAL + "/#", MATCH_OPENING_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoVenda.TABELA_VENDAS, MATCH_SELL);
        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoVenda.TABELA_VENDAS + "/#", MATCH_SELL_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoClientes.TABELA_CLIENTES, MATCH_CLIENT);
        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoClientes.TABELA_CLIENTES + "/#", MATCH_CLIENT_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoAReceber.TABELA_A_RECEBER, MATCH_RECEIVE);
        sUriMatcher.addURI(CONTENT_AUTORITY, AcessoAReceber.TABELA_A_RECEBER + "/#", MATCH_RECEIVE_ID);
    }


    private DbHelper mDbHelper;

    /**
     * Cria o objeto DbHelper
     *
     * @return true se criou corretamento, false se houve erro
     */
    @Override
    public boolean onCreate() {

        Log.v(TAG, "onCreate: Criando instancia DbHelper - Para Gerênciar Banco de Dados");

        /* Cria uma nova instancia do banco de dados */
        mDbHelper = new DbHelper(getContext());

        return true;
    }

    /**
     * Gerencia as pesquisa no Banco de dados
     *
     * @param uri           Uri da tabela a ser pesquisada - Endereço onde a tabela esta salva
     * @param projection    Quais as colunas serão retornadas
     * @param selection     A coluna que será pesquisada
     * @param selectionArgs O que será pesquisado
     * @param sortOrder     Ordem dos dados retornados
     * @return cursor com resultado da pesquisa
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        final int match = sUriMatcher.match(uri);

        switch (match) {

            /* PRODUCTS */
            case MATCH_PRODUCTS:

                cursor = database.query(
                        EntryProduct.TABLE_PRODUCT,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY PRODUCTS: Pesquisando todos os produtos");

                break;

            case MATCH_PRODUCT_ID:

                selection = EntryProduct._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        EntryProduct.TABLE_PRODUCT,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY PRODUCTS: Pesquisando um unico Produto");

                break;



            /* CASHMOVE */
            case MATCH_CASHMOVE:

                cursor = database.query(
                        AcessoEntRet.TABELA_ENT_RET,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY CASHMOVE: Pesquisando todas Entradas e Retiradas");

                break;

            case MATCH_CASHMOVE_ID:

                selection = AcessoEntRet._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoEntRet.TABELA_ENT_RET,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY CASHMOVE: Pesquisando uma unica Entrada ou Retirada");

                break;


            /* OPENING */
            case MATCH_OPENING:

                cursor = database.query(
                        AcessoSaldo.TABELA_SALDO_INICIAL,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY OPENING: Pesquisando todos os Saldos Iniciais");

                break;

            case MATCH_OPENING_ID:

                selection = AcessoSaldo._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoSaldo.TABELA_SALDO_INICIAL,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY OPENING: Pesquisando um unico Saldo Inicial");

                break;


            /* SELL */
            case MATCH_SELL:

                cursor = database.query(
                        AcessoVenda.TABELA_VENDAS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY SELL: Pesquisando todas as Vendas");

                break;

            case MATCH_SELL_ID:

                selection = AcessoVenda._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoVenda.TABELA_VENDAS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY SELL: Pesquisando uma unica venda");

                break;


            /* CLIENT */
            case MATCH_CLIENT:

                cursor = database.query(
                        AcessoClientes.TABELA_CLIENTES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY CLIENT: Pesquisando todos os Clientes");

                break;

            case MATCH_CLIENT_ID:

                selection = AcessoClientes._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoClientes.TABELA_CLIENTES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY CLIENT: Pesquisando um unico Cliente");

                break;

            /* RECEIVE */
            case MATCH_RECEIVE:

                cursor = database.query(
                        AcessoAReceber.TABELA_A_RECEBER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY RECEIVE: Pesquisando todos os A Receber");

                break;

            case MATCH_RECEIVE_ID:

                selection = AcessoAReceber._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoAReceber.TABELA_A_RECEBER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v(TAG, "QUERY RECEIVE: Pesquisando um unico A Receber");

                break;


            /* DEFAULT */
            default:

                throw new IllegalArgumentException(getContext() != null
                        ? String.format(getContext().getString(R.string.exception_query), uri)
                        : null
                );
        }

        if (getContext() != null && cursor != null) {

            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        } else {

            Log.e(TAG, getContext() != null
                    ? getContext().getString(R.string.log_e_query)
                    : null
            );
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            /* PRODUCT */
            case MATCH_PRODUCTS:
                return EntryProduct.CONTENT_TYPE_PRODUTOS;

            case MATCH_PRODUCT_ID:
                return EntryProduct.CONTENT_ITEM_TYPE_PRODUTOS;

            /* CASHMOVE */
            case MATCH_CASHMOVE:
                return AcessoEntRet.CONTENT_TYPE_ENT_RET;

            case MATCH_CASHMOVE_ID:
                return AcessoEntRet.CONTENT_ITEM_TYPE_ENT_RET;

            /* OPENING */
            case MATCH_OPENING:
                return AcessoSaldo.CONTENT_TYPE_SALDO_INICIAL;

            case MATCH_OPENING_ID:
                return AcessoSaldo.CONTENT_ITEM_TYPE_SALDO_INICIAL;

            /* SELL */
            case MATCH_SELL:
                return AcessoVenda.CONTENT_TYPE_VENDA;

            case MATCH_SELL_ID:
                return AcessoVenda.CONTENT_ITEM_TYPE_VENDA;

            /* CLIENT */
            case MATCH_CLIENT:
                return AcessoClientes.CONTENT_TYPE_CLIENTES;

            case MATCH_CLIENT_ID:
                return AcessoClientes.CONTENT_ITEM_TYPE_CLIENTES;

            /* RECEIVE */
            case MATCH_RECEIVE:
                return AcessoAReceber.CONTENT_TYPE_A_RECEBER;

            case MATCH_RECEIVE_ID:
                return AcessoAReceber.CONTENT_ITEM_TYPE_A_RECEBER;

            /* DEFAULT */
            default:

                throw new IllegalArgumentException(getContext() != null
                        ? String.format(getContext().getString(R.string.exception_gettype), uri, match)
                        : null
                );
        }
    }

    /**
     * Inserir registros no banco de dados
     *
     * @param uri    Uri da tabela onde sera inserido o registro
     * @param values Dados a serem inseridos
     * @return Uri exato do registro inserido, Uri da tabela mais id do registro inserido
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        Log.v(TAG, "INSERT - INSERINDO UM NOVO REGISTRO");

        final int match = sUriMatcher.match(uri);

        switch (match) {

            /* PRODUCT */
            case MATCH_PRODUCTS:
                return insertRegister(EntryProduct.TABLE_PRODUCT, uri, values);

            /*  CASHMOVE */
            case MATCH_CASHMOVE:
                return insertRegister(AcessoEntRet.TABELA_ENT_RET, uri, values);

            /* OPENING */
            case MATCH_OPENING:
                return insertRegister(AcessoSaldo.TABELA_SALDO_INICIAL, uri, values);

            /* SELL */
            case MATCH_SELL:
                return insertRegister(AcessoVenda.TABELA_VENDAS, uri, values);

            /* CLIENT */
            case MATCH_CLIENT:
                return insertRegister(AcessoClientes.TABELA_CLIENTES, uri, values);

            /* RECEIVE */
            case MATCH_RECEIVE:
                return insertRegister(AcessoAReceber.TABELA_A_RECEBER, uri, values);

            /* DEFAULT */
            default:

                throw new IllegalArgumentException(getContext() != null
                        ? String.format(getContext().getString(R.string.exception_insert), uri)
                        : null
                );
        }
    }

    /**
     * Deleta registro(s) do banco de dados
     *
     * @param uri           Uri especifico deleta um registro, ou Uri da tabela que tera todos os dados deletados
     * @param selection     Regra para pesquisa do registro a ser deletado
     * @param selectionArgs Parametro da pesquisa do registro a ser deletado
     * @return Quantidade de registros deletados
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        Log.v(TAG, "DELETE - EXCLUINDO REGISTRO(S)");

        final int match = sUriMatcher.match(uri);

        switch (match) {

            /* PRODUCT */
            case MATCH_PRODUCTS:

                deleteRegisterAll(uri, EntryProduct.TABLE_PRODUCT, selection, selectionArgs);

            case MATCH_PRODUCT_ID:

                return deleteRegisterId(uri, EntryProduct.TABLE_PRODUCT, EntryProduct._ID);

            /* CASHMOVE */
            case MATCH_CASHMOVE:

                return deleteRegisterAll(uri, AcessoEntRet.TABELA_ENT_RET, selection, selectionArgs);

            case MATCH_CASHMOVE_ID:

                return deleteRegisterId(uri, AcessoEntRet.TABELA_ENT_RET, AcessoEntRet._ID);

            /* OPENING */
            case MATCH_OPENING:

                return deleteRegisterAll(uri, AcessoSaldo.TABELA_SALDO_INICIAL, selection, selectionArgs);

            case MATCH_OPENING_ID:

                return deleteRegisterId(uri, AcessoSaldo.TABELA_SALDO_INICIAL, AcessoSaldo._ID);

            /* SELL */
            case MATCH_SELL:

                return deleteRegisterAll(uri, AcessoVenda.TABELA_VENDAS, selection, selectionArgs);

            case MATCH_SELL_ID:

                return deleteRegisterId(uri, AcessoVenda.TABELA_VENDAS, AcessoVenda._ID);

            /* CLIENT */
            case MATCH_CLIENT:

                return deleteRegisterAll(uri, AcessoClientes.TABELA_CLIENTES, selection, selectionArgs);

            case MATCH_CLIENT_ID:

                return deleteRegisterId(uri, AcessoClientes.TABELA_CLIENTES, AcessoClientes._ID);

            /* RECEIVE */
            case MATCH_RECEIVE:

                return deleteRegisterAll(uri, AcessoAReceber.TABELA_A_RECEBER, selection, selectionArgs);

            case MATCH_RECEIVE_ID:

                return deleteRegisterId(uri, AcessoAReceber.TABELA_A_RECEBER, AcessoAReceber._ID);

            /* DEFAULT */
            default:
                throw new IllegalArgumentException(getContext() != null
                        ? String.format(getContext().getString(R.string.exception_delete), uri)
                        : null
                );

        }
    }

    /**
     * Atualiza registros
     *
     * @param uri           Uri do registro ou tabela a ser atualizado
     * @param values        Dados a serem atualizado
     * @param selection     Regras a serem aplicadas na pesquisa dos dados a serem atualizados
     * @param selectionArgs Argumentos a serem pesquisados
     * @return Quantidade de registros atualizado
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        Log.v(TAG, "UPDATE - ATUALIZANDO REGISTRO(S)");

        final int match = sUriMatcher.match(uri);

        switch (match) {

            /* PRODUCT */
            case MATCH_PRODUCTS:

                return updateRegisterAll(uri, values, EntryProduct.TABLE_PRODUCT, selection, selectionArgs);

            case MATCH_PRODUCT_ID:

                return updateRegisterId(uri, values, EntryProduct.TABLE_PRODUCT, EntryProduct._ID);

            /* CASHMOVE */
            case MATCH_CASHMOVE:

                return updateRegisterAll(uri, values, AcessoEntRet.TABELA_ENT_RET, selection, selectionArgs);

            case MATCH_CASHMOVE_ID:

                return updateRegisterId(uri, values, AcessoEntRet.TABELA_ENT_RET, AcessoEntRet._ID);

            /* OPENING */
            case MATCH_OPENING:

                return updateRegisterAll(uri, values, AcessoSaldo.TABELA_SALDO_INICIAL, selection, selectionArgs);

            case MATCH_OPENING_ID:

                return updateRegisterId(uri, values, AcessoSaldo.TABELA_SALDO_INICIAL, AcessoSaldo._ID);

            /* SELL */
            case MATCH_SELL:

                return updateRegisterAll(uri, values, AcessoVenda.TABELA_VENDAS, selection, selectionArgs);

            case MATCH_SELL_ID:

                return updateRegisterId(uri, values, AcessoVenda.TABELA_VENDAS, AcessoVenda._ID);

            /* CLIENT */
            case MATCH_CLIENT:

                return updateRegisterAll(uri, values, AcessoClientes.TABELA_CLIENTES, selection, selectionArgs);

            case MATCH_CLIENT_ID:

                return updateRegisterId(uri, values, AcessoClientes.TABELA_CLIENTES, AcessoClientes._ID);

            /* RECEIVE */
            case MATCH_RECEIVE:

                return updateRegisterAll(uri, values, AcessoAReceber.TABELA_A_RECEBER, selection, selectionArgs);

            case MATCH_RECEIVE_ID:

                return updateRegisterId(uri, values, AcessoAReceber.TABELA_A_RECEBER, AcessoAReceber._ID);

            /* DEFAULT */
            default:

                throw new IllegalArgumentException(getContext() != null
                        ? String.format(getContext().getString(R.string.exception_update), uri)
                        : null
                );
        }
    }


    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@     UTILS - CRUD   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/


    /*-********************************     INSERT    *******************************************-*/

    /**
     * Insere um novo registro
     *
     * @param tableName Nome da tabela
     * @param uri       Uri da tabela onde o registro sera inserido
     * @param values    Dados a serem inseridos
     * @return Uri completo - Uri da tabela mais id do resgistro inserido
     */
    private Uri insertRegister(String tableName, Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(
                tableName,
                null,
                values
        );

        if (id == -1) {

            Log.e(TAG, getContext() != null
                    ? String.format(getContext().getString(R.string.log_e_insert), uri)
                    : null
            );
        }

        if (getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, id);
    }


    /*-********************************     UPDATE    *******************************************-*/

    /**
     * Atualizar registros
     *
     * @param uri           Uri da tabela a ser atualizada
     * @param values        Dados a serem atualizados
     * @param tableName     Nome da tabela
     * @param selection     Regras para pesquisa dos registros a serem atualizados
     * @param selectionArgs Argumentos a serem pesquisados na Regras do selection
     * @return Quantidade de registros editados
     */
    private int updateRegisterAll(Uri uri, ContentValues values, String tableName, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        int updates = db.update(
                tableName,
                values, selection,
                selectionArgs
        );

        if (updates > 0 && getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);

        } else {

            Log.e(TAG, getContext() != null
                    ? String.format(getContext().getString(R.string.log_e_update), uri)
                    : null
            );
        }

        return updates;
    }

    /**
     * Atualizar um unico registro
     *
     * @param uri       Uri da tabela
     * @param values    Dados a serem atualizados
     * @param tableName Nome da tabela
     * @param id        id a ser atualizado
     * @return quantidade de registros atualizados
     */
    private int updateRegisterId(Uri uri, ContentValues values, String tableName, String id) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = id + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

        int updates = db.update(
                tableName,
                values,
                selection,
                selectionArgs
        );

        if (updates > 0 && getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);

        } else {

            Log.e(TAG, getContext() != null
                    ? String.format(getContext().getString(R.string.log_e_update), uri)
                    : null
            );
        }

        return updates;
    }


    /*-********************************     DELETE    *******************************************-*/

    /**
     * Excluir registros
     *
     * @param uri           Uri da tabela
     * @param tableName     Nome da tabela
     * @param selection     Regras para pesquisar registros a excluir
     * @param selectionArgs Argumentos a serem pesquisados nas regras do Selection
     * @return quantidade de registros excluidos
     */
    private int deleteRegisterAll(Uri uri, String tableName, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deletes = db.delete(
                tableName,
                selection,
                selectionArgs
        );

        if (deletes > 0 && getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);

        } else {

            Log.e(TAG, getContext() != null
                    ? String.format(getContext().getString(R.string.log_e_delete), uri)
                    : null
            );
        }

        return deletes;

    }

    /**
     * Excluir registro especifico
     *
     * @param uri       Uri da tabela
     * @param tableName Nome da tabela
     * @param id        id do registro
     * @return quantidade de registros excluidos
     */
    private int deleteRegisterId(Uri uri, String tableName, String id) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = id + " = ? ";

        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

        int deletes = db.delete(
                tableName,
                selection,
                selectionArgs);

        if (deletes > 0 && getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);

        } else {

            Log.e(TAG, getContext() != null
                    ? String.format(getContext().getString(R.string.log_e_delete), uri)
                    : null
            );
        }

        return deletes;
    }
}
