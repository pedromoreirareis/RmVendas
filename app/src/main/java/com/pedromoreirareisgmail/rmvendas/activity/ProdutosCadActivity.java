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
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoProdutos;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MAX_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MAX_CARACT_MSG;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_QUANT_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;

public class ProdutosCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_PROD_CAD = 0;

    private EditText mEtNome;
    private EditText mEtValor;
    private final EditText.OnTouchListener mTouchListnerEditFocoCursorFim = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int id = view.getId();

            switch (id) {

                case R.id.et_preco:
                    mEtValor.requestFocus();
                    mEtValor.setSelection(mEtValor.getText().length());
                    Utilidades.mostrarTeclado(ProdutosCadActivity.this, mEtValor);
                    return true;

                default:
                    return false;
            }
        }
    };
    private Uri mUriAtual = null;
    private boolean isDadosAlterados = false;
    private boolean isFormatarCurrencyAtualizado = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_cad);

        Intent intent = getIntent();
        mUriAtual = intent.getData();

        if (mUriAtual == null) {

            setTitle(R.string.title_prod_cad_add);

        } else {

            setTitle(R.string.title_prod_cad_edit);
            getLoaderManager().initLoader(LOADER_PROD_CAD, null, this);
        }

        mEtNome = (EditText) findViewById(R.id.et_nome);
        mEtValor = (EditText) findViewById(R.id.et_preco);

        mEtNome.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CARACT)});

        mEtNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isDadosAlterados = true;

                if (charSequence.toString().trim().length() > MAX_CARACT_MSG) {

                    Toast.makeText(ProdutosCadActivity.this,
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

                isDadosAlterados = true;

                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

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
                    salvarDadosBD();
                    return true;
                }

                return false;
            }
        });

        mEtValor.setOnTouchListener(mTouchListnerEditFocoCursorFim);

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
                salvarDadosBD();
                return true;

            case android.R.id.home:

                if (!isDadosAlterados) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener descartarButClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(ProdutosCadActivity.this);
                            }
                        };

                Dialogos.dialogoConfirmarAlteracao(
                        ProdutosCadActivity.this,
                        descartarButClickListener
                );

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void salvarDadosBD() {

        String nomeEditText = mEtNome.getText().toString().trim();
        String precoEditText = mEtValor.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(precoEditText);

        if (TextUtils.isEmpty(nomeEditText)) {

            mEtNome.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (TextUtils.isEmpty(precoEditText)) {

            mEtValor.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (valorDouble <= NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            return;
        }

        if (nomeEditText.length() < MIN_QUANT_CARACT) {

            mEtNome.setError(getString(R.string.error_campo_lenght_10));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoProdutos.COLUNA_PRODUTOS_NOME, nomeEditText);
        values.put(AcessoProdutos.COLUNA_PRODUTOS_VALOR, valorDouble);

        if (mUriAtual == null) {

            Crud.inserir(ProdutosCadActivity.this, AcessoProdutos.CONTENT_URI_PRODUTOS, values);

        } else {

            Crud.editar(ProdutosCadActivity.this, mUriAtual, values);

        }

        finish();
    }

    @Override
    public void onBackPressed() {

        if (!isDadosAlterados) {

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
                ProdutosCadActivity.this,
                descartarButClickListener
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoProdutos._ID,
                AcessoProdutos.COLUNA_PRODUTOS_NOME,
                AcessoProdutos.COLUNA_PRODUTOS_VALOR
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

            double valorBD = cursor.getDouble(
                    cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTOS_VALOR));

            String nomeBD = cursor.getString(
                    cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTOS_NOME));

            mEtValor.setText(String.valueOf(valorBD * 100));
            mEtNome.setText(nomeBD);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
