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
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoSaldo;

import java.text.NumberFormat;

public class SaldoAdapter extends CursorAdapter {


    public SaldoAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_saldo_inicial, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        SaldoViewHolder holder = new SaldoViewHolder(view);

        double valorDouble = cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_VALOR));
        String dataString = cursor.getString(cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_DATA));

        NumberFormat valorFormat = NumberFormat.getCurrencyInstance();
        String horaString = DataHora.formatHoraMinutoBr(dataString);

        holder.valor.setText(valorFormat.format(valorDouble));
        holder.hora.setText(horaString);
    }


    class SaldoViewHolder {

        final TextView valor;
        final TextView hora;

        public SaldoViewHolder(View view) {

            valor = view.findViewById(R.id.tv_valor_saldo_inicial_list);
            hora = view.findViewById(R.id.tv_hora_saldo_inicial_list);
        }
    }
}
