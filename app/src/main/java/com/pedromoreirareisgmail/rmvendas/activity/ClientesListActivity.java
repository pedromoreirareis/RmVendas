package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.adapter.ClientesAdapter;

import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;

public class ClientesListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener {

    public static final int LOADER_CLIENTES_LIST = 0;

    private ClientesAdapter mAdapter;

    private String mPesquisar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_list);

        // Trata o botão Flutuante - Abre activity EntCadActivity
        FloatingActionButton fabProdutos = (FloatingActionButton) findViewById(R.id.fab_add);
        fabProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ClientesListActivity.this, ClientesCadActivity.class);
                startActivity(intent);
            }
        });

        // Referencia itens do layout
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);
        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);

        // Layout vazio - Cadastro sem registros
        tvEmpty.setText(getString(R.string.text_clientes_list_empty));
        ivEmpty.setImageResource(R.drawable.ic_money_up);
        ivEmpty.setContentDescription("Imagem cliente");
        listView.setEmptyView(emptyView);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ClientesAdapter(this);
        listView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_CLIENTES_LIST, null, this);
    }

    /**
     * Cria o menu e o SearchView para pesquisa no banco de dados
     *
     * @param menu Interface de criação do menu
     * @return Menu inflado
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Define os parametros de pesquisa no BD
     *
     * @param id   Loader responsavel pela pesquisa
     * @param args Conjunto de dados em um bundle
     * @return Um Loader com um Cursor com resultado da pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                AcessoClientes._ID,
                AcessoClientes.NOME,
                AcessoClientes.TELEFONE
        };

        /* retorna todos os produtos cadastrados - A pesquisa inicial traz todos os produtos, se
         * utilizar o menu search, sera pesquisado pelo nome do produto */
        String selection = AcessoClientes.NOME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + mPesquisar + "%"};
        String sortOrder = AcessoClientes.NOME;

        return new CursorLoader(
                this,
                AcessoClientes.CONTENT_URI_CLIENTES,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

    }

    /**
     * Define o que fazer com os dados retornados do BD
     *
     * @param loader Define o loader pesquisado
     * @param data   Cursor com dados da pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);
    }

    /**
     * Ao reiniciar a pesquisa o que fazer com os dados velhos
     *
     * @param loader Loader com dados antigos
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }

    /**
     * Click simples no ListView
     * Ao clicar ira abrir a {@link RegistroReceberActivity} para fazer edição de um registro de
     * um cliente ou para adicionar um novo registro
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = mAdapter.getCursor();

        String nome = cursor.getString(cursor.getColumnIndex(AcessoClientes.NOME));
        int fone = cursor.getInt(cursor.getColumnIndex(AcessoClientes.TELEFONE));

        Intent intent = new Intent(ClientesListActivity.this, RegistroReceberActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("clienteId", String.valueOf(id));
        bundle.putString("clienteNome", nome);
        bundle.putString("clienteFone", String.valueOf(fone));

        intent.putExtras(bundle);
        startActivity(intent);

    }

    /**
     * Click longo no ListView
     * Ao ter um click longo sera aberto um Dialog para escolher se deve editar ou excluir o registro
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     * @return true de click longo foi efetuado com sucesso
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Uri uri = ContentUris.withAppendedId(AcessoClientes.CONTENT_URI_CLIENTES, id);

        Cursor cursor = mAdapter.getCursor();
        String mensagemExcluir = mAdapter.getCursor().getString(
                cursor.getColumnIndex(AcessoClientes.NOME));

        Dialogos.dialogoEditarExcluir(
                ClientesListActivity.this,
                ClientesCadActivity.class,
                uri,
                mensagemExcluir
        );

        return true;

    }

    /**
     * Pesquisa apos digitação do texto e click no icone de pesquisa
     *
     * @param query Texto no campo Edit do SearcView
     * @return verdadeiro se pesquisa for completada com sucesso
     */
    @Override
    public boolean onQueryTextSubmit(String query) {

        // Não implementado
        return false;
    }

    /**
     * Pesquisa no banco de dado de acordo com a alteração no texto digitado no edit do searchView
     * Apenas pela alteração do texto, faz uma nova pesquisa
     *
     * @param newText Texto no campo Edit do searchView
     * @return verdadeiro se pesquisa doi efetuada com sucesso
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        mPesquisar = newText;

        getLoaderManager().restartLoader(LOADER_CLIENTES_LIST, null, ClientesListActivity.this);

        return true;
    }
}
