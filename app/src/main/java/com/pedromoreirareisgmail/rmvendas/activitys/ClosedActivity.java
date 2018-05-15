package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Calculus;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.TimeDate;
import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.constant.ConstTag;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryOpening;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;

import java.text.NumberFormat;

import static com.pedromoreirareisgmail.rmvendas.constant.Const.NUMBER_ZERO;

public class ClosedActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ConstTag.TAG_MAIN + ClosedActivity.class.getSimpleName();


    private TextView mTvValueBalanceStart;
    private TextView mTvValueAddMoney;
    private TextView mTvValueRemoveMoney;
    private TextView mTvValueDiscount;
    private TextView mTvValueAdd;
    private TextView mTvValueSaleForward;
    private TextView mTvValueTotalSale;
    private TextView mTvValueSaleInCash;
    private TextView mTvValueBalanceEnd;
    private TextView mTvQuantitySale;
    private TextView mTvQuantitySold;
    private TextView mTvQuantitySoldInCash;
    private TextView mTvClientNameForward;
    private CardView mCvClientNameForward;


    private double mValueBalanceEnd = 0;
    private double mValueSaleTotal = 0;
    private double mValueSaleInCash = 0;
    private double mValueBalanceStart = 0;
    private double mValueAddMoney = 0;
    private double mValueRemoveMoney = 0;
    private double mValueDiscount = 0;
    private double mValueAdd = 0;
    private double mValueSaleForward = 0;
    private int mQuantitySale = 0;
    private int mQuantitySold = 0;
    private int mQuantitySoldInCash = 0;
    private String mClientNameForward = "";

    private Context mContext;

    private String mDateSearchDB = null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed);

        Log.v(TAG, "onCreate");

        mContext = ClosedActivity.this;

        initViews();
        initTitleDate();

        // Inicia as pesquisas com a data do dia
        getLoaderManager().initLoader(ConstLoader.LOADER_CLOSED_CASHMOVE, null, this);
        getLoaderManager().initLoader(ConstLoader.LOADER_CLOSED_OPENING, null, this);
        getLoaderManager().initLoader(ConstLoader.LOADER_CLOSED_SELL, null, this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia os itens do layout
        mTvValueBalanceStart = findViewById(R.id.tv_closed_cashmove_balance_start_value);
        mTvValueAddMoney = findViewById(R.id.tv_closed_cashmove_add_value);
        mTvValueTotalSale = findViewById(R.id.tv_closed_cashmove_sale_value_total);
        mTvValueSaleInCash = findViewById(R.id.tv_closed_cashmove_in_cash_value);
        mTvValueRemoveMoney = findViewById(R.id.tv_closed_cashmove_remove_value);
        mTvValueDiscount = findViewById(R.id.tv_closed_inf_discount_value);
        mTvValueAdd = findViewById(R.id.tv_closed_inf_add_value);
        mTvValueSaleForward = findViewById(R.id.tv_closed_inf_forward_value);
        mTvValueBalanceEnd = findViewById(R.id.tv_closed_balance_value_end);
        mTvQuantitySale = findViewById(R.id.tv_closed_inf_sale_value);
        mTvQuantitySold = findViewById(R.id.tv_closed_inf_solds_value);
        mTvQuantitySoldInCash = findViewById(R.id.tv_closed_inf_sold_in_cash_value);
        mTvClientNameForward = findViewById(R.id.tv_closed_client_forward);
        mCvClientNameForward = findViewById(R.id.cv_closed_client_forward);
    }

    private void initTitleDate() {

        //  Obtem a data calendário do Dialog
        getCalendarDate();

        // coloca titulo na Activity  juntamente com a data da pesquisa
        setTitle(String.format(getString(R.string.title_closed), TimeDate.getDateTitleBr()));

        // Data do dia para pesquisa no BD
        mDateSearchDB = TimeDate.formatDateSearch(TimeDate.getDateTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_date, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {

            case R.id.action_date:
                Messages.dialogCalendar(mContext, mDateSetListener);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        /* Pesquisa todas as entradas e retiradas na data pesquisada */
        if (loaderId == ConstLoader.LOADER_CLOSED_CASHMOVE) {

            Log.v(TAG, "onCreateLoader - LOADER_ENTRADAS_RETIRADAS");

            String[] projection = new String[]{
                    EntryCashMove._ID,
                    EntryCashMove.COLUMN_TIMESTAMP,
                    EntryCashMove.COLUMN_VALUE,
                    EntryCashMove.COLUMN_DESCRIPTION,
                    EntryCashMove.COLUMN_TYPE
            };

            // Pesquisa por data
            String selection = EntryCashMove.COLUMN_TIMESTAMP + " LIKE ? ";
            String[] selectionArgs = new String[]{mDateSearchDB + "%"};

            return new CursorLoader(
                    mContext,
                    EntryCashMove.CONTENT_URI_CASHMOVE,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
        }

        /* Pesquisa o saldo inicial na data pesquisada */
        if (loaderId == ConstLoader.LOADER_CLOSED_OPENING) {

            Log.v(TAG, "onCreateLoader - LOADER_SALDO_INICIAL");

            String[] projection = {
                    EntryOpening._ID,
                    EntryOpening.COLUMN_VALUE,
                    EntryOpening.COLUMN_TIMESTAMP
            };

            // Pesquisa por data
            String selection = EntryOpening.COLUMN_TIMESTAMP + " LIKE ? ";
            String[] selectionArgs = new String[]{mDateSearchDB + "%"};

            return new CursorLoader(
                    mContext,
                    EntryOpening.CONTENT_URI_OPENING,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
        }

        /* Pesquisa vendas realizada na data pesquisada */
        if (loaderId == ConstLoader.LOADER_CLOSED_SELL) {

            Log.v(TAG, "onCreateLoader - LOADER_VENDAS");

            String[] projection = {
                    EntrySeel._ID,
                    EntrySeel.COLUMN_CLIENT_ID,
                    EntrySeel.COLUMN_NAME,
                    EntrySeel.COLUMN_QUANTITY,
                    EntrySeel.COLUMN_TIMESTAMP,
                    EntrySeel.COLUMN_DISCOUNT_VALUE,
                    EntrySeel.COLUMN_ADD_VALUE,
                    EntrySeel.COLUMN_FORWARD_VALUE,
                    EntrySeel.COLUMN_PRICE,
                    EntrySeel.COLUMN_RECEIVE_ID
            };

            // Pesquisa por data
            String selection = EntrySeel.COLUMN_TIMESTAMP + " LIKE ? ";
            String[] selectionArgs = new String[]{mDateSearchDB + "%"};

            return new CursorLoader(
                    mContext,
                    EntrySeel.CONTENT_URI_SELL,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        /* Entradas e Retiradas
         * Soma todas as entradas e coloca valor em uma varivel
         * Soma todas as retiradas e coloca valor em uma variavel
         */
        if (loader.getId() == ConstLoader.LOADER_CLOSED_CASHMOVE && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_ENTRADAS_RETIRADAS");

            for (int i = 0; i < cursor.getCount(); i++) {


                if (ConstDB.TYPE_ADD_MONEY_CASHMOVE ==
                        cursor.getInt(cursor.getColumnIndex(EntryCashMove.COLUMN_TYPE))) {

                    mValueAddMoney = mValueAddMoney +
                            cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));

                } else if (ConstDB.TYPE_REMOVE_MONEY_CASHMOVE ==
                        cursor.getInt(cursor.getColumnIndex(EntryCashMove.COLUMN_TYPE))) {

                    mValueRemoveMoney = mValueRemoveMoney +
                            cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));
                }

                cursor.moveToNext();
            }

        }

        /* Valor do saldo inicial */
        if (loader.getId() == ConstLoader.LOADER_CLOSED_OPENING && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_SALDO_INICIAL");

            for (int i = 0; i < cursor.getCount(); i++) {

                mValueBalanceStart = mValueBalanceStart + cursor.getDouble(
                        cursor.getColumnIndex(EntryOpening.COLUMN_VALUE));

                cursor.moveToNext();
            }

        }

        /* Vendas - Todas as vendas da data pesquisada
         * É obtido todos os dados das vendas e esses dados são somados
         *
         * Soma a quantidade de produtos vendidos
         * Soma a quantidade de produtos vendidos a prazo
         * Soma a quantidade de produtos vendidos a vista
         *
         * Soma todos os valores de descontos
         * Soma todos os valores de adicionais
         * Soma todos os valores de produtos vendidos a vista
         * Soma todos os valores de produtos vendidos a prazo
         *
         * Com os dados obtidos e feito o calculo do valor obtido em todas a vendas*/
        if (loader.getId() == ConstLoader.LOADER_CLOSED_SELL && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_VENDAS");

            mQuantitySale = cursor.getCount();

            for (int i = 0; i < cursor.getCount(); i++) {

                mQuantitySold = mQuantitySold +
                        cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY));


                if (cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)) != NUMBER_ZERO) {

                    mValueAdd = mValueAdd +
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE));
                }

                if (cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)) != NUMBER_ZERO) {

                    mValueDiscount = mValueDiscount +
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE));
                }


                if (cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE)) == NUMBER_ZERO) {

                    mValueSaleInCash = mValueSaleInCash + Calculus.CalcularValorAVistaDouble(
                            cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE))
                    );


                    mQuantitySoldInCash = mQuantitySoldInCash + cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY));

                }


                if (cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE)) != NUMBER_ZERO) {

                    mValueSaleForward = mValueSaleForward +
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE));

                    mValueSaleInCash = mValueSaleInCash + Calculus.CalcularValorAVistaDouble(
                            cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE)));

                    int idCliente = cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_CLIENT_ID));
                    mClientNameForward = mClientNameForward.concat(String.format(
                            getString(R.string.text_closed_client_name_forward),
                            SearchDB.searchClientName(ClosedActivity.this, idCliente)
                    ));


                }

                mValueSaleTotal = mValueSaleTotal + Calculus.calcularValorTotalVendaDouble(
                        cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                        cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)),
                        cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)),
                        cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE))
                );

                cursor.moveToNext();


            }

            if (!mClientNameForward.isEmpty()) {

                mCvClientNameForward.setVisibility(View.VISIBLE);
                mTvClientNameForward.append(String.format(
                        getString(R.string.text_closed_client_name), mClientNameForward));
            } else {

                mCvClientNameForward.setVisibility(View.GONE);
            }

        }


        mTvValueAddMoney.setText(String.format(getString(R.string.text_closed_add_money), preco.format(mValueAddMoney)));
        mTvValueRemoveMoney.setText(String.format(getString(R.string.text_closed_remove_money), preco.format(mValueRemoveMoney)));
        mTvValueBalanceStart.setText(String.format(getString(R.string.text_closed_balance_start), preco.format(mValueBalanceStart)));
        mTvQuantitySale.setText(String.format(getString(R.string.text_closed_sale), String.valueOf(mQuantitySale)));
        mTvValueSaleInCash.setText(String.format(getString(R.string.text_closed_sale_in_cash), preco.format(mValueSaleInCash)));
        mTvValueTotalSale.setText(String.format(getString(R.string.text_closed_sale_total), preco.format(mValueSaleTotal)));
        mTvQuantitySold.setText(String.format(getString(R.string.text_closed_quantity_product_sold), String.valueOf(mQuantitySold)));
        mTvQuantitySoldInCash.setText(String.format(getString(R.string.text_closed_quantity_product_sold_in_cash), String.valueOf(mQuantitySoldInCash)));
        mTvValueAdd.setText(String.format(getString(R.string.text_closed_add), preco.format(mValueAdd)));
        mTvValueDiscount.setText(String.format(getString(R.string.text_closed_discount), preco.format(mValueDiscount)));
        mTvValueSaleForward.setText(String.format(getString(R.string.text_closed_forward), preco.format(mValueSaleForward)));

        // Calculo Saldo Final - Valor que usuario deve ter no caixa
        mValueBalanceEnd = mValueAddMoney + mValueBalanceStart + mValueSaleInCash - mValueRemoveMoney;

        // Coloca nas TextView resultados dos dados obtidos

        mTvValueBalanceEnd.setText(String.format(getString(R.string.text_closed_balance_end), preco.format(mValueBalanceEnd)));


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    /*
     * Escolha no calendário uma data que será utilizada para pesquisar no banco de dados. Essa
     * data será formatada para tipo do Brasil e será apresentada no titulo, e iniciará uma
     * pesquisa para verificar se há dados para esta data
     *
     * Ao escolher uma nova data para pesquisa, os valores de variaveis são zerados e e reiniciada a
     * pesquisa de todos os Loaders
     */
    private void getCalendarDate() {

        Log.v(TAG, "getCalendarDate");

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                zeraVariaveisValores();

                mDateSearchDB = TimeDate.getDateSearchDB(year, month, day);

                setTitle(String.format(getResources().getString(R.string.title_closed),
                        TimeDate.getDateTitleBr(year, month, day)));

                resetSearch();
            }
        };

    }

    /* Zera os valores das variaveis
     * Ao zera o valor evitar ter uma pesquisa com valores duplicados
     */
    private void zeraVariaveisValores() {

        Log.v(TAG, "pegarDataDialogCalendario");


        mValueBalanceEnd = 0;
        mValueSaleInCash = 0;
        mValueSaleTotal = 0;
        mValueBalanceStart = 0;
        mValueAddMoney = 0;
        mValueRemoveMoney = 0;
        mValueDiscount = 0;
        mValueAdd = 0;
        mValueSaleForward = 0;
        mQuantitySale = 0;
        mQuantitySold = 0;
        mQuantitySoldInCash = 0;
        mClientNameForward = "";
        mTvClientNameForward.setText("");
    }

    /* Faz a pesquisa novamente com todos os Loaders
     */
    private void resetSearch() {

        getLoaderManager().restartLoader(ConstLoader.LOADER_CLOSED_CASHMOVE, null, ClosedActivity.this);
        getLoaderManager().restartLoader(ConstLoader.LOADER_CLOSED_OPENING, null, ClosedActivity.this);
        getLoaderManager().restartLoader(ConstLoader.LOADER_CLOSED_SELL, null, ClosedActivity.this);
    }
}
