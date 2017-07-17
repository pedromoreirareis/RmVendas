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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.Crud;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;

import static com.pedromoreirareisgmail.rmvendas.Utils.Datas.getDateTime;

public class EntCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MAX_CARACT_DESC = 50;
    private static final int LOADER_ENT_CAD = 0;

    private EditText mEtValor;
    private EditText mEtDescricao;

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
        setContentView(R.layout.activity_ent_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (mUriAtual == null) {

            setTitle(R.string.title_ent_cad_add);

        } else {

            setTitle(R.string.title_ent_cad_edit);
            getLoaderManager().initLoader(LOADER_ENT_CAD, null, this);
        }

        mEtValor = (EditText) findViewById(R.id.et_valor_ent);
        mEtDescricao = (EditText) findViewById(R.id.et_descricao_ent);

        mEtDescricao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CARACT_DESC)});

        mEtDescricao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().trim().length() > 48) {

                    Toast.makeText(EntCadActivity.this,
                            R.string.msg_max_caract, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mEtDescricao.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    adicionar();
                    return true;
                }

                return false;
            }
        });

        mEtValor.setOnTouchListener(mTouchListenet);
        mEtDescricao.setOnTouchListener(mTouchListenet);
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
                                NavUtils.navigateUpFromSameTask(EntCadActivity.this);
                            }
                        };

                UtilsDialog.confirmarAlteracao(
                        EntCadActivity.this,
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

            mEtValor.setError(getString(R.string.error_campo_vazio));
            return;
        }

        double valorDouble = Double.parseDouble(valor);
        if (valorDouble <= 0) {

            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            return;
        }

        if (TextUtils.isEmpty(descricao)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (descricao.length() < 10) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_10));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoEntRet.COLUNA_ENT_RET_VALOR, valorDouble);
        values.put(AcessoEntRet.COLUNA_ENT_RET_DESC, descricao);
        values.put(AcessoEntRet.COLUNA_ENT_RET_TIPO, Constantes.TIPO_ENTRADA);

        if (mUriAtual == null) {

            values.put(AcessoEntRet.COLUNA_ENT_RET_DATA, getDateTime());

        } else {

            values.put(AcessoEntRet.COLUNA_ENT_RET_DATA, mData);

        }

        if (mUriAtual == null) {

            Crud.inserir(EntCadActivity.this, AcessoEntRet.CONTENT_URI_ENT_RET, values);

        } else {

            Crud.editar(EntCadActivity.this, mUriAtual, values);

        }

        finish();
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
                EntCadActivity.this,
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
