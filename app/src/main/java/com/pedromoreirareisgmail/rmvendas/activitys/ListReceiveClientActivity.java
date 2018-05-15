package com.pedromoreirareisgmail.rmvendas.activitys;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.TimeDate;
import com.pedromoreirareisgmail.rmvendas.adapters.ReceiveAdapter;
import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.models.Client;

import static com.pedromoreirareisgmail.rmvendas.constant.ConstDB.TYPE_DEBIT;
import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;

public class ListReceiveClientActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        FloatingActionButton.OnClickListener,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener {

    private TextView mTvBalance;
    private ListView mListView;
    private FloatingActionButton mFab;

    private View mEmptyView;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;

    private Client client;
    private Context mContext;
    private ReceiveAdapter mAdapter;

    private double mCredit = 0;
    private double mDebit = 0;
    private double mBalance = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_receive_client);

        initViews();
        emptyLayout();
        initListenerAndObject();
        initTitleDate();

        getLoaderManager().initLoader(ConstLoader.LOADER_LIST_RECEIVE_CLIENT, null, this);
    }

    private void initViews() {

        mTvBalance = findViewById(R.id.tv_receive_client_balance);
        mListView = findViewById(R.id.lv_list_receive_client);
        mFab = findViewById(R.id.fab_receive_receip);
        mTvEmpty = findViewById(R.id.tv_empty_view);
        mIvEmpty = findViewById(R.id.iv_empty_view);
        mEmptyView = findViewById(R.id.empty_view);
    }

    private void emptyLayout() {

        // Layout vazio - Cadastro sem registros
        mTvEmpty.setText(R.string.text_receive_list_empty);
        mIvEmpty.setImageResource(R.drawable.ic_money_payment);
        mIvEmpty.setContentDescription(getString(R.string.descr_hand_money_empty));
        mListView.setEmptyView(mEmptyView);
    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = ListReceiveClientActivity.this;

        // Cria o adapter e o ListView
        mAdapter = new ReceiveAdapter(mContext);
        mListView.setAdapter(mAdapter);

        // Instancia o Objeto Client
        client = new Client();

        Intent intent = getIntent();
        client.setUri(intent.getData());

        if (intent.hasExtra(ConstIntents.INTENT_CLIENT_DATA)) {

            client = intent.getParcelableExtra(ConstIntents.INTENT_CLIENT_DATA);

        }

        mFab.setOnClickListener(this);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    private void initTitleDate() {

        setTitle(client.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_client_fone, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /* Abre o App de telefone para fazer ligação para o cliente, no numero que esta
             * cadastrado */
            case R.id.action_fone_cliente:

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + client.getFone()));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.fab_receive_receip:

                Intent intent = new Intent(mContext, RegisterReceiveActivity.class);
                intent.setData(client.getUri());
                intent.putExtra(ConstIntents.INTENT_CLIENT_DATA, client);

                startActivity(intent);

                break;

        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /* Retorna todas as colunas e registro a receber de um unico cliente*/
        String[] projection = new String[]{
                EntryReceive._ID,
                EntryReceive.COLUMN_CLIENT_ID,
                EntryReceive.COLUMN_CLIENT_NAME,
                EntryReceive.COLUMN_TIMESTAMP,
                EntryReceive.COLUMN_DESCRIPTION,
                EntryReceive.COLUMN_TYPE,
                EntryReceive.COLUMN_VALUE
        };

        String selection = EntryReceive.COLUMN_CLIENT_ID + " LIKE ? ";
        String[] selectionArgs = new String[]{String.valueOf(client.getId())};
        String sortOrder = EntryReceive.COLUMN_TIMESTAMP + " DESC ";

        return new CursorLoader(
                mContext,
                EntryReceive.CONTENT_URI_RECEIVE,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        resetVariables();

        if (loader.getId() == ConstLoader.LOADER_LIST_RECEIVE_CLIENT && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                if (cursor.getInt(cursor.getColumnIndex(EntryReceive.COLUMN_TYPE)) == TYPE_DEBIT) {

                    mDebit = mDebit + cursor.getDouble(cursor.getColumnIndex(EntryReceive.COLUMN_VALUE));

                } else {

                    mCredit = mCredit + cursor.getDouble(cursor.getColumnIndex(EntryReceive.COLUMN_VALUE));
                }

                cursor.moveToNext();
            }


            mBalance = mCredit - mDebit;

            if (mBalance < 0) {

                mTvBalance.setTextColor(getResources().getColor(R.color.colorRed));
                mTvBalance.setText(String.format(getString(
                        R.string.text_receiv_list_balance),
                        Formatting.doubleToCurrency(mBalance)
                ));

            } else {

                mTvBalance.setTextColor(getResources().getColor(R.color.colorBlue));
                mTvBalance.setText(String.format(
                        getString(R.string.text_receiv_list_balance),
                        Formatting.doubleToCurrency(mBalance)
                ));
            }

        } else {

            /* Não encontrou nenhum registro a receber para esse cliente, logo não conseguiu colocar
             * no primeiro registro - Informa ao usuario que não a nenhum registro para esse cliente*/
            mTvBalance.setText(getString(R.string.text_receiv_list_balance_empty));
        }

        mAdapter.swapCursor(cursor);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }

    private void resetVariables() {

        mCredit = 0;
        mDebit = 0;
        mBalance = 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        String title;

        int type = cursor.getInt(cursor.getColumnIndex(EntryReceive.COLUMN_TYPE));
        String date = cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_TIMESTAMP));
        String hour = cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_TIMESTAMP));
        String description = cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_DESCRIPTION));
        Double value = cursor.getDouble(cursor.getColumnIndex(EntryReceive.COLUMN_VALUE));

        if (type == ConstDB.TYPE_CREDIT) { // Credito

            title = getString(R.string.text_receive_credit);

        } else { // Debito

            title = getString(R.string.text_receive_debit);
        }

        String message = String.format(
                getString(R.string.dialog_inf_receive),
                TimeDate.formatDateBr(date),
                TimeDate.formatDateToHourAndMinute(hour),
                description,
                Formatting.doubleToCurrency(value)
        );

        Messages.displayData(mContext, title, message);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


        Uri uri = ContentUris.withAppendedId(EntryReceive.CONTENT_URI_RECEIVE, id);
        Cursor cursor = mAdapter.getCursor();

        String clientName = cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_CLIENT_NAME));
        String description = cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_DESCRIPTION));
        int type = cursor.getInt(cursor.getColumnIndex(EntryReceive.COLUMN_TYPE));
        Double value = cursor.getDouble(cursor.getColumnIndex(EntryReceive.COLUMN_VALUE));
        String timestamp = cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_TIMESTAMP));

        String typeString;

        if (type == ConstDB.TYPE_DEBIT) { // Debito - venda a prazo

            typeString = "A prazo";

        } else { // Credito - recebimento

            typeString = "Recebimento";
        }

        String messageDelete = String.format(
                getString(R.string.dialog_edit_del_message_delete_receive),
                clientName,
                typeString,
                description,
                Formatting.doubleToCurrency(value),
                TimeDate.formatDateBr(timestamp)
        );

        Messages.deleteReceive(
                mContext,
                uri,
                messageDelete
        );

        return true;
    }
}
