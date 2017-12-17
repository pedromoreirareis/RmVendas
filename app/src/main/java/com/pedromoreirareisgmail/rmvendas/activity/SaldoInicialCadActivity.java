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
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;

public class SaldoInicialCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, EditText.OnTouchListener, EditText.OnEditorActionListener {

    private static final int LOADER_RET_CAD = 0;

    private EditText mEtValor;

    private String mDataHoraBD = null;
    private Uri mUriAtual = null;
    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_cad);

        // Recebe dados de SaldoInicialListActivity
        Intent intent = getIntent();
        mUriAtual = intent.getData();

        // Se mUriAtual tiver for nulo então vai adicionar, se tiver dados vai editar
        if (mUriAtual == null) {

            setTitle(R.string.title_saldo_inicial_cad_add);

        } else {

            setTitle(R.string.title_saldo_inicial_cad_edit);
            getLoaderManager().initLoader(LOADER_RET_CAD, null, this);
        }

        // Referencia intens do layout
        mEtValor = (EditText) findViewById(R.id.et_valor);

        /* Define o EditorAction do teclado, refrente a view mEtValor
         * Quando o teclado estiver aberto referente ao edit mEtValor, o EditorAction sera DONE
         */
        mEtValor.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Verifica a entrada de caractres em um edit especifico
        controleTextWatcher();

        // define qual ação sera tomada ao clicar no EditorAction
        mEtValor.setOnEditorActionListener(this);

        // Monitora se há toques em uma view especifica
        mEtValor.setOnTouchListener(this);

        // Retira o foco e coloca o valor zero no edit
        Utilidades.semFocoZerado(mEtValor);
    }

    /**
     * Cria o menu
     *
     * @param menu objeto do menu
     * @return infla o layout de menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    /**
     * Define o que fazer ao seleciona um item do menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Salva dados no BD
            case R.id.action_salvar:
                salvarDadosBD();
                return true;

            /* Menu Up
             * Se dados foram alterados abre Dialog para decidir se os dados alterados vao ser
              * descartados ou se vão ser mantidos e a alteração continuara
             */
            case android.R.id.home:

                if (!isDadosAlterado) {

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

    /* Salva dados no BD
     * Recebe dados dos edits, faz validações, coloca dados no objeto values e salva no BD
     */
    private void salvarDadosBD() {

        String valorEditText = mEtValor.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorEditText);

        // Campo não pode ficar vazio
        if (TextUtils.isEmpty(valorEditText)) {
            mEtValor.setError(getString(R.string.error_campo_vazio));
            mEtValor.requestFocus();
            return;
        }

        // Valor não pode ser negativo
        if (valorDouble <= NUMERO_ZERO) {
            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            mEtValor.requestFocus();
            return;
        }

        // Coloca dados no objeto values
        ContentValues values = new ContentValues();
        values.put(AcessoSaldo.VALOR, valorDouble);

        // Salva dados no BD
        if (mUriAtual == null) {

            values.put(AcessoSaldo.DATA, obterDataHoraSistema());

            Crud.inserir(SaldoInicialCadActivity.this, AcessoSaldo.CONTENT_URI_SALDO_INICIAL, values);

        } else {

            values.put(AcessoSaldo.DATA, mDataHoraBD);

            Crud.editar(SaldoInicialCadActivity.this, mUriAtual, values);
        }

        finish();
    }

    /* Botão voltar (embaixo)
     * Verifica se houve alterações, se houve abre Dialog para decidir se as alterações vao ser
     * descartadas ou se continuara a fazendo alterações
     */
    @Override
    public void onBackPressed() {

        if (!isDadosAlterado) {

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

    /**
     * Define parametros de pesquisa no BD
     *
     * @param i      Loader resposanvel pela pesquisa
     * @param bundle argumentos dentro do loader de pesquisa
     * @return cursor com dados retornados da pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoSaldo._ID,
                AcessoSaldo.DATA,
                AcessoSaldo.VALOR
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
     * Define o que sera feito com os dados retornados da pesquisa
     *
     * @param loader Loader que foi resposanvel pela pesquisa
     * @param cursor dados retornados da pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            double valorBD = cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.VALOR));
            mDataHoraBD = cursor.getString(cursor.getColumnIndex(AcessoSaldo.DATA));

            mEtValor.setText(String.valueOf(valorBD * 100));
        }
    }

    /**
     * Define o que sera feito com dados antigos da pesquisa quando foi iniciado nova pesquisa
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nao implementado
    }

    /**
     * Monitora toque em uma view especifica
     *
     * @param view  view que sera  monitorada
     * @param event evento
     * @return verdadeiro se houve toque na view monitorada
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        switch (id) {

            case R.id.et_valor:
                mEtValor.requestFocus();
                mEtValor.setSelection(mEtValor.getText().length());
                Utilidades.mostrarTeclado(SaldoInicialCadActivity.this, mEtValor);
                return true;

            default:
                return false;
        }
    }

    /**
     * Verifica se EditorAction do teclado foi clicado
     *
     * @param view     viu que abriu o teclado
     * @param actionId id do EditorAction
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

    /* Verifica a entrada de caracteres nos edits*/
    private void controleTextWatcher() {

        /* O teclado por esse edit possui apenas numeros, faz a captura desses caracteres e formata
         * para o estilo moeda (currency) para ser apresentado ao usuario
         */
        mEtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                isDadosAlterado = true;

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
