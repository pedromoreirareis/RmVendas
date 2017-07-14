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
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.Crud;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoProdutos;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoVenda;

import static com.pedromoreirareisgmail.rmvendas.Utils.Utilidades.calculaValorBolo;

public class VendQuantActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_VENDA_EDITAR = 13;
    private static final int LOADER_VENDA_ADICIONAR = 11;

    private TextView mTvNome;
    private EditText mEtQuant;
    private Switch mSwitchCobertura;
    private EditText mEtCobertura;
    private Switch mSwitchDesc;
    private EditText mEtDesc;
    private TextView mTvTotal;
    private FrameLayout mFlCobet;
    private FrameLayout mFlDesc;

    private Uri mUriAtual = null;
    private boolean mAdicionar = false;
    private double mPrecoBolo = 0;
    private String mData = "";
    private boolean mAlteracao = false;

    private final EditText.OnTouchListener mTouchListenetEdit = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mAlteracao = true;
            return false;
        }
    };
    private final Switch.OnTouchListener mTouchListenerSwitch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mAlteracao = true;
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vend_quant);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (intent.hasExtra(Constantes.VENDA_ADICIONAR)) {

            mAdicionar = intent.getStringExtra(Constantes.VENDA_ADICIONAR).equals(Constantes.VENDA_ADICIONAR);

        }

        if (mAdicionar) {

            setTitle(R.string.title_venda_add);
            getLoaderManager().initLoader(LOADER_VENDA_ADICIONAR, null, this);
        }

        if (!mAdicionar) {

            setTitle(R.string.title_vend_edit);
            getLoaderManager().initLoader(LOADER_VENDA_EDITAR, null, this);
        }

        mTvNome = (TextView) findViewById(R.id.tv_nome_vend_quant);
        mEtQuant = (EditText) findViewById(R.id.et_quant_vend_quant);
        mSwitchCobertura = (Switch) findViewById(R.id.switch_cobertura_vend_quant);
        mEtCobertura = (EditText) findViewById(R.id.et_valor_cobertura_vend_quant);
        mSwitchDesc = (Switch) findViewById(R.id.switch_desconto_vend_quant);
        mEtDesc = (EditText) findViewById(R.id.et_valor_desconto_vend_quant);
        mTvTotal = (TextView) findViewById(R.id.tv_valor_total_vend_quant);
        mFlCobet = (FrameLayout) findViewById(R.id.fl_cobertura_vend_quant);
        mFlDesc = (FrameLayout) findViewById(R.id.fl_desconto_vend_quant);

        if (mAdicionar) {
            mEtCobertura.setText("0");
            mEtDesc.setText("0");
            mEtQuant.setText("1");
        }

        mEtQuant.setOnTouchListener(mTouchListenetEdit);
        mEtCobertura.setOnTouchListener(mTouchListenetEdit);
        mEtDesc.setOnTouchListener(mTouchListenetEdit);
        mSwitchCobertura.setOnTouchListener(mTouchListenerSwitch);
        mSwitchDesc.setOnTouchListener(mTouchListenerSwitch);

        mEtQuant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String vlQuant = charSequence.toString().trim();
                String vlCobert = mEtCobertura.getText().toString().trim();
                String vlDesc = mEtDesc.getText().toString().trim();

                mTvTotal.setText(calculaValorBolo(vlQuant, vlCobert, vlDesc, mPrecoBolo));
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

                String vlQuant = mEtQuant.getText().toString().trim();
                String vlCobert = charSequence.toString().trim();
                String vlDesc = mEtDesc.getText().toString().trim();

                mTvTotal.setText(calculaValorBolo(vlQuant, vlCobert, vlDesc, mPrecoBolo));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mEtDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String vlQuant = mEtQuant.getText().toString().trim();
                String vlCobert = mEtCobertura.getText().toString().trim();
                String vlDesc = charSequence.toString().trim();

                mTvTotal.setText(calculaValorBolo(vlQuant, vlCobert, vlDesc, mPrecoBolo));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSwitchCobertura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {

                    mFlCobet.setVisibility(View.VISIBLE);
                    Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchCobertura);

                } else {

                    mFlCobet.setVisibility(View.GONE);
                    Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchCobertura);
                    mEtCobertura.setText("0");
                }
            }
        });

        mSwitchDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {

                    mFlDesc.setVisibility(View.VISIBLE);
                    Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchDesc);

                } else {

                    mFlDesc.setVisibility(View.GONE);
                    Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchDesc);
                    mEtDesc.setText("0");
                }
            }
        });
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
                adicionar();
                return true;
            case android.R.id.home:
                if (!mAlteracao) {

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

                UtilsDialog.confirmarAlteracao(
                        VendQuantActivity.this,
                        descartarButClickListener
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void adicionar() {

        String nome;
        String quantString;
        String vlCobertString;
        String vlDescString;
        String vlTotalString;
        boolean temCobert;
        boolean temDesc;
        int quant;
        double valorCobert;
        double valorDesconto;
        double valorTotal;

        nome = mTvNome.getText().toString().trim();
        quantString = mEtQuant.getText().toString().trim();
        temCobert = mSwitchCobertura.isChecked();
        temDesc = mSwitchDesc.isChecked();
        vlTotalString = mTvTotal.getText().toString().trim();
        vlCobertString = mEtCobertura.getText().toString().trim();
        vlDescString = mEtDesc.getText().toString().trim();

        if (TextUtils.isEmpty(quantString)) {
            quantString = "0";
        }

        if (TextUtils.isEmpty(vlCobertString)) {
            vlCobertString = "0";
        }

        if (TextUtils.isEmpty(vlDescString)) {
            vlDescString = "0";
        }

        if (TextUtils.isEmpty(vlTotalString)) {
            vlTotalString = "0";
        }

        // Conversões
        quant = Integer.parseInt(quantString);
        valorCobert = Double.parseDouble(vlCobertString);
        valorDesconto = Double.parseDouble(vlDescString);
        valorTotal = Double.parseDouble(vlTotalString);

        // Validações
        if (TextUtils.isEmpty(quantString)) {
            mEtQuant.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (TextUtils.isEmpty(vlCobertString)) {
            mEtCobertura.setError(getString(R.string.error_campo_vazio));
            return;
        }
        if (TextUtils.isEmpty(vlDescString)) {
            mEtDesc.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (quant < 1) {
            mEtQuant.setError(getString(R.string.error_valor_menor_um));
            return;
        }

        if (valorCobert < 0) {
            mEtCobertura.setError(getString(R.string.error_valor_negativo));
            return;
        }

        if (valorDesconto < 0) {
            mEtDesc.setError(getString(R.string.error_valor_negativo));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoVenda.COLUNA_VENDA_NOME_PROD, nome);
        values.put(AcessoVenda.COLUNA_VENDA_QUANT, quant);
        values.put(AcessoVenda.COLUNA_VENDA_VALOR_PROD, valorTotal);
        values.put(AcessoVenda.COLUNA_VENDA_PRECO_UM_BOLO, mPrecoBolo);
        values.put(AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA, valorCobert);
        values.put(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO, valorDesconto);

        if (temCobert) {

            values.put(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA, Constantes.COBERTURA_SIM);
        }

        if (!temCobert) {

            values.put(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA, Constantes.COBERTURA_NAO);
        }

        if (temDesc) {

            values.put(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO, Constantes.DESCONTO_SIM);
        }

        if (!temDesc) {

            values.put(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO, Constantes.DESCONTO_NAO);
        }

        if (mAdicionar) {

            values.put(AcessoVenda.COLUNA_VENDA_DATA, Datas.getDateTime());

        } else {

            values.put(AcessoVenda.COLUNA_VENDA_DATA, mData);
        }

        if (mAdicionar) {

            Crud.inserir(VendQuantActivity.this, AcessoVenda.CONTENT_URI_VENDA, values);

        } else {

            Crud.editar(VendQuantActivity.this, mUriAtual, values);
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if (i == LOADER_VENDA_ADICIONAR) {

            String[] projection = {
                    AcessoProdutos._ID,
                    AcessoProdutos.COLUNA_PRODUTO_PRECO,
                    AcessoProdutos.COLUNA_PRODUTO_NOME
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
                    AcessoVenda.COLUNA_VENDA_NOME_PROD,
                    AcessoVenda.COLUNA_VENDA_QUANT,
                    AcessoVenda.COLUNA_VENDA_DATA,
                    AcessoVenda.COLUNA_VENDA_VALOR_PROD,
                    AcessoVenda.COLUNA_VENDA_TEM_COBERTURA,
                    AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA,
                    AcessoVenda.COLUNA_VENDA_TEM_DESCONTO,
                    AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO,
                    AcessoVenda.COLUNA_VENDA_PRECO_UM_BOLO

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

            String nomeBolo = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_NOME_PROD));
            int quantidade = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_QUANT));
            int temCobertura = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA));
            double valorCobertura = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA));
            int temDesconto = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO));
            double valorDesconto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO));
            double valorBolo = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));
            mData = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_DATA));
            mPrecoBolo = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRECO_UM_BOLO));

            mTvNome.setText(nomeBolo);
            mEtQuant.setText(String.valueOf(quantidade));

            if (temCobertura == Constantes.COBERTURA_SIM) {

                mSwitchCobertura.setChecked(true);
                mEtCobertura.setVisibility(View.VISIBLE);
                mEtCobertura.setText(String.valueOf(valorCobertura));
            }

            if (temCobertura == Constantes.COBERTURA_NAO) {

                mSwitchCobertura.setChecked(false);
                mEtCobertura.setVisibility(View.GONE);
            }

            if (temDesconto == Constantes.DESCONTO_SIM) {

                mSwitchDesc.setChecked(true);
                mEtDesc.setVisibility(View.VISIBLE);
                mEtDesc.setText(String.valueOf(valorDesconto));
            }

            if (temDesconto == Constantes.DESCONTO_NAO) {

                mSwitchDesc.setChecked(false);
                mEtDesc.setVisibility(View.GONE);
            }

            mTvTotal.setText(String.valueOf(valorBolo));
        }

        if (loader.getId() == LOADER_VENDA_ADICIONAR && cursor.moveToFirst()) {

            String mNomeDoBolo = cursor.getString(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_NOME));
            mPrecoBolo = cursor.getDouble(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_PRECO));

            mTvNome.setText(mNomeDoBolo);
            mTvTotal.setText(String.valueOf(mPrecoBolo));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onBackPressed() {

        if (!mAlteracao) {

            super.onBackPressed();
        }

        DialogInterface.OnClickListener descartarButClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        UtilsDialog.confirmarAlteracao(
                VendQuantActivity.this,
                descartarButClickListener
        );
    }
}
