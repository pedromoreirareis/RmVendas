package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.constantes.Const;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.constantes.Const.NUMERO_ZERO;

public class ProdutosCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnEditorActionListener,
        EditText.OnTouchListener {

    private static final String TAG = ProdutosCadActivity.class.getSimpleName();
    private static final int LOADER_PROD_CAD = 0;

    private EditText mEtNome;
    private EditText mEtPreco;

    private Uri mUriAtual = null;
    private boolean isDadosAlterados = false;
    private boolean isFormatarCurrencyAtualizado = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_cad);

        Log.v(TAG, "onCreate");

        initViews();
        initIntents();

        // Se tiver dados em mUriAtual, então vai editar, senão vai adicionar
        if (mUriAtual == null) {

            setTitle(R.string.title_produto_cad_add);

        } else {

            setTitle(R.string.title_produto_cad_edit);
            getLoaderManager().initLoader(LOADER_PROD_CAD, null, this);
        }

        // Controle entrada de caracteres nos edits
        controleTextWatcher();

        // Verifica se o texto do edit nome foi alterado
        if (!isDadosAlterados) {

            isDadosAlterados = Utilidades.verificarAlteracaoDados(mEtNome);
        }

        // Monitora o EditorAction do teclado
        mEtPreco.setOnEditorActionListener(this);

        // Monitora toques em uma view especifica
        mEtPreco.setOnTouchListener(this);

        // Retira o foco do edit e coloca o valor zero nele
        Utilidades.semFocoZerado(mEtPreco);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mEtNome = findViewById(R.id.et_nome);
        mEtPreco = findViewById(R.id.et_preco);
    }

    private void initIntents() {

        Log.v(TAG, "initIntents");

        // Recebe dados de Uri de ProdutosListActivity
        Intent intent = getIntent();
        mUriAtual = intent.getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {

            // Menu Salvar
            case R.id.action_salvar:
                salvarDadosBD();
                return true;

            /* Menu Up - Verifica de houve alteração, se houve abre Dialog para confirma se vai
             * descartar a alteração ou se vai continuar alteradnto
             */
            case android.R.id.home:

                // Não houve alteracao
                if (!isDadosAlterados) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Dialogos.homeDescartarConfirmar(
                        ProdutosCadActivity.this,
                        ProdutosCadActivity.this);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Botão voltar (embaixo)
     * Verifica se houve alteração, se houve abre Dialog para escolher se deseja descatar dados
     * alterados ou se deseja continua alterando, se não houve volta
     */
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        if (!isDadosAlterados) {

            super.onBackPressed();
        }

        Dialogos.onBackPressedDescartarConfirmar(
                ProdutosCadActivity.this,
                ProdutosCadActivity.this);
    }

    /* Recebe dados dos edits, faz validações, coloca no obejtos values e salva os dados no BD */
    private void salvarDadosBD() {

        Log.v(TAG, "salvarDadosBD - Inicio");

        String nomeEditText = mEtNome.getText().toString().trim();
        String precoEditText = mEtPreco.getText().toString().trim();

        double precoDouble = Formatar.formatarParaDouble(precoEditText);

        // Campo não pode ficar vazio
        if (TextUtils.isEmpty(nomeEditText)) {

            mEtNome.setError(getString(R.string.error_campo_vazio_nome));
            mEtNome.requestFocus();
            return;
        }

        // determina quantidade minima de caracteres no campo de nome
        if (nomeEditText.length() < Const.MIN_CARACT_5) {

            mEtNome.setError(getString(R.string.error_campo_lenght_nome_5));
            mEtNome.requestFocus();
            return;
        }

        // Valor não pode ser negativo
        if (precoDouble == NUMERO_ZERO) {

            mEtPreco.setError(getString(R.string.error_valor_valido));
            mEtPreco.requestFocus();
            return;
        }

        // Coloca dados no Objeto values para salvar no BD
        ContentValues values = new ContentValues();
        values.put(EntryProduct.COLUMN_NAME, nomeEditText);
        values.put(EntryProduct.COLUMN_PRICE, precoDouble);

        // Salva dados no BD
        if (mUriAtual == null) {

            Log.v(TAG, "salvarDadosBD - inserir");

            Crud.inserir(ProdutosCadActivity.this, EntryProduct.CONTENT_URI_PRODUCT, values);

        } else {

            Log.v(TAG, "salvarDadosBD - editar");

            Crud.editar(ProdutosCadActivity.this, mUriAtual, values);
        }

        Log.v(TAG, "salvarDadosBD - Fim");

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        // Pesquisa especifica pelo mUriAtual por um unico produto cadastrado
        String[] projection = {
                EntryProduct._ID,
                EntryProduct.COLUMN_NAME,
                EntryProduct.COLUMN_PRICE
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

        Log.v(TAG, "onLoadFinished");

        if (cursor.moveToFirst()) {

            double valorBD = cursor.getDouble(
                    cursor.getColumnIndex(EntryProduct.COLUMN_PRICE));

            String nomeBD = cursor.getString(
                    cursor.getColumnIndex(EntryProduct.COLUMN_NAME));

            mEtPreco.setText(String.valueOf(valorBD * 100));
            mEtNome.setText(nomeBD);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

        Log.v(TAG, "onEditorAction");

        // Salva dados no banco de dados
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            salvarDadosBD();
            return true;
        }

        return false;
    }

    /* Verifica entrada de caracteres nos edits e formata os caracteres se necessario */
    private void controleTextWatcher() {

        Log.v(TAG, "controleTextWatcher");

        /* No edit mEtValor e permitido a entrada apenas de numeros
         * Captura a entrada dos caracteres de numero no edit e faz a formatação para moeda
         * (currency) para ser apresentado para o usuario
         */
        mEtPreco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!isDadosAlterados) {

                    isDadosAlterados = true;
                }

                if (isFormatarCurrencyAtualizado) {

                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtPreco.setText(Formatar.formatarParaCurrency(charSequence.toString().trim()));
                mEtPreco.setSelection(mEtPreco.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");

        int id = view.getId();

        switch (id) {

            // Recebe o foco, coloca cursor a direita dos caracteres e abre teclado se tiver fechado
            case R.id.et_preco:
                mEtPreco.requestFocus();
                mEtPreco.setSelection(mEtPreco.getText().length());
                Utilidades.mostrarTeclado(ProdutosCadActivity.this, mEtPreco);
                return true;

            default:
                return false;
        }
    }
}
