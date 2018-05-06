package com.pedromoreirareisgmail.rmvendas.activitys;

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
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.adapters.SaldoInicialAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoSaldo;

public class SaldoInicialListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        FloatingActionButton.OnClickListener {

    private static final String TAG = SaldoInicialListActivity.class.getSimpleName();
    private static final int LOADER_SALDO_INICIAL_LIST = 0;

    private FloatingActionButton mFab;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;

    private SaldoInicialAdapter mAdapter;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    private String mDataPesquisarBD = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_list);

        Log.v(TAG, "onCreate");

        initViews();
        emptyLayout();

        // Trata o botão Flutuante - Abre activity SaldoInicialCadActivity
        mFab.setOnClickListener(this);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new SaldoInicialAdapter(this);
        mListView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        //  Pega data calendário do Dialog
        pegarDataDialogCalendario();

        // Coloca o titulo e data na Activity, e define data da pesquisa no BD
        setTitle(String.format(getResources().getString(R.string.title_saldo_inicial_list), DataHora.obterFormatarDataBrTitulo()));

        // O Loader utiliza mDataPesquisarBD para fazer a pesquisa no banco de dados - "yyyy-MM-dd"
        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        getLoaderManager().initLoader(LOADER_SALDO_INICIAL_LIST, null, this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        //  Faz referencia aos itens do layout
        mFab = findViewById(R.id.fab_add);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        Log.v(TAG, "emptyLayout");

        //  layout vazio - cadastro sem registros
        mTvEmpty.setText(R.string.text_saldo_inicial_list_empty);
        mIvEmpty.setImageResource(R.drawable.ic_dinheiro_duas_maos);
        mIvEmpty.setContentDescription(getString(R.string.image_desc_saldo_inicial_list_empty));
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

        if (item.getItemId() == R.id.action_data) {

            Dialogos.dialogoDatas(SaldoInicialListActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        String[] projection = {
                AcessoSaldo._ID,
                AcessoSaldo.DATA_HORA,
                AcessoSaldo.VALOR
        };

        // Procura por todos os dados salvos na tabela com parte da data do tipo "yyyy-MM-dd"
        String selection = AcessoSaldo.DATA_HORA + " LIKE ?";
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

        Log.v(TAG, "onLoadFinished");

        mAdapter.swapCursor(cursor);

        /* Se encontrar pelo menos um dado salvo para a data o FloatingActionButton deve ficar
           invisivel, se não tiver nenhum dado deve ficar visivel */
        if (mAdapter.getCount() > 0) {

            mFab.setVisibility(View.GONE);
        } else {

            mFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

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

        Log.v(TAG, "onItemClick");

        Cursor cursor = mAdapter.getCursor();

        String tituloDialog = getString(R.string.dialog_informacao_saldo_inicial_title);

        //  Mensagem do Dialog - Descrição
        String mensagemDialog = String.format(getResources().getString(R.string.dialog_informacao_saldo_inicial_list),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.VALOR))),
                DataHora.formatarDataBr(cursor.getString(cursor.getColumnIndex(AcessoSaldo.DATA_HORA))),
                DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(AcessoSaldo.DATA_HORA))));

        Dialogos.dialogoExibirDados(SaldoInicialListActivity.this, tituloDialog, mensagemDialog);
    }

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

        Log.v(TAG, "onItemLongClick");

        Uri uri = ContentUris.withAppendedId(AcessoSaldo.CONTENT_URI_SALDO_INICIAL, id);

        Cursor cursor = mAdapter.getCursor();

        String mensagemExcluir = String.format(getResources().getString(R.string.dialog_exc_edit_texto_excluir_saldo_inicial),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.VALOR))),
                DataHora.formatarDataBr(cursor.getString(cursor.getColumnIndex(AcessoSaldo.DATA_HORA))));

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
    private void pegarDataDialogCalendario() {

        Log.v(TAG, "pegarDataDialogCalendario");

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(String.format(getResources().getString(R.string.title_saldo_inicial_list),
                        DataHora.dateSetListenerDataBrTitulo(year, month, day)));

                getLoaderManager().restartLoader(LOADER_SALDO_INICIAL_LIST, null, SaldoInicialListActivity.this);
            }
        };
    }

    @Override
    public void onClick(View view) {

        Log.v(TAG, "onClick - mFab");

        if (view.getId() == R.id.fab_add) {

            Intent intentSaldoInicial = new Intent(
                    SaldoInicialListActivity.this, SaldoInicialCadActivity.class);
            startActivity(intentSaldoInicial);
        }
    }
}
