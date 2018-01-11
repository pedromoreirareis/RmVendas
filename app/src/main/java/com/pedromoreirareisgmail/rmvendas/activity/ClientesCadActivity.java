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
import android.text.TextUtils;
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
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;
import com.pedromoreirareisgmail.rmvendas.db.Crud;


public class ClientesCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        EditText.OnEditorActionListener {

    private static final String TAG = ClientesCadActivity.class.getSimpleName();
    private static final int LOADER_CLIENTES_CAD = 0;

    private EditText mEtNome;
    private EditText mEtFone;

    private Uri mUriAtual = null;
    private boolean isDadosAlterado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_cad);

        Log.v(TAG, "onCreate");

        initViews();
        initIntents();

        /* Se Uri vazio, Activity vai adicionar registro. Senão então editar.*/
        if (mUriAtual == null) {

            setTitle(getResources().getString(R.string.title_clientes_add));
        } else {

            /* Para editar fazer pesquisa no BD para pegar dados do Uri passado*/
            setTitle(getResources().getString(R.string.title_clientes_edit));
            getLoaderManager().initLoader(LOADER_CLIENTES_CAD, null, this);
        }

        // Verifica se houve alteração nos caracteres do edit
        if (!isDadosAlterado) {

            isDadosAlterado = Utilidades.verificarAlteracaoDados(mEtFone)
                    || Utilidades.verificarAlteracaoDados(mEtNome);
        }

        mEtFone.setOnEditorActionListener(this);
        mEtFone.setOnTouchListener(this);
    }

    private void initViews() {

        // Referencia itens do layout
        mEtNome = findViewById(R.id.et_clientes_nome);
        mEtFone = findViewById(R.id.et_clientes_numero_fone);

        Log.v(TAG, "initViews");
    }

    private void initIntents() {

        /* Se Activity foi aberta para alteração, vair receber uma Uri*/
        Intent intentUri = getIntent();
        mUriAtual = intentUri.getData();

        Log.v(TAG, "initIntents");
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

        int id = item.getItemId();

        switch (id) {

            // Menu salvar
            case R.id.action_salvar:
                salvarDadosBD();
                return true;

            /* Botão Up - Verifica alteração de dados antes de voltar a Activity que chamou
             *
             * Não Alterados - volta para activity que chamou a ClientesCadActivity
             *
             * Alterados - Abre um Dialog para confirmar se os dados alterados serão descartados e
             * devem voltar a Activity que chamou a activity ClientesCadActivity ou se deve
             * permanecer na Activity atual e manter os dados que estão sendo alterados
             */
            case android.R.id.home:

                // Não foi alterado - Volta a Activity que chamou a activity ClientesCadActivity
                if (!isDadosAlterado) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Dialogos.homeDescartarConfirmar(
                        ClientesCadActivity.this,
                        ClientesCadActivity.this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botão volta (embaixo) - Se os dados foram alterados abre Dialog para verificar se deseja
     * descartar as alterações ou se deseja continuar editando*/
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        Dialogos.onBackPressedDescartarConfirmar(
                ClientesCadActivity.this,
                ClientesCadActivity.this);
    }

    /* Recebe os dados que foram digitados nos Edits, faz validações, coloca em um ContentValues,
     * verifica se deve criar um novo registro com os dados ou se é uma alteração de dados dados e
     * depois salva no banco de dados*/
    private void salvarDadosBD() {

        Log.v(TAG, "Iniciando salvar BD");

        String nomeEditText = mEtNome.getText().toString().trim();
        String foneEditText = mEtFone.getText().toString().trim();

        // O campo nome não pode ficar vazio
        if (TextUtils.isEmpty(nomeEditText)) {

            mEtNome.setError(getString(R.string.error_campo_vazio));
            mEtNome.requestFocus();
            return;
        }

        // O campo nome deve ter pelo menos 5 caracteres
        if (nomeEditText.length() < Constantes.MIN_QUANT_CARACT_5) {

            mEtNome.setError(getString(R.string.error_campo_lenght_5));
            mEtNome.requestFocus();
            return;
        }

        // Campo não pode ficar vazio
        if (TextUtils.isEmpty(foneEditText)) {

            mEtFone.setError(getString(R.string.error_campo_vazio));
            mEtFone.requestFocus();
            return;
        }

        // Campo não pode ficar vazio
        if (foneEditText.length() < Constantes.MIN_DIGITOS_NUM_FONE) {

            mEtFone.setError(getString(R.string.error_campo_lenght_fone));
            mEtFone.requestFocus();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AcessoClientes.NOME, nomeEditText);
        values.put(AcessoClientes.TELEFONE, foneEditText);

        if (mUriAtual == null) {

            Crud.inserir(ClientesCadActivity.this, AcessoClientes.CONTENT_URI_CLIENTES, values);

            Log.v(TAG, "Adicionar - adicionou cliente");

        } else {


            Crud.editar(ClientesCadActivity.this, mUriAtual, values);

            Log.v(TAG, "Editando - editou cliente");
        }

        Log.v(TAG, "Finalizando salvar BD");

        finish();
    }


    /**
     * No caso de uma Edição - Define quais são as regras para pesquisa no banco de dados
     * Verifica quais são as colunas que retornarão dados (id é o unico obrigatorio)
     * Quais os criterios de busca, e qauais os termos a serem buscados
     *
     * @param id   identificador do Loader que ira fazer as buscas
     * @param args Argumento para as buscas
     * @return Cursor com dados obtidos na pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(TAG, "onCreateLoader");

        // Trazer todos os dados de um clientes especifico indentificado pelo mUriAtual
        String[] projection = {
                AcessoClientes._ID,
                AcessoClientes.NOME,
                AcessoClientes.TELEFONE
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
     * Define o que fazer com os dados obtidos na pesquisa
     *
     * @param loader Loader cursor com dados
     * @param cursor Cursor com dados da pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.v(TAG, "onLoadFinished");

        // Pega nome e telefone do cliente e coloca nos Edits, caso esteja editando
        if (cursor.moveToFirst()) {

            String nomeBD = cursor.getString(
                    cursor.getColumnIndex(AcessoClientes.NOME));

            int foneBD = cursor.getInt(
                    cursor.getColumnIndex(AcessoClientes.TELEFONE));

            mEtNome.setText(nomeBD);
            mEtFone.setText(foneBD);
        }
    }

    /**
     * O que fazer com dados atuais no caso de iniciar nova pesquisa
     *
     * @param loader loader com dados atuais
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Monitora o toque em Views especifica
     *
     * @param view  Identifica pelo getId a view que deve ser monitorada
     * @param event Evento
     * @return Verdadeiro se a view monitorada foi tocada
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");

        int id = view.getId();

        switch (id) {

            // Recebe foco, mostra o teclado
            case R.id.et_clientes_nome:
                mEtNome.requestFocus();
                Utilidades.mostrarTeclado(ClientesCadActivity.this, mEtNome);
                return true;

            case R.id.et_clientes_numero_fone:
                mEtFone.requestFocus();
                Utilidades.mostrarTeclado(ClientesCadActivity.this, mEtFone);
                return true;

            default:
                return false;
        }
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
