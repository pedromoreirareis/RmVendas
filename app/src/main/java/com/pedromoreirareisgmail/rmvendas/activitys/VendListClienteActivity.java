package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
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
import com.pedromoreirareisgmail.rmvendas.adapters.ClientesAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoClientes;

import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.ACTIVITY_CHAMOU;
import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.VEND_LIST_CLIENTES_ACTIVITY;


public class VendListClienteActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener {

    private static final String TAG = VendListClienteActivity.class.getSimpleName();
    private static final int LOADER_VEND_CLIENTES_LIST = 0;

    private static final String URI_ATUAL = "uri_atual";
    private static final String VALOR_UNIDADE = "valor_unidade";
    private static final String ID_CLIENTE = "id_cliente";

    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;
    private FloatingActionButton mFab;

    private ClientesAdapter mAdapter;
    private String mUri;
    private String mValorUnidade;
    private String mPesquisar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_list);

        Log.v(TAG, "onCreate");

        initViews();
        emptyLayout();

        Intent intentDadosVenda = getIntent();

        if (intentDadosVenda.hasExtra(URI_ATUAL)) {

            mUri = intentDadosVenda.getStringExtra(URI_ATUAL);
        }

        if (intentDadosVenda.hasExtra(VALOR_UNIDADE)) {

            mValorUnidade = intentDadosVenda.getStringExtra(VALOR_UNIDADE);
        }

        // Trata o botão Flutuante - Abre activity EntCadActivity
        mFab.setOnClickListener(this);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ClientesAdapter(this);
        mListView.setAdapter(mAdapter);

        // Clique simples no ListView
        mListView.setOnItemClickListener(this);

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_VEND_CLIENTES_LIST, null, this);

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

        //TODO: TROCAR ICONE DE CLIENTE ALGO VERMELHO
        // Layout vazio - Cadastro sem registros
        mTvEmpty.setText(R.string.text_clientes_list_empty_pesquisa);
        mIvEmpty.setImageResource(R.drawable.ic_money_up);
        mIvEmpty.setContentDescription(getString(R.string.image_desc_clientes_list_empty));
        mListView.setEmptyView(mEmptyView);
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
                AcessoClientes._ID,
                AcessoClientes.NOME,
                AcessoClientes.TELEFONE
        };

        /* retorna todos os produtos cadastrados - A pesquisa inicial traz todos os produtos, se
         * utilizar o menu search, sera pesquisado pelo nome do produto */
        String selection = AcessoClientes.NOME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + mPesquisar + "%"};
        String sortOrder = AcessoClientes.NOME;

        return new CursorLoader(
                this,
                AcessoClientes.CONTENT_URI_CLIENTES,
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

        mPesquisar = newText;

        getLoaderManager().restartLoader(LOADER_VEND_CLIENTES_LIST, null, VendListClienteActivity.this);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Log.v(TAG, "onItemClick");

        Cursor cursor = mAdapter.getCursor();

        String idCliente = cursor.getString(cursor.getColumnIndex(AcessoClientes._ID));

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

            Log.v(TAG, "FloatingActionButton");

            Intent intentCadastroClientes =
                    new Intent(VendListClienteActivity.this, ClientesCadActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(ACTIVITY_CHAMOU, VEND_LIST_CLIENTES_ACTIVITY);

            intentCadastroClientes.putExtras(bundle);

            startActivity(intentCadastroClientes);
        }
    }
}
