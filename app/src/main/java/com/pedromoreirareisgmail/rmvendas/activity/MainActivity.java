package com.pedromoreirareisgmail.rmvendas.activity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.adapter.MainAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoVenda;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        SearchView.OnQueryTextListener {

    private static final int LOADER_MAIN = 0;

    private MainAdapter mAdapter;

    private String mDataPesquisarBD = null;
    private String mPesquisarBD = "";

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cria o ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Cria o fab - botão flutuante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentListaProdutos = new Intent(MainActivity.this, VendListActivity.class);
                startActivity(intentListaProdutos);
            }
        });

        /* Cria o menu de gaveta - Menu lateral
         * Indica que o botão toggle sera adcionado ao menu Drawer e seu estado estara ssicronizado
         * ao menu drawer
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* Parte fisica do Drawer, onde realemente fica o itens do menu
         * O Drawer é o ViewGroup e NavigationView é uma view do Drawer
         * Indica que Activity é que vai gerenciar a seleção dos itens de menu do Navigation
         */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Referencia os itens do layout
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);
        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);

        // EmptyView sera acionado se não houver nenhum registro no listview
        tvEmpty.setText(R.string.text_main_empty);
        ivEmpty.setImageResource(R.drawable.ic_bolo_fuba);
        ivEmpty.setContentDescription(getString(R.string.image_desc_main_empty));
        listView.setEmptyView(emptyView);

        // Cria o adapter e o ListView
        mAdapter = new MainAdapter(this);
        listView.setAdapter(mAdapter);

        // Click longo no Listview
        listView.setOnItemLongClickListener(this);

        // Selecionada data no Dialog de calendario
        getDataCalendario();

        // Adiciona data como Titulo da Activity
        setTitle(DataHora.obterFormatarDataBrTitulo());

        // Pega data do sistema para iniciar pesquisa no banco de dados
        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        // Inicia Pesquisa no banco de dados
        getLoaderManager().initLoader(LOADER_MAIN, null, this);
    }

    /* Se o Drawer estiver aberto (Menu lateral estiver aberto), então fecha ele
     * Se ele estiver fechado, o fucionamento do BackPressed é normal, ele fecha o App
     */
    @Override
    public void onBackPressed() {

        // Referencia o o Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Se Drawer Estiver aberto então fecha
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();
        }
    }

    /**
     * Cria o menu e o SearchView para pesquisas
     *
     * @param menu objeto de menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_search_data, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search_main);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return true;
    }

    /**
     * Identifica item selecionado no menu
     *
     * @param item item que foi selecionado no menu
     * @return verdadeiro se item foi selecionado e a ação teve sucesso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Item Calendario - Abre para fazer uma pesquisa por data no BD vendas
            case R.id.action_data_main:
                Dialogos.dialogoDatas(MainActivity.this, mDateSetListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Monitora a seleção de itens de menu do Drawer Navigation
     * A selecionar um item, captura essa selação e lança um ação definida
     *
     * @param item item selecionado no Navigation do Drawer
     * @return verdadeiro se item foi selecionado e ação foi bem sucedida
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
                startActivity(new Intent(MainActivity.this, ProdutosListActivity.class));
                break;

            // Click no menu Clientes
            case R.id.nav_action_list_clientes:
                startActivity(new Intent(MainActivity.this, ClientesListActivity.class));
                break;

        }

        // Apos o click o Drawer é fechado
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                AcessoVenda._ID,
                AcessoVenda.NOME_PRODUTO,
                AcessoVenda.QUANTIDADE_VENDIDA,
                AcessoVenda.DATA,
                AcessoVenda.VALOR_TOTAL_VENDA,
                AcessoVenda.TEM_DESCONTO,
                AcessoVenda.TEM_ADICIONAL,
                AcessoVenda.VALOR_DESCONTO,
                AcessoVenda.VALOR_ADICIONAL,
                AcessoVenda.A_PRAZO,
                AcessoVenda.VALOR_UMA_UNIDADE_PRODUTO
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

            selection = AcessoVenda.DATA + " LIKE ?  AND " + AcessoVenda.NOME_PRODUTO + " LIKE ?";
            selectionArgs = new String[]{mDataPesquisarBD + "%", "%" + mPesquisarBD + "%"};
            sortOrder = AcessoVenda.DATA;

        } else {

            selection = AcessoVenda.DATA + " LIKE ?";
            selectionArgs = new String[]{mDataPesquisarBD + "%"};
            sortOrder = AcessoVenda.DATA;
        }

        return new CursorLoader(
                this,
                AcessoVenda.CONTENT_URI_VENDA,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    /**
     * Define o que fazer com os dados retornados da pesquisa
     *
     * @param loader Define o loader pesquisado
     * @param cursor Cursor com dados da pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    /**
     * Define o que sera feito com dados antigos ao iniciar uma nova pesquisa
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Click longo no listview
     * Abre Dialog para escolha se deseja editar o registro ou excluir
     *
     * @param parent   layout onde esta cada item do listview
     * @param view     item do listview
     * @param position posição de cada registro no listview
     * @param id       id no BD de um item apresentado no listview
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Uri uri = ContentUris.withAppendedId(AcessoVenda.CONTENT_URI_VENDA, id);

        Cursor cursor = mAdapter.getCursor();
        String mensagemExcluir = cursor.getString(cursor.getColumnIndex(AcessoVenda.QUANTIDADE_VENDIDA)) + "  "
                + cursor.getString(cursor.getColumnIndex(AcessoVenda.NOME_PRODUTO));

        Dialogos.dialogoEditarExcluir(
                MainActivity.this,
                VendQuantActivity.class,
                uri,
                mensagemExcluir
        );

        return true;
    }

    /*
     * Escolha no calendário uma data que será utilizada para pesquisar no banco de dados. Essa
     * data será formatada para tipo do Brasil e será apresentada no titulo, e iniciará uma
     * pesquisa para verificar se há dados para esta data
     */
    private void getDataCalendario() {

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(DataHora.dateSetListenerDataBrTitulo(year, month, day));

                getLoaderManager().restartLoader(LOADER_MAIN, null, MainActivity.this);
            }
        };
    }

    /**
     * Pesquisar pelo SearchView apos digitar e clicar no menu de pesquisa
     *
     * @param query Texto digitado no editdo SearchView
     * @return verdadeiro se foi clicado no menu de pesquisa
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Pesquisa ao alterar texto digitado no menu SearchView
     *
     * @param newText Texto digitado no edit do menu SearchView
     * @return verdadeiro se pesquisa foi realizado com sucesso
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        mPesquisarBD = newText;

        getLoaderManager().restartLoader(LOADER_MAIN, null, MainActivity.this);

        return true;
    }

}
