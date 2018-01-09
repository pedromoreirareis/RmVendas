package com.pedromoreirareisgmail.rmvendas.db;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;

public class Crud {

    private static final String TAG = Crud.class.getSimpleName();

    /**
     * Insere um registro no Banco de Dados
     *
     * @param context Contexto da Activity
     * @param uri     Uri do registro a ser inserido - parte do Uri
     * @param values  Dados a ser inserido
     */
    public static void inserir(Context context, Uri uri, ContentValues values) {

        Log.v(TAG, "Crud - inserir");

        Uri newUri = context.getContentResolver().insert(uri, values);

        if (newUri != null) {
            Toast.makeText(context, "Inserido com sucesso", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(context, "Erro ao inserir", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Edita um registro no BD
     *
     * @param context Contexto da Activity
     * @param uri     Uri do registro a ser Editado
     * @param values  dados a serem editados(Atualizados)
     */
    public static void editar(Context context, Uri uri, ContentValues values) {

        Log.v(TAG, "Crud - editar");

        int editadas = context.getContentResolver().update(uri, values, null, null);

        if (editadas > 0) {

            Toast.makeText(context, R.string.msg_editado_sucesso, Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(context, R.string.msg_editado_erro, Toast.LENGTH_SHORT).show();
        }
    }

}
