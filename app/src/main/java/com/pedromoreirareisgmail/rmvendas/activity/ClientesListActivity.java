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

public class ClientesListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_CLIENTES_LIST = 0;
    private ClientesAdapter mAdapter;

    private String mPesquisar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_list);


        FloatingActionButton fabProdutos = (FloatingActionButton) findViewById(R.id.fab_add);
        fabProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ClientesListActivity.this, ClientesCadActivity.class);
                startActivity(intent);
            }
        });

        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);

        tvEmpty.setText("Nenhum cliente cadastrado");
        ivEmpty.setImageResource(R.drawable.ic_money_up);
        ivEmpty.setContentDescription("Imagem cliente");

        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mAdapter = new ClientesAdapter(this);
        listView.setAdapter(mAdapter);

        // Com clique longo no listview, aparecera um dialog com opção de editar ou excluir
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {

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
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Cursor cursor = mAdapter.getCursor();

                String nome = cursor.getString(cursor.getColumnIndex(AcessoClientes.NOME));

                Intent intent = new Intent(ClientesListActivity.this, RegistroReceberActivity.class);


                Bundle bundle = new Bundle();
                bundle.putString("clienteId", String.valueOf(id));
                bundle.putString("clienteNome", nome);


                intent.putExtras(bundle);
                startActivity(intent);


            }
        });

        getLoaderManager().initLoader(LOADER_CLIENTES_LIST, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mPesquisar = newText;

                getLoaderManager().restartLoader(LOADER_CLIENTES_LIST, null, ClientesListActivity.this);

                return true;
            }
        });


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

    }
}
