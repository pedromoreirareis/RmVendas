package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.adapter.RetAdapter;
import com.pedromoreirareisgmail.rmvendas.adapter.SaldoAdapter;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoSaldo;

public class SaldoListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LOADER_SALDO = 8;
    private SaldoAdapter mAdapter;

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
        mAdapter = new SaldoAdapter(this, null);
        listView.setAdapter(mAdapter);
        listView.setEmptyView(fab);

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

        getLoaderManager().initLoader(LOADER_SALDO, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AcessoSaldo._ID,
                AcessoSaldo.COLUNA_SALDO_DATA,
                AcessoSaldo.COLUNA_SALDO_VALOR
        };


        return new CursorLoader(
                this,
                AcessoSaldo.CONTENT_URI_SALDO,
                projection,
                null,
                null,
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
