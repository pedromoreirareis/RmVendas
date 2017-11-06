package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;

import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoAReceber;

public class AReceberAdapter extends CursorAdapter {

    public AReceberAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_a_receber_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        AReceberViewHolder holder = new AReceberViewHolder(view);

        String descricaoBD = cursor.getString(cursor.getColumnIndex(AcessoAReceber.COLUNA_A_RECEBER_DESCRICAO));
        String dataBD = cursor.getString(cursor.getColumnIndex(AcessoAReceber.COLUNA_A_RECEBER_DATA_HORA));
        double valorBD = cursor.getDouble(cursor.getColumnIndex(AcessoAReceber.COLUNA_A_RECEBER_VALOR));

        holder.tvDescricao.setText(descricaoBD);
        holder.tvData.setText(dataBD);
        holder.tvValor.setText(String.valueOf(valorBD));
    }

    class AReceberViewHolder {

        final TextView tvValor;
        final TextView tvData;
        final TextView tvDescricao;

        public AReceberViewHolder(View view) {

            tvValor = view.findViewById(R.id.tv_a_receber_list_valor);
            tvData = view.findViewById(R.id.tv_a_receber_list_data);
            tvDescricao = view.findViewById(R.id.tv_a_receber_list_descricao);
        }

    }
}
