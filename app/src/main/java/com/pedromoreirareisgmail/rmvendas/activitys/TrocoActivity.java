package com.pedromoreirareisgmail.rmvendas.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.constantes.Const;

import static com.pedromoreirareisgmail.rmvendas.Utils.Formatar.formatarDoubleParaCurrency;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatar.formatarParaCurrency;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatar.formatarParaDouble;
import static com.pedromoreirareisgmail.rmvendas.constantes.Const.*;
import static com.pedromoreirareisgmail.rmvendas.constantes.ConstIntents.*;

public class TrocoActivity extends AppCompatActivity implements
        EditText.OnTouchListener,
        Button.OnClickListener {

    private static final String TAG = TrocoActivity.class.getSimpleName();

    private EditText mEtValorRecebido;
    private TextView mTvValorVenda;
    private TextView mTvTroco;
    private TextView mTvTrocoLabel;
    private Button mButLimpar;

    private double mValorVenda = 0;
    private double mValorRecebido = 0;
    private String mTextoErro = "";

    private boolean isFormatarCurrencyAtualizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troco);

        Log.v(TAG, "onCreate");

        initViews();
        initItents();

        // Faz controle de entrada de dados no edit
        controleTextWatcher();

        mEtValorRecebido.setText(NUMERO_ZERO_STRING);

        mEtValorRecebido.requestFocus();

        mTvValorVenda.setText(Formatar.formatarDoubleParaCurrency(mValorVenda));
        mEtValorRecebido.setOnTouchListener(this);

        mButLimpar.setOnClickListener(this);
    }

    private void initViews() {

        Log.v(TAG, "initViews");

        mTvValorVenda = findViewById(R.id.tv_troco_valor_venda);
        mEtValorRecebido = findViewById(R.id.et_troco_valor_recebido);
        mButLimpar = findViewById(R.id.but_troco_limpar);
        mTvTroco = findViewById(R.id.tv_troco_valor_troco);
        mTvTrocoLabel = findViewById(R.id.tv_troco_valor_troco_label);
    }

    private void initItents() {

        Log.v(TAG, "initItents");

        Intent intentTroco = getIntent();

        if (intentTroco.hasExtra(VALOR_VENDA_TROCO)) {

            mValorVenda = intentTroco.getDoubleExtra(VALOR_VENDA_TROCO, Const.NUMERO_ZERO);
        }
    }

    private void calcularTroco() {

        Log.v(TAG, "calcularTroco");

        mValorRecebido = formatarParaDouble(mEtValorRecebido.getText().toString().trim());
        mTextoErro = String.format(getResources().getString(R.string.valor_maior), formatarDoubleParaCurrency(mValorVenda));

        if (mValorVenda != 0 && mValorRecebido != 0) {

            if (mValorVenda > mValorRecebido) {

                mEtValorRecebido.setError(mTextoErro);
                mTvTrocoLabel.setText("");
                mTvTroco.setText("");

            } else {

                mTvTroco.setText(formatarDoubleParaCurrency(mValorRecebido - mValorVenda));
                mTvTrocoLabel.setText(getString(R.string.valor_troco));
                mEtValorRecebido.setError(null);
            }
        }
    }

    private void mostrarTeclado(EditText meuEdit) {

        Log.v(TAG, "mostrarTeclado");

        if (meuEdit != null) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {

                imm.showSoftInput(meuEdit, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.v(TAG, "onTouch");

        switch (view.getId()) {

            case R.id.et_troco_valor_recebido:
                mEtValorRecebido.requestFocus();
                mEtValorRecebido.setSelection(mEtValorRecebido.getText().length());
                mostrarTeclado(mEtValorRecebido);
                return true;

            default:
                return false;
        }
    }

    private void controleTextWatcher() {

        Log.v(TAG, "controleTextWatcher");

        mEtValorRecebido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (isFormatarCurrencyAtualizado) {
                    isFormatarCurrencyAtualizado = false;
                    return;
                }

                isFormatarCurrencyAtualizado = true;

                mEtValorRecebido.setText(formatarParaCurrency(charSequence.toString().trim()));
                mEtValorRecebido.setSelection(mEtValorRecebido.getText().length());

                calcularTroco();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mTvTroco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count > 11) {
                    mTvTroco.setTextSize(44);
                } else if (count > 10) {
                    mTvTroco.setTextSize(48);
                } else if (count > 9) {
                    mTvTroco.setTextSize(52);
                } else if (count > 8) {
                    mTvTroco.setTextSize(56);
                } else if (count > 7) {
                    mTvTroco.setTextSize(64);
                } else {
                    mTvTroco.setTextSize(76);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        Log.v(TAG, "onClick but limpar");


        if (view.getId() == R.id.but_troco_limpar) {

            mValorRecebido = 0;
            mTextoErro = "";

            mEtValorRecebido.setError(null);
            mEtValorRecebido.setText("0");
            mTvTroco.setText("");
            mTvTrocoLabel.setText("");
        }
    }
}
