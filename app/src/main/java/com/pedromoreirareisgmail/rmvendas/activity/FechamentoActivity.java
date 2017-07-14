package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.Utils.UtilsDialog;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoVenda;

public class FechamentoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ENTRADA_REIRADA = 2;
    private static final int LOADER_SALDO = 3;
    private static final int LOADER_VENDAS = 4;

    private static String dataEscolhida = "";

    double totalEntrada = 0;
    double totalRetirada = 0;
    double totalVendas = 0;
    double saldoInicial = 0;
    double TotalGeral = 0;

    private TextView mTvData;
    private TextView mTvEntrada;
    private TextView mTvRetirada;
    private TextView mTvTotal;
    private TextView mTvSaldoInicial;
    private TextView mTvVendas;

    private String mDataPesquisar = "";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fechamento);

        dataEscolhida = Datas.getDate();

        mTvEntrada = (TextView) findViewById(R.id.tv_entrada);
        mTvRetirada = (TextView) findViewById(R.id.tv_retirada);
        mTvSaldoInicial = (TextView) findViewById(R.id.tv_saldo_inicial);
        mTvTotal = (TextView) findViewById(R.id.tv_total);
        mTvData = (TextView) findViewById(R.id.tv_data);
        mTvVendas = (TextView) findViewById(R.id.tv_vendas);

        mTvData.setText(dataEscolhida);

        mTvData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UtilsDialog.dialogData(FechamentoActivity.this, mDateSetListener);

            }
        });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {


                mDataPesquisar = Datas.dateSetListenerString(year, month, day);

                mTvData.setText(Datas.dateSetListenerInverseString(year, month, day));
            }
        };

        mDataPesquisar = Datas.formatDatePesquisa(Datas.getDateTime());

        getLoaderManager().initLoader(LOADER_ENTRADA_REIRADA, null, this);
        getLoaderManager().initLoader(LOADER_SALDO, null, this);
        getLoaderManager().initLoader(LOADER_VENDAS, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle bundle) {

        if (loader == LOADER_ENTRADA_REIRADA) {

            String[] projection = new String[]{
                    AcessoEntRet._ID,
                    AcessoEntRet.COLUNA_ENT_RET_DATA,
                    AcessoEntRet.COLUNA_ENT_RET_VALOR,
                    AcessoEntRet.COLUNA_ENT_RET_DESC,
                    AcessoEntRet.COLUNA_ENT_RET_TIPO
            };

            return new CursorLoader(
                    this,
                    AcessoEntRet.CONTENT_URI_ENT_RET,
                    projection,
                    null,
                    null,
                    null
            );
        }

        if (loader == LOADER_SALDO) {

            String[] projection = {
                    AcessoSaldo._ID,
                    AcessoSaldo.COLUNA_SALDO_VALOR,
                    AcessoSaldo.COLUNA_SALDO_DATA
            };

            return new CursorLoader(
                    this,
                    AcessoSaldo.CONTENT_URI_SALDO,
                    projection,
                    null,
                    null,
                    null
            );
        }

        if (loader == LOADER_VENDAS) {

            String[] projection = {
                    AcessoVenda._ID,
                    AcessoVenda.COLUNA_VENDA_NOME_PROD,
                    AcessoVenda.COLUNA_VENDA_VALOR_PROD,
                    AcessoVenda.COLUNA_VENDA_DATA,
                    AcessoVenda.COLUNA_VENDA_QUANT,
                    AcessoVenda.COLUNA_VENDA_TEM_COBERTURA,
                    AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA,
                    AcessoVenda.COLUNA_VENDA_TEM_DESCONTO,
                    AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO
            };

            return new CursorLoader(
                    this,
                    AcessoVenda.CONTENT_URI_VENDA,
                    projection,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        if (loader.getId() == LOADER_ENTRADA_REIRADA && cursor.moveToFirst()) {


            for (int i = 0; i < cursor.getCount(); i++) {


                if (Constantes.TIPO_ENTRADA ==
                        cursor.getInt(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_TIPO))) {

                    totalEntrada = totalEntrada
                            + cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));

                } else if (Constantes.TIPO_RETIRADA ==
                        cursor.getInt(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_TIPO))) {

                    totalRetirada = totalRetirada
                            + cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
                }

                cursor.moveToNext();
            }

        }

        if (loader.getId() == LOADER_SALDO && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                saldoInicial = saldoInicial
                        + cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_VALOR));

                cursor.moveToNext();
            }
        }

        if (loader.getId() == LOADER_VENDAS && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                totalVendas = totalVendas
                        + cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));
            }
        }

        TotalGeral = totalEntrada + saldoInicial + totalVendas - totalRetirada;

        mTvEntrada.setText(String.valueOf(totalEntrada));
        mTvRetirada.setText(String.valueOf(totalRetirada));
        mTvSaldoInicial.setText(String.valueOf(saldoInicial));
        mTvVendas.setText(String.valueOf(totalVendas));
        mTvTotal.setText(String.valueOf(TotalGeral));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
