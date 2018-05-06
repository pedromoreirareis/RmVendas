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
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.adapters.ProdAdapter;
import com.pedromoreirareisgmail.rmvendas.constantes.ConstTag;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

public class ListProductActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener {

    private static final String TAG = ConstTag.TAG_MAIN + ListProductActivity.class.getSimpleName();
    private static final int LOADER_PRODUTOS_LIST = 0;

    private FloatingActionButton mFab;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;

    private ProdAdapter mAdapter;
    private String mProdutoPesquisarBD = "";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        Log.v(TAG, "onCreate");

        // Instancia o contexto
        mContext = ListProductActivity.this;

        initViews();
        emptyLayout();
        initListenersAndObjects();


        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_PRODUTOS_LIST, null, this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        //  Faz referencia aos itens do layout
        mFab = findViewById(R.id.fab_add);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        Log.v(TAG, "emptyLayout");

        //  layout vazio - cadastro sem registros
        mTvEmpty.setText(R.string.text_produto_list_empty);
        mIvEmpty.setImageResource(R.drawable.ic_contract_list);
        mIvEmpty.setContentDescription(getString(R.string.image_desc_produto_list_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenersAndObjects() {

        Log.v(TAG, "initListenersAndObjects");

        // Trata o botão Flutuante - Abre activity ProdutosCadActivity
        mFab.setOnClickListener(this);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new ProdAdapter(this);
        mListView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Referencia o menu de pesuisa
        MenuItem menuItem = menu.findItem(R.id.action_search);

        //  Instancia o SearchView e diz que quem vai tratar o evento é a Activity
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

        /* retorna todos os produtos cadastrados - A pesquisa inicial traz todos os produtos, se
         * utilizar o menu search, sera pesquisado pelo nome do produto */
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
     * Abre um Dialog com o nome do Produto no Titulo e o valor na mensagem
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "onItemClick");

        Cursor cursor = mAdapter.getCursor();

        String tituloDialog = getString(R.string.dialog_informacao_produto_title);

        //  Mensagem do Dialog - Descrição
        String mensagemDialog = String.format(getResources().getString(R.string.dialog_informacao_produtos_list),
                cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME)),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE))));

        Dialogos.dialogoExibirDados(ListProductActivity.this, tituloDialog, mensagemDialog);
    }

    /**
     * Click longo no ListView
     * Abre um Dialog para escolher se vai editar ou excluir produto
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     * @return true de click longo foi efetuado com sucesso
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "onItemLongClick");

        Uri uri = ContentUris.withAppendedId(EntryProduct.CONTENT_URI_PRODUCT, id);

        Cursor cursor = mAdapter.getCursor();

        String mensagemExcluir = String.format(getResources().getString(R.string.dialog_excluir_produtos_list),
                cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME)),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE))));

        Dialogos.dialogoEditarExcluir(
                ListProductActivity.this,
                ProdutosCadActivity.class,
                uri,
                mensagemExcluir
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

        // Ao digitar, automaticamente iniciar a pesquisa no BD
        mProdutoPesquisarBD = newText;

        //  Reiniciar o gerenciador de dados do BD - Pesquisa novamente e retornando novos dados
        getLoaderManager().restartLoader(LOADER_PRODUTOS_LIST, null, ListProductActivity.this);

        return true;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fab_add) {

            Intent intentCadastroProdutos =
                    new Intent(ListProductActivity.this, ProdutosCadActivity.class);
            startActivity(intentCadastroProdutos);
        }
    }
}
