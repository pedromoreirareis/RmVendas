package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
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
import com.pedromoreirareisgmail.rmvendas.Utils.TimeData;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryOpening;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;

import java.text.NumberFormat;

import static com.pedromoreirareisgmail.rmvendas.constant.Const.NUMBER_ZERO;

public class ClosedActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ClosedActivity.class.getSimpleName();
    private static final int LOADER_ENTRADAS_RETIRADAS = 0;
    private static final int LOADER_SALDO_INICIAL = 1;
    private static final int LOADER_VENDAS = 2;

    private TextView mTvValorSaldoInicial;
    private TextView mTvValorEntradas;
    private TextView mTvValorRetiradas;
    private TextView mTvValorDescontos;
    private TextView mTvValorAdicional;
    private TextView mTvValorVendasPrazo;
    private TextView mTvValorTotalVendas;
    private TextView mTvValorVendasVista;
    private TextView mTvValorSaldoFinal;
    private TextView mTvQuantVendas;
    private TextView mTvQuantVendidos;
    private TextView mTvQuantVendidosVista;
    private TextView mTvClientesPrazo;
    private CardView mCvClientesPrazo;

    private double mValorSaldoFinal = 0;
    private double mValorVendasTotal = 0;
    private double mValorVendasVista = 0;
    private double mValorSaldoInicial = 0;
    private double mValorEntradas = 0;
    private double mValorRetiradas = 0;
    private double mValorDescontos = 0;
    private double mValorAdicional = 0;
    private double mValorVendasPrazo = 0;
    private int mQuantVendas = 0;
    private int mQuantVendidos = 0;
    private int mQuantVendidosVista = 0;
    private String mNomeClientesPrazo = "";

    private String mDataPesquisarBD = null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed);

        Log.v(TAG, "onCreate");

        initViews();

        // pega data do calendario para uma nova pesquisa
        pegarDataDialogCalendario();

        // coloca titulo na Activity  juntamente com a data da pesquisa
        setTitle(String.format(getResources().getString(R.string.title_closed), TimeData.getDateTitleBr()));

        // Data do dia para pesquisa no BD
        mDataPesquisarBD = TimeData.formatDateSearch(TimeData.getDateTime());

        // Inicia as pesquisas com a data do dia
        getLoaderManager().initLoader(LOADER_ENTRADAS_RETIRADAS, null, this);
        getLoaderManager().initLoader(LOADER_SALDO_INICIAL, null, this);
        getLoaderManager().initLoader(LOADER_VENDAS, null, this);
    }


    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia os itens do layout
        mTvValorSaldoInicial = findViewById(R.id.tv_fechamento_saldo_inicial);
        mTvValorEntradas = findViewById(R.id.tv_fechamento_entrada);
        mTvValorTotalVendas = findViewById(R.id.tv_fechamento_valor_total_vendas);
        mTvValorVendasVista = findViewById(R.id.tv_fechamento_valor_vendas_vista);
        mTvValorRetiradas = findViewById(R.id.tv_fechamento_retirada);
        mTvValorDescontos = findViewById(R.id.tv_fechamento_descontos);
        mTvValorAdicional = findViewById(R.id.tv_fechamento_adicional);
        mTvValorVendasPrazo = findViewById(R.id.tv_fechamento_prazo);
        mTvValorSaldoFinal = findViewById(R.id.tv_fechamento_saldo_final);
        mTvQuantVendas = findViewById(R.id.tv_fechamento_vendas);
        mTvQuantVendidos = findViewById(R.id.tv_fechamento_vendidos);
        mTvQuantVendidosVista = findViewById(R.id.tv_fechamento_vendidos_vista);
        mTvClientesPrazo = findViewById(R.id.tv_fechamento_clientes_a_prazo);
        mCvClientesPrazo = findViewById(R.id.cv_fechamento_clientes_prazo);
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

        // Abre o Dialog de data para fazer pesquisa por data
        if (item.getItemId() == R.id.action_date) {

            Messages.dialogCalendar(ClosedActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle bundle) {

        /* LOADER_ENTRADA_RETIRADA
         * Esse Loader é responsavel pela pesquisa de todas as entradas e retiradas efetuadas na data
         * pesquisada
         */
        if (loader == LOADER_ENTRADAS_RETIRADAS) {

            Log.v(TAG, "onCreateLoader - LOADER_ENTRADAS_RETIRADAS");

            String[] projection = new String[]{
                    EntryCashMove._ID,
                    EntryCashMove.COLUMN_TIMESTAMP,
                    EntryCashMove.COLUMN_VALUE,
                    EntryCashMove.COLUMN_DESCRIPTION,
                    EntryCashMove.COLUMN_TYPE
            };

            // Pesquisa por data
            String selection = EntryCashMove.COLUMN_TIMESTAMP + " LIKE ?";
            String[] selectionArgs = new String[]{mDataPesquisarBD + "%"};

            return new CursorLoader(
                    this,
                    EntryCashMove.CONTENT_URI_CASHMOVE,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
        }

        /* LOADER_SALDO
         * Esse loader e responsavel por pesquisar o saldo inicial da data pesquisada
         */
        if (loader == LOADER_SALDO_INICIAL) {

            Log.v(TAG, "onCreateLoader - LOADER_SALDO_INICIAL");

            String[] projection = {
                    EntryOpening._ID,
                    EntryOpening.COLUMN_VALUE,
                    EntryOpening.COLUMN_TIMESTAMP
            };

            // Pesquisa por data
            String selection = EntryOpening.COLUMN_TIMESTAMP + " LIKE ?";
            String[] selectionArgs = new String[]{mDataPesquisarBD + "%"};

            return new CursorLoader(
                    this,
                    EntryOpening.CONTENT_URI_OPENING,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
        }

        /* LOADER_VENDAS
         * Esse loader e resposavel por pesquisar todas as vendas realizada na data pesquisada
         */
        if (loader == LOADER_VENDAS) {

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
                    EntrySeel.COLUMN_PRICE
            };

            // Pesquisa por data
            String selection = EntrySeel.COLUMN_TIMESTAMP + " LIKE ?";
            String[] selectionArgs = new String[]{mDataPesquisarBD + "%"};

            return new CursorLoader(
                    this,
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

        /* Entradas e Retiradas -  Todas entradas e retiradas da data pesquisada
         * Soma todas as entradas e coloca valor em uma varivel
         * Soma todas as retiradas e coloca valor em uma variavel
         */
        if (loader.getId() == LOADER_ENTRADAS_RETIRADAS && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_ENTRADAS_RETIRADAS");

            for (int i = 0; i < cursor.getCount(); i++) {

                if (ConstDB.TYPE_ADD_MONEY_CASHMOVE ==
                        cursor.getInt(cursor.getColumnIndex(EntryCashMove.COLUMN_TYPE))) {

                    mValorEntradas = mValorEntradas +
                            cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));

                } else if (ConstDB.TYPE_REMOVE_MONEY_CASHMOVE ==
                        cursor.getInt(cursor.getColumnIndex(EntryCashMove.COLUMN_TYPE))) {

                    mValorRetiradas = mValorRetiradas +
                            cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));
                }

                cursor.moveToNext();
            }

        }

        /* Saldo Inicial - O valor do saldo inicial da data pesquisada
         *
         */
        if (loader.getId() == LOADER_SALDO_INICIAL && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_SALDO_INICIAL");

            for (int i = 0; i < cursor.getCount(); i++) {

                mValorSaldoInicial = mValorSaldoInicial + cursor.getDouble(
                        cursor.getColumnIndex(EntryOpening.COLUMN_VALUE));

                cursor.moveToNext();
            }

        }

        /* Vendas - Todas as vendas da data pesquisada
         * É obtido todos os dados das vendas e esses dados são somados
         *
         * Soma a quantidade de bolos vendidos na data pesquisada
         * Soma a quantidade de bolos vendidos a prazo
         * Soma a quantidade de bolos vendidos a vista
         * Soma todos os valores de descontos dados
         * Soma todos os valores de bolos vendidos a vista
         * Soma todos os valores de bolos vendidos a prazo
         *
         * Com os dados obtidos e feito o calculo do valor obtido em todas a vendas
         */


        if (loader.getId() == LOADER_VENDAS && cursor.moveToFirst()) {

            Log.v(TAG, "onLoadFinished - LOADER_VENDAS");

            mQuantVendas = cursor.getCount();

            for (int i = 0; i < cursor.getCount(); i++) {

                mQuantVendidos = mQuantVendidos +
                        cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY));


                if (cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)) != NUMBER_ZERO) {

                    mValorAdicional = mValorAdicional +
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE));
                }

                if (cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)) != NUMBER_ZERO) {

                    mValorDescontos = mValorDescontos +
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE));
                }


                if (cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE)) == NUMBER_ZERO) {

                    mValorVendasVista = mValorVendasVista + Calculus.CalcularValorAVistaDouble(
                            cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE))
                    );


                    mQuantVendidosVista = mQuantVendidosVista + cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY));

                }


                if (cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE)) != NUMBER_ZERO) {

                    mValorVendasPrazo = mValorVendasPrazo +
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE));

                    mValorVendasVista = mValorVendasVista + Calculus.CalcularValorAVistaDouble(
                            cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)),
                            cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE)));

                    int idCliente = cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_CLIENT_ID));
                    mNomeClientesPrazo = mNomeClientesPrazo + SearchDB.searchClientName(ClosedActivity.this, idCliente) + "\n";


                }

                mValorVendasTotal = mValorVendasTotal + Calculus.calcularValorTotalVendaDouble(
                        cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                        cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)),
                        cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)),
                        cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE))
                );

                cursor.moveToNext();


            }

            if (!mNomeClientesPrazo.isEmpty()) {

                mCvClientesPrazo.setVisibility(View.VISIBLE);
                mTvClientesPrazo.append(String.format(getResources().getString(R.string.text_fechamento_clientes_nome), mNomeClientesPrazo));
            } else {

                mCvClientesPrazo.setVisibility(View.GONE);
            }

        }


        mTvValorEntradas.setText(String.format(getResources().getString(R.string.text_fechamento_entradas), preco.format(mValorEntradas)));
        mTvValorRetiradas.setText(String.format(getResources().getString(R.string.text_fechamento_retiradas), preco.format(mValorRetiradas)));
        mTvValorSaldoInicial.setText(String.format(getResources().getString(R.string.text_fechamento_saldo_inicial), preco.format(mValorSaldoInicial)));
        mTvQuantVendas.setText(String.format(getResources().getString(R.string.text_fechamento_vendas), String.valueOf(mQuantVendas)));
        mTvValorVendasVista.setText(String.format(getResources().getString(R.string.text_fechamento_vendas_a_vista), preco.format(mValorVendasVista)));
        mTvValorTotalVendas.setText(String.format(getResources().getString(R.string.text_fechamento_vendas_total), preco.format(mValorVendasTotal)));
        mTvQuantVendidos.setText(String.format(getResources().getString(R.string.text_fechamento_produtos_vendidos), String.valueOf(mQuantVendidos)));
        mTvQuantVendidosVista.setText(String.format(getResources().getString(R.string.text_fechamento_produtos_vendidos_avista), String.valueOf(mQuantVendidosVista)));
        mTvValorAdicional.setText(String.format(getResources().getString(R.string.text_fechamento_adicional), preco.format(mValorAdicional)));
        mTvValorDescontos.setText(String.format(getResources().getString(R.string.text_fechamento_descontos), preco.format(mValorDescontos)));
        mTvValorVendasPrazo.setText(String.format(getResources().getString(R.string.text_fechamento_a_prazo), preco.format(mValorVendasPrazo)));

        // Calculo Saldo Final - Valor que usuario deve ter no caixa
        mValorSaldoFinal = mValorEntradas + mValorSaldoInicial + mValorVendasVista - mValorRetiradas;

        // Coloca nas TextView resultados dos dados obtidos

        mTvValorSaldoFinal.setText(String.format(getResources().getString(R.string.text_fechamento_saldo_final), preco.format(mValorSaldoFinal)));


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
    private void pegarDataDialogCalendario() {

        Log.v(TAG, "pegarDataDialogCalendario");

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                zeraVariaveisValores();

                mDataPesquisarBD = TimeData.getDateSearchDB(year, month, day);

                setTitle(String.format(getResources().getString(R.string.title_closed),
                        TimeData.getDateTitleBr(year, month, day)));

                reiniciarPesquisas();
            }
        };

    }

    /* Zera os valores das variaveis
     * Ao zera o valor evitar ter uma pesquisa com valores duplicados
     */
    private void zeraVariaveisValores() {

        Log.v(TAG, "pegarDataDialogCalendario");


        mValorSaldoFinal = 0;
        mValorVendasVista = 0;
        mValorVendasTotal = 0;
        mValorSaldoInicial = 0;
        mValorEntradas = 0;
        mValorRetiradas = 0;
        mValorDescontos = 0;
        mValorAdicional = 0;
        mValorVendasPrazo = 0;
        mQuantVendas = 0;
        mQuantVendidos = 0;
        mQuantVendidosVista = 0;
        mNomeClientesPrazo = "";
        mTvClientesPrazo.setText("");
    }

    /* Faz a pesquisa novamente com todos os Loaders
     */
    private void reiniciarPesquisas() {

        getLoaderManager().restartLoader(LOADER_ENTRADAS_RETIRADAS, null, ClosedActivity.this);
        getLoaderManager().restartLoader(LOADER_SALDO_INICIAL, null, ClosedActivity.this);
        getLoaderManager().restartLoader(LOADER_VENDAS, null, ClosedActivity.this);
    }
}
