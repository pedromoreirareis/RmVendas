package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
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
import android.util.Log;
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

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_CARACT_10;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;

public class RetCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener {

    private static final String TAG = RetCadActivity.class.getSimpleName();
    private static final int LOADER_RET_CAD = 0;

    private EditText mEtValor;
    private EditText mEtDescricao;

    private String mDataHoraBD = null;
    private Uri mUriAtual = null;

    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_cad);

        Log.v(TAG, "");

        initViews();
        initIntents();

        // Se mUriAtual tiver vazio, então vai adicionar
        if (mUriAtual == null) {

            setTitle(R.string.title_retirada_cad_add);

        } else {

            setTitle(R.string.title_retirada_cad_edit);
            getLoaderManager().initLoader(LOADER_RET_CAD, null, this);
        }

        // Monitora caracteres digitas no edit
        controleTextWatcher();

        // Verifica se houve alteração do texto em mEtDescricao
        if (!isDadosAlterado) {

            isDadosAlterado = Utilidades.verificarAlteracaoDados(mEtDescricao);
        }

        // Verifica se foi clicado um EditorAction
        mEtDescricao.setOnEditorActionListener(this);

        // Monitora toques em uma view especifica
        mEtValor.setOnTouchListener(this);

        // Retira foco e coloca o valor zero no mEtValor
        Utilidades.semFocoZerado(mEtValor);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mEtValor = findViewById(R.id.et_valor);
        mEtDescricao = findViewById(R.id.et_descricao);
    }

    private void initIntents() {

        Log.v(TAG, "initIntents");

        // Recebe dados da activity RetListActivity
        Intent intent = getIntent();
        mUriAtual = intent.getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {

            // Salva dados no BD
            case R.id.action_salvar:
                salvarDadosBD();
                return true;

            /* Menu Up -
             * Verifica se algum dado foi alterado, caso tenha sido alterado abre Dialog para decidir
             * se deseja descartar alterações ou se deseja continuar alterando
             */
            case android.R.id.home:
                if (!isDadosAlterado) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Dialogos.homeDescartarConfirmar(
                        RetCadActivity.this,
                        RetCadActivity.this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botão voltar (embaixo)
     * Se tiver ocorrido alteração abre Dialog para decidir se vai descartar a alteração ou se
     * vai continuar alterando dados
     */
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        Dialogos.onBackPressedDescartarConfirmar(
                RetCadActivity.this,
                RetCadActivity.this);
    }

    /* Salva dados no BD
     * Recebe dados do edits, faz validações, coloca dados no objeto values e salva no BD
     */
    private void salvarDadosBD() {

        Log.v(TAG, "salvarDadosBD - Inicio");

        String valorEditText = mEtValor.getText().toString().trim();
        String descricaoEditText = mEtDescricao.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorEditText);

        // Campo nao pode ficar vazio
        if (TextUtils.isEmpty(descricaoEditText)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio_descricao));
            mEtDescricao.requestFocus();
            return;
        }

        // Quantidade minima de caracteres aceita nesse campo
        if (descricaoEditText.length() < MIN_CARACT_10) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_descricao_10));
            mEtDescricao.requestFocus();
            return;
        }

        if (valorDouble == NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_valido));
            mEtValor.requestFocus();
            return;
        }

        // Coloca dados no objeto values
        ContentValues values = new ContentValues();
        values.put(AcessoEntRet.VALOR, valorDouble);
        values.put(AcessoEntRet.DESCRICAO, descricaoEditText);
        values.put(AcessoEntRet.TIPO, Constantes.TIPO_RETIRADA_CAIXA);

        /* Salva dados no banco de dados
         * Se mUriAtual tiver vazio (null) vai adicionar registro
         * Se mUriAtual contiver o Uri de um registro, vai editar um registro
         */
        if (mUriAtual == null) {

            values.put(AcessoEntRet.DATA_HORA, obterDataHoraSistema());

            Crud.inserir(RetCadActivity.this, AcessoEntRet.CONTENT_URI_ENT_RET, values);

            Log.v(TAG, "salvarDadosBD - inserir");

        } else {

            values.put(AcessoEntRet.DATA_HORA, mDataHoraBD);

            Crud.editar(RetCadActivity.this, mUriAtual, values);

            Log.v(TAG, "salvarDadosBD - editar");
        }

        Log.v(TAG, "salvarDadosBD - Fim");

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        String[] projection = {
                AcessoEntRet._ID,
                AcessoEntRet.DATA_HORA,
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

        Log.v(TAG, "onLoadFinished");

        if (cursor.moveToFirst()) {

            double valorBD = cursor.getDouble(
                    cursor.getColumnIndex(AcessoEntRet.VALOR));

            String descricaoBD = cursor.getString(
                    cursor.getColumnIndex(AcessoEntRet.DESCRICAO));

            mDataHoraBD = cursor.getString(
                    cursor.getColumnIndex(AcessoEntRet.DATA_HORA));

            mEtValor.setText(String.valueOf(valorBD * 100));
            mEtDescricao.setText(descricaoBD);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");

        switch (view.getId()) {

            case R.id.et_valor:
                mEtValor.requestFocus();
                mEtValor.setSelection(mEtValor.getText().length());
                Utilidades.mostrarTeclado(RetCadActivity.this, mEtValor);
                return true;

            default:
                return false;
        }
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

        Log.v(TAG, "onEditorAction");

        if (actionId == EditorInfo.IME_ACTION_DONE) {

            salvarDadosBD();
            return true;
        }

        return false;
    }

    /* Verifica a entrada de caracteres em um edit
     */
    private void controleTextWatcher() {

        Log.v(TAG, "controleTextWatcher");

        /* Edit abre teclado numerico, entao ao entrar um caractere de numero ele é formatado
         *  no estilo de moeda para ser apresentado ao usuario
         */
        mEtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!isDadosAlterado) {

                    isDadosAlterado = true;
                }

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
    }
}
