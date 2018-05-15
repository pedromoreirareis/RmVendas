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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.adapters.ProductAdapter;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.constant.ConstTag;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

public class ListProductSaleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener {

    private static final String TAG = ConstTag.TAG_MAIN + ListProductSaleActivity.class.getSimpleName();

    private View mEmptyView;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;


    private ProductAdapter mAdapter;
    private Context mContext;

    private String mSearchDB = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_sale);

        Log.v(TAG, "onCreate");

        initViews();
        emptyLayout();
        initListenerAndObject();

        // Obtem e iniciar o gerenciador do carregar de dados
        getLoaderManager().initLoader(ConstLoader.LOADER_LIST_PRODUCT_SALE, null, this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        Log.v(TAG, "emptyLayout");

        // Layout vazio - Cadastro sem registros
        mTvEmpty.setText(R.string.text_product_sale_empty);
        mIvEmpty.setImageResource(R.drawable.ic_heart_broken);
        mIvEmpty.setContentDescription(getString(R.string.descr_product_sale_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenerAndObject() {

        Log.v(TAG, "initListenerAndObject");

        // Contexto da Activity
        mContext = ListProductSaleActivity.this;

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ProductAdapter(mContext);
        mListView.setAdapter(mAdapter);

        // Clique simples no listview
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        // Colunas que serao retornadas
        String[] projection = {
                EntryProduct._ID,
                EntryProduct.COLUMN_NAME,
                EntryProduct.COLUMN_PRICE
        };

        // Coluna que sera pesquisada e tipo da pesquisa
        String selection = EntryProduct.COLUMN_NAME + " LIKE ? ";

        // Argumentos da pesquisa
        String[] selectionArgs = new String[]{"%" + mSearchDB + "%"};

        // Ordem de retorno da pesquisa
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

        Log.v(TAG, "onLoadFinished");

        // Passa retorno da pesquisa para o adapter e listview
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

        // Se pesquisa redefinida não faz nada
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "onItemClick");

        Uri uri = ContentUris.withAppendedId(EntryProduct.CONTENT_URI_PRODUCT, id);

        Intent intentSale = new Intent(
                mContext,
                SellActivity.class
        );

        intentSale.setData(uri);
        intentSale.putExtra(ConstIntents.INTENT_ADD_SELL, Const.CALL_SELL_ADD);
        startActivity(intentSale);

        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.v(TAG, "onQueryTextChange");

        mSearchDB = newText;

        getLoaderManager().restartLoader(
                ConstLoader.LOADER_LIST_PRODUCT_SALE,
                null,
                ListProductSaleActivity.this
        );

        return true;
    }
}
