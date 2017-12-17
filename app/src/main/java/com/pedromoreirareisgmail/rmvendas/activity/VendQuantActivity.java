package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
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
import com.pedromoreirareisgmail.rmvendas.Utils.Calculos;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoProdutos;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoVenda;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import java.text.NumberFormat;

import static com.pedromoreirareisgmail.rmvendas.Utils.Calculos.calcularValorVendaBolo;

public class VendQuantActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, EditText.OnTouchListener {

    private static final int LOADER_VENDA_ADICIONAR = 0;
    private static final int LOADER_VENDA_EDITAR = 1;

    private final NumberFormat mValorFormatarCurrency = NumberFormat.getCurrencyInstance();

    private TextView mTvNomeProduto;
    private TextView mTvValorTotal;
    private TextView mTvCliente;
    private EditText mEtQuantidade;
    private EditText mEtCobertura;
    private EditText mEtDesconto;
    private EditText mEtPrazo;
    private Button mButCliente;
    private Switch mSwitchCobertura;
    private Switch mSwitchDesconto;
    private Switch mSwitchPrazo;
    private TextInputLayout layoutCobertura;
    private TextInputLayout layoutDesconto;
    private LinearLayout layoutPrazo;
    private Uri mUriAtual = null;
    private double mValorUnidadeProduto = 0;
    private String mDataHoraBD = null;
    private boolean mAdicionarProdutoBD = false;
    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vend_quant);

        // Recebe dados de VendListActivity
        Intent intent = getIntent();
        mUriAtual = intent.getData();

        // Verifica se os dados recebidos indica se e para Adicionar ou Editar registro
        if (intent.hasExtra(Constantes.VENDA_ADICIONAR)) {

            mAdicionarProdutoBD = intent.getStringExtra(Constantes.VENDA_ADICIONAR).equals(Constantes.VENDA_ADICIONAR);
        }

        // Se for para adicionar coloca titulo na activity ADICIONAR
        if (mAdicionarProdutoBD) {

            setTitle(R.string.title_venda_add);
            getLoaderManager().initLoader(LOADER_VENDA_ADICIONAR, null, this);
        }

        // Se não for para adicionar coloca titulo na activity para EDITAR
        if (!mAdicionarProdutoBD) {

            setTitle(R.string.title_vend_edit);
            getLoaderManager().initLoader(LOADER_VENDA_EDITAR, null, this);
        }

        // Referencia itens do layout
        mTvNomeProduto = (TextView) findViewById(R.id.tv_vend_quant_nome_produto);
        mTvValorTotal = (TextView) findViewById(R.id.tv_vend_quant_valor_total);
        mTvCliente = (TextView) findViewById(R.id.tv_vend_quant_cliente);
        mButCliente = (Button) findViewById(R.id.but_vend_quant_cliente);
        mEtQuantidade = (EditText) findViewById(R.id.et_vend_quant_quantidade);
        mEtDesconto = (EditText) findViewById(R.id.et_vend_quant_valor_desconto);
        mEtCobertura = (EditText) findViewById(R.id.et_vend_quant_valor_cobertura);
        mEtPrazo = (EditText) findViewById(R.id.et_vend_quant_valor_prazo);
        mSwitchCobertura = (Switch) findViewById(R.id.switch_vend_quant_cobertura);
        mSwitchDesconto = (Switch) findViewById(R.id.switch_vend_quant_desconto);
        mSwitchPrazo = (Switch) findViewById(R.id.switch_vend_quant_prazo);
        layoutDesconto = (TextInputLayout) findViewById(R.id.til_vend_quant_desconto);
        layoutCobertura = (TextInputLayout) findViewById(R.id.til_vend_quant_cobertura);
        layoutPrazo = (LinearLayout) findViewById(R.id.ll_vend_quant_prazo);

        // Se for para adicionar, colocar valor "1" no edit de quantidade
        if (mAdicionarProdutoBD) {
            mEtQuantidade.setText("1");
            mEtQuantidade.setSelection(mEtQuantidade.getText().length());
        }


        // Verifica a entrada de caracteres nos edits
        controleTextWatcher();

        // Verifica a maudança de estado do Switch
        controleSwitchCheckedChange();

        // Botão abre activity VendListClienteActivity, para selecionar cliente para venda a prazo
        mButCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Apos ir para a VendListClienteActivity dados sao perdidos - salvar dados com savedIntanceState
                Intent listaClienteIntent =
                        new Intent(VendQuantActivity.this, VendListClienteActivity.class);
                startActivity(listaClienteIntent);

                Utilidades.fecharTecladoView(VendQuantActivity.this, mButCliente);
            }
        });


        // Monitora toques nos edits
        mEtQuantidade.setOnTouchListener(this);
        mEtCobertura.setOnTouchListener(this);
        mEtDesconto.setOnTouchListener(this);
        mEtPrazo.setOnTouchListener(this);

        // Monitora toques nos Switchs
        mSwitchCobertura.setOnTouchListener(this);
        mSwitchDesconto.setOnTouchListener(this);
        mSwitchPrazo.setOnTouchListener(this);


        // Coloca foco e seleciona dados do edit quantidade
        mEtQuantidade.setSelectAllOnFocus(true);

        // Tira o foco e coloca valor zero nos edits
        Utilidades.semFocoZerado(mEtCobertura);
        Utilidades.semFocoZerado(mEtDesconto);
        Utilidades.semFocoZerado(mEtPrazo);
    }


    /**
     * Cria o menu
     *
     * @param menu objeto do menu
     * @return verdadeiro se menu foi inflado
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    /**
     * Define o que fazer ao selecionar um item no menu
     *
     * @param item item selecionado
     * @return verdadeiro se item foi selecionado no menu e a ação foi executada com sucesso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

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

                DialogInterface.OnClickListener descartarButClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(VendQuantActivity.this);
                            }
                        };

                Dialogos.dialogoConfirmarAlteracao(
                        VendQuantActivity.this,
                        descartarButClickListener
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Salva dados no BD
     * Recebe dados dos edits e dos Switchs, faz validações, coloca dados no objeto values e
     * salva no banco de dados
     */
    private void salvarDadosBD() {

        String nomeProdutoTextView = mTvNomeProduto.getText().toString().trim();
        String quatidadeEditText = mEtQuantidade.getText().toString().trim();
        String valorCoberturaEditText = mEtCobertura.getText().toString().trim();
        String valorDescontoEditText = mEtDesconto.getText().toString().trim();

        boolean temCoberturaSwitch = mSwitchCobertura.isChecked();
        boolean temDescontoSwitch = mSwitchDesconto.isChecked();
        boolean temPrazoSwitch = mSwitchPrazo.isChecked();


        // Campo não pode ser vazio
        if (TextUtils.isEmpty(quatidadeEditText)) {

            mEtQuantidade.setError(getString(R.string.error_campo_vazio));
            mEtQuantidade.requestFocus();
            return;
        }


        // Se Switch cobertura estiver Checked
        if (temCoberturaSwitch) {

            // Campo valor nao pode fica vazio
            if (TextUtils.isEmpty(valorCoberturaEditText)) {

                mEtCobertura.setError(getString(R.string.error_campo_vazio));
                mEtCobertura.requestFocus();
                return;
            }

            // O valor desse campo deve ser positivo
            if (valorCoberturaEditText.equals("0")) {

                mEtCobertura.setError(getString(R.string.error_valor_maior_zero));
                mEtCobertura.requestFocus();
                return;
            }
        }

        // Se Switch desconto estiver Checked
        if (temDescontoSwitch) {

            // Campo não pode ficar vazio
            if (TextUtils.isEmpty(valorDescontoEditText)) {

                mEtDesconto.setError(getString(R.string.error_campo_vazio));
                mEtDesconto.requestFocus();
                return;
            }

            // O valor desse campo deve ser positivo
            if (valorDescontoEditText.equals("0")) {

                mEtDesconto.setError(getString(R.string.error_valor_maior_zero));
                mEtDesconto.requestFocus();
                return;
            }
        }

        // Se campo estiver vazio, atribui o valor "1"
        if (TextUtils.isEmpty(quatidadeEditText)) {

            quatidadeEditText = "1";
        }

        // Se Switch cobertura não estiver Checked atribui o valor "0" ao valor da cobertura
        if (!temCoberturaSwitch) {

            valorCoberturaEditText = "0";
        }

        // Se Switch desconto não estiver Checked atribui o valor "0" ao valor do desconto
        if (!temDescontoSwitch) {

            valorDescontoEditText = "0";
        }

        // Converte os String do campo valorQuantidade para inteiro
        int quantidadeInt = Integer.parseInt(quatidadeEditText);

        // Campo não pode ser negativo
        if (quantidadeInt < 1) {
            mEtQuantidade.setError(getString(R.string.error_valor_menor_um));
            return;
        }

        // Converte as String dos campos valorCobertura, valorDesconto para double
        double valorCoberturaDouble = Formatar.formatarParaDouble(valorCoberturaEditText);
        double valorDescontoDouble = Formatar.formatarParaDouble(valorDescontoEditText);

        // Faz o calculo de qual é o valor final da venda, esse valor sera salvo no BD
        double valorTotalDoouble = Calculos.calcularValorVendaBoloDouble(
                mValorUnidadeProduto,
                quantidadeInt,
                valorCoberturaDouble,
                valorDescontoDouble);


        // Coloca dados em um objeto values para ser salvo no BD
        ContentValues values = new ContentValues();
        values.put(AcessoVenda.NOME_PRODUTO, nomeProdutoTextView);
        values.put(AcessoVenda.QUANTIDADE_VENDIDA, quantidadeInt);
        values.put(AcessoVenda.VALOR_TOTAL_VENDA, valorTotalDoouble);
        values.put(AcessoVenda.VALOR_UMA_UNIDADE_PRODUTO, mValorUnidadeProduto);
        values.put(AcessoVenda.VALOR_COBERTURA, valorCoberturaDouble);
        values.put(AcessoVenda.VALOR_DESCONTO, valorDescontoDouble);

        if (temCoberturaSwitch) {

            values.put(AcessoVenda.TEM_COBERTURA, Constantes.COBERTURA_SIM);
        } else {

            values.put(AcessoVenda.TEM_COBERTURA, Constantes.COBERTURA_NAO);
        }


        if (temDescontoSwitch) {

            values.put(AcessoVenda.TEM_DESCONTO, Constantes.DESCONTO_SIM);
        } else {

            values.put(AcessoVenda.TEM_DESCONTO, Constantes.DESCONTO_NAO);
        }

        if (temPrazoSwitch) {

            values.put(AcessoVenda.A_PRAZO, Constantes.PRAZO_SIM);

        } else {

            values.put(AcessoVenda.A_PRAZO, Constantes.PRAZO_NAO);
        }

        // Salva dados no BD
        if (mAdicionarProdutoBD) {

            values.put(AcessoVenda.DATA, DataHora.obterDataHoraSistema());

            Crud.inserir(VendQuantActivity.this, AcessoVenda.CONTENT_URI_VENDA, values);

        } else {

            values.put(AcessoVenda.DATA, mDataHoraBD);

            Crud.editar(VendQuantActivity.this, mUriAtual, values);
        }

        finish();
    }

    /* Botão voltar (embaixo)
     * Se dados foram alterados abre Dialog para decidir se ira descatar dados alterados e sair
     * da Activity ou se deseja ficar na activity e continuar as alterações
     */
    @Override
    public void onBackPressed() {

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        DialogInterface.OnClickListener descartarButClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        Dialogos.dialogoConfirmarAlteracao(
                VendQuantActivity.this,
                descartarButClickListener
        );
    }

    /**
     * Define parametros para pesquisa no BD
     *
     * @param i      Loader utilizado na pesquisa ao BD
     * @param bundle argumentos dentro do loader de pesquisa
     * @return cursor com resultados da pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        /* Se LOADER_VENDA_ADICIONAR
         * com Uri do produto a ser adicionado, faz a pesquisa por esse produto e retorna com o nome
         * do produto, valor e seu id
         */
        if (i == LOADER_VENDA_ADICIONAR) {

            String[] projection = {
                    AcessoProdutos._ID,
                    AcessoProdutos.VALOR,
                    AcessoProdutos.NOME
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

            String[] projection = {
                    AcessoVenda._ID,
                    AcessoVenda.NOME_PRODUTO,
                    AcessoVenda.QUANTIDADE_VENDIDA,
                    AcessoVenda.DATA,
                    AcessoVenda.VALOR_TOTAL_VENDA,
                    AcessoVenda.A_PRAZO,
                    AcessoVenda.TEM_COBERTURA,
                    AcessoVenda.VALOR_COBERTURA,
                    AcessoVenda.TEM_DESCONTO,
                    AcessoVenda.VALOR_DESCONTO,
                    AcessoVenda.VALOR_UMA_UNIDADE_PRODUTO
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

        return null;
    }

    /**
     * Define o que fazer com os resultados das pesquisas
     *
     * @param loader Loader responsavel pela pesquisa
     * @param cursor cursor com resultados da pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /* Retorna todos os dados de uma vena especifica, e coloca resultados em seus respectivos
         * campos, de forma que possa ser feita a edicao
         */
        if (loader.getId() == LOADER_VENDA_EDITAR && cursor.moveToFirst()) {

            String nomeProdutoBD = cursor.getString(cursor.getColumnIndex(AcessoVenda.NOME_PRODUTO));
            mDataHoraBD = cursor.getString(cursor.getColumnIndex(AcessoVenda.DATA));

            int quantidadeBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.QUANTIDADE_VENDIDA));
            int temCoberturaBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.TEM_COBERTURA));
            int temDescontoBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.TEM_DESCONTO));
            int temPrazoBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.A_PRAZO));

            double valorCoberturaBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_COBERTURA));
            double valorDescontoBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_DESCONTO));
            double valorTotalBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_TOTAL_VENDA));
            mValorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_UMA_UNIDADE_PRODUTO));

            mTvNomeProduto.setText(nomeProdutoBD);
            mEtQuantidade.setText(String.valueOf(quantidadeBD));

            if (temCoberturaBD == Constantes.COBERTURA_SIM) {

                mSwitchCobertura.setChecked(true);
                layoutCobertura.setVisibility(View.VISIBLE);
                mEtCobertura.setText(mValorFormatarCurrency.format(valorCoberturaBD));

            } else {

                mSwitchCobertura.setChecked(false);
                layoutCobertura.setVisibility(View.GONE);
            }


            if (temDescontoBD == Constantes.DESCONTO_SIM) {

                mSwitchDesconto.setChecked(true);
                layoutDesconto.setVisibility(View.VISIBLE);
                mEtDesconto.setText(mValorFormatarCurrency.format(valorDescontoBD));

            } else {
                mSwitchDesconto.setChecked(false);
                layoutDesconto.setVisibility(View.GONE);
            }


            if (temPrazoBD == Constantes.PRAZO_SIM) {

                mSwitchPrazo.setChecked(true);

            } else {

                mSwitchPrazo.setChecked(false);
            }

            mTvValorTotal.setText(mValorFormatarCurrency.format(valorTotalBD));

            mEtQuantidade.requestFocus();
        }

        /* Faz pesquisa pelo id de um produto e econtro o seu nome e seu valor e colocar nos
         * respectivos campos
         */
        if (loader.getId() == LOADER_VENDA_ADICIONAR && cursor.moveToFirst()) {

            String nomeProduto = cursor.getString(cursor.getColumnIndex(AcessoProdutos.NOME));
            mValorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(AcessoProdutos.VALOR));

            mTvNomeProduto.setText(nomeProduto);
            mTvValorTotal.setText(mValorFormatarCurrency.format(mValorUnidadeProduto));
        }
    }

    /**
     * Define o que fazer com os resultados antigos de pesquisa ao iniciar uma nova pesquisa
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Monitora toques em views especificas
     * Monitora toques nos edits - Solicita foco, coloca curso a direita de todos os caracteres
     * mostrar o teclado se ele estiver fechado
     * <p>
     * Monitora toques nos Switchs -  indica se houve alteração no estado do Switch
     *
     * @param view  view que foi tocadas
     * @param event evento
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        switch (id) {

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

            case R.id.et_vend_quant_valor_cobertura:
                mEtCobertura.requestFocus();
                mEtCobertura.setSelection(mEtCobertura.getText().length());
                Utilidades.mostrarTeclado(VendQuantActivity.this, mEtCobertura);
                return true;

            case R.id.et_vend_quant_valor_prazo:
                mEtPrazo.requestFocus();
                mEtPrazo.setSelection(mEtPrazo.getText().length());
                Utilidades.mostrarTeclado(VendQuantActivity.this, mEtPrazo);
                return true;

            case R.id.switch_vend_quant_cobertura:
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

        /* Edits tem apenas caracteres numericos em seu teclado
         * Apos a entrada de caracteres, e feita a formatação para o estilo moeda para ser
         * apresentado ao usuario
         * Tambem e feito o calculo do valor de venda do produto apos a etrada da quantidade de
         * produto, valor da cobertura e do desconto caso tenha
         */
        mEtQuantidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isDadosAlterado = true;

                String vlQuant = charSequence.toString().trim();
                String vlCobert = mEtCobertura.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlDesc = mEtDesconto.getText().toString().trim().replaceAll("[^\\d]", "");

                mTvValorTotal.setText(calcularValorVendaBolo(vlQuant, vlCobert, vlDesc, mValorUnidadeProduto));

                mEtQuantidade.setSelection(mEtQuantidade.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mEtCobertura.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isDadosAlterado = true;

                String vlQuant = mEtQuantidade.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlCobert = charSequence.toString().trim().replaceAll("[^\\d]", "");
                String vlDesc = mEtDesconto.getText().toString().trim().replaceAll("[^\\d]", "");

                mTvValorTotal.setText(calcularValorVendaBolo(vlQuant, vlCobert, vlDesc, mValorUnidadeProduto));

                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtCobertura.setText(Formatar.formatarParaCurrency(vlCobert));

                mEtCobertura.setSelection(mEtCobertura.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mEtDesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isDadosAlterado = true;

                String vlQuant = mEtQuantidade.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlCobert = mEtCobertura.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlDesc = charSequence.toString().trim().replaceAll("[^\\d]", "");

                mTvValorTotal.setText(calcularValorVendaBolo(vlQuant, vlCobert, vlDesc, mValorUnidadeProduto));


                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtDesconto.setText(Formatar.formatarParaCurrency(vlDesc));

                mEtDesconto.setSelection(mEtDesconto.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        mEtPrazo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


                isDadosAlterado = true;

                String vlPrazo = charSequence.toString().trim().replaceAll("[^\\d]", "");
               /* String vlQuant = mEtQuantidade.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlCobert = mEtCobertura.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlDesc = charSequence.toString().trim().replaceAll("[^\\d]", "");

                mTvValorTotal.setText(calcularValorVendaBolo(vlQuant, vlCobert, vlDesc, mValorUnidadeProduto));
                */

                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtPrazo.setText(Formatar.formatarParaCurrency(vlPrazo));

                mEtPrazo.setSelection(mEtPrazo.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /* Veifica se houve alteração no estado do Switch*/
    private void controleSwitchCheckedChange() {

        /* Se Switch estiver Checked fica visivel edit para entrada de valores
         * Se Switch não estiver Checked o edit para entrada de dados fica invisivel
         */
        mSwitchCobertura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {

                    layoutCobertura.setVisibility(View.VISIBLE);

                    if (mAdicionarProdutoBD) {
                        mEtCobertura.setText("0");
                    }

                    mEtCobertura.requestFocus();


                } else {

                    layoutCobertura.setVisibility(View.GONE);
                    // Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchCobertura);
                    mEtCobertura.setText("0");
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
                    //Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchDesconto);
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
                    //Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchDesconto);
                    mEtPrazo.setText("0");
                }

            }
        });

    }
}
