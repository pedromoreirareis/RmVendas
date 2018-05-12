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
import android.util.Log;
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
import com.pedromoreirareisgmail.rmvendas.constant.ConstTag;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;
import com.pedromoreirareisgmail.rmvendas.models.Client;


public class ListClientSaleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener {

    private static final String TAG = ConstTag.TAG_MAIN + ListClientSaleActivity.class.getSimpleName();

    private static final String URI_ATUAL = "uri_atual";
    private static final String VALOR_UNIDADE = "valor_unidade";
    private static final String ID_CLIENTE = "id_cliente";

    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;
    private FloatingActionButton mFab;

    private Context mContext;
    private ClientAdapter mAdapter;
    private String mUri;
    private String mValorUnidade;
    private String mSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_client);

        Log.v(TAG, "onCreate");

        initViews();
        emptyLayout();
        initListenerAndObject();

        Intent intentDadosVenda = getIntent();

        if (intentDadosVenda.hasExtra(URI_ATUAL)) {

            mUri = intentDadosVenda.getStringExtra(URI_ATUAL);
        }

        if (intentDadosVenda.hasExtra(VALOR_UNIDADE)) {

            mValorUnidade = intentDadosVenda.getStringExtra(VALOR_UNIDADE);
        }

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(ConstLoader.LOADER_LIST_CLIENT_SALE, null, this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mFab = findViewById(R.id.fab_add);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        Log.v(TAG, "emptyLayout");

        // Layout vazio - Cadastro sem registros
        mTvEmpty.setText(R.string.text_client_search_empty);
        mIvEmpty.setImageResource(R.drawable.ic_money_up); //TODO: mudar icone dos clientes TROCAR ICONE DE CLIENTE ALGO VERMELHO
        mIvEmpty.setContentDescription(getString(R.string.descr_client_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenerAndObject() {

        // Contexto da Activty
        mContext = ListClientSaleActivity.this;

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ClientAdapter(mContext);
        mListView.setAdapter(mAdapter);

        // Listener do botão Flutuante
        mFab.setOnClickListener(this);

        // Clique simples no ListView
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {


            case android.R.id.home:

                Log.v(TAG, "onOptionsItemSelected - android.R.id.home");

                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                // TODO: implemntar junto com a venda
                bundle.putString(URI_ATUAL, mUri);
                bundle.putString(VALOR_UNIDADE, mValorUnidade);

                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);

                finish();

                return true;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(TAG, "onCreateLoader");

        String[] projection = {
                EntryClient._ID,
                EntryClient.COLUMN_NAME,
                EntryClient.COLUMN_FONE
        };

        String selection = EntryClient.COLUMN_NAME + " LIKE ?";
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

        Log.v(TAG, "onLoadFinished");

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.v(TAG, "onQueryTextChange");

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


        Log.v(TAG, "onItemClick");

        Cursor cursor = mAdapter.getCursor();

        String idCliente = cursor.getString(cursor.getColumnIndex(EntryClient._ID));

        //TODO: ver como implementar

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString(ID_CLIENTE, idCliente);
        bundle.putString(URI_ATUAL, mUri);
        bundle.putString(VALOR_UNIDADE, mValorUnidade);

        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.fab_add) {

            Log.v(TAG, "OnClick - FloatingActionButton");

            Intent intentRegisterClient = new Intent(
                    ListClientSaleActivity.this,
                    RegisterClientActivity.class
            );

            Client client = new Client();

            client.setCalled(Const.CALL_LIST_CLIENT_SALE);

            intentRegisterClient.putExtra(ConstIntents.INTENT_CALLED_CLIENT, client);

            startActivity(intentRegisterClient);
        }
    }
}
