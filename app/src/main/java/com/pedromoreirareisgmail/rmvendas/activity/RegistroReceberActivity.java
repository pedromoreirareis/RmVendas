package com.pedromoreirareisgmail.rmvendas.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.adapter.AReceberAdapter;
import com.pedromoreirareisgmail.rmvendas.db.Crud;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.TIPO_RECEBIMENTO;
import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.TIPO_VENDA;
import static com.pedromoreirareisgmail.rmvendas.Utils.DataHora.obterDataHoraSistema;
import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoAReceber;

public class RegistroReceberActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_BUSCAR_CLIENTE_REGISTRO = 0;
    private static final int LOADER_REGISTRAR_A_RECEBER = 1;

    private AReceberAdapter mAdapter;

    private Button mButVenda;
    private Button mButRecebimento;
    private TextView mTvTotal;
    private EditText mDescricao;
    private EditText mValor;


    private Uri mUriAtual = null;
    private String mId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_receber);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        mId = bundle.getString("clienteId");

        mButVenda = (Button) findViewById(R.id.but_a_receber_venda);
        mButRecebimento = (Button) findViewById(R.id.but_a_receber_recebimento);
        mDescricao = (EditText) findViewById(R.id.et_a_receber_descricao);
        mValor = (EditText) findViewById(R.id.et_a_receber_valor);
        mTvTotal = (TextView) findViewById(R.id.tv_a_receber_total);


        ListView listView = (ListView) findViewById(R.id.lv_list);


        mAdapter = new AReceberAdapter(this);
        listView.setAdapter(mAdapter);

        mButVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                salvarDadosBD(TIPO_VENDA);
            }
        });

        mButRecebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                salvarDadosBD(TIPO_RECEBIMENTO);
            }
        });


        getLoaderManager().initLoader(LOADER_BUSCAR_CLIENTE_REGISTRO, null, this);


    }


    private void salvarDadosBD(int tipoEntrada) {

        String descricaoEditText = mDescricao.getText().toString().trim();
        String valorEditText = mValor.getText().toString().trim();

        double valor = Double.parseDouble(valorEditText);


        ContentValues values = new ContentValues();

        values.put(AcessoAReceber.COLUNA_A_RECEBER_CLIENTE_ID, mId);
        values.put(AcessoAReceber.COLUNA_A_RECEBER_CLIENTE_NOME, "Teste");
        values.put(AcessoAReceber.COLUNA_A_RECEBER_DATA_HORA, obterDataHoraSistema());
        values.put(AcessoAReceber.COLUNA_A_RECEBER_DESCRICAO, descricaoEditText);
        values.put(AcessoAReceber.COLUNA_A_RECEBER_TIPO_ENTRADA, tipoEntrada);
        values.put(AcessoAReceber.COLUNA_A_RECEBER_VALOR, valor);

        Crud.inserir(RegistroReceberActivity.this, AcessoAReceber.CONTENT_URI_ARECEBER, values);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle args) {

        if (loader == LOADER_BUSCAR_CLIENTE_REGISTRO) {

            String[] projection = {
                    AcessoAReceber._ID,
                    AcessoAReceber.COLUNA_A_RECEBER_CLIENTE_ID,
                    AcessoAReceber.COLUNA_A_RECEBER_CLIENTE_NOME,
                    AcessoAReceber.COLUNA_A_RECEBER_DATA_HORA,
                    AcessoAReceber.COLUNA_A_RECEBER_DESCRICAO,
                    AcessoAReceber.COLUNA_A_RECEBER_TIPO_ENTRADA,
                    AcessoAReceber.COLUNA_A_RECEBER_VALOR
            };


            String selection = AcessoAReceber.COLUNA_A_RECEBER_CLIENTE_ID + " LIKE ? ";
            String[] selectionArgs = new String[]{mId};


            return new CursorLoader(
                    this,
                    AcessoAReceber.CONTENT_URI_ARECEBER,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
        }

        if (loader == LOADER_REGISTRAR_A_RECEBER) {

        }

        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
