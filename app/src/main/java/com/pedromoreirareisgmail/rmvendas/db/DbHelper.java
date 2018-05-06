package com.pedromoreirareisgmail.rmvendas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pedromoreirareisgmail.rmvendas.constantes.ConstTag;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoVenda;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoAReceber;
import static com.pedromoreirareisgmail.rmvendas.db.Contract.AcessoClientes;

public class DbHelper extends SQLiteOpenHelper {

    /* Tag para verificações no LogCat */
    private static final String TAG = ConstTag.TAG_MAIN + DbHelper.class.getSimpleName();


    /* Nome do banco de dados */
    private static final String DB_NAME = "vendas.db";


    /* Versão do banco de dados*/
    private static final int DB_VERSION = 1;

    /**
     * Construtor do Banco de dados
     *
     * @param context contexto
     */
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    /**
     * Cria o banco de dados - Cria as tabelas do banco de dados se elas não existem
     *
     * @param db banco de dados a ser criado
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.v(TAG, "CRIANDO TABELAS: Inicio");

        db.execSQL(EntryProduct.CREATE_TABLE_PRODUCT);
        db.execSQL(AcessoEntRet.CRIAR_TABELA_ENT_RET);
        db.execSQL(AcessoSaldo.CRIAR_TABELA_SALDO_INICIAL);
        db.execSQL(AcessoVenda.CRIAR_TABELA_VENDA);
        db.execSQL(AcessoClientes.CRIAR_TABELA_CLIENTES);
        db.execSQL(AcessoAReceber.CRIAR_TABELA_A_RECEBER);

        Log.v(TAG, "CRIANDO TABELAS: Fim");
    }

    /**
     * Faz a atualização do banco de dados - Atualiza as tabelas
     * Ex: Inclui um nova coluna em uma tabelas, exclui ou inclui tabelas
     *
     * @param db          banco de dados
     * @param versaoAtual versão atual do banco de dados
     * @param novaVersao  versão nova versão do banco de dados
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAtual, int novaVersao) {
        // Implementar quando for Atualizar o banco de dados

        Log.v(TAG, "ATUALIZANDO TABELAS");

    }
}
