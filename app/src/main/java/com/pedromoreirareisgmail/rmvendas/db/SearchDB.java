package com.pedromoreirareisgmail.rmvendas.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SearchDB {


    public static String Pesquisarcliente(Context context, int idCliente) {

        DbHelper mDbHelper = new DbHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                Contract.EntryClient._ID,
                Contract.EntryClient.COLUMN_NAME
        };

        String selection = Contract.EntryClient._ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(idCliente)};

        Cursor cursor = db.query(
                Contract.EntryClient.TABLE_CLIENT,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        cursor.moveToFirst();

        String nomeCliente = cursor.getString(cursor.getColumnIndex(Contract.EntryClient.COLUMN_NAME));

        db.close();

        return nomeCliente;
    }


}
