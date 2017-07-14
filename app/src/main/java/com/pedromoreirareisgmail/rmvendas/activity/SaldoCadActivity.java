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

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.Crud;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoSaldo;

import static com.pedromoreirareisgmail.rmvendas.Utils.Datas.getDateTime;

public class SaldoCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_RET_CAD = 10;
    private EditText mEtValor;

    private Uri mUriAtual = null;
    private String mData = "";
    private boolean mAlteracao = false;

    private final EditText.OnTouchListener mTouchListenet = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mAlteracao = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (mUriAtual == null) {

            setTitle(R.string.title_saldo_cad_add);

        } else {

            setTitle(R.string.title_saldo_cad_edit);
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
                                NavUtils.navigateUpFromSameTask(SaldoCadActivity.this);
                            }
                        };

                UtilsDialog.confirmarAlteracao(
                        SaldoCadActivity.this,
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
            mEtValor.setError(getString(R.string.error_campo_vazio));
            return;
        }

        double valorDouble = Double.parseDouble(valor);
        if (valorDouble <= 0) {
            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoSaldo.COLUNA_SALDO_VALOR, valorDouble);

        if (mUriAtual == null) {

            values.put(AcessoSaldo.COLUNA_SALDO_DATA, getDateTime());

        } else {

            values.put(AcessoSaldo.COLUNA_SALDO_DATA, mData);
        }


        if (mUriAtual == null) {

            Crud.inserir(SaldoCadActivity.this, AcessoSaldo.CONTENT_URI_SALDO, values);

        } else {

            Crud.editar(SaldoCadActivity.this, mUriAtual, values);

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
                descartarButClickListener
        );
    }

}
