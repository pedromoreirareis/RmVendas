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
import com.pedromoreirareisgmail.rmvendas.Utils.ControlViews;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.Verify;
import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.TimeData.getDateTime;
import static com.pedromoreirareisgmail.rmvendas.constant.Const.MIN_CARACT_10;
import static com.pedromoreirareisgmail.rmvendas.constant.Const.NUMERO_ZERO;

public class RegisterRemoveMoneyActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener {

    private static final String TAG = RegisterRemoveMoneyActivity.class.getSimpleName();
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
        setContentView(R.layout.activity_register_remove_money);

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

            isDadosAlterado = Verify.dataChanged(mEtDescricao);
        }

        // Verifica se foi clicado um EditorAction
        mEtDescricao.setOnEditorActionListener(this);

        // Monitora toques em uma view especifica
        mEtValor.setOnTouchListener(this);

        // Retira foco e coloca o valor zero no mEtValor
        ControlViews.noFocusAndZero(mEtValor);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mEtValor = findViewById(R.id.et_valor);
        mEtDescricao = findViewById(R.id.et_descricao);
    }

    private void initIntents() {

        Log.v(TAG, "initIntents");

        // Recebe dados da activity ListRemoveMoneyActivity
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

                Messages.homePressed(
                        RegisterRemoveMoneyActivity.this,
                        RegisterRemoveMoneyActivity.this);

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

        Messages.backPressed(
                RegisterRemoveMoneyActivity.this,
                RegisterRemoveMoneyActivity.this);
    }

    /* Salva dados no BD
     * Recebe dados do edits, faz validações, coloca dados no objeto values e salva no BD
     */
    private void salvarDadosBD() {

        Log.v(TAG, "salvarDadosBD - Inicio");

        String valorEditText = mEtValor.getText().toString().trim();
        String descricaoEditText = mEtDescricao.getText().toString().trim();

        double valorDouble = Formatting.currencyToDouble(valorEditText);

        // Campo nao pode ficar vazio
        if (TextUtils.isEmpty(descricaoEditText)) {

            mEtDescricao.setError(getString(R.string.error_empty_description));
            mEtDescricao.requestFocus();
            return;
        }

        // Quantidade minima de caracteres aceita nesse campo
        if (descricaoEditText.length() < MIN_CARACT_10) {

            mEtDescricao.setError(getString(R.string.error_lenght_description_10));
            mEtDescricao.requestFocus();
            return;
        }

        if (valorDouble == NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valide_value));
            mEtValor.requestFocus();
            return;
        }

        // Coloca dados no objeto values
        ContentValues values = new ContentValues();
        values.put(EntryCashMove.COLUMN_VALUE, valorDouble);
        values.put(EntryCashMove.COLUMN_DESCRIPTION, descricaoEditText);
        values.put(EntryCashMove.COLUMN_TYPE, ConstDB.TYPE_CASHMOVE_REMOVE_MONEY);

        /* Salva dados no banco de dados
         * Se mUriAtual tiver vazio (null) vai adicionar registro
         * Se mUriAtual contiver o Uri de um registro, vai editar um registro
         */
        if (mUriAtual == null) {

            values.put(EntryCashMove.COLUMN_TIMESTAMP, getDateTime());

            Crud.insert(RegisterRemoveMoneyActivity.this, EntryCashMove.CONTENT_URI_CASHMOVE, values);

            Log.v(TAG, "salvarDadosBD - inserir");

        } else {

            values.put(EntryCashMove.COLUMN_TIMESTAMP, mDataHoraBD);

            Crud.update(RegisterRemoveMoneyActivity.this, mUriAtual, values);

            Log.v(TAG, "salvarDadosBD - editar");
        }

        Log.v(TAG, "salvarDadosBD - Fim");

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        String[] projection = {
                EntryCashMove._ID,
                EntryCashMove.COLUMN_TIMESTAMP,
                EntryCashMove.COLUMN_DESCRIPTION,
                EntryCashMove.COLUMN_TYPE,
                EntryCashMove.COLUMN_VALUE
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
                    cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));

            String descricaoBD = cursor.getString(
                    cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION));

            mDataHoraBD = cursor.getString(
                    cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP));

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
                ControlViews.showKeyboard(RegisterRemoveMoneyActivity.this, mEtValor);
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

                mEtValor.setText(Formatting.currencyToStringToCurrency(charSequence.toString().trim()));
                mEtValor.setSelection(mEtValor.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
