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

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.UserInterface;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.data.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MAX_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_QUANT_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.DatasHoras.getDataHoraSistema;

public class RetCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_RET_CAD = 8;

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
    private EditText mEtDescricao;
    private String mData = "";
    private Uri mUriAtual = null;
    private boolean isAlterado = false;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (mUriAtual == null) {

            setTitle(R.string.title_ret_cad_add);

        } else {

            setTitle(R.string.title_ret_cad_edit);
            getLoaderManager().initLoader(LOADER_RET_CAD, null, this);
        }

        mEtValor = (EditText) findViewById(R.id.et_valor);
        mEtDescricao = (EditText) findViewById(R.id.et_descricao);

        mEtDescricao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CARACT)});

        mEtDescricao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isAlterado = true;

                if (charSequence.toString().trim().length() > 48) {

                    Toast.makeText(RetCadActivity.this,
                            R.string.msg_max_caract, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

        mEtValor.setOnTouchListener(mTouchListnerEditCursorFim);

        UserInterface.focoCursorSelect(mEtValor);
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
                                NavUtils.navigateUpFromSameTask(RetCadActivity.this);
                            }
                        };

                UtilsDialog.confirmarAlteracao(
                        RetCadActivity.this,
                        descartarButClickListener
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void adicionar() {

        String valorStr = mEtValor.getText().toString().trim();
        String descricao = mEtDescricao.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorStr);

        if (TextUtils.isEmpty(valorStr)) {

            mEtValor.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (TextUtils.isEmpty(descricao)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (valorDouble <= NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            return;
        }

        if (descricao.length() < MIN_QUANT_CARACT) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_10));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoEntRet.COLUNA_ENT_RET_VALOR, valorDouble);
        values.put(AcessoEntRet.COLUNA_ENT_RET_DESC, descricao);
        values.put(AcessoEntRet.COLUNA_ENT_RET_TIPO, Constantes.TIPO_RETIRADA);

        if (mUriAtual == null) {

            values.put(AcessoEntRet.COLUNA_ENT_RET_DATA, getDataHoraSistema());

        } else {

            values.put(AcessoEntRet.COLUNA_ENT_RET_DATA, mData);
        }

        if (mUriAtual == null) {

            Crud.inserir(RetCadActivity.this, AcessoEntRet.CONTENT_URI_ENT_RET, values);

        } else {

            Crud.editar(RetCadActivity.this, mUriAtual, values);
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
                RetCadActivity.this,
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

            double valorDouble = cursor.getDouble(
                    cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));

            String desc = cursor.getString(
                    cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DESC));

            mData = cursor.getString(
                    cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DATA));

            mEtValor.setText(String.valueOf(valorDouble * 100));
            mEtDescricao.setText(desc);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
