package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoVenda;

import java.text.NumberFormat;

public class FechamentoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ENTRADA_RETIRADA = 2;
    private static final int LOADER_SALDO = 3;
    private static final int LOADER_VENDAS = 4;
    double mSaldoInicial = 0;
    double mTotalEntrada = 0;
    double mTotalVendas = 0;
    double mTotalRetirada = 0;
    double mTotalDescontos = 0;
    double mTotalPrazo = 0;
    double mSaldoFinal = 0;
    int mQuantBolo = 0;
    int mQuantBoloVista = 0;
    int mQuantBoloPrazo = 0;
    private TextView mTvSaldoInicial;
    private TextView mTvEntrada;
    private TextView mTvVendas;
    private TextView mTvRetirada;
    private TextView mTvDesconto;
    private TextView mTvPrazo;
    private TextView mTvSaldoFinal;
    private TextView mTvQuantBolo;
    private TextView mTvQuantBoloVista;
    private TextView mTvQuantBoloPrazo;
    private String mDataPesquisa = "";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fechamento);


        mTvSaldoInicial = (TextView) findViewById(R.id.tv_saldo_inicial_fechamento);
        mTvEntrada = (TextView) findViewById(R.id.tv_entrada_fechamento);
        mTvVendas = (TextView) findViewById(R.id.tv_vendas_fechamento);
        mTvRetirada = (TextView) findViewById(R.id.tv_retirada_fechamento);
        mTvDesconto = (TextView) findViewById(R.id.tv_descontos_fechamento);
        mTvPrazo = (TextView) findViewById(R.id.tv_prazo_fechamento);
        mTvSaldoFinal = (TextView) findViewById(R.id.tv_saldo_final_fechamento);
        mTvQuantBolo = (TextView) findViewById(R.id.tv_bolos_vendidos_fechamento);
        mTvQuantBoloVista = (TextView) findViewById(R.id.tv_bolos_vendidos_vista_fechamento);
        mTvQuantBoloPrazo = (TextView) findViewById(R.id.tv_bolos_vendidos_prazo_fechamento);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mSaldoInicial = 0;
                mTotalEntrada = 0;
                mTotalVendas = 0;
                mTotalRetirada = 0;
                mTotalDescontos = 0;
                mTotalPrazo = 0;
                mSaldoFinal = 0;
                mQuantBolo = 0;
                mQuantBoloVista = 0;
                mQuantBoloPrazo = 0;

                mDataPesquisa = Datas.dateSetListenerPesquisa(year, month, day);

                setTitle(getString(R.string.title_fechamento) + "  " + Datas.dateSetListenerTitle(year, month, day));

                getLoaderManager().restartLoader(LOADER_ENTRADA_RETIRADA, null, FechamentoActivity.this);
                getLoaderManager().restartLoader(LOADER_SALDO, null, FechamentoActivity.this);
                getLoaderManager().restartLoader(LOADER_VENDAS, null, FechamentoActivity.this);
            }
        };


        setTitle(getString(R.string.title_ent_list) + "  " + Datas.getDate());

        mDataPesquisa = Datas.formatDatePesquisa(Datas.getDateTime());

        getLoaderManager().initLoader(LOADER_ENTRADA_RETIRADA, null, this);
        getLoaderManager().initLoader(LOADER_SALDO, null, this);
        getLoaderManager().initLoader(LOADER_VENDAS, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_data) {

            UtilsDialog.dialogData(FechamentoActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle bundle) {

        if (loader == LOADER_ENTRADA_RETIRADA) {

            String[] projection = new String[]{
                    AcessoEntRet._ID,
                    AcessoEntRet.COLUNA_ENT_RET_DATA,
                    AcessoEntRet.COLUNA_ENT_RET_VALOR,
                    AcessoEntRet.COLUNA_ENT_RET_DESC,
                    AcessoEntRet.COLUNA_ENT_RET_TIPO
            };

            String selection = AcessoEntRet.COLUNA_ENT_RET_DATA + " LIKE ?";
            String[] selectionArgs = new String[]{mDataPesquisa + "%"};

            return new CursorLoader(
                    this,
                    AcessoEntRet.CONTENT_URI_ENT_RET,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
        }

        if (loader == LOADER_SALDO) {

            String[] projection = {
                    AcessoSaldo._ID,
                    AcessoSaldo.COLUNA_SALDO_VALOR,
                    AcessoSaldo.COLUNA_SALDO_DATA
            };

            String selection = AcessoSaldo.COLUNA_SALDO_DATA + " LIKE ?";
            String[] selectionArgs = new String[]{mDataPesquisa + "%"};

            return new CursorLoader(
                    this,
                    AcessoSaldo.CONTENT_URI_SALDO,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
        }

        if (loader == LOADER_VENDAS) {

            String[] projection = {
                    AcessoVenda._ID,
                    AcessoVenda.COLUNA_VENDA_NOME_PROD,
                    AcessoVenda.COLUNA_VENDA_QUANT,
                    AcessoVenda.COLUNA_VENDA_DATA,
                    AcessoVenda.COLUNA_VENDA_VALOR_PROD,
                    AcessoVenda.COLUNA_VENDA_TEM_DESCONTO,
                    AcessoVenda.COLUNA_VENDA_TEM_COBERTURA,
                    AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO,
                    AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA,
                    AcessoVenda.COLUNA_VENDA_PRAZO,
                    AcessoVenda.COLUNA_VENDA_PRECO_UM_BOLO
            };

            String selection = AcessoVenda.COLUNA_VENDA_DATA + " LIKE ?";
            String[] selectionArgs = new String[]{mDataPesquisa + "%"};

            return new CursorLoader(
                    this,
                    AcessoVenda.CONTENT_URI_VENDA,
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

        if (loader.getId() == LOADER_ENTRADA_RETIRADA && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                if (Constantes.TIPO_ENTRADA == cursor.getInt(
                        cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_TIPO))) {

                    mTotalEntrada = mTotalEntrada + cursor.getDouble(
                            cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));

                } else if (Constantes.TIPO_RETIRADA == cursor.getInt(
                        cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_TIPO))) {

                    mTotalRetirada = mTotalRetirada + cursor.getDouble(
                            cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
                }

                cursor.moveToNext();
            }
        }

        if (loader.getId() == LOADER_SALDO && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                mSaldoInicial = mSaldoInicial + cursor.getDouble(
                        cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_VALOR));

                cursor.moveToNext();
            }
        }

        if (loader.getId() == LOADER_VENDAS && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                mQuantBolo = mQuantBolo + cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_QUANT));

                if (cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRAZO)) == Constantes.PRAZO_SIM) {

                    mQuantBoloPrazo = mQuantBoloPrazo + 1;

                } else {

                    mQuantBoloVista = mQuantBoloVista + 1;
                }

                if (cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO)) == Constantes.DESCONTO_SIM) {

                    mTotalDescontos = mTotalDescontos + cursor.getDouble(
                            cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO));
                }

                if (cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRAZO)) == Constantes.PRAZO_NAO) {

                    mTotalVendas = mTotalVendas + cursor.getDouble(
                            cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));

                }

                if (cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRAZO)) == Constantes.PRAZO_SIM) {

                    mTotalPrazo = mTotalPrazo + cursor.getDouble(
                            cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));
                }

                cursor.moveToNext();
            }
        }

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        mSaldoFinal = mTotalEntrada + mSaldoInicial + mTotalVendas - mTotalRetirada;

        mTvQuantBolo.setText(String.valueOf(mQuantBolo));
        mTvQuantBoloVista.setText(String.valueOf(mQuantBoloVista));
        mTvQuantBoloPrazo.setText(String.valueOf(mQuantBoloPrazo));

        mTvSaldoInicial.setText(preco.format(mSaldoInicial));
        mTvEntrada.setText(preco.format(mTotalEntrada));
        mTvVendas.setText(preco.format(mTotalVendas));
        mTvRetirada.setText(preco.format(mTotalRetirada));
        mTvDesconto.setText(preco.format(mTotalDescontos));
        mTvPrazo.setText(preco.format(mTotalPrazo));
        mTvSaldoFinal.setText(preco.format(mSaldoFinal));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
