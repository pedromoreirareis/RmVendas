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
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class VendQuantActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_VENDA_ADICIONAR = 0;
    private static final int LOADER_VENDA_EDITAR = 1;
    private final NumberFormat mValorFormatarCurrency = NumberFormat.getCurrencyInstance();

    private TextView mTvNomeProduto;
    private TextView mTvValorTotal;
    private EditText mEtQuantidade;
    private EditText mEtCobertura;
    private EditText mEtDesconto;
    private final EditText.OnTouchListener mTouchListnerEditCursorFim = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int id = view.getId();

            switch (id) {

                case R.id.et_quant_vend_quant:
                    mEtQuantidade.requestFocus();
                    mEtQuantidade.setSelection(mEtQuantidade.getText().length());
                    Utilidades.mostrarTeclado(VendQuantActivity.this, mEtQuantidade);
                    return true;

                case R.id.et_valor_desconto_vend_quant:
                    mEtDesconto.requestFocus();
                    mEtDesconto.setSelection(mEtDesconto.getText().length());
                    Utilidades.mostrarTeclado(VendQuantActivity.this, mEtDesconto);
                    return true;

                case R.id.et_valor_cobertura_vend_quant:
                    mEtCobertura.requestFocus();
                    mEtCobertura.setSelection(mEtCobertura.getText().length());
                    Utilidades.mostrarTeclado(VendQuantActivity.this, mEtCobertura);
                    return true;

                default:
                    return false;
            }
        }
    };
    private Switch mSwitchCobertura;
    private Switch mSwitchDesconto;
    private Switch mSwitchPrazo;
    private TextInputLayout layoutCobertura;
    private TextInputLayout layoutDesconto;
    private Uri mUriAtual = null;
    private double mValorUnidadeProduto = 0;
    private String mDataHoraBD = "";
    private boolean mAdicionarProdutoBD = false;
    private boolean isDadosAlterado = false;
    private final Switch.OnTouchListener mTouchListenerSwitch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            isDadosAlterado = true;
            return false;
        }
    };
    private boolean isFormatarCurrencyAtualizado = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vend_quant);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (intent.hasExtra(Constantes.VENDA_ADICIONAR)) {

            mAdicionarProdutoBD = intent.getStringExtra(Constantes.VENDA_ADICIONAR).equals(Constantes.VENDA_ADICIONAR);
        }

        if (mAdicionarProdutoBD) {

            setTitle(R.string.title_venda_add);
            getLoaderManager().initLoader(LOADER_VENDA_ADICIONAR, null, this);
        }

        if (!mAdicionarProdutoBD) {

            setTitle(R.string.title_vend_edit);
            getLoaderManager().initLoader(LOADER_VENDA_EDITAR, null, this);
        }

        mTvNomeProduto = (TextView) findViewById(R.id.tv_nome_vend_quant);
        mTvValorTotal = (TextView) findViewById(R.id.tv_valor_total_vend_quant);
        mEtQuantidade = (EditText) findViewById(R.id.et_quant_vend_quant);
        mEtDesconto = (EditText) findViewById(R.id.et_valor_desconto_vend_quant);
        mEtCobertura = (EditText) findViewById(R.id.et_valor_cobertura_vend_quant);
        mSwitchCobertura = (Switch) findViewById(R.id.switch_cobertura_vend_quant);
        mSwitchDesconto = (Switch) findViewById(R.id.switch_desconto_vend_quant);
        mSwitchPrazo = (Switch) findViewById(R.id.switch_prazo);
        layoutDesconto = (TextInputLayout) findViewById(R.id.til_desc);
        layoutCobertura = (TextInputLayout) findViewById(R.id.til_cobert);

        if (mAdicionarProdutoBD) {
            mEtQuantidade.setText("1");
            mEtQuantidade.setSelection(mEtQuantidade.getText().length());
        }

        mSwitchCobertura.setOnTouchListener(mTouchListenerSwitch);
        mSwitchDesconto.setOnTouchListener(mTouchListenerSwitch);

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

        mSwitchCobertura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {

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
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {

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


        mEtQuantidade.setOnTouchListener(mTouchListnerEditCursorFim);
        mEtCobertura.setOnTouchListener(mTouchListnerEditCursorFim);
        mEtDesconto.setOnTouchListener(mTouchListnerEditCursorFim);

        // mEtQuantidade.setCursorVisible(false);  // Cursor fica invisivel - roberta pediu visivel
        mEtQuantidade.setSelectAllOnFocus(true); // estava false no entanto na primeira entrada do usuario nao apaga texto anterior testando true
        Utilidades.semCursorFocoSelecaoZerado(mEtCobertura);
        Utilidades.semCursorFocoSelecaoZerado(mEtDesconto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_salvar:
                salvarDadosBD();
                return true;
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

    private void salvarDadosBD() {

        String nomeProdutoTextView = mTvNomeProduto.getText().toString().trim();
        String quatidadeEditText = mEtQuantidade.getText().toString().trim();
        String valorCoberturaEditText = mEtCobertura.getText().toString().trim();
        String valorDescontoEditText = mEtDesconto.getText().toString().trim();

        boolean temCoberturaSwitch = mSwitchCobertura.isChecked();
        boolean temDescontoSwitch = mSwitchDesconto.isChecked();
        boolean temPrazoSwitch = mSwitchPrazo.isChecked();


        // Validações
        if (TextUtils.isEmpty(quatidadeEditText)) {

            mEtQuantidade.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (temCoberturaSwitch) {

            if (TextUtils.isEmpty(valorCoberturaEditText)) {

                mEtCobertura.setError(getString(R.string.error_campo_vazio));
                return;
            }

            if (valorCoberturaEditText.equals("0")) {

                mEtCobertura.setError(getString(R.string.error_valor_maior_zero));
                return;
            }
        }

        if (temDescontoSwitch) {

            if (TextUtils.isEmpty(valorDescontoEditText)) {

                mEtDesconto.setError(getString(R.string.error_campo_vazio));
                return;
            }

            if (valorDescontoEditText.equals("0")) {

                mEtDesconto.setError(getString(R.string.error_valor_maior_zero));
                return;
            }
        }

        if (TextUtils.isEmpty(quatidadeEditText)) {

            quatidadeEditText = "1";
        }

        if (!temCoberturaSwitch) {

            valorCoberturaEditText = "0";
        }

        if (!temDescontoSwitch) {

            valorDescontoEditText = "0";
        }

        // Conversões
        int quantidadeInt = Integer.parseInt(quatidadeEditText);
        double valorCoberturaDouble = Formatar.formatarParaDouble(valorCoberturaEditText);
        double valorDescontoDouble = Formatar.formatarParaDouble(valorDescontoEditText);
        double valorTotalDoouble = Calculos.calcularValorVendaBoloDouble(mValorUnidadeProduto, quantidadeInt, valorCoberturaDouble, valorDescontoDouble);

        if (quantidadeInt < 1) {
            mEtQuantidade.setError(getString(R.string.error_valor_menor_um));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoVenda.COLUNA_VENDAS_NOME_PRODUTO, nomeProdutoTextView);
        values.put(AcessoVenda.COLUNA_VENDAS_QUANTIDADE_VENDIDA, quantidadeInt);
        values.put(AcessoVenda.COLUNA_VENDAS_VALOR_TOTAL_VENDA, valorTotalDoouble);
        values.put(AcessoVenda.COLUNA_VENDAS_VALOR_UMA_UNIDADE_PRODUTO, mValorUnidadeProduto);
        values.put(AcessoVenda.COLUNA_VENDAS_VALOR_COBERTURA, valorCoberturaDouble);
        values.put(AcessoVenda.COLUNA_VENDAS_VALOR_DESCONTO, valorDescontoDouble);

        if (temCoberturaSwitch) {

            values.put(AcessoVenda.COLUNA_VENDAS_TEM_COBERTURA, Constantes.COBERTURA_SIM);
        } else {

            values.put(AcessoVenda.COLUNA_VENDAS_TEM_COBERTURA, Constantes.COBERTURA_NAO);
        }


        if (temDescontoSwitch) {

            values.put(AcessoVenda.COLUNA_VENDAS_TEM_DESCONTO, Constantes.DESCONTO_SIM);
        } else {

            values.put(AcessoVenda.COLUNA_VENDAS_TEM_DESCONTO, Constantes.DESCONTO_NAO);
        }

        if (temPrazoSwitch) {

            values.put(AcessoVenda.COLUNA_VENDAS_A_PRAZO, Constantes.PRAZO_SIM);

        } else {

            values.put(AcessoVenda.COLUNA_VENDAS_A_PRAZO, Constantes.PRAZO_NAO);
        }

        if (mAdicionarProdutoBD) {

            values.put(AcessoVenda.COLUNA_VENDAS_DATA, DataHora.obterDataHoraSistema());

            Crud.inserir(VendQuantActivity.this, AcessoVenda.CONTENT_URI_VENDA, values);

        } else {

            values.put(AcessoVenda.COLUNA_VENDAS_DATA, mDataHoraBD);

            Crud.editar(VendQuantActivity.this, mUriAtual, values);
        }

        finish();
    }

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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if (i == LOADER_VENDA_ADICIONAR) {

            String[] projection = {
                    AcessoProdutos._ID,
                    AcessoProdutos.COLUNA_PRODUTOS_VALOR,
                    AcessoProdutos.COLUNA_PRODUTOS_NOME
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

        if (i == LOADER_VENDA_EDITAR) {

            String[] projection = {
                    AcessoVenda._ID,
                    AcessoVenda.COLUNA_VENDAS_NOME_PRODUTO,
                    AcessoVenda.COLUNA_VENDAS_QUANTIDADE_VENDIDA,
                    AcessoVenda.COLUNA_VENDAS_DATA,
                    AcessoVenda.COLUNA_VENDAS_VALOR_TOTAL_VENDA,
                    AcessoVenda.COLUNA_VENDAS_A_PRAZO,
                    AcessoVenda.COLUNA_VENDAS_TEM_COBERTURA,
                    AcessoVenda.COLUNA_VENDAS_VALOR_COBERTURA,
                    AcessoVenda.COLUNA_VENDAS_TEM_DESCONTO,
                    AcessoVenda.COLUNA_VENDAS_VALOR_DESCONTO,
                    AcessoVenda.COLUNA_VENDAS_VALOR_UMA_UNIDADE_PRODUTO
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        if (loader.getId() == LOADER_VENDA_EDITAR && cursor.moveToFirst()) {

            String nomeProdutoBD = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_NOME_PRODUTO));
            mDataHoraBD = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_DATA));

            int quantidadeBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_QUANTIDADE_VENDIDA));
            int temCoberturaBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_TEM_COBERTURA));
            int temDescontoBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_TEM_DESCONTO));
            int temPrazoBD = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_A_PRAZO));

            double valorCoberturaBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_VALOR_COBERTURA));
            double valorDescontoBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_VALOR_DESCONTO));
            double valorTotalBD = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_VALOR_TOTAL_VENDA));
            mValorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_VALOR_UMA_UNIDADE_PRODUTO));

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

        if (loader.getId() == LOADER_VENDA_ADICIONAR && cursor.moveToFirst()) {

            String nomeProduto = cursor.getString(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTOS_NOME));
            mValorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTOS_VALOR));

            mTvNomeProduto.setText(nomeProduto);
            mTvValorTotal.setText(mValorFormatarCurrency.format(mValorUnidadeProduto));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
