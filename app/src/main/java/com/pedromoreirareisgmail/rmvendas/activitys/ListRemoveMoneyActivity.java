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
import com.pedromoreirareisgmail.rmvendas.adapters.RemoveMoneyAdapter;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;

import static com.pedromoreirareisgmail.rmvendas.constant.ConstDB.TYPE_REMOVE_MONEY_CASHMOVE;

public class ListRemoveMoneyActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        FloatingActionButton.OnClickListener {

    private View mEmptyView;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private ListView mListView;
    private FloatingActionButton mFab;

    private RemoveMoneyAdapter mAdapter;
    private Context mContext;

    private String mSearchDateDB = null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_remove_money);

        initViews();
        emptyLayout();
        initListenerAndObject();
        initTitleDate();

        // Inicia o gerenciamento de dados no BD - Busca de dados
        getLoaderManager().initLoader(ConstLoader.LOADER_LIST_REMOVE_MONEY, null, this);
    }

    private void initViews() {

        // Referencia itens do layout
        mFab = findViewById(R.id.fab_add);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mListView = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        // Layout vazio - Cadastro sem registros
        mTvEmpty.setText(R.string.text_remove_money_empty);
        mIvEmpty.setImageResource(R.drawable.ic_money_arrow_down);
        mIvEmpty.setContentDescription(getString(R.string.descr_remove_money_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = ListRemoveMoneyActivity.this;

        // Cria o adapter e colocar o adapter no Listview
        mAdapter = new RemoveMoneyAdapter(mContext);
        mListView.setAdapter(mAdapter);

        // Listener do botão Flutuante
        mFab.setOnClickListener(this);

        // Listener do clique simples e Longo no ListView
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void initTitleDate() {

        //  Obtem a data calendário do Dialog
        getCalendarDate();

        // Coloca o titulo e data na Activity, e define data da pesquisa no BD
        setTitle(String.format(getString(R.string.title_remove_money_list),
                TimeDate.getDateTitleBr())
        );

        // Recebe a data do dia para pesquisa no banco de dados
        mSearchDateDB = TimeDate.formatDateSearch(TimeDate.getDateTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_date, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Abre o caléndaria para obter data
        switch (item.getItemId()) {

            case R.id.action_date:
                Messages.dialogCalendar(mContext, mDateSetListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Colunas que serao retornadas
        String[] projection = {
                EntryCashMove._ID,
                EntryCashMove.COLUMN_TIMESTAMP,
                EntryCashMove.COLUMN_VALUE,
                EntryCashMove.COLUMN_DESCRIPTION,
                EntryCashMove.COLUMN_TYPE
        };

        // O que sera pesquisado em casa coluna
        String selection = EntryCashMove.COLUMN_TYPE + " = ? AND " + EntryCashMove.COLUMN_TIMESTAMP + " LIKE ? ";

        // Dados para a pesquisa em cada coluna
        String[] selectionArgs = new String[]{String.valueOf(TYPE_REMOVE_MONEY_CASHMOVE), mSearchDateDB + "%"};

        // Ordem que sera retonado os dados
        String sortOrder = EntryCashMove.COLUMN_TIMESTAMP;

        return new CursorLoader(
                mContext,
                EntryCashMove.CONTENT_URI_CASHMOVE,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Envia dados retornados do BD para o adapter e ListView
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Se loader foi redefinido não passa nenhum dado ao adapter
        mAdapter.swapCursor(null);
    }

    /* Ao clicar vair abir um Dialog com o valor e descrição da Retirada */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        String title = getString(R.string.dialog_inf_title_remove_money);
        Double value = cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));
        String description = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION));
        String timestamp = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP));

        String message = String.format(
                getString(R.string.dialog_inf_add_remove_money_list),
                Formatting.doubleToCurrency(value),
                description,
                TimeDate.formatDateToHourAndMinute(timestamp)
        );

        Messages.displayData(mContext, title, message);
    }

    /* No click longo sera aberto um Dialog com opção Editar ou Excluir */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Uri uri = ContentUris.withAppendedId(EntryCashMove.CONTENT_URI_CASHMOVE, id);
        Cursor cursor = mAdapter.getCursor();

        Double value = cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));
        String description = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION));
        String timestamp = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP));

        String messageDelete = String.format(
                getString(R.string.dialog_edit_del_message_delete_add_remove_money),
                description,
                Formatting.doubleToCurrency(value),
                TimeDate.formatDateToHourAndMinute(timestamp)
        );

        Messages.editOurDelete(
                mContext,
                RegisterRemoveMoneyActivity.class,
                uri,
                messageDelete
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

                setTitle(
                        String.format(getString(R.string.title_remove_money_list),
                                TimeDate.getDateTitleBr(year, month, day))
                );

                getLoaderManager().restartLoader(
                        ConstLoader.LOADER_LIST_REMOVE_MONEY,
                        null,
                        ListRemoveMoneyActivity.this
                );
            }
        };
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_add:
                Intent intentRegisterRemoveMoney = new Intent(
                        mContext,
                        RegisterRemoveMoneyActivity.class
                );
                startActivity(intentRegisterRemoveMoney);
                break;
        }
    }
}
