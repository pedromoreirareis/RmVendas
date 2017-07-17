package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoEntRet;

import java.text.NumberFormat;

public class RetAdapter extends CursorAdapter {

    public RetAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_ent_ret_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        RetViewHolder holder = new RetViewHolder(view);

        double valorDouble = cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_VALOR));
        String dataString = cursor.getString(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DATA));
        String descString = cursor.getString(cursor.getColumnIndex(AcessoEntRet.COLUNA_ENT_RET_DESC));

        NumberFormat valorFormat = NumberFormat.getCurrencyInstance();
        String horaString = Datas.formatDateTimeEmTime(dataString);

        holder.valor.setText(valorFormat.format(valorDouble));
        holder.hora.setText(horaString);
        holder.desc.setText(descString);
    }

    class RetViewHolder {

        final TextView valor;
        final TextView hora;
        final TextView desc;

        public RetViewHolder(View view) {

            valor = view.findViewById(R.id.tv_valor_ent_ret_list);
            hora = view.findViewById(R.id.tv_hora_ent_ret_list);
            desc = view.findViewById(R.id.tv_desc_ent_ret_list);
        }
    }
}
