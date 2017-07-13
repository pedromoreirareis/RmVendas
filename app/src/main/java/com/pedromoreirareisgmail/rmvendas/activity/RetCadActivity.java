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
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;

import static com.pedromoreirareisgmail.rmvendas.Utils.Datas.getDateTime;

public class RetCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_RET_CAD = 7;
    private static final int MAX_CARACT_DESC = 50;
    private EditText mEtValor;
    private EditText mEtDescricao;

    private String mData = "";
    private boolean mAlteracao = false;
    private final EditText.OnTouchListener mTouchListenet = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mAlteracao = true;
            return false;
        }
    };
    private Uri mUriAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (mUriAtual == null) {
            setTitle(R.string.tela_ret_cad_adicionar);
        } else {
            setTitle(R.string.tela_ret_cad_editar);
            getLoaderManager().initLoader(LOADER_RET_CAD,null,this);
        }

        mEtValor = (EditText) findViewById(R.id.et_valor_ret);
        mEtDescricao = (EditText) findViewById(R.id.et_descricao_ret);

        mEtDescricao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CARACT_DESC)});

        mEtDescricao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 48) {

                    Toast.makeText(RetCadActivity.this,
                            R.string.toast_saldo_ent_ret_max_caract, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtValor.setOnTouchListener(mTouchListenet);
        mEtDescricao.setOnTouchListener(mTouchListenet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salvar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
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
                                NavUtils.navigateUpFromSameTask(RetCadActivity.this);
                            }
                        };

                UtilsDialog.confirmarAlteracao(
                        RetCadActivity.this,
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

        String valor = mEtValor.getText().toString().trim();
        String descricao = mEtDescricao.getText().toString().trim();

        /* validações */
        if (TextUtils.isEmpty(valor)) {
            mEtValor.setError(getString(R.string.error_saldo_ent_ret_valor_digitar));
            return;
        }

        double valorDouble = Double.parseDouble(valor);
        if (valorDouble <= 0) {
            mEtValor.setError(getString(R.string.error_saldo_ent_ret_valor_positivo));
            return;
        }

        if (TextUtils.isEmpty(descricao)) {
            mEtDescricao.setError(getString(R.string.error_saldo_ent_ret_descr_digitar));
            return;
        }

        ContentValues values = new ContentValues();

        values.put(VendasContrato.AcessoEntRet.COLUNA_ENT_RET_VALOR, valorDouble);
        values.put(AcessoEntRet.COLUNA_ENT_RET_DESC, descricao);
        values.put(AcessoEntRet.COLUNA_ENT_RET_TIPO, Constantes.TIPO_RETIRADA);

        if (mUriAtual == null) {
            values.put(AcessoEntRet.COLUNA_ENT_RET_DATA, getDateTime());
        } else {
            values.put(AcessoEntRet.COLUNA_ENT_RET_DATA, mData);
        }


        if(mUriAtual == null){
            Uri newUri = getContentResolver().insert(AcessoEntRet.CONTENT_URI_ENT_RET, values);

            if (newUri != null) {
                Toast.makeText(this, "Inserido com sucesso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao inserir", Toast.LENGTH_SHORT).show();
            }

        }else{
            int linhasAtualizadas = getContentResolver().update(mUriAtual, values,null,null);

            if (linhasAtualizadas > 0) {
                Toast.makeText(this, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        if (!mAlteracao) {
            /* Não houve alteração */
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
                RetCadActivity.this,
                getString(R.string.dialog_prod_cad_alt_titulo),
                getString(R.string.dialog_prod_cad_alt_continuar),
                getString(R.string.dialog_prod_cad_alt_descatar),
                descartarButClickListener
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoEntRet._ID,
                AcessoEntRet.COLUNA_ENT_RET_DATA,
                AcessoEntRet.COLUNA_ENT_RET_DESC,
                AcessoEntRet.COLUNA_ENT_RET_TIPO,
                AcessoEntRet.COLUNA_ENT_RET_VALOR
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            double valorDouble = cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
            String desc = cursor.getString(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DESC));
            mData = cursor.getString(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DATA));

            String valor = String.valueOf(valorDouble);

            mEtValor.setText(valor);
            mEtDescricao.setText(desc);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
