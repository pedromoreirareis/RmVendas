package com.pedromoreirareisgmail.rmvendas.adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoProdutos;

import java.text.NumberFormat;

public class ProdAdapter extends CursorAdapter{

    public ProdAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_prod_list,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ProdViewHolder holder = new ProdViewHolder(view);

        String nome = cursor.getString(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_NOME));
        double precoDouble = cursor.getDouble(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTO_PRECO));

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        holder.tvNome.setText(nome);
        holder.tvPreco.setText(preco.format(precoDouble));
    }

    class ProdViewHolder{

        TextView tvNome;
        TextView tvPreco;

        public ProdViewHolder(View view) {

            tvNome = view.findViewById(R.id.tv_prod_nome);
            tvPreco = view.findViewById(R.id.tv_prod_preco);
        }

    }
}
