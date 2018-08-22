package com.pedromoreirareisgmail.rmvendas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryOpening;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;

public class DbHelper extends SQLiteOpenHelper {

    /* Nome do banco de dados */
    private static final String DB_NAME = "sale.db";

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

        db.execSQL(EntrySeel.CREATE_TABLE_SELL);
        db.execSQL(EntryProduct.CREATE_TABLE_PRODUCT);
        db.execSQL(EntryCashMove.CREATE_TABLE_CASHMOVE);
        db.execSQL(EntryOpening.CREATE_TABLE_OPENING);
        db.execSQL(Contract.EntryClient.CREATE_TABLE_CLIENT);
        db.execSQL(EntryReceive.CREATE_TABLE_RECEIVE);
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

        if (versaoAtual < 2) {
            //TODO: Atualizar banco de dados para nova versao
        }
        //TODO: verificar se e melhor adicionar uma coluna ou uma tabela para vendas no cartão
        //TODO: talvez crar uma nova tabelas para as vendas no cartão seja bom e mais facil e mostar qual cliente comprou - ver se é melhor

        // Implementar quando for Atualizar o banco de dados


    }
}
