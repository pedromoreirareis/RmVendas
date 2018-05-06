package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
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
import com.pedromoreirareisgmail.rmvendas.Utils.Calculos;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.constantes.Const;
import com.pedromoreirareisgmail.rmvendas.constantes.ConstDB;
import com.pedromoreirareisgmail.rmvendas.db.Contract;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoClientes;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoVenda;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;

import java.text.NumberFormat;

import static com.pedromoreirareisgmail.rmvendas.Utils.Calculos.calcularValorTotalVendaString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatar.formatarCharSequenceDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatar.formatarCharSequenceString;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatar.formatarEditsDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatar.formatarEditsInt;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatar.formatarEditsString;
import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.*;


public class VendQuantActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        Button.OnClickListener {

    private static final String TAG = VendQuantActivity.class.getSimpleName();
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

    private Uri mUriAtual = null;
    private Uri mUriCliente = null;

    private long mIdCliente = Const.MENOS_UM;
    private double mValorUnidadeProduto = 0;
    private String mValorTotalBundle = "";
    private String mDataHoraBD = null;
    private String mNomeCliente = "";

    private boolean mAdicionarProdutoBD = false;
    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;
    private boolean isFormatarIntegerAtualizado = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vend_quant);

        Log.v(TAG, "onCreate");

        initViews();

        // Recebe dados de VendListActivity
        Intent intentDadosProduto = getIntent();
        mUriAtual = intentDadosProduto.getData();

        // Verifica se os dados recebidos indica se e para Adicionar ou Editar registro
        if (intentDadosProduto.hasExtra(ADICIONAR_VENDA)) {

            mAdicionarProdutoBD = intentDadosProduto.getStringExtra(
                    ADICIONAR_VENDA).equals(ADICIONAR);
        }

        // Se for para adicionar coloca titulo na activity ADICIONAR
        if (mAdicionarProdutoBD) {

            Log.v(TAG, "mAdicionarProdutoBD - Adicionar");
            Log.v(TAG, "mAdicionarProdutoBD - Adicionar :" + mUriAtual.toString());

            setTitle(R.string.title_venda_add);
            getLoaderManager().initLoader(LOADER_VENDA_ADICIONAR, null, this);

            mTvQuantidadeProduto.setText(Const.UMA_UNIDADE_STRING);
            mEtQuantidade.setText(Const.UMA_UNIDADE_STRING);
            mEtQuantidade.setSelection(mEtQuantidade.getText().length());
        }

        // Se não for para adicionar coloca titulo na activity para EDITAR
        if (!mAdicionarProdutoBD) {

            Log.v(TAG, "!mAdicionarProdutoBD - Editar");

            setTitle(R.string.title_venda_edit);
            getLoaderManager().initLoader(LOADER_VENDA_EDITAR, null, this);
        }

        // Se tiver uma instancia salva então ativa
        verificarSavedInstanceState(savedInstanceState);

        // Verifica a entrada de caracteres nos edits
        controleTextWatcher();

        // Verifica a maudança de estado do Switch
        controleSwitchCheckedChange();

        // Botão abre activity VendListClienteActivity, para selecionar cliente para venda a prazo
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

        // Coloca foco e seleciona dados do edit quantidade
        mEtQuantidade.setSelectAllOnFocus(true);

        // Tira o foco e coloca valor zero nos edits
        Utilidades.semFocoZerado(mEtAdicional);
        Utilidades.semFocoZerado(mEtDesconto);
        Utilidades.semFocoZerado(mEtPrazo);
    }


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
                    mUriCliente = ContentUris.withAppendedId(AcessoClientes.CONTENT_URI_CLIENTES, mIdCliente);
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
                    new Intent(VendQuantActivity.this, VendListClienteActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(URI_ATUAL, mUriAtual.toString());
            bundle.putString(VALOR_UNIDADE, String.valueOf(mValorUnidadeProduto));

            intentListaCliente.putExtras(bundle);

            startActivityForResult(intentListaCliente, Const.COD_RESULT_VENDA_CLIENTES);

            Utilidades.fecharTecladoView(VendQuantActivity.this, mButCliente);
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

                Dialogos.homeDescartarConfirmar(
                        VendQuantActivity.this,
                        VendQuantActivity.this);

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

        Dialogos.onBackPressedDescartarConfirmar(
                VendQuantActivity.this,
                VendQuantActivity.this);
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

            mEtQuantidade.setError(getString(R.string.error_valor_valido));
            mEtQuantidade.requestFocus();
            return;
        }

        // Converte as String dos campos valorAdicional, valorDesconto  e valorPrazo para double
        double valorAdicionalDouble = Formatar.formatarParaDouble(valorAdicionalEditText);
        double valorDescontoDouble = Formatar.formatarParaDouble(valorDescontoEditText);
        double valorPrazoDouble = Formatar.formatarParaDouble(valorPrazoEditText);


        // Se Switch adicional estiver Checked
        if (temAdicionalSwitch) {

            // O valor desse campo deve ser positivo
            if (valorAdicionalDouble == 0) {

                mEtAdicional.setError(getString(R.string.error_valor_valido_adicional));
                mEtAdicional.requestFocus();
                return;
            }
        }

        // Se Switch desconto estiver Checked
        if (temDescontoSwitch) {

            // O valor desse campo deve ser positivo
            if (valorDescontoDouble == 0) {

                mEtDesconto.setError(getString(R.string.error_valor_valido_desconto));
                mEtDesconto.requestFocus();
                return;
            }
        }

        if (temPrazoSwitch) {

            // O valor desse campo deve ser positivo
            if (valorPrazoDouble == 0) {

                mEtPrazo.setError(getString(R.string.error_valor_valido_prazo));
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

            valuesVendaPrazo.put(Contract.AcessoAReceber.CLIENTE_ID, mIdCliente);
            valuesVendaPrazo.put(Contract.AcessoAReceber.CLIENTE_NOME, nomeClienteTextView);
            valuesVendaPrazo.put(Contract.AcessoAReceber.TIPO_ENTRADA, ConstDB.TIPO_VENDA);
            valuesVendaPrazo.put(Contract.AcessoAReceber.VALOR, valorPrazoDouble);
            valuesVendaPrazo.put(Contract.AcessoAReceber.DESCRICAO, String.format(
                    getResources().getString(R.string.text_venda_a_prazo_venda),
                    quatidadeEditText,
                    nomeProdutoTextView));
        }

        // Coloca dados em um objeto values para ser salvo no BD
        ContentValues values = new ContentValues();
        values.put(AcessoVenda.NOME_PRODUTO, nomeProdutoTextView);
        values.put(AcessoVenda.QUANTIDADE, quantidadeInt);
        values.put(AcessoVenda.VALOR_UNIDADE, mValorUnidadeProduto);
        values.put(AcessoVenda.VALOR_ADICIONAL, valorAdicionalDouble);
        values.put(AcessoVenda.VALOR_DESCONTO, valorDescontoDouble);
        values.put(AcessoVenda.VALOR_PRAZO, valorPrazoDouble);
        valuesVendaPrazo.put(Contract.AcessoAReceber.DATA_HORA, DataHora.obterDataHoraSistema());
        if (temPrazoSwitch) {
            values.put(AcessoVenda.ID_CLIENTE, mIdCliente);
        }

        // Salva dados no BD
        if (mAdicionarProdutoBD) {

            values.put(AcessoVenda.DATA_HORA, DataHora.obterDataHoraSistema());

            Crud.inserir(VendQuantActivity.this, AcessoVenda.CONTENT_URI_VENDA, values);

            if (temPrazoSwitch) {

                Crud.inserir(VendQuantActivity.this, Contract.AcessoAReceber.CONTENT_URI_ARECEBER, valuesVendaPrazo);
            }

            Log.v(TAG, "salvarDadosBD - inserir");

        } else {

            if (temPrazoSwitch) {

                Snackbar.make(mButCliente, getString(R.string.error_nome_edicao_nao_permitida), Snackbar.LENGTH_INDEFINITE).show();
                return;

            } else {

                values.put(AcessoVenda.DATA_HORA, mDataHoraBD);

                Crud.editar(VendQuantActivity.this, mUriAtual, values);

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
                    mUriAtual,
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
                    AcessoVenda._ID,
                    AcessoVenda.NOME_PRODUTO,
                    AcessoVenda.QUANTIDADE,
                    AcessoVenda.DATA_HORA,
                    AcessoVenda.VALOR_ADICIONAL,
                    AcessoVenda.VALOR_DESCONTO,
                    AcessoVenda.VALOR_PRAZO,
                    AcessoVenda.ID_CLIENTE,
                    AcessoVenda.VALOR_UNIDADE
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

        if (i == LOADER_CLIENTE) {

            Log.v(TAG, "onCreateLoader - LOADER_CLIENTE");

            // Trazer todos os dados de um clientes especifico indentificado pelo mUriAtual
            String[] projection = {
                    AcessoClientes._ID,
                    AcessoClientes.NOME,
                    AcessoClientes.TELEFONE
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

            String nomeProdutoBD = cursor.getString(cursor.getColumnIndex(AcessoVenda.NOME_PRODUTO));
            mDataHoraBD = cursor.getString(cursor.getColumnIndex(AcessoVenda.DATA_HORA));

            int quantidadeBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.QUANTIDADE));
            double valorUnidadeBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_UNIDADE));
            double valorAdicionalBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_ADICIONAL));
            double valorDescontoBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_DESCONTO));
            double valorPrazoBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_PRAZO));
            double valorTotalBD = Calculos.calcularValorTotalVendaDouble(
                    quantidadeBD,
                    valorUnidadeBD,
                    valorAdicionalBD,
                    valorDescontoBD
            );

            mValorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_UNIDADE));

            int idCliente = cursor.getInt(cursor.getColumnIndex(AcessoVenda.ID_CLIENTE));
            mIdCliente = idCliente;
            mUriCliente = ContentUris.withAppendedId(AcessoClientes.CONTENT_URI_CLIENTES, mIdCliente);

            String nomeClienteBD = "";
            if (mIdCliente > 0) {
                nomeClienteBD = SearchDB.Pesquisarcliente(VendQuantActivity.this, idCliente);
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

            mNomeCliente = cursor.getString(cursor.getColumnIndex(AcessoClientes.NOME));
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
                Utilidades.mostrarTeclado(VendQuantActivity.this, mEtQuantidade);
                return true;

            case R.id.et_vend_quant_valor_desconto:
                mEtDesconto.requestFocus();
                mEtDesconto.setSelection(mEtDesconto.getText().length());
                Utilidades.mostrarTeclado(VendQuantActivity.this, mEtDesconto);
                return true;

            case R.id.et_vend_quant_valor_adicional:
                mEtAdicional.requestFocus();
                mEtAdicional.setSelection(mEtAdicional.getText().length());
                Utilidades.mostrarTeclado(VendQuantActivity.this, mEtAdicional);
                return true;

            case R.id.et_vend_quant_valor_prazo:
                mEtPrazo.requestFocus();
                mEtPrazo.setSelection(mEtPrazo.getText().length());
                Utilidades.mostrarTeclado(VendQuantActivity.this, mEtPrazo);
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
    private void controleTextWatcher() {

        Log.v(TAG, "controleTextWatcher");

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

                mEtAdicional.setText(Formatar.formatarParaCurrency(formatarCharSequenceString(charSequence)));

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
                        formatarEditsDouble(mEtAdicional);

                if (formatarCharSequenceDouble(charSequence) > valorVendaAdicional) {

                    mEtDesconto.setError(String.format(getResources().getString(R.string.error_campo_desconto_maior_venda), Formatar.formatarDoubleParaCurrency(valorVendaAdicional)));
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

                mEtDesconto.setText(Formatar.formatarParaCurrency(formatarCharSequenceString(charSequence)));

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
                        formatarEditsDouble(mEtAdicional) - formatarEditsDouble(mEtDesconto);

                if (formatarCharSequenceDouble(charSequence) / 100 > valorVendaAdicionalDesconto) {

                    mEtDesconto.setError(String.format(getResources().getString(R.string.error_campo_prazo_maior_venda), Formatar.formatarDoubleParaCurrency(valorVendaAdicionalDesconto)));
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

                mEtPrazo.setText(Formatar.formatarParaCurrency(formatarCharSequenceString(charSequence)));

                mEtPrazo.setSelection(mEtPrazo.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /* Veifica se houve alteração no estado do Switch*/
    private void controleSwitchCheckedChange() {

        Log.v(TAG, "");

        /* Se Switch estiver Checked fica visivel edit para entrada de valores
         * Se Switch não estiver Checked o edit para entrada de dados fica invisivel
         */
        mSwitchAdicional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {

                    layoutAdicional.setVisibility(View.VISIBLE);

                    if (mAdicionarProdutoBD) {
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

                    if (mAdicionarProdutoBD) {
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

                    if (mAdicionarProdutoBD) {
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
