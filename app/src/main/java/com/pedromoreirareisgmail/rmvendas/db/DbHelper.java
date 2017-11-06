package com.pedromoreirareisgmail.rmvendas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoEntRet;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoProdutos;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoSaldo;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoVenda;

import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoAReceber;
import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;

public class DbHelper extends SQLiteOpenHelper {

    /**
     * Nome do banco de dados
     */
    private static final String NOME_BANCO_DADOS = "vendas.db";

    /**
     * Versão do banco de dados
     */
    private static final int VERSAO_DB = 1;

    /**
     * Construtor do Banco de dados
     *
     * @param context contexto
     */
    public DbHelper(Context context) {
        super(context, NOME_BANCO_DADOS, null, VERSAO_DB);
    }

    /**
     * Cria o banco de dados - Cria as tabelas do banco de dados
     *
     * @param db banco de dados a ser criado
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(AcessoProdutos.CRIAR_TABELA_PRODUTOS);
        db.execSQL(AcessoEntRet.CRIAR_TABELA_ENT_RET);
        db.execSQL(AcessoSaldo.CRIAR_TABELA_SALDO_INICIAL);
        db.execSQL(AcessoVenda.CRIAR_TABELA_VENDA);
        db.execSQL(AcessoClientes.CRIAR_TABELA_CLIENTES);
        db.execSQL(AcessoAReceber.CRIAR_TABELA_A_RECEBER);

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

    }
}
