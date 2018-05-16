package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.ControlViews;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.Verify;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.models.Product;

import static com.pedromoreirareisgmail.rmvendas.constant.Const.NUMBER_ZERO;

public class RegisterProductActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnEditorActionListener,
        EditText.OnTouchListener {

    private EditText mEtName;
    private EditText mEtPrice;

    private Context mContext;
    private Product product;

    private boolean isDataChanged = false;
    private boolean isFormatCurrencyUpdate = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);

        initViews();
        initListenerAndObject();

        if (product.getUri() == null) { // Adicionar

            setTitle(R.string.title_product_register_add);

        } else { // Editar

            setTitle(R.string.title_product_register_edit);
            getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_PRODUCT, null, this);
        }

        // Controle entrada de caracteres nos edits
        watcherControl();

        // Retira o foco do edit e coloca o valor zero nele
        ControlViews.noFocusAndZero(mEtPrice);

        // Verifica se o texto do edit nome foi alterado
        if (!isDataChanged) {

            isDataChanged = Verify.dataChanged(mEtName);
        }
    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = RegisterProductActivity.this;

        // Instacia o objeto Product
        product = new Product();

        Intent intent = getIntent();
        product.setUri(intent.getData());

        // Monitora o EditorAction do teclado
        mEtPrice.setOnEditorActionListener(this);

        // Monitora toques em uma view especifica
        mEtPrice.setOnTouchListener(this);
    }

    private void initViews() {

        // Referencia itens do layout
        mEtName = findViewById(R.id.et_register_product_name);
        mEtPrice = findViewById(R.id.et_register_product_price);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Menu Salvar
            case R.id.action_salvar:
                saveDataDB();
                return true;

            /* Menu Up - Verifica de houve alteração, se houve abre Dialog para confirma se vai
             * descartar a alteração ou se vai continuar alteradnto*/
            case android.R.id.home:

                // Não houve alteracao
                if (!isDataChanged) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                Messages.homePressed(
                        mContext,
                        RegisterProductActivity.this);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Botão voltar (embaixo)
     * Verifica se houve alteração, se houve abre Dialog para escolher se deseja descatar dados
     * alterados ou se deseja continua alterando, se não houve volta*/
    @Override
    public void onBackPressed() {

        if (!isDataChanged) {

            super.onBackPressed();
        }

        Messages.backPressed(
                mContext,
                RegisterProductActivity.this);
    }

    private void saveDataDB() {

        String name = mEtName.getText().toString().trim();
        String price = mEtPrice.getText().toString().trim();

        double priceDouble = Formatting.currencyToDouble(price);

        // Campo não pode ficar vazio
        if (name.isEmpty()) {

            mEtName.setError(getString(R.string.error_empty_name));
            mEtName.requestFocus();
            return;
        }

        // determina quantidade minima de caracteres no campo de nome
        if (name.length() < Const.MIN_CARACT_5) {

            mEtName.setError(getString(R.string.error_name_lenght_5));
            mEtName.requestFocus();
            return;
        }

        // Valor não pode ser negativo
        if (priceDouble == NUMBER_ZERO) {

            mEtPrice.setError(getString(R.string.error_valide_value));
            mEtPrice.requestFocus();
            return;
        }

        product.setName(name);
        product.setPrice(priceDouble);

        // Coloca dados no Objeto values para salvar no BD
        ContentValues values = new ContentValues();
        values.put(EntryProduct.COLUMN_NAME, product.getName());
        values.put(EntryProduct.COLUMN_PRICE, product.getPrice());

        // Salva dados no BD
        if (product.getUri() == null) { // Adicionar

            Crud.insert(mContext, EntryProduct.CONTENT_URI_PRODUCT, values);

        } else { // Editar

            Crud.update(mContext, product.getUri(), values);
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                EntryProduct._ID,
                EntryProduct.COLUMN_NAME,
                EntryProduct.COLUMN_PRICE
        };

        return new CursorLoader(
                mContext,
                product.getUri(),
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            product.setName(cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME)));
            product.setPrice(cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE)));

            mEtPrice.setText(Formatting.doubleDBToString(product.getPrice()));
            mEtName.setText(product.getName());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

        // Salva dados no banco de dados
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            saveDataDB();
            return true;
        }

        return false;
    }

    /* Verifica entrada de caracteres nos edits e formata os caracteres se necessario */
    private void watcherControl() {

        /* No edit mEtValor e permitido a entrada apenas de numeros
         * Captura a entrada dos caracteres de numero no edit e faz a formatação para moeda
         * (currency) para ser apresentado para o usuario
         */
        mEtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!isDataChanged) {

                    isDataChanged = true;
                }

                if (isFormatCurrencyUpdate) {

                    isFormatCurrencyUpdate = false;
                    return;
                }

                isFormatCurrencyUpdate = true;

                mEtPrice.setText(Formatting.currencyToStringToCurrency(charSequence.toString().trim()));
                mEtPrice.setSelection(mEtPrice.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        switch (id) {

            case R.id.et_register_product_price:
                mEtPrice.requestFocus();
                mEtPrice.setSelection(mEtPrice.getText().length());
                ControlViews.showKeyboard(mContext, mEtPrice);
                return true;

            default:
                return false;
        }
    }
}
