package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;
import com.pedromoreirareisgmail.rmvendas.db.Crud;


public class ClientesCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener {

    public static final int LOADER_CLIENTES_CAD = 0;

    private EditText mEtNome;
    private EditText mEtFone;

    private Uri mUriAtual = null;

    private boolean isDadosAlterado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_cad);

        /* Se Activity foi aberta para alteração, vair receber uma Uri*/
        Intent intent = getIntent();
        mUriAtual = intent.getData();

        /* Se não foi recebido Uri, Activity vai adicionar registro. Se recebeu vai editar.*/
        if (mUriAtual == null) {

            setTitle("Titulo Add");

        } else {

            /* Se for editar fara pesquisa na banco de dados para pegar dados do Uri passado*/
            setTitle("Titulo Editar");
            getLoaderManager().initLoader(LOADER_CLIENTES_CAD, null, this);
        }

        // Referencia itens do layout
        mEtNome = (EditText) findViewById(R.id.et_clientes_nome);
        mEtFone = (EditText) findViewById(R.id.et_clientes_num_fone);

        mEtFone.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_salvar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_salvar:
                salvarDadosBD();
                return true;

            case android.R.id.home:
                if (!isDadosAlterado) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener descartarButClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(ClientesCadActivity.this);
                            }
                        };

                Dialogos.dialogoConfirmarAlteracao(
                        ClientesCadActivity.this,
                        descartarButClickListener
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void salvarDadosBD() {

        String nomeEditText = mEtNome.getText().toString().trim();
        String foneEditText = mEtFone.getText().toString().trim();

        if (TextUtils.isEmpty(nomeEditText)) {

            mEtNome.setError(getString(R.string.error_campo_vazio));
            mEtNome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(foneEditText)) {

            mEtFone.setError(getString(R.string.error_campo_vazio));
            mEtFone.requestFocus();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(AcessoClientes.NOME, nomeEditText);
        values.put(AcessoClientes.TELEFONE, foneEditText);

        if (mUriAtual == null) {


            Crud.inserir(ClientesCadActivity.this, AcessoClientes.CONTENT_URI_CLIENTES, values);

        } else {


            Crud.editar(ClientesCadActivity.this, mUriAtual, values);
        }

        finish();


    }

    @Override
    public void onBackPressed() {

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        DialogInterface.OnClickListener descartarButClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        Dialogos.dialogoConfirmarAlteracao(
                ClientesCadActivity.this,
                descartarButClickListener
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                AcessoClientes._ID,
                AcessoClientes.NOME,
                AcessoClientes.TELEFONE
        };

        return new CursorLoader(
                this,
                mUriAtual,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            String nomeBD = cursor.getString(
                    cursor.getColumnIndex(AcessoClientes.NOME));

            int foneBD = cursor.getInt(
                    cursor.getColumnIndex(AcessoClientes.TELEFONE));


            mEtNome.setText(nomeBD);
            mEtFone.setText(foneBD);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        switch (id) {

            // Recebe o foco e coloca o cursor no fim
            case R.id.et_valor:
                mEtFone.requestFocus();
                Utilidades.mostrarTeclado(ClientesCadActivity.this, mEtFone);
                return true;

            default:
                return false;
        }
    }


}
