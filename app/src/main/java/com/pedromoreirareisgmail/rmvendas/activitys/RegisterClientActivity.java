package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.Verify;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryClient;
import com.pedromoreirareisgmail.rmvendas.db.Crud;
import com.pedromoreirareisgmail.rmvendas.models.Client;

import java.util.Objects;


public class RegisterClientActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        EditText.OnEditorActionListener,
        View.OnClickListener {

    private EditText mEtName;
    private EditText mEtFone;

    private Button mButClearName;
    private Button mButClearFone;

    private Context mContext;
    private Client client;

    private boolean isDataChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        initViews();
        initListenerAndObject();

        if (client.getUri() == null) { // Adicionar

            setTitle(getString(R.string.title_client_add));

        } else { // Editar

            setTitle(getString(R.string.title_client_edit));

            // Faz pesquisa no DB do uri pra editar
            getLoaderManager().initLoader(ConstLoader.LOADER_REGISTER_CLIENT, null, this);
        }

        // Faz controle de entrada de dados no edit
        watcherControl();

        // Verifica se houve alteração nos caracteres do edit
        if (!isDataChanged) {

            isDataChanged = Verify.dataChanged(mEtFone)
                    || Verify.dataChanged(mEtName);
        }
    }

    private void initViews() {

        // Referencia itens do layout
        mEtName = findViewById(R.id.et_client_name);
        mEtFone = findViewById(R.id.et_client_fone);
        mButClearName = findViewById(R.id.but_clear_register_client_name);
        mButClearFone = findViewById(R.id.but_clear_register_client_fone);
    }

    private void initListenerAndObject() {

        // Contexto da activity
        mContext = RegisterClientActivity.this;

        // Instancia o obejto Client
        client = new Client();

        /* Se tiver uri é uma edição*/
        Intent intentInit = getIntent();
        client.setUri(intentInit.getData());

        // Indica qual Activity chamou RegisterClientActivity
        if (intentInit.hasExtra(ConstIntents.INTENT_CALLED_CLIENT)) {

            client = intentInit.getParcelableExtra(ConstIntents.INTENT_CALLED_CLIENT);
        }

        mEtFone.setOnEditorActionListener(this);

        mButClearName.setOnClickListener(this);
        mButClearFone.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Botão salvar
            case R.id.action_salvar:
                saveDataDB();
                return true;

            /* Botão Up - Verifica alteração de dados antes de voltar a Activity que chamou
             *
             * Não Alterados - volta para activity que chamou a RegisterClientActivity
             * Alterados - Abre um Dialog para confirmar se deseja continuar alterando ou de
             * se deve descartar e voltar a activity que chamou RegisterClientActivity*/
            case android.R.id.home:

                // Não foi alterado
                if (!isDataChanged) {

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Voltar para activity que chamou
                DialogInterface.OnClickListener discartClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (client.getCalled() == Const.CALL_LIST_CLIENT) {

                                    Intent intentCalled = NavUtils.getParentActivityIntent(RegisterClientActivity.this);

                                    // Volta para activity que chamou
                                    NavUtils.navigateUpTo(RegisterClientActivity.this, Objects.requireNonNull(intentCalled));

                                } else {

                                    Intent intentCalled = new Intent(RegisterClientActivity.this, ListClientSaleActivity.class);

                                    NavUtils.navigateUpTo(RegisterClientActivity.this, intentCalled);
                                }
                            }
                        };

                // Descartar alterações
                Messages.continueOrDiscart(
                        mContext,
                        discartClickListener
                );

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Botão volta (embaixo) - Se os dados foram alterados abre Dialog para verificar se deseja
     * descartar as alterações ou se deseja continuar editando*/
    @Override
    public void onBackPressed() {

        if (!isDataChanged) {

            super.onBackPressed();
        }

        Messages.backPressed(
                mContext,
                RegisterClientActivity.this);
    }

    private void saveDataDB() {

        String name = mEtName.getText().toString().trim();
        String fone = mEtFone.getText().toString().trim();

        // O nome não pode ficar vazio
        if (name.isEmpty()) {

            mEtName.setError(getString(R.string.error_empty_name));
            mEtName.requestFocus();
            return;
        }

        // O campo nome deve ter pelo menos 3 caracteres
        if (name.length() < Const.MIN_CARACT_3) {

            mEtName.setError(getString(R.string.error_length_name_3));
            mEtName.requestFocus();
            return;
        }

        // Campo não pode ficar vazio
        if (fone.isEmpty()) {

            mEtFone.setError(getString(R.string.error_empty_fone));
            mEtFone.requestFocus();
            return;
        }

        // Campo não pode ficar vazio
        if (fone.length() < Const.MIN_NUM_FONE_8) {

            mEtFone.setError(getString(R.string.error_lenght_fone_8));
            mEtFone.requestFocus();
            return;
        }

        client.setName(name);
        client.setFone(fone);

        ContentValues values = new ContentValues();
        values.put(EntryClient.COLUMN_NAME, client.getName());
        values.put(EntryClient.COLUMN_FONE, client.getFone());

        if (client.getUri() == null) { // Adicionar

            Crud.insert(mContext, EntryClient.CONTENT_URI_CLIENT, values);

        } else { // Editar

            Crud.update(mContext, client.getUri(), values);
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                EntryClient._ID,
                EntryClient.COLUMN_NAME,
                EntryClient.COLUMN_FONE
        };

        return new CursorLoader(
                mContext,
                client.getUri(),
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Pega nome e telefone do cliente e coloca nos Edits, caso esteja editando
        if (cursor.moveToFirst()) {

            client.setName(cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_NAME)));
            client.setFone(cursor.getString(cursor.getColumnIndex(EntryClient.COLUMN_FONE)));

            mEtName.setText(client.getName());
            mEtFone.setText(client.getFone());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        // Salvar dados no banco de dados
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            saveDataDB();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.but_clear_register_client_name:
                mEtName.setText(Const.EMPTY_STRING);
                break;

            case R.id.but_clear_register_client_fone:
                mEtFone.setText(Const.EMPTY_STRING);
                break;
        }
    }

    private void watcherControl() {

        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (charSequence.length() > 0) {

                    mButClearName.setVisibility(View.VISIBLE);

                } else {

                    mButClearName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtFone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (charSequence.length() > 0) {

                    mButClearFone.setVisibility(View.VISIBLE);

                } else {

                    mButClearFone.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
