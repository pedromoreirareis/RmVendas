package com.pedromoreirareisgmail.rmvendas.Utils;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.dialogTitle;
import static android.R.attr.onClick;

public class UtilsDialog {


    /**
     * @param context               Contexto da Activity
     * @param classActivity         Activity que sera aberta pela intent
     * @param uri                   uri produto clicadado
     * @param dialogTitle           titulo do dialog principal
     * @param dialogExcluirTitle    titulo do dialog de exclusão
     * @param dialogExcluirCancelar dialog de exclusao opção cancelar
     * @param dialogExcluirExcluir  dialog de exclusao opção excluir
     * @param dialogExcluirSucesso  dialog de exclusao msg sucesso
     * @param dialogExcluirErro     dialog de exclusao msg erro
     * @param nomeProd              nome do Produto do item clicado
     */
    public static void editarExcluir(
            final Context context,
            final Class classActivity,
            final Uri uri,
            String dialogTitle,
            final String dialogExcluirTitle,
            final String dialogExcluirCancelar,
            final String dialogExcluirExcluir,
            final String dialogExcluirSucesso,
            final String dialogExcluirErro,
            final String nomeProd

    ) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(dialogTitle);

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

                        builderExcluir.setTitle(dialogExcluirTitle);
                        builderExcluir.setMessage("\n" + nomeProd);

                        builderExcluir.setPositiveButton(
                                dialogExcluirExcluir,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        int linhaExcluida = context.getContentResolver().delete(
                                                uri,
                                                null,
                                                null);

                                        if (linhaExcluida > 0) {

                                            Toast.makeText(context,
                                                    dialogExcluirSucesso,
                                                    Toast.LENGTH_SHORT).show();
                                        } else {

                                            Toast.makeText(context,
                                                    dialogExcluirErro,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        builderExcluir.setNegativeButton(
                                dialogExcluirCancelar, null);

                        builderExcluir.create().show();

                        break;
                }
            }
        });

        builder.create().show();
    }

    /**
     *
     * @param context contexto
     * @param prodCadAltTitulo titulo do dialog
     * @param prodCadAltContinuar titulo com botão setpositive
     * @param prodCadAltDescartar titulo botão set negative
     * @param descartarButClickListener Listener criado dizendo o que sera feito para descartar
     */
    public static void confirmarAlteracao(
            Context context,
            String prodCadAltTitulo,
            String prodCadAltContinuar,
            String prodCadAltDescartar,
            DialogInterface.OnClickListener descartarButClickListener
    ) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(prodCadAltTitulo);

        dialog.setPositiveButton(prodCadAltContinuar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        dialog.setNegativeButton(prodCadAltDescartar, descartarButClickListener);

        dialog.create().show();
    }


}

