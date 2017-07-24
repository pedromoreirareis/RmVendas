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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.adapter.EntAdapter;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;

public class EntListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ENT = 1;
    private EntAdapter mAdapter;

    private String mDataPesquisa = "";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ent_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentEntrada = new Intent(EntListActivity.this, EntCadActivity.class);
                startActivity(intentEntrada);
            }
        });

        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);

        tvEmpty.setText(R.string.text_ent_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_money_up);
        ivEmpty.setContentDescription(getString(R.string.image_desc_ent_list_empty));

        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mAdapter = new EntAdapter(this);
        listView.setAdapter(mAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {

                Uri uri = ContentUris.withAppendedId(AcessoEntRet.CONTENT_URI_ENT_RET, id);

                Cursor cur = mAdapter.getCursor();
                String desc = mAdapter.getCursor().getString(
                        cur.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DESC));

                UtilsDialog.editarExcluir(
                        EntListActivity.this,
                        EntCadActivity.class,
                        uri,
                        desc
                );

                return true;
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisa = Datas.dateSetListenerPesquisa(year, month, day);

                setTitle(getString(R.string.title_ent_list) + "  " + Datas.dateSetListenerTitle(year, month, day));

                getLoaderManager().restartLoader(LOADER_ENT, null, EntListActivity.this);

            }
        };

        setTitle(getString(R.string.title_ent_list) + "  " + Datas.getDate());

        mDataPesquisa = Datas.formatDatePesquisa(Datas.getDateTime());

        getLoaderManager().initLoader(LOADER_ENT, null, this);
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

            UtilsDialog.dialogData(EntListActivity.this, mDateSetListener);
        }
        return super.onOptionsItemSelected(item);
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

        String selection = AcessoEntRet.COLUNA_ENT_RET_TIPO + " =? AND " + AcessoEntRet.COLUNA_ENT_RET_DATA + " LIKE ?";
        String[] selectionArgs = new String[]{String.valueOf(Constantes.TIPO_ENTRADA), mDataPesquisa + "%"};
        String sortOrder = AcessoEntRet.COLUNA_ENT_RET_DATA;

        return new CursorLoader(
                this,
                AcessoEntRet.CONTENT_URI_ENT_RET,
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
