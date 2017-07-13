package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.adapter.MainAdapter;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoVenda;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_MAIN = 15;

    private MainAdapter mAdapter;
    private TextView mTvData;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String mDataPesquisar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabProdutos = (FloatingActionButton) findViewById(R.id.fab_main);
        fabProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VendListActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTvData = (TextView) findViewById(R.id.tv_data_main);

        ListView listView = (ListView) findViewById(R.id.listView_main);
        View emptyView = findViewById(R.id.empty_view_main);
        listView.setEmptyView(emptyView);

        mAdapter = new MainAdapter(this, null);
        listView.setAdapter(mAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {

                Uri uri = ContentUris.withAppendedId(AcessoVenda.CONTENT_URI_VENDA, id);

                Cursor cursor = mAdapter.getCursor();
                String desc = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_QUANT)) + "  "
                        + cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_NOME_PROD));

                UtilsDialog.editarExcluir(
                        MainActivity.this,
                        VendQuantActivity.class,
                        uri,
                        getString(R.string.dialog_prod_list_esc_ee_tilte),
                        getString(R.string.dialog_prod_list_esc_ee_conf_excluir_title),
                        getString(R.string.dialog_prod_list_esc_ee_conf_excluir_cancelar),
                        getString(R.string.dialog_prod_list_esc_ee_conf_excluir_excluir),
                        getString(R.string.dialog_prod_list_esc_ee_excluido_sucesso),
                        getString(R.string.dialog_prod_list_esc_ee_excluido_erro),
                        desc
                );

                return true;
            }
        });

        mTvData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DialogFragment newFragment = new DatePickerFragment();
                // newFragment.show(getSupportFragmentManager(), "datePicker");

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                // Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = day + "/" + month + "/" + year;


                String yearString = String.valueOf(year);
                String monthString = "";
                String dayString = "";

                if (month < 10) {
                    monthString = "0" + String.valueOf(month);
                } else {
                    monthString = String.valueOf(month);
                }

                if (day < 10) {
                    dayString = "0" + String.valueOf(day);
                } else {
                    dayString = String.valueOf(day);
                }

                mDataPesquisar = yearString + "-" + monthString + "-" + dayString;
                mTvData.setText(date);

                getLoaderManager().restartLoader(LOADER_MAIN, null, MainActivity.this);
            }
        };

        mDataPesquisar = Datas.formatDatePesquisa(Datas.getDateTime());
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_action_vender:
                //  startActivity(new Intent(MainActivity.this, ));
                break;

            case R.id.nav_action_entrada:
                startActivity(new Intent(MainActivity.this, EntListActivity.class));
                break;

            case R.id.nav_Action_retirada:
                startActivity(new Intent(MainActivity.this, RetListActivity.class));
                break;
            case R.id.nav_action_saldo_inicial:
                startActivity(new Intent(MainActivity.this, SaldoListActivity.class));
                break;
            case R.id.nav_action_list_prod:
                startActivity(new Intent(MainActivity.this, ProdutosListActivity.class));
                break;
            case R.id.nav_action_fechamento:
                startActivity(new Intent(MainActivity.this, FechamentoActivity.class));
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
                AcessoVenda.COLUNA_VENDA_NOME_PROD,
                AcessoVenda.COLUNA_VENDA_QUANT,
                AcessoVenda.COLUNA_VENDA_DATA,
                AcessoVenda.COLUNA_VENDA_VALOR_PROD,
                AcessoVenda.COLUNA_VENDA_TEM_DESCONTO,
                AcessoVenda.COLUNA_VENDA_TEM_COBERTURA,
                AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO,
                AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA
        };

        String selection = AcessoVenda.COLUNA_VENDA_DATA + " LIKE ?";
        String[] selectionArgs = new String[]{mDataPesquisar + "%"};
        String sortOrder = AcessoVenda.COLUNA_VENDA_DATA;


        /*

        Posso salvar apenas a data - sem as horas - e fazer sortOrder pelo _ID

         */

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
