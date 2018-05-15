package com.pedromoreirareisgmail.rmvendas.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;
import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;


public class SearchDB {


    public static String searchClientName(Context context, long id) {

        DbHelper mDbHelper = new DbHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                EntryClient._ID,
                EntryClient.COLUMN_NAME
        };

        String selection = EntryClient._ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        Cursor cursor = db.query(
                EntryClient.TABLE_CLIENT,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_NAME));

        cursor.close();
        db.close();

        return name;
    }


    /**
     * Faz pesquisa nos registros de vendas a prazo de um cliente especifico
     * Verifica o valor do saldo que o cliente tem.
     * Cliente pode esta em debito, ter credito, estar zerado ou nunca ter comprado a prazo.
     *
     * @param context   Contexto da Activity
     * @param idCliente id do cliente (Cliente tem um id unico que é usado em todos seus registros)
     * @return O valor do saldo do cliente
     */
    public static double receivable(Context context, long idCliente) {

        double sales = 0;
        double receivables = 0;
        double total = 0;

        DbHelper mDbHelper = new DbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                EntryReceive._ID,
                EntryReceive.COLUMN_CLIENT_ID,
                EntryReceive.COLUMN_CLIENT_NAME,
                EntryReceive.COLUMN_TYPE,
                EntryReceive.COLUMN_TIMESTAMP,
                EntryReceive.COLUMN_VALUE,
                EntryReceive.COLUMN_DESCRIPTION,
                EntryReceive.COLUMN_VALUE
        };

        String selection = EntryReceive.COLUMN_CLIENT_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(idCliente)};
        String sortOrder = EntryReceive.COLUMN_TIMESTAMP;

        Cursor cursor = db.query(
                EntryReceive.TABLE_RECEIVE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {

            if (cursor.getInt(cursor.getColumnIndex(EntryReceive.COLUMN_TYPE)) == ConstDB.TYPE_DEBIT) {

                sales = sales + cursor.getDouble(cursor.getColumnIndex(EntryReceive.COLUMN_VALUE));

            } else {

                receivables = receivables + cursor.getDouble(cursor.getColumnIndex(EntryReceive.COLUMN_VALUE));
            }

            total = receivables - sales;

            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return total;
    }

}
