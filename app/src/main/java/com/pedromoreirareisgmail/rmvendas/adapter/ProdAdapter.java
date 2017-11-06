package com.pedromoreirareisgmail.rmvendas.adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoProdutos;

public class ProdAdapter extends CursorAdapter{

    public ProdAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_prod_list,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ProdViewHolder holder = new ProdViewHolder(view);

        String nomeProduto = cursor.getString(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTOS_NOME));
        double valor = cursor.getDouble(cursor.getColumnIndex(AcessoProdutos.COLUNA_PRODUTOS_VALOR));

        holder.tvNome.setText(nomeProduto);
        holder.tvPreco.setText(Formatar.formatarDoubleParaCurrency(valor));
    }

    class ProdViewHolder{

        final TextView tvNome;
        final TextView tvPreco;

        public ProdViewHolder(View view) {

            tvNome = view.findViewById(R.id.tv_produto_nome);
            tvPreco = view.findViewById(R.id.tv_produto_preco);
        }

    }
}
