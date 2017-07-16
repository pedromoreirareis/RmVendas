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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.adapter.SaldoAdapter;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoSaldo;

public class SaldoListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_SALDO = 11;
    private SaldoAdapter mAdapter;

    private String mDataPesquisar = "";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_saldo_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentSaldo = new Intent(SaldoListActivity.this, SaldoCadActivity.class);
                startActivity(intentSaldo);
            }
        });

        ListView listView = (ListView) findViewById(R.id.listview_saldo_list);
        View emptyView = findViewById(R.id.empty_view_saldo_list);
        mAdapter = new SaldoAdapter(this);

        listView.setEmptyView(emptyView);
        listView.setAdapter(mAdapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {

                Uri uri = ContentUris.withAppendedId(AcessoSaldo.CONTENT_URI_SALDO, id);

                Cursor cur = mAdapter.getCursor();
                String desc = mAdapter.getCursor().getString(cur.getColumnIndex(AcessoSaldo.COLUNA_SALDO_VALOR));

                UtilsDialog.editarExcluir(
                        SaldoListActivity.this,
                        SaldoCadActivity.class,
                        uri,
                        desc
                );

                return true;
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisar = Datas.dateSetListenerString(year, month, day);

                setTitle(getString(R.string.title_saldo_list) + "  " + Datas.dateSetListenerInverseString(year, month, day));

                getLoaderManager().restartLoader(LOADER_SALDO, null, SaldoListActivity.this);
            }
        };

        setTitle(getString(R.string.title_saldo_list) + "  " + Datas.getDate());

        mDataPesquisar = Datas.formatDatePesquisa(Datas.getDateTime());

        getLoaderManager().initLoader(LOADER_SALDO, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_data) {

            UtilsDialog.dialogData(SaldoListActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoSaldo._ID,
                AcessoSaldo.COLUNA_SALDO_DATA,
                AcessoSaldo.COLUNA_SALDO_VALOR
        };

        String selection = AcessoSaldo.COLUNA_SALDO_DATA + " LIKE ?";
        String[] selectionArgs = new String[]{mDataPesquisar + "%"};


        return new CursorLoader(
                this,
                AcessoSaldo.CONTENT_URI_SALDO,
                projection,
                selection,
                selectionArgs,
                null
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
