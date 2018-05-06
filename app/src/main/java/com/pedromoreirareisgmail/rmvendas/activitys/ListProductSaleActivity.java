package com.pedromoreirareisgmail.rmvendas.activitys;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.adapters.ProdAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.*;

public class ListProductSaleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener {

    private static final String TAG = ListProductSaleActivity.class.getSimpleName();
    private static final int LOADER_VEND_LIST = 0;

    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;

    private ProdAdapter mAdapter;

    private String mProdutoPesquisarBD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_sale);

        Log.v(TAG, "");

        initViews();
        emptyLayout();

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ProdAdapter(this);
        mListView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        mListView.setOnItemClickListener(this);

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_VEND_LIST, null, this);
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
        mTvEmpty.setText(R.string.text_venda_list_empty);
        mIvEmpty.setImageResource(R.drawable.ic_coracao_partido);
        mIvEmpty.setContentDescription(getString(R.string.image_desc_produto_list_empty));
        mListView.setEmptyView(mEmptyView);
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

        String[] projection = {
                EntryProduct._ID,
                EntryProduct.COLUMN_NAME,
                EntryProduct.COLUMN_PRICE
        };

        /* A pesquisa inicial traz todos os produtos cadastrados, se clicar no menu search, sera
        pesquisado de acordo com nome do produto digitado
         */
        String selection = EntryProduct.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + mProdutoPesquisarBD + "%"};
        String sortOrder = EntryProduct.COLUMN_NAME;

        return new CursorLoader(
                this,
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

        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

        mAdapter.swapCursor(null);
    }

    /**
     * Click simples no ListView
     * Ao clicar, vai ser aberto a Activity {@link ListProductSaleActivity} , onde podera ser escolhido
     * a quantidade do produto, se a venda sera a prazo ou a vista, se tem adicional ou não e
     * se tem desconto ou não
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "onItemClick");

        Uri uri = ContentUris.withAppendedId(EntryProduct.CONTENT_URI_PRODUCT, id);

        Intent intentRegistrarVenda = new Intent(
                ListProductSaleActivity.this, SellActivity.class);
        intentRegistrarVenda.putExtra(ADICIONAR_VENDA, ADICIONAR_VENDA);
        intentRegistrarVenda.setData(uri);
        startActivity(intentRegistrarVenda);
        finish();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.v(TAG, "onQueryTextChange");

        mProdutoPesquisarBD = newText;

        getLoaderManager().restartLoader(LOADER_VEND_LIST, null, ListProductSaleActivity.this);

        return true;
    }
}
