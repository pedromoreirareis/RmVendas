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

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_QUANT_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;

public class RetCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener {

    private static final int LOADER_RET_CAD = 0;

    private EditText mEtValor;
    private EditText mEtDescricao;

    private String mDataHoraBD = null;
    private Uri mUriAtual = null;

    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_cad);

        // Recebe dados da activity RetListActivity
        Intent intent = getIntent();
        mUriAtual = intent.getData();

        // Se mUriAtual tiver vazio, então vai adicionar
        if (mUriAtual == null) {

            setTitle(R.string.title_retirada_cad_add);

        } else {

            setTitle(R.string.title_retirada_cad_edit);
            getLoaderManager().initLoader(LOADER_RET_CAD, null, this);
        }

        // Referencia itens do layout
        mEtValor = findViewById(R.id.et_valor);
        mEtDescricao = findViewById(R.id.et_descricao);

        // Monitora caracteres digitas no edit
        controleTextWatcher();

        // Verifica se houve alteração do texto em mEtDescricao
        if (!isDadosAlterado) {

            isDadosAlterado = Utilidades.verificarAlteracaoDados(mEtDescricao);
        }

        // Verifica se foi clicado um EditorAction
        mEtDescricao.setOnEditorActionListener(this);

        // Monitora toques em uma view especifica
        mEtValor.setOnTouchListener(this);

        // Retira foco e coloca o valor zero no mEtValor
        Utilidades.semFocoZerado(mEtValor);
    }

    /**
     * Cria o item de menu
     *
     * @param menu Objeto de menu
     * @return infla o layout de menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    /**
     * Define o que fazer quando um item de menu foi selecionado
     *
     * @param item item do menu selecionado
     * @return verdadeiro se item de menu foi selecionado e acao foi efetuada com sucesso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Salva dados no BD
            case R.id.action_salvar:
                salvarDadosBD();
                return true;

            /* Menu Up -
             * Verifica se algum dado foi alterado, caso tenha sido alterado abre Dialog para decidir
             * se deseja descartar alterações ou se deseja continuar alterando
             */
            case android.R.id.home:
                if (!isDadosAlterado) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Dialogos.homeDescartarConfirmar(
                        RetCadActivity.this,
                        RetCadActivity.this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botão voltar (embaixo)
     * Se tiver ocorrido alteração abre Dialog para decidir se vai descartar a alteração ou se
     * vai continuar alterando dados
     */
    @Override
    public void onBackPressed() {

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        Dialogos.onBackPressedDescartarConfirmar(
                RetCadActivity.this,
                RetCadActivity.this);
    }

    /* Salva dados no BD
     * Recebe dados do edits, faz validações, coloca dados no objeto values e salva no BD
     */
    private void salvarDadosBD() {

        String valorEditText = mEtValor.getText().toString().trim();
        String descricaoEditText = mEtDescricao.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorEditText);

        // campo nao pode ficar vazio
        if (TextUtils.isEmpty(valorEditText)) {

            mEtValor.setError(getString(R.string.error_campo_vazio));
            mEtValor.requestFocus();
            return;
        }

        // Campo nao pode ficar vazio
        if (TextUtils.isEmpty(descricaoEditText)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio));
            mEtDescricao.requestFocus();
            return;
        }

        if (valorDouble <= NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            mEtValor.requestFocus();
            return;
        }

        // Quantidade minima de caracteres aceita nesse campo
        if (descricaoEditText.length() < MIN_QUANT_CARACT) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_10));
            mEtDescricao.requestFocus();
            return;
        }

        // Coloca dados no objeto values
        ContentValues values = new ContentValues();
        values.put(AcessoEntRet.VALOR, valorDouble);
        values.put(AcessoEntRet.DESCRICAO, descricaoEditText);
        values.put(AcessoEntRet.TIPO, Constantes.TIPO_RETIRADA_CAIXA);

        /* Salva dados no banco de dados
         * Se mUriAtual tiver vazio (null) vai adicionar registro
         * Se mUriAtual contiver o Uri de um registro, vai editar um registro
         */
        if (mUriAtual == null) {

            values.put(AcessoEntRet.DATA_HORA, obterDataHoraSistema());

            Crud.inserir(RetCadActivity.this, AcessoEntRet.CONTENT_URI_ENT_RET, values);

        } else {

            values.put(AcessoEntRet.DATA_HORA, mDataHoraBD);

            Crud.editar(RetCadActivity.this, mUriAtual, values);
        }

        finish();
    }

    /**
     * Define paramentros da pesquisa
     *
     * @param i      Define Loader que vai ser utilizado na pesquisa
     * @param bundle argumentos de pesquisa dentro do Loader
     * @return cursor com dados retornados na pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

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

    /**
     * Define o que vai ser feito com dados obtidos na pesquisa
     *
     * @param loader loader que foi utilizado na pesquisa
     * @param cursor dados retornados na pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

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
     * O que sera feito com dados antigos da pesquisa ao iniciar nova pesquisa
     *
     * @param loader loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Monitora toques em uma view
     *
     * @param view  view que vai ser monitorada
     * @param event evento
     * @return verdadeiro se houve toque na view
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        switch (id) {

            case R.id.et_valor:
                mEtValor.requestFocus();
                mEtValor.setSelection(mEtValor.getText().length());
                Utilidades.mostrarTeclado(RetCadActivity.this, mEtValor);
                return true;

            default:
                return false;
        }
    }

    /**
     * Verifica se houve toque em um EditorAction no teclado
     *
     * @param view     view que abriu o teclado
     * @param actionId id de um ActionId
     * @param event    evento
     * @return verdadeiro se EditorAction foi clicado
     */
    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {

            salvarDadosBD();
            return true;
        }

        return false;
    }

    /* Verifica a entrada de caracteres em um edit
     */
    private void controleTextWatcher() {

        /* Edit abre teclado numerico, entao ao entrar um caractere de numero ele é formatado
         *  no estilo de moeda para ser apresentado ao usuario
         */
        mEtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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
}
