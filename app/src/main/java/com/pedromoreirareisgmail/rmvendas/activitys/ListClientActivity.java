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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.adapters.ClientAdapter;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.constant.ConstTag;
import com.pedromoreirareisgmail.rmvendas.models.Client;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;

public class ListClientActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener {

    private static final String TAG = ConstTag.TAG_MAIN + ListClientActivity.class.getSimpleName();

    private View mEmptyView;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private FloatingActionButton mFab;

    private ClientAdapter mAdapter;
    private Context mContext;

    private String mSearchDB = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_client);

        Log.v(TAG, "onCreate");

        initViews();
        emptyLayout();
        initListenerAndObject();

        // Obtem e iniciar o gerenciador do carregar de dados
        getLoaderManager().initLoader(ConstLoader.LOADER_LIST_CLIENT, null, this);
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
        mTvEmpty.setText(R.string.text_clientes_list_empty);
        mIvEmpty.setImageResource(R.drawable.ic_money_up); //TODO: mudar icone dos clientes
        mIvEmpty.setContentDescription(getString(R.string.descr_client_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenerAndObject() {

        Log.v(TAG, "initListenerAndObject");

        // Contexto da Activity
        mContext = ListClientActivity.this;

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ClientAdapter(mContext);
        mListView.setAdapter(mAdapter);

        // Listener do botão Flutuante
        mFab.setOnClickListener(this);

        // Clique simples e Longo no ListView
        mListView.setOnItemLongClickListener(this);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(TAG, "onCreateLoader");

        // Colunas que serao retornadas
        String[] projection = {
                EntryClient._ID,
                EntryClient.COLUMN_NAME,
                EntryClient.COLUMN_FONE
        };

        // Coluna onde havera a busca
        String selection = EntryClient.COLUMN_NAME + " LIKE ?";

        // Parametro da busca
        String[] selectionArgs = new String[]{"%" + mSearchDB + "%"};

        // Ordem de retorno dos dados
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

        // Dados da busca serao repassados ao adapter e listview
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

        // Se a busca for redefinida não faz nada
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "onItemClick");

        Uri uri = ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, id);
        Cursor cursor = mAdapter.getCursor();

        Client client = new Client();
        client.setId(id);
        client.setName(cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_NAME)));
        client.setFone(cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_FONE)));

        Intent intentClient = new Intent(
                mContext,
                ListReceiveClientActivity.class
        );

        intentClient.setData(uri);
        intentClient.putExtra(ConstIntents.INTENT_CLIENT_DATA, client);
        startActivity(intentClient);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "onItemLongClick");

        Uri uri = ContentUris.withAppendedId(EntryClient.CONTENT_URI_CLIENT, id);
        Cursor cursor = mAdapter.getCursor();

        String messageDelete = mAdapter.getCursor().getString(
                cursor.getColumnIndex(EntryClient.COLUMN_NAME));

        // Verifica se cliente tem divida antes de excluir
        Messages.editOurDelete(
                mContext,
                RegisterClientActivity.class,
                uri,
                id,
                messageDelete
        );

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Log.v(TAG, "onQueryTextSubmit");

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.v(TAG, "onQueryTextChange");

        mSearchDB = newText;

        getLoaderManager().restartLoader(
                ConstLoader.LOADER_LIST_CLIENT,
                null,
                ListClientActivity.this
        );

        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_add:

                Log.v(TAG, "onClick - FloatingActionButton");

                Intent intentRegisterClient = new Intent(
                        mContext,
                        RegisterClientActivity.class
                );

                Client client = new Client();

                client.setCalled(Const.CALL_LIST_CLIENT);

                intentRegisterClient.putExtra(ConstIntents.INTENT_CALLED_CLIENT, client);
                startActivity(intentRegisterClient);

                break;
        }
    }
}
