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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.data.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;

public class SaldoInicialCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_RET_CAD = 10;

    private EditText mEtValor;
    private final EditText.OnTouchListener mTouchListnerEditCursorFim = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int id = view.getId();

            switch (id) {

                case R.id.et_valor:
                    mEtValor.requestFocus();
                    mEtValor.setSelection(mEtValor.getText().length());
                    return true;

                default:
                    return false;
            }
        }
    };
    private String mData = "";
    private Uri mUriAtual = null;
    private boolean isAlterado = false;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (mUriAtual == null) {

            setTitle(R.string.title_saldo_inicial_cad_add);

        } else {

            setTitle(R.string.title_saldo_inicial_cad_edit);
            getLoaderManager().initLoader(LOADER_RET_CAD, null, this);
        }

        mEtValor = (EditText) findViewById(R.id.et_valor);
        mEtValor.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mEtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isAlterado = true;

                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                isUpdating = true;

                mEtValor.setText(Formatar.formatarParaCurrency(charSequence.toString().trim()));
                mEtValor.setSelection(mEtValor.getText().length());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtValor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    adicionar();
                    return true;
                }

                return false;
            }
        });

        mEtValor.setOnTouchListener(mTouchListnerEditCursorFim);

        Utilidades.semCursorFocoSelecaoZerado(mEtValor);
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
                                NavUtils.navigateUpFromSameTask(SaldoInicialCadActivity.this);
                            }
                        };

                Dialogos.dialogoConfirmarAlteracao(
                        SaldoInicialCadActivity.this,
                        descartarButClickListener
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void adicionar() {

        String valorStr = mEtValor.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorStr);


        if (TextUtils.isEmpty(valorStr)) {
            mEtValor.setError(getString(R.string.error_campo_vazio));
            return;
        }


        if (valorDouble <= NUMERO_ZERO) {
            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoSaldo.COLUNA_SALDO_VALOR, valorDouble);

        if (mUriAtual == null) {

            values.put(AcessoSaldo.COLUNA_SALDO_DATA, obterDataHoraSistema());

        } else {

            values.put(AcessoSaldo.COLUNA_SALDO_DATA, mData);
        }


        if (mUriAtual == null) {

            Crud.inserir(SaldoInicialCadActivity.this, AcessoSaldo.CONTENT_URI_SALDO, values);

        } else {

            Crud.editar(SaldoInicialCadActivity.this, mUriAtual, values);

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

        Dialogos.dialogoConfirmarAlteracao(
                SaldoInicialCadActivity.this,
                descartarButClickListener
        );
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

            mEtValor.setText(String.valueOf(valorDouble * 100));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
