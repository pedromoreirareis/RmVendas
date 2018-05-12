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
import android.text.TextUtils;
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
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;
import com.pedromoreirareisgmail.rmvendas.models.Product;
import com.pedromoreirareisgmail.rmvendas.models.Sell;

import java.text.NumberFormat;

import static com.pedromoreirareisgmail.rmvendas.Utils.Calculus.calcularValorTotalVendaString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.editsToDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.formatarCharSequenceDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.formatarCharSequenceString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.formatarEditsInt;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.formatarEditsString;
import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;


public class SellActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        Button.OnClickListener {

    private static final String TAG = SellActivity.class.getSimpleName();
    private static final int LOADER_VENDA_ADICIONAR = 0;
    private static final int LOADER_VENDA_EDITAR = 1;
    private static final int LOADER_CLIENTE = 2;

    private static final String URI_ATUAL = "uri_atual";
    private static final String VALOR_UNIDADE = "valor_unidade";
    private static final String ID_CLIENTE = "id_cliente";

    private final NumberFormat mValorFormatarCurrency = NumberFormat.getCurrencyInstance();

    private TextView mTvQuantidadeProduto;
    private TextView mTvNomeProduto;
    private TextView mTvValorTotalVista;
    private TextView mTvNomeCliente;
    private EditText mEtQuantidade;
    private EditText mEtAdicional;
    private EditText mEtDesconto;
    private EditText mEtPrazo;
    private Button mButCliente;
    private Switch mSwitchAdicional;
    private Switch mSwitchDesconto;
    private Switch mSwitchPrazo;
    private TextInputLayout layoutAdicional;
    private TextInputLayout layoutDesconto;
    private LinearLayout layoutPrazo;

    private Context mContext;
    private Product product;
    private Sell sell;

    private Uri mUriCliente = null;

    private long mIdCliente = Const.MENOS_UM;
    private double mValorUnidadeProduto = 0;
    private String mValorTotalBundle = "";
    private String mDataHoraBD = null;
    private String mNomeCliente = "";

    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;
    private boolean isFormatarIntegerAtualizado = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        Log.v(TAG, "onCreate");

        initViews();
        initListenerAndObject();


        // Se for para adicionar coloca titulo na activity ADICIONAR
        if (product.getAddSell()) {

            Log.v(TAG, "mAdicionarProdutoBD - Adicionar");
            Log.v(TAG, "mAdicionarProdutoBD - Adicionar :" + product.getUri().toString());

            setTitle(R.string.title_venda_add);
            getLoaderManager().initLoader(LOADER_VENDA_ADICIONAR, null, this);

            mTvQuantidadeProduto.setText(Const.UMA_UNIDADE_STRING);
            mEtQuantidade.setText(Const.UMA_UNIDADE_STRING);
            mEtQuantidade.setSelection(mEtQuantidade.getText().length());
        }

        // Se não for para adicionar coloca titulo na activity para EDITAR
        if (!product.getAddSell()) {

            Log.v(TAG, "!mAdicionarProdutoBD - Editar");

            setTitle(R.string.title_venda_edit);
            getLoaderManager().initLoader(LOADER_VENDA_EDITAR, null, this);
        }

        // Se tiver uma instancia salva então ativa
        verificarSavedInstanceState(savedInstanceState);

        // Verifica a entrada de caracteres nos edits
        watcherControl();

        // Verifica a maudança de estado do Switch
        switchControlChange();

        // Coloca foco e seleciona dados do edit quantidade
        mEtQuantidade.setSelectAllOnFocus(true);

        // Tira o foco e coloca valor zero nos edits
        ControlViews.noFocusAndZero(mEtAdicional);
        ControlViews.noFocusAndZero(mEtDesconto);
        ControlViews.noFocusAndZero(mEtPrazo);
    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = SellActivity.this;

        // Instancia o objeto Sell
        sell = new Sell();

        // Instancia o objeto Product
        product = new Product();

        // Recebe dados de ListProductSaleActivity
        Intent intentProduct = getIntent();
        product.setUri(intentProduct.getData());

        if (intentProduct.hasExtra(ConstIntents.INTENT_ADD_SELL)) {

            product = intentProduct.getParcelableExtra(ConstIntents.INTENT_ADD_SELL);

        } else {

            product.setAddSell(false);
        }

        /* Abre activity ListClientSaleActivity para selecionar cliente para venda a prazo */
        mButCliente.setOnClickListener(this);

        // Monitora toques nos edits
        mEtQuantidade.setOnTouchListener(this);
        mEtAdicional.setOnTouchListener(this);
        mEtDesconto.setOnTouchListener(this);
        mEtPrazo.setOnTouchListener(this);

        // Monitora toques nos Switchs
        mSwitchAdicional.setOnTouchListener(this);
        mSwitchDesconto.setOnTouchListener(this);
        mSwitchPrazo.setOnTouchListener(this);
    }

    //TODO: parei aqui

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mTvQuantidadeProduto = findViewById(R.id.tv_vend_quant_quantidade);
        mTvNomeProduto = findViewById(R.id.tv_vend_quant_nome_produto);
        mTvValorTotalVista = findViewById(R.id.tv_vend_quant_valor_total_vista);
        mTvNomeCliente = findViewById(R.id.tv_vend_quant_cliente);
        mButCliente = findViewById(R.id.but_vend_quant_cliente);
        mEtQuantidade = findViewById(R.id.et_vend_quant_quantidade);
        mEtDesconto = findViewById(R.id.et_vend_quant_valor_desconto);
        mEtAdicional = findViewById(R.id.et_vend_quant_valor_adicional);
        mEtPrazo = findViewById(R.id.et_vend_quant_valor_prazo);
        mSwitchAdicional = findViewById(R.id.switch_vend_quant_adicional);
        mSwitchDesconto = findViewById(R.id.switch_vend_quant_desconto);
        mSwitchPrazo = findViewById(R.id.switch_vend_quant_prazo);
        layoutDesconto = findViewById(R.id.til_vend_quant_desconto);
        layoutAdicional = findViewById(R.id.til_vend_quant_adicional);
        layoutPrazo = findViewById(R.id.ll_vend_quant_prazo);
    }

    private void verificarSavedInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {

            Log.v(TAG, "savedInstanceState != null");

            if (savedInstanceState.containsKey("idCliente")) {

                mIdCliente = savedInstanceState.getLong("idCliente");
            }

            if (savedInstanceState.containsKey("nomeCliente")) {

                mNomeCliente = savedInstanceState.getString("nomeCliente");
                mTvNomeCliente.setText(savedInstanceState.getString("nomeCliente"));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "onActivityResult");

        if (requestCode == Const.COD_RESULT_VENDA_CLIENTES && resultCode == RESULT_OK) {

            if (data != null) {

                Log.v(TAG, "onActivityResult -  data != null ");

                if (data.hasExtra(ID_CLIENTE)) {

                    mIdCliente = Long.parseLong(data.getStringExtra(ID_CLIENTE));
                    mUriCliente = ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, mIdCliente);
                    getLoaderManager().initLoader(LOADER_CLIENTE, null, this);
                }

                mValorTotalBundle = calcularValorTotalVendaString(
                        formatarEditsString(mEtQuantidade),
                        Double.parseDouble(data.getStringExtra(VALOR_UNIDADE)),
                        formatarEditsString(mEtAdicional),
                        formatarEditsString(mEtDesconto),
                        formatarEditsString(mEtPrazo));
            }
        }
    }


    @Override
    public void onClick(View view) {

        Log.v(TAG, "onClick - but_vend_quant_cliente");

        if (view.getId() == R.id.but_vend_quant_cliente) {

            mIdCliente = Const.MENOS_UM;
            mNomeCliente = "";

            Intent intentListaCliente =
                    new Intent(SellActivity.this, ListClientSaleActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(URI_ATUAL, product.getUri().toString());
            bundle.putString(VALOR_UNIDADE, String.valueOf(mValorUnidadeProduto));

            intentListaCliente.putExtras(bundle);

            startActivityForResult(intentListaCliente, Const.COD_RESULT_VENDA_CLIENTES);

            ControlViews.hideKeyboard(SellActivity.this, mButCliente);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v(TAG, "onSaveInstanceState");

        if (mIdCliente != Const.MENOS_UM && !mNomeCliente.isEmpty()) {

            outState.putLong("idCliente", mIdCliente);
            outState.putString("nomeCliente", mNomeCliente);

        }
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
             * Verifica se houve alteração, se houve abre um Dialog para verificar se deseja descatar
             * as alterações e sair da activity ou se deseja continuar na activity e continua alterando
             */
            case android.R.id.home:
                if (!isDadosAlterado) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Messages.homePressed(
                        SellActivity.this,
                        SellActivity.this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botão voltar (embaixo)
     * Se dados foram alterados abre Dialog para decidir se ira descatar dados alterados e sair
     * da Activity ou se deseja ficar na activity e continuar as alterações
     */
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        Messages.backPressed(
                SellActivity.this,
                SellActivity.this);
    }

    /* Salva dados no BD
     * Recebe dados dos edits e dos Switchs, faz validações, coloca dados no objeto values e
     * salva no banco de dados
     */
    private void salvarDadosBD() {

        Log.v(TAG, "salvarDadosBD - Inicio");

        // Pega os valores nos edits
        String nomeProdutoTextView = mTvNomeProduto.getText().toString().trim();
        String quatidadeEditText = mEtQuantidade.getText().toString().trim();
        String valorAdicionalEditText = mEtAdicional.getText().toString().trim();
        String valorDescontoEditText = mEtDesconto.getText().toString().trim();
        String valorPrazoEditText = mEtPrazo.getText().toString().trim();
        String nomeClienteTextView = mTvNomeCliente.getText().toString();

        // Verifica se os Switch estão checked
        boolean temAdicionalSwitch = mSwitchAdicional.isChecked();
        boolean temDescontoSwitch = mSwitchDesconto.isChecked();
        boolean temPrazoSwitch = mSwitchPrazo.isChecked();

        // Campo não pode ser vazio
        if (TextUtils.isEmpty(quatidadeEditText)) {

            mEtQuantidade.setError(getString(R.string.error_campo_vazio_quantidade));
            mEtQuantidade.requestFocus();
            return;
        }

        // Converte os String do campo valorQuantidade para inteiro
        int quantidadeInt = Integer.parseInt(quatidadeEditText);

        // Se campo tiver valor zero, apresenta mensagem erro
        if (quantidadeInt == Const.NUMERO_ZERO) {

            mEtQuantidade.setError(getString(R.string.error_valide_value));
            mEtQuantidade.requestFocus();
            return;
        }

        // Converte as String dos campos valorAdicional, valorDesconto  e valorPrazo para double
        double valorAdicionalDouble = Formatting.currencyToDouble(valorAdicionalEditText);
        double valorDescontoDouble = Formatting.currencyToDouble(valorDescontoEditText);
        double valorPrazoDouble = Formatting.currencyToDouble(valorPrazoEditText);


        // Se Switch adicional estiver Checked
        if (temAdicionalSwitch) {

            // O valor desse campo deve ser positivo
            if (valorAdicionalDouble == 0) {

                mEtAdicional.setError(getString(R.string.error_valide_value_adicional));
                mEtAdicional.requestFocus();
                return;
            }
        }

        // Se Switch desconto estiver Checked
        if (temDescontoSwitch) {

            // O valor desse campo deve ser positivo
            if (valorDescontoDouble == 0) {

                mEtDesconto.setError(getString(R.string.error_valide_value_desconto));
                mEtDesconto.requestFocus();
                return;
            }
        }

        if (temPrazoSwitch) {

            // O valor desse campo deve ser positivo
            if (valorPrazoDouble == 0) {

                mEtPrazo.setError(getString(R.string.error_valide_value_prazo));
                mEtPrazo.requestFocus();
                return;
            }


            // Deve se realizar a busca de um cliente para venda a prazo
            if (mNomeCliente.isEmpty()) {

                Snackbar.make(mButCliente, getString(R.string.error_nome_cliente), Snackbar.LENGTH_LONG).show();
                return;
            }

            // Deve se realizar a busca de um cliente para venda a prazo
            if (mIdCliente == -1) {

                Snackbar.make(mButCliente, getString(R.string.error_nome_cliente), Snackbar.LENGTH_LONG).show();
                return;
            }
        }

        // Colocando dados dentro de objeto para salvar venda a prazo
        ContentValues valuesVendaPrazo = new ContentValues();
        if (temPrazoSwitch) {

            valuesVendaPrazo.put(EntryReceive._ID, mIdCliente);
            valuesVendaPrazo.put(EntryReceive.COLUMN_CLIENT_NAME, nomeClienteTextView);
            valuesVendaPrazo.put(EntryReceive.COLUMN_TYPE, ConstDB.TIPO_VENDA);
            valuesVendaPrazo.put(EntryReceive.COLUMN_VALUE, valorPrazoDouble);
            valuesVendaPrazo.put(EntryReceive.COLUMN_DESCRIPTION, String.format(
                    getResources().getString(R.string.text_venda_a_prazo_venda),
                    quatidadeEditText,
                    nomeProdutoTextView));
        }

        // Coloca dados em um objeto values para ser salvo no BD
        ContentValues values = new ContentValues();
        values.put(EntrySeel.COLUMN_NAME, nomeProdutoTextView);
        values.put(EntrySeel.COLUMN_QUANTITY, quantidadeInt);
        values.put(EntrySeel.COLUMN_PRICE, mValorUnidadeProduto);
        values.put(EntrySeel.COLUMN_ADD_VALUE, valorAdicionalDouble);
        values.put(EntrySeel.COLUMN_DISCOUNT_VALUE, valorDescontoDouble);
        values.put(EntrySeel.COLUMN_FORWARD_VALUE, valorPrazoDouble);
        valuesVendaPrazo.put(EntryReceive.COLUMN_TIMESTAMP, TimeData.getDateTime());
        if (temPrazoSwitch) {
            values.put(EntrySeel.COLUMN_CLIENT_ID, mIdCliente);
        }

        // Salva dados no BD
        if (product.getAddSell()) {

            values.put(EntrySeel.COLUMN_TIMESTAMP, TimeData.getDateTime());

            Crud.insert(SellActivity.this, EntrySeel.CONTENT_URI_SELL, values);

            if (temPrazoSwitch) {

                Crud.insert(SellActivity.this, EntryReceive.CONTENT_URI_RECEIVE, valuesVendaPrazo);
            }

            Log.v(TAG, "salvarDadosBD - inserir");

        } else {

            if (temPrazoSwitch) {

                Snackbar.make(mButCliente, getString(R.string.error_nome_edicao_nao_permitida), Snackbar.LENGTH_INDEFINITE).show();
                return;

            } else {

                values.put(EntrySeel.COLUMN_TIMESTAMP, mDataHoraBD);

                Crud.update(SellActivity.this, product.getUri(), values);

                Log.v(TAG, "salvarDadosBD - editar");
            }
        }

        Log.v(TAG, "salvarDadosBD - Fim");

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        /* Se LOADER_VENDA_ADICIONAR
         * com Uri do produto a ser adicionado, faz a pesquisa por esse produto e retorna com o nome
         * do produto, valor e seu id
         */
        if (i == LOADER_VENDA_ADICIONAR) {

            Log.v(TAG, "onCreateLoader - LOADER_VENDA_ADICIONAR");

            String[] projection = {
                    EntryProduct._ID,
                    EntryProduct.COLUMN_PRICE,
                    EntryProduct.COLUMN_NAME
            };

            return new CursorLoader(
                    this,
                    product.getUri(),
                    projection,
                    null,
                    null,
                    null
            );
        }

        /* se LOADER_VENDA_EDITAR
         * com Uri da venda, faz pesquisa e retorna com todos os dados referentes a essa venda
         * especifica
         */
        if (i == LOADER_VENDA_EDITAR) {

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
                    EntrySeel.COLUMN_PRICE
            };

            return new CursorLoader(
                    this,
                    product.getUri(),
                    projection,
                    null,
                    null,
                    null
            );
        }

        if (i == LOADER_CLIENTE) {

            Log.v(TAG, "onCreateLoader - LOADER_CLIENTE");

            // Trazer todos os dados de um clientes especifico indentificado pelo mUriAtual
            String[] projection = {
                    EntryClient._ID,
                    EntryClient.COLUMN_NAME,
                    EntryClient.COLUMN_FONE
            };

            return new CursorLoader(
                    this,
                    mUriCliente,
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

        /* Retorna todos os dados de uma vena especifica, e coloca resultados em seus respectivos
         * campos, de forma que possa ser feita a edicao
         */
        if (loader.getId() == LOADER_VENDA_EDITAR && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_VENDA_EDITAR");

            String nomeProdutoBD = cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_NAME));
            mDataHoraBD = cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_TIMESTAMP));

            int quantidadeBD = cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY));
            double valorUnidadeBD = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE));
            double valorAdicionalBD = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE));
            double valorDescontoBD = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE));
            double valorPrazoBD = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE));
            double valorTotalBD = Calculus.calcularValorTotalVendaDouble(
                    quantidadeBD,
                    valorUnidadeBD,
                    valorAdicionalBD,
                    valorDescontoBD
            );

            mValorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE));

            int idCliente = cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_CLIENT_ID));
            mIdCliente = idCliente;
            mUriCliente = ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, mIdCliente);

            String nomeClienteBD = "";
            if (mIdCliente > 0) {
                nomeClienteBD = SearchDB.searchClientName(SellActivity.this, idCliente);
                mNomeCliente = nomeClienteBD;
                mTvNomeCliente.setText(mNomeCliente);
            }
            mTvNomeProduto.setText(nomeProdutoBD);
            mEtQuantidade.setText(String.valueOf(quantidadeBD));
            mTvQuantidadeProduto.setText(String.valueOf(quantidadeBD));

            if (valorAdicionalBD != Const.NUMERO_ZERO) {

                mSwitchAdicional.setChecked(true);
                layoutAdicional.setVisibility(View.VISIBLE);
                mEtAdicional.setText(mValorFormatarCurrency.format(valorAdicionalBD));

            } else {

                mSwitchAdicional.setChecked(false);
                layoutAdicional.setVisibility(View.GONE);
            }

            if (valorDescontoBD != Const.NUMERO_ZERO) {

                mSwitchDesconto.setChecked(true);
                layoutDesconto.setVisibility(View.VISIBLE);
                mEtDesconto.setText(mValorFormatarCurrency.format(valorDescontoBD));

            } else {
                mSwitchDesconto.setChecked(false);
                layoutDesconto.setVisibility(View.GONE);
            }


            if (valorPrazoBD != Const.NUMERO_ZERO) {

                mSwitchPrazo.setChecked(true);
                layoutPrazo.setVisibility(View.VISIBLE);
                mEtPrazo.setText(mValorFormatarCurrency.format(valorPrazoBD));
                mTvNomeProduto.setText(nomeClienteBD);

            } else {

                mSwitchPrazo.setChecked(false);
                layoutPrazo.setVisibility(View.GONE);
            }

            mTvValorTotalVista.setText(mValorFormatarCurrency.format(valorTotalBD));

            mEtQuantidade.requestFocus();
        }

        /* Faz pesquisa pelo id de um produto e econtro o seu nome e seu valor e colocar nos
         * respectivos campos
         */
        if (loader.getId() == LOADER_VENDA_ADICIONAR && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_VENDA_ADICIONAR");

            String nomeProduto = cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME));
            mValorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE));

            mTvNomeProduto.setText(nomeProduto);

            mTvValorTotalVista.setText(
                    calcularValorTotalVendaString(
                            formatarEditsString(mEtQuantidade),
                            mValorUnidadeProduto,
                            formatarEditsString(mEtAdicional),
                            formatarEditsString(mEtDesconto),
                            formatarEditsString(mEtPrazo)));
        }

        if (loader.getId() == LOADER_CLIENTE && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_CLIENTE");

            mNomeCliente = cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_NAME));
            mTvNomeCliente.setText(mNomeCliente);

            mTvValorTotalVista.setText(mValorTotalBundle);
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

            case R.id.et_vend_quant_quantidade:
                mEtQuantidade.requestFocus();
                mEtQuantidade.setSelection(mEtQuantidade.getText().length());
                ControlViews.showKeyboard(SellActivity.this, mEtQuantidade);
                return true;

            case R.id.et_vend_quant_valor_desconto:
                mEtDesconto.requestFocus();
                mEtDesconto.setSelection(mEtDesconto.getText().length());
                ControlViews.showKeyboard(SellActivity.this, mEtDesconto);
                return true;

            case R.id.et_vend_quant_valor_adicional:
                mEtAdicional.requestFocus();
                mEtAdicional.setSelection(mEtAdicional.getText().length());
                ControlViews.showKeyboard(SellActivity.this, mEtAdicional);
                return true;

            case R.id.et_vend_quant_valor_prazo:
                mEtPrazo.requestFocus();
                mEtPrazo.setSelection(mEtPrazo.getText().length());
                ControlViews.showKeyboard(SellActivity.this, mEtPrazo);
                return true;

            case R.id.switch_vend_quant_adicional:
                isDadosAlterado = true;
                return false;

            case R.id.switch_vend_quant_desconto:
                isDadosAlterado = true;
                return false;

            case R.id.switch_vend_quant_prazo:
                isDadosAlterado = true;
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
         * produto, valor adicional e do desconto caso tenha
         */
        mEtQuantidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (mEtQuantidade.length() > Const.NUMERO_ZERO) {

                    int numero = Integer.parseInt(charSequence.toString());
                    charSequence = String.valueOf(numero);
                }

                if (!isDadosAlterado) {

                    isDadosAlterado = true;
                }

                mTvValorTotalVista.setText(calcularValorTotalVendaString(
                        formatarCharSequenceString(charSequence),
                        mValorUnidadeProduto,
                        formatarEditsString(mEtAdicional),
                        formatarEditsString(mEtDesconto),
                        formatarEditsString(mEtPrazo)));


                if (isFormatarIntegerAtualizado) {

                    isFormatarIntegerAtualizado = false;
                    return;
                }

                isFormatarIntegerAtualizado = true;

                mEtQuantidade.setText(charSequence);
                mEtQuantidade.setSelection(mEtQuantidade.getText().length());
                mTvQuantidadeProduto.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtAdicional.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!isDadosAlterado) {

                    isDadosAlterado = true;
                }


                mTvValorTotalVista.setText(calcularValorTotalVendaString(
                        formatarEditsString(mEtQuantidade),
                        mValorUnidadeProduto,
                        formatarCharSequenceString(charSequence),
                        formatarEditsString(mEtDesconto),
                        formatarEditsString(mEtPrazo)));

                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtAdicional.setText(Formatting.currencyToStringToCurrency(formatarCharSequenceString(charSequence)));

                mEtAdicional.setSelection(mEtAdicional.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mEtDesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                double valorVendaAdicional = formatarEditsInt(mEtQuantidade) * mValorUnidadeProduto +
                        editsToDouble(mEtAdicional);

                if (formatarCharSequenceDouble(charSequence) > valorVendaAdicional) {

                    mEtDesconto.setError(String.format(getResources().getString(R.string.error_campo_desconto_maior_venda), Formatting.doubleToCurrency(valorVendaAdicional)));
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!isDadosAlterado) {

                    isDadosAlterado = true;
                }

                mTvValorTotalVista.setText(calcularValorTotalVendaString(
                        formatarEditsString(mEtQuantidade),
                        mValorUnidadeProduto,
                        formatarEditsString(mEtAdicional),
                        formatarCharSequenceString(charSequence),
                        formatarEditsString(mEtPrazo)));


                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtDesconto.setText(Formatting.currencyToStringToCurrency(formatarCharSequenceString(charSequence)));

                mEtDesconto.setSelection(mEtDesconto.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        mEtPrazo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {


                double valorVendaAdicionalDesconto = formatarEditsInt(mEtQuantidade) * mValorUnidadeProduto +
                        editsToDouble(mEtAdicional) - editsToDouble(mEtDesconto);

                if (formatarCharSequenceDouble(charSequence) / 100 > valorVendaAdicionalDesconto) {

                    mEtDesconto.setError(String.format(getResources().getString(R.string.error_campo_prazo_maior_venda), Formatting.doubleToCurrency(valorVendaAdicionalDesconto)));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


                if (!isDadosAlterado) {

                    isDadosAlterado = true;
                }

                mTvValorTotalVista.setText(calcularValorTotalVendaString(
                        formatarEditsString(mEtQuantidade),
                        mValorUnidadeProduto,
                        formatarEditsString(mEtAdicional),
                        formatarEditsString(mEtDesconto),
                        formatarCharSequenceString(charSequence)));

                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtPrazo.setText(Formatting.currencyToStringToCurrency(formatarCharSequenceString(charSequence)));

                mEtPrazo.setSelection(mEtPrazo.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /* Veifica se houve alteração no estado do Switch*/
    private void switchControlChange() {

        Log.v(TAG, "");

        /* Se Switch estiver Checked fica visivel edit para entrada de valores
         * Se Switch não estiver Checked o edit para entrada de dados fica invisivel
         */
        mSwitchAdicional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {

                    layoutAdicional.setVisibility(View.VISIBLE);

                    if (product.getAddSell()) {
                        mEtAdicional.setText("0");
                    }

                    mEtAdicional.requestFocus();


                } else {

                    layoutAdicional.setVisibility(View.GONE);
                    mEtAdicional.setText("0");
                }
            }
        });

        mSwitchDesconto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {

                    layoutDesconto.setVisibility(View.VISIBLE);

                    if (product.getAddSell()) {
                        mEtDesconto.setText("0");
                    }

                    mEtDesconto.requestFocus();

                } else {

                    layoutDesconto.setVisibility(View.GONE);
                    mEtDesconto.setText("0");
                }
            }
        });

        mSwitchPrazo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    layoutPrazo.setVisibility(View.VISIBLE);

                    if (product.getAddSell()) {
                        mEtPrazo.setText("0");
                    }

                    mEtPrazo.requestFocus();

                } else {

                    layoutPrazo.setVisibility(View.GONE);
                    mEtPrazo.setText("0");
                    mTvNomeCliente.setText("");
                    mNomeCliente = "";
                }

            }
        });
    }

}
