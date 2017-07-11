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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.adapter.ProdAdapter;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoProdutos;

public class ProdutosListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ProdutosListActivity.class.getSimpleName();

    private static final int LOADER_PROD_LISTA = 0;

    private ProdAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_list);

        // Botão flutuante
        FloatingActionButton fabProdutos = (FloatingActionButton) findViewById(R.id.fab_prod_list);
        fabProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProdutosListActivity.this, ProdutosCadActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView_prod_list);
        View emptyView = findViewById(R.id.empty_view_prod_list);
        mAdapter = new ProdAdapter(this, null);

        listView.setEmptyView(emptyView);
        listView.setAdapter(mAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, final long id) {


                Uri uri = ContentUris.withAppendedId(AcessoProdutos.CONTENT_URI_PRODUTO, id);

                Cursor cur = mAdapter.getCursor();
                String nome = mAdapter.getCursor().getString(cur.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_NOME));

                UtilsDialog.editarExcluir(
                        ProdutosListActivity.this,
                        ProdutosCadActivity.class,
                        uri,
                        getString(R.string.dialog_prod_list_esc_ee_tilte),
                        getString(R.string.dialog_prod_list_esc_ee_conf_excluir_title),
                        getString(R.string.dialog_prod_list_esc_ee_conf_excluir_cancelar),
                        getString(R.string.dialog_prod_list_esc_ee_conf_excluir_excluir),
                        getString(R.string.dialog_prod_list_esc_ee_excluido_sucesso),
                        getString(R.string.dialog_prod_list_esc_ee_excluido_erro),
                        nome
                );

                return true;
            }
        });

        getLoaderManager().initLoader(LOADER_PROD_LISTA, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoProdutos._ID,
                AcessoProdutos.COLUNA_PRODUTO_NOME,
                AcessoProdutos.COLUNA_PRODUTO_PRECO
        };

        String sortOrder = AcessoProdutos.COLUNA_PRODUTO_NOME;

        /* CONTENT_URI_PRODUTO - uri de todos itens da tabela */
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
        mAdapter.swapCursor(null);
    }
}
