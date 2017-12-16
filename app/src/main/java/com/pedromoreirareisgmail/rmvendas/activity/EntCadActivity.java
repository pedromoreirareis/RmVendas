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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_QUANT_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;

public class EntCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener {

    private static final int LOADER_ENT_CAD = 0;

    private EditText mEtValor;
    private EditText mEtDescricao;

    private Uri mUriAtual = null;
    private String mDataHoraBD = null;

    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ent_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();


        if (mUriAtual == null) {

            setTitle(R.string.title_ent_cad_add);

        } else {

            setTitle(R.string.title_ent_cad_edit);
            getLoaderManager().initLoader(LOADER_ENT_CAD, null, this);
        }

        mEtValor = (EditText) findViewById(R.id.et_valor);
        mEtDescricao = (EditText) findViewById(R.id.et_descricao);

        mEtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isDadosAlterado = true;

                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtValor.setText(Formatar.formatarParaCurrency(charSequence.toString().trim()));
                mEtValor.setSelection(mEtValor.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtDescricao.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    salvarDadosBD();
                    return true;
                }

                return false;
            }
        });

        mEtValor.setOnTouchListener(this);


        Utilidades.semFocoZerado(mEtValor);
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
                                NavUtils.navigateUpFromSameTask(EntCadActivity.this);
                            }
                        };

                Dialogos.dialogoConfirmarAlteracao(
                        EntCadActivity.this,
                        descartarButClickListener
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void salvarDadosBD() {

        String valorEditText = mEtValor.getText().toString().trim();
        String descricaoEditText = mEtDescricao.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorEditText);

        if (TextUtils.isEmpty(valorEditText)) {

            mEtValor.setError(getString(R.string.error_campo_vazio));
            mEtValor.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(descricaoEditText)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio));
            mEtDescricao.requestFocus();
            return;
        }

        if (valorDouble <= NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            mEtValor.requestFocus();
            return;
        }

        if (descricaoEditText.length() < MIN_QUANT_CARACT) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_10));
            mEtDescricao.requestFocus();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoEntRet.VALOR, valorDouble);
        values.put(AcessoEntRet.DESCRICAO, descricaoEditText);
        values.put(AcessoEntRet.TIPO, Constantes.TIPO_ENTRADA_CAIXA);

        if (mUriAtual == null) {

            values.put(AcessoEntRet.DATA, obterDataHoraSistema());

            Crud.inserir(EntCadActivity.this, AcessoEntRet.CONTENT_URI_ENT_RET, values);

        } else {

            values.put(AcessoEntRet.DATA, mDataHoraBD);

            Crud.editar(EntCadActivity.this, mUriAtual, values);
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
                EntCadActivity.this,
                descartarButClickListener
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoEntRet._ID,
                AcessoEntRet.DATA,
                AcessoEntRet.DESCRICAO,
                AcessoEntRet.TIPO,
                AcessoEntRet.VALOR
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

            double valorBD = cursor.getDouble(
                    cursor.getColumnIndex(AcessoEntRet.VALOR));

            String descricaoBD = cursor.getString(
                    cursor.getColumnIndex(AcessoEntRet.DESCRICAO));

            mDataHoraBD = cursor.getString(
                    cursor.getColumnIndex(AcessoEntRet.DATA));

            mEtValor.setText(String.valueOf(valorBD * 100));
            mEtDescricao.setText(descricaoBD);
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
                mEtValor.requestFocus();
                mEtValor.setSelection(mEtValor.getText().length());
                Utilidades.mostrarTeclado(EntCadActivity.this, mEtValor);
                return true;

            default:
                return false;
        }
    }
}
