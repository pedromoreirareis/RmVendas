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
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.adapter.RetAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoEntRet;

public class RetListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener {

    private static final int LOADER_RETIRADA_LIST = 0;

    private RetAdapter mAdapter;

    private String mDataPesquisarBD = null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_list);

        // Trata o botão Flutuante - Abre activity RetCadActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentRetirada = new Intent(RetListActivity.this, RetCadActivity.class);
                startActivity(intentRetirada);
            }
        });

        // Referencia itens do layout
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_view);
        ImageView ivEmpty = (ImageView) findViewById(R.id.iv_empty_view);
        ListView listView = (ListView) findViewById(R.id.lv_list);
        View emptyView = findViewById(R.id.empty_view);

        // Layout vazio - Cadastro sem registros
        tvEmpty.setText(R.string.text_ret_list_empty);
        ivEmpty.setImageResource(R.drawable.ic_money_down);
        ivEmpty.setContentDescription(getString(R.string.image_desc_ret_list_empty));
        listView.setEmptyView(emptyView);


        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new RetAdapter(this);
        listView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        //  Pega data do Dialog de calendário
        getDataCalendario();

        // Coloca o titulo e data na Activity, e define data da pesquisa no BD
        setTitle(getString(R.string.title_ret_list) + "  " + DataHora.obterFormatarDataBrTitulo());
        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_RETIRADA_LIST, null, this);
    }

    /**
     * Cria o menu
     *
     * @param menu Interface de criação do menu
     * @return Menu inflado
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
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

            Dialogos.dialogoDatas(RetListActivity.this, mDateSetListener);
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
                AcessoEntRet._ID,
                AcessoEntRet.DATA,
                AcessoEntRet.VALOR,
                AcessoEntRet.DESCRICAO,
                AcessoEntRet.TIPO
        };

         /* Retorna dados cadastrados em uma data especificada e se for do tipo retirada */
        String selection = AcessoEntRet.TIPO + " =? AND " + AcessoEntRet.DATA + " LIKE ?";
        String[] selectionArgs = new String[]{String.valueOf(Constantes.TIPO_RETIRADA_CAIXA), mDataPesquisarBD + "%"};
        String sortOrder = AcessoEntRet.DATA;

        return new CursorLoader(
                this,
                AcessoEntRet.CONTENT_URI_ENT_RET,
                projection,
                selection,
                selectionArgs,
                sortOrder
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
     * Ao clicar vai abrir um Dialog com a descrição e o valor da Retirada
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

        tituloDialog = "Retirada";

        //  Mensagem do Dialog - Descrição
        mensagemDialog = "\nValor:    "
                + Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.VALOR)))
                + "\n\n"
                + "Descrição:   "
                + cursor.getString(cursor.getColumnIndex(AcessoEntRet.DESCRICAO))
                + "\n\n"
                + "Hora:    "
                + DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(AcessoEntRet.DATA)));

        Dialogos.dialogoExibirDados(RetListActivity.this, tituloDialog, mensagemDialog);

    }

    /**
     * Click longo no ListView
     * Ao clicar vai abrir um dialog para escolher se vai editar ou excluir a retirada
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     * @return true de click longo foi efetuado com sucesso
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


        Uri uri = ContentUris.withAppendedId(AcessoEntRet.CONTENT_URI_ENT_RET, id);

        Cursor cursor = mAdapter.getCursor();
        String mensagemExcluir = mAdapter.getCursor().getString(
                cursor.getColumnIndex(AcessoEntRet.DESCRICAO)) +
                getString(R.string.dialog_exc_edit_texto_excluir_valor) +
                " " +
                Formatar.formatarDoubleParaCurrency(mAdapter.getCursor().getDouble(
                        cursor.getColumnIndex(AcessoEntRet.VALOR)));

        Dialogos.dialogoEditarExcluir(
                RetListActivity.this,
                EntCadActivity.class,
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

                setTitle(getString(R.string.title_ret_list) + "  " + DataHora.dateSetListenerDataBrTitulo(year, month, day));

                getLoaderManager().restartLoader(LOADER_RETIRADA_LIST, null, RetListActivity.this);
            }
        };
    }
}
