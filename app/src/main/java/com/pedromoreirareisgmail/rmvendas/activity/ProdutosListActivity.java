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
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.adapter.ProdAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoProdutos;

public class ProdutosListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener,
        SearchView.OnQueryTextListener {

    private static final int LOADER_PRODUTOS_LIST = 0;

    private ProdAdapter mAdapter;
    private String mProdutoPesquisarBD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_list);

        // Trata o botão Flutuante - Abre activity ProdutosCadActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentCadastroProdutos = new Intent(ProdutosListActivity.this, ProdutosCadActivity.class);
                startActivity(intentCadastroProdutos);
            }
        });

        //  Faz referencia aos itens do layout
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);
        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);

        //  layout vazio - cadastro sem registros
        tvEmpty.setText(R.string.text_produto_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_contract_list);
        ivEmpty.setContentDescription(getString(R.string.image_desc_produto_list_empty));
        listView.setEmptyView(emptyView);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ProdAdapter(this);
        listView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_PRODUTOS_LIST, null, this);
    }

    /**
     * Cria o menu
     *
     * @param menu Interface de criação do menu
     * @return Menu inflado
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Referencia o menu de pesuisa
        MenuItem menuItem = menu.findItem(R.id.action_search);

        //  Instancia o SearchView e diz que quem vai tratar o evento é a Activity
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    /**
     * Define os parametros de pesquisa no BD
     *
     * @param i      Loader responsavel pela pesquisa
     * @param bundle Conjunto de dados em um bundle
     * @return Um Loader com um Cursor com resultado da pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoProdutos._ID,
                AcessoProdutos.NOME,
                AcessoProdutos.VALOR
        };

        /* retorna todos os produtos cadastrados - A pesquisa inicial traz todos os produtos, se
         * utilizar o menu search, sera pesquisado pelo nome do produto */
        String selection = AcessoProdutos.NOME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + mProdutoPesquisarBD + "%"};
        String sortOrder = AcessoProdutos.NOME;

        return new CursorLoader(
                this,
                AcessoProdutos.CONTENT_URI_PRODUTOS,
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
     * @param cursor Cursor com dados da pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
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
     * Abre um Dialog com o nome do Produto no Titulo e o valor na mensagem
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        String tituloDialog;
        String mensagemDialog;

        tituloDialog = getString(R.string.dialog_informacao_produto_title);


        //  Mensagem do Dialog - Descrição
        mensagemDialog = String.format(getResources().getString(R.string.dialog_informacao_produtos_list),
                cursor.getString(cursor.getColumnIndex(AcessoProdutos.NOME)),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoProdutos.VALOR))));

        Dialogos.dialogoExibirDados(ProdutosListActivity.this, tituloDialog, mensagemDialog);
    }

    /**
     * Click longo no ListView
     * Abre um Dialog para escolher se vai editar ou excluir produto
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     * @return true de click longo foi efetuado com sucesso
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Uri uri = ContentUris.withAppendedId(AcessoProdutos.CONTENT_URI_PRODUTOS, id);

        Cursor cursor = mAdapter.getCursor();

        String mensagemExcluir = String.format(getResources().getString(R.string.dialog_excluir_produtos_list),
                cursor.getString(cursor.getColumnIndex(AcessoProdutos.NOME)),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoProdutos.VALOR))));

        Dialogos.dialogoEditarExcluir(
                ProdutosListActivity.this,
                ProdutosCadActivity.class,
                uri,
                mensagemExcluir
        );

        return true;
    }

    /**
     * Não implementado
     * Faz pesquisa ao digitar texto e clicar no icone de pesquisa
     *
     * @param query Texto digitado para pesquisa
     * @return verdadeiro se pesquisa deu certo
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Ao alterar o texto no campo Edit do SearchView sera feita nova pesquisa
     *
     * @param newText Texto no campo Edit do SearchView
     * @return verdadeiro se pesquisa for completada com sucesso
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        // Ao digitar, automaticamente iniciar a pesquisa no BD
        mProdutoPesquisarBD = newText;

        //  Reiniciar o gerenciador de dados do BD - Pesquisa novamente e retornando novos dados
        getLoaderManager().restartLoader(LOADER_PRODUTOS_LIST, null, ProdutosListActivity.this);

        return true;
    }
}
