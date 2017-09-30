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
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.UserInterface;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoProdutos;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoVenda;
import com.pedromoreirareisgmail.rmvendas.data.Crud;

import java.text.NumberFormat;

import static com.pedromoreirareisgmail.rmvendas.Utils.Utilidades.calculaValorBolo;

public class VendQuantActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_VENDA_EDITAR = 13;
    private static final int LOADER_VENDA_ADICIONAR = 11;
    private final NumberFormat mPreco = NumberFormat.getCurrencyInstance();
    private TextView mTvNome;
    private TextView mTvTotal;
    private EditText mEtQuant;
    private EditText mEtCobertura;
    private EditText mEtDesc;
    private final EditText.OnTouchListener mTouchListnerEditCursorFim = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int id = view.getId();

            switch (id) {

                case R.id.et_quant_vend_quant:
                    mEtQuant.requestFocus();
                    mEtQuant.setSelection(mEtQuant.getText().length());
                    return true;

                case R.id.et_valor_desconto_vend_quant:
                    mEtDesc.requestFocus();
                    mEtDesc.setSelection(mEtDesc.getText().length());
                    return true;

                case R.id.et_valor_cobertura_vend_quant:
                    mEtCobertura.requestFocus();
                    mEtCobertura.setSelection(mEtCobertura.getText().length());
                    return true;

                default:
                    return false;
            }
        }
    };
    private Switch mSwitchCobertura;
    private Switch mSwitchDesc;
    private Switch mSwitchPrazo;
    private TextInputLayout layoutCobert;
    private TextInputLayout layoutDesc;
    private Uri mUriAtual = null;
    private double mPrecoBolo = 0;
    private String mData = "";
    private boolean mAdicionar = false;
    private boolean isAlterado = false;
    private final Switch.OnTouchListener mTouchListenerSwitch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            isAlterado = true;
            return false;
        }
    };
    private boolean isUpdating = false;

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
        mTvTotal = (TextView) findViewById(R.id.tv_valor_total_vend_quant);
        mEtQuant = (EditText) findViewById(R.id.et_quant_vend_quant);
        mEtDesc = (EditText) findViewById(R.id.et_valor_desconto_vend_quant);
        mEtCobertura = (EditText) findViewById(R.id.et_valor_cobertura_vend_quant);
        mSwitchCobertura = (Switch) findViewById(R.id.switch_cobertura_vend_quant);
        mSwitchDesc = (Switch) findViewById(R.id.switch_desconto_vend_quant);
        mSwitchPrazo = (Switch) findViewById(R.id.switch_prazo);
        layoutDesc = (TextInputLayout) findViewById(R.id.til_desc);
        layoutCobert = (TextInputLayout) findViewById(R.id.til_cobert);

        if (mAdicionar) {
            mEtQuant.setText("1");
            mEtQuant.setSelection(mEtQuant.getText().length());
        }

        mSwitchCobertura.setOnTouchListener(mTouchListenerSwitch);
        mSwitchDesc.setOnTouchListener(mTouchListenerSwitch);

        mEtQuant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isAlterado = true;

                String vlQuant = charSequence.toString().trim();
                String vlCobert = mEtCobertura.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlDesc = mEtDesc.getText().toString().trim().replaceAll("[^\\d]", "");

                mTvTotal.setText(calculaValorBolo(vlQuant, vlCobert, vlDesc, mPrecoBolo));

                mEtQuant.setSelection(mEtQuant.getText().length());
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

                isAlterado = true;

                String vlQuant = mEtQuant.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlCobert = charSequence.toString().trim().replaceAll("[^\\d]", "");
                String vlDesc = mEtDesc.getText().toString().trim().replaceAll("[^\\d]", "");

                mTvTotal.setText(calculaValorBolo(vlQuant, vlCobert, vlDesc, mPrecoBolo));

                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                isUpdating = true;

                mEtCobertura.setText(Formatar.emCurrency(vlCobert));
                mEtCobertura.setSelection(mEtCobertura.getText().length());
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

                isAlterado = true;

                String vlQuant = mEtQuant.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlCobert = mEtCobertura.getText().toString().trim().replaceAll("[^\\d]", "");
                String vlDesc = charSequence.toString().trim().replaceAll("[^\\d]", "");

                mTvTotal.setText(calculaValorBolo(vlQuant, vlCobert, vlDesc, mPrecoBolo));


                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                isUpdating = true;

                mEtDesc.setText(Formatar.emCurrency(vlDesc));
                mEtDesc.setSelection(mEtDesc.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSwitchCobertura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {

                    layoutCobert.setVisibility(View.VISIBLE);

                    if (mAdicionar) {
                        mEtCobertura.setText("0");
                    }

                    mEtCobertura.requestFocus();

                } else {

                    layoutCobert.setVisibility(View.GONE);
                    Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchCobertura);
                    mEtCobertura.setText("0");
                }
            }
        });

        mSwitchDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {

                    layoutDesc.setVisibility(View.VISIBLE);

                    if (mAdicionar) {
                        mEtDesc.setText("0");
                    }

                    mEtDesc.requestFocus();

                } else {

                    layoutDesc.setVisibility(View.GONE);
                    Utilidades.fecharTecladoSwitch(VendQuantActivity.this, mSwitchDesc);
                    mEtDesc.setText("0");
                }
            }
        });


        mEtQuant.setOnTouchListener(mTouchListnerEditCursorFim);
        mEtCobertura.setOnTouchListener(mTouchListnerEditCursorFim);
        mEtDesc.setOnTouchListener(mTouchListnerEditCursorFim);

        mEtQuant.setCursorVisible(false);
        mEtQuant.setSelectAllOnFocus(false);
        UserInterface.focoCursorSelect(mEtCobertura);
        UserInterface.focoCursorSelect(mEtDesc);
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
                if (!isAlterado) {

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
        boolean temCobert;
        boolean temDesc;
        boolean temPrazo;
        int quant;
        double valorCobert;
        double valorDesconto;
        double valorTotal;

        nome = mTvNome.getText().toString().trim();
        quantString = mEtQuant.getText().toString().trim();
        temCobert = mSwitchCobertura.isChecked();
        temDesc = mSwitchDesc.isChecked();
        temPrazo = mSwitchPrazo.isChecked();
        vlCobertString = mEtCobertura.getText().toString().trim();
        vlDescString = mEtDesc.getText().toString().trim();

        // Validações
        if (TextUtils.isEmpty(quantString)) {

            mEtQuant.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (temCobert) {

            if (TextUtils.isEmpty(vlCobertString)) {

                mEtCobertura.setError(getString(R.string.error_campo_vazio));
                return;
            }

            if (vlCobertString.equals("0")) {

                mEtCobertura.setError(getString(R.string.error_valor_maior_zero));
                return;
            }
        }

        if (temDesc) {

            if (TextUtils.isEmpty(vlDescString)) {

                mEtDesc.setError(getString(R.string.error_campo_vazio));
                return;
            }

            if (vlDescString.equals("0")) {

                mEtDesc.setError(getString(R.string.error_valor_maior_zero));
                return;
            }
        }

        if (TextUtils.isEmpty(quantString)) {

            quantString = "1";
        }

        if (!temCobert) {

            vlCobertString = "0";
        }

        if (!temDesc) {

            vlDescString = "0";
        }

        // Conversões
        quant = Integer.parseInt(quantString);
        valorCobert = Formatar.emDouble(vlCobertString);
        valorDesconto = Formatar.emDouble(vlDescString);
        valorTotal = Utilidades.calculaValorBoloDouble(mPrecoBolo, quant, valorCobert, valorDesconto);

        if (quant < 1) {
            mEtQuant.setError(getString(R.string.error_valor_menor_um));
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

        if (temPrazo) {

            values.put(AcessoVenda.COLUNA_VENDA_PRAZO, Constantes.PRAZO_SIM);

        } else {

            values.put(AcessoVenda.COLUNA_VENDA_PRAZO, Constantes.PRAZO_NAO);
        }

        if (mAdicionar) {

            Crud.inserir(VendQuantActivity.this, AcessoVenda.CONTENT_URI_VENDA, values);

        } else {

            Crud.editar(VendQuantActivity.this, mUriAtual, values);
        }

        finish();
    }

    @Override
    public void onBackPressed() {

        if (!isAlterado) {

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
                    AcessoVenda.COLUNA_VENDA_PRAZO,
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
            mData = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_DATA));

            int quantidade = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_QUANT));
            int temCobertura = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA));
            int temDesconto = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO));
            int temPrazo = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRAZO));

            double valorCobertura = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA));
            double valorDesconto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO));
            double valorBolo = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));
            mPrecoBolo = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRECO_UM_BOLO));

            mTvNome.setText(nomeBolo);
            mEtQuant.setText(String.valueOf(quantidade));

            if (temCobertura == Constantes.COBERTURA_SIM) {

                mSwitchCobertura.setChecked(true);
                layoutCobert.setVisibility(View.VISIBLE);
                mEtCobertura.setText(mPreco.format(valorCobertura));
            }

            if (temCobertura == Constantes.COBERTURA_NAO) {

                mSwitchCobertura.setChecked(false);
                layoutCobert.setVisibility(View.GONE);
            }

            if (temDesconto == Constantes.DESCONTO_SIM) {

                mSwitchDesc.setChecked(true);
                layoutDesc.setVisibility(View.VISIBLE);
                mEtDesc.setText(mPreco.format(valorDesconto));
            }

            if (temDesconto == Constantes.DESCONTO_NAO) {

                mSwitchDesc.setChecked(false);
                layoutDesc.setVisibility(View.GONE);
            }

            if (temPrazo == Constantes.PRAZO_SIM) {

                mSwitchPrazo.setChecked(true);
            }

            if (temPrazo == Constantes.PRAZO_NAO) {

                mSwitchPrazo.setChecked(false);
            }

            mTvTotal.setText(mPreco.format(valorBolo));
        }

        if (loader.getId() == LOADER_VENDA_ADICIONAR && cursor.moveToFirst()) {

            String mNomeDoBolo = cursor.getString(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_NOME));
            mPrecoBolo = cursor.getDouble(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_PRECO));

            mTvNome.setText(mNomeDoBolo);
            mTvTotal.setText(mPreco.format(mPrecoBolo));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
