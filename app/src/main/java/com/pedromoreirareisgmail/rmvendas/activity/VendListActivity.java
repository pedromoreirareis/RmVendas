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

public class VendListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_VEND_LIST = 0;

    private ProdAdapter mAdapter;

    private String mProdutoPesquisarBD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vend_list);

        Intent newIntent = getIntent();

        if (newIntent.hasExtra(Constantes.VENDA_ADICIONAR)) {

            if (newIntent.getStringExtra(Constantes.VENDA_ADICIONAR).equals(Constantes.VENDA_ADICIONAR)) {

                setTitle(R.string.title_vend_list);
            }
        }

        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);

        tvEmpty.setText(R.string.text_vend_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_coracao_partido);
        ivEmpty.setContentDescription(getString(R.string.image_desc_prod_list_empty));

        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mAdapter = new ProdAdapter(this);

        listView.setAdapter(mAdapter);

        /* ao clicar sera aberto uma nova activity para escolha da quantidade e outros detalhes da
         venda*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                Uri uri = ContentUris.withAppendedId(AcessoProdutos.CONTENT_URI_PRODUTOS, id);

                Intent intent = new Intent(VendListActivity.this, VendQuantActivity.class);
                intent.putExtra(Constantes.VENDA_ADICIONAR, Constantes.VENDA_ADICIONAR);
                intent.setData(uri);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(LOADER_VEND_LIST, null, this);
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

                mProdutoPesquisarBD = newText;

                getLoaderManager().restartLoader(LOADER_VEND_LIST, null, VendListActivity.this);

                return true;
            }
        });


        return true;
    }

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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
