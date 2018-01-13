package com.pedromoreirareisgmail.rmvendas.activity;

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
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_CARACT_10;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;

public class EntCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener {

    private static final String TAG = EntCadActivity.class.getSimpleName();
    private static final int LOADER_ENT_CAD = 0;

    private EditText mEtValor;
    private EditText mEtDescricao;

    private Uri mUriAtual = null;
    private String mDataHoraBD = null;

    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ent_cad);

        Log.v(TAG, "onCreate");

        initViews();

        initIntents();

        // Se tiver Uri então deve pesquisar essa uri para editar registro
        if (mUriAtual == null) {

            setTitle(R.string.title_entrada_cad_add);

        } else {

            setTitle(R.string.title_entrada_cad_edit);
            getLoaderManager().initLoader(LOADER_ENT_CAD, null, this);
        }

        // Faz controle de entrada de dados no edit
        controleTextWatcher();

        // Verifica se houve alteração no edit descrição
        if (!isDadosAlterado) {

            isDadosAlterado = Utilidades.verificarAlteracaoDados(mEtDescricao);
        }

        // Define o que ação tomar a clicar no botão EditorAction do teclado
        mEtDescricao.setOnEditorActionListener(this);

        // Monitora toques no edit de valor
        mEtValor.setOnTouchListener(this);

        // Retira o foco e coloca o valor zero
        Utilidades.semFocoZerado(mEtValor);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mEtValor = findViewById(R.id.et_valor);
        mEtDescricao = findViewById(R.id.et_descricao);
    }

    private void initIntents() {

        Log.v(TAG, "initIntents");

        /* Recebe dados de EntListActivity para mUriAtual
         * Tem dados - Editar / Não tem dados - Adicionar
         */
        Intent intentUri = getIntent();
        mUriAtual = intentUri.getData();
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

            // Botao salvar
            case R.id.action_salvar:
                salvarDadosBD();
                return true;

            /* Botão Up - Verifica se algum dado foi alterado, ou houve tentativa de alteração
             * Em caso afirmativo abre Dialog para escolha se deseja deacatar alteração ou se deseja
             * continuar alterado
             */
            case android.R.id.home:

                // Não foi alterado - Volta a Activity que chamou EntCadActivity
                if (!isDadosAlterado) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Dialogos.homeDescartarConfirmar(
                        EntCadActivity.this,
                        EntCadActivity.this);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Botao voltar (embaixo) - Verifica se houve alteração
     * Se houve - Abre dialog para confirmar se deseja descartar alterações ou não
     */
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        Dialogos.onBackPressedDescartarConfirmar(
                EntCadActivity.this,
                EntCadActivity.this);
    }

    /* Recebe dados do Edits, faz validações, verifica se e um novo registro ou se é uma alteração
     * e salva os dados no BD
     */
    private void salvarDadosBD() {

        Log.v(TAG, "Iniciando salvar BD");

        String valorEditText = mEtValor.getText().toString().trim();
        String descricaoEditText = mEtDescricao.getText().toString().trim();

        // Converte String Currency para double
        double valorDouble = Formatar.formatarParaDouble(valorEditText);

        // O campo descrição não pode ficar vazio
        if (TextUtils.isEmpty(descricaoEditText)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio_descricao));
            mEtDescricao.requestFocus();
            return;
        }

        // A descrição deve ter pelo menos 10 caracteres
        if (descricaoEditText.length() < MIN_CARACT_10) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_descricao_10));
            mEtDescricao.requestFocus();
            return;
        }

        // Valor não pode ser negativo
        if (valorDouble == NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_valido));
            mEtValor.requestFocus();
            return;
        }

        // coloca dados no Objeto values para salvar no BD
        ContentValues values = new ContentValues();
        values.put(AcessoEntRet.VALOR, valorDouble);
        values.put(AcessoEntRet.DESCRICAO, descricaoEditText);
        values.put(AcessoEntRet.TIPO, Constantes.TIPO_ENTRADA);

        if (mUriAtual == null) {

            values.put(AcessoEntRet.DATA_HORA, obterDataHoraSistema());

            Crud.inserir(EntCadActivity.this, AcessoEntRet.CONTENT_URI_ENT_RET, values);

            Log.v(TAG, "Adicionar - adicionou cliente");

        } else {

            values.put(AcessoEntRet.DATA_HORA, mDataHoraBD);

            Crud.editar(EntCadActivity.this, mUriAtual, values);

            Log.v(TAG, "Editando - editou cliente");
        }

        Log.v(TAG, "Finalizando salvar BD");

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        // Retorna todos os dados do registro identificado pelo mUriAtual
        String[] projection = {
                AcessoEntRet._ID,
                AcessoEntRet.DATA_HORA,
                AcessoEntRet.DESCRICAO,
                AcessoEntRet.TIPO,
                AcessoEntRet.VALOR
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
                    cursor.getColumnIndex(AcessoEntRet.VALOR));

            String descricaoBD = cursor.getString(
                    cursor.getColumnIndex(AcessoEntRet.DESCRICAO));

            mDataHoraBD = cursor.getString(
                    cursor.getColumnIndex(AcessoEntRet.DATA_HORA));

            mEtValor.setText(String.valueOf(valorBD * 100));
            mEtDescricao.setText(descricaoBD);
        }
    }

    /**
     * O que fazer com os dados no caso de reiniciar pesquisa
     *
     * @param loader Cursor com dados antigos
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");

        int id = view.getId();

        switch (id) {

            // Recebe o foco e coloca o cursor no fim, se teclado tiver fechado abre ele
            case R.id.et_valor:
                mEtValor.requestFocus();
                mEtValor.setSelection(mEtValor.getText().length());
                Utilidades.mostrarTeclado(EntCadActivity.this, mEtValor);
                return true;

            default:
                return false;
        }
    }

    /* Faz o controle da entrada de dados (caracteres) nos edits*/
    private void controleTextWatcher() {

        /* Os caracteres que entram no mEtValor são apenas numeros
         * Na entrada de caracteres envia para fazer uma formatação, para que os caracteres seja
         * apresentado ao usuario em forma de moeda(currency). tambem controla o cursor para que ele
         * sempre esteja no fim e não seja possivel apagar um caracetres do centro de um conjunto de
         * caracteres sem antes apagar todos a sua direita
         */
        mEtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.v(TAG, "controleTextWatcher - mEtValor");

                if (!isDadosAlterado) {

                    isDadosAlterado = true;
                }

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
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        Log.v(TAG, "onEditorAction");

        // Salvar dados no banco de dados
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            salvarDadosBD();
            return true;
        }

        return false;
    }

}
