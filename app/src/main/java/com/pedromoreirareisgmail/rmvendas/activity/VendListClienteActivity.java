package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
import com.pedromoreirareisgmail.rmvendas.adapter.ClientesAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;

public class VendListClienteActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener {

    public static final int LOADER_VEND_CLIENTES_LIST = 0;

    private static final String URI_ATUAL = "uri_atual";
    private static final String VALOR_UNIDADE = "valor_unidade";
    private static final String ID_CLIENTE = "id_cliente";
    private String mUri;
    private String mValorUnidade;
    private String mNomeProduto;
    private String mQuantidadeProduto;
    private String mValorAdicional;
    private String mValorDesconto;
    private String mValorPrazo;
    private ClientesAdapter mAdapter;

    private String mPesquisar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_list);


        Intent intentDadosVenda = getIntent();

        if (intentDadosVenda.hasExtra(URI_ATUAL)) {

            mUri = intentDadosVenda.getStringExtra(URI_ATUAL);
        }

        if (intentDadosVenda.hasExtra(VALOR_UNIDADE)) {

            mValorUnidade = intentDadosVenda.getStringExtra(VALOR_UNIDADE);
        }

        // Referencia itens do layout
        TextView tvEmpty = findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = findViewById(R.id.iv_empty_view);
        ListView listView = findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);

        // Layout vazio - Cadastro sem registros
        tvEmpty.setText(R.string.text_clientes_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_money_up);
        ivEmpty.setContentDescription(getString(R.string.image_desc_clientes_list_empty));
        listView.setEmptyView(emptyView);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ClientesAdapter(this);
        listView.setAdapter(mAdapter);

        // Clique simples no ListView
        listView.setOnItemClickListener(this);

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_VEND_CLIENTES_LIST, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
// Não implementado
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mPesquisar = newText;

        getLoaderManager().restartLoader(LOADER_VEND_CLIENTES_LIST, null, VendListClienteActivity.this);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Cursor cursor = mAdapter.getCursor();

        String idCliente = cursor.getString(cursor.getColumnIndex(AcessoClientes._ID));

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString(ID_CLIENTE, idCliente);
        bundle.putString(URI_ATUAL, mUri);
        bundle.putString(VALOR_UNIDADE, mValorUnidade);

        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);

        finish();
    }
}
