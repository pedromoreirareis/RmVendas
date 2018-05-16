package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
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
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.adapters.ProductAdapter;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

public class ListProductActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener {

    private View mEmptyView;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private FloatingActionButton mFab;

    private ProductAdapter mAdapter;
    private Context mContext;

    private String mSearchDB = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        initViews();
        emptyLayout();
        initListenersAndObjects();

        // Obtem e iniciar o gerenciador do carregar de dados
        getLoaderManager().initLoader(
                ConstLoader.LOADER_LIST_PRODUCT,
                null,
                this
        );
    }

    private void initViews() {

        //  Faz referencia aos itens do layout
        mFab = findViewById(R.id.fab_add);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        //  layout vazio - cadastro sem registros
        mTvEmpty.setText(R.string.text_product_empty);
        mIvEmpty.setImageResource(R.drawable.ic_barcode_search_black);
        mIvEmpty.setContentDescription(getString(R.string.descr_product_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenersAndObjects() {

        // Contexto da Activity
        mContext = ListProductActivity.this;

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ProductAdapter(mContext);
        mListView.setAdapter(mAdapter);

        // Trata o botão Flutuante
        mFab.setOnClickListener(this);

        // Clique simples e Longo no ListView
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Referencia o menu de pesquisa
        MenuItem menuItem = menu.findItem(R.id.action_search);

        //  Instancia o SearchView e diz que quem vai tratar o esse metodo é a Activity
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Colunas que serao retornadas
        String[] projection = {
                EntryProduct._ID,
                EntryProduct.COLUMN_NAME,
                EntryProduct.COLUMN_PRICE
        };

        // O que sera pesquisado em casa coluna
        String selection = EntryProduct.COLUMN_NAME + " LIKE ? ";

        // Dados para a pesquisa em cada coluna
        String[] selectionArgs = new String[]{"%" + mSearchDB + "%"};

        // Ordem que sera retonado os dados
        String sortOrder = EntryProduct.COLUMN_NAME;

        return new CursorLoader(
                mContext,
                EntryProduct.CONTENT_URI_PRODUCT,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Envia dados retornados do BD para o adapter e ListView
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Se loader foi redefinido não passa nenhum dado ao adapter
        mAdapter.swapCursor(null);
    }

    /* Ao clicar vair abir um Dialog com o valor e descrição da Entrada */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        String title = getString(R.string.dialog_inf_title_product);
        String name = cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE));

        String message = String.format(
                getString(R.string.dialog_inf_product_list),
                name,
                Formatting.doubleToCurrency(price)
        );

        Messages.displayData(mContext, title, message);
    }

    /* No click longo sera aberto um Dialog com opção Editar ou Excluir */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Uri uri = ContentUris.withAppendedId(EntryProduct.CONTENT_URI_PRODUCT, id);
        Cursor cursor = mAdapter.getCursor();

        String name = cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE));

        String messageDelete = String.format(
                getString(R.string.dialog_excluir_produtos_list),
                name,
                Formatting.doubleToCurrency(price));

        Messages.editOurDelete(
                mContext,
                RegisterProductActivity.class,
                uri,
                messageDelete
        );

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        // Ao digitar o searchView sera atualizada mSearchView
        mSearchDB = newText;

        // Se mSearchView for diferente de vazio, o gerenciador de dados sera reiniciado
        // e uma nova pesquisa ao banco de dados sera feita
        getLoaderManager().restartLoader(
                ConstLoader.LOADER_LIST_PRODUCT,
                null,
                ListProductActivity.this
        );

        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_add:
                Intent intentCadastroProdutos = new Intent(
                        mContext,
                        RegisterProductActivity.class
                );
                startActivity(intentCadastroProdutos);
                break;
        }
    }
}
