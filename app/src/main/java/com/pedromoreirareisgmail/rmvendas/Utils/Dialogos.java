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
     * Cria um Dialog simples para exibir informações ao usuario
     *
     * @param context  Contexto da Activity - Activity que solicitou o Dialog
     * @param titulo   Titulo do Dialog de informação
     * @param mensagem Mensagem do Dialog de informação
     */
    public static void dialogoExibirDados(Context context, String titulo, String mensagem) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(titulo);

        builder.setMessage(mensagem);

        builder.create().show();
    }


    /**
     * Cria um Dialog com opções de editar ou excluir um registro no BD
     * Excluir  - Com o Uri do registro, faz a exclusão
     * Editar   - Com o Uri do regitro, abre um Activity preparada para edição do registro
     *
     * @param context         Contexto da Activity - Activity que solicitou o Dialog
     * @param classActivity   Activity que sera aberta pela intent - sera aberta se for editar
     * @param uri             Uri do registro clicadado - usado para excluir ou editar
     * @param mensagemExcluir nome ou descricao do registro do item clicado - apresentado no dialogo
     */
    public static void dialogoEditarExcluir(
            final Context context,
            final Class classActivity,
            final Uri uri,
            final String mensagemExcluir
    ) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.dialog_exc_edit_tilte);

        /*Itens de escolha do Dialog*/
        builder.setItems(R.array.array_editar_excluir_prod, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {

                switch (item) {

                    // Editar - Abre um Activity para editar registro do Uri
                    case 0:
                        Intent intent = new Intent(context, classActivity);
                        intent.setData(uri);
                        context.startActivity(intent);
                        break;

                    //Excluir - Exclui registro do Uri no BD
                    case 1:

                        // Cria um novo Dialog para confirmar exclusão
                        AlertDialog.Builder builderExcluir =
                                new AlertDialog.Builder(context);

                        builderExcluir.setTitle(R.string.dialog_exc_edit_conf_exc_title);
                        builderExcluir.setMessage("\n" + mensagemExcluir);

                        // Excluir registro
                        builderExcluir.setPositiveButton(
                                R.string.dialog_exc_edit_conf_exc_excluir,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        // Exclui registro do Uri no BD
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

                        // Cancela exclusão
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
     * Dialog para confirmar se deseja descartar uma alteraçã/inclusão ou se deseja continuar
     * alterando/editando
     *
     * @param context                   contexto
     * @param descartarButClickListener Listener criado dizendo o que sera feito para descartar
     */
    public static void dialogoConfirmarAlteracao(
            Context context,
            DialogInterface.OnClickListener descartarButClickListener
    ) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.dialog_alt_titulo);

        // Continuar editando
        dialog.setPositiveButton(R.string.dialog_alt_continuar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Descartar edição
        dialog.setNegativeButton(R.string.dialog_alt_descatar, descartarButClickListener);

        dialog.create().show();
    }

    /**
     * Abre o calendário para escolha de uma data
     *
     * @param context          Activity onde o calendario sera aberto
     * @param mDateSetListener Entrada da data
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

