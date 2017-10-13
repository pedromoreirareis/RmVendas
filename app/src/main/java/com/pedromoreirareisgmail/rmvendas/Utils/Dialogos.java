package com.pedromoreirareisgmail.rmvendas.Utils;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;

import java.util.Calendar;

public class Dialogos {

    /**
     * @param context        Contexto da Activity - activity atual
     * @param classActivity  Activity que sera aberta pela intent - sera aberta se for editar
     * @param uri            uri produto clicadado - usado para excluir ou editar
     * @param mensagemExcluir nome ou descricao do Produto do item clicado - apresentado no dialogo
     */
    public static void dialogoEditarExcluir(
            final Context context,
            final Class classActivity,
            final Uri uri,
            final String mensagemExcluir
    ) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.dialog_exc_edit_tilte);


        builder.setItems(R.array.array_editar_excluir_prod, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {

                switch (item) {

                    // Editar
                    case 0:
                        Intent intent = new Intent(context, classActivity);
                        intent.setData(uri);
                        context.startActivity(intent);
                        break;

                    //Excluir
                    case 1:

                        AlertDialog.Builder builderExcluir =
                                new AlertDialog.Builder(context);

                        builderExcluir.setTitle(R.string.dialog_exc_edit_conf_exc_title);
                        builderExcluir.setMessage("\n" + mensagemExcluir);

                        builderExcluir.setPositiveButton(
                                R.string.dialog_exc_edit_conf_exc_excluir,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        int excluidas = context.getContentResolver().delete(
                                                uri,
                                                null,
                                                null);

                                        if (excluidas > 0) {

                                            Toast.makeText(context,
                                                    R.string.dialog_exc_edit_sucesso,
                                                    Toast.LENGTH_SHORT).show();

                                        } else {

                                            Toast.makeText(context,
                                                    R.string.dialog_exc_edit_erro,
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                        builderExcluir.setNegativeButton(
                                R.string.dialog_exc_edit_conf_exc_cancelar, null);

                        builderExcluir.create().show();

                        break;
                }
            }
        });
        builder.create().show();
    }

    /**
     * @param context                   contexto
     * @param descartarButClickListener Listener criado dizendo o que sera feito para descartar
     */
    public static void dialogoConfirmarAlteracao(
            Context context,
            DialogInterface.OnClickListener descartarButClickListener
    ) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.dialog_alt_titulo);

        dialog.setPositiveButton(R.string.dialog_alt_continuar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        dialog.setNegativeButton(R.string.dialog_alt_descatar, descartarButClickListener);

        dialog.create().show();
    }

    /**
     * Abre o calendário para escolha de uma data
     *
     * @param context          activity onde o calendario sera aberto
     * @param mDateSetListener entrada da data
     */
    public static void dialogoDatas(Context context, DatePickerDialog.OnDateSetListener mDateSetListener) {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                mDateSetListener,
                year, month, day);
        dialog.show();
    }

}

