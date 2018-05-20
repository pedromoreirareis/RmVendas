package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
import com.pedromoreirareisgmail.rmvendas.adapters.ClientAdapter;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;
import com.pedromoreirareisgmail.rmvendas.models.Client;
import com.pedromoreirareisgmail.rmvendas.models.SellToClient;


public class ListClientSaleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener {

    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;
    private FloatingActionButton mFab;

    private SellToClient sellToClient;

    private Context mContext;
    private ClientAdapter mAdapter;
    private String mSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_client);

        initViews();
        emptyLayout();
        initListenerAndObject();

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(ConstLoader.LOADER_LIST_CLIENT_SALE, null, this);
    }

    private void initViews() {

        // Referencia itens do layout
        mFab = findViewById(R.id.fab_add);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        // Layout vazio - Cadastro sem registros
        mTvEmpty.setText(R.string.text_client_search_empty);
        mIvEmpty.setImageResource(R.drawable.ic_question);
        mIvEmpty.setContentDescription(getString(R.string.descr_client_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenerAndObject() {

        // Contexto da Activty
        mContext = ListClientSaleActivity.this;

        sellToClient = new SellToClient();

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ClientAdapter(mContext);
        mListView.setAdapter(mAdapter);

        Intent intentSell = getIntent();
        if (intentSell.hasExtra(ConstIntents.INTENT_SELL_TO_CLIENT)) {

            sellToClient = intentSell.getParcelableExtra(ConstIntents.INTENT_SELL_TO_CLIENT);
        }

        // Listener do botão Flutuante
        mFab.setOnClickListener(this);

        // Clique simples no ListView
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search_client, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search_menu_search_client);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent intentSell = new Intent();

                sellToClient.setClientId(Const.NO_ID_CLIENT_TO_SELL);

                intentSell.putExtra(ConstIntents.INTENT_CLIENT_TO_SELL, sellToClient);

                setResult(RESULT_OK, intentSell);

                finish();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                EntryClient._ID,
                EntryClient.COLUMN_NAME,
                EntryClient.COLUMN_FONE
        };

        String selection = EntryClient.COLUMN_NAME + " LIKE ? ";
        String[] selectionArgs = new String[]{"%" + mSearch + "%"};
        String sortOrder = EntryClient.COLUMN_NAME;

        return new CursorLoader(
                mContext,
                EntryClient.CONTENT_URI_CLIENT,
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

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mSearch = newText;

        getLoaderManager().restartLoader(
                ConstLoader.LOADER_LIST_CLIENT_SALE,
                null,
                ListClientSaleActivity.this
        );

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        Intent intentSell = new Intent();
        sellToClient.setClientId(cursor.getLong(cursor.getColumnIndex(EntryClient._ID)));

        intentSell.putExtra(ConstIntents.INTENT_CLIENT_TO_SELL, sellToClient);

        setResult(RESULT_OK, intentSell);

        finish();
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.fab_add:

                Intent intentRegisterClient = new Intent(
                        mContext,
                        RegisterClientActivity.class
                );

                Client client = new Client();

                client.setCalled(Const.CALL_LIST_CLIENT_SALE);

                intentRegisterClient.putExtra(ConstIntents.INTENT_CALLED_CLIENT, client);

                startActivity(intentRegisterClient);
                break;
        }
    }

    @Override
    public void onBackPressed() {


        Intent intentSell = new Intent();

        sellToClient.setClientId(Const.NO_ID_CLIENT_TO_SELL);

        intentSell.putExtra(ConstIntents.INTENT_CLIENT_TO_SELL, sellToClient);

        setResult(RESULT_OK, intentSell);

        super.onBackPressed();
    }


}
