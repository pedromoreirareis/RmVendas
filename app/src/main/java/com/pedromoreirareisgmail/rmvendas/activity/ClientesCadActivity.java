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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MAX_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MAX_CARACT_MSG;

public class ClientesCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_CLIENTES_CAD = 0;

    private EditText mEtNome;
    private EditText mEtFone;
    private final EditText.OnTouchListener mTouchListnerEditFocoCursorFim = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

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
    };
    private Uri mUriAtual = null;
    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (mUriAtual == null) {

            setTitle("Titulo Add");

        } else {

            setTitle("Titulo Editar");
            getLoaderManager().initLoader(LOADER_CLIENTES_CAD, null, this);
        }

        mEtNome = (EditText) findViewById(R.id.et_clientes_nome);
        mEtFone = (EditText) findViewById(R.id.et_clientes_num_fone);

        mEtNome.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CARACT)});

        mEtNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                isDadosAlterado = true;

                if (charSequence.toString().trim().length() > MAX_CARACT_MSG) {

                    Toast.makeText(ClientesCadActivity.this,
                            R.string.msg_max_caract, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtFone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtFone.setOnTouchListener(mTouchListnerEditFocoCursorFim);

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
            return;
        }

        if (TextUtils.isEmpty(foneEditText)) {

            mEtFone.setError(getString(R.string.error_campo_vazio));
            return;
        }


        int foneInt = Integer.parseInt(foneEditText);


        ContentValues values = new ContentValues();
        values.put(AcessoClientes.COLUNA_CLIENTES_NOME, nomeEditText);
        values.put(AcessoClientes.COLUNA_CLIENTES_TELEFONE, foneInt);

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
                AcessoClientes.COLUNA_CLIENTES_NOME,
                AcessoClientes.COLUNA_CLIENTES_TELEFONE
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
                    cursor.getColumnIndex(AcessoClientes.COLUNA_CLIENTES_NOME));

            int foneBD = cursor.getInt(
                    cursor.getColumnIndex(AcessoClientes.COLUNA_CLIENTES_TELEFONE));


            mEtNome.setText(nomeBD);
            mEtFone.setText(String.valueOf(foneBD));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
