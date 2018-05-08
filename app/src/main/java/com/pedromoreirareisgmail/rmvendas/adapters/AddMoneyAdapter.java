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
import com.pedromoreirareisgmail.rmvendas.Utils.TimeData;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;

public class AddMoneyAdapter extends CursorAdapter {

    /**
     * Construtor
     *
     * @param context Contexto da Activity
     */
    public AddMoneyAdapter(Context context) {
        super(context, null, 0);
    }

    /**
     * Infla os itens do layout, reaproveitando os itens
     *
     * @param context   Contexto da Activity que vem do construtor
     * @param cursor    Cursor com dados de uma pesquisa no BD
     * @param viewGroup Layout onde sera colocado os itens
     * @return View de um unico item do viewGroup
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_cash_move, viewGroup, false);
    }

    /**
     * Coloca informações nos itens(views) do layout a partir de dados de um Cursor
     *
     * @param view    item do layout
     * @param context Contexto da Activity
     * @param cursor  Cursor com dados de uma pesquisa no BD
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        EntViewHolder holder = new EntViewHolder(view);

        /* Recebe o valor, hora e descrição de uma entrada do caixa */
        double value = cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));
        String hour = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP));
        String description = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION));

        holder.tvValue.setText(Formatting.doubleToCurrency(value));
        holder.tvHour.setText(TimeData.formatDateToHourAndMinute(hour));
        holder.tvDescription.setText(description);
    }

    /* Cria o ViewHolder para gerenciar a criação de itens(views) */
    class EntViewHolder {

        final TextView tvValue;
        final TextView tvHour;
        final TextView tvDescription;

        public EntViewHolder(View view) {

            tvValue = view.findViewById(R.id.tv_add_remove_list_value);
            tvHour = view.findViewById(R.id.tv_add_remove_list_hour);
            tvDescription = view.findViewById(R.id.tv_add_remove_list_description);
        }
    }
}
