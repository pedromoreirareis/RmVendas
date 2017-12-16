package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.adapter.ProdAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoProdutos;

public class VendListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener {

    private static final int LOADER_VEND_LIST = 0;

    private ProdAdapter mAdapter;

    private String mProdutoPesquisarBD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vend_list);

        // Referencia itens do layout
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);
        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);

        // Layout vazio - Cadastro sem registros
        tvEmpty.setText(R.string.text_vend_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_coracao_partido);
        ivEmpty.setContentDescription(getString(R.string.image_desc_prod_list_empty));
        listView.setEmptyView(emptyView);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ProdAdapter(this);
        listView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        listView.setOnItemClickListener(this);

        // Coloca o titulo e data na Activity
        setTitle(R.string.title_vend_list);

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_VEND_LIST, null, this);
    }


    /**
     * Cria o menu - Cria o searchView para pesquisas
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

        /* A pesquisa inicial traz todos os produtos cadastrados, se clicar no menu search, sera
        pesquisado de acordo com nome do produto digitado
         */
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
     * @param loader Loader com um cursor com dados da pesquisa
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

    /* */

    /**
     * Click simples no ListView
     * Ao clicar, vai ser aberto a Activity {@link VendListActivity} , onde podera ser escolhido
     * a quantidade do produto, se a venda sera a prazo ou a vista, se tem cobertura ou não e
     * se tem desconto ou não
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Uri uri = ContentUris.withAppendedId(AcessoProdutos.CONTENT_URI_PRODUTOS, id);

        Intent intent = new Intent(VendListActivity.this, VendQuantActivity.class);
        intent.putExtra(Constantes.VENDA_ADICIONAR, Constantes.VENDA_ADICIONAR);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * Pesquisa apos digitação do texto e click no icone de pesquisa
     *
     * @param query Texto no campo Edit do SearcView
     * @return verdadeiro se pesquisa for completada com sucesso
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
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
        mProdutoPesquisarBD = newText;

        getLoaderManager().restartLoader(LOADER_VEND_LIST, null, VendListActivity.this);

        return true;
    }
}
