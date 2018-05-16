package com.pedromoreirareisgmail.rmvendas.Utils;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;

import java.util.Calendar;

public class Messages {

    /**
     * Cria um Dialog simples para exibir informações ao usuario
     *
     * @param context Contexto da Activity - Activity que solicitou o Dialog
     * @param title   Titulo do Dialog de informação
     * @param message Mensagem do Dialog de informação
     */
    public static void displayData(Context context, String title, String message) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.create().show();
    }

    /**
     * Geral
     * Cria Dialog com opções de editar ou excluir um registro no Banco de Dados
     * Excluir  - Com o Uri do registro, faz a exclusão
     * Editar   - Com o Uri do regitro, abre um Activity preparada para edição do registro
     *
     * @param context       Contexto da Activity - Activity que solicitou o Dialog
     * @param classActivity Activity que sera aberta pela intent - sera aberta se for editar
     * @param uri           Uri do registro clicadado - usado para excluir ou editar
     * @param messageDelete nome ou descricao do registro do item clicado - apresentado no dialogo
     */
    public static void editOurDelete(
            final Context context,
            final Class classActivity,
            final Uri uri,
            final String messageDelete
    ) {

        final AlertDialog.Builder builderSelection = new AlertDialog.Builder(context);

        // Titulo do Dialog de escolha
        builderSelection.setTitle(R.string.dialog_edit_del_title);

        /* Itens de escolha do Dialog */
        builderSelection.setItems(R.array.array_edit_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {

                        switch (item) {

                            // Editar - Abre um Activity para editar registro do Uri
                            case 0:
                                Intent intent = new Intent(context, classActivity);
                                intent.setData(uri);
                                context.startActivity(intent);
                                break;


                            //Excluir - Exclui registro do Uri no Banco de Dados
                            case 1:

                                confirmDelete(context, uri, messageDelete);

                                break;
                        }
                    }
                }
        );

        builderSelection.create().show();
    }

    /**
     * Pesquisa divida cliente
     * Cria Dialog com opções de editar ou excluir um registro no Banco de Dados
     * Editar   - Com o Uri do regitro, abre um Activity preparada para edição do registro
     * Excluir  - Com o Uri do registro, faz a exclusão, se cliente não tiver nenhuma venda a prazo
     * ou não tiver saldo devedor
     *
     * @param context       Contexto da Activity - Activity que solicitou o Dialog
     * @param classActivity Activity que sera aberta pela intent - sera aberta se for editar
     * @param uri           Uri do registro clicadado - usado para excluir ou editar
     * @param messageDelete nome ou descricao do registro do item clicado - apresentado no dialogo
     */
    public static void editOurDelete(
            final Context context,
            final Class classActivity,
            final Uri uri,
            final long id,
            final String messageDelete
    ) {

        final AlertDialog.Builder builderSelection = new AlertDialog.Builder(context);

        // Titulo do Dialog de escolha
        builderSelection.setTitle(R.string.dialog_edit_del_title);

        /* Itens de escolha do Dialog */
        builderSelection.setItems(R.array.array_edit_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {

                        switch (item) {

                            // Editar - Abre um Activity para editar registro do Uri
                            case 0:
                                Intent intent = new Intent(context, classActivity);
                                intent.setData(uri);
                                context.startActivity(intent);
                                break;


                            //Excluir - Exclui registro do Uri no Banco de Dados
                            case 1:

                                // Verifica se o cliente tem divida ou credito antes de excluir
                                Double receivable = SearchDB.receivable(context, id);

                                if (receivable == 0) {

                                    // Cliente não possui crédito nem divida
                                    confirmDelete(context, uri, messageDelete);

                                } else if (receivable > 0) {

                                    // Tem crédito
                                    Toast.makeText(
                                            context,
                                            String.format(
                                                    context.getString(R.string.dialog_edit_del_conf_credit)
                                                    , Formatting.doubleToCurrency(receivable)),
                                            Toast.LENGTH_LONG
                                    ).show();

                                } else if (receivable < 0) {

                                    // Tem divida
                                    Toast.makeText(
                                            context,
                                            String.format(
                                                    context.getString(R.string.dialog_edit_del_conf_debt)
                                                    , Formatting.doubleToCurrency(receivable)),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                                break;
                        }
                    }
                }
        );

        builderSelection.create().show();
    }

    /* Antes de excluir solicitação cnfirmação ao usuario se realmente quer excluir */
    private static void confirmDelete(final Context context, final Uri uri, String messageDelete) {


        // Cria um novo Dialog para confirmar exclusão
        AlertDialog.Builder builderDelete =
                new AlertDialog.Builder(context);

        builderDelete.setTitle(R.string.dialog_edit_del_conf_delete_title);
        builderDelete.setMessage("\n" + messageDelete);

        // Exclusão confirmada
        builderDelete.setPositiveButton(

                R.string.dialog_edit_del_conf_delete_del,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Exclui registro do Uri no banco de dados
                        int deletes = context.getContentResolver().delete(
                                uri,
                                null,
                                null);

                        if (deletes > 0) {

                            // Sucesso
                            Toast.makeText(
                                    context,
                                    R.string.dialog_edit_del_sucess,
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            // Erro
                            Toast.makeText(
                                    context,
                                    R.string.dialog_edit_del_error,
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                    }
                }
        );

        // Exclusão cancelada
        builderDelete.setNegativeButton(
                R.string.dialog_edit_del_conf_delete_cancel,
                null
        );

        builderDelete.create().show();
    }

    public static void deleteReceive(
            final Context context,
            final Uri uri,
            final String messageDelete
    ) {


        final AlertDialog.Builder builderSelection = new AlertDialog.Builder(context);

        // Titulo do Dialog de escolha
        builderSelection.setTitle(R.string.dialog_edit_del_title);

        /* Itens de escolha do Dialog */
        builderSelection.setItems(R.array.array_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {

                        switch (item) {

                            //Excluir - Exclui registro do Uri no Banco de Dados
                            case 0:

                                // Cria um novo Dialog para confirmar exclusão
                                AlertDialog.Builder builderDelete = new AlertDialog.Builder(context);

                                builderDelete.setTitle(R.string.dialog_edit_del_conf_delete_title);
                                builderDelete.setMessage("\n" + messageDelete);

                                // Exclusão confirmada
                                builderDelete.setPositiveButton(

                                        R.string.dialog_edit_del_conf_delete_del,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                int deletes;

                                                // Exclui registro do Uri no banco de dados
                                                deletes = context.getContentResolver().delete(
                                                        uri,
                                                        null,
                                                        null);

                                                if (deletes > 0) {

                                                    // Sucesso
                                                    Toast.makeText(
                                                            context,
                                                            R.string.dialog_edit_del_sucess,
                                                            Toast.LENGTH_SHORT
                                                    ).show();


                                                } else {

                                                    // Erro
                                                    Toast.makeText(
                                                            context,
                                                            R.string.dialog_edit_del_error,
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                }
                                            }
                                        }
                                );

                                // Exclusão cancelada
                                builderDelete.setNegativeButton(
                                        R.string.dialog_edit_del_conf_delete_cancel,
                                        null
                                );

                                builderDelete.create().show();

                                break;
                        }
                    }
                }
        );

        builderSelection.create().show();

    }

    /**
     * Abre o calendário para escolha de uma data
     *
     * @param context         Activity onde o calendario sera aberto
     * @param dateSetListener Ouvinte que indica que data foi selecionada
     */
    public static void dialogCalendar(Context context, DatePickerDialog.OnDateSetListener dateSetListener) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                dateSetListener,
                year, month, day);

        dialog.show();
    }


    /**
     * Botao voltar (embaixo) - Verifica se houve alteração
     * Se houve - Abre dialog para confirmar se deseja descartar alterações ou não
     *
     * @param context  Cntexto da activity
     * @param activity activity que sera fechada caso for descartar as alterações
     */
    public static void backPressed(final Context context, final Activity activity) {

        DialogInterface.OnClickListener discartClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        activity.finish();
                    }
                };

        Messages.continueOrDiscart(
                context,
                discartClickListener
        );
    }

    /**
     * Se o botao home foi pressionado define o que fazer
     *
     * @param context  Contexto da activity
     * @param activity Activity que sera fechada caso for descartar as alterações
     */
    public static void homePressed(Context context, final Activity activity) {

        /* Alterado - Abre Dialog para confirmar se deseja continuar alterando, nesse caso
         * permanece na Activity atual ou de deseja descartar alteração, nesse caso volta
         * para activity que chamou a ActivityAtual */
        DialogInterface.OnClickListener discartClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        NavUtils.navigateUpFromSameTask(activity);
                    }
                };

        /* Chama o metodo para descartar alterações */
        Messages.continueOrDiscart(
                context,
                discartClickListener
        );
    }

    /**
     * Dialog para confirmar se deseja descartar uma alteração/inclusão ou se deseja continuar
     * alterando/editando
     *
     * @param context              contexto
     * @param discartClickListener Listener criado dizendo o que sera feito para descartar
     */
    public static void continueOrDiscart(
            Context context,
            DialogInterface.OnClickListener discartClickListener
    ) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.dialog_alteration_title);

        // Continuar editando
        dialog.setPositiveButton(R.string.dialog_alteration_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (dialogInterface != null) {

                    dialogInterface.dismiss();
                }
            }
        });

        // Descartar edição
        dialog.setNegativeButton(R.string.dialog_alteration_discart, discartClickListener);

        dialog.create().show();
    }

    public static void editOurDeleteSell(
            final Context context,
            final View viewSnack,
            final Uri uriSell,
            final Uri uriReceive,
            final String messageDelete
    ) {

        final AlertDialog.Builder builderSelection = new AlertDialog.Builder(context);

        // Titulo do Dialog de escolha
        builderSelection.setTitle(R.string.dialog_edit_del_title);

        /* Itens de escolha do Dialog */
        builderSelection.setItems(R.array.array_edit_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {

                        switch (item) {

                            // Editar - Abre um Activity para editar registro do Uri
                            case 0:
                                /*Intent intent = new Intent(context, classActivity);
                                intent.setData(uriSell);
                                context.startActivity(intent); */
                                Snackbar.make(viewSnack, context.getString(R.string.error_no_edition_exclude),
                                        Snackbar.LENGTH_LONG).show();
                                break;


                            //Excluir - Exclui registro do Uri no Banco de Dados
                            case 1:

                                // Cria um novo Dialog para confirmar exclusão
                                AlertDialog.Builder builderDelete =
                                        new AlertDialog.Builder(context);

                                builderDelete.setTitle(R.string.dialog_edit_del_conf_delete_title);
                                builderDelete.setMessage("\n" + messageDelete);

                                // Exclusão confirmada
                                builderDelete.setPositiveButton(

                                        R.string.dialog_edit_del_conf_delete_del,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                // Exclui registro do Uri no banco de dados
                                                int deletesSell = context.getContentResolver().delete(
                                                        uriSell, null, null);

                                                int deletesReceive = context.getContentResolver().delete(
                                                        uriReceive, null, null);

                                                if (deletesSell > 0 && deletesReceive > 0) {

                                                    // Sucesso
                                                    Toast.makeText(
                                                            context,
                                                            R.string.dialog_edit_del_sucess,
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                } else {

                                                    // Erro
                                                    Toast.makeText(
                                                            context,
                                                            R.string.dialog_edit_del_error,
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                }
                                            }
                                        }
                                );

                                // Exclusão cancelada
                                builderDelete.setNegativeButton(
                                        R.string.dialog_edit_del_conf_delete_cancel,
                                        null
                                );


                                builderDelete.create().show();

                                break;
                        }
                    }
                }
        );

        builderSelection.create().show();
    }
}

