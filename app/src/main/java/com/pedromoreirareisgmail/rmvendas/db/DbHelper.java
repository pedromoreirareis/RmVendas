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
    private static final int DB_VERSION = 2;  // Mudei de versão 1 para 2

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
     * @param oldVersion versão atual do banco de dados
     * @param newVersion  versão nova versão do banco de dados
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {

            // Adiciona COLUMN_CARD_VALUE a versão 2 do banco de dados
            db.execSQL(EntrySeel.ALTER_TABLE_SELL_ADD_COLUMN_CARD);

        }
    }
}
