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
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoProdutos;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_QUANT_CARACT_10;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;

public class ProdutosCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnEditorActionListener,
        EditText.OnTouchListener {

    private static final int LOADER_PROD_CAD = 0;

    private EditText mEtNome;
    private EditText mEtValor;

    private Uri mUriAtual = null;
    private boolean isDadosAlterados = false;
    private boolean isFormatarCurrencyAtualizado = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_cad);

        // Recebe dados de Uri de ProdutosListActivity
        Intent intent = getIntent();
        mUriAtual = intent.getData();

        // Se tiver dados em mUriAtual, então vai editar, senão vai adicionar
        if (mUriAtual == null) {

            setTitle(R.string.title_produto_cad_add);

        } else {

            setTitle(R.string.title_produto_cad_edit);
            getLoaderManager().initLoader(LOADER_PROD_CAD, null, this);
        }

        // Referencia itens do layout
        mEtNome = findViewById(R.id.et_nome);
        mEtValor = findViewById(R.id.et_preco);

        // Controle entrada de caracteres nos edits
        controleTextWatcher();

        // Verifica se o texto do edit nome foi alterado
        if (!isDadosAlterados) {

            isDadosAlterados = Utilidades.verificarAlteracaoDados(mEtNome);
        }

        // Monitora o EditorAction do teclado
        mEtValor.setOnEditorActionListener(this);

        // Monitora toques em uma view especifica
        mEtValor.setOnTouchListener(this);

        // Retira o foco do edit e coloca o valor zero nele
        Utilidades.semFocoZerado(mEtValor);
    }

    /**
     * Cria o menu
     *
     * @param menu objeto de criação do menu
     * @return verdadeiro se menu foi criado corretamente
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    /**
     * Verifica item do menu que foi selecionado
     *
     * @param item item selecionado
     * @return verdadeiro se ação no menu selecionado foi executada
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

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

        if (!isDadosAlterados) {

            super.onBackPressed();
        }

        Dialogos.onBackPressedDescartarConfirmar(
                ProdutosCadActivity.this,
                ProdutosCadActivity.this);
    }

    /* Recebe dados dos edits, faz validações, coloca no obejtos values e salva os dados no BD */
    private void salvarDadosBD() {

        String nomeEditText = mEtNome.getText().toString().trim();
        String precoEditText = mEtValor.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(precoEditText);

        // Campo não pode ficar vazio
        if (TextUtils.isEmpty(nomeEditText)) {

            mEtNome.setError(getString(R.string.error_campo_vazio));
            mEtNome.requestFocus();
            return;
        }

        // Campo não pode fica vazio
        if (TextUtils.isEmpty(precoEditText)) {

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

        // determina quantidade minima de caracteres no campo de descrição
        if (nomeEditText.length() < MIN_QUANT_CARACT_10) {

            mEtNome.setError(getString(R.string.error_campo_lenght_10));
            mEtNome.requestFocus();
            return;
        }

        // Coloca dados no Objeto values para salvar no BD
        ContentValues values = new ContentValues();
        values.put(AcessoProdutos.NOME, nomeEditText);
        values.put(AcessoProdutos.VALOR, valorDouble);

        // Salva dados no BD
        if (mUriAtual == null) {

            Crud.inserir(ProdutosCadActivity.this, AcessoProdutos.CONTENT_URI_PRODUTOS, values);

        } else {

            Crud.editar(ProdutosCadActivity.this, mUriAtual, values);
        }

        finish();
    }

    /**
     * Define criterios para pesquisa no BD
     * Determina quais colunas retornaram dados, quais as regras a seguidas na pesquisa
     * e o que devem ser pesquisado
     *
     * @param i      Loader que vai fazer a pesquisa
     * @param bundle Argumentos de pesquisa dentro do loader
     * @return resultado da pesquisa em um cursor
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Pesquisa especifica pelo mUriAtual por um unico produto cadastrado
        String[] projection = {
                AcessoProdutos._ID,
                AcessoProdutos.NOME,
                AcessoProdutos.VALOR
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
     * Resultado da pesquisa
     *
     * @param loader Define o loader pesquisado
     * @param cursor cursor com resultado da pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            double valorBD = cursor.getDouble(
                    cursor.getColumnIndex(AcessoProdutos.VALOR));

            String nomeBD = cursor.getString(
                    cursor.getColumnIndex(AcessoProdutos.NOME));

            mEtValor.setText(String.valueOf(valorBD * 100));
            mEtNome.setText(nomeBD);
        }
    }

    /**
     * Determina o que sera feito com os dados antigos apos iniciar uma nova pesquisa
     *
     * @param loader resultado antigo das pesquisas
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Executa uma ação se for clicado um EditorAction no teclado em uma determinada view
     *
     * @param view     view  que vai ser monitorada
     * @param actionId id de uma acao a ser executada
     * @param event    evento
     * @return verdadeiro se ação for executada
     */
    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

        // Salva dados no banco de dados
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            salvarDadosBD();
            return true;
        }

        return false;

    }

    /* Verifica entrada de caracteres nos edits e formata os caracteres se necessario */
    private void controleTextWatcher() {

        /* No edit mEtValor e permitido a entrada apenas de numeros
         * Captura a entrada dos caracteres de numero no edit e faz a formatação para moeda
         * (currency) para ser apresentado para o usuario
         */
        mEtValor.addTextChangedListener(new TextWatcher() {
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

                mEtValor.setText(Formatar.formatarParaCurrency(charSequence.toString().trim()));
                mEtValor.setSelection(mEtValor.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    /**
     * Monitora toque em uma determinada view
     *
     * @param view  view que sera verificada se houve toque
     * @param event evento
     * @return verdadeiro se houve toque
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        switch (id) {

            // Recebe o foco, coloca cursor a direita dos caracteres e abre teclado se tiver fechado
            case R.id.et_preco:
                mEtValor.requestFocus();
                mEtValor.setSelection(mEtValor.getText().length());
                Utilidades.mostrarTeclado(ProdutosCadActivity.this, mEtValor);
                return true;

            default:
                return false;
        }
    }
}
