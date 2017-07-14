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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.adapter.ProdAdapter;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoProdutos;

public class VendListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_PROD_LISTA = 12;

    private ProdAdapter mAdapter;

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

        ListView listView = (ListView) findViewById(R.id.listView_venda_list);
        View emptyView = findViewById(R.id.empty_view_venda_list);
        mAdapter = new ProdAdapter(this, null);

        listView.setEmptyView(emptyView);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                Uri uri = ContentUris.withAppendedId(AcessoProdutos.CONTENT_URI_PRODUTO, id);

                Intent intent = new Intent(VendListActivity.this, VendQuantActivity.class);
                intent.putExtra(Constantes.VENDA_ADICIONAR,Constantes.VENDA_ADICIONAR);
                intent.setData(uri);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(LOADER_PROD_LISTA,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoProdutos._ID,
                AcessoProdutos.COLUNA_PRODUTO_NOME,
                AcessoProdutos.COLUNA_PRODUTO_PRECO
        };

        String sortOrder = AcessoProdutos.COLUNA_PRODUTO_NOME;

        return new CursorLoader(
                this,
                AcessoProdutos.CONTENT_URI_PRODUTO,
                projection,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
