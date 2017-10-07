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
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoSaldo;

public class SaldoInicialAdapter extends CursorAdapter {


    public SaldoInicialAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_saldo_inicial, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        SaldoViewHolder holder = new SaldoViewHolder(view);

        double valor = cursor.getDouble(cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_INICIAL_VALOR));
        String dataHora = cursor.getString(cursor.getColumnIndex(AcessoSaldo.COLUNA_SALDO_INICIAL_DATA));

        holder.tvValor.setText(Formatar.formatarDoubleParaCurrency(valor));
        holder.tvHoraMinuto.setText(DataHora.formatarHoraMinutoBr(dataHora));
    }


    class SaldoViewHolder {

        final TextView tvValor;
        final TextView tvHoraMinuto;

        public SaldoViewHolder(View view) {

            tvValor = view.findViewById(R.id.tv_valor_saldo_inicial_list);
            tvHoraMinuto = view.findViewById(R.id.tv_hora_saldo_inicial_list);
        }
    }
}
