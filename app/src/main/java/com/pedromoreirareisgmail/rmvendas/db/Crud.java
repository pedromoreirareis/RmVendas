package com.pedromoreirareisgmail.rmvendas.db;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.constantes.ConstTag;

public class Crud {

    private static final String TAG = ConstTag.TAG_MAIN + Crud.class.getSimpleName();

    /**
     * Enviar dados ao Provider para inserção de um registro
     *
     * @param context Contexto da Activity
     * @param uri     Uri do registro a ser inserido - parte do Uri
     * @param values  Dados a ser inserido
     */
    public static void insert(Context context, Uri uri, ContentValues values) {

        Log.v(TAG, "CRUD INSERT - inserir registro");

        Uri newUri = context.getContentResolver().insert(uri, values);

        if (newUri != null) {

            Toast.makeText(context, R.string.msg_inserido_sucesso, Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(context, R.string.msg_inserido_erro, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Enviar dados ao Provider para atualização de um registro
     *
     * @param context Contexto da Activity
     * @param uri     Uri do registro a ser Editado
     * @param values  dados a serem editados(Atualizados)
     */
    public static void update(Context context, Uri uri, ContentValues values) {

        Log.v(TAG, "CRUD UPDATE - atualizar registro");

        int updates = context.getContentResolver().update(uri, values, null, null);

        if (updates > 0) {

            Toast.makeText(context, R.string.msg_editado_sucesso, Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(context, R.string.msg_editado_erro, Toast.LENGTH_SHORT).show();
        }
    }

}
