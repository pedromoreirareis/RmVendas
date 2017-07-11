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
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoProdutos;

import static java.security.CryptoPrimitive.MAC;

public class VendasProvider extends ContentProvider {

    private static final String TAG = VendasProvider.class.getSimpleName();


    /**
     * Indica se Uri é de uma unica linha do banco de dados
     */
    private static final int MATCH_PRODUTO_ID = 1;

    /**
     * Indica Uri de todas as linhas do banco de dados
     */
    private static final int MATCH_PRODUTOS = 2;

    private static final int MATCH_ENT_RET_ID = 3;
    private static final int MATCH_ENT_RET = 4;

    /**
     * Inicia o UriMatcher com identificador dizendo
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        /*  com.pedromoreirareisgmail.minhasvendas/nomeDaTabela/    - Uri geral toda a tabela */
        sUriMatcher.addURI(VendasContrato.CONTENT_AUTORITY, AcessoProdutos.NOME_TABELA_PRODUTO, MATCH_PRODUTOS);


        /*  com.pedromoreirareisgmail.minhasvendas/nomeDaTabela/1   - Uri especifico uma unica linha */
        sUriMatcher.addURI(VendasContrato.CONTENT_AUTORITY, AcessoProdutos.NOME_TABELA_PRODUTO + "/#", MATCH_PRODUTO_ID);

        /*  com.pedromoreirareisgmail.minhasvendas/nomeDaTabela/    - Uri geral toda a tabela */
        sUriMatcher.addURI(VendasContrato.CONTENT_AUTORITY, AcessoEntRet.NOME_TABELA_ENT_RET, MATCH_ENT_RET);

        /*  com.pedromoreirareisgmail.minhasvendas/nomeDaTabela/1   - Uri especifico uma unica linha */
        sUriMatcher.addURI(VendasContrato.CONTENT_AUTORITY, AcessoEntRet.NOME_TABELA_ENT_RET + "/#", MATCH_ENT_RET_ID);
    }

    /**
     * Cria o objeto VendasDbHelper
     */
    private VendasDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        /*
         * Cria uma nova instancia do banco de dados
         */
        mDbHelper = new VendasDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        final int match = sUriMatcher.match(uri);

        switch (match) {

            /* Faz uma pesquisa, retornando as linhas do banco de dados */
            case MATCH_PRODUTOS:
                cursor = database.query(
                        AcessoProdutos.NOME_TABELA_PRODUTO,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            /* Faz uma pesquisa, retornando uma linha especifica do banco de dados */
            case MATCH_PRODUTO_ID:

                /* Indica a forma de acesso a pesquisa
                Ex: pesquisar pelo _ID ou outro campo ou campos */
                selection = AcessoProdutos._ID + "=?";

                /* Passa a linha especifica que deve ser pesquisada */
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoProdutos.NOME_TABELA_PRODUTO,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder
                );
                break;


            case MATCH_ENT_RET:
                cursor = database.query(
                        AcessoEntRet.NOME_TABELA_ENT_RET,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MATCH_ENT_RET_ID:
            /* Indica a forma de acesso a pesquisa
                Ex: pesquisar pelo _ID ou outro campo ou campos */
                selection = AcessoEntRet._ID + "=?";

                /* Passa a linha especifica que deve ser pesquisada */
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        AcessoEntRet.NOME_TABELA_ENT_RET,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder
                );
                break;

            /*
                Caso o tipo de pesquisa não for de uma linha especifica e nem for geral
                lança uma excessao que infroma que esse tipo de pesquisa foi invalida
             */
            default:

                throw new IllegalArgumentException(getContext() != null ?
                        getContext().getString(R.string.provider_exception_query)
                                + uri : null
                );
        }

        /* Faz uma notificação para o CursorLoader para exibição da pesquisa */

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
            case MATCH_PRODUTOS:
                return AcessoProdutos.CONTENT_TYPE_PRODUTO;
            case MATCH_PRODUTO_ID:
                return AcessoProdutos.CONTENT_ITEM_TYPE_PRODUTO;
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

            /* Ao inserir não é necessário implementar MATCH_PRODUTO_ID (Uri especifico),
               pois não há como inserir em um _ID especifico se ele não existe

               Faz a inserção no MATCH_PRODUTOS (Uri geral) , e será retornado o um Uri especifico
             */
            case MATCH_PRODUTOS:
                return inserirProduto(uri, values);
            case MATCH_ENT_RET:
                return inserirEntRet(uri, values);

            /* Caso o Uri não for o geral ou houver um erro na inserção será lançada uma exceção */
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

        int linhasExcluidas;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch (match) {

            /* Uri geral */
            case MATCH_PRODUTOS:

                /* Exclui todos os itens da tabela */
                linhasExcluidas = database.delete(
                        AcessoProdutos.NOME_TABELA_PRODUTO,
                        selection,
                        selectionArgs
                );

                /* Se houve exclusao */
                if (linhasExcluidas > 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return linhasExcluidas;

            /* Uri especifico */
            case MATCH_PRODUTO_ID:

                /* indica a forma de acesso a linha para exclusao */
                selection = AcessoProdutos._ID + "=?";

                /* indica a linha a ser excluida */
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                /* exclui uma linha especifica */
                linhasExcluidas = database.delete(
                        AcessoProdutos.NOME_TABELA_PRODUTO,
                        selection,
                        selectionArgs);

                /* se houve exclusao */
                if (linhasExcluidas > 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                /* retorna a quantidade de linhas excluida */
                return linhasExcluidas;

            case MATCH_ENT_RET:
                /* Exclui todos os itens da tabela */
                linhasExcluidas = database.delete(
                        AcessoEntRet.NOME_TABELA_ENT_RET,
                        selection,
                        selectionArgs
                );

                /* Se houve exclusao */
                if (linhasExcluidas > 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return linhasExcluidas;

            case MATCH_ENT_RET_ID:
                /* indica a forma de acesso a linha para exclusao */
                selection = AcessoEntRet._ID + "=?";

                /* indica a linha a ser excluida */
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                /* exclui uma linha especifica */
                linhasExcluidas = database.delete(
                        AcessoEntRet.NOME_TABELA_ENT_RET,
                        selection,
                        selectionArgs);

                /* se houve exclusao */
                if (linhasExcluidas > 0 && getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                /* retorna a quantidade de linhas excluida */
                return linhasExcluidas;

            default:
                throw new IllegalArgumentException(getContext() != null ?
                        getContext().getString(R.string.provider_exception_delete) : null);

        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            /* Uri geral */
            case MATCH_PRODUTOS:
                return atualizarProduto(
                        uri,
                        values,
                        selection, selectionArgs
                );

            /* Uri especifico */
            case MATCH_PRODUTO_ID:
                selection = AcessoProdutos._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return atualizarProduto(uri, values, selection, selectionArgs);

            case MATCH_ENT_RET:
                return atualizarEntRet(
                        uri,
                        values,
                        selection, selectionArgs
                );

            case MATCH_ENT_RET_ID:
                selection = AcessoEntRet._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return atualizarEntRet(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException(getContext()!=null?
                        getContext().getString(R.string.provider_exception_update)+ uri:null);
        }
    }

    private Uri inserirProduto(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(
                AcessoProdutos.NOME_TABELA_PRODUTO,
                null,
                values
        );

        /* Se id == -1 significa que não foi possivel faz a inserção no banco de dados */
        if (id == -1) {
            Log.e(TAG, getContext() != null ?
                    getContext().getString(R.string.provider_log_inserir_prod) + uri : null);
        }

        /* Se a inclusão der certo será retornado
           um id que sera o _ID desse produto e juntaremos
           esse id com o Uri geral para formar um Uri especifico
           que sera o endereço desse produto no banco de dados */

        /* Notificando ao CursorLoader o resultado da inclusão */
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri inserirEntRet(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(
                AcessoEntRet.NOME_TABELA_ENT_RET,
                null,
                values
        );

        /* Se id == -1 significa que não foi possivel faz a inserção no banco de dados */
        if (id == -1) {
            Log.e(TAG, getContext() != null ?
                    getContext().getString(R.string.provider_log_inserir_ent_ret) + uri : null);
        }

        /* Se a inclusão der certo será retornado
           um id que sera o _ID desse produto e juntaremos
           esse id com o Uri geral para formar um Uri especifico
           que sera o endereço desse produto no banco de dados */

        /* Notificando ao CursorLoader o resultado da inclusão */
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private int atualizarProduto(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int linhasAtualizadas;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        linhasAtualizadas = database.update(
                AcessoProdutos.NOME_TABELA_PRODUTO,
                values, selection,
                selectionArgs
        );

        /* houve atualização da tabela */
        if (linhasAtualizadas > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return linhasAtualizadas;
    }

    private int atualizarEntRet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int linhasAtualizadas;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        linhasAtualizadas = database.update(
                AcessoEntRet.NOME_TABELA_ENT_RET,
                values, selection,
                selectionArgs
        );

        /* houve atualização da tabela */
        if (linhasAtualizadas > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return linhasAtualizadas;
    }

}
