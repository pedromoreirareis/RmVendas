package com.pedromoreirareisgmail.rmvendas.data;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;

public class Crud {

    public static void inserir(Context context, Uri uri, ContentValues values) {

        Uri newUri = context.getContentResolver().insert(uri, values);

        if (newUri != null) {
            Toast.makeText(context, "Inserido com sucesso", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(context, "Erro ao inserir", Toast.LENGTH_SHORT).show();
        }
    }

    public static void editar(Context context, Uri uri, ContentValues values) {

        int editadas = context.getContentResolver().update(uri, values, null, null);

        if (editadas > 0) {

            Toast.makeText(context, R.string.msg_editado_sucesso, Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(context, R.string.msg_editado_erro, Toast.LENGTH_SHORT).show();
        }
    }

}
