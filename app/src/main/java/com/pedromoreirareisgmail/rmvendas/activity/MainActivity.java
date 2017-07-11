package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ENTRADA = 3;
    private static final int LOADER_RETIRADA = 4;
    public static String dataEscolhida = "";
    public TextView mTvData;
    public FragmentManager mFragManager;
    private TextView mTvEntrada;
    private TextView mTvRetirada;
    private TextView mTvTotal;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dataEscolhida = Datas.getDate();

        mTvEntrada = (TextView) findViewById(R.id.tv_entrada);
        mTvRetirada = (TextView) findViewById(R.id.tv_retirada);
        mTvTotal = (TextView) findViewById(R.id.tv_total);
        mTvData = (TextView) findViewById(R.id.tv_data);
        mTvData.setText(dataEscolhida);
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
                mTvData.setText(date);
            }
        };

        getLoaderManager().initLoader(LOADER_ENTRADA, null, this);

        mFragManager = getSupportFragmentManager();
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
                startActivity(new Intent(MainActivity.this, ProdutosSelecActivity.class));
                break;

            case R.id.nav_action_entrada:
                startActivity(new Intent(MainActivity.this,EntListActivity.class));
                break;

            case R.id.nav_Action_retirada:
                startActivity(new Intent(MainActivity.this,RetListActivity.class));
                break;
            case R.id.nav_action_saldo_inicial:
               /* Intent intentSaldoInicial = new Intent(MainActivity.this, );
                intentSaldoInicial.putExtra(Constantes.ACTION_SALDO_INICIAL, Constantes.ACTION_SALDO_INICIAL);
                startActivity(intentSaldoInicial);*/
                break;
            case R.id.nav_action_list_prod:
                startActivity(new Intent(MainActivity.this, ProdutosListActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


        String[] projection = {
                AcessoEntRet._ID,
                AcessoEntRet.COLUNA_ENT_RET_DATA,
                AcessoEntRet.COLUNA_ENT_RET_VALOR,
                AcessoEntRet.COLUNA_ENT_RET_DESC,
                AcessoEntRet.COLUNA_ENT_RET_TIPO
        };

        return new CursorLoader(
                this,
                AcessoEntRet.CONTENTENT_URI_ENT_RET,
                projection,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        cursor.moveToFirst();

        double totalEntrada = 0;
        double totalRetirada = 0;

        for (int i = 0; i < cursor.getCount(); i++) {


            if (Constantes.TIPO_ENTRADA ==
                    cursor.getInt(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_TIPO))) {

                totalEntrada = totalEntrada + cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
            }

            if (Constantes.TIPO_RETIRADA ==
                    cursor.getInt(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_TIPO))) {

                totalRetirada = totalRetirada + cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
            }
            cursor.moveToNext();
        }

        mTvEntrada.setText(String.valueOf(totalEntrada));
        mTvRetirada.setText(String.valueOf(totalRetirada));
        mTvTotal.setText(String.valueOf(totalEntrada - totalRetirada));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
