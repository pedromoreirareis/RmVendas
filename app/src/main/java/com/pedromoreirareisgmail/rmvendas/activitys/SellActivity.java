package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Calculus;
import com.pedromoreirareisgmail.rmvendas.Utils.ControlViews;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.TimeData;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;
import com.pedromoreirareisgmail.rmvendas.models.Client;
import com.pedromoreirareisgmail.rmvendas.models.Receive;
import com.pedromoreirareisgmail.rmvendas.models.Sell;
import com.pedromoreirareisgmail.rmvendas.models.SellToClient;

import java.text.NumberFormat;

import static com.pedromoreirareisgmail.rmvendas.Utils.Calculus.calcularValorTotalVendaString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.charSequenceToDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.charSequenceToString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.editToDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.editToInteger;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.editToString;
import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;


public class SellActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        Button.OnClickListener {

    private static final String TAG = SellActivity.class.getSimpleName();


    private final NumberFormat mFormatCurrency = NumberFormat.getCurrencyInstance();

    private TextView mTvQuantity;
    private TextView mTvProductName;
    private TextView mTvTotalValue;
    private TextView mTvClientName;
    private EditText mEtQuantity;
    private EditText mEtAdd;
    private EditText mEtDiscount;
    private EditText mEtForward;
    private Button mButClient;
    private Switch mSwitchAdd;
    private Switch mSwitchDiscount;
    private Switch mSwitchForward;
    private TextInputLayout mLayoutAdd;
    private TextInputLayout mLayoutDiscount;
    private LinearLayout mLayoutForward;

    private Uri mUriInitial = null;
    //private Uri mUriClient = null;

    private Context mContext;
    private Client client;
    private Sell sell;

    // private double mProductValue = 0;

    private String mValorTotalBundle = "";

    private boolean isAddProduct = false;
    private boolean idDataChanged = false;
    private boolean isFormatCurrencyUpdate = false;
    private boolean isFormatIntegerUpdate = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        Log.v(TAG, "onCreate");

        initViews();
        initObject();

        if (isAddProduct) { // Adicionar

            Log.v(TAG, "mAdicionarProdutoBD - Adicionar :" + mUriInitial.toString());

            setTitle(R.string.title_sell_add);
            getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_SELL_ADD, null, this);

            mTvQuantity.setText(Const.A_UNIT_STRING);
            mEtQuantity.setText(Const.A_UNIT_STRING);
            mEtQuantity.setSelection(mEtQuantity.getText().length());
        }

        if (!isAddProduct) { // Editar

            Log.v(TAG, "!mAdicionarProdutoBD - Editar");

            setTitle(R.string.title_sell_edit);
            getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_SELL_EDIT, null, this);
        }

        // Instancia estado da Activity se tiver salvo
        verifySavedInstanceState(savedInstanceState);

        // Controle da entrada dos edits
        watcherControl();

        // Controle de mudanças dos switchs
        switchControl();

        // Instancia os Listener
        initListener();

        // Foco no edit Quantity
        mEtQuantity.setSelectAllOnFocus(true);

        // Tira o foco e coloca valor zero nos edits
        ControlViews.noFocusAndZero(mEtAdd);
        ControlViews.noFocusAndZero(mEtDiscount);
        ControlViews.noFocusAndZero(mEtForward);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mTvQuantity = findViewById(R.id.tv_sell_quantity);
        mTvProductName = findViewById(R.id.tv_sell_product_name);
        mTvTotalValue = findViewById(R.id.tv_sell_total_value);
        mTvClientName = findViewById(R.id.tv_sell_client_name);
        mButClient = findViewById(R.id.but_sell_client);
        mEtQuantity = findViewById(R.id.et_sell_quantity);
        mEtDiscount = findViewById(R.id.et_sell_discount_value);
        mEtAdd = findViewById(R.id.et_sell_add_value);
        mEtForward = findViewById(R.id.et_sell_forward_value);
        mSwitchAdd = findViewById(R.id.switch_sell_add);
        mSwitchDiscount = findViewById(R.id.switch_sell_discount);
        mSwitchForward = findViewById(R.id.switch_sell_forward);
        mLayoutDiscount = findViewById(R.id.til_sell_discount);
        mLayoutAdd = findViewById(R.id.til_sell_add);
        mLayoutForward = findViewById(R.id.til_sell_forward);
    }

    private void initObject() {

        Log.v(TAG, "initObject");

        // Contexto da Activity
        mContext = SellActivity.this;

        sell = new Sell();

        client = new Client();
        client.setId(Const.ONE_LESS);

        // Recebe dados de ListProductSaleActivity
        Intent intentInitial = getIntent();
        mUriInitial = intentInitial.getData();

        if (intentInitial.hasExtra(ConstIntents.INTENT_ADD_SELL)) {

            // Verificar se vai adicionar ou editar venda
            isAddProduct = intentInitial.getBooleanExtra(ConstIntents.INTENT_ADD_SELL, true);
        }
    }

    private void verifySavedInstanceState(Bundle savedInstanceState) {

        Log.v(TAG, "verifySavedInstanceState");

        if (savedInstanceState != null) {

            Log.v(TAG, "savedInstanceState != null");

            // Tiver dados salvos no Objeto savedInstanceState captura os dados e repassa a Activity
            client = savedInstanceState.getParcelable(Const.SELL_SAVED_INSTANCE_STATE);
            if (client != null) {

                mTvClientName.setText(client.getName());
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v(TAG, "onSaveInstanceState");

        // Salva dados no estado da aplicação para ser usados no retorno da Acitivyt
        if (client.getId() != Const.ONE_LESS && !client.getName().isEmpty()) {

            Client clientOut = new Client();

            clientOut.setId(client.getId());
            clientOut.setName(client.getName());

            outState.putParcelable(Const.SELL_SAVED_INSTANCE_STATE, clientOut);
        }
    }

    private void initListener() {

        Log.v(TAG, "initListener");

        // Botão abre activity ListClientSaleActivity, para selecionar cliente para venda a prazo
        mButClient.setOnClickListener(this);

        // Monitora toques nos edits
        mEtQuantity.setOnTouchListener(this);
        mEtAdd.setOnTouchListener(this);
        mEtDiscount.setOnTouchListener(this);
        mEtForward.setOnTouchListener(this);

        // Monitora toques nos Switchs
        mSwitchAdd.setOnTouchListener(this);
        mSwitchDiscount.setOnTouchListener(this);
        mSwitchForward.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {

        Log.v(TAG, "onClick - but_vend_quant_cliente");

        /* Abre activity para buscar cliente para uma venda a prazo */
        if (view.getId() == R.id.but_sell_client) {


            Intent intentClientList = new Intent(
                    mContext,
                    ListClientSaleActivity.class
            );

            client.setId(Const.ONE_LESS);
            client.setName("");

            SellToClient sellToClient = new SellToClient();
            sellToClient.setUriInitial(mUriInitial);
            sellToClient.setUnitValue(sell.getPrice());

            intentClientList.putExtra(ConstIntents.INTENT_SELL_TO_CLIENT, sellToClient);

            startActivityForResult(intentClientList, Const.COD_RESULT_CLIENT_SELL);

            ControlViews.hideKeyboard(mContext, mButClient);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "onActivityResult");

        // Recebe os dados do cliente para venda a prazo
        if (requestCode == Const.COD_RESULT_CLIENT_SELL && resultCode == RESULT_OK) {

            if (data != null) {

                Log.v(TAG, "onActivityResult -  data != null ");

                client = new Client();
                SellToClient sellToClient = new SellToClient();

                if (data.hasExtra(ConstIntents.INTENT_CLIENT_TO_SELL)) {

                    sellToClient = data.getParcelableExtra(ConstIntents.INTENT_CLIENT_TO_SELL);

                    client.setId(sellToClient.getClientId());
                    //mUriClient = ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, client.getId());
                    client.setUri(ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, client.getId()));
                    getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_SELL_CLIENT, null, this);

                }

                mValorTotalBundle = calcularValorTotalVendaString(
                        editToString(mEtQuantity),
                        sellToClient.getUnitValue(),
                        editToString(mEtAdd),
                        editToString(mEtDiscount),
                        editToString(mEtForward));
            }
        }
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

            // Salva dados no BD
            case R.id.action_salvar:
                saveDataDB();
                return true;

            /* Menu Up
             * Verifica se houve alteração, se houve abre um Dialog para verificar se deseja descatar
             * as alterações e sair da activity ou se deseja continuar na activity e continua alterando*/
            case android.R.id.home:
                if (!idDataChanged) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Messages.homePressed(
                        mContext,
                        SellActivity.this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botão voltar (embaixo)
     * Se dados foram alterados abre Dialog para decidir se ira descatar dados alterados e sair
     * da Activity ou se deseja ficar na activity e continuar as alterações*/
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        if (!idDataChanged) {

            super.onBackPressed();
        }

        Messages.backPressed(
                mContext,
                SellActivity.this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int iLoader, Bundle bundle) {

        /* Adicionar Venda */
        if (iLoader == ConstLoader.LOADER_REGISTER_SELL_ADD) {

            Log.v(TAG, "onCreateLoader - LOADER_VENDA_ADICIONAR");

            String[] projection = {
                    EntryProduct._ID,
                    EntryProduct.COLUMN_PRICE,
                    EntryProduct.COLUMN_NAME
            };

            return new CursorLoader(
                    mContext,
                    mUriInitial,
                    projection,
                    null,
                    null,
                    null
            );
        }

        /* Editar Venda */
        if (iLoader == ConstLoader.LOADER_REGISTER_SELL_EDIT) {

            Log.v(TAG, "onCreateLoader - LOADER_VENDA_EDITAR");

            String[] projection = {
                    EntrySeel._ID,
                    EntrySeel.COLUMN_NAME,
                    EntrySeel.COLUMN_QUANTITY,
                    EntrySeel.COLUMN_TIMESTAMP,
                    EntrySeel.COLUMN_ADD_VALUE,
                    EntrySeel.COLUMN_DISCOUNT_VALUE,
                    EntrySeel.COLUMN_FORWARD_VALUE,
                    EntrySeel.COLUMN_CLIENT_ID,
                    EntrySeel.COLUMN_PRICE,
                    EntrySeel.COLUMN_CLIENT_NAME,
                    EntrySeel.COLUMN_RECEIVE_ID
            };

            return new CursorLoader(
                    mContext,
                    mUriInitial,
                    projection,
                    null,
                    null,
                    null
            );
        }

        /* Pesquisa dados do cliente para venda a prazo */
        if (iLoader == ConstLoader.LOADER_REGISTER_SELL_CLIENT) {

            Log.v(TAG, "onCreateLoader - LOADER_CLIENTE");

            // Trazer todos os dados de um clientes especifico indentificado pelo mUriInitial
            String[] projection = {
                    EntryClient._ID,
                    EntryClient.COLUMN_NAME,
                    EntryClient.COLUMN_FONE
            };

            return new CursorLoader(
                    mContext,
                    client.getUri(),
                    projection,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /* Retorna dados de uma venda especifica, e coloca resultados em seus respectivos
         * campos, de forma que possa ser feita a edicao*/
        if (loader.getId() == ConstLoader.LOADER_REGISTER_SELL_EDIT && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_VENDA_EDITAR");

            sell.setName(cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_NAME)));
            sell.setTimestamp(cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_TIMESTAMP)));

            sell.setQuantity(cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)));
            sell.setPrice(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)));
            sell.setAddValue(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)));
            sell.setDiscountValue(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)));
            sell.setForwardValue(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE)));
            sell.setReceiveId(cursor.getLong(cursor.getColumnIndex(EntrySeel.COLUMN_RECEIVE_ID)));

            double totalValueDB = Calculus.calcularValorTotalVendaDouble(
                    sell.getQuantity(),
                    sell.getPrice(),
                    sell.getAddValue(),
                    sell.getDiscountValue()
            );

            sell.setPrice(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)));
            sell.setClientId(cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_CLIENT_ID)));

            client.setId(sell.getClientId());
            client.setUri(ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, client.getId()));

            if (client.getId() > 0) {
                sell.setClientName(SearchDB.searchClientName(mContext, client.getId()));
                client.setName(sell.getClientName());
                mTvClientName.setText(client.getName());
            }

            mTvProductName.setText(sell.getName());
            mEtQuantity.setText(String.valueOf(sell.getQuantity()));
            mTvQuantity.setText(String.valueOf(sell.getQuantity()));

            if (sell.getAddValue() != Const.NUMBER_ZERO) { // Tem valor adicional

                mSwitchAdd.setChecked(true);
                mLayoutAdd.setVisibility(View.VISIBLE);
                mEtAdd.setText(mFormatCurrency.format(sell.getAddValue()));

            } else { // Não tem valor adicional

                mSwitchAdd.setChecked(false);
                mLayoutAdd.setVisibility(View.GONE);
            }

            if (sell.getDiscountValue() != Const.NUMBER_ZERO) { // Tem desconto

                mSwitchDiscount.setChecked(true);
                mLayoutDiscount.setVisibility(View.VISIBLE);
                mEtDiscount.setText(mFormatCurrency.format(sell.getDiscountValue()));

            } else { // Não tem desconto

                mSwitchDiscount.setChecked(false);
                mLayoutDiscount.setVisibility(View.GONE);
            }


            if (sell.getForwardValue() != Const.NUMBER_ZERO) { // Tem valor a prazo

                mSwitchForward.setChecked(true);
                mLayoutForward.setVisibility(View.VISIBLE);
                mEtForward.setText(mFormatCurrency.format(sell.getForwardValue()));
                mTvProductName.setText(sell.getClientName());

            } else { // Não tem valor a prazo

                mSwitchForward.setChecked(false);
                mLayoutForward.setVisibility(View.GONE);
            }

            mTvTotalValue.setText(mFormatCurrency.format(totalValueDB));

            mEtQuantity.requestFocus();
        }

        /* Faz pesquisa pelo id de um produto e econtra o seu nome e seu valor e colocar nos
         * respectivos campos*/
        if (loader.getId() == ConstLoader.LOADER_REGISTER_SELL_ADD && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_VENDA_ADICIONAR");

            sell.setName(cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME)));
            sell.setPrice(cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE)));

            mTvProductName.setText(sell.getName());

            if (sell.getPrice() == null) {

                sell.setPrice(0.0);
            }

            mTvTotalValue.setText(
                    calcularValorTotalVendaString(
                            editToString(mEtQuantity),
                            sell.getPrice(),
                            editToString(mEtAdd),
                            editToString(mEtDiscount),
                            editToString(mEtForward)));
        }

        /* Faz pesquisa dos dados do cliente */
        if (loader.getId() == ConstLoader.LOADER_REGISTER_SELL_CLIENT && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_CLIENTE");

            client.setName(cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_NAME)));
            sell.setClientName(client.getName());
            mTvClientName.setText(client.getName());

            mTvTotalValue.setText(mValorTotalBundle);
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

            case R.id.et_sell_quantity:
                mEtQuantity.requestFocus();
                mEtQuantity.setSelection(mEtQuantity.getText().length());
                ControlViews.showKeyboard(mContext, mEtQuantity);
                return true;

            case R.id.et_sell_discount_value:
                mEtDiscount.requestFocus();
                mEtDiscount.setSelection(mEtDiscount.getText().length());
                ControlViews.showKeyboard(mContext, mEtDiscount);
                return true;

            case R.id.et_sell_add_value:
                mEtAdd.requestFocus();
                mEtAdd.setSelection(mEtAdd.getText().length());
                ControlViews.showKeyboard(mContext, mEtAdd);
                return true;

            case R.id.et_sell_forward_value:
                mEtForward.requestFocus();
                mEtForward.setSelection(mEtForward.getText().length());
                ControlViews.showKeyboard(mContext, mEtForward);
                return true;

            case R.id.switch_sell_add:
                idDataChanged = true;
                return false;

            case R.id.switch_sell_discount:
                idDataChanged = true;
                return false;

            case R.id.switch_sell_forward:
                idDataChanged = true;
                return false;

            default:
                return false;
        }
    }

    /* Verifica a entrada de caracteres nos edits*/
    private void watcherControl() {

        Log.v(TAG, "watcherControl");

        /* Edits tem apenas caracteres numericos em seu teclado
         * Apos a entrada de caracteres, e feita a formatação para o estilo moeda para ser
         * apresentado ao usuario
         * Tambem e feito o calculo do valor de venda do produto apos a etrada da quantidade de
         * produto, valor adicional e do desconto caso tenha */
        mEtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (mEtQuantity.length() > Const.NUMBER_ZERO) {

                    int numero = Integer.parseInt(charSequence.toString());
                    charSequence = String.valueOf(numero);
                }

                if (!idDataChanged) {

                    idDataChanged = true;
                }

                if (sell.getPrice() == null) {

                    sell.setPrice(0.0);
                }

                mTvTotalValue.setText(calcularValorTotalVendaString(
                        charSequenceToString(charSequence),
                        sell.getPrice(),
                        editToString(mEtAdd),
                        editToString(mEtDiscount),
                        editToString(mEtForward)));

                if (isFormatIntegerUpdate) {

                    isFormatIntegerUpdate = false;
                    return;
                }

                isFormatIntegerUpdate = true;

                mEtQuantity.setText(charSequence);
                mEtQuantity.setSelection(mEtQuantity.getText().length());
                mTvQuantity.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!idDataChanged) {

                    idDataChanged = true;
                }

                if (sell.getPrice() == null) {

                    sell.setPrice(0.0);
                }

                mTvTotalValue.setText(calcularValorTotalVendaString(
                        editToString(mEtQuantity),
                        sell.getPrice(),
                        charSequenceToString(charSequence),
                        editToString(mEtDiscount),
                        editToString(mEtForward)));

                if (isFormatCurrencyUpdate) {

                    isFormatCurrencyUpdate = false;
                    return;
                }

                isFormatCurrencyUpdate = true;

                mEtAdd.setText(Formatting.currencyToStringToCurrency(charSequenceToString(charSequence)));

                mEtAdd.setSelection(mEtAdd.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mEtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                double sellValueAdd = editToInteger(mEtQuantity) * sell.getPrice() +
                        editToDouble(mEtAdd);

                if (charSequenceToDouble(charSequence) > sellValueAdd) {

                    mEtDiscount.setError(String.format(
                            getString(R.string.error_discount_value_greater_sale),
                            Formatting.doubleToCurrency(sellValueAdd))
                    );
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!idDataChanged) {

                    idDataChanged = true;
                }

                if (sell.getPrice() == null) {

                    sell.setPrice(0.0);
                }

                mTvTotalValue.setText(calcularValorTotalVendaString(
                        editToString(mEtQuantity),
                        sell.getPrice(),
                        editToString(mEtAdd),
                        charSequenceToString(charSequence),
                        editToString(mEtForward)));


                if (isFormatCurrencyUpdate) {

                    isFormatCurrencyUpdate = false;
                    return;
                }

                isFormatCurrencyUpdate = true;

                mEtDiscount.setText(Formatting.currencyToStringToCurrency(charSequenceToString(charSequence)));

                mEtDiscount.setSelection(mEtDiscount.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        mEtForward.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {


                double sellValueDiscount =
                        editToInteger(mEtQuantity) * sell.getPrice() + editToDouble(mEtAdd) - editToDouble(mEtDiscount);

                if (charSequenceToDouble(charSequence) / 100 > sellValueDiscount) {

                    mEtDiscount.setError(String.format(
                            getString(R.string.error_forward_value_greater_sale),
                            Formatting.doubleToCurrency(sellValueDiscount))
                    );
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


                if (!idDataChanged) {

                    idDataChanged = true;
                }

                if (sell.getPrice() == null) {

                    sell.setPrice(0.0);
                }

                mTvTotalValue.setText(calcularValorTotalVendaString(
                        editToString(mEtQuantity),
                        sell.getPrice(),
                        editToString(mEtAdd),
                        editToString(mEtDiscount),
                        charSequenceToString(charSequence)));

                if (isFormatCurrencyUpdate) {
                    isFormatCurrencyUpdate = false;
                    return;
                }

                isFormatCurrencyUpdate = true;

                mEtForward.setText(Formatting.currencyToStringToCurrency(charSequenceToString(charSequence)));

                mEtForward.setSelection(mEtForward.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /* Veifica se houve alteração no estado do Switch */
    private void switchControl() {

        Log.v(TAG, "");

        /* Se Switch estiver Checked fica visivel edit para entrada de valores
         * Se Switch não estiver Checked o edit para entrada de dados fica invisivel */
        mSwitchAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) { // Se tiver checked

                    mLayoutAdd.setVisibility(View.VISIBLE);

                    if (isAddProduct) {

                        mEtAdd.setText("0");
                    }

                    mEtAdd.requestFocus();


                } else { // Se não tiver checked

                    mLayoutAdd.setVisibility(View.GONE);
                    mEtAdd.setText("0");
                }
            }
        });

        mSwitchDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) { // Se tiver checked

                    mLayoutDiscount.setVisibility(View.VISIBLE);

                    if (isAddProduct) {

                        mEtDiscount.setText("0");
                    }

                    mEtDiscount.requestFocus();

                } else { // Se não tiver checked

                    mLayoutDiscount.setVisibility(View.GONE);
                    mEtDiscount.setText("0");
                }
            }
        });

        mSwitchForward.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { // Se tiver checked

                    mLayoutForward.setVisibility(View.VISIBLE);

                    if (isAddProduct) {

                        mEtForward.setText("0");
                    }

                    mEtForward.requestFocus();

                } else { // Se não tiver checked

                    mLayoutForward.setVisibility(View.GONE);
                    mEtForward.setText("0");
                    mTvClientName.setText("");
                    client.setName("");
                }

            }
        });
    }

    private void saveDataDB() {

        Log.v(TAG, "saveDataDB - Inicio");

        // Captura os valores nos edits
        String productName = mTvProductName.getText().toString().trim();
        String quantity = mEtQuantity.getText().toString().trim();
        String addValue = mEtAdd.getText().toString().trim();
        String discountValue = mEtDiscount.getText().toString().trim();
        String forwardValue = mEtForward.getText().toString().trim();
        String clientName = mTvClientName.getText().toString();


        // Verifica se os Switch estão checked
        boolean isCheckAdd = mSwitchAdd.isChecked();
        boolean isCheckDiscount = mSwitchDiscount.isChecked();
        boolean isCheckForward = mSwitchForward.isChecked();

        // Campo não pode ser vazio
        if (quantity.isEmpty()) {

            mEtQuantity.setError(getString(R.string.error_empty_quantity));
            mEtQuantity.requestFocus();
            return;
        }

        // Converte os String do campo valorQuantidade para inteiro
        int quantityInteger = Integer.parseInt(quantity);

        // Se campo tiver valor zero, apresenta mensagem erro
        if (quantityInteger == Const.NUMBER_ZERO) {

            mEtQuantity.setError(getString(R.string.error_valide_value));
            mEtQuantity.requestFocus();
            return;
        }

        // Converte as String dos campos valorAdicional, valorDesconto  e valorPrazo para double
        double addValueDouble = Formatting.currencyToDouble(addValue);
        double discountValueDouble = Formatting.currencyToDouble(discountValue);
        double forwardValueDouble = Formatting.currencyToDouble(forwardValue);

        // Se Switch adicional estiver Checked
        if (isCheckAdd) {

            // O valor desse campo deve ser positivo
            if (addValueDouble == 0) {

                mEtAdd.setError(getString(R.string.error_valide_value_add));
                mEtAdd.requestFocus();
                return;
            }
        }

        // Se Switch desconto estiver Checked
        if (isCheckDiscount) {

            // O valor desse campo deve ser positivo
            if (discountValueDouble == 0) {

                mEtDiscount.setError(getString(R.string.error_valide_value_discount));
                mEtDiscount.requestFocus();
                return;
            }
        }

        if (isCheckForward) {

            // O valor desse campo deve ser positivo
            if (forwardValueDouble == 0) {

                mEtForward.setError(getString(R.string.error_valide_value_forward));
                mEtForward.requestFocus();
                return;
            }


            // Deve se realizar a busca de um cliente para venda a prazo
            if (client.getName().isEmpty()) {

                Snackbar.make(mButClient, getString(R.string.error_client_name_empty), Snackbar.LENGTH_LONG).show();
                return;
            }

            // Deve se realizar a busca de um cliente para venda a prazo
            if (client.getId() == -1) {

                Snackbar.make(mButClient, getString(R.string.error_client_name_empty), Snackbar.LENGTH_LONG).show();
                return;
            }
        }

        //TODO usar receiveID para poder excluir ou eidtar uma venda a prazo


        Receive receive = new Receive();
        receive.setClientId(sell.getClientId());
        receive.setClientName(clientName);
        receive.setType(ConstDB.TYPE_DEBIT);
        receive.setValue(forwardValueDouble);
        receive.setDescription(String.format(
                getString(R.string.text_forward_sell),
                quantity,
                productName
        ));


        sell.setClientName(clientName);
        sell.setQuantity(quantityInteger);
        sell.setName(productName);
        sell.setAddValue(addValueDouble);
        sell.setDiscountValue(discountValueDouble);
        sell.setForwardValue(forwardValueDouble);
        sell.setClientName(clientName);

        // Se tiver venda a prazo Objeto values
        ContentValues valuesForward = new ContentValues();


        // Venda a prazo
        if (isCheckForward) {

            valuesForward.put(EntryReceive.COLUMN_CLIENT_ID, receive.getClientId());
            valuesForward.put(EntryReceive.COLUMN_CLIENT_NAME, receive.getClientId());
            valuesForward.put(EntryReceive.COLUMN_TYPE, receive.getType());
            valuesForward.put(EntryReceive.COLUMN_VALUE, receive.getValue());
            valuesForward.put(EntryReceive.COLUMN_DESCRIPTION, receive.getDescription());
        }


        // Coloca dados em um objeto values para ser salvo no BD
        ContentValues valuesSell = new ContentValues();
        valuesSell.put(EntrySeel.COLUMN_NAME, sell.getName());
        valuesSell.put(EntrySeel.COLUMN_QUANTITY, sell.getQuantity());
        valuesSell.put(EntrySeel.COLUMN_PRICE, sell.getPrice());
        valuesSell.put(EntrySeel.COLUMN_ADD_VALUE, sell.getAddValue());
        valuesSell.put(EntrySeel.COLUMN_DISCOUNT_VALUE, sell.getDiscountValue());
        valuesSell.put(EntrySeel.COLUMN_FORWARD_VALUE, sell.getForwardValue());


        if (isCheckForward) {

            valuesForward.put(EntryReceive.COLUMN_TIMESTAMP, TimeData.getDateTime());

            valuesSell.put(EntrySeel.COLUMN_CLIENT_ID, client.getId());
        }

        // Salva dados no BD
        if (isAddProduct) {

            valuesSell.put(EntrySeel.COLUMN_TIMESTAMP, TimeData.getDateTime());

            Crud.insert(mContext, EntrySeel.CONTENT_URI_SELL, valuesSell);

            if (isCheckForward) {

                Crud.insert(mContext, EntryReceive.CONTENT_URI_RECEIVE, valuesForward);
            }

            Log.v(TAG, "saveDataDB - inserir");

        } else {

            if (isCheckForward) {

                Snackbar.make(mButClient, getString(R.string.error_no_edition),
                        Snackbar.LENGTH_INDEFINITE).show();
                return;

            } else {

                valuesSell.put(EntrySeel.COLUMN_TIMESTAMP, sell.getTimestamp());

                Crud.update(mContext, mUriInitial, valuesSell);

                Log.v(TAG, "saveDataDB - editar");
            }
        }

        Log.v(TAG, "saveDataDB - Fim");

        finish();
    }

}
