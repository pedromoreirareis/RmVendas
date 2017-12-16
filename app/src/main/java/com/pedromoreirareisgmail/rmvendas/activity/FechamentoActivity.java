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
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoVenda;

import java.text.NumberFormat;

public class FechamentoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ENTRADA_RETIRADA = 0;
    private static final int LOADER_SALDO = 1;
    private static final int LOADER_VENDAS = 2;

    private TextView tvValorSaldoInicial;
    private TextView tvValorEntradas;
    private TextView tvValorRetiradas;
    private TextView tvValorDescontos;
    private TextView tvValorTotalVendasPrazo;
    private TextView tvValorTotalVendasVista;
    private TextView tvValorSaldoFinalFechamento;
    private TextView tvQuantidadeBolosVendidos;
    private TextView tvQuantidadeBolosVendidosVista;
    private TextView tvQuantidadeBolosVendidosPrazo;

    private double mValorSaldoInicial = 0;
    private double mValorTotalEntradas = 0;
    private double mValorTotalRetiradas = 0;
    private double mValorTotalDescontos = 0;
    private double mValorTotalVendasPrazo = 0;
    private double mValorTotalVendasVista = 0;
    private double mValorSaldoFinalFechamento = 0;
    private int mQuantidadeBolosVendidos = 0;
    private int mQuantidadeBolosVendidosVista = 0;
    private int mQuantidadeBolosVendidosPrazo = 0;

    private String mDataPesquisarBD = null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fechamento);


        tvValorSaldoInicial = (TextView) findViewById(R.id.tv_fechamento_saldo_inicial);
        tvValorEntradas = (TextView) findViewById(R.id.tv_fechamento_entrada);
        tvValorTotalVendasVista = (TextView) findViewById(R.id.tv_fechamento_vendas);
        tvValorRetiradas = (TextView) findViewById(R.id.tv_fechamento_retirada);
        tvValorDescontos = (TextView) findViewById(R.id.tv_fechamento_descontos);
        tvValorTotalVendasPrazo = (TextView) findViewById(R.id.tv_fechamento_prazo);
        tvValorSaldoFinalFechamento = (TextView) findViewById(R.id.tv_fechamento_saldo_final);
        tvQuantidadeBolosVendidos = (TextView) findViewById(R.id.tv_fechamento_bolos_vendidos);
        tvQuantidadeBolosVendidosVista = (TextView) findViewById(R.id.tv_fechamento_bolos_vendidos_vista);
        tvQuantidadeBolosVendidosPrazo = (TextView) findViewById(R.id.tv_fechamento_bolos_vendidos_prazo);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mValorSaldoInicial = 0;
                mValorTotalEntradas = 0;
                mValorTotalVendasVista = 0;
                mValorTotalRetiradas = 0;
                mValorTotalDescontos = 0;
                mValorTotalVendasPrazo = 0;
                mValorSaldoFinalFechamento = 0;
                mQuantidadeBolosVendidos = 0;
                mQuantidadeBolosVendidosVista = 0;
                mQuantidadeBolosVendidosPrazo = 0;

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(getString(R.string.title_fechamento) + "  " + DataHora.dateSetListenerDataBrTitulo(year, month, day));

                getLoaderManager().restartLoader(LOADER_ENTRADA_RETIRADA, null, FechamentoActivity.this);
                getLoaderManager().restartLoader(LOADER_SALDO, null, FechamentoActivity.this);
                getLoaderManager().restartLoader(LOADER_VENDAS, null, FechamentoActivity.this);
            }
        };


        setTitle(getString(R.string.title_ent_list) + "  " + DataHora.obterFormatarDataBrTitulo());

        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

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

            Dialogos.dialogoDatas(FechamentoActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle bundle) {

        if (loader == LOADER_ENTRADA_RETIRADA) {

            String[] projection = new String[]{
                    AcessoEntRet._ID,
                    AcessoEntRet.DATA,
                    AcessoEntRet.VALOR,
                    AcessoEntRet.DESCRICAO,
                    AcessoEntRet.TIPO
            };

            String selection = AcessoEntRet.DATA + " LIKE ?";
            String[] selectionArgs = new String[]{mDataPesquisarBD + "%"};

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
                    AcessoSaldo.VALOR,
                    AcessoSaldo.DATA
            };

            String selection = AcessoSaldo.DATA + " LIKE ?";
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

        if (loader == LOADER_VENDAS) {

            String[] projection = {
                    AcessoVenda._ID,
                    AcessoVenda.NOME_PRODUTO,
                    AcessoVenda.QUANTIDADE_VENDIDA,
                    AcessoVenda.DATA,
                    AcessoVenda.VALOR_TOTAL_VENDA,
                    AcessoVenda.TEM_DESCONTO,
                    AcessoVenda.TEM_COBERTURA,
                    AcessoVenda.VALOR_DESCONTO,
                    AcessoVenda.VALOR_COBERTURA,
                    AcessoVenda.A_PRAZO,
                    AcessoVenda.VALOR_UMA_UNIDADE_PRODUTO
            };

            String selection = AcessoVenda.DATA + " LIKE ?";
            String[] selectionArgs = new String[]{mDataPesquisarBD + "%"};

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

                if (Constantes.TIPO_ENTRADA_CAIXA == cursor.getInt(
                        cursor.getColumnIndex(AcessoEntRet.TIPO))) {

                    mValorTotalEntradas = mValorTotalEntradas + cursor.getDouble(
                            cursor.getColumnIndex(AcessoEntRet.VALOR));

                } else if (Constantes.TIPO_RETIRADA_CAIXA == cursor.getInt(
                        cursor.getColumnIndex(AcessoEntRet.TIPO))) {

                    mValorTotalRetiradas = mValorTotalRetiradas + cursor.getDouble(
                            cursor.getColumnIndex(AcessoEntRet.VALOR));
                }

                cursor.moveToNext();
            }
        }

        if (loader.getId() == LOADER_SALDO && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                mValorSaldoInicial = mValorSaldoInicial + cursor.getDouble(
                        cursor.getColumnIndex(AcessoSaldo.VALOR));

                cursor.moveToNext();
            }
        }

        if (loader.getId() == LOADER_VENDAS && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                mQuantidadeBolosVendidos = mQuantidadeBolosVendidos + cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.QUANTIDADE_VENDIDA));

                if (cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.A_PRAZO)) == Constantes.PRAZO_SIM) {

                    mQuantidadeBolosVendidosPrazo = mQuantidadeBolosVendidosPrazo + cursor.getInt(
                            cursor.getColumnIndex(AcessoVenda.QUANTIDADE_VENDIDA));

                } else {

                    mQuantidadeBolosVendidosVista = mQuantidadeBolosVendidosVista + cursor.getInt(
                            cursor.getColumnIndex(AcessoVenda.QUANTIDADE_VENDIDA));
                }

                if (cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.TEM_DESCONTO)) == Constantes.DESCONTO_SIM) {

                    mValorTotalDescontos = mValorTotalDescontos + cursor.getDouble(
                            cursor.getColumnIndex(AcessoVenda.VALOR_DESCONTO));
                }

                if (cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.A_PRAZO)) == Constantes.PRAZO_NAO) {

                    mValorTotalVendasVista = mValorTotalVendasVista + cursor.getDouble(
                            cursor.getColumnIndex(AcessoVenda.VALOR_TOTAL_VENDA));

                }

                if (cursor.getInt(
                        cursor.getColumnIndex(AcessoVenda.A_PRAZO)) == Constantes.PRAZO_SIM) {

                    mValorTotalVendasPrazo = mValorTotalVendasPrazo + cursor.getDouble(
                            cursor.getColumnIndex(AcessoVenda.VALOR_TOTAL_VENDA));
                }

                cursor.moveToNext();
            }
        }

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        mValorSaldoFinalFechamento = mValorTotalEntradas + mValorSaldoInicial + mValorTotalVendasVista - mValorTotalRetiradas;

        tvQuantidadeBolosVendidos.setText(String.valueOf(mQuantidadeBolosVendidos));
        tvQuantidadeBolosVendidosVista.setText(String.valueOf(mQuantidadeBolosVendidosVista));
        tvQuantidadeBolosVendidosPrazo.setText(String.valueOf(mQuantidadeBolosVendidosPrazo));

        tvValorSaldoInicial.setText(preco.format(mValorSaldoInicial));
        tvValorEntradas.setText(preco.format(mValorTotalEntradas));
        tvValorTotalVendasVista.setText(preco.format(mValorTotalVendasVista));
        tvValorRetiradas.setText(preco.format(mValorTotalRetiradas));
        tvValorDescontos.setText(preco.format(mValorTotalDescontos));
        tvValorTotalVendasPrazo.setText(preco.format(mValorTotalVendasPrazo));
        tvValorSaldoFinalFechamento.setText(preco.format(mValorSaldoFinalFechamento));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
