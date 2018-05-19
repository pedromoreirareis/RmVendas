package com.pedromoreirareisgmail.rmvendas.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;
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
import com.pedromoreirareisgmail.rmvendas.models.ProductSync;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

public class Sync {

    private static boolean isDelete = false;
    private static List<ProductSync> mListProduct;
    private static ProgressDialog mDialog;

    public static void exportProduct(final Context context, final String companyID, final Cursor cursor) {

        mListProduct = new ArrayList<>();
        mDialog = new ProgressDialog(context);

        mListProduct.clear();

        AlertDialog.Builder dialogExport = new AlertDialog.Builder(context);

        dialogExport.setTitle(context.getString(R.string.dialog_sync_product_title));
        dialogExport.setMessage(context.getString(R.string.dialog_sync_export_product_message));
        dialogExport.setNegativeButton(context.getString(R.string.dialog_sync_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        if (dialogInterface != null) {

                            dialogInterface.dismiss();
                        }
                    }
                });

        dialogExport.setPositiveButton(context.getString(R.string.dialog_sync_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                final List<String> listDelete = new ArrayList<>();
                isDelete = false;

                Fire.getRefColProduct(companyID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {


                                listDelete.add(document.getId());


                            }

                            if(listDelete.size() > 0){

                                isDelete = true;
                            }

                            for(int j = 0; j < listDelete.size(); j++){

                                Fire.getRefColProduct(companyID).document(listDelete.get(j)).delete();

                            }


                            if (isDelete) {


                                if (Net.verifyConnect(context)) {

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

                                    mDialog.setTitle(context.getString(R.string.msg_wait));
                                    mDialog.setMessage(context.getString(R.string.msg_init_export_product));
                                    mDialog.setCancelable(false);
                                    mDialog.setCanceledOnTouchOutside(false);
                                    mDialog.show();

                                    for (int i = 0; i < mListProduct.size(); i++) {

                                        Fire.getRefColProduct(companyID)
                                                .document(String.format(context.getString(R.string.msg_export_id_name),
                                                        String.valueOf(mListProduct.get(i).getId()),
                                                        mListProduct.get(i).getName()))
                                                .set(mListProduct.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    mDialog.dismiss();
                                                    Toast.makeText(context, context.getString(R.string.msg_export_sucess), Toast.LENGTH_SHORT).show();

                                                } else {

                                                    mDialog.dismiss();
                                                    Toast.makeText(context, context.getString(R.string.error_export_backup), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {

                                    Toast.makeText(context, context.getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                                }

                            }

                            //mDialog.dismiss();

                        } else {

                           // mDialog.dismiss();
                          //  Toast.makeText(context, context.getString(R.string.error_import_backup), Toast.LENGTH_SHORT).show();
                        }


                    }
                });





            }
        });

        dialogExport.create().show();
    }

    public static void importProduct(final Context context) {

        mListProduct = new ArrayList<>();
        mDialog = new ProgressDialog(context);

        mListProduct.clear();

        final String companyID = PrefsUser.getCompanyCnpj(context);


        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.edit_dialog_number, null);
        final EditText etCompanyID = view.findViewById(R.id.et_dialog_company_id);


        AlertDialog.Builder dialogImport = new AlertDialog.Builder(context);
        dialogImport.setView(view);

        dialogImport.setTitle(context.getString(R.string.dialog_sync_product_title));
        dialogImport.setView(view);

        dialogImport.setNegativeButton(context.getString(R.string.dialog_sync_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        if (dialogInterface != null) {

                            dialogInterface.dismiss();
                        }
                    }
                });


        dialogImport.setPositiveButton(context.getString(R.string.dialog_sync_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Net.verifyConnect(context)) {

                    String userCompanyID = etCompanyID.getText().toString();

                    if (!userCompanyID.equals(companyID)) {

                        etCompanyID.setError(context.getString(R.string.error_cnpj_invalide));
                        Toast.makeText(context, context.getText(R.string.error_cnpj_invalide), Toast.LENGTH_SHORT).show();
                        return;

                    } else {

                        ControlViews.hideKeyboard(context, etCompanyID);

                        mDialog.setTitle(context.getString(R.string.msg_wait));
                        mDialog.setMessage(context.getString(R.string.msg_init_import_product));
                        mDialog.setCancelable(false);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();

                        Fire.getRefColProduct(companyID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {

                                    for (DocumentSnapshot document : task.getResult()) {


                                        String name = Objects.requireNonNull(document.get("name")).toString();
                                        Long id = Objects.requireNonNull(document.getLong("id"));
                                        double price = Objects.requireNonNull(document.getDouble("price"));

                                        mListProduct.add(new ProductSync(id, name, price));

                                        Log.v("TAG_P", String.valueOf(id) + "-*-*-*-*-" + name + "*+*+*+*" + String.valueOf(price) + "\n");
                                    }

                                    if (mListProduct.size() == Const.NUMBER_ZERO) {

                                        mDialog.setMessage(context.getString(R.string.msg_import_product_null));


                                    } else { // Apagar Produtos DB e salvar backup restaurado no lugar


                                        int deletes = context.getContentResolver().delete(EntryProduct.CONTENT_URI_PRODUCT, null, null);


                                        if (deletes > 0) { // Sucesso ao excluir DB Product

                                            int quantityInsert = 0;

                                            for (int i = 0; i < mListProduct.size(); i++) {


                                                ContentValues values = new ContentValues();
                                                values.put(EntryProduct.COLUMN_NAME, mListProduct.get(i).getName());
                                                values.put(EntryProduct.COLUMN_PRICE, mListProduct.get(i).getPrice());


                                                if (context.getContentResolver().insert(EntryProduct.CONTENT_URI_PRODUCT, values) != null) {

                                                    quantityInsert++;
                                                }
                                            }

                                            Toast.makeText(context, String.format(context.getString(R.string.msg_sucess_insert_product),
                                                    String.valueOf(quantityInsert), String.valueOf(mListProduct.size())), Toast.LENGTH_SHORT).show();

                                        } else { // Erro ao excluir DB Product

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
