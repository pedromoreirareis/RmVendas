package com.pedromoreirareisgmail.rmvendas.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SearchDB {


    public static String Pesquisarcliente(Context context, int idCliente) {

        DbHelper mDbHelper = new DbHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                Contract.AcessoClientes._ID,
                Contract.AcessoClientes.NOME
        };

        String selection = Contract.AcessoClientes._ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(idCliente)};

        Cursor cursor = db.query(
                Contract.AcessoClientes.TABELA_CLIENTES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        cursor.moveToFirst();

        String nomeCliente = cursor.getString(cursor.getColumnIndex(Contract.AcessoClientes.NOME));

        db.close();

        return nomeCliente;
    }


}
