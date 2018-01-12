package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
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
import android.util.Log;
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
import com.pedromoreirareisgmail.rmvendas.db.PesquisasBD;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_CARACT_10;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.TIPO_A_RECEBER_RECEBIMENTO;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.TIPO_A_RECEBER_VENDA;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;
import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoAReceber;

public class RegistroReceberActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnTouchListener,
        ListView.OnItemClickListener, Button.OnClickListener {

    private static final String TAG = RegistroReceberActivity.class.getSimpleName();
    private static final int LOADER_BUSCAR_CLIENTE_REGISTRO = 0;

    private AReceberAdapter mAdapter;

    private Button mButVenda;
    private Button mButRecebimento;
    private TextView mTvTotal;
    private EditText mEtDescricao;
    private EditText mEtValor;
    private ListView mListview;

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

        Log.v(TAG, "onCreate");

        initViews();
        initIntents();

        /* Coloca o nome do cliente no titulo da Activity*/
        setTitle(mNomeCliente);

        // Controla a entrada de caracteres nos edits
        controleTextWatcher();

        // Cria o adapter e o ListView
        mAdapter = new AReceberAdapter(this);
        mListview.setAdapter(mAdapter);

        // Click simples no listview
        mListview.setOnItemClickListener(this);

        // Click nos botões
        mButVenda.setOnClickListener(this);
        mButRecebimento.setOnClickListener(this);

        // Monitora se houve toque em mEtValor
        mEtValor.setOnTouchListener(this);

        // Retira foco e coloca o valor zero no edit
        Utilidades.semFocoZerado(mEtValor);

        // Inicia pesquisa no banco de dados
        getLoaderManager().initLoader(LOADER_BUSCAR_CLIENTE_REGISTRO, null, this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        // Referencia os itens do layout
        mButVenda = findViewById(R.id.but_a_receber_venda);
        mButRecebimento = findViewById(R.id.but_a_receber_recebimento);
        mEtDescricao = findViewById(R.id.et_a_receber_descricao);
        mEtValor = findViewById(R.id.et_a_receber_valor);
        mTvTotal = findViewById(R.id.tv_a_receber_total);
        mListview = findViewById(R.id.lv_list_registro_receber);
    }

    private void initIntents() {

        Log.v(TAG, "initIntents");

        /* Recebe dados da activity ClientesListActivity */
        Intent intentDadosCliente = getIntent();

        /* Coloca nas variaveis mIdCliente, mNomeCliente e mNumTelefone
         * os dados que vieram da ClientesListActivity
         */
        if (intentDadosCliente.hasExtra("clienteId")) {

            mIdCliente = intentDadosCliente.getStringExtra("clienteId");
        }

        if (intentDadosCliente.hasExtra("clienteNome")) {

            mNomeCliente = intentDadosCliente.getStringExtra("clienteNome");
        }

        if (intentDadosCliente.hasExtra("clienteFone")) {

            mNumTelefone = intentDadosCliente.getStringExtra("clienteFone");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.v(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_fone_cliente, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {

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

                Dialogos.homeDescartarConfirmar(
                        RegistroReceberActivity.this,
                        RegistroReceberActivity.this);

                return true;

             /* Abre o App de telefone para fazer ligação para o cliente, no numero que esta
              * cadastrado
              */
            case R.id.action_fone_cliente:

                //TODO: TROCAR PARA ACTION_CALL - ligar direto verificar commons intents
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mNumTelefone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botao voltar (embaixo)
     * Verifica se houve alteração, se houve abre Dialog para verificar se as alterações vao ser
     * descartadas ou não, se forem descartadas volkta para activity ClientesListActivity, se não
     * forem fica na activity RegistroReceberAcitivty
     */
    @Override
    public void onBackPressed() {

        Log.v(TAG, "onBackPressed");

        dadosAlterados();

        if (!isDadosAlterado) {

            super.onBackPressed();
        }

        Dialogos.onBackPressedDescartarConfirmar(
                RegistroReceberActivity.this,
                RegistroReceberActivity.this);
    }

    /* Zera as variaveis antes de iniciar salvamento, dessa forma o novo saldo do cliente sera
     * calculado de forma correta
     */
    private void zerarVariaveis() {

        Log.v(TAG, "zerarVariaveis");

        mVendas = 0;
        mRecebimentos = 0;
        mValorTotal = 0;
    }

    /* Apos fazer o salvamento de dados e feita limpeza dos edits para que possa digitar novodados*/
    private void limparEdits() {

        Log.v(TAG, "limparEdits");

        mEtDescricao.setText("");
        mEtValor.setText("");
        mEtValor.requestFocus();
    }

    /* Verifica se os dados foram alterados
     * Se o valor for maior que zero, ou
     * Se o campo descrição não estiver vazio, e considerado que os dados foram alterados
     */
    private void dadosAlterados() {

        Log.v(TAG, "dadosAlterados");

        String descricaoEditText = mEtDescricao.getText().toString().trim();
        String valorEditText = mEtValor.getText().toString().trim();

        double valor = Formatar.formatarParaDouble(valorEditText);

        isDadosAlterado = valor > 0 || !descricaoEditText.isEmpty();
    }

    /* Salva dados no BD
     * Recebe dados dos edits, faz validações, coloca dados no objeto values, e salva no BD
     */
    private void salvarDadosBD(int tipoEntrada) {

        Log.v(TAG, "salvarDadosBD - Inicio");

        String descricaoEditText = mEtDescricao.getText().toString().trim();
        String valorEditText = mEtValor.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorEditText);

        // Campo não pode ser vazio
        if (TextUtils.isEmpty(descricaoEditText)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio_descricao));
            mEtDescricao.requestFocus();
            return;
        }

        // Campo deve ter pelo menos 10 caracteres
        if (descricaoEditText.length() < MIN_CARACT_10) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_descricao_10));
            mEtDescricao.requestFocus();
            return;
        }

        // Valor não pode ser negativo
        if (valorDouble == NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_valido));
            mEtValor.requestFocus();
            return;
        }

        // Cria objeto values a recebe dados em campos tipo chave valor para salvar no BD
        ContentValues values = new ContentValues();

        values.put(AcessoAReceber.CLIENTE_ID, Integer.parseInt(mIdCliente));
        values.put(AcessoAReceber.CLIENTE_NOME, PesquisasBD.Pesuisarcliente(RegistroReceberActivity.this, Integer.parseInt(mIdCliente)));
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

        Log.v(TAG, "salvarDadosBD - Fim");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle args) {

        Log.v(TAG, "onCreateLoader");

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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.v(TAG, "onLoadFinished");

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
                    mTvTotal.setText(
                            String.format(getResources().getString(R.string.text_registro_a_receber_valor_saldo_total_a_receber_cliente),
                                    Formatar.formatarDoubleParaCurrency(mValorTotal)));

                } else {

                    mTvTotal.setTextColor(getResources().getColor(R.color.colorBlue));
                    mTvTotal.setText(
                            String.format(getResources().getString(R.string.text_registro_a_receber_valor_saldo_total_a_receber_cliente),
                                    Formatar.formatarDoubleParaCurrency(mValorTotal)));
                }

                cursor.moveToNext();
            }

        } else {

            /* Não encontrou nenhum registro a receber para esse cliente, logo não conseguiu colocar
             * no primeiro registro - Informa ao usuario que não a nenhum registro para esse cliente
             */
            mTvTotal.setText(getString(R.string.text_registro_a_receber_valor_saldo_total_a_receber_cliente_sem_registro));
        }

        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderReset");

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");

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

        Log.v(TAG, "controleTextWatcher");

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

        Log.v(TAG, "onCreateLoader");

        Cursor cursor = mAdapter.getCursor();

        String tituloDialogTipo;
        int tipoInt;

        tipoInt = cursor.getInt(cursor.getColumnIndex(AcessoAReceber.TIPO_ENTRADA));

        if (tipoInt == Constantes.TIPO_A_RECEBER_RECEBIMENTO) {

            tituloDialogTipo = getString(R.string.text_registro_a_receber_recebimento);
        } else {

            tituloDialogTipo = getString(R.string.text_registro_a_receber_venda);
        }

        String mensagemDialog = String.format(getResources().getString(R.string.dialog_informacao_registro_a_receber),
                DataHora.formatarDataBr(cursor.getString(cursor.getColumnIndex(AcessoAReceber.DATA_HORA))),
                DataHora.formatarHoraMinutoBr(cursor.getString(cursor.getColumnIndex(AcessoAReceber.DATA_HORA))),
                cursor.getString(cursor.getColumnIndex(AcessoAReceber.DESCRICAO)),
                Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoAReceber.VALOR))));

        Dialogos.dialogoExibirDados(RegistroReceberActivity.this, tituloDialogTipo, mensagemDialog);
    }

    @Override
    public void onClick(View view) {

        Log.v(TAG, "onCreateLoader");

        switch (view.getId()) {

            case R.id.but_a_receber_venda:
                zerarVariaveis();
                salvarDadosBD(TIPO_A_RECEBER_VENDA);
                break;

            case R.id.but_a_receber_recebimento:
                zerarVariaveis();
                salvarDadosBD(TIPO_A_RECEBER_RECEBIMENTO);
                break;
        }
    }
}

