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
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.Crud;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoProdutos;

public class ProdutosCadActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_PROD_CAD = 6;
    private static final int MAX_CARACT_NOME = 50;

    private EditText mEtNome;
    private EditText mEtPreco;

    private Uri mUriAtual = null;
    private boolean mAlteracao = false;

    private final EditText.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mAlteracao = true;
            return false;
        }
    };

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

        mEtNome = (EditText) findViewById(R.id.et_prod_nome);
        mEtPreco = (EditText) findViewById(R.id.et_prod_preco);

        mEtNome.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CARACT_NOME)});
        mEtNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 48) {

                    Toast.makeText(ProdutosCadActivity.this,
                            R.string.msg_max_caract, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtPreco.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    adicionar();
                    return true;
                }

                return false;
            }
        });

        mEtNome.setOnTouchListener(mTouchListener);
        mEtPreco.setOnTouchListener(mTouchListener);
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
                                NavUtils.navigateUpFromSameTask(ProdutosCadActivity.this);
                            }
                        };

                UtilsDialog.confirmarAlteracao(
                        ProdutosCadActivity.this,
                        descartarButClickListener
                );

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void adicionar() {

        String nome = mEtNome.getText().toString().trim();
        String preco = mEtPreco.getText().toString().trim();

        /* Validações */

        if (TextUtils.isEmpty(nome)) {

            mEtNome.setError(getString(R.string.error_campo_vazio));
            return;
        }

        if (nome.length() < 10) {

            mEtNome.setError(getString(R.string.error_campo_lenght_10));
            return;
        }

        if (TextUtils.isEmpty(preco)) {

            mEtPreco.setError(getString(R.string.error_campo_vazio));
            return;
        }

        double precoDouble = Double.parseDouble(preco);
        if (precoDouble <= 0) {

            mEtPreco.setError(getString(R.string.error_valor_maior_zero));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoProdutos.COLUNA_PRODUTO_NOME, nome);
        values.put(AcessoProdutos.COLUNA_PRODUTO_PRECO, precoDouble);

        if (mUriAtual == null) {

            Crud.inserir(ProdutosCadActivity.this, AcessoProdutos.CONTENT_URI_PRODUTO, values);

        } else {

            Crud.editar(ProdutosCadActivity.this, mUriAtual, values);

        }

        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoProdutos._ID,
                AcessoProdutos.COLUNA_PRODUTO_NOME,
                AcessoProdutos.COLUNA_PRODUTO_PRECO
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

            String nome = cursor.getString(
                    cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_NOME));

            String preco = cursor.getString(
                    cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_PRECO));

            mEtNome.setText(nome);
            mEtPreco.setText(preco);
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
                ProdutosCadActivity.this,
                descartarButClickListener
        );
    }
}
