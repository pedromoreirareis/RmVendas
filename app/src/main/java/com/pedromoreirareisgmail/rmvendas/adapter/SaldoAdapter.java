package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoSaldo;

import org.w3c.dom.Text;

import java.text.NumberFormat;

public class SaldoAdapter extends CursorAdapter {


    public SaldoAdapter(Context context, Cursor c) {
        super(context, c, 0);
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
        String horaString = Datas.formatDateTimeEmTime(dataString);

        holder.valor.setText(valorFormat.format(valorDouble));
        holder.hora.setText(horaString);
    }


    class SaldoViewHolder {

        TextView valor;
        TextView hora;

        public SaldoViewHolder(View view) {

            valor = view.findViewById(R.id.tv_valor_saldo_inicial_list);
            hora = view.findViewById(R.id.tv_hora_saldo_inicial_list);
        }
    }
}
