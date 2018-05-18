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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.ControlViews;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryOpening;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.models.Opening;

import static com.pedromoreirareisgmail.rmvendas.Utils.TimeDate.getDateTime;
import static com.pedromoreirareisgmail.rmvendas.constant.Const.NUMBER_ZERO;

public class RegisterOpeningActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener,
        View.OnClickListener {

    private EditText mEtValue;
    private Button mButClearValue;

    private Opening opening;
    private Context mContext;

    private boolean isDataChaged = false;
    private boolean isFormatCurrencyUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_opening);

        initViews();
        initListenerAndObject();

        // Se mUriInitial tiver for nulo então vai adicionar, se tiver dados vai editar
        if (opening.getUri() == null) { // Adicionar

            setTitle(R.string.title_opening_register_add);

        } else { // Editar

            setTitle(R.string.title_opening_register_edit);
            getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_OPENING, null, this);
        }

        // Faz controle de entrada de dados no edit
        watcherControl();

        // Retira o foco e coloca o valor zero
        ControlViews.noFocusAndZero(mEtValue);
    }

    private void initViews() {

        // Referencia intens do layout
        mEtValue = findViewById(R.id.et_value);
        mButClearValue = findViewById(R.id.but_clear_et_value);
    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = RegisterOpeningActivity.this;

        opening = new Opening();

        Intent intent = getIntent();
        opening.setUri(intent.getData());

        /* EditorAction sera Done - No layout esta definido como Next que é para as outras Activitys*/
        mEtValue.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // define qual ação sera tomada ao clicar no EditorAction
        mEtValue.setOnEditorActionListener(this);

        // Monitora se há toques em uma view especifica
        mEtValue.setOnTouchListener(this);

        mButClearValue.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Salva dados no DB
            case R.id.action_salvar:
                saveDataDB();
                return true;

            /* Botão Up
             * Se dados foram alterados abre Dialog para decidir se os dados alterados vao ser
             * descartados ou se vão ser mantidos e a alteração continuara*/
            case android.R.id.home:

                if (!isDataChaged) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Messages.homePressed(
                        mContext,
                        RegisterOpeningActivity.this
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botão voltar (embaixo)
     * Verifica se houve alterações, se houve abre Dialog para decidir se as alterações vao ser
     * descartadas ou se continuara a fazendo alterações*/
    @Override
    public void onBackPressed() {

        if (!isDataChaged) {

            super.onBackPressed();
        }

        Messages.backPressed(
                mContext,
                RegisterOpeningActivity.this
        );
    }

    private void saveDataDB() {

        String value = mEtValue.getText().toString().trim();

        double valueDouble = Formatting.currencyToDouble(value);

        // Valor não pode ser zero
        if (valueDouble == NUMBER_ZERO) {

            mEtValue.setError(getString(R.string.error_valide_value));
            mEtValue.requestFocus();
            return;
        }

        opening.setValue(valueDouble);

        // Coloca os dados para salvar em um Objeto values
        ContentValues values = new ContentValues();
        values.put(EntryOpening.COLUMN_VALUE, opening.getValue());

        if (opening.getUri() == null) { // Adicionar

            values.put(EntryOpening.COLUMN_TIMESTAMP, getDateTime());

            Crud.insert(mContext, EntryOpening.CONTENT_URI_OPENING, values);

        } else { // Editar

            values.put(EntryOpening.COLUMN_TIMESTAMP, opening.getTimestamp());

            Crud.update(mContext, opening.getUri(), values);
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                EntryOpening._ID,
                EntryOpening.COLUMN_TIMESTAMP,
                EntryOpening.COLUMN_VALUE
        };

        return new CursorLoader(
                mContext,
                opening.getUri(),
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            opening.setValue(cursor.getDouble(cursor.getColumnIndex(EntryOpening.COLUMN_VALUE)));
            opening.setTimestamp(cursor.getString(cursor.getColumnIndex(EntryOpening.COLUMN_TIMESTAMP)));

            mEtValue.setText(Formatting.doubleDBToString(opening.getValue()));
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
                return true;

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

    /* Verifica a entrada de caracteres nos edits*/
    private void watcherControl() {

        /* O teclado para esse edit possui apenas numeros, faz a captura desses caracteres e formata
         * para o estilo moeda (currency) para ser apresentado ao usuario*/
        mEtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // Se o valor for acima de zero,fica visivel
                if (Formatting.charSequenceToDouble(charSequence) > 0) {

                    mButClearValue.setVisibility(View.VISIBLE);

                } else {

                    mButClearValue.setVisibility(View.GONE);
                }

                if (!isDataChaged) {

                    isDataChaged = true;
                }

                if (isFormatCurrencyUpdate) {
                    isFormatCurrencyUpdate = false;
                    return;
                }

                isFormatCurrencyUpdate = true;

                mEtValue.setText(Formatting.currencyToStringToCurrency(charSequence.toString().trim()));
                mEtValue.setSelection(mEtValue.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.but_clear_et_value:
                mEtValue.setText(Const.NUMBER_ZERO_STRING);
                break;
        }
    }
}
