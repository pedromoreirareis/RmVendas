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
import com.pedromoreirareisgmail.rmvendas.adapters.RetAdapter;
import com.pedromoreirareisgmail.rmvendas.constantes.ConstDB;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;

public class RetListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        FloatingActionButton.OnClickListener {

    private static final String TAG = RetListActivity.class.getSimpleName();
    private static final int LOADER_RETIRADA_LIST = 0;

    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private View mEmptyView;
    private FloatingActionButton mFab;

    private RetAdapter mAdapter;

    private String mDataPesquisarBD = null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_list);

        Log.v(TAG, "onCreate");

        initViews();
        emptyLayout();

        // Trata o botão Flutuante - Abre activity RetCadActivity
        mFab.setOnClickListener(this);

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new RetAdapter(this);
        mListView.setAdapter(mAdapter);

        // Clique simples e Longo no ListView
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);

        //  Pega data do Dialog de calendário
        pegarDataDialogCalendario();

        // Coloca o titulo e data na Activity, e define data da pesquisa no BD
        setTitle(String.format(getResources().getString(R.string.title_retirada_list), DataHora.obterFormatarDataBrTitulo()));
        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(LOADER_RETIRADA_LIST, null, this);
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
        mTvEmpty.setText(R.string.text_retirada_list_empty);
        mIvEmpty.setImageResource(R.drawable.ic_money_down);
        mIvEmpty.setContentDescription(getString(R.string.image_desc_retirada_list_empty));
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

            Dialogos.dialogoDatas(RetListActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        String[] projection = {
                EntryCashMove._ID,
                EntryCashMove.COLUMN_TIMESTAMP,
                EntryCashMove.COLUMN_VALUE,
                EntryCashMove.COLUMN_DESCRIPTION,
                EntryCashMove.COLUMN_TYPE
        };

         /* Retorna dados cadastrados em uma data especificada e se for do tipo retirada */
        String selection = EntryCashMove.COLUMN_TYPE + " =? AND " + EntryCashMove.COLUMN_TIMESTAMP + " LIKE ?";
        String[] selectionArgs = new String[]{String.valueOf(ConstDB.TIPO_RETIRADA), mDataPesquisarBD + "%"};
        String sortOrder = EntryCashMove.COLUMN_TIMESTAMP;

        return new CursorLoader(
                this,
                EntryCashMove.CONTENT_URI_CASHMOVE,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.v(TAG, "onLoadFinished");

        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

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

        Log.v(TAG, "onItemClick");

        Cursor cursor = mAdapter.getCursor();

        String tituloDialog = getString(R.string.dialog_informacao_retirada_title);

        //  Mensagem do Dialog - Descrição
        String mensagemDialog = String.format(getResources().getString(R.string.dialog_informacao_entrada_retirada_list),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE))),
                cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION)),
                DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP))));

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

        Log.v(TAG, "onItemLongClick");

        Uri uri = ContentUris.withAppendedId(EntryCashMove.CONTENT_URI_CASHMOVE, id);

        Cursor cursor = mAdapter.getCursor();

        String mensagemExcluir = String.format(getResources().getString(R.string.dialog_exc_edit_texto_excluir_valor),
                cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION)),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE))),
                DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP))));

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
    private void pegarDataDialogCalendario() {

        Log.v(TAG, "pegarDataDialogCalendario");

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(String.format(getResources().getString(R.string.title_retirada_list),
                        DataHora.dateSetListenerDataBrTitulo(year, month, day)));

                getLoaderManager().restartLoader(LOADER_RETIRADA_LIST, null, RetListActivity.this);
            }
        };
    }

    @Override
    public void onClick(View view) {

        Log.v(TAG, "onClick mFab");

        if (view.getId() == R.id.fab_add) {

            Intent intentRetirada = new Intent(RetListActivity.this, RetCadActivity.class);
            startActivity(intentRetirada);
        }
    }
}
