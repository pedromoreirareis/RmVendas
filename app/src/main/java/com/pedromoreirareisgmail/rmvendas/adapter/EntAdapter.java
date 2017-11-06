package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoEntRet;

public class EntAdapter extends CursorAdapter {

    public EntAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_ent_ret_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        EntViewHolder holder = new EntViewHolder(view);

        double valor = cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
        String dataHora = cursor.getString(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DATA));
        String descricao = cursor.getString(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DESCRICAO));

        holder.tvValor.setText(Formatar.formatarDoubleParaCurrency(valor));
        holder.tvHoraMinuto.setText(DataHora.formatarHoraMinutoBr(dataHora));
        holder.tvDescricao.setText(descricao);
    }

    class EntViewHolder {

        final TextView tvValor;
        final TextView tvHoraMinuto;
        final TextView tvDescricao;

        public EntViewHolder(View view) {

            tvValor = view.findViewById(R.id.tv_ent_ret_valor_list);
            tvHoraMinuto = view.findViewById(R.id.tv_ent_ret_hora_list);
            tvDescricao = view.findViewById(R.id.tv_ent_ret_desconto_list);
        }

    }
}
