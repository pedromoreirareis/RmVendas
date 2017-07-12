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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoVenda;

public class VendQuantActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_VENDA_EDITAR = 11;
    private static final int LOADER_VENDA_ADICIONAR = 12;

    private TextView mTvNome;
    private EditText mEtQuant;
    private Switch mSwitchCobertura;
    private EditText mEtCobertura;
    private Switch mSwitchDesc;
    private EditText mEtDesc;
    private TextView mTvTotal;

    private Uri mUriAtual = null;

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
    private boolean mAdicionar = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vend_quant);

        Intent intent = getIntent();
        mUriAtual = intent.getData();


        if (intent.hasExtra(Constantes.VENDA_ADICIONAR)) {
            setTitle(R.string.tela_venda_adicionar);
            mAdicionar = true;
            getLoaderManager().initLoader(LOADER_VENDA_ADICIONAR, null, this);
        }

        if (intent.hasExtra(Constantes.VENDA_EDITAR)) {
            setTitle(R.string.tela_vend_editar);
            mAdicionar = false;
            getLoaderManager().initLoader(LOADER_VENDA_EDITAR, null, this);
        }

        mTvNome = (TextView) findViewById(R.id.tv_nome_vend_quant);
        mEtQuant = (EditText) findViewById(R.id.et_quant_vend_quant);
        mSwitchCobertura = (Switch) findViewById(R.id.switch_cobertura_vend_quant);
        mEtCobertura = (EditText) findViewById(R.id.et_valor_cobertura_vend_quant);
        mSwitchDesc = (Switch) findViewById(R.id.switch_desconto_vend_quant);
        mEtDesc = (EditText) findViewById(R.id.et_valor_desconto_vend_quant);
        mTvTotal = (TextView) findViewById(R.id.tv_valor_total_vend_quant);

        mEtQuant.setOnTouchListener(mTouchListenetEdit);
        mEtCobertura.setOnTouchListener(mTouchListenetEdit);
        mEtDesc.setOnTouchListener(mTouchListenetEdit);
        mSwitchCobertura.setOnTouchListener(mTouchListenerSwitch);
        mSwitchDesc.setOnTouchListener(mTouchListenerSwitch);
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
                    /* Não teve alteração */
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                /* Teve alteração */
                DialogInterface.OnClickListener descartarButClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(VendQuantActivity.this);
                            }
                        };

                UtilsDialog.confirmarAlteracao(
                        VendQuantActivity.this,
                        getString(R.string.dialog_prod_cad_alt_titulo),
                        getString(R.string.dialog_prod_cad_alt_continuar),
                        getString(R.string.dialog_prod_cad_alt_descatar),
                        descartarButClickListener
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void adicionar() {


        String nome = "";
        String quantString = "0";
        String valorCobertString = "0";
        String valorDescString = "0";
        String valorTotalString = "0";
        boolean temCobert = false;
        boolean temDesc = false;
        int quant = 0;
        double valorCobert = 0;
        double valorDesconto = 0;
        double valorTotal = 0;


        nome = mTvNome.getText().toString().trim();
        quantString = mEtQuant.getText().toString().trim();
        temCobert = mSwitchCobertura.isChecked();
        temDesc = mSwitchDesc.isChecked();
        valorTotalString = mTvTotal.getText().toString().trim();

        if (temCobert) {
            valorCobertString = mEtCobertura.getText().toString().trim();
        }

        if (temDesc) {
            valorDescString = mEtDesc.getText().toString().trim();
        }


        // Conversões

        quant = Integer.parseInt(quantString);
        valorCobert = Double.parseDouble(valorCobertString);
        valorDesconto = Double.parseDouble(valorDescString);
        valorTotal = Double.parseDouble(valorTotalString);


        // Validações
        if (TextUtils.isEmpty(quantString)) {
            mEtQuant.setError(getString(R.string.error_venda_cad_quant_digitar));
            return;
        }

        if (temCobert) {
            mEtCobertura.setError(getString(R.string.error_venda_cad_cobert_digitar));
            return;
        }
        if (temDesc) {
            mEtDesc.setError(getString(R.string.error_venda_cad_desc_digitar));
            return;
        }

        if (quant < 0) {
            mEtQuant.setError(getString(R.string.error_venda_cad_quant_negativo));
            return;
        }

        if (valorCobert < 0) {
            mEtCobertura.setError(getString(R.string.error_venda_cad_cobert_negativo));
            return;
        }

        if (valorDesconto < 0) {
            mEtDesc.setError(getString(R.string.error_venda_cad_desc_negativo));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoVenda.COLUNA_VENDA_NOME_PROD, nome);
        values.put(AcessoVenda.COLUNA_VENDA_QUANT, quant);
        values.put(AcessoVenda.COLUNA_VENDA_VALOR_PROD, valorTotal);
        if (temCobert) {
            values.put(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA, Constantes.COBERTURA_SIM);

        }
        if (!temCobert) {
            values.put(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA, Constantes.COBERTURA_NAO);
        }
        values.put(AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA, valorCobert);

        if (temDesc) {
            values.put(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO, Constantes.DESCONTO_SIM);
        }
        if (!temDesc) {
            values.put(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO, Constantes.DESCONTO_NAO);
        }

        values.put(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO, valorDesconto);

        if (mAdicionar) {

            Uri newUri = getContentResolver().insert(AcessoVenda.CONTENT_URI_VENDA, values);

            if (newUri != null) {
                Toast.makeText(this, "Inserido com sucesso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro o inserir", Toast.LENGTH_SHORT).show();
            }

        } else {
            int linhasAtualizadas = getContentResolver().update(mUriAtual, values, null, null);

            if (linhasAtualizadas > 0) {
                Toast.makeText(this, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
/*
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
*/
        if (i == LOADER_VENDA_EDITAR) {

            String[] projection = {
                    AcessoVenda._ID,
                    AcessoVenda.COLUNA_VENDA_NOME_PROD,
                    AcessoVenda.COLUNA_VENDA_VALOR_PROD,
                    AcessoVenda.COLUNA_VENDA_TEM_COBERTURA,
                    AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA,
                    AcessoVenda.COLUNA_VENDA_TEM_DESCONTO,
                    AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO,
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

        if (loader.getId() == LOADER_VENDA_EDITAR) {

            String nomeBolo = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_NOME_PROD));
            int quantidade = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_QUANT));
            int temCobertura = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA));
            double valorCobertura = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA));
            int temDesconto = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO));
            double valorDesconto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO));
            double valorBolo = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
