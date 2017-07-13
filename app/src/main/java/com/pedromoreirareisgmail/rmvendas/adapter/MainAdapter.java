package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoVenda;


public class MainAdapter extends CursorAdapter {

    public MainAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_main, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        MainViewHolder holder = new MainViewHolder(view);

        String nome = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_NOME_PROD));
        String data = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_DATA));
        int quantInt = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_QUANT));
        double vlCoberDouble = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA));
        double vlDescDouble = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO));
        double vlTotalDouble = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));

        String quant = String.valueOf(quantInt);
        String vlCobert = String.valueOf(vlCoberDouble);
        String vlDesc = String.valueOf(vlDescDouble);
        String vlTotal = String.valueOf(vlTotalDouble);


        holder.tvQuant.setText(quant);
        holder.tvNome.setText(nome);

        if (vlCoberDouble == 0) {
            holder.tvVlCober.setText("--");
        } else {
            holder.tvVlCober.setText(vlCobert);
        }

        if (vlDescDouble == 0) {
            holder.tvVlDesc.setText("--");
        } else {
            holder.tvVlDesc.setText(vlDesc);
        }

        holder.tvVlTotal.setText(vlTotal);
    }

    class MainViewHolder {

        TextView tvNome;
        TextView tvQuant;
        TextView tvVlCober;
        TextView tvVlDesc;
        TextView tvVlTotal;

        public MainViewHolder(View view) {

            tvNome = view.findViewById(R.id.tv_nome_main);
            tvQuant = view.findViewById(R.id.tv_valor_quant_main);
            tvVlCober = view.findViewById(R.id.tv_valor_cober_main);
            tvVlDesc = view.findViewById(R.id.tv_valor_desc_main);
            tvVlTotal = view.findViewById(R.id.tv_valor_venda_main);
        }
    }
}
