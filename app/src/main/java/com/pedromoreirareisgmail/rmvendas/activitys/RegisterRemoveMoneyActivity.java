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
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.models.CashMove;

import static com.pedromoreirareisgmail.rmvendas.Utils.TimeDate.getDateTime;

public class RegisterRemoveMoneyActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener {

    private EditText mEtValue;
    private EditText mEtDescription;

    private Context mContext;
    private CashMove cashMove;

    private boolean isDataChanged = false;
    private boolean isFormatCurrencyUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_remove_money);

        initViews();
        initListenerAndObject();

        if (cashMove.getUri() == null) {

            // Se Uri nulo, então é uma nova entrada
            setTitle(R.string.title_remove_money_register_add);

        } else {

            // Se uri tiver dados, então deve fazer pesquisa para editar o registro
            setTitle(R.string.title_remove_money_register_edit);
            getLoaderManager().initLoader(
                    ConstLoader.LOADER_REGISTER_REMOVE_MONEY,
                    null,
                    this
            );
        }

        // Faz controle da entrada de dados no edit
        watcherControl();

        // Retira foco e coloca o valor zero no mEtValue
        ControlViews.noFocusAndZero(mEtValue);

        // Verifica se houve alteração do texto em mEtDescription
        if (!isDataChanged) {

            isDataChanged = Verify.dataChanged(mEtDescription);
        }
    }

    private void initViews() {

        // Referencia itens do layout
        mEtValue = findViewById(R.id.et_value);
        mEtDescription = findViewById(R.id.et_description);
    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = RegisterRemoveMoneyActivity.this;

        // Instanciando o Objeto CashMove
        cashMove = new CashMove();

        /* Tem dados - Editar / Não tem dados - Adicionar*/
        Intent intent = getIntent();
        cashMove.setUri(intent.getData());

        // Verifica se foi clicado um EditorAction
        mEtDescription.setOnEditorActionListener(this);

        // Monitora toques em uma view especifica
        mEtValue.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Salva dados no BD
            case R.id.action_salvar:
                saveDataDB();
                return true;

            /* Botão Up - Verifica se algum dado foi alterado, ou houve tentativa de alteração
             * Em caso afirmativo abre Dialog para escolha se deseja descartar alteração ou se deseja
             * continuar alterado */
            case android.R.id.home:
                if (!isDataChanged) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Messages.homePressed(
                        mContext,
                        RegisterRemoveMoneyActivity.this
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botao voltar (embaixo) - Verifica se houve alteração
     * Se houve - Abre dialog para confirmar se deseja descartar alterações ou não*/
    @Override
    public void onBackPressed() {

        if (!isDataChanged) {

            super.onBackPressed();
        }

        Messages.backPressed(
                mContext,
                RegisterRemoveMoneyActivity.this
        );
    }

    /* Salva dados no BD
     * Recebe dados do edits, faz validações, coloca dados no objeto values e salva no BD*/
    private void saveDataDB() {

        String value = mEtValue.getText().toString().trim();
        String description = mEtDescription.getText().toString().trim();

        double valueDouble = Formatting.currencyToDouble(value);

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

        cashMove.setValue(valueDouble);
        cashMove.setDescription(description);

        // Coloca dados no objeto values
        ContentValues values = new ContentValues();
        values.put(EntryCashMove.COLUMN_VALUE, cashMove.getValue());
        values.put(EntryCashMove.COLUMN_DESCRIPTION, cashMove.getDescription());
        values.put(EntryCashMove.COLUMN_TYPE, ConstDB.TYPE_REMOVE_MONEY_CASHMOVE);


        if (cashMove.getUri() == null) { /* Adicionando registro */


            values.put(EntryCashMove.COLUMN_TIMESTAMP, getDateTime());

            Crud.insert(mContext, EntryCashMove.CONTENT_URI_CASHMOVE, values);

        } else { /* Editando Registro */

            values.put(EntryCashMove.COLUMN_TIMESTAMP, cashMove.getTimestamp());

            Crud.update(mContext, cashMove.getUri(), values);
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

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

        if (cursor.moveToFirst()) {

            cashMove.setValue(cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE)));
            cashMove.setDescription(cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION)));
            cashMove.setTimestamp(cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP)));

            mEtValue.setText(Formatting.doubleDBToString(cashMove.getValue()));
            mEtDescription.setText(cashMove.getDescription());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (view.getId()) {

            case R.id.et_value:

                mEtValue.requestFocus();
                mEtValue.setSelection(mEtValue.getText().length());
                ControlViews.showKeyboard(mContext, mEtValue);

                return mEtValue.performClick();

            default:
                return false;
        }
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {

            saveDataDB();
            return true;
        }

        return false;
    }

    /* Verifica a entrada de caracteres em um edit
     */
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

                if (!isDataChanged) {

                    isDataChanged = true;
                }

                if (isFormatCurrencyUpdated) {
                    isFormatCurrencyUpdated = false;
                    return;
                }

                isFormatCurrencyUpdated = true;

                mEtValue.setText(Formatting.currencyToStringToCurrency(charSequence.toString().trim()));
                mEtValue.setSelection(mEtValue.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
