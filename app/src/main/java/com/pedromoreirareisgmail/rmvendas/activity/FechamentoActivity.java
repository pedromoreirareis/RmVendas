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

    private static final int LOADER_ENTRADAS_RETIRADAS = 0;
    private static final int LOADER_SALDO_INICIAL = 1;
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

        // Referencia os itens do layout
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

        // pega data do calendario para uma nova pesquisa
        getDataCalendario();

        // coloca titulo na Activity  juntamente com a data da pesquisa
        setTitle(getString(R.string.title_fechamento) + "  " + DataHora.obterFormatarDataBrTitulo());

        // Data do dia para pesquisa no BD
        mDataPesquisarBD = DataHora.formatarDataPesquisarBancoDados(DataHora.obterDataHoraSistema());

        // Inicia as pesquisas com a data do dia
        getLoaderManager().initLoader(LOADER_ENTRADAS_RETIRADAS, null, this);
        getLoaderManager().initLoader(LOADER_SALDO_INICIAL, null, this);
        getLoaderManager().initLoader(LOADER_VENDAS, null, this);
    }

    /**
     * Cria o menu
     *
     * @param menu Objeto do menu
     * @return infla o menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_data, menu);

        return true;
    }

    /**
     * Define o que fazer com o item selecionado no menu
     *
     * @param item Item selecionado do menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Abre o Dialog de data para fazer pesquisa por data
        if (id == R.id.action_data) {

            Dialogos.dialogoDatas(FechamentoActivity.this, mDateSetListener);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Define os parametros de pesquisa
     *
     * @param loader Define os Loaders responsaveis pela pesquisa
     * @param bundle Argumento dentro do Loaders para pesquisa
     * @return cursor com dados da pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle bundle) {

        /* LOADER_ENTRADA_RETIRADA
         * Esse Loader é responsavel pela pesquisa de todas as entradas e retiradas efetuadas na data
         * pesquisada
         */
        if (loader == LOADER_ENTRADAS_RETIRADAS) {

            String[] projection = new String[]{
                    AcessoEntRet._ID,
                    AcessoEntRet.DATA,
                    AcessoEntRet.VALOR,
                    AcessoEntRet.DESCRICAO,
                    AcessoEntRet.TIPO
            };

            // Pesquisa por data
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

        /* LOADER_SALDO
         * Esse loader e responsavel por pesquisar o saldo inicial da data pesquisada
         */
        if (loader == LOADER_SALDO_INICIAL) {

            String[] projection = {
                    AcessoSaldo._ID,
                    AcessoSaldo.VALOR,
                    AcessoSaldo.DATA
            };

            // Pesquisa por data
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

        /* LOADER_VENDAS
         * Esse loader e resposavel por pesquisar todas as vendas realizada na data pesquisada
         */
        if (loader == LOADER_VENDAS) {

            String[] projection = {
                    AcessoVenda._ID,
                    AcessoVenda.NOME_PRODUTO,
                    AcessoVenda.QUANTIDADE_VENDIDA,
                    AcessoVenda.DATA,
                    AcessoVenda.VALOR_TOTAL_VENDA,
                    AcessoVenda.TEM_DESCONTO,
                    AcessoVenda.TEM_ADICIONAL,
                    AcessoVenda.VALOR_DESCONTO,
                    AcessoVenda.VALOR_ADICIONAL,
                    AcessoVenda.A_PRAZO,
                    AcessoVenda.VALOR_UMA_UNIDADE_PRODUTO
            };

            // Pesquisa por data
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

    /**
     * Define o que fazer com os dados retornados da pesquisa
     *
     * @param loader Define o loader pesquisado
     * @param cursor Dados retornados pela pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /* Entradas e Retiradas -  Todas entradas e retiradas da data pesquisada
         * Soma todas as entradas e coloca valor em uma varivel
         * Soma todas as retiradas e coloca valor em uma variavel
         */
        if (loader.getId() == LOADER_ENTRADAS_RETIRADAS && cursor.moveToFirst()) {

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

        /* Saldo Inicial - O valor do saldo inicial da data pesquisada
         *
         */
        if (loader.getId() == LOADER_SALDO_INICIAL && cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                mValorSaldoInicial = mValorSaldoInicial + cursor.getDouble(
                        cursor.getColumnIndex(AcessoSaldo.VALOR));

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

        // Calculo do valor final do caixa
        mValorSaldoFinalFechamento = mValorTotalEntradas + mValorSaldoInicial + mValorTotalVendasVista - mValorTotalRetiradas;

        // Coloca nas TextView resultados dos dados obtidos
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

    /**
     * Define o que fazer com resultados antigos da pesquisa ao fazer uma nova pesquisa
     *
     * @param loader Loader da pesquisa
     */
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
    private void getDataCalendario() {

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                zeraVariaveisValores();

                mDataPesquisarBD = DataHora.dateSetListenerPesquisarBancoDados(year, month, day);

                setTitle(getString(R.string.title_fechamento) + "  " + DataHora.dateSetListenerDataBrTitulo(year, month, day));

                reiniciarPesquisas();
            }
        };

    }

    /* Zera os valores das variaveis
     * Ao zera o valor evitar ter uma pesquisa com valores duplicados
     */
    private void zeraVariaveisValores() {

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
    }

    /* Faz a pesquisa novamente com todos os Loaders
     */
    private void reiniciarPesquisas() {

        getLoaderManager().restartLoader(LOADER_ENTRADAS_RETIRADAS, null, FechamentoActivity.this);
        getLoaderManager().restartLoader(LOADER_SALDO_INICIAL, null, FechamentoActivity.this);
        getLoaderManager().restartLoader(LOADER_VENDAS, null, FechamentoActivity.this);
    }
}
