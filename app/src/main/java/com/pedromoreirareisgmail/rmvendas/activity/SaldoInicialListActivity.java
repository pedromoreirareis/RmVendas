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

public class SaldoInicialListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener {

    private static final int LOADER_SALDO_INICIAL_LIST = 0;

    private SaldoInicialAdapter mAdapter;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FloatingActionButton fab;

    private String mDataPesquisarBD = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_list);

        // Trata o botão Flutuante - Abre activity SaldoInicialCadActivity
        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentSaldo = new Intent(SaldoInicialListActivity.this, SaldoInicialCadActivity.class);
                startActivity(intentSaldo);
            }
        });

        //  Faz referencia aos itens do layout
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);
        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);

        //  layout vazio - cadastro sem registros
        tvEmpty.setText(R.string.text_saldo_inicial_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_dinheiro_duas_maos);
        ivEmpty.setContentDescription(getString(R.string.image_desc_saldo_inicial_list_empty));
        listView.setEmptyView(emptyView);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new SaldoInicialAdapter(this);
        listView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        //  Pega data calendário do Dialog
        getDataCalendario();

        // Coloca o titulo e data na Activity, e define data da pesquisa no BD
        setTitle(getString(R.string.title_saldo_inicial_list) + "  " + DataHora.obterFormatarDataBrTitulo());

        // O Loader utiliza mDataPesquisarBD para fazer a pesquisa no banco de dados - "yyyy-MM-dd"
        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        getLoaderManager().initLoader(LOADER_SALDO_INICIAL_LIST, null, this);
    }

    /**
     * Cria o menu
     *
     * @param menu Interface de criação do menu
     * @return Menu inflado
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_data, menu);

        return true;
    }

    /**
     * Define o que fazer ao selecionar um item do menu
     *
     * @param item Item que foi selecionado
     * @return verdadeiro se item foi selecionado
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_data) {

            Dialogos.dialogoDatas(SaldoInicialListActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Define os parametros de pesquisa no BD
     *
     * @param i      Loader responsavel pela pesquisa
     * @param bundle Conjunto de dados em um bundle
     * @return Um Loader com um Cursor com resultado da pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AcessoSaldo._ID,
                AcessoSaldo.DATA,
                AcessoSaldo.VALOR
        };

        // Procura por todos os dados salvos na tabela com parte da data do tipo "yyyy-MM-dd"
        String selection = AcessoSaldo.DATA + " LIKE ?";
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

    /**
     * Define o que fazer com os dados retornados do BD
     *
     * @param loader Define o loader pesquisado
     * @param cursor Cursor com dados da pesquisa
     */
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

    /**
     * Ao reiniciar a pesquisa o que fazer com os dados velhos
     *
     * @param loader Loader com dados antigos
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Click simples no ListView
     * Ao clicar vair abir um Dialog com o valor, saldo inicial e hora do registro
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        String tituloDialog;
        String mensagemDialog;

        tituloDialog = "Saldo Inicial";

        //  Mensagem do Dialog - Descrição
        mensagemDialog = "Valor:    "
                + Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.VALOR)))
                + "\n\n"
                + "Data:    "
                + DataHora.formatarDataBr(cursor.getString(cursor.getColumnIndex(AcessoSaldo.DATA)))
                + "\n\n"
                + "Hora:    "
                + DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(AcessoSaldo.DATA)));


        Dialogos.dialogoExibirDados(SaldoInicialListActivity.this, tituloDialog, mensagemDialog);
    }

    /**

     */

    /**
     * Click Longo no ListView
     * Ao ter um click longo em um item do listview, será indentificado o id, deste item na
     * tabela do banco de dados, e abrirá um dialogo para escolher se será editado ou excluido
     * se for editar será aberta a activity de cadastro para fazer a edição
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     * @return true de click longo foi efetuado com sucesso
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Uri uri = ContentUris.withAppendedId(AcessoSaldo.CONTENT_URI_SALDO_INICIAL, id);

        Cursor cursor = mAdapter.getCursor();

        String dataExcluir = DataHora.formatarDataBr(
                mAdapter.getCursor().getString(
                        cursor.getColumnIndex(AcessoSaldo.DATA)));
        String valorExcluir = Formatar.formatarDoubleParaCurrency(
                mAdapter.getCursor().getDouble(
                        cursor.getColumnIndex(AcessoSaldo.VALOR)));

       /* String mensagemExcluir = getString(R.string.dialog_exc_edit_texto_excluir_saldo_inicial_1) +
                " " +
                dataExcluir +
                getString(R.string.dialog_exc_edit_texto_excluir_saldo_inicial_2) +
                " " +
                valorExcluir; */

        String mensagemExcluir = String.format(getResources().getString(R.string.dialog_exc_edit_texto_excluir_saldo_inicial), dataExcluir, valorExcluir);

        Dialogos.dialogoEditarExcluir(
                SaldoInicialListActivity.this,
                SaldoInicialCadActivity.class,
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

                setTitle(getString(R.string.title_saldo_inicial_list) + "  " + DataHora.dateSetListenerDataBrTitulo(year, month, day));

                getLoaderManager().restartLoader(LOADER_SALDO_INICIAL_LIST, null, SaldoInicialListActivity.this);
            }
        };

    }
}
