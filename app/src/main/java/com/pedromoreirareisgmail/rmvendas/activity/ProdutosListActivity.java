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
import com.pedromoreirareisgmail.rmvendas.adapter.ProdAdapter;
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoProdutos;

public class ProdutosListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_PROD_LISTA = 7;

    private ProdAdapter mAdapter;
    private String mPesquisa = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_list);

        FloatingActionButton fabProdutos = (FloatingActionButton) findViewById(R.id.fab_add);
        fabProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProdutosListActivity.this, ProdutosCadActivity.class);
                startActivity(intent);
            }
        });

        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);

        tvEmpty.setText(R.string.text_prod_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_contract_list);
        ivEmpty.setContentDescription(getString(R.string.image_desc_prod_list_empty));

        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);
        mAdapter = new ProdAdapter(this);

        listView.setEmptyView(emptyView);
        listView.setAdapter(mAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, final long id) {

                Uri uri = ContentUris.withAppendedId(AcessoProdutos.CONTENT_URI_PRODUTO, id);

                Cursor cur = mAdapter.getCursor();
                String nome = mAdapter.getCursor().getString(
                        cur.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_NOME));

                Dialogos.editarExcluir(
                        ProdutosListActivity.this,
                        ProdutosCadActivity.class,
                        uri,
                        nome
                );

                return true;
            }
        });

        getLoaderManager().initLoader(LOADER_PROD_LISTA, null, this);
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

                mPesquisa = newText;

                getLoaderManager().restartLoader(LOADER_PROD_LISTA, null, ProdutosListActivity.this);

                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoProdutos._ID,
                AcessoProdutos.COLUNA_PRODUTO_NOME,
                AcessoProdutos.COLUNA_PRODUTO_PRECO
        };

        String selection = AcessoProdutos.COLUNA_PRODUTO_NOME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + mPesquisa + "%"};
        String sortOrder = AcessoProdutos.COLUNA_PRODUTO_NOME;

        return new CursorLoader(
                this,
                AcessoProdutos.CONTENT_URI_PRODUTO,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
