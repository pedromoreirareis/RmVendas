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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;
import com.pedromoreirareisgmail.rmvendas.db.Crud;


public class ClientesCadActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener {

    public static final int LOADER_CLIENTES_CAD = 0;

    private EditText mEtNome;
    private EditText mEtFone;

    private Uri mUriAtual = null;
    private boolean isDadosAlterado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_cad);

        /* Se Activity foi aberta para alteração, vair receber uma Uri*/
        Intent intent = getIntent();
        mUriAtual = intent.getData();

        /* Se não foi recebido Uri, Activity vai adicionar registro. Se recebeu vai editar.*/
        if (mUriAtual == null) {

            setTitle("Titulo Add");

        } else {

            /* Se for editar fara pesquisa na banco de dados para pegar dados do Uri passado*/
            setTitle("Titulo Editar");
            getLoaderManager().initLoader(LOADER_CLIENTES_CAD, null, this);
        }

        // Referencia itens do layout
        mEtNome = (EditText) findViewById(R.id.et_clientes_nome);
        mEtFone = (EditText) findViewById(R.id.et_clientes_num_fone);

        mEtFone.setOnTouchListener(this);
    }

    /**
     * Cria o menu
     *
     * @param menu Objeto para criação do menu
     * @return verdadeiro se menu foi inflado a partir de um Layout de menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_salvar, menu);

        return true;
    }

    /**
     * Identifica item do menu selecionado
     *
     * @param item Item do menu que foi selecionado
     * @return verdadeiro se item do menu selecionado foi ativado corretamente
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

        String nomeEditText = mEtNome.getText().toString().trim();
        String foneEditText = mEtFone.getText().toString().trim();

        // Campo não pode ficar vazio
        if (TextUtils.isEmpty(nomeEditText)) {

            mEtNome.setError(getString(R.string.error_campo_vazio));
            mEtNome.requestFocus();
            return;
        }

        // Campo não pode ficar vazio
        if (TextUtils.isEmpty(foneEditText)) {

            mEtFone.setError(getString(R.string.error_campo_vazio));
            mEtFone.requestFocus();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(AcessoClientes.NOME, nomeEditText);
        values.put(AcessoClientes.TELEFONE, foneEditText);

        if (mUriAtual == null) {

            Crud.inserir(ClientesCadActivity.this, AcessoClientes.CONTENT_URI_CLIENTES, values);

        } else {


            Crud.editar(ClientesCadActivity.this, mUriAtual, values);
        }

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

        int id = view.getId();

        switch (id) {

            // Recebe o foco e coloca o cursor no fim
            case R.id.et_valor:
                mEtFone.requestFocus();
                Utilidades.mostrarTeclado(ClientesCadActivity.this, mEtFone);
                return true;

            default:
                return false;
        }
    }
}
