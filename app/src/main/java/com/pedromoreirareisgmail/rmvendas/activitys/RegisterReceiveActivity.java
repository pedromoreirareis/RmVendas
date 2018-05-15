package com.pedromoreirareisgmail.rmvendas.activitys;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.ControlViews;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstTag;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.models.Client;
import com.pedromoreirareisgmail.rmvendas.models.Receive;

import static com.pedromoreirareisgmail.rmvendas.Utils.TimeDate.getDateTime;
import static com.pedromoreirareisgmail.rmvendas.constant.Const.MIN_CARACT_10;
import static com.pedromoreirareisgmail.rmvendas.constant.Const.NUMBER_ZERO;
import static com.pedromoreirareisgmail.rmvendas.constant.ConstDB.TYPE_CREDIT;
import static com.pedromoreirareisgmail.rmvendas.constant.ConstDB.TYPE_DEBIT;
import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;

public class RegisterReceiveActivity extends AppCompatActivity implements
        EditText.OnTouchListener,
        Button.OnClickListener {

    private static final String TAG = ConstTag.TAG_MAIN + RegisterReceiveActivity.class.getSimpleName();

    private Button mButSell;
    private Button mButReceip;
    private EditText mEtDescription;
    private EditText mEtValue;

    private Context mContext;
    private Client client;
    private Receive receive;

    private boolean isDataChaged = false;
    private boolean isFormatCurrencyUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_receive);

        Log.v(TAG, "onCreate");

        initViews();
        initListenerAndObject();
        initTitleDate();

        // Controla a entrada de caracteres nos edits
        watcherControl();

        // Retira foco e coloca o valor zero no edit
        ControlViews.noFocusAndZero(mEtValue);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia os itens do layout
        mButSell = findViewById(R.id.but_receive_debit);
        mButReceip = findViewById(R.id.but_receive_credit);
        mEtDescription = findViewById(R.id.et_receive_description);
        mEtValue = findViewById(R.id.et_receive_value);

    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = RegisterReceiveActivity.this;

        // Instancia o objeto Receive
        receive = new Receive();

        // Instancia o objeto Client
        client = new Client();

        Intent intent = getIntent();
        client.setUri(intent.getData());

        if (intent.hasExtra(ConstIntents.INTENT_CLIENT_DATA)) {

            client = intent.getParcelableExtra(ConstIntents.INTENT_CLIENT_DATA);
        }

        /* Botão click venda */
        mButSell.setOnClickListener(this);

        /* Botão click recebimento */
        mButReceip.setOnClickListener(this);

        // Monitora se houve toque em mEtValue
        mEtValue.setOnTouchListener(this);
    }

    private void initTitleDate() {

        /* Coloca o nome do cliente no titulo da Activity*/
        setTitle(client.getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {

            /* Menu Up
             * Verifica se houve alteração, se houve, da opção de continuar editando ou
             * volta a activity anterior*/
            case android.R.id.home:

                dadosAlterados();

                if (!isDataChaged) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Messages.homePressed(
                        mContext,
                        RegisterReceiveActivity.this
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botao voltar (embaixo)
     * Verifica se houve alteração, se houve, da opção de continuar editando ou
     * volta a activity anterior*/
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        dadosAlterados();

        if (!isDataChaged) {

            super.onBackPressed();
        }

        Messages.backPressed(
                mContext,
                RegisterReceiveActivity.this
        );
    }

    /* Verifica se os dados foram alterados
     * Se o valor for maior que zero, ou
     * Se o campo descrição não estiver vazio, e considerado que os dados foram alterados*/
    private void dadosAlterados() {

        Log.v(TAG, "dadosAlterados");

        String description = mEtDescription.getText().toString().trim();
        String value = mEtValue.getText().toString().trim();

        double valor = Formatting.currencyToDouble(value);

        isDataChaged = valor > 0 || !description.isEmpty();
    }

    private void saveDataDB(int type) {

        Log.v(TAG, "saveDataDB - Inicio");

        String description = mEtDescription.getText().toString().trim();
        String value = mEtValue.getText().toString().trim();

        double valueDouble = Formatting.currencyToDouble(value);

        // Campo não pode ser vazio
        if (description.isEmpty()) {

            mEtDescription.setError(getString(R.string.error_empty_description));
            mEtDescription.requestFocus();
            return;
        }

        // Campo deve ter pelo menos 10 caracteres
        if (description.length() < MIN_CARACT_10) {

            mEtDescription.setError(getString(R.string.error_lenght_description_10));
            mEtDescription.requestFocus();
            return;
        }

        // Valor não pode ser negativo
        if (valueDouble == NUMBER_ZERO) {

            mEtValue.setError(getString(R.string.error_valide_value));
            mEtValue.requestFocus();
            return;
        }

        receive.setClientId(client.getId());
        receive.setClientName(client.getName());
        receive.setDescription(description);
        receive.setTimestamp(getDateTime());
        receive.setType(type);
        receive.setValue(valueDouble);

        // Cria objeto values a recebe dados em campos tipo chave valor para salvar no BD
        ContentValues values = new ContentValues();

        values.put(EntryReceive.COLUMN_CLIENT_ID, Integer.parseInt(String.valueOf(receive.getClientId())));
        values.put(EntryReceive.COLUMN_CLIENT_NAME, receive.getClientName());
        values.put(EntryReceive.COLUMN_TIMESTAMP, receive.getTimestamp());
        values.put(EntryReceive.COLUMN_DESCRIPTION, receive.getDescription());
        values.put(EntryReceive.COLUMN_TYPE, receive.getType());
        values.put(EntryReceive.COLUMN_VALUE, receive.getValue());

        // Salva dados no BD
        Crud.insert(mContext, EntryReceive.CONTENT_URI_RECEIVE, values);

        finish();

        Log.v(TAG, "saveDataDB - Fim");
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");

        int id = view.getId();

        switch (id) {

            // Recebe o foco, coloca o cursor no fim e abre o teclado se tiver fechado
            case R.id.et_receive_value:
                mEtValue.requestFocus();
                mEtValue.setSelection(mEtValue.getText().length());
                ControlViews.showKeyboard(mContext, mEtValue);
                return true;

            default:
                return false;
        }
    }

    /* Controla a entrada de caracteres */
    private void watcherControl() {

        Log.v(TAG, "watcherControl");

        /* Nesse edit entrara apenas caracteres numericos. Ao entrar um caracteres ele sera capturado
         * e enviado para formatação, e sera apresentao ao usuario em formato moeda (currency)*/
        mEtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (isFormatCurrencyUpdate) {

                    isFormatCurrencyUpdate = false;
                    return;
                }

                isFormatCurrencyUpdate = true;

                mEtValue.setText(Formatting.currencyToStringToCurrency(charSequence.toString().trim()));
                mEtValue.setSelection(mEtValue.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        Log.v(TAG, "onCreateLoader");

        switch (view.getId()) {

            case R.id.but_receive_debit:
                saveDataDB(TYPE_DEBIT);
                break;

            case R.id.but_receive_credit:
                saveDataDB(TYPE_CREDIT);
                break;
        }
    }
}

