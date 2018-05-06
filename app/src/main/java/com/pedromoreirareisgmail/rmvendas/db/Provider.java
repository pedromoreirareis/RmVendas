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
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryOpening;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;
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

        sUriMatcher.addURI(CONTENT_AUTORITY, EntryCashMove.TABLE_CASHMOVE, MATCH_CASHMOVE);
        sUriMatcher.addURI(CONTENT_AUTORITY, EntryCashMove.TABLE_CASHMOVE + "/#", MATCH_CASHMOVE_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, EntryOpening.TABLE_OPENING, MATCH_OPENING);
        sUriMatcher.addURI(CONTENT_AUTORITY, EntryOpening.TABLE_OPENING + "/#", MATCH_OPENING_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, EntrySeel.TABLE_SELL, MATCH_SELL);
        sUriMatcher.addURI(CONTENT_AUTORITY, EntrySeel.TABLE_SELL + "/#", MATCH_SELL_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, EntryClient.TABLE_CLIENT, MATCH_CLIENT);
        sUriMatcher.addURI(CONTENT_AUTORITY, EntryClient.TABLE_CLIENT + "/#", MATCH_CLIENT_ID);

        sUriMatcher.addURI(CONTENT_AUTORITY, EntryReceive.TABLE_RECEIVE, MATCH_RECEIVE);
        sUriMatcher.addURI(CONTENT_AUTORITY, EntryReceive.TABLE_RECEIVE + "/#", MATCH_RECEIVE_ID);
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
                        EntryCashMove.TABLE_CASHMOVE,
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

                selection = EntryCashMove._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        EntryCashMove.TABLE_CASHMOVE,
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
                        EntryOpening.TABLE_OPENING,
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

                selection = EntryOpening._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        EntryOpening.TABLE_OPENING,
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
                        EntrySeel.TABLE_SELL,
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

                selection = EntrySeel._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        EntrySeel.TABLE_SELL,
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
                        EntryClient.TABLE_CLIENT,
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

                selection = EntryClient._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        EntryClient.TABLE_CLIENT,
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
                        EntryReceive.TABLE_RECEIVE,
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

                selection = EntryReceive._ID + " = ? ";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        EntryReceive.TABLE_RECEIVE,
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
                return EntryCashMove.CONTENT_TYPE_CASHMOVE;

            case MATCH_CASHMOVE_ID:
                return EntryCashMove.CONTENT_ITEM_TYPE_CASHMOVE;

            /* OPENING */
            case MATCH_OPENING:
                return EntryOpening.CONTENT_TYPE_OPENING;

            case MATCH_OPENING_ID:
                return EntryOpening.CONTENT_ITEM_TYPE_OPENING;

            /* SELL */
            case MATCH_SELL:
                return EntrySeel.CONTENT_ITEM_TYPE_SELL;

            case MATCH_SELL_ID:
                return EntrySeel.CONTENT_ITEM_TYPE_SELL;

            /* CLIENT */
            case MATCH_CLIENT:
                return EntryClient.CONTENT_TYPE_CLIENT;

            case MATCH_CLIENT_ID:
                return EntryClient.CONTENT_ITEM_TYPE_CLIENT;

            /* RECEIVE */
            case MATCH_RECEIVE:
                return EntryReceive.CONTENT_TYPE_RECEIVE;

            case MATCH_RECEIVE_ID:
                return EntryReceive.CONTENT_ITEM_TYPE_RECEIVE;

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
                return insertRegister(EntryCashMove.TABLE_CASHMOVE, uri, values);

            /* OPENING */
            case MATCH_OPENING:
                return insertRegister(EntryOpening.TABLE_OPENING, uri, values);

            /* SELL */
            case MATCH_SELL:
                return insertRegister(EntrySeel.TABLE_SELL, uri, values);

            /* CLIENT */
            case MATCH_CLIENT:
                return insertRegister(EntryClient.TABLE_CLIENT, uri, values);

            /* RECEIVE */
            case MATCH_RECEIVE:
                return insertRegister(EntryReceive.TABLE_RECEIVE, uri, values);

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

                return deleteRegisterAll(uri, EntryCashMove.TABLE_CASHMOVE, selection, selectionArgs);

            case MATCH_CASHMOVE_ID:

                return deleteRegisterId(uri, EntryCashMove.TABLE_CASHMOVE, EntryCashMove._ID);

            /* OPENING */
            case MATCH_OPENING:

                return deleteRegisterAll(uri, EntryOpening.TABLE_OPENING, selection, selectionArgs);

            case MATCH_OPENING_ID:

                return deleteRegisterId(uri, EntryOpening.TABLE_OPENING, EntryOpening._ID);

            /* SELL */
            case MATCH_SELL:

                return deleteRegisterAll(uri, EntrySeel.TABLE_SELL, selection, selectionArgs);

            case MATCH_SELL_ID:

                return deleteRegisterId(uri, EntrySeel.TABLE_SELL, EntrySeel._ID);

            /* CLIENT */
            case MATCH_CLIENT:

                return deleteRegisterAll(uri, EntryClient.TABLE_CLIENT, selection, selectionArgs);

            case MATCH_CLIENT_ID:

                return deleteRegisterId(uri, EntryClient.TABLE_CLIENT, EntryClient._ID);

            /* RECEIVE */
            case MATCH_RECEIVE:

                return deleteRegisterAll(uri, EntryReceive.TABLE_RECEIVE, selection, selectionArgs);

            case MATCH_RECEIVE_ID:

                return deleteRegisterId(uri, EntryReceive.TABLE_RECEIVE, EntryReceive._ID);

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

                return updateRegisterAll(uri, values, EntryCashMove.TABLE_CASHMOVE, selection, selectionArgs);

            case MATCH_CASHMOVE_ID:

                return updateRegisterId(uri, values, EntryCashMove.TABLE_CASHMOVE, EntryCashMove._ID);

            /* OPENING */
            case MATCH_OPENING:

                return updateRegisterAll(uri, values, EntryOpening.TABLE_OPENING, selection, selectionArgs);

            case MATCH_OPENING_ID:

                return updateRegisterId(uri, values, EntryOpening.TABLE_OPENING, EntryOpening._ID);

            /* SELL */
            case MATCH_SELL:

                return updateRegisterAll(uri, values, EntrySeel.TABLE_SELL, selection, selectionArgs);

            case MATCH_SELL_ID:

                return updateRegisterId(uri, values, EntrySeel.TABLE_SELL, EntrySeel._ID);

            /* CLIENT */
            case MATCH_CLIENT:

                return updateRegisterAll(uri, values, EntryClient.TABLE_CLIENT, selection, selectionArgs);

            case MATCH_CLIENT_ID:

                return updateRegisterId(uri, values, EntryClient.TABLE_CLIENT, EntryClient._ID);

            /* RECEIVE */
            case MATCH_RECEIVE:

                return updateRegisterAll(uri, values, EntryReceive.TABLE_RECEIVE, selection, selectionArgs);

            case MATCH_RECEIVE_ID:

                return updateRegisterId(uri, values, EntryReceive.TABLE_RECEIVE, EntryReceive._ID);

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
