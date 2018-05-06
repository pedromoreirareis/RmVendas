package com.pedromoreirareisgmail.rmvendas.activitys;

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
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.adapters.ClientesAdapter;
import com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents;

import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.ACTIVITY_CHAMOU;
import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.CLIENTE_FONE;
import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.CLIENTE_ID;
import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.CLIENTE_NOME;
import static com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoClientes;

public class ClientesListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener {

    private static final int LOADER_CLIENTES_LIST = 0;

    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;
    private FloatingActionButton mFab;

    private ClientesAdapter mAdapter;

    private String mPesquisar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_list);

        initViews();
        emptyLayout();

        // Trata o botão Flutuante - Abre activity EntCadActivity
        mFab.setOnClickListener(this);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ClientesAdapter(this);
        mListView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_CLIENTES_LIST, null, this);
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
        mTvEmpty.setText(R.string.text_clientes_list_empty);
        mIvEmpty.setImageResource(R.drawable.ic_money_up);
        mIvEmpty.setContentDescription(getString(R.string.image_desc_clientes_list_empty));
        mListView.setEmptyView(mEmptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                AcessoClientes._ID,
                AcessoClientes.NOME,
                AcessoClientes.TELEFONE
        };

        /* retorna todos os produtos cadastrados - A pesquisa inicial traz todos os produtos, se
         * utilizar o menu search, sera pesquisado pelo nome do produto
         */
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

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }

    /**
     * Click simples no ListView
     * Ao clicar ira abrir a {@link RegistroReceberActivity} para fazer edição de um registro de
     * um cliente ou para adicionar um novo registro
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // No cursor pode-se obter os dados de cada cliente
        Cursor cursor = mAdapter.getCursor();
                
        String nomeCliente = cursor.getString(cursor.getColumnIndex(AcessoClientes.NOME));
        String foneCliente = cursor.getString(cursor.getColumnIndex(AcessoClientes.TELEFONE));

        Intent intentRegistroAReceber = new Intent(
                ClientesListActivity.this, RegistroReceberActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(CLIENTE_ID, String.valueOf(id));
        bundle.putString(CLIENTE_NOME, nomeCliente);
        bundle.putString(CLIENTE_FONE, foneCliente);

        intentRegistroAReceber.putExtras(bundle);
        startActivity(intentRegistroAReceber);
    }

    /**
     * Click longo no ListView
     * Ao ter um click longo sera aberto um Dialog para escolher se deve editar ou excluir o registro
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        //TODO: Ao excluir tem de fazer verificação se o cliente tem vendas associadas ou registro a prazo - se tiver não pode excluir

        // Caminho especifico de um cliente no BD
        Uri uri = ContentUris.withAppendedId(AcessoClientes.CONTENT_URI_CLIENTES, id);

        Cursor cursor = mAdapter.getCursor();

        String mensagemExcluir = mAdapter.getCursor().getString(
                cursor.getColumnIndex(AcessoClientes.NOME));

        Dialogos.dialogoEditarExcluir(
                ClientesListActivity.this,
                ClientesCadActivity.class,
                uri,
                mensagemExcluir
        );

        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mPesquisar = newText;

        getLoaderManager().restartLoader(LOADER_CLIENTES_LIST, null, ClientesListActivity.this);

        return true;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fab_add) {

            Intent intentCadastroClientes =
                    new Intent(ClientesListActivity.this, ClientesCadActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(ACTIVITY_CHAMOU, ConstIntents.CLIENTES_LIST_ACTIVITY);

            intentCadastroClientes.putExtras(bundle);

            startActivity(intentCadastroClientes);
        }
    }
}
