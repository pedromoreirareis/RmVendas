package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Calculos;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.adapters.MainAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;

import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_MAIN = 0;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationview;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private View mEmptyView;
    private ListView mListview;

    private MainAdapter mAdapter;

    private String mDataPesquisarBD = null;
    private String mPesquisarBD = "";

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "onCreate");

        initViews();
        emptyLayout();

        // Cria o ToolBar
        setSupportActionBar(mToolbar);

        // Cria o fab - botão flutuante
        mFab.setOnClickListener(this);

        /* Cria o menu de gaveta - Menu lateral
         * Indica que o botão toggle sera adcionado ao menu Drawer e seu estado estara ssicronizado
         * ao menu drawer
         */
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        /* Parte fisica do Drawer, onde realemente fica o itens do menu
         * O Drawer é o ViewGroup e NavigationView é uma view do Drawer
         * Indica que Activity é que vai gerenciar a seleção dos itens de menu do Navigation
         */
        mNavigationview.setNavigationItemSelectedListener(this);

        // Cria o adapter e o ListView
        mAdapter = new MainAdapter(this);
        mListview.setAdapter(mAdapter);
        mListview.setDivider(null);

        // Click longo no Listview e click simples
        mListview.setOnItemLongClickListener(this);
        mListview.setOnItemClickListener(this);

        // Selecionada data no Dialog de calendario
        pegarDataDialogCalendario();

        // Adiciona data como Titulo da Activity
        setTitle(DataHora.obterFormatarDataBrTitulo());

        // Pega data do sistema para iniciar pesquisa no banco de dados
        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        // Inicia Pesquisa no banco de dados
        getLoaderManager().initLoader(LOADER_MAIN, null, this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia os itens Navegação Menu lateral
        mToolbar = findViewById(R.id.toolbar);
        mFab = findViewById(R.id.fab_add);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationview = findViewById(R.id.nav_view);

        // Referencia os itens do layout
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListview = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        Log.v(TAG, "emptyLayout");

        // EmptyView sera acionado se não houver nenhum registro no listview
        mTvEmpty.setText(R.string.text_main_empty);
        mIvEmpty.setImageResource(R.drawable.ic_bolo_fuba);
        mIvEmpty.setContentDescription(getString(R.string.image_desc_main_empty));
        mListview.setEmptyView(mEmptyView);
    }

    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        // Referencia o o Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Se Drawer Estiver aberto então fecha
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.main_search_data, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search_main);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {

            // Item Calendario - Abre para fazer uma pesquisa por data no BD vendas
            case R.id.action_data_main:
                Dialogos.dialogoDatas(MainActivity.this, mDateSetListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Log.v(TAG, "onNavigationItemSelected");

        int id = item.getItemId();

        switch (id) {

            // Click no menu Entrada
            case R.id.nav_action_entrada:
                startActivity(new Intent(MainActivity.this, EntListActivity.class));
                break;

            // Click no menu Retirada
            case R.id.nav_Action_retirada:
                startActivity(new Intent(MainActivity.this, RetListActivity.class));
                break;

            // Click no menu Saldo Inicial
            case R.id.nav_action_saldo_inicial:
                startActivity(new Intent(MainActivity.this, SaldoInicialListActivity.class));
                break;

            // Click no menu Fechamento
            case R.id.nav_action_fechamento:
                startActivity(new Intent(MainActivity.this, FechamentoActivity.class));
                break;

            // Click no menu Produtos
            case R.id.nav_action_list_prod:
                startActivity(new Intent(MainActivity.this, ListProductActivity.class));
                break;

            // Click no menu Clientes
            case R.id.nav_action_list_clientes:
                startActivity(new Intent(MainActivity.this, ClientesListActivity.class));
                break;
        }

        // Apos o click o Drawer é fechado
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Define os parametros de pesquisa no BD
     *
     * @param i      Loader que vai ser usado na pesquisa
     * @param bundle Argumentos do oader para pesquisa
     * @return cursor com dados da pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


        // Define quais colunas retornaram dados
        String[] projection = new String[]{
                EntrySeel._ID,
                EntrySeel.COLUMN_NAME,
                EntrySeel.COLUMN_QUANTITY,
                EntrySeel.COLUMN_TIMESTAMP,
                EntrySeel.COLUMN_DISCOUNT_VALUE,
                EntrySeel.COLUMN_ADD_VALUE,
                EntrySeel.COLUMN_FORWARD_VALUE,
                EntrySeel.COLUMN_CLIENT_ID,
                EntrySeel.COLUMN_PRICE
        };

        String selection;
        String[] selectionArgs;
        String sortOrder;

        /* Dados para pesquisa
         * mPesquisarBD - dado digitados no edit do SearchView
         * mDataPesquisarBD - Data do dia ou data selecionada no item de menu calendario
         *
         * Verifica se há pelo menos 1 caractere em mPesquisarBD, se tiver a pesquisa sera feita
         * utilizando mPesquisarBD e mDataPesquisarBD, sera retornado dados de uma data especifica e
         * que cotenham os caracteres do mPesquisarBD no nome do produto
         *
         * Se mPesquisarBD estiver vazio, trara dados apenas da data que estiver em mDataPesquisarBD
         */
        if (mPesquisarBD.length() > 0) {

            Log.v(TAG, "onCreateLoader - Data + Nome");

            selection = EntrySeel.COLUMN_TIMESTAMP + " LIKE ?  AND " + EntrySeel.COLUMN_NAME + " LIKE ?";
            selectionArgs = new String[]{mDataPesquisarBD + "%", "%" + mPesquisarBD + "%"};
            sortOrder = EntrySeel.COLUMN_TIMESTAMP + " DESC";

        } else {

            Log.v(TAG, "onCreateLoader - Nome (Data ja capturada e em memoria)");

            selection = EntrySeel.COLUMN_TIMESTAMP + " LIKE ?";
            selectionArgs = new String[]{mDataPesquisarBD + "%"};
            sortOrder = EntrySeel.COLUMN_TIMESTAMP + " DESC";
        }

        return new CursorLoader(
                this,
                EntrySeel.CONTENT_URI_SELL,
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

    /*
     * Escolha no calendário uma data que será utilizada para pesquisar no banco de dados. Essa
     * data será formatada para tipo do Brasil e será apresentada no titulo, e iniciará uma
     * pesquisa para verificar se há dados para esta data
     */
    private void pegarDataDialogCalendario() {

        Log.v(TAG, "pegarDataDialogCalendario");

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(DataHora.dateSetListenerDataBrTitulo(year, month, day));

                getLoaderManager().restartLoader(LOADER_MAIN, null, MainActivity.this);
            }
        };
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Log.v(TAG, "onQueryTextSubmit");

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.v(TAG, "onQueryTextChange");

        mPesquisarBD = newText;

        getLoaderManager().restartLoader(LOADER_MAIN, null, MainActivity.this);

        return true;
    }

    @Override
    public void onClick(View view) {

        Log.v(TAG, "onClick");

        if (view.getId() == R.id.fab_add) {

            Intent intentListaProdutosVenda =
                    new Intent(MainActivity.this, VendListActivity.class);
            startActivity(intentListaProdutosVenda);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "ListView onItemClick");

        Cursor cursor = mAdapter.getCursor();

        double valorVendaVista = Calculos.CalcularValorAVistaDouble(
                cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)),
                cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)),
                cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)),
                cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE))
        );

        if (valorVendaVista > 0) {

            Intent intentTroco =
                    new Intent(MainActivity.this, TrocoActivity.class);

            Bundle bundle = new Bundle();
            bundle.putDouble(VALOR_VENDA_TROCO, valorVendaVista);

            intentTroco.putExtras(bundle);

            startActivity(intentTroco);
        }
    }

    /**
     * Click longo no listview
     * Abre Dialog para escolha se deseja editar o registro ou excluir
     *
     * @param parent   layout onde esta cada item do listview
     * @param view     item do listview
     * @param position posição de cada registro no listview
     * @param id       id no BD de um item apresentado no listview
     * @return verdadeiro se foi executado
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Log.v(TAG, "onItemLongClick");

        Uri uri = ContentUris.withAppendedId(EntrySeel.CONTENT_URI_SELL, id);

        Cursor cursor = mAdapter.getCursor();
        String mensagemExcluir = cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)) + "  "
                + cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_NAME));

        Dialogos.dialogoEditarExcluir(
                MainActivity.this,
                VendQuantActivity.class,
                uri,
                mensagemExcluir
        );

        return true;
    }
}
