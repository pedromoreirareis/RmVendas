package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.adapter.AReceberAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_QUANT_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.TIPO_A_RECEBER_RECEBIMENTO;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.TIPO_A_RECEBER_VENDA;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;
import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoAReceber;

public class RegistroReceberActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener, ListView.OnItemClickListener {

    private static final int LOADER_BUSCAR_CLIENTE_REGISTRO = 0;

    private AReceberAdapter mAdapter;

    private Button mButVenda;
    private Button mButRecebimento;
    private TextView mTvTotal;
    private EditText mEtDescricao;
    private EditText mEtValor;

    private double mValorTotal = 0;
    private double mRecebimentos = 0;
    private double mVendas = 0;
    private String mIdCliente = null;
    private String mNomeCliente = null;
    private String mNumTelefone = null;
    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_receber);

        /* Recebe dados da activity ClientesListActivity */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        /* Coloca nas variaveis mIdCliente, mNomeCliente e mNumTelefone
         * os dados que vieram da ClientesListActivity
         */
        mIdCliente = bundle.getString("clienteId");
        mNomeCliente = bundle.getString("clienteNome");
        mNumTelefone = bundle.getString("clienteFone");

        /* Coloca o nome do cliente no titulo da Activity*/
        setTitle(mNomeCliente);

        // Referencia os itens do layout
        mButVenda = (Button) findViewById(R.id.but_a_receber_venda);
        mButRecebimento = (Button) findViewById(R.id.but_a_receber_recebimento);
        mEtDescricao = (EditText) findViewById(R.id.et_a_receber_descricao);
        mEtValor = (EditText) findViewById(R.id.et_a_receber_valor);
        mTvTotal = (TextView) findViewById(R.id.tv_a_receber_total);
        ListView listView = (ListView) findViewById(R.id.lv_list_registro_receber);

        // Controla a entrada de caracteres nos edits
        controleTextWatcher();

        // Cria o adapter e o ListView
        mAdapter = new AReceberAdapter(this);
        listView.setAdapter(mAdapter);

        // Click simples no listview
        listView.setOnItemClickListener(this);


        /* Click botão venda
         * Zera as variaveis para recebimentos de novos valores, apos o salvamento
         * Salva a venda no BD
         */
        mButVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                zerarVariaveis();
                salvarDadosBD(TIPO_A_RECEBER_VENDA);

            }
        });

        /* Click botão Recebimento
         * Zera as variaveis para recebimento de novos valores, apos o salvamento
         * Salva recebimento no Banco de dados
         */
        mButRecebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                zerarVariaveis();
                salvarDadosBD(TIPO_A_RECEBER_RECEBIMENTO);
            }
        });


        // Monitora se houve toque em mEtValor
        mEtValor.setOnTouchListener(this);

        // Retira foco e coloca o valor zero no edit
        Utilidades.semFocoZerado(mEtValor);

        // Inicia pesquisa no banco de dados
        getLoaderManager().initLoader(LOADER_BUSCAR_CLIENTE_REGISTRO, null, this);
    }

    /**
     * Cria o menu
     *
     * @param menu Objeto de menu
     * @return infla o menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_fone_cliente, menu);

        return true;
    }

    /**
     * Verifica se item do menu foi selecionado
     *
     * @param item item do menu que foi selecionado
     * @return verdadeiro se item de menu foi selecionado
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            /* Menu Up
             * Verifica se dados foram alterados, se foi alterado abre Dialog para decidir se vai
             * descartar dados alterados ou se vai continuar alterando, se nada foi alterado volta
             * a activity que chamou RegistroReceberActivity
             */
            case android.R.id.home:

                dadosAlterados();

                if (!isDadosAlterado) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener descartarButClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(RegistroReceberActivity.this);
                            }
                        };

                Dialogos.dialogoConfirmarAlteracao(
                        RegistroReceberActivity.this,
                        descartarButClickListener
                );

                return true;

             /* Abre o App de telefone para fazer ligação para o cliente, no numero que esta
              * cadastrado
              */
            case R.id.action_fone_cliente:

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mNumTelefone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Zera as variaveis antes de iniciar salvamento, dessa forma o novo saldo do cliente sera
     * calculado de forma correta
     */
    private void zerarVariaveis() {
        mVendas = 0;
        mRecebimentos = 0;
        mValorTotal = 0;
    }

    /* Apos fazer o salvamento de dados e feita limpeza dos edits para que possa digitar novodados*/
    private void limparEdits() {

        mEtDescricao.setText("");
        mEtValor.setText("");
        mEtValor.requestFocus();
    }

    /* Verifica se os dados foram alterados
     * Se o valor for maior que zero, ou
     * Se o campo descrição não estiver vazio, e considerado que os dados foram alterados
     */
    private void dadosAlterados() {

        String descricaoEditText = mEtDescricao.getText().toString().trim();
        String valorEditText = mEtValor.getText().toString().trim();

        double valor = Formatar.formatarParaDouble(valorEditText);

        isDadosAlterado = valor > 0 || !descricaoEditText.isEmpty();

    }

    /* Salva dados no BD
     * Recebe dados dos edits, faz validações, coloca dados no objeto values, e salva no BD
     */
    private void salvarDadosBD(int tipoEntrada) {

        String descricaoEditText = mEtDescricao.getText().toString().trim();
        String valorEditText = mEtValor.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorEditText);

        // Campo não pode ser vazio
        if (TextUtils.isEmpty(valorEditText)) {

            mEtValor.setError(getString(R.string.error_campo_vazio));
            mEtValor.requestFocus();
            return;
        }

        // Campo não pode ser vazio
        if (TextUtils.isEmpty(descricaoEditText)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio));
            mEtDescricao.requestFocus();
            return;
        }

        // Valor não pode ser negativo
        if (valorDouble <= NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            mEtValor.requestFocus();
            return;
        }

        // Campo deve ter pelo menos 10 caracteres
        if (descricaoEditText.length() < MIN_QUANT_CARACT) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_10));
            mEtDescricao.requestFocus();
            return;
        }

        // Cria objeto values a recebe dados em campos tipo chave valor para salvar no BD
        ContentValues values = new ContentValues();

        values.put(AcessoAReceber.CLIENTE_ID, mIdCliente);
        values.put(AcessoAReceber.CLIENTE_NOME, "Teste");
        values.put(AcessoAReceber.DATA_HORA, obterDataHoraSistema());
        values.put(AcessoAReceber.DESCRICAO, descricaoEditText);
        values.put(AcessoAReceber.TIPO_ENTRADA, tipoEntrada);
        values.put(AcessoAReceber.VALOR, valorDouble);

        // Salva dados no BD
        Crud.inserir(RegistroReceberActivity.this, AcessoAReceber.CONTENT_URI_ARECEBER, values);

        // Limpa edits e coloca foco no mEtValor
        limparEdits();

        // Fecha teclado para visualizar o ListView com registro do cliente
        Utilidades.fecharTecladoView(RegistroReceberActivity.this, mButRecebimento);
    }

    /**
     * Define os criterios para pesquisa no banco de dados
     *
     * @param loader Loader responsavel pela pesquisa
     * @param args   Argumentos de pesquisa dentro do Loader
     * @return cursor co retorno da pesquisa
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle args) {

        /* Retorna todas as colunas e registro a receber de um unico cliente*/
        String[] projection = {
                AcessoAReceber._ID,
                AcessoAReceber.CLIENTE_ID,
                AcessoAReceber.CLIENTE_NOME,
                AcessoAReceber.DATA_HORA,
                AcessoAReceber.DESCRICAO,
                AcessoAReceber.TIPO_ENTRADA,
                AcessoAReceber.VALOR
        };


        String selection = AcessoAReceber.CLIENTE_ID + " LIKE ? ";
        String[] selectionArgs = new String[]{mIdCliente};
        String sortOrder = AcessoAReceber.DATA_HORA + " DESC ";


        return new CursorLoader(
                this,
                AcessoAReceber.CONTENT_URI_ARECEBER,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );


    }

    /**
     * Define o que fazer com com os dados retornados da pesquisa
     *
     * @param loader Define o loader pesquisado
     * @param cursor Cursor com dados de retorno da pesquisa
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /* Recebe os registro de um cliente
         * Soma os registro de venda na variavel mVendas
         * Soma os registro de recebimento na variavel mRecebimento
         * Calcula o saldo e coloca na variavel mValorTotal
         *
         * Se o saldo for positivo a cor da fonte fica azul
         * Se o saldo for negativo a cor da fonte fica vermelha
         */
        if (loader.getId() == LOADER_BUSCAR_CLIENTE_REGISTRO && cursor.moveToFirst()) {


            for (int i = 0; i < cursor.getCount(); i++) {

                if (cursor.getInt(cursor.getColumnIndex(AcessoAReceber.TIPO_ENTRADA)) == Constantes.TIPO_A_RECEBER_VENDA) {

                    mVendas = mVendas + cursor.getDouble(cursor.getColumnIndex(AcessoAReceber.VALOR));

                } else {

                    mRecebimentos = mRecebimentos + cursor.getDouble(cursor.getColumnIndex(AcessoAReceber.VALOR));
                }

                mValorTotal = mRecebimentos - mVendas;

                if (mValorTotal < 0) {

                    mTvTotal.setTextColor(getResources().getColor(R.color.colorRed));
                    mTvTotal.setText(Formatar.formatarDoubleParaCurrency(mValorTotal));

                } else if (mValorTotal == 0) {

                    mTvTotal.setTextColor(getResources().getColor(R.color.colorBlack));
                    mTvTotal.setText(Formatar.formatarDoubleParaCurrency(mValorTotal));

                } else if (mValorTotal > 0) {

                    mTvTotal.setTextColor(getResources().getColor(R.color.colorBlue));
                    mTvTotal.setText(Formatar.formatarDoubleParaCurrency(mValorTotal));
                }

                cursor.moveToNext();
            }
        }

        mAdapter.swapCursor(cursor);

    }

    /**
     * Define o que fazer com dados antigo da pesquisa ao fazer uma nova pesquisa
     *
     * @param loader Dados antigo da pesquisa
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /* Botao voltar (embaixo)
     * Verifica se houve alteração, se houve abre Dialog para verificar se as alterações vao ser
     * descartadas ou não, se forem descartadas volkta para activity ClientesListActivity, se não
     * forem fica na activity RegistroReceberAcitivty
     */
    @Override
    public void onBackPressed() {

        dadosAlterados();

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        DialogInterface.OnClickListener descartarButClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        Dialogos.dialogoConfirmarAlteracao(
                RegistroReceberActivity.this,
                descartarButClickListener
        );
    }

    /**
     * Monitora o toque em uma view especifica
     *
     * @param view  view que vai ser monitorada
     * @param event evento a ser realizado com a view
     * @return verdadeiro se houve toque na view
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        switch (id) {

            // Recebe o foco, coloca o cursor no fim e abre o teclado se tiver fechado
            case R.id.et_a_receber_valor:
                mEtValor.requestFocus();
                mEtValor.setSelection(mEtValor.getText().length());
                Utilidades.mostrarTeclado(RegistroReceberActivity.this, mEtValor);
                return true;

            default:
                return false;
        }
    }

    /* Controla a entrada de caracteres */
    private void controleTextWatcher() {

        /* Nesse edit entrara apenas caracteres numericos. Ao entrar um caracteres ele sera capturado
         * e enviado para formatação, e sera apresentao ao usuario em formato moeda (currency)
         */
        mEtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtValor.setText(Formatar.formatarParaCurrency(charSequence.toString().trim()));
                mEtValor.setSelection(mEtValor.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Click simples no ListView
     * Ao ter um click simples no listview, abre Dialog simples com informações sobre o registro
     * Sera informado o nome do cliente e o tipo de entrada, a data, hora, descrição e valor
     *
     * @param parent   layout parent onde esta a view item,
     * @param view     view item onde os dados estão
     * @param position posição da view no parent
     * @param id       id no BD do registro que um view esta representando
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        String tituloDialog;
        String mensagemDialog;
        String tipoString;
        int tipoInt;

        tipoInt = cursor.getInt(cursor.getColumnIndex(AcessoAReceber.TIPO_ENTRADA));

        if (tipoInt == Constantes.TIPO_A_RECEBER_RECEBIMENTO) {

            tipoString = "Recebimento";
        } else {
            tipoString = "Venda";
        }

        // Nome do cliente mais o tipo de entrada
        tituloDialog = mNomeCliente + ":    " + tipoString;

        //  Mensagem do Dialog - Descrição
        mensagemDialog = "\nData: "
                + DataHora.formatarDataBr(cursor.getString(cursor.getColumnIndex(AcessoAReceber.DATA_HORA)))
                + "\n\n"
                + "Hora: "
                + DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(AcessoAReceber.DATA_HORA)))
                + "\n\n"
                + "Descrição: "
                + cursor.getString(cursor.getColumnIndex(AcessoAReceber.DESCRICAO))
                + "\n\n"
                + "Valor:   "
                + Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoAReceber.VALOR)));

        Dialogos.dialogoExibirDados(RegistroReceberActivity.this, tituloDialog, mensagemDialog);


    }
}
