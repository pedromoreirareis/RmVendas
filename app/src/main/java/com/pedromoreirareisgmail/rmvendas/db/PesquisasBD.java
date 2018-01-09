package com.pedromoreirareisgmail.rmvendas.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class PesquisasBD {


    public static String Pesuisarcliente(Context context, int idCliente) {

        DbHelper mDbHelper = new DbHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                Contrato.AcessoClientes._ID,
                Contrato.AcessoClientes.NOME
        };

        String selection = Contrato.AcessoClientes._ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(idCliente)};

        Cursor cursor = db.query(
                Contrato.AcessoClientes.TABELA_CLIENTES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        cursor.moveToFirst();

        String nomeCliente = cursor.getString(cursor.getColumnIndex(Contrato.AcessoClientes.NOME));

        db.close();

        return nomeCliente;
    }


}
