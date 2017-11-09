package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.Dialogos;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.Utils.Utilidades;
import com.pedromoreirareisgmail.rmvendas.adapter.AReceberAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MAX_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MAX_CARACT_MSG;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.MIN_QUANT_CARACT;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.NUMERO_ZERO;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.TIPO_A_RECEBER_RECEBIMENTO;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.TIPO_A_RECEBER_VENDA;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;
import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoAReceber;

public class RegistroReceberActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_BUSCAR_CLIENTE_REGISTRO = 0;

    private AReceberAdapter mAdapter;

    private Button mButVenda;
    private Button mButRecebimento;
    private TextView mTvTotal;
    private EditText mEtDescricao;
    private EditText mEtValor;
    private final EditText.OnTouchListener mTouchListnerEditFocoCursorFim = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int id = view.getId();

            switch (id) {

                // Recebe o foco e coloca o cursor no fim
                case R.id.et_a_receber_valor:
                    mEtValor.requestFocus();
                    mEtValor.setSelection(mEtValor.getText().length());
                    Utilidades.mostrarTeclado(RegistroReceberActivity.this, mEtValor);
                    return true;

                default:
                    return false;
            }
        }
    };
    private double mValorTotal = 0;
    private double mRecebimentos = 0;
    private double mVendas = 0;
    private String mId = null;
    private boolean isDadosAlterado = false;
    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_receber);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        mId = bundle.getString("clienteId");

        setTitle(bundle.getString("clienteNome"));

        mButVenda = (Button) findViewById(R.id.but_a_receber_venda);
        mButRecebimento = (Button) findViewById(R.id.but_a_receber_recebimento);
        mEtDescricao = (EditText) findViewById(R.id.et_a_receber_descricao);
        mEtValor = (EditText) findViewById(R.id.et_a_receber_valor);
        mTvTotal = (TextView) findViewById(R.id.tv_a_receber_total);


        ListView listView = (ListView) findViewById(R.id.lv_list_registro_receber);


        mAdapter = new AReceberAdapter(this);
        listView.setAdapter(mAdapter);

        mEtDescricao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CARACT)});

        mEtDescricao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (charSequence.toString().trim().length() > MAX_CARACT_MSG) {

                    Toast.makeText(RegistroReceberActivity.this,
                            R.string.msg_max_caract, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        mButVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                zerarVariaveis();
                salvarDadosBD(TIPO_A_RECEBER_VENDA);

            }
        });

        mButRecebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                zerarVariaveis();
                salvarDadosBD(TIPO_A_RECEBER_RECEBIMENTO);
            }
        });


        mEtValor.setOnTouchListener(mTouchListnerEditFocoCursorFim);

        Utilidades.semCursorFocoSelecaoZerado(mEtValor);

        getLoaderManager().initLoader(LOADER_BUSCAR_CLIENTE_REGISTRO, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void zerarVariaveis() {
        mVendas = 0;
        mRecebimentos = 0;
        mValorTotal = 0;
    }

    private void limparEdits() {

        mEtDescricao.setText("");
        mEtValor.setText("");
        mEtValor.requestFocus();


        getLoaderManager().restartLoader(LOADER_BUSCAR_CLIENTE_REGISTRO, null, this);
    }

    private void dadosAlterados() {

        String descricaoEditText = mEtDescricao.getText().toString().trim();
        String valorEditText = mEtValor.getText().toString().trim();

        double valor = Formatar.formatarParaDouble(valorEditText);

        isDadosAlterado = valor > 0 || !descricaoEditText.isEmpty();

    }

    private void salvarDadosBD(int tipoEntrada) {

        String descricaoEditText = mEtDescricao.getText().toString().trim();
        String valorEditText = mEtValor.getText().toString().trim();

        double valorDouble = Formatar.formatarParaDouble(valorEditText);


        if (TextUtils.isEmpty(valorEditText)) {

            mEtValor.setError(getString(R.string.error_campo_vazio));
            mEtValor.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(descricaoEditText)) {

            mEtDescricao.setError(getString(R.string.error_campo_vazio));
            mEtDescricao.requestFocus();
            return;
        }

        if (valorDouble <= NUMERO_ZERO) {

            mEtValor.setError(getString(R.string.error_valor_maior_zero));
            mEtValor.requestFocus();
            return;
        }

        if (descricaoEditText.length() < MIN_QUANT_CARACT) {

            mEtDescricao.setError(getString(R.string.error_campo_lenght_10));
            mEtDescricao.requestFocus();
            return;
        }


        ContentValues values = new ContentValues();

        values.put(AcessoAReceber.CLIENTE_ID, mId);
        values.put(AcessoAReceber.CLIENTE_NOME, "Teste");
        values.put(AcessoAReceber.DATA_HORA, obterDataHoraSistema());
        values.put(AcessoAReceber.DESCRICAO, descricaoEditText);
        values.put(AcessoAReceber.TIPO_ENTRADA, tipoEntrada);
        values.put(AcessoAReceber.VALOR, valorDouble);

        Crud.inserir(RegistroReceberActivity.this, AcessoAReceber.CONTENT_URI_ARECEBER, values);

        limparEdits();
        Utilidades.fecharTecladoView(RegistroReceberActivity.this, mButRecebimento);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle args) {

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
        String[] selectionArgs = new String[]{mId};
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

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

}
