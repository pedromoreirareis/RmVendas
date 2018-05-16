package com.pedromoreirareisgmail.rmvendas.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;

import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.currencyToDouble;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.currencyToStringToCurrency;
import static com.pedromoreirareisgmail.rmvendas.Utils.Formatting.doubleToCurrency;

public class MoneyBackActivity extends AppCompatActivity implements
        EditText.OnTouchListener,
        Button.OnClickListener {

    private EditText mEtReceipValue;
    private TextView mTvSellValue;
    private TextView mTvChangeValue;
    private TextView mTvChangeValueLabel;
    private Button mButClear;

    private double mSellValue = 0;
    private double mReceipValue = 0;
    private String mErrorText = "";

    private boolean isFormatCurrencyUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_back);

        initViews();
        initItents();
        initListener();

        // Faz controle de entrada de dados no edit
        watcherControl();

        mEtReceipValue.setText(Const.NUMBER_ZERO_STRING);
        mEtReceipValue.requestFocus();

        mTvSellValue.setText(Formatting.doubleToCurrency(mSellValue));
    }

    private void initViews() {

        mTvSellValue = findViewById(R.id.tv_change_value_sale);
        mEtReceipValue = findViewById(R.id.et_change_amount_received);
        mButClear = findViewById(R.id.but_change_clear);
        mTvChangeValue = findViewById(R.id.tv_change_value_change);
        mTvChangeValueLabel = findViewById(R.id.tv_change_value_change_label);
    }

    private void initItents() {

        Intent intentChange = getIntent();

        if (intentChange.hasExtra(ConstIntents.INTENT_MONEY_BACK)) {

            mSellValue = intentChange.getDoubleExtra(ConstIntents.INTENT_MONEY_BACK, Const.NUMBER_ZERO);
        }
    }

    private void initListener() {

        mEtReceipValue.setOnTouchListener(this);

        mButClear.setOnClickListener(this);
    }

    private void showKeyboard(EditText myEdit) {

        if (myEdit != null) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {

                imm.showSoftInput(myEdit, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (view.getId()) {

            case R.id.et_change_amount_received:
                mEtReceipValue.requestFocus();
                mEtReceipValue.setSelection(mEtReceipValue.getText().length());
                showKeyboard(mEtReceipValue);
                return true;

            default:
                return false;
        }
    }

    private void watcherControl() {

        mEtReceipValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (isFormatCurrencyUpdate) {
                    isFormatCurrencyUpdate = false;
                    return;
                }

                isFormatCurrencyUpdate = true;

                mEtReceipValue.setText(currencyToStringToCurrency(charSequence.toString().trim()));
                mEtReceipValue.setSelection(mEtReceipValue.getText().length());

                changeValueCalculate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mTvChangeValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Controle do tamanho da fonte, de acordo com a quantida de digitos
                if (count > 11) {
                    mTvChangeValue.setTextSize(44);
                } else if (count > 10) {
                    mTvChangeValue.setTextSize(48);
                } else if (count > 9) {
                    mTvChangeValue.setTextSize(52);
                } else if (count > 8) {
                    mTvChangeValue.setTextSize(56);
                } else if (count > 7) {
                    mTvChangeValue.setTextSize(64);
                } else {
                    mTvChangeValue.setTextSize(76);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.but_change_clear) {

            mReceipValue = 0;
            mErrorText = "";

            mEtReceipValue.setError(null);
            mEtReceipValue.setText("0");
            mTvChangeValue.setText("");
            mTvChangeValueLabel.setText("");
        }
    }

    private void changeValueCalculate() {

        mReceipValue = currencyToDouble(mEtReceipValue.getText().toString().trim());
        mErrorText = String.format(getString(R.string.text_change_value_larger),
                doubleToCurrency(mSellValue)
        );

        if (mSellValue != 0 && mReceipValue != 0) {

            if (mSellValue > mReceipValue) {

                mEtReceipValue.setError(mErrorText);
                mTvChangeValueLabel.setText("");
                mTvChangeValue.setText("");

            } else {

                mTvChangeValue.setText(doubleToCurrency(mReceipValue - mSellValue));
                mTvChangeValueLabel.setText(getString(R.string.text_change_label_change_value));
                mEtReceipValue.setError(null);
            }
        }
    }
}
