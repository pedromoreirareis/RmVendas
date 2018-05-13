package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
import com.pedromoreirareisgmail.rmvendas.Utils.ControlViews;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.Verify;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.constant.ConstTag;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.models.CashMove;

import static com.pedromoreirareisgmail.rmvendas.Utils.TimeData.getDateTime;

public class RegisterAddMoneyActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener {

    private static final String TAG = ConstTag.TAG_MAIN + RegisterAddMoneyActivity.class.getSimpleName();

    private EditText mEtValue;
    private EditText mEtDescription;

    private Context mContext;
    private CashMove cashMove;

    private boolean isDataChanged = false;
    private boolean isFormatCurrencyUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_add_money);

        Log.v(TAG, "onCreate");

        initViews();
        initListenerAndObject();

        if (cashMove.getUri() == null) {

            // Se Uri nulo então é uma nova entrada
            setTitle(R.string.title_add_money_register_add);

        } else {

            // Se Uri tiver dados então fazer pesquisar para editar registro
            setTitle(R.string.title_add_money_register_edit);
            getLoaderManager().initLoader(
                    ConstLoader.LOADER_REGISTER_ADD_MONEY,
                    null,
                    this
            );
        }

        // Faz controle de entrada de dados no edit
        watcherControl();

        // Retira o foco e coloca o valor zero
        ControlViews.noFocusAndZero(mEtValue);

        // Verifica se houve alteração no edit descrição
        if (!isDataChanged) {

            isDataChanged = Verify.dataChanged(mEtDescription);
        }
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mEtValue = findViewById(R.id.et_value);
        mEtDescription = findViewById(R.id.et_description);
    }


    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = RegisterAddMoneyActivity.this;

        // Instanciando o Objeto CashMove
        cashMove = new CashMove();

        Intent intentUri = getIntent();
        cashMove.setUri(intentUri.getData());

        // Define o que ação tomar a clicar no botão EditorAction do teclado
        mEtDescription.setOnEditorActionListener(this);

        // Monitora toques no edit de valor
        mEtValue.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {

            // Botao salvar
            case R.id.action_salvar:
                saveDataDB();
                return true;

            /* Botão Up - Verifica se algum dado foi alterado, ou houve tentativa de alteração
             * Em caso afirmativo abre Dialog para escolha se deseja descartar alteração ou se deseja
             * continuar alterado */
            case android.R.id.home:

                // Não foi alterado - Volta a Activity que chamou RegisterAddMoneyActivity
                if (!isDataChanged && ControlViews.noChangedValueDescription(mEtValue, mEtDescription)) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Foi alterado abre Dialog de confirmacao
                Messages.homePressed(
                        mContext,
                        RegisterAddMoneyActivity.this
                );

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /* Botao voltar (embaixo) - Verifica se houve alteração
     * Se houve - Abre dialog para confirmar se deseja descartar alterações ou não
     */
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        if (!isDataChanged && ControlViews.noChangedValueDescription(mEtValue, mEtDescription)) {

            super.onBackPressed();
        }

        Messages.backPressed(
                mContext,
                RegisterAddMoneyActivity.this
        );
    }

    private void saveDataDB() {

        Log.v(TAG, "saveDataDB - Iniciando ");

        Log.v(TAG, "saveDataDB - captureData");

        String value = mEtValue.getText().toString();
        String description = mEtDescription.getText().toString();

        Double valueDouble = Formatting.currencyToDouble(value);

        Log.v(TAG, "saveDataDB - validateCashMove");

        // Valor não pode ser zero
        if (valueDouble == Const.NUMBER_ZERO) {

            mEtValue.setError(getString(R.string.error_valide_value));
            mEtValue.requestFocus();
            return;
        }

        // A descrição não pode fica vazia
        if (description.isEmpty()) {

            mEtDescription.setError(getString(R.string.error_empty_description));
            mEtDescription.requestFocus();
            return;
        }

        // A descrição deve ter pelo menos 10 caracteres
        if (description.length() < Const.MIN_CARACT_10) {

            mEtDescription.setError(getString(R.string.error_lenght_description_10));
            mEtDescription.requestFocus();
            return;
        }

        Log.v(TAG, "saveDataDB - cashMove");

        cashMove.setValue(valueDouble);
        cashMove.setDescription(description);

        Log.v(TAG, "saveDataDB - insertContentValues");

        // Coloca os dados para salvar em um Objeto
        ContentValues values = new ContentValues();
        values.put(EntryCashMove.COLUMN_VALUE, cashMove.getValue());
        values.put(EntryCashMove.COLUMN_DESCRIPTION, cashMove.getDescription());
        values.put(EntryCashMove.COLUMN_TYPE, ConstDB.TYPE_ADD_MONEY_CASHMOVE);


        if (cashMove.getUri() == null) { /* Adicionando registro */

            values.put(EntryCashMove.COLUMN_TIMESTAMP, getDateTime());

            Crud.insert(mContext, EntryCashMove.CONTENT_URI_CASHMOVE, values);

            Log.v(TAG, "Adicionar - adicionou");

        } else { /* Editando Registro */

            values.put(EntryCashMove.COLUMN_TIMESTAMP, cashMove.getTimestamp());

            Crud.update(mContext, cashMove.getUri(), values);

            Log.v(TAG, "Editando - editou");
        }

        Log.v(TAG, "Finalizando salvar BD");

        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        // Retorna todos os dados do registro identificado pelo mUriInitial
        String[] projection = {
                EntryCashMove._ID,
                EntryCashMove.COLUMN_TIMESTAMP,
                EntryCashMove.COLUMN_DESCRIPTION,
                EntryCashMove.COLUMN_TYPE,
                EntryCashMove.COLUMN_VALUE
        };

        return new CursorLoader(
                mContext,
                cashMove.getUri(),
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

            cashMove.setValue(cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE)));
            cashMove.setDescription(cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION)));
            cashMove.setTimestamp(cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP)));

            mEtValue.setText(Formatting.doubleDBToString(cashMove.getValue()));
            mEtDescription.setText(cashMove.getDescription());
        }
    }

    /**
     * O que fazer com os dados no caso de reiniciar pesquisa
     *
     * @param loader Cursor com dados antigos
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");


        switch (view.getId()) {

            /* Recebe o foco, coloca o cursor no fim da string e se teclado tiver fechado abre ele */
            case R.id.et_value:

                view.performClick();

                mEtValue.requestFocus();
                mEtValue.setSelection(mEtValue.getText().length());
                ControlViews.showKeyboard(mContext, mEtValue);
                return true;
            default:
                return false;

        }
    }

    /* Faz o controle da entrada de dados (caracteres) nos edits*/
    private void watcherControl() {

        /* Os caracteres que entram no mEtValue são apenas numeros
         * Na entrada de caracteres envia para fazer uma formatação, para que os caracteres sejam
         * apresentados ao usuario em forma de moeda(currency). Tambem controla o cursor para que ele
         * sempre esteja no fim e não seja possivel apagar um caraceter do centro de um conjunto de
         * caracteres, sem antes apagar todos a sua direita
         */
        mEtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.v(TAG, "controleTextWatcher - mEtValue");

                // Verifica alteração de dados no Edit
                if (!isDataChanged) {

                    isDataChanged = true;
                }

                if (isFormatCurrencyUpdated) {

                    isFormatCurrencyUpdated = false;
                    return;
                }

                isFormatCurrencyUpdated = true;

                mEtValue.setText(Formatting.currencyToStringToCurrency(charSequence.toString()));
                mEtValue.setSelection(mEtValue.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        Log.v(TAG, "onEditorAction");

        // Salvar dados no banco de dados
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            saveDataDB();
            return true;
        }

        return false;
    }
}
