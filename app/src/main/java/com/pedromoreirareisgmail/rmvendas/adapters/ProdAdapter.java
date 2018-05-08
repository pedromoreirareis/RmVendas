package com.pedromoreirareisgmail.rmvendas.adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

public class ProdAdapter extends CursorAdapter{

    /**
     * Construtor
     *
     * @param context Contexto da Activity
     */
    public ProdAdapter(Context context) {
        super(context, null, 0);
    }

    /**
     * Infla os itens do layout, reaproveitando os itens
     *
     * @param context   Contexto da Activity que vem do construtor
     * @param cursor    Cursor com dados de uma pesquisa no BD
     * @param viewGroup Layout onde sera colocado os itens
     * @return View de um unico item do viewGroup
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_product, viewGroup, false);
    }

    /**
     * Coloca informações nos itens(views) do layout a partir de dados de um Cursor
     *
     * @param view    Um unico item do layout
     * @param context Contexto da Activity
     * @param cursor  Cursor com dados de uma pesquisa no BD
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ProdViewHolder holder = new ProdViewHolder(view);

        /* Nome do produto e valor */
        String nomeProduto = cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME));
        double valor = cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE));

        holder.tvNome.setText(nomeProduto);
        holder.tvPreco.setText(Formatting.doubleToCurrency(valor));
    }

    /*
     *   Cria o ViewHolder para gerenciar a criação de itens(views)
     */
    class ProdViewHolder{

        final TextView tvNome;
        final TextView tvPreco;

        public ProdViewHolder(View view) {

            tvNome = view.findViewById(R.id.tv_produto_nome);
            tvPreco = view.findViewById(R.id.tv_produto_preco);
        }
    }
}
