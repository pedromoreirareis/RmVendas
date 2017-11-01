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
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.adapter.MainAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoVenda;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_MAIN = 0;

    private MainAdapter mAdapter;

    private String mDataPesquisarBD = "";
    private String mProdutoPesquisarBD = "";

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabProdutos = (FloatingActionButton) findViewById(R.id.fab_add);
        fabProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, VendListActivity.class);
                intent.putExtra(Constantes.VENDA_ADICIONAR, Constantes.VENDA_ADICIONAR);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);

        tvEmpty.setText(R.string.text_main_empty);
        ivEmpty.setImageResource(R.drawable.ic_bolo_fuba);
        ivEmpty.setContentDescription(getString(R.string.image_desc_main_empty));

        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mAdapter = new MainAdapter(this);
        listView.setAdapter(mAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {

                Uri uri = ContentUris.withAppendedId(AcessoVenda.CONTENT_URI_VENDA, id);

                Cursor cursor = mAdapter.getCursor();
                String mensagemExcluir = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_QUANTIDADE_VENDIDA)) + "  "
                        + cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDAS_NOME_PRODUTO));

                Dialogos.dialogoEditarExcluir(
                        MainActivity.this,
                        VendQuantActivity.class,
                        uri,
                        mensagemExcluir
                );

                return true;
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(DataHora.dateSetListenerDataBrTitulo(year, month, day));

                getLoaderManager().restartLoader(LOADER_MAIN, null, MainActivity.this);
            }
        };

        setTitle(DataHora.obterFormatarDataBrTitulo());

        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        getLoaderManager().initLoader(LOADER_MAIN, null, this);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search_main);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mProdutoPesquisarBD = newText;

                getLoaderManager().restartLoader(LOADER_MAIN, null, MainActivity.this);

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_data_main:
                Dialogos.dialogoDatas(MainActivity.this, mDateSetListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.nav_action_entrada:
                startActivity(new Intent(MainActivity.this, EntListActivity.class));
                break;

            case R.id.nav_Action_retirada:
                startActivity(new Intent(MainActivity.this, RetListActivity.class));
                break;

            case R.id.nav_action_saldo_inicial:
                startActivity(new Intent(MainActivity.this, SaldoInicialListActivity.class));
                break;

            case R.id.nav_action_fechamento:
                startActivity(new Intent(MainActivity.this, FechamentoActivity.class));
                break;

            case R.id.nav_action_list_prod:
                startActivity(new Intent(MainActivity.this, ProdutosListActivity.class));
                break;

            case R.id.nav_action_list_clientes:
                startActivity(new Intent(MainActivity.this, ClientesListActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = new String[]{
                AcessoVenda._ID,
                AcessoVenda.COLUNA_VENDAS_NOME_PRODUTO,
                AcessoVenda.COLUNA_VENDAS_QUANTIDADE_VENDIDA,
                AcessoVenda.COLUNA_VENDAS_DATA,
                AcessoVenda.COLUNA_VENDAS_VALOR_TOTAL_VENDA,
                AcessoVenda.COLUNA_VENDAS_TEM_DESCONTO,
                AcessoVenda.COLUNA_VENDAS_TEM_COBERTURA,
                AcessoVenda.COLUNA_VENDAS_VALOR_DESCONTO,
                AcessoVenda.COLUNA_VENDAS_VALOR_COBERTURA,
                AcessoVenda.COLUNA_VENDAS_A_PRAZO,
                AcessoVenda.COLUNA_VENDAS_VALOR_UMA_UNIDADE_PRODUTO
        };

        String selection;
        String[] selectionArgs;
        String sortOrder;

        if (mProdutoPesquisarBD.length() > 0) {

            selection = AcessoVenda.COLUNA_VENDAS_DATA + " LIKE ?  AND " + AcessoVenda.COLUNA_VENDAS_NOME_PRODUTO + " LIKE ?";
            selectionArgs = new String[]{mDataPesquisarBD + "%", "%" + mProdutoPesquisarBD + "%"};
            sortOrder = AcessoVenda.COLUNA_VENDAS_DATA;

        } else {

            selection = AcessoVenda.COLUNA_VENDAS_DATA + " LIKE ?";
            selectionArgs = new String[]{mDataPesquisarBD + "%"};
            sortOrder = AcessoVenda.COLUNA_VENDAS_DATA;
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
