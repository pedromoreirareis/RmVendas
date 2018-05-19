package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
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
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.TimeDate;
import com.pedromoreirareisgmail.rmvendas.adapters.OpeningAdapter;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryOpening;

public class ListOpeningActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        FloatingActionButton.OnClickListener {

    private View mEmptyView;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private FloatingActionButton mFab;

    private OpeningAdapter mAdapter;
    private Context mContext;

    private String mSearchDateDB = null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_opening);

        initViews();
        emptyLayout();
        initListenerAndObject();
        initTitleDate();

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(ConstLoader.LOADER_LIST_OPENING, null, this);
    }

    private void initTitleDate() {

        //  Obtem a data calendário do Dialog
        getCalendarDate();

        // Coloca o titulo e data na Activity, e define data da pesquisa no BD
        setTitle(String.format(
                getResources().getString(R.string.title_opening_list),
                TimeDate.getDateTitleBr())
        );

        // Recebe a data do dia para pesquisa no banco de dados
        mSearchDateDB = TimeDate.formatDateSearch(TimeDate.getDateTime());
    }

    private void initViews() {

        //  Faz referencia aos itens do layout
        mFab = findViewById(R.id.fab_add);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        //  layout vazio - cadastro sem registros
        mTvEmpty.setText(R.string.text_opening_empty);
        mIvEmpty.setImageResource(R.drawable.ic_money_two_hands);
        mIvEmpty.setContentDescription(getString(R.string.descr_opening_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = ListOpeningActivity.this;

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new OpeningAdapter(mContext);
        mListView.setAdapter(mAdapter);

        // Listener do botão Flutuante
        mFab.setOnClickListener(this);

        // Listener do clique simples e Longo no ListView
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_date, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_date_menu_date:
                Messages.dialogCalendar(mContext, mDateSetListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Colunas que serao retornadas
        String[] projection = {
                EntryOpening._ID,
                EntryOpening.COLUMN_TIMESTAMP,
                EntryOpening.COLUMN_VALUE
        };

        // Define a coluna onde vai acontecer a pesquisa e a forma da pesquisa
        String selection = EntryOpening.COLUMN_TIMESTAMP + " LIKE ? ";

        // Define o que vai ser pesquisado
        String[] selectionArgs = new String[]{mSearchDateDB + "%"};

        return new CursorLoader(
                mContext,
                EntryOpening.CONTENT_URI_OPENING,
                projection,
                selection,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Passa os dados retonados da pesquisa para o adapter e listview
        mAdapter.swapCursor(cursor);

        /* Se pesquisa tiver retorno FloatingActionButton deve ficar invisivel */
        if (mAdapter.getCount() > 0) {

            mFab.setVisibility(View.GONE);

        } else {

            mFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Se loader foi redefinido não passa nenhum dado ao adapter
        mAdapter.swapCursor(null);
    }

    /* Ao clicar vair abir um Dialog com o valor, saldo inicial e hora do registro */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        String title = getString(R.string.dialog_inf_title_opening);
        Double value = cursor.getDouble(cursor.getColumnIndex(EntryOpening.COLUMN_VALUE));
        String timestampDate = cursor.getString(cursor.getColumnIndex(EntryOpening.COLUMN_TIMESTAMP));
        String timestampHour = cursor.getString(cursor.getColumnIndex(EntryOpening.COLUMN_TIMESTAMP));

        String message = String.format(
                getString(R.string.dialog_informacao_saldo_inicial_list),
                Formatting.doubleToCurrency(value),
                TimeDate.formatDateBr(timestampDate),
                TimeDate.formatDateToHourAndMinute(timestampHour)
        );

        Messages.displayData(mContext, title, message);
    }

    /* No click longo sera aberto um Dialog com opção Editar ou Excluir */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Uri uri = ContentUris.withAppendedId(EntryOpening.CONTENT_URI_OPENING, id);
        Cursor cursor = mAdapter.getCursor();

        Double value = cursor.getDouble(cursor.getColumnIndex(EntryOpening.COLUMN_VALUE));
        String timestamp = cursor.getString(cursor.getColumnIndex(EntryOpening.COLUMN_TIMESTAMP));

        String mensagemExcluir = String.format(
                getResources().getString(R.string.dialog_edit_del_message_delete_opening),
                Formatting.doubleToCurrency(value),
                TimeDate.formatDateBr(timestamp)
        );

        Messages.editOurDelete(
                mContext,
                RegisterOpeningActivity.class,
                uri,
                mensagemExcluir
        );

        return true;
    }

    /* Obtem a data que sera utilizada para pesquisa no banco de dados. Sera formatada
     * no formato usado no Barsil e sera mostrada no titulo da Activity*/
    private void getCalendarDate() {

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mSearchDateDB = TimeDate.getDateSearchDB(year, month, day);

                setTitle(String.format(
                        getString(R.string.title_opening_list),
                        TimeDate.getDateTitleBr(year, month, day))
                );

                getLoaderManager().restartLoader(
                        ConstLoader.LOADER_LIST_OPENING,
                        null,
                        ListOpeningActivity.this
                );
            }
        };
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_add:
                Intent intentSaldoInicial = new Intent(
                        mContext,
                        RegisterOpeningActivity.class
                );
                startActivity(intentSaldoInicial);

                break;
        }
    }
}
