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
import com.pedromoreirareisgmail.rmvendas.Utils.TimeDate;
import com.pedromoreirareisgmail.rmvendas.constant.ConstDB;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;

public class ReceiveAdapter extends CursorAdapter {

    public ReceiveAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_receive, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ReceivedHolder holder = new ReceivedHolder(view);

        // Venda ou Recebimento
        int type = cursor.getInt(cursor.getColumnIndex(EntryReceive.COLUMN_TYPE));

        /* Data, Descrição e Valor do Registro*/
        String date = TimeDate.formatDateBr(cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_TIMESTAMP)));
        String description = cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_DESCRIPTION));
        String value = Formatting.doubleToCurrency(cursor.getDouble(cursor.getColumnIndex(EntryReceive.COLUMN_VALUE)));

        /*  Recebimento - fonte do registro fica Azul
         *   Venda - fonte do registro fica vermelha*/
        if (type == ConstDB.TYPE_CREDIT) {

            holder.tvDate.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.tvDescription.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.tvValue.setTextColor(context.getResources().getColor(R.color.colorBlue));

            holder.tvDate.setText(date);
            holder.tvDescription.setText(description);
            holder.tvValue.setText(value);

        } else {

            holder.tvDate.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.tvDescription.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.tvValue.setTextColor(context.getResources().getColor(R.color.colorRed));

            holder.tvDate.setText(date);
            holder.tvDescription.setText(description);
            holder.tvValue.setText(value);
        }
    }

    class ReceivedHolder {

        private final TextView tvValue;
        private final TextView tvDate;
        private final TextView tvDescription;

        public ReceivedHolder(View view) {

            tvValue = view.findViewById(R.id.tv_a_receber_list_valor);
            tvDate = view.findViewById(R.id.tv_a_receber_list_data);
            tvDescription = view.findViewById(R.id.tv_a_receber_list_descricao);
        }
    }
}
