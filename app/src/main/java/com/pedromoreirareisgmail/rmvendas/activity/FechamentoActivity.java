package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.Constantes;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoSaldo;

import java.util.Calendar;

public class FechamentoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ENTRADA = 3;
    private static final int LOADER_SALDO = 4;
    public static String dataEscolhida = "";
    public TextView mTvData;
    double totalEntrada = 0;
    double totalRetirada = 0;
    double totalSaldo = 0;
    double saldoFinal = 0;
    private TextView mTvEntrada;
    private TextView mTvRetirada;
    private TextView mTvTotal;
    private TextView mTvSaldoInicial;
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
        mTvData.setText(dataEscolhida);
        mTvData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DialogFragment newFragment = new DatePickerFragment();
                // newFragment.show(getSupportFragmentManager(), "datePicker");

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        FechamentoActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                // Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = day + "/" + month + "/" + year;
                mTvData.setText(date);
            }
        };

        getLoaderManager().initLoader(LOADER_ENTRADA, null, this);
        getLoaderManager().initLoader(LOADER_SALDO, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == LOADER_ENTRADA) {

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

        if (i == LOADER_SALDO) {

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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();


        if (loader.getId() == LOADER_ENTRADA) {

            for (int i = 0; i < cursor.getCount(); i++) {


                if (Constantes.TIPO_ENTRADA ==
                        cursor.getInt(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_TIPO))) {

                    totalEntrada = totalEntrada + cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
                } else if (Constantes.TIPO_RETIRADA ==
                        cursor.getInt(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_TIPO))) {

                    totalRetirada = totalRetirada + cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
                }

                cursor.moveToNext();
            }
        }

        if (loader.getId() == LOADER_SALDO) {
            for (int i = 0; i < cursor.getCount(); i++) {

                totalSaldo = totalSaldo + cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_VALOR));

                cursor.moveToNext();
            }
        }


        saldoFinal = totalEntrada + totalSaldo - totalRetirada;

        mTvEntrada.setText(String.valueOf(totalEntrada));
        mTvRetirada.setText(String.valueOf(totalRetirada));
        mTvSaldoInicial.setText(String.valueOf(totalSaldo));
        mTvTotal.setText(String.valueOf(saldoFinal));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
