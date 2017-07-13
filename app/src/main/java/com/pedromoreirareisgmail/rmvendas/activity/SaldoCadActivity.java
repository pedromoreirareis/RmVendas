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
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoSaldo;

import static com.pedromoreirareisgmail.rmvendas.Utils.Datas.getDateTime;

public class SaldoCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_RET_CAD = 8;
    private static final int MAX_CARACT_DESC = 50;
    private EditText mEtValor;

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
        setContentView(R.layout.activity_saldo_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (mUriAtual == null) {
            setTitle(R.string.tela_saldo_cad_adicionar);
        } else {
            setTitle(R.string.tela_saldo_cad_editar);
            getLoaderManager().initLoader(LOADER_RET_CAD, null, this);
        }

        mEtValor = (EditText) findViewById(R.id.et_valor_saldo);

        mEtValor.setOnTouchListener(mTouchListenet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salvar, menu);
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
                                NavUtils.navigateUpFromSameTask(SaldoCadActivity.this);
                            }
                        };

                UtilsDialog.confirmarAlteracao(
                        SaldoCadActivity.this,
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

        ContentValues values = new ContentValues();

        values.put(AcessoSaldo.COLUNA_SALDO_VALOR, valorDouble);

        if (mUriAtual == null) {
            values.put(AcessoSaldo.COLUNA_SALDO_DATA, getDateTime());
        } else {
            values.put(AcessoSaldo.COLUNA_SALDO_DATA, mData);
        }


        if(mUriAtual == null){
            Uri newUri = getContentResolver().insert(AcessoSaldo.CONTENT_URI_SALDO, values);

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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AcessoSaldo._ID,
                AcessoSaldo.COLUNA_SALDO_DATA,
                AcessoSaldo.COLUNA_SALDO_VALOR
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
            double valorDouble = cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_VALOR));
            mData = cursor.getString(cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_DATA));

            String valor = String.valueOf(valorDouble);

            mEtValor.setText(valor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
                SaldoCadActivity.this,
                getString(R.string.dialog_prod_cad_alt_titulo),
                getString(R.string.dialog_prod_cad_alt_continuar),
                getString(R.string.dialog_prod_cad_alt_descatar),
                descartarButClickListener
        );
    }

}
