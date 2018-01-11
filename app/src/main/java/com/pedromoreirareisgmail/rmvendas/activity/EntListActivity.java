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
import android.util.Log;
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
import com.pedromoreirareisgmail.rmvendas.adapter.EntAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoEntRet;

public class EntListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener {

    private static final String TAG = EntListActivity.class.getSimpleName();
    private static final int LOADER_ENTRADA_LIST = 0;

    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;
    private FloatingActionButton mFab;

    private EntAdapter mAdapter;

    private String mDataPesquisarBD = null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ent_list);

        Log.v(TAG, "onCreate");

        initViews();
        emptyLayout();

        // Trata o botão Flutuante - Abre activity EntCadActivity
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(TAG, "FloatingActionButton");

                Intent intentEntrada = new Intent(
                        EntListActivity.this, EntCadActivity.class);

                startActivity(intentEntrada);
            }
        });

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new EntAdapter(this);
        mListView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        //  Pega data calendário do Dialog
        pegarDataDialogCalendario();

        // Coloca o titulo e data na Activity, e define data da pesquisa no BD
        setTitle(String.format(getResources().getString(R.string.title_entrada_list),
                DataHora.obterFormatarDataBrTitulo()));

        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_ENTRADA_LIST, null, this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia itens do layout
        mFab = findViewById(R.id.fab_add);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        Log.v(TAG, "emptyLayout");

        // Layout vazio - Cadastro sem registros
        mTvEmpty.setText(R.string.text_entrada_list_empty);
        mIvEmpty.setImageResource(R.drawable.ic_money_up);
        mIvEmpty.setContentDescription(getString(R.string.image_desc_entrada_list_empty));
        mListView.setEmptyView(mEmptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_data, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        int id = item.getItemId();

        // Menu Calendário
        if (id == R.id.action_data) {

            Dialogos.dialogoDatas(EntListActivity.this, mDateSetListener);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        String[] projection = {
                AcessoEntRet._ID,
                AcessoEntRet.DATA_HORA,
                AcessoEntRet.VALOR,
                AcessoEntRet.DESCRICAO,
                AcessoEntRet.TIPO
        };

        /* Retorna dados cadastrados em uma data especificada e se for do tipo entrada */
        String selection = AcessoEntRet.TIPO + " =? AND " + AcessoEntRet.DATA_HORA + " LIKE ?";
        String[] selectionArgs = new String[]{String.valueOf(Constantes.TIPO_ENTRADA_CAIXA), mDataPesquisarBD + "%"};
        String sortOrder = AcessoEntRet.DATA_HORA;

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

        Log.v(TAG, "onLoadFinished");

        // Envia dados retornados do BD para o adapter e ListView
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

        mAdapter.swapCursor(null);
    }

    /**
     * Click simples no ListView
     * Ao clicar vair abir um Dialog com o valor e descrição da Entrada
     *
     * @param parent   adaptador
     * @param view     item do listview
     * @param position posição da view no adaptador
     * @param id       id do item (id dentro do BD, vem pelo cursor junto com pesquisa)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        String tituloDialog = getString(R.string.dialog_informacao_entrada_title);

        //  Mensagem do Dialog - Descrição

        String mensagemDialog = String.format(
                getResources().getString(R.string.dialog_informacao_entrada_retirada_list),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.VALOR))),
                cursor.getString(cursor.getColumnIndex(AcessoEntRet.DESCRICAO)),
                DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(AcessoEntRet.DATA_HORA))));

        Dialogos.dialogoExibirDados(EntListActivity.this, tituloDialog, mensagemDialog);
    }

    /**
     * Click longo no ListView ()
     * Ao clicar e ficar apertado vair abir um Dialog com opção Editar ou Excluir a Entrada
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

        String mensagemExcluir = String.format(
                getResources().getString(R.string.dialog_exc_edit_texto_excluir_valor),
                cursor.getString(cursor.getColumnIndex(AcessoEntRet.DESCRICAO)),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.VALOR))),
                DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(AcessoEntRet.DATA_HORA))));

        Dialogos.dialogoEditarExcluir(
                EntListActivity.this,
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
    private void pegarDataDialogCalendario() {

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(String.format(
                        getResources().getString(R.string.title_entrada_list),
                        DataHora.dateSetListenerDataBrTitulo(year, month, day)));

                getLoaderManager().restartLoader(LOADER_ENTRADA_LIST, null, EntListActivity.this);
            }
        };
    }
}
