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
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.adapter.SaldoInicialAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoSaldo;

public class SaldoInicialListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_SALDO_INICIAL_LIST = 0;

    private SaldoInicialAdapter mAdapter;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FloatingActionButton fab;

    private String mDataPesquisarBD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_list);

        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentSaldo = new Intent(SaldoInicialListActivity.this, SaldoInicialCadActivity.class);
                startActivity(intentSaldo);
            }
        });

        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);

        tvEmpty.setText(R.string.text_saldo_inicial_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_dinheiro_duas_maos);
        ivEmpty.setContentDescription(getString(R.string.image_desc_saldo_inicial_list_empty));

        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);
        mAdapter = new SaldoInicialAdapter(this);

        listView.setEmptyView(emptyView);
        listView.setAdapter(mAdapter);


        /**
         * Ao ter um clique longo em um item do listview, será indentificado o id, deste item na
         * tabela do banco de dados, e abrirá um dialogo para escolher se será editado ou excluido
         * se for editar será aberta a activity de cadastro para fazer a edição
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {

                Uri uri = ContentUris.withAppendedId(AcessoSaldo.CONTENT_URI_SALDO_INICIAL, id);

                Cursor cursor = mAdapter.getCursor();

                String dataExcluir = DataHora.formatarDataBr(
                        mAdapter.getCursor().getString(
                                cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_INICIAL_DATA)));
                String valorExcluir = Formatar.formatarDoubleParaCurrency(
                        mAdapter.getCursor().getDouble(
                                cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_INICIAL_VALOR)));

                String mensagemExcluir = getString(R.string.dialog_exc_edit_texto_excluir_saldo_inicial_1) +
                        " " +
                        dataExcluir +
                        getString(R.string.dialog_exc_edit_texto_excluir_saldo_inicial_2) +
                        " " +
                        valorExcluir;


                Dialogos.dialogoEditarExcluir(
                        SaldoInicialListActivity.this,
                        SaldoInicialCadActivity.class,
                        uri,
                        mensagemExcluir
                );

                return true;
            }
        });

        /**
         * Escolha no calendário uma data que será utilizada para pesquisar no banco de dados. Essa
         * data será formatada para tipo do Brasil e será apresentada no titulo, e iniciará uma
         * pesquisa para verificar se há dados para esta data
         */
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(getString(R.string.title_saldo_list) + "  " + DataHora.dateSetListenerDataBrTitulo(year, month, day));

                getLoaderManager().restartLoader(LOADER_SALDO_INICIAL_LIST, null, SaldoInicialListActivity.this);
            }
        };

        setTitle(getString(R.string.title_saldo_list) + "  " + DataHora.obterFormatarDataBrTitulo());

        // O Loader utiliza mDataPesquisarBD para fazer a pesquisa no banco de dados - "yyyy-MM-dd"
        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        getLoaderManager().initLoader(LOADER_SALDO_INICIAL_LIST, null, this);
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

            Dialogos.dialogoDatas(SaldoInicialListActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoSaldo._ID,
                AcessoSaldo.COLUNA_SALDO_INICIAL_DATA,
                AcessoSaldo.COLUNA_SALDO_INICIAL_VALOR
        };

        // Procura por todos os dados salvos na tabela com parte da data do tipo "yyyy-MM-dd"
        String selection = AcessoSaldo.COLUNA_SALDO_INICIAL_DATA + " LIKE ?";
        String[] selectionArgs = new String[]{mDataPesquisarBD + "%"};

        return new CursorLoader(
                this,
                AcessoSaldo.CONTENT_URI_SALDO_INICIAL,
                projection,
                selection,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);

        /* Se encontrar pelo menos um dado salvo para a data o FloatingActionButton deve ficar
           invisivel, se não tiver nenhum dado deve ficar visivel */
        if (mAdapter.getCount() > 0) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
