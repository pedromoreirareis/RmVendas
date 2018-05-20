package com.pedromoreirareisgmail.rmvendas.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pedromoreirareisgmail.rmvendas.Fire.Fire;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;
import com.pedromoreirareisgmail.rmvendas.models.ProductSync;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

public class Sync {

    private static List<ProductSync> mListProduct;
    private static ProgressDialog mDialog;

    public static void exportProduct(final Context context, final String companyID, final Cursor cursor) {

        mListProduct = new ArrayList<>();
        mDialog = new ProgressDialog(context);
        mListProduct.clear();

        /* Criar Alert para confirmar de deseja exportar dados */
        AlertDialog.Builder dialogExport = new AlertDialog.Builder(context);
        dialogExport.setTitle(context.getString(R.string.dialog_sync_product_title));
        dialogExport.setMessage(context.getString(R.string.dialog_sync_export_product_message));

        /* Usuario cancela a exportação e volta a activity */
        dialogExport.setNegativeButton(context.getString(R.string.dialog_sync_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        if (dialogInterface != null) {

                            dialogInterface.dismiss();
                        }
                    }
                });


        /* Usuario confirmação exportação e inicia o processo para exportar */
        dialogExport.setPositiveButton(context.getString(R.string.dialog_sync_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                /* Verifica se tem internet */
                if (Net.verifyConnect(context)) {

                    /* Informando o usuario que vai iniciar BACKUP */
                    mDialog.setTitle(context.getString(R.string.msg_wait));
                    mDialog.setMessage(context.getString(R.string.msg_delete_export_product));
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();


                    /* Antes de enviar os dados do DB para Firestore, primeiro apaga os dados salvos no Firestore.
                     * Primeiro, faz pesquisa de todos os dados que estão no Firestore */
                    Fire.getRefColProduct(companyID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            final List<String> listDelete = new ArrayList<>();

                            /* Pesquisa realizada com sucesso */
                            if (task.isSuccessful()) {

                                /* Coloca resultados da pesquisa em um List */
                                for (DocumentSnapshot document : task.getResult()) {

                                    listDelete.add(document.getId());
                                }

                                /* Exclui Produtos no DB do Firestore */
                                for (int j = 0; j < listDelete.size(); j++) {

                                    Fire.getRefColProduct(companyID).document(listDelete.get(j)).delete();

                                }

                                /* Coloca dados do Cursor em Objetos, e adiciona os Objetos em um List  de objetos */
                                if (cursor.moveToFirst()) {

                                    // Coloca produtos em objetos e depois em uma lista
                                    for (int i = 0; i < cursor.getCount(); i++) {

                                        Long id = cursor.getLong(cursor.getColumnIndex(EntryProduct._ID));
                                        String name = cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME));
                                        double price = cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE));

                                        mListProduct.add(new ProductSync(id, name, price));

                                        cursor.moveToNext();
                                    }
                                }

                                /* Informausuario que vai iniciar BACKUP */
                                mDialog.setMessage(context.getString(R.string.msg_init_export_product));

                                for (int i = 0; i < mListProduct.size(); i++) {

                                    /* Inicia o BACKUP - Enviando dados para FIRESTORE */
                                    Fire.getRefColProduct(companyID).document(String.format(context.getString(R.string.msg_export_id_name),
                                            String.valueOf(mListProduct.get(i).getId()),
                                            mListProduct.get(i).getName())).set(mListProduct.get(i))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        mDialog.dismiss();

                                                    } else {

                                                        mDialog.dismiss();
                                                        Toast.makeText(context, context.getString(R.string.error_export_backup), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }

                                Toast.makeText(context, context.getString(R.string.msg_export_sucess), Toast.LENGTH_SHORT).show();

                            } else { // Pesquisa não teve sucesso

                                mDialog.dismiss();
                                Toast.makeText(context, context.getString(R.string.error_export_backup), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    Toast.makeText(context, context.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogExport.create().show();
    }

    public static void importProduct(final Context context, final String companyID) {

        mListProduct = new ArrayList<>();
        mDialog = new ProgressDialog(context);
        mListProduct.clear();

        /* Infla layout para ser colocado no AlertDialog */
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.edit_dialog_number, null);
        final EditText etCompanyID = view.findViewById(R.id.et_dialog_company_id);

        /* Cria AlertDialog para confirmar se deseja importar Produtos */
        AlertDialog.Builder dialogImport = new AlertDialog.Builder(context);
        dialogImport.setTitle(context.getString(R.string.dialog_sync_product_title));
        dialogImport.setView(view);

        /* Usuario cancela importação, fecha Alert e volta a activyt */
        dialogImport.setNegativeButton(context.getString(R.string.dialog_sync_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        if (dialogInterface != null) {

                            dialogInterface.dismiss();
                        }
                    }
                });

        /* Usuario confirma que quer fazer Importação */
        dialogImport.setPositiveButton(context.getString(R.string.dialog_sync_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                /* Fecha Teclado */
                ControlViews.hideKeyboard(context, etCompanyID);

                if (Net.verifyConnect(context)) {

                    String userCompanyID = etCompanyID.getText().toString();

                    // Verifica se CompanyID digitado é o que esta salvo no FIRESTORE */
                    if (!userCompanyID.equals(companyID)) {

                        Toast.makeText(context, context.getText(R.string.error_cnpj_invalide), Toast.LENGTH_SHORT).show();

                    } else {

                        /* Dialog para informar que esta iniciando Importação */
                        mDialog.setTitle(context.getString(R.string.msg_wait));
                        mDialog.setMessage(context.getString(R.string.msg_init_import_product));
                        mDialog.setCancelable(false);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();

                        /* Inicia o processo de importação, fazendo pesquisa no Banco de dados */
                        Fire.getRefColProduct(companyID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                /* Se pesquisa teve sucesso */
                                if (task.isSuccessful()) {

                                    /* Coloca dados do FIRESTORE em uma lista de Object */
                                    for (DocumentSnapshot document : task.getResult()) {

                                        mListProduct.add(
                                                new ProductSync(
                                                        Objects.requireNonNull(document.getLong("id")),
                                                        Objects.requireNonNull(document.get("name")).toString(),
                                                        Objects.requireNonNull(document.getDouble("price"))
                                                )
                                        );
                                    }

                                    /* Se pesquisa não retornou dados */
                                    if (mListProduct.size() == Const.NUMBER_ZERO) {

                                        mDialog.setMessage(context.getString(R.string.msg_import_product_null));


                                    } else { // Apagar Produtos DB e salvar backup restaurado no lugar

                                        int countProduct = SearchDB.searchCountPorduct(context);

                                        int deletes = 0;

                                        if (countProduct > 0) {
                                            /* Apaga dados do DB SQLite */
                                            deletes = context.getContentResolver().delete(EntryProduct.CONTENT_URI_PRODUCT, null, null);
                                        }

                                        /* Se excluir dados do SQLite com sucesso */
                                        if (deletes > 0 || countProduct == 0) {

                                            int quantityInsert = 0;

                                            /* Salva dados vindo do FIRESTORE no SQLite */
                                            for (int i = 0; i < mListProduct.size(); i++) {

                                                ContentValues values = new ContentValues();
                                                values.put(EntryProduct.COLUMN_NAME, mListProduct.get(i).getName());
                                                values.put(EntryProduct.COLUMN_PRICE, mListProduct.get(i).getPrice());

                                                if (context.getContentResolver().insert(EntryProduct.CONTENT_URI_PRODUCT, values) != null) {

                                                    quantityInsert++;
                                                }
                                            }

                                            /* Informa quantos registro foram salvos */
                                            Toast.makeText(context, String.format(context.getString(R.string.msg_sucess_insert_product),
                                                    String.valueOf(quantityInsert), String.valueOf(mListProduct.size())), Toast.LENGTH_SHORT).show();

                                        } else { /* Erro ao excluir dados do SQLite */

                                            Toast.makeText(context, context.getString(R.string.dialog_edit_del_error), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    mDialog.dismiss();
                                } else {

                                    mDialog.dismiss();
                                    Toast.makeText(context, context.getString(R.string.error_import_backup), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                } else {

                    Toast.makeText(context, context.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogImport.create().show();
    }

}
