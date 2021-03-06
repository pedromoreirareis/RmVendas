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
import com.pedromoreirareisgmail.rmvendas.Utils.ControlViews;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.TimeDate;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;
import com.pedromoreirareisgmail.rmvendas.models.Receive;
import com.pedromoreirareisgmail.rmvendas.models.Sell;
import com.pedromoreirareisgmail.rmvendas.models.SellToClient;

import java.text.NumberFormat;

import static com.pedromoreirareisgmail.rmvendas.Utils.Calculus.calculateInCashValueDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Calculus.calculateInCashValueString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Calculus.calculateSaleValueDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Calculus.calculateSaleValueString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.charSequenceToDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.charSequenceToString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.editToDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.editToInteger;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.editToString;
import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;


public class SellActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        View.OnClickListener {

    private final NumberFormat mFormatCurrency = NumberFormat.getCurrencyInstance();

    private TextView mTvQuantity;
    private TextView mTvProductName;
    private TextView mTvInCashValue;
    private TextView mTvSaleValue;
    private TextView mTvClientName;
    private EditText mEtQuantity;
    private EditText mEtAdd;
    private EditText mEtDiscount;
    private EditText mEtForward;
    private EditText mEtCard;
    private Button mButClient;
    private Button mButClearQuantity;
    private Button mButClearAdd;
    private Button mButClearDiscount;
    private Button mButClearForward;
    private Button mButClearCard;
    private Switch mSwitchAdd;
    private Switch mSwitchDiscount;
    private Switch mSwitchForward;
    private Switch mSwitchCard;
    private TextInputLayout mLayoutAdd;
    private TextInputLayout mLayoutDiscount;
    private TextInputLayout mLayoutCard;
    private LinearLayout mLayoutForward;

    private Uri mUriInitial = null;
    private Uri mUriClient = null;
    private double mProductValue;

    private Context mContext;
    private Sell sell;

    private String mInCashValue = "";
    private String mSaleValue = "";

    private boolean isAddProduct = false;
    private boolean isDataChanged = false;
    private boolean isFormatCurrencyUpdate = false;
    private boolean isFormatIntegerUpdate = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        initViews();
        initObject();

        if (isAddProduct) { // Adicionar

            setTitle(R.string.title_sell_add);
            getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_SELL_ADD, null, this);

            mTvQuantity.setText(Const.A_UNIT_STRING);
            mEtQuantity.setText(Const.A_UNIT_STRING);
            mEtQuantity.setSelection(mEtQuantity.getText().length());
        }

        if (!isAddProduct) { // Editar

            setTitle(R.string.title_sell_edit);
            getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_SELL_EDIT, null, this);
        }

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
        ControlViews.noFocusAndZero(mEtCard);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Controle da entrada dos edits
        watcherControl();
    }

    private void initViews() {

        // Referencia itens do layout
        mTvQuantity = findViewById(R.id.tv_sell_quantity);
        mTvProductName = findViewById(R.id.tv_sell_product_name);
        mTvInCashValue = findViewById(R.id.tv_sell_in_cash_value);
        mTvSaleValue = findViewById(R.id.tv_sell_sale_value);
        mTvClientName = findViewById(R.id.tv_sell_client_name);

        mEtQuantity = findViewById(R.id.et_sell_quantity);
        mEtDiscount = findViewById(R.id.et_sell_discount_value);
        mEtAdd = findViewById(R.id.et_sell_add_value);
        mEtForward = findViewById(R.id.et_sell_forward_value);
        mEtCard = findViewById(R.id.et_sell_card_value);

        mSwitchAdd = findViewById(R.id.switch_sell_add);
        mSwitchDiscount = findViewById(R.id.switch_sell_discount);
        mSwitchForward = findViewById(R.id.switch_sell_forward);
        mSwitchCard = findViewById(R.id.switch_sell_card);

        mLayoutDiscount = findViewById(R.id.til_sell_discount);
        mLayoutAdd = findViewById(R.id.til_sell_add);
        mLayoutForward = findViewById(R.id.til_sell_forward);
        mLayoutCard = findViewById(R.id.til_sell_card);

        mButClient = findViewById(R.id.but_sell_client);
        mButClearQuantity = findViewById(R.id.but_clear_sell_quantity);
        mButClearAdd = findViewById(R.id.but_clear_sell_add);
        mButClearDiscount = findViewById(R.id.but_clear_sell_discount);
        mButClearForward = findViewById(R.id.but_clear_sell_forward);
        mButClearCard = findViewById(R.id.but_clear_sell_card);
    }

    private void initObject() {

        // Contexto da Activity
        mContext = SellActivity.this;

        sell = new Sell();

        sell.setClientId(Const.ONE_LESS);

        // Recebe dados de ListProductSaleActivity
        Intent intentInitial = getIntent();
        mUriInitial = intentInitial.getData();

        if (intentInitial.hasExtra(ConstIntents.INTENT_ADD_SELL)) {

            // Verificar se vai adicionar ou editar venda
            isAddProduct = intentInitial.getBooleanExtra(ConstIntents.INTENT_ADD_SELL, true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (sell.getClientName() != null) {

            // Salva dados no estado da aplicação para ser usados no retorno da Acitivyt
            if (sell.getClientId() != Const.ONE_LESS && !sell.getClientName().isEmpty()) {

                Sell sellOut = new Sell();

                sellOut.setClientId(sell.getClientId());
                sellOut.setClientName(sell.getClientName());

                outState.putParcelable(Const.SELL_SAVED_INSTANCE_STATE, sellOut);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Tiver dados salvos no Objeto savedInstanceState captura os dados e repassa a Activity
        sell = savedInstanceState.getParcelable(Const.SELL_SAVED_INSTANCE_STATE);
        if (sell != null) {

            mTvClientName.setText(sell.getClientName());
        }
    }


    private void initListener() {

        // Abre activity ListClientSaleActivity, para selecionar cliente para venda a prazo
        mButClient.setOnClickListener(this);

        // Monitora toques nos edits
        mEtQuantity.setOnTouchListener(this);
        mEtAdd.setOnTouchListener(this);
        mEtDiscount.setOnTouchListener(this);
        mEtForward.setOnTouchListener(this);
        mEtCard.setOnTouchListener(this);

        // Monitora toques nos Switchs
        mSwitchAdd.setOnTouchListener(this);
        mSwitchDiscount.setOnTouchListener(this);
        mSwitchForward.setOnTouchListener(this);
        mSwitchCard.setOnTouchListener(this);

        // Listener para clear edits
        mButClearQuantity.setOnClickListener(this);
        mButClearAdd.setOnClickListener(this);
        mButClearDiscount.setOnClickListener(this);
        mButClearForward.setOnClickListener(this);
        mButClearCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            /* Abre activity para buscar cliente para uma venda a prazo */
            case R.id.but_sell_client:

                Intent intentClientList = new Intent(
                        mContext,
                        ListClientSaleActivity.class
                );

                sell.setClientId(Const.ONE_LESS);
                sell.setClientName("");

                SellToClient sellToClient = new SellToClient();
                sellToClient.setUriInitial(mUriInitial);
                sellToClient.setUnitValue(mProductValue);

                intentClientList.putExtra(ConstIntents.INTENT_SELL_TO_CLIENT, sellToClient);

                startActivityForResult(intentClientList, Const.PICK_COD_RESULT_CLIENT_SELL);

                ControlViews.hideKeyboard(mContext, mButClient);
                break;


            case R.id.but_clear_sell_quantity:
                mEtQuantity.setText(Const.EMPTY_STRING);
                break;

            case R.id.but_clear_sell_add:
                mEtAdd.setText(Const.NUMBER_ZERO_STRING);
                break;

            case R.id.but_clear_sell_discount:
                mEtDiscount.setText(Const.NUMBER_ZERO_STRING);
                break;

            case R.id.but_clear_sell_forward:
                mEtForward.setText(Const.NUMBER_ZERO_STRING);
                break;

            case R.id.but_clear_sell_card:
                mEtCard.setText(Const.NUMBER_ZERO_STRING);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Recebe os dados do cliente para venda a prazo
        if (requestCode == Const.PICK_COD_RESULT_CLIENT_SELL && resultCode == RESULT_OK) {

            if (data != null) {

                sell = new Sell();
                SellToClient sellToClient = new SellToClient();

                if (data.hasExtra(ConstIntents.INTENT_CLIENT_TO_SELL)) {

                    sellToClient = data.getParcelableExtra(ConstIntents.INTENT_CLIENT_TO_SELL);

                    sell.setPrice(sellToClient.getUnitValue());
                    sell.setClientId(sellToClient.getClientId());


                    if (sell.getClientId() == Const.NO_ID_CLIENT_TO_SELL) { // Não realiza pesquisa

                        Snackbar.make(mButClient, getString(R.string.error_no_id_client), Snackbar.LENGTH_LONG).show();

                    } else { // Faz pesquisa

                        mUriClient = ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, sell.getClientId());
                        getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_SELL_CLIENT, null, this);
                    }
                }

                // Valor a vista
                mInCashValue = calculateInCashValueString(
                        editToString(mEtQuantity),
                        sellToClient.getUnitValue(),
                        editToString(mEtAdd),
                        editToString(mEtDiscount),
                        editToString(mEtForward),
                        editToString(mEtCard));

                // Valor de venda
                mSaleValue = calculateSaleValueString(
                        editToString(mEtQuantity),
                        sellToClient.getUnitValue(),
                        editToString(mEtAdd),
                        editToString(mEtDiscount)
                );
            }
        }
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
            case R.id.action_save_menu_save:
                saveDataDB();
                return true;

            /* Menu Up
             * Verifica se houve alteração, se houve abre um Dialog para verificar se deseja descatar
             * as alterações e sair da activity ou se deseja continuar na activity e continua alterando*/
            case android.R.id.home:
                if (!isDataChanged) {

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

        if (!isDataChanged) {

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

            String[] projection = {
                    EntrySeel._ID,
                    EntrySeel.COLUMN_NAME,
                    EntrySeel.COLUMN_QUANTITY,
                    EntrySeel.COLUMN_TIMESTAMP,
                    EntrySeel.COLUMN_ADD_VALUE,
                    EntrySeel.COLUMN_DISCOUNT_VALUE,
                    EntrySeel.COLUMN_FORWARD_VALUE,
                    EntrySeel.COLUMN_CARD_VALUE,
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

            // Trazer todos os dados de um clientes especifico indentificado pelo mUriInitial
            String[] projection = {
                    EntryClient._ID,
                    EntryClient.COLUMN_NAME,
                    EntryClient.COLUMN_FONE
            };

            return new CursorLoader(
                    mContext,
                    mUriClient,
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

        /* Faz pesquisa pelo id de um produto e econtra o seu nome e seu valor e colocar nos
         * respectivos campos*/
        if (loader.getId() == ConstLoader.LOADER_REGISTER_SELL_ADD && cursor.moveToFirst()) {

            sell.setName(cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME)));
            sell.setPrice(cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE)));

            mProductValue = sell.getPrice();

            mTvProductName.setText(sell.getName());

            displayCalculateSaleValueAndInCash(
                    editToString(mEtQuantity),
                    mProductValue,
                    editToString(mEtAdd),
                    editToString(mEtDiscount),
                    editToString(mEtForward),
                    editToString(mEtCard));

        }

        /* Retorna dados de uma venda especifica, e coloca resultados em seus respectivos
         * campos, de forma que possa ser feita a edicao*/
        if (loader.getId() == ConstLoader.LOADER_REGISTER_SELL_EDIT && cursor.moveToFirst()) {

            sell.setName(cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_NAME)));
            sell.setTimestamp(cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_TIMESTAMP)));

            sell.setQuantity(cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)));
            sell.setPrice(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)));
            sell.setAddValue(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)));
            sell.setDiscountValue(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)));
            sell.setForwardValue(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE)));

            if (cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_CARD_VALUE)).isEmpty()) {

                sell.setCardValue(Const.NUMBER_ZERO);

            } else {

                sell.setCardValue(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_CARD_VALUE)));
            }

            sell.setReceiveId(cursor.getLong(cursor.getColumnIndex(EntrySeel.COLUMN_RECEIVE_ID)));
            mProductValue = sell.getPrice();

            double saleValueDB = calculateSaleValueDouble(
                    sell.getQuantity(),
                    mProductValue,
                    sell.getAddValue(),
                    sell.getDiscountValue()
            );

            double inCashValueDB = calculateInCashValueDouble(
                    sell.getQuantity(),
                    mProductValue,
                    sell.getAddValue(),
                    sell.getDiscountValue(),
                    sell.getForwardValue(),
                    sell.getCardValue()
            );

            sell.setPrice(cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)));
            sell.setClientId(cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_CLIENT_ID)));

            mUriClient = ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, sell.getClientId());


            if (sell.getClientId() > 0) {

                sell.setClientName(SearchDB.searchClientName(mContext, sell.getClientId()));
                mTvClientName.setText(sell.getClientName());
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


            if (sell.getCardValue() != Const.NUMBER_ZERO) { // Tem valor no cartão

                mSwitchCard.setChecked(true);
                mLayoutCard.setVisibility(View.VISIBLE);
                mEtCard.setText(mFormatCurrency.format(sell.getCardValue()));

            } else { // Não tem valor no cartão

                mSwitchCard.setChecked(false);
                mLayoutCard.setVisibility(View.GONE);
            }

            mTvInCashValue.setText(mFormatCurrency.format(inCashValueDB));
            mTvSaleValue.setText(mFormatCurrency.format(saleValueDB));

            mEtQuantity.requestFocus();
        }

        /* Faz pesquisa dos dados do cliente */
        if (loader.getId() == ConstLoader.LOADER_REGISTER_SELL_CLIENT && cursor.moveToFirst()) {

            sell.setClientName(cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_NAME)));
            mTvClientName.setText(sell.getClientName());

            mTvInCashValue.setText(mInCashValue);
            mTvSaleValue.setText(mSaleValue);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {


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

            case R.id.et_sell_card_value:
                mEtCard.requestFocus();
                mEtCard.setSelection(mEtCard.getText().length());
                ControlViews.showKeyboard(mContext, mEtCard);
                return true;

            case R.id.switch_sell_add:
                isDataChanged = true;
                return false;

            case R.id.switch_sell_discount:
                isDataChanged = true;
                return false;

            case R.id.switch_sell_forward:
                isDataChanged = true;
                return false;

            case R.id.switch_sell_card:
                isDataChanged = true;
                return false;

            default:
                return false;
        }

    }


    /**
     * Apresenta os valores da venda
     *
     * @param quantity     quantidade do produto
     * @param productValue valor de cada unidade
     * @param add          valor adicional, se existir
     * @param discount     valor do desconto, se existir
     * @param forward      valor a prazo, se existir
     * @param card         valor no cartão, se existir
     */
    private void displayCalculateSaleValueAndInCash(String quantity, double productValue,
                                                    String add, String discount,
                                                    String forward, String card) {
        // Apresenta valor a vista
        mTvInCashValue.setText(
                calculateInCashValueString(
                        quantity,
                        productValue,
                        add,
                        discount,
                        forward,
                        card));

        // Apresenta valor de venda
        mTvSaleValue.setText(
                calculateSaleValueString(
                        quantity,
                        productValue,
                        add,
                        discount));

    }


    /* Verifica a entrada de caracteres nos edits*/
    private void watcherControl() {

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

                // Se tiver algum caracter no edit, but clear fica visisvel
                if (charSequence.length() > 0) {

                    mButClearQuantity.setVisibility(View.VISIBLE);

                } else {

                    mButClearQuantity.setVisibility(View.GONE);
                }

                if (mEtQuantity.length() > Const.NUMBER_ZERO) {

                    int numero = Integer.parseInt(charSequence.toString());
                    charSequence = String.valueOf(numero);
                }

                if (!isDataChanged) {

                    isDataChanged = true;
                }

                displayCalculateSaleValueAndInCash(
                        charSequenceToString(charSequence),
                        mProductValue,
                        editToString(mEtAdd),
                        editToString(mEtDiscount),
                        editToString(mEtForward),
                        editToString(mEtCard));

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

                // Se o valor for acima de zero,fica visivel
                if (charSequenceToDouble(charSequence) > 0) {

                    mButClearAdd.setVisibility(View.VISIBLE);

                } else {

                    mButClearAdd.setVisibility(View.GONE);
                }


                if (!isDataChanged) {

                    isDataChanged = true;
                }

                displayCalculateSaleValueAndInCash(
                        editToString(mEtQuantity),
                        mProductValue,
                        charSequenceToString(charSequence),
                        editToString(mEtDiscount),
                        editToString(mEtForward),
                        editToString(mEtCard)
                );

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

                // maxDiscountValue = quantity*productValue + addValue
                double maxDiscountValue = editToInteger(mEtQuantity) * mProductValue +
                        editToDouble(mEtAdd);

                if (charSequenceToDouble(charSequence) > maxDiscountValue) {

                    mEtDiscount.setError(String.format(
                            getString(R.string.error_discount_value_greater_sale),
                            Formatting.doubleToCurrency(maxDiscountValue))
                    );
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // Se o valor for acima de zero,fica visivel
                if (charSequenceToDouble(charSequence) > 0) {

                    mButClearDiscount.setVisibility(View.VISIBLE);

                } else {

                    mButClearDiscount.setVisibility(View.GONE);
                }

                if (!isDataChanged) {

                    isDataChanged = true;
                }

                displayCalculateSaleValueAndInCash(
                        editToString(mEtQuantity),
                        mProductValue,
                        editToString(mEtAdd),
                        charSequenceToString(charSequence),
                        editToString(mEtForward),
                        editToString(mEtCard)
                );

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


                // maxForwardValue = quantity*productValue + addValue - discountValue - cardValue
                double maxForwardValue = editToInteger(mEtQuantity) * mProductValue +
                        editToDouble(mEtAdd) - editToDouble(mEtDiscount) - editToDouble(mEtCard);

                if (charSequenceToDouble(charSequence) / 100 > maxForwardValue) {

                    mEtDiscount.setError(String.format(
                            getString(R.string.error_forward_value_greater_sale),
                            Formatting.doubleToCurrency(maxForwardValue))
                    );
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                // Se o valor for acima de zero,fica visivel
                if (charSequenceToDouble(charSequence) > 0) {

                    mButClearForward.setVisibility(View.VISIBLE);

                } else {

                    mButClearForward.setVisibility(View.GONE);
                }

                if (!isDataChanged) {

                    isDataChanged = true;
                }

                displayCalculateSaleValueAndInCash(
                        editToString(mEtQuantity),
                        mProductValue,
                        editToString(mEtAdd),
                        editToString(mEtDiscount),
                        charSequenceToString(charSequence),
                        editToString(mEtCard)
                );

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

        mEtCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                // maxCardValue = quantity*productValue + addValue - discountValue - forwardValue
                double maxCardValue = editToInteger(mEtQuantity) * mProductValue +
                        editToDouble(mEtAdd) - editToDouble(mEtDiscount) - editToDouble(mEtForward);

                if (charSequenceToDouble(charSequence) > maxCardValue) {

                    mEtCard.setError(String.format(
                            getString(R.string.error_card_value_greater_sale),
                            Formatting.doubleToCurrency(maxCardValue))
                    );
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                // Se o valor for acima de zero,fica visivel
                if (Formatting.charSequenceToDouble(charSequence) > 0) {

                    mButClearCard.setVisibility(View.VISIBLE);

                } else {

                    mButClearCard.setVisibility(View.GONE);
                }

                if (!isDataChanged) {

                    isDataChanged = true;
                }


                displayCalculateSaleValueAndInCash(
                        editToString(mEtQuantity),
                        mProductValue,
                        editToString(mEtAdd),
                        editToString(mEtDiscount),
                        editToString(mEtForward),
                        charSequenceToString(charSequence)
                );


                if (isFormatCurrencyUpdate) {

                    isFormatCurrencyUpdate = false;
                    return;
                }

                isFormatCurrencyUpdate = true;

                mEtCard.setText(Formatting.currencyToStringToCurrency(charSequenceToString(charSequence)));
                mEtCard.setSelection(mEtCard.getText().length());
            }

            @Override
            public void afterTextChanged(Editable charSequence) {

            }
        });

    }

    /* Veifica se houve alteração no estado do Switch */
    private void switchControl() {

        /* Se Switch estiver Checked fica visivel edit para entrada de valores
         * Se Switch não estiver Checked o edit para entrada de dados fica invisivel */
        mSwitchAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) { // Se der checked

                    mLayoutAdd.setVisibility(View.VISIBLE);

                    if (isAddProduct) {

                        mEtAdd.setText("0");
                    }

                    mEtAdd.requestFocus();


                } else { // Se retirar checked

                    mLayoutAdd.setVisibility(View.GONE);
                    mEtAdd.setText("0");
                }
            }
        });

        mSwitchDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) { // Se der checked

                    mLayoutDiscount.setVisibility(View.VISIBLE);

                    if (isAddProduct) {

                        mEtDiscount.setText("0");
                    }

                    mEtDiscount.requestFocus();

                } else { // Se retirar checked

                    mLayoutDiscount.setVisibility(View.GONE);
                    mEtDiscount.setText("0");
                }
            }
        });

        mSwitchForward.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { // Se der checked

                    mLayoutForward.setVisibility(View.VISIBLE);

                    if (isAddProduct) {

                        mEtForward.setText("0");
                    }

                    mEtForward.requestFocus();

                } else { // Se retirar checked

                    mLayoutForward.setVisibility(View.GONE);
                    mEtForward.setText("0");
                    mTvClientName.setText("");
                    sell.setClientName("");
                }
            }
        });

        mSwitchCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { // Se der checked

                    mLayoutCard.setVisibility(View.VISIBLE);

                    if (isAddProduct) {

                        mEtCard.setText("0");

                    }

                    mEtCard.requestFocus();

                } else { // Se retirar checked

                    mLayoutCard.setVisibility(View.GONE);
                    mEtCard.setText("0");
                }
            }
        });


    }


    private void saveDataDB() {

        // Captura os valores nos edits
        String productName = mTvProductName.getText().toString().trim();
        String quantity = mEtQuantity.getText().toString().trim();
        String addValue = mEtAdd.getText().toString().trim();
        String discountValue = mEtDiscount.getText().toString().trim();
        String forwardValue = mEtForward.getText().toString().trim();
        String cardValue = mEtCard.getText().toString().trim();
        String clientName = mTvClientName.getText().toString();

        // Verifica se os Switch estão checked
        boolean isCheckAdd = mSwitchAdd.isChecked();
        boolean isCheckDiscount = mSwitchDiscount.isChecked();
        boolean isCheckForward = mSwitchForward.isChecked();
        boolean isCheckedCard = mSwitchCard.isChecked();

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
        double cardValueDouble = Formatting.currencyToDouble(cardValue);

        // Se Switch add estiver Checked
        if (isCheckAdd) {

            // O valor desse campo deve ser positivo
            if (addValueDouble == 0) {

                mEtAdd.setError(getString(R.string.error_valide_value_add));
                mEtAdd.requestFocus();
                return;
            }
        }

        // Se Switch discount estiver Checked
        if (isCheckDiscount) {

            // O valor desse campo deve ser positivo
            if (discountValueDouble == 0) {

                mEtDiscount.setError(getString(R.string.error_valide_value_discount));
                mEtDiscount.requestFocus();
                return;
            }
        }


        // Se Switch forward estiver Checked
        if (isCheckForward) {

            // O valor desse campo deve ser positivo
            if (forwardValueDouble == 0) {

                mEtForward.setError(getString(R.string.error_valide_value_forward));
                mEtForward.requestFocus();
                return;
            }

            // Deve se realizar a busca de um cliente para venda a prazo
            if (clientName.isEmpty()) {

                Snackbar.make(mButClient, getString(R.string.error_client_name_empty), Snackbar.LENGTH_LONG).show();
                return;
            }

            // Deve se realizar a busca de um cliente para venda a prazo
            if (sell.getClientId() == Const.ONE_LESS) {

                Snackbar.make(mButClient, getString(R.string.error_client_name_empty), Snackbar.LENGTH_LONG).show();
                return;
            }
        }

        // Se Switch card estiver Checked
        if (isCheckedCard) {

            // O valor desse campo deve ser positivo
            if (cardValueDouble == 0) {

                mEtCard.setError(getString(R.string.error_valide_value_card));
                mEtCard.requestFocus();
                return;
            }
        }


        sell.setClientName(clientName);
        sell.setQuantity(quantityInteger);
        sell.setName(productName);
        sell.setAddValue(addValueDouble);
        sell.setDiscountValue(discountValueDouble);
        sell.setForwardValue(forwardValueDouble);
        sell.setCardValue(cardValueDouble);
        sell.setClientName(clientName);
        sell.setPrice(mProductValue);

        // Coloca dados em um objeto values para ser salvo no BD
        ContentValues valuesSell = new ContentValues();
        valuesSell.put(EntrySeel.COLUMN_NAME, sell.getName());
        valuesSell.put(EntrySeel.COLUMN_QUANTITY, sell.getQuantity());
        valuesSell.put(EntrySeel.COLUMN_PRICE, sell.getPrice());
        valuesSell.put(EntrySeel.COLUMN_ADD_VALUE, sell.getAddValue());
        valuesSell.put(EntrySeel.COLUMN_DISCOUNT_VALUE, sell.getDiscountValue());
        valuesSell.put(EntrySeel.COLUMN_FORWARD_VALUE, sell.getForwardValue());
        valuesSell.put(EntrySeel.COLUMN_CARD_VALUE, sell.getCardValue());

        if (isAddProduct) {

            if (isCheckForward) {

                Receive receive = new Receive();

                receive.setClientId(sell.getClientId());
                receive.setClientName(clientName);
                receive.setType(ConstDB.TYPE_DEBIT);
                receive.setValue(forwardValueDouble);
                receive.setDescription(String.format(getString(R.string.text_forward_sell), quantity, productName));

                ContentValues valuesForward = new ContentValues();
                valuesForward.put(EntryReceive.COLUMN_CLIENT_ID, receive.getClientId());
                valuesForward.put(EntryReceive.COLUMN_CLIENT_NAME, receive.getClientName());
                valuesForward.put(EntryReceive.COLUMN_TYPE, receive.getType());
                valuesForward.put(EntryReceive.COLUMN_VALUE, receive.getValue());
                valuesForward.put(EntryReceive.COLUMN_DESCRIPTION, receive.getDescription());

                valuesForward.put(EntryReceive.COLUMN_TIMESTAMP, TimeDate.getDateTime());

                long receiveId = Crud.insertReceiveSell(mContext, EntryReceive.CONTENT_URI_RECEIVE, valuesForward);

                valuesSell.put(EntrySeel.COLUMN_CLIENT_NAME, sell.getClientName());
                valuesSell.put(EntrySeel.COLUMN_CLIENT_ID, sell.getClientId());
                valuesSell.put(EntrySeel.COLUMN_RECEIVE_ID, receiveId);

            }

            valuesSell.put(EntrySeel.COLUMN_TIMESTAMP, TimeDate.getDateTime());

            Crud.insert(mContext, EntrySeel.CONTENT_URI_SELL, valuesSell);

        } else {

            if (isCheckForward) {

                Snackbar.make(mButClient, getString(R.string.error_no_edition),
                        Snackbar.LENGTH_INDEFINITE).show();
                return;

            } else {

                valuesSell.put(EntrySeel.COLUMN_TIMESTAMP, sell.getTimestamp());

                Crud.update(mContext, mUriInitial, valuesSell);
            }
        }

        finish();
    }


}
