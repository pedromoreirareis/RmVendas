package com.pedromoreirareisgmail.rmvendas.activitys;

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
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.constantes.Const.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;

public class SaldoInicialCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener {

    private static final String TAG = SaldoInicialCadActivity.class.getSimpleName();
    private static final int LOADER_RET_CAD = 0;

    private EditText mEtValor;

    private String mDataHoraBD = null;
    private Uri mUriAtual = null;
    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_cad);

        Log.v(TAG, "onCreate");

        initViews();
        initIntents();

        // Se mUriAtual tiver for nulo então vai adicionar, se tiver dados vai editar
        if (mUriAtual == null) {

            setTitle(R.string.title_saldo_inicial_cad_add);

        } else {

            setTitle(R.string.title_saldo_inicial_cad_edit);
            getLoaderManager().initLoader(LOADER_RET_CAD, null, this);
        }

        /* Define o EditorAction do teclado, refrente a view mEtValor
         * Quando o teclado estiver aberto referente ao edit mEtValor, o EditorAction sera DONE
         */
        mEtValor.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Verifica a entrada de caractres em um edit especifico
        controleTextWatcher();

        // define qual ação sera tomada ao clicar no EditorAction
        mEtValor.setOnEditorActionListener(this);

        // Monitora se há toques em uma view especifica
        mEtValor.setOnTouchListener(this);

        // Retira o foco e coloca o valor zero no edit
        Utilidades.semFocoZerado(mEtValor);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia intens do layout
        mEtValor = findViewById(R.id.et_valor);
    }

    private void initIntents() {

        Log.v(TAG, "initIntents");

        // Recebe dados de SaldoInicialListActivity
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

            /* Menu Up
             * Se dados foram alterados abre Dialog para decidir se os dados alterados vao ser
              * descartados ou se vão ser mantidos e a alteração continuara
             */
            case android.R.id.home:

                if (!isDadosAlterado) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Dialogos.homeDescartarConfirmar(
                        SaldoInicialCadActivity.this,
                        SaldoInicialCadActivity.this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botão voltar (embaixo)
     * Verifica se houve alterações, se houve abre Dialog para decidir se as alterações vao ser
     * descartadas ou se continuara a fazendo alterações
     */
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        Dialogos.onBackPressedDescartarConfirmar(
                SaldoInicialCadActivity.this,
                SaldoInicialCadActivity.this);
    }

    /* Salva dados no BD
     * Recebe dados dos edits, faz validações, coloca dados no objeto values e salva no BD
     */
    private void salvarDadosBD() {

        Log.v(TAG, "salvarDadosBD - Inicio");

        String valorEditText = mEtValor.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorEditText);

        // Valor não pode ser zero
        if (valorDouble == NUMERO_ZERO) {
            mEtValor.setError(getString(R.string.error_valor_valido));
            mEtValor.requestFocus();
            return;
        }

        // Coloca dados no objeto values
        ContentValues values = new ContentValues();
        values.put(AcessoSaldo.VALOR, valorDouble);

        // Salva dados no BD
        if (mUriAtual == null) {

            values.put(AcessoSaldo.DATA_HORA, obterDataHoraSistema());

            Crud.inserir(SaldoInicialCadActivity.this, AcessoSaldo.CONTENT_URI_SALDO_INICIAL, values);

            Log.v(TAG, "salvarDadosBD - inserir");

        } else {

            values.put(AcessoSaldo.DATA_HORA, mDataHoraBD);

            Crud.editar(SaldoInicialCadActivity.this, mUriAtual, values);

            Log.v(TAG, "salvarDadosBD - editar");
        }

        Log.v(TAG, "salvarDadosBD - Fim");

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        String[] projection = {
                AcessoSaldo._ID,
                AcessoSaldo.DATA_HORA,
                AcessoSaldo.VALOR
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

            double valorBD = cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.VALOR));
            mDataHoraBD = cursor.getString(cursor.getColumnIndex(AcessoSaldo.DATA_HORA));

            mEtValor.setText(String.valueOf(valorBD * 100));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nao implementado
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");

        switch (view.getId()) {

            case R.id.et_valor:
                mEtValor.requestFocus();
                mEtValor.setSelection(mEtValor.getText().length());
                Utilidades.mostrarTeclado(SaldoInicialCadActivity.this, mEtValor);
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

    /* Verifica a entrada de caracteres nos edits*/
    private void controleTextWatcher() {

        Log.v(TAG, "controleTextWatcher");

        /* O teclado por esse edit possui apenas numeros, faz a captura desses caracteres e formata
         * para o estilo moeda (currency) para ser apresentado ao usuario
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
